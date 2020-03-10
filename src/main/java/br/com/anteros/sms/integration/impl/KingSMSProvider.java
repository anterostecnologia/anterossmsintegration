package br.com.anteros.sms.integration.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.sms.integration.AnterosSMSProvider;
import br.com.anteros.sms.integration.SMSResult;
import br.com.anteros.sms.integration.SMSStatus;
import br.com.anteros.sms.integration.exception.AnterosSMSIntegrationException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

/**
 * KingSMS sms provider
 * 
 * http://download.kingtelecom.com.br/download/KingSMS-API.pdf
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */
public class KingSMSProvider implements AnterosSMSProvider {

	private String url;
	private String tokenAcesso;
	private String userName;

	public KingSMSProvider(String userName, String tokenAcesso) {
		this(userName, tokenAcesso, "http://painel.kingsms.com.br/kingsms/api.php");
	}

	public KingSMSProvider(String userName, String tokenAcesso, String url) {
		this.userName = userName;
		this.tokenAcesso = tokenAcesso;
		this.url = url;
	}

	@Override
	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {
		HttpResponse<String> response = Unirest.get(this.url + "?acao=sendsms&login=" + this.userName + "&token="
				+ this.tokenAcesso + "&numero=" + receiver + "&msg=" + content).asString();

		if (response.getStatus() == 200) {
			String body = response.getBody();

			JSONObject jsonObject = new JSONObject(body);
			if (jsonObject.getString("status").equals("success")) {
				return SMSResult.of(SMSStatus.SCHEDULED, translateMessage(jsonObject.getString("cause")), null,
						jsonObject.getString("id"));
			} else {
				return SMSResult.of(SMSStatus.ERROR, translateMessage(jsonObject.getString("cause")), null);
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. KingSMS");
		}
	}

	private String translateMessage(String message) {

		switch (message) {
		case "SMS Add Queue":
			return "SMS adicionado na fila";
		case "Action Not Informed OR Invalid":
			return "Acão não informada ou inválida";
		case "Incorrect Login":
			return "Login incorreto";
		case "Incorrect Token":
			return "Token incorreto";
		case "Number Not Informed":
			return "Número não informado";
		case "Invalid Number":
			return "Número inválido";
		case "Incorrect Number Format":
			return "Formato do número incorreto";
		case "Number Not Movel":
			return "Não é um número móvel";
		case "Message Not Informed":
			return "Mensagem não informada";
		case "Number of characters > 160":
			return "Número de caracteres maior que 160";
		case "Without Credit":
			return "Sem crédito";
		case "SMS still in the send Queue":
			return "SMS ainda na fila";
		case "SendingError":
			return "Erro enviando";
		case "ID Not Informed":
			return "ID não informado";
		case "ID Not Found":
			return "ID não encontrado";
		}
		return message;
	}

	@Override
	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receivers) throws Exception {
		Collection<SMSResult> result = new ArrayList<>();
		for (String rec : receivers) {
			HttpResponse<String> response = Unirest.get(this.url + "?acao=sendsms&login=" + this.userName + "&token="
					+ this.tokenAcesso + "&numero=" + rec + "&msg=" + content).asString();
			if (response.getStatus() == 200) {
				String body = response.getBody();

				JSONObject jsonObject = new JSONObject(body);
				if (jsonObject.getString("status").equals("success")) {
					result.add(SMSResult.of(SMSStatus.SCHEDULED, translateMessage(jsonObject.getString("cause")), null,
							jsonObject.getString("id")));
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, translateMessage(jsonObject.getString("cause")), null));
				}
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. KingSMS");
			}
		}
		return result.toArray(new SMSResult[] {});
	}

	@Override
	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receivers)
			throws Exception {
		Collection<SMSResult> result = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdt = new SimpleDateFormat("HH:mm");
		for (String rec : receivers) {
			HttpResponse<String> response = Unirest.get(this.url + "?acao=sendsms&login=" + this.userName + "&token="
					+ this.tokenAcesso + "&numero=" + rec + "&msg=" + content + "&data=" + sdf.format(scheduleDate)
					+ "&hora=" + sdt.format(scheduleDate)).asString();
			if (response.getStatus() == 200) {
				String body = response.getBody();

				JSONObject jsonObject = new JSONObject(body);
				if (jsonObject.getString("status").equals("success")) {
					result.add(SMSResult.of(SMSStatus.SCHEDULED, translateMessage(jsonObject.getString("cause")), null,
							jsonObject.getString("id")));
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, translateMessage(jsonObject.getString("cause")), null));
				}
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. KingSMS");
			}
		}
		return result.toArray(new SMSResult[] {});
	}

	@Override
	public SMSResult getAccountBalance() throws Exception {
		HttpResponse<String> response = Unirest
				.get(this.url + "?acao=saldo&login=" + this.userName + "&token=" + this.tokenAcesso).asString();
		JSONObject result = new JSONObject(response.getBody());
		if (result.get("status") != null) {
			if (result.get("status").equals("success")) {
				String value = result.get("cause").toString();
				value = StringUtils.replaceAll(value, "Credit", "");
				value = StringUtils.replaceAll(value, "SMS", "");

				return SMSResult.of(SMSStatus.SENT, "", Long.valueOf(value.trim()));
			}
		}
		return SMSResult.of(SMSStatus.ERROR, "Não foi possível consultar o saldo. KingSMS");
	}

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {
		Collection<SMSResult> result = new ArrayList<>();
		for (String id : ids) {
			HttpResponse<String> response = Unirest
					.get(this.url + "?acao=reportsms&login=" + this.userName + "&id=" + id).asString();
			if (response.getStatus() == 200) {
				String body = response.getBody();

				JSONObject jsonObject = new JSONObject(body);
				if (jsonObject.getString("status").equals("success")) {
					if (jsonObject.getString("cause").equals("SMS still in the send Queue"))
						result.add(SMSResult.of(SMSStatus.SCHEDULED, translateMessage(jsonObject.getString("cause")),
								null, id));
					else
						result.add(SMSResult.of(SMSStatus.SENT, translateMessage(jsonObject.getString("cause")), null));
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, translateMessage(jsonObject.getString("cause")), null));
				}
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. KingSMS");
			}
		}
		return result.toArray(new SMSResult[] {});
	}

	@Override
	public boolean isSupportsStatusQuery() {
		return true;
	}

	@Override
	public boolean isSupportsAccountBalance() {
		return true;
	}

}

/*
 * Para algumas palavras informamos uma sugestão para substituição para que a
 * mensagem não seja considerada como Spam. PALAVRA SUGESTÃO acesse -> visite
 * o site aproveite -> não perca caro cliente -> caro Sr.(a) cliente -> sr.(a)
 * desconto -> descto ganhe -> receba gratis -> deve ser evitada! imperdível ->
 * deve ser evitada! informa comunica -> info - informe informações -> mais
 * detalhes informar -> informe informativo -> comunicado negocie -> negoc
 * prezado cliente -> sr.(a) promoção -> beneficio ou vantagem ou oportunidade
 * regularize -> quite telefone -> tels veja -> confira
 * 
 */
