package br.com.anteros.sms.integration.sdk.facilitamovel.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.anteros.sms.integration.sdk.facilitamovel.bean.MO;

/**
 * Biblioteca SDK 
 * @author Facilita Movel
 *
 */
public class ReceiveMessage {

	/**
	 * 
	 * @param method
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static String activeMessage(String params) throws Exception {
		URL url = new URL("http://www.facilitamovel.com.br/api/readMO.ft?"); 
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("GET"); 
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
	 * Lista os MO nao lidos da plataforma, apos executar este metodo, o mesmo vai marcar como lido os MOs, portanto para
	 * continuar seus testes, va ate o painel de controle da Facilita, Mensagens Recebidas, e Marque todos como NAO LIDOS.
	 * https://www.facilitamovel.com.br/manuais/IntegracaoHTTPS.pdf
	 * @param sms
	 * @return Lista de MOs, caso voce possua
	 */
	public static List<MO> readUnreadMO(String usuario, String senha) throws Exception {
		List<MO> listaMO = null; 
		if(usuario != null && senha != null){
			String params = "user=" + usuario + "&password=" + senha;

			String reqResult = activeMessage(params);
			if(reqResult != null && !"1;Login Invalido".equals(reqResult)){
				if("".equals(reqResult)){
					System.out.println("Nao Existem Mos nao lidos");
				} else {
					String[] mos = reqResult.split("/n/n");
					if(mos != null && mos.length > 0 ){
						listaMO = new ArrayList<MO>();
						for (int i = 0; i < mos.length; i++) {
							String[] campo = mos[i].split("/n;");
							String telefone = campo[0];
							Date dataHora = new SimpleDateFormat("yyyy-MM-dd kk:mm").parse(campo[1]);
							String mensagem = "";
							if(campo.length > 2 ){
								mensagem = campo[2];
							}

							MO mo = new MO();
							mo.setTelefone(telefone);
							mo.setDataHora(dataHora);
							mo.setMensagem(mensagem);
							
							System.out.println("Telefone:" + telefone);
							System.out.println("Data/Hora:" + dataHora);
							System.out.println("Mensagem:" + mensagem);
							System.out.println("\n\n");
							
							listaMO.add(mo);
							
						}
					}
				}
			} else {
				System.out.println("Login Invalido.");
				throw new Exception ("Login Invalido.");
			}
			
		}
		return listaMO;
	}
	
	
	
	
	
	
	
	

	
}
