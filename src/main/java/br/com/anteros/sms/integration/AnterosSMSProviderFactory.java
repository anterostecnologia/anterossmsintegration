package br.com.anteros.sms.integration;

import br.com.anteros.sms.integration.exception.AnterosSMSIntegrationException;
import br.com.anteros.sms.integration.impl.ComTeleSMSProvider;
import br.com.anteros.sms.integration.impl.FacilitaMovelSMSProvider;
import br.com.anteros.sms.integration.impl.GtiSMSProvider;
import br.com.anteros.sms.integration.impl.KingSMSProvider;
import br.com.anteros.sms.integration.impl.LocaSMSProvider;
import br.com.anteros.sms.integration.impl.SmsBaratoProvider;
import br.com.anteros.sms.integration.impl.ZenviaSMSProvider;

public class AnterosSMSProviderFactory {

	public static AnterosSMSProvider create(SMSProviders provider, String userName, String password) {
		switch (provider) {
		case Comtele:
			return new ComTeleSMSProvider(password);
		case FacilitaMovel:
			return new FacilitaMovelSMSProvider(userName, password);
		case GtiSMS:
			return new GtiSMSProvider(userName, password); 
		case KingSMS:
			return new KingSMSProvider(userName, password);
		case LocaSMS:
			return new LocaSMSProvider(userName, password);
		case SMSBarato:
			return new SmsBaratoProvider(password);
		case Zenvia:
			return new ZenviaSMSProvider(userName, password);		
		default:
			throw new AnterosSMSIntegrationException("Provider "+provider+" desconhecido.");
		}
	}

}
