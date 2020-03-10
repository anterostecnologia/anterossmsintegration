package br.com.anteros.sms.integration.sdk.facilitamovel.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import br.com.anteros.sms.integration.sdk.facilitamovel.bean.Retorno;
import br.com.anteros.sms.integration.sdk.facilitamovel.bean.SmsMultiplo;
import br.com.anteros.sms.integration.sdk.facilitamovel.bean.SmsMultiploMessages;
import br.com.anteros.sms.integration.sdk.facilitamovel.bean.SmsSimples;

/**
 * Biblioteca SDK
 * 
 * @author Facilita Movel
 *
 */
public class SendMessage {

	/**
	 * 
	 * @param method
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static String activeMessage(String method, String params) throws Exception {
		URL url = new URL("http://www.facilitamovel.com.br/api/" + method);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
		connection.setUseCaches(false);

		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(params);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer strRet = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			strRet.append(line);
		}
		wr.close();
		rd.close();

		return "" + strRet;
	}

	/**
	 * Envia um SMS Simples, Item 4 do Manual de Integracao:
	 * https://www.facilitamovel.com.br/manuais/IntegracaoHTTPS.pdf
	 * 
	 * @param sms
	 * @return
	 */
	public static Retorno simpleSend(SmsSimples sms) throws Exception {
		Retorno ret = new Retorno();
		if (sms != null) {
			String params = "user=" + sms.getUser() + "&password=" + sms.getPassword() + "&destinatario="
					+ sms.getDestinatario() + "&msg=" + URLEncoder.encode(sms.getMessage(), "UTF-8");

			if (sms.getDia() != null && !"".equals(sms.getDia())) {
				params = params + "&day=" + sms.getDia();
			}

			if (sms.getMes() != null && !"".equals(sms.getMes())) {
				params = params + "&month=" + sms.getMes();
			}

			if (sms.getAno() != null && !"".equals(sms.getAno())) {
				params = params + "&year=" + sms.getAno();
			}

			if (sms.getHora() != null && !"".equals(sms.getHora())) {
				params = params + "&formHour=" + sms.getHora();
			}

			if (sms.getMinuto() != null && !"".equals(sms.getMinuto())) {
				params = params + "&formMinute=" + sms.getMinuto();
			}
			if (sms.getFlashsms() != null && sms.getFlashsms().intValue() == 1) {
				params = params + "&flashsms=1";
			}

			System.out.println(params);

			String str = activeMessage("simpleSend.ft?", params);
			String[] arrRet = str.split(";");
			ret.setCodigo(new Integer(arrRet[0]));
			ret.setMensagem(arrRet[1]);

			if (ret.getCodigo() == 5 || ret.getCodigo() == 6) {
				ret.setId(arrRet[2]);
			}
		}
		return ret;
	}

	/**
	 * Obtém o status de um ou mais SMS, Item 4 do Manual de Integracao:
	 * https://www.facilitamovel.com.br/manuais/IntegracaoHTTPS.pdf
	 * 
	 * @param sms
	 * @return
	 */
	public static Retorno[] getStatus(String userName, String password, String[] ids) throws Exception {
		Collection<Retorno> result = new ArrayList<>();
		if (ids != null) {

			StringBuilder sb = new StringBuilder();
			boolean appendDelimiter = false;
			for (String id : ids) {
				if (appendDelimiter)
					sb.append(",");
				sb.append(id);
				appendDelimiter = true;
			}

			String params = "user=" + userName + "&password=" + password + "&externalkey=" + sb.toString();

			String str = activeMessage("dlrByExternalKey.ft?", params);
			String[] arrRet = str.split("-");

			for (String r : arrRet) {
				String[] ret = r.split(";");
				Retorno rt = new Retorno();
				rt.setCodigo(new Integer(ret[1]));
				rt.setId(ret[0]);
			}

		}
		return result.toArray(new Retorno[] {});
	}

	/**
	 * Envia uma mensagem para multiplos destinatarios, Item 5 do Manual de
	 * Integracao: https://www.facilitamovel.com.br/manuais/IntegracaoHTTPS.pdf
	 * 
	 * @param sms
	 * @return
	 * @throws Exception
	 */
	public static Retorno multipleSend(SmsMultiplo sms) throws Exception {
		Retorno ret = new Retorno();
		if (sms != null) {
			String nmbs = "";
			if (sms.getDestinatarios() != null && sms.getDestinatarios().size() > 0) {
				for (String nmb : sms.getDestinatarios()) {
					nmbs += nmb + ";";
				}
			}

			String keys = "";
			if (sms.getChaveClientes() != null && sms.getChaveClientes().size() > 0) {
				for (String key : sms.getChaveClientes()) {
					keys += key + ";";
				}
			}

			String params = "user=" + sms.getUser() + "&password=" + sms.getPassword() + "&destinatario=" + nmbs
					+ "&externalkey=" + keys + "&msg=" + URLEncoder.encode(sms.getMessage(), "UTF-8");

			if (sms.getDia() != null && !"".equals(sms.getDia())) {
				params = params + "&day=" + sms.getDia();
			}

			if (sms.getMes() != null && !"".equals(sms.getMes())) {
				params = params + "&month=" + sms.getMes();
			}

			if (sms.getAno() != null && !"".equals(sms.getAno())) {
				params = params + "&year=" + sms.getAno();
			}

			if (sms.getHora() != null && !"".equals(sms.getHora())) {
				params = params + "&formHour=" + sms.getHora();
			}

			if (sms.getMinuto() != null && !"".equals(sms.getMinuto())) {
				params = params + "&formMinute=" + sms.getMinuto();
			}

			if (sms.getFlashsms() != null && sms.getFlashsms().intValue() == 1) {
				params = params + "&flashsms=1";
			}

			String str = activeMessage("multipleSend.ft?", params);
			String[] arrRet = str.split(";");
			ret.setCodigo(new Integer(arrRet[0]));
			ret.setMensagem(arrRet[1]);
			if (ret.getCodigo() == 5 || ret.getCodigo() == 6) {
				ret.setId(arrRet[2]);
			}
		}
		return ret;
	}

	/**
	 * Envia Multiplas Mensagem para Multiplos telefones, ver Item 5.1 do Manual de
	 * Integracao https://www.facilitamovel.com.br/manuais/IntegracaoHTTPS.pdf
	 * 
	 * @param sms
	 * @return
	 * @throws Exception
	 */
	public static Retorno multipleMessagesToMultPhones(SmsMultiploMessages sms) throws Exception {
		Retorno ret = new Retorno();
		if (sms != null) {
			String nmbs = "";
			if (sms.getDestinatarios() != null && sms.getDestinatarios().size() > 0) {
				for (String nmb : sms.getDestinatarios()) {
					nmbs += nmb + "/n";
				}
			}

			String keys = "";
			if (sms.getChaveClientes() != null && sms.getChaveClientes().size() > 0) {
				for (String key : sms.getChaveClientes()) {
					keys += key + "/n";
				}
			}

			String msgs = "";
			if (sms.getMessages() != null && sms.getMessages().size() > 0) {
				for (String msg : sms.getMessages()) {
					msgs += msg + "/n";
				}
			}

			String params = "user=" + sms.getUser() + "&password=" + sms.getPassword() + "&destinatario=" + nmbs
					+ "&externalkey=" + keys + "&msg=" + URLEncoder.encode(msgs, "UTF-8");

			if (sms.getFlashsms() != null && sms.getFlashsms().intValue() == 1) {
				params = params + "&flashsms=1";
			}

			String str = activeMessage("messagesPhonesMultipleSend.ft?", params);
			String[] arrRet = str.split(";");
			ret.setCodigo(new Integer(arrRet[0]));
			ret.setMensagem(arrRet[1]);
		}
		return ret;
	}

}
