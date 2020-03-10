package br.com.anteros.sms.integration.sdk.facilitamovel.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Biblioteca SDK 
 * @author Facilita Movel
 *
 */
public class CheckCredit {

	/**
	 * 
	 * @param method
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static String activeMessage(String params) throws Exception {
		URL url = new URL("http://www.facilitamovel.com.br/api/checkCredit.ft?"); 
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
		connection.setUseCaches (false);
		
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
	 * Verifica exatamente quantos creditos possui em sua conta no momento que for feita a requisicao
	 * https://www.facilitamovel.com.br/manuais/IntegracaoHTTPS.pdf
	 * @param usuario e senha
	 * @return Creditos em SMS de sua conta
	 */
	public static Integer checkRealCredit(String usuario, String senha) throws Exception {
		Integer credits = 0 ;
		if(usuario != null && senha != null){
			String params = "user=" + usuario + "&password=" + senha;

			String reqResult = activeMessage(params);
			if(reqResult != null && !"1;Login Invalido".equals(reqResult)){
				credits = new Integer(reqResult.split(";")[1]);
			} else {
				System.out.println("Login Invalido.");
				throw new Exception ("Login Invalido.");
			}
		}
		return credits;
	}
	
	
	
	
	
	
	
	

	
}
