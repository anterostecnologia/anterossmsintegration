package br.com.anteros.sms.integration.exception;

public class AnterosSMSIntegrationException extends RuntimeException {

	public AnterosSMSIntegrationException() {
	}

	public AnterosSMSIntegrationException(String message) {
		super(message);
	}

	public AnterosSMSIntegrationException(Throwable cause) {
		super(cause);
	}

	public AnterosSMSIntegrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnterosSMSIntegrationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


}
