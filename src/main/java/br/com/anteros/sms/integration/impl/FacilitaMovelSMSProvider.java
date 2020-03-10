package br.com.anteros.sms.integration.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import br.com.anteros.sms.integration.AnterosSMSProvider;
import br.com.anteros.sms.integration.SMSResult;
import br.com.anteros.sms.integration.SMSStatus;
import br.com.anteros.sms.integration.exception.AnterosSMSIntegrationException;
import br.com.anteros.sms.integration.sdk.facilitamovel.bean.Retorno;
import br.com.anteros.sms.integration.sdk.facilitamovel.bean.SmsMultiplo;
import br.com.anteros.sms.integration.sdk.facilitamovel.bean.SmsSimples;
import br.com.anteros.sms.integration.sdk.facilitamovel.service.CheckCredit;
import br.com.anteros.sms.integration.sdk.facilitamovel.service.SendMessage;


/**
 * FaciltaMovel provider sms
 * 
 * http://www.facilitamovel.com.br/manuais/http/#documenter-1
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */
public class FacilitaMovelSMSProvider implements AnterosSMSProvider {
	
	
	private String userName;
	private String url;
	private String password;

	public FacilitaMovelSMSProvider(String userName, String password){
		this("http://www.facilitamovel.com.br/api/",userName, password);
	}
	
	public FacilitaMovelSMSProvider(String url, String userName, String password){
		this.userName = userName;
		this.url = url;
		this.password = password;
	}
	

	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {
		SmsSimples sms = new SmsSimples();
		try {
			sms.setDestinatario(receiver);
			sms.setUser(userName);
			sms.setPassword(password);
			sms.setMessage(content);
			sms.setChaveCliente(id);
			Retorno result = SendMessage.simpleSend(sms);
			return processResponse(result);			
			
		} catch (Exception e) {
			throw new AnterosSMSIntegrationException(e);
		}
	}

	protected SMSResult processResponse(Retorno result) {
		switch (result.getCodigo()) {
		case 5:
			return SMSResult.of(SMSStatus.SCHEDULED, "OK",null,result.getId());
		case 6:
			return SMSResult.of(SMSStatus.SENT, "OK",null,result.getId());
		case 1:
			return SMSResult.of(SMSStatus.ERROR, "Usuário ou Senha enviados na URL estão inválidos, ou a conta pode estar inativa/cancelada.",null);
		case 2:
			return SMSResult.of(SMSStatus.ERROR, "Usuário sem Créditos",null);
		case 3:
			return SMSResult.of(SMSStatus.ERROR, "Todos os envios foram invalidados. Todos celulares são inválidos",null);
		case 4:
			return SMSResult.of(SMSStatus.ERROR, "A mensagem passada está vazia ou possui características de uma mensagem inválida",null);
		case 7:
			return SMSResult.of(SMSStatus.ERROR, "Algumas mensagens foram enviadas, mas alguns números estão com problemas",null);
		}
		return SMSResult.of(SMSStatus.ERROR, "Ocorreu um erro desconhecido",null);
	}

	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receivers) throws Exception {
		
		SmsMultiplo multiplo = new SmsMultiplo();
		multiplo.setDestinatarios(Arrays.asList(receivers));
		multiplo.setMessage(content);
		multiplo.setUser(userName);
		multiplo.setPassword(password);
		multiplo.setMessage(content);
		multiplo.setChaveClientes(Arrays.asList(ids));
		
		try {
			Retorno result = SendMessage.multipleSend(multiplo);
			return new SMSResult[] {processResponse(result)};
		} catch (Exception e) {
			throw new AnterosSMSIntegrationException(e);
		}
	}

	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receivers) throws Exception {
		SmsMultiplo multiplo = new SmsMultiplo();
		multiplo.setDestinatarios(Arrays.asList(receivers));
		multiplo.setMessage(content);
		multiplo.setUser(userName);
		multiplo.setPassword(password);
		multiplo.setMessage(content);
		Calendar cal = Calendar.getInstance();
		cal.setTime(scheduleDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		multiplo.setAno(year);
		multiplo.setDia(day);
		multiplo.setMes(month);
		multiplo.setHora(hour);
		multiplo.setMinuto(min);		
		
		try {
			Retorno result = SendMessage.multipleSend(multiplo);
			return new SMSResult[] {processResponse(result)};
		} catch (Exception e) {
			throw new AnterosSMSIntegrationException(e);
		}
	}

	public SMSResult getAccountBalance() throws Exception {
		try {
			Integer realCredit = CheckCredit.checkRealCredit(userName, password);
			return SMSResult.of(SMSStatus.SENT, "",realCredit);
		} catch (Exception e) {
			throw new AnterosSMSIntegrationException(e);
		}
	}

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {		
		Retorno[] retornos = SendMessage.getStatus(userName, password, ids);
		Collection<SMSResult> result = new ArrayList<>();
		for (Retorno ret : retornos) {
		   result.add(processResponse(ret));
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

