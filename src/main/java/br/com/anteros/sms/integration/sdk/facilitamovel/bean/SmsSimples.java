package br.com.anteros.sms.integration.sdk.facilitamovel.bean;


public class SmsSimples {

	private Integer id;
	private String user;
	private String password;
	private String destinatario;
	private String message;
	private String chaveCliente;
	private Integer dia;
	private Integer mes;
	private Integer ano;
	private Integer minuto;	
	private Integer hora;
	private Integer flashsms;
	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getChaveCliente() {
		return chaveCliente;
	}
	public void setChaveCliente(String chaveCliente) {
		this.chaveCliente = chaveCliente;
	}
	public Integer getDia() {
		return dia;
	}
	public void setDia(Integer dia) {
		this.dia = dia;
	}
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public Integer getMinuto() {
		return minuto;
	}
	public void setMinuto(Integer minuto) {
		this.minuto = minuto;
	}
	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}
	public Integer getFlashsms() {
		return flashsms;
	}
	/**
	 * Seta para 1 quando quiser enviar FLASHSMS, caso contrario
	 * deixe como nulo ou nem use este atributo
	 * @param flashsms
	 */
	public void setFlashsms(Integer flashsms) {
		this.flashsms = flashsms;
	}

}
