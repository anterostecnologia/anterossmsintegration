package br.com.anteros.sms.integration;

import java.util.Date;

public interface AnterosSMSProvider {
	
	public SMSResult send(String id, String sender, String content, String receiver) throws Exception;
	
	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receivers)  throws Exception;
	
	public SMSResult[] schedule(String ids[], String sender, String content, Date scheduleDate, String... receivers) throws Exception;
	
	public SMSResult[] getStatus(String[] ids)  throws Exception;
	
	public SMSResult getAccountBalance()  throws Exception;
	
	public boolean isSupportsStatusQuery();
	
	public boolean isSupportsAccountBalance();

}
