package br.com.anteros.sms.integration.impl;

import java.text.SimpleDateFormat;
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
 * LocaSMS sms provider.
 * 
 * http://locasms.com.br/download/locasms-manual-api.pdf
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */
public class LocaSMSProvider implements AnterosSMSProvider {

	private String url;
	private String password;
	private String userName;

	public LocaSMSProvider(String userName, String password) {
		this(userName, password, "http://209.133.205.2/painel/api.ashx");
	}

	public LocaSMSProvider(String userName, String password, String url) {
		this.userName = userName;
		this.password = password;
		this.url = url;
	}

	@Override
	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {
		HttpResponse<String> response = Unirest.get(this.url + "?action=sendsms&lgn=" + this.userName + "&pwd="
				+ this.password + "&msg=" + content + "&numbers=" + receiver).asString();

		if (response.getStatus() == 200) {
			JSONObject jsonObject = new JSONObject(response.getBody());
			if (jsonObject.getInt("status") == 1 && jsonObject.getString("msg").equals("SUCESSO")) {
				return SMSResult.of(SMSStatus.SCHEDULED, jsonObject.getString("msg"), null,
						jsonObject.getString("data"));
			} else {
				return SMSResult.of(SMSStatus.ERROR, jsonObject.getString("msg"), null, jsonObject.getString("data"));
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. LocaSMS");
		}
	}

	@Override
	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receiver) throws Exception {
		boolean appendDelimiter = false;
		StringBuilder sb = new StringBuilder();
		for (String rec : receiver) {
			if (appendDelimiter) {
				sb.append(",");
			}
			sb.append(rec);
			appendDelimiter = true;
		}
		HttpResponse<String> response = Unirest.get(this.url + "?action=sendsms&lgn=" + this.userName + "&pwd="
				+ this.password + "&msg=" + content + "&numbers=" + sb.toString()).asString();

		if (response.getStatus() == 200) {
			JSONObject jsonObject = new JSONObject(response.getBody());
			if (jsonObject.getInt("status") == 1 && jsonObject.getString("msg").equals("SUCESSO")) {
				return new SMSResult[] { SMSResult.of(SMSStatus.SCHEDULED, jsonObject.getString("msg"), null,
						jsonObject.getString("data")) };
			} else {
				return new SMSResult[] { SMSResult.of(SMSStatus.ERROR, jsonObject.getString("msg"), null,
						jsonObject.getString("data")) };
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro enviando SMS. LocaSMS");
		}
	}

	@Override
	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receiver)
			throws Exception {
		boolean appendDelimiter = false;
		StringBuilder sb = new StringBuilder();
		for (String rec : receiver) {
			if (appendDelimiter) {
				sb.append(",");
			}
			sb.append(rec);
			appendDelimiter = true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdt = new SimpleDateFormat("HH:mm");

		HttpResponse<String> response = Unirest.get(this.url + "?action=sendsms&lgn=" + this.userName + "&pwd="
				+ this.password + "&msg=" + content + "&numbers=" + sb.toString() + "&jobdate="
				+ sdf.format(scheduleDate) + "&jobtime=" + sdt.format(scheduleDate)).asString();

		if (response.getStatus() == 200) {
			JSONObject jsonObject = new JSONObject(response.getBody());
			if (jsonObject.getInt("status") == 1 && jsonObject.getString("msg").equals("SUCESSO")) {
				return new SMSResult[] { SMSResult.of(SMSStatus.SCHEDULED, jsonObject.getString("msg"), null,
						jsonObject.getString("data")) };
			} else {
				return new SMSResult[] { SMSResult.of(SMSStatus.ERROR, jsonObject.getString("msg"), null,
						jsonObject.getString("data")) };
			}
		} else {
			throw new AnterosSMSIntegrationException("Ocorreu um erro agendando SMS. LocaSMS");
		}
	}

	@Override
	public SMSResult getAccountBalance() throws Exception {
		HttpResponse<String> response = Unirest
				.get(this.url + "?action=getbalance&lgn=" + this.userName + "&pwd=" + this.password).asString();
		JSONObject object = new JSONObject(response.getBody());
		return SMSResult.of(SMSStatus.SENT, object.getString("data"), null);
	}

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {
		Collection<SMSResult> result = new ArrayList<>();
		for (String id : ids) {
			HttpResponse<String> response = Unirest.get(this.url + "?action=getstatus&lgn=" + this.userName + "&pwd=" + this.password+ "&id="+id).asString();
			JSONObject object = new JSONObject(response.getBody());
			
			if (response.getStatus() == 200) {
				result.add(SMSResult.of(SMSStatus.SENT, object.getString("data"), null));
			} else {
				throw new AnterosSMSIntegrationException("Ocorreu um erro comsultado status SMS. LocaSMS");
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
