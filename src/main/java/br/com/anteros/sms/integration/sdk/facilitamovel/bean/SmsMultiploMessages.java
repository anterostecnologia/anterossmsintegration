package br.com.anteros.sms.integration.sdk.facilitamovel.bean;

import java.util.List;


public class SmsMultiploMessages {
	private Integer id;
	private String user;
	private String password;
	private List<String> destinatarios;
	private List<String> messages;
	private List<String> chaveClientes;
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
	public List<String> getDestinatarios() {
		return destinatarios;
	}
	public void setDestinatarios(List<String> destinatarios) {
		this.destinatarios = destinatarios;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public List<String> getChaveClientes() {
		return chaveClientes;
	}
	public void setChaveClientes(List<String> chaveClientes) {
		this.chaveClientes = chaveClientes;
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
