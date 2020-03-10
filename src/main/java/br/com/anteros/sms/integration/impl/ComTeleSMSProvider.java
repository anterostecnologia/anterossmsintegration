package br.com.anteros.sms.integration.impl;

import java.util.Arrays;
import java.util.Date;

import br.com.anteros.sms.integration.AnterosSMSProvider;
import br.com.anteros.sms.integration.SMSResult;
import br.com.anteros.sms.integration.SMSStatus;
import br.com.anteros.sms.integration.exception.AnterosSMSIntegrationException;
import models.DefaultServiceResult;
import services.CreditService;
import services.TextMessageService;

/**
 * ComTele provider SMS
 * 
 * https://docs.comtele.com.br/
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */
public class ComTeleSMSProvider implements AnterosSMSProvider {

	private String apiKey;

	public ComTeleSMSProvider(String apiKey) {
		this.apiKey = apiKey;
	}

	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {
		TextMessageService textMessageService = new TextMessageService(apiKey);
		DefaultServiceResult result = textMessageService.send(sender, content, Arrays.asList(receiver));

		if (!result.Success) {
			return SMSResult.of(SMSStatus.ERROR, result.Message);
		} else {
			return SMSResult.of(SMSStatus.SENT, result.Message);
		}
	}

	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receivers) throws Exception {
		TextMessageService textMessageService = new TextMessageService(apiKey);
		DefaultServiceResult result = textMessageService.send(sender, content, Arrays.asList(receivers));
		if (!result.Success) {
			return new SMSResult[] {SMSResult.of(SMSStatus.ERROR, result.Message)};
		} else {
			return new SMSResult[] {SMSResult.of(SMSStatus.SENT, result.Message)};
		}
	}

	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receivers)
			throws Exception {
		TextMessageService textMessageService = new TextMessageService(apiKey);
		DefaultServiceResult result = textMessageService.schedule(sender, content, scheduleDate,
				Arrays.asList(receivers));
		
		if (!result.Success) {
			return new SMSResult[] {SMSResult.of(SMSStatus.ERROR, result.Message)};
		} else {
			return new SMSResult[] {SMSResult.of(SMSStatus.SENT, result.Message)};
		}
	}

	public SMSResult getAccountBalance() throws Exception {
		CreditService creditService = new CreditService(apiKey);
		Integer myCredits = creditService.getMyCredits();
		return SMSResult.of(SMSStatus.SENT, "",myCredits);
	}

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {
		throw new AnterosSMSIntegrationException("Status não implementado por este provider. ComTeleSMS.");
	}

	@Override
	public boolean isSupportsStatusQuery() {
		return false;
	}

	@Override
	public boolean isSupportsAccountBalance() {
		return true;
	}

}
