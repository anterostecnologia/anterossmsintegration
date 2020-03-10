package br.com.anteros.sms.integration.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.com.anteros.sms.integration.AnterosSMSProvider;
import br.com.anteros.sms.integration.SMSResult;
import br.com.anteros.sms.integration.SMSStatus;
import br.com.anteros.sms.integration.exception.AnterosSMSIntegrationException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

/**
 * SMSDev provider SMS
 * 
 * https://www.smsdev.com.br/envio-sms-unico/
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */
public class SMSDevProvider implements AnterosSMSProvider {

	private String accessKey;
	private String url;

	public SMSDevProvider(String accessKey) {
		this(accessKey, "https://api.smsdev.com.br");
	}

	public SMSDevProvider(String accessKey, String url) {
		this.accessKey = accessKey;
		this.url = url;
	}

	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {
		HttpResponse<String> response = Unirest
				.get(this.url + "/send?key=" + this.accessKey + "&type=9&number=" + receiver + "&msg=" + content)
				.asString();

		if (response.getStatus() == 200) {
			JSONObject jsonObject = new JSONObject(response.getBody());
			if (jsonObject.getString("situacao").equals("OK")) {
				return SMSResult.of(SMSStatus.SCHEDULED, jsonObject.getString("descricao"), null,
						jsonObject.getString("id"));
			} else {
				return SMSResult.of(SMSStatus.ERROR, jsonObject.getString("descricao"), null);
			}

		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS por este provider. SMSDev.");
		}

	}

	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receiver) throws Exception {
		if (receiver.length > 300) {
			throw new AnterosSMSIntegrationException(
					"Número máximo permitido de destinatários para este Provider é 300. SMSDev.");
		}
		boolean appendDelimiter = false;
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (String rec : receiver) {
			if (appendDelimiter) {
				sb.append("&");
			}
			index++;
			sb.append("number").append(index).append("=").append(rec);
			appendDelimiter = true;
		}
		Collection<SMSResult> result = new ArrayList<>();
		HttpResponse<String> response = Unirest
				.get(this.url + "/send?key=" + this.accessKey + "&type=9&" + sb.toString() + "&msg=" + content)
				.asString();

		if (response.getStatus() == 200) {
			JSONArray jsonObject = new JSONArray(response.getBody());
			for (Object ob : jsonObject) {
				JSONObject obj = (JSONObject) ob;
				if (obj.getString("situacao").equals("OK")) {
					result.add(
							SMSResult.of(SMSStatus.SCHEDULED, obj.getString("descricao"), null, obj.getString("id")));
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, obj.getString("descricao"), null));
				}
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS por este provider. SMSDev.");
		}
		return result.toArray(new SMSResult[] {});
	}

	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receiver)
			throws Exception {
		throw new AnterosSMSIntegrationException("Agendamento não implementado por este provider. SMSDev.");
	}

	public SMSResult getAccountBalance() throws Exception {
		HttpResponse<String> response = Unirest.get(this.url + "/get?key=" + this.accessKey + "&action=saldo")
				.asString();
		JSONObject object = new JSONObject(response.getBody());

		return SMSResult.of(SMSStatus.SENT, object.getString("saldo_sms"), null);
	}

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {
		Collection<SMSResult> result = new ArrayList<>();
		for (String id : ids) {
			HttpResponse<String> response = Unirest.get(this.url + "/get?key=" + this.accessKey + "&id=" + id)
					.asString();
			if (response.getStatus() == 200) {
				JSONObject jsonObject = new JSONObject(response.getBody());
				if (jsonObject.getString("situacao").equals("OK")) {
					if (jsonObject.getString("descricao").equals("RECEBIDA")) {
						result.add(SMSResult.of(SMSStatus.RECEIVED, jsonObject.getString("descricao"), null));
					} else if (jsonObject.getString("descricao").equals("ENVIADA")) {
						result.add(SMSResult.of(SMSStatus.SENDING, jsonObject.getString("descricao"), null));
					} else if (jsonObject.getString("descricao").equals("FILA")) {
						result.add(SMSResult.of(SMSStatus.SCHEDULED, jsonObject.getString("descricao"), null));	
					} else if (jsonObject.getString("descricao").equals("CANCELADA")) {
						result.add(SMSResult.of(SMSStatus.CANCELED, jsonObject.getString("descricao"), null));
					} else if (jsonObject.getString("descricao").equals("BLACK LIST")) {
						result.add(SMSResult.of(SMSStatus.BLACKLIST, jsonObject.getString("descricao"), null));
					} else if (jsonObject.getString("descricao").equals("ERRO")) {
						result.add(SMSResult.of(SMSStatus.ERROR, jsonObject.getString("descricao"), null));
					}					
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, jsonObject.getString("descricao"), null));
				}
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS por este provider. SMSDev.");
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
 * SITUAÇÃO DESCRIÇÃO RECEBIDA Mensagem entregue no aparelho do cliente. ENVIADA
 * Mensagem enviada a operadora. ERRO Erro de validação da mensagem. FILA
 * Mensagem aguardando processamento. CANCELADA Mensagem cancelada pelo usuário.
 * BLACK LIST Destinatário ativo no grupo ‘Black List’.
 */
