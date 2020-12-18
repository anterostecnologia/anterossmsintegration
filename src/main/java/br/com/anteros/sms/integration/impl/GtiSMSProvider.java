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
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */

public class GtiSMSProvider implements AnterosSMSProvider {

	private String userName;
	private String token;
	private String url;

	public GtiSMSProvider(String email, String token) {
		this(email, token, "https://api.gtisms.com/rest/api/SMS");
	}

	public GtiSMSProvider(String email, String token, String url) {
		this.userName = email;
		this.token = token;
		this.url = url;
	}

	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {
		HttpResponse<String> response = Unirest.post(this.url + "/EnviarSMS")
				.header("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
				.body("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; "
						+ "name=\"email\"\r\n\r\n" + this.userName
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: "
						+ "form-data; name=\"token\"\r\n\r\n" + this.token
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: "
						+ "form-data; name=\"id\"\r\n\r\n" + id
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; "
						+ "name=\"numeros\"\r\n\r\n" + receiver
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; "
						+ "name=\"mensagem\"\r\n\r\n" + content + "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
				.asString();
		if (response.getStatus() == 200) {
			String body = response.getBody();

			JSONObject jsonObject = new JSONObject(body);
			if (jsonObject.getString("code").equals("6")) {
				return SMSResult.of(SMSStatus.SCHEDULED, jsonObject.getString("message"), null);
			} else {
				return SMSResult.of(SMSStatus.ERROR, jsonObject.getString("message"), null);
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. GitSMS");
		}

	}

	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receivers) throws Exception {

		StringBuilder sb = new StringBuilder();
		boolean appendDelimiter = false;
		for (String rec : receivers) {
			if (appendDelimiter) {
				sb.append(",");
			}
			sb.append(rec);
			appendDelimiter = true;
		}

		HttpResponse<String> response = Unirest.post(this.url + "/EnviarSMS")
				.header("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
				.body("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; "
						+ "name=\"email\"\r\n\r\n" + this.userName
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: "
						+ "form-data; name=\"token\"\r\n\r\n" + this.token
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: "
						+ "form-data; name=\"id\"\r\n\r\n" + ids[0]
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; "
						+ "name=\"numeros\"\r\n\r\n" + sb.toString()
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; "
						+ "name=\"mensagem\"\r\n\r\n" + content + "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
				.asString();

		if (response.getStatus() == 200) {
			String body = response.getBody();

			JSONObject jsonObject = new JSONObject(body);
			if (jsonObject.getString("code").equals("6")) {
				return new SMSResult[] {SMSResult.of(SMSStatus.SCHEDULED, jsonObject.getString("message"), null, ids[0])};
			} else {
				return new SMSResult[] {SMSResult.of(SMSStatus.ERROR, jsonObject.getString("message"), null, ids[0])};
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. GitSMS");
		}
	}

	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receivers)
			throws Exception {
		throw new AnterosSMSIntegrationException("Agendamento não implementado por este provider. GtiSMS.");
	}

	public SMSResult getAccountBalance() throws Exception {
		HttpResponse<String> response = Unirest.post(this.url + "/ConsultarCreditos")
				.header("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
				.body("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; "
						+ "name=\"email\"\r\n\r\n" + this.userName
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: "
						+ "form-data; name=\"token\"\r\n\r\n" + this.token
						+ "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
				.asString();

		JSONObject result = new JSONObject(response.getBody());
		if (response.getStatus()==200) {
			if (result.get("reason") != null) {
				if (result.get("reason").equals("success")) {
					String value = result.get("creditos").toString();
					return SMSResult.of(SMSStatus.SENT, "", Long.valueOf(value.trim()));
				}
			}
			return SMSResult.of(SMSStatus.ERROR,result.getString("reason"));
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. GtiSMS");
		}
	}

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {

		Collection<SMSResult> result = new ArrayList<>();
		for (String id : ids) {
			HttpResponse<String> response = Unirest
					.get(this.url + "/VerificaStatus?email=" + this.userName + "&token=" + this.token + "&id=" + id)
					.header("cache-control", "no-cache").asString();

			if (response.getStatus() == 200) {
				JSONObject jsonObject = new JSONObject(response.getBody());
				if (jsonObject.getString("result").equals("success")) {
					if (jsonObject.getString("status").equals("entregue")) {
						result.add(SMSResult.of(SMSStatus.SENT, "", null, id));
					} else {
						result.add(SMSResult.of(SMSStatus.SCHEDULED, "", null, id));
					}
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, "", jsonObject.getString("message"), id));
				}
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro verificando status SMS. GitSMS");
			}
		}
		return new SMSResult[] {};
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
 * { “reason”: “success”, “message”: “mensagem enviada com sucesso!”, “code”: 6
 * } { “reason”: “error”, “message”: “o parametro Email e vazio ou invalido!”,
 * “code”: 1 } { “reason”: “error”, “message”: “o parametro Token e vazio ou
 * invalido!”, “code”: 1 } { “reason”: “error”, “message”: “o usuário não foi
 * encontrado!”, “code”: 1 } { “reason”: “error”, “message”: “não há creditos
 * suficientes!”, “code”: 5 } { “reason”: “error”, “message”: “o parametro
 * Mensagem e vazio ou invalido!”, “code”: 2 } { “reason”: “error”, “message”:
 * “o parametro Numeros e vazio ou invalido!”, “code”: 3 }
 */
