package br.com.anteros.sms.integration;

public class SMSResult {
	
	private SMSStatus status;
	private String message;	
	private Object value;
	private String id;
	

	private SMSResult(SMSStatus status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	private SMSResult(SMSStatus status, String message, Object value) {
		super();
		this.status = status;
		this.message = message;
		this.value = value;
	}
	
	private SMSResult(SMSStatus status, String message, Object value, String id) {
		super();
		this.status = status;
		this.message = message;
		this.value = value;
		this.id = id;
	}

	public static SMSResult of(SMSStatus status, String message) {
		return new SMSResult(status, message);
	}
	
	public static SMSResult of(SMSStatus status, String message, Object value) {
		return new SMSResult(status, message,value);
	}
	
	public static SMSResult of(SMSStatus status, String message, Object value, String id) {
		return new SMSResult(status, message,value,id);
	}

	public SMSStatus getStatus() {
		return status;
	}

	public void setStatus(SMSStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
