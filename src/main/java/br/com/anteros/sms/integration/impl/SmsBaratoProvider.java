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
import kong.unirest.json.JSONObject;

/**
 * SMSBarato sms provider
 * 
 * https://www.smsbarato.com.br https://sistema.smsbarato.com.br/integra.html
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */
//TODO  ver consulta status que não vem no envio
public class SmsBaratoProvider implements AnterosSMSProvider {

	private String accessKey;
	private String url;

	public SmsBaratoProvider(String accessKey) {
		this(accessKey, "http://sistema81.smsbarato.com.br");
	}

	public SmsBaratoProvider(String accessKey, String url) {
		this.url = url;
		this.accessKey = accessKey;
	}

	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {
		HttpResponse<String> response = Unirest
				.get(this.url + "/send?chave=" + this.accessKey + "&dest=" + receiver + "&text=" + content).asString();

		if (response.getStatus() == 200) {
			String body = response.getBody();

			if (response.getBody().startsWith("ERRO1-1")) {
				return SMSResult.of(SMSStatus.ERROR, "Problemas com a sua chave", null);
			} else if (response.getBody().startsWith("ERRO1-2")) {
				return SMSResult.of(SMSStatus.ERROR, "Problemas com seu IP (nao autorizado)", null);
			} else if (response.getBody().startsWith("ERRO1-3")) {
				return SMSResult.of(SMSStatus.ERROR, "Saldo insuficiente para enviar mensagem", null);
			} else if (response.getBody().startsWith("ERRO2")) {
				return SMSResult.of(SMSStatus.ERROR, "Problemas com o numero de destino (parametro dest)", null);
			} else if (response.getBody().startsWith("ERRO3")) {
				return SMSResult.of(SMSStatus.ERROR, "Problemas com o texto (parametro text)", null);
			} else {
				return SMSResult.of(SMSStatus.SCHEDULED, "Agendamento efetuado com sucesso.", null, body);
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. SMSBarato");
		}
	}

	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receiver) throws Exception {
		Collection<SMSResult> result = new ArrayList<SMSResult>();
		for (String rec : receiver) {
			HttpResponse<String> response = Unirest
					.get(this.url + "/send?chave=" + this.accessKey + "&dest=" + rec + "&text=" + content).asString();
			if (response.getStatus() == 200) {
				String body = response.getBody();

				if (response.getBody().startsWith("ERRO1-1")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "Problemas com a sua chave", null));
				} else if (response.getBody().startsWith("ERRO1-2")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "Problemas com seu IP (nao autorizado)", null));
				} else if (response.getBody().startsWith("ERRO1-3")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "Saldo insuficiente para enviar mensagem", null));
				} else if (response.getBody().startsWith("ERRO2")) {
					result.add(
							SMSResult.of(SMSStatus.ERROR, "Problemas com o numero de destino (parametro dest)", null));
				} else if (response.getBody().startsWith("ERRO3")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "Problemas com o texto (parametro text)", null));
				} else {
					result.add(SMSResult.of(SMSStatus.SCHEDULED, "Agendamento efetuado com sucesso.", null, body));
				}
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. SMSBarato");
			}
		}
		return result.toArray(new SMSResult[] {});
	}

	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receiver)
			throws Exception {
		throw new AnterosSMSIntegrationException("Agendamento não implementado por este provider. SmsBarato.");
	}

	public SMSResult getAccountBalance() throws Exception {
		HttpResponse<String> response = Unirest.get(this.url + "/saldo?chave=" + this.accessKey).asString();
		if (response.getStatus() == 200) {
			return SMSResult.of(SMSStatus.SENT, response.getBody(), null);
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro consultado saldo SMS. SMSBarato");
		}
	}

	

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {
		Collection<SMSResult> result = new ArrayList<SMSResult>();
		for (String id : ids) {
			HttpResponse<String> response = Unirest
					.get(this.url + "/status?chave=" + this.accessKey + "&id=" + id).asString();
			if (response.getStatus() == 200) {
				String body = response.getBody();

				if (response.getBody().startsWith("ERRO2")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "ID Inválido", null));
				} else if (response.getBody().startsWith("ERRO")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "O ID especificado nao existe.", null));
				} else if (response.getBody().startsWith("N")) {
					result.add(SMSResult.of(SMSStatus.SCHEDULED, "Mensagem nova, aguardando envio.", null));
				} else if (response.getBody().startsWith("R")) {
					result.add(
							SMSResult.of(SMSStatus.ERROR, "Mensagem sendo enviada.", null));
				} else if (response.getBody().startsWith("S")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "Mensagem foi enviada com Sucesso.", null));
				} else if (response.getBody().startsWith("F")) {
					result.add(SMSResult.of(SMSStatus.ERROR, "Envio falhou. Nao sera tentado novamente.", null));	
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, "Ocorreu algum erro com o SMS", null, body));
				}
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro consultando status SMS. SMSBarato");
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
