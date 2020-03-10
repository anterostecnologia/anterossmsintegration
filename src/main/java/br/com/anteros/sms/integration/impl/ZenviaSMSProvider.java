package br.com.anteros.sms.integration.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.anteros.sms.integration.AnterosSMSProvider;
import br.com.anteros.sms.integration.SMSResult;
import br.com.anteros.sms.integration.SMSStatus;
import br.com.anteros.sms.integration.exception.AnterosSMSIntegrationException;
import br.com.zenvia.client.RestClient;
import br.com.zenvia.client.exception.RestClientException;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;
import br.com.zenvia.client.request.SingleMessageSms;
import br.com.zenvia.client.response.BaseResponse;
import br.com.zenvia.client.response.GetSmsStatusResponse;
import br.com.zenvia.client.response.SendMultipleSmsResponse;
import br.com.zenvia.client.response.SendSmsResponse;

/**
 * Zenvia provider SMS
 * 
 * https://zenviasms.docs.apiary.io/#
 * 
 * @author Edson Martins - Relevant Solutions
 *
 */

public class ZenviaSMSProvider implements AnterosSMSProvider {

	private RestClient client;

	public ZenviaSMSProvider(String userName, String password) {
		this("https://api-rest.zenvia.com/services", userName, password);
	}

	public ZenviaSMSProvider(String url, String userName, String password) {
		client = new RestClient(url);
		client.setUsername(userName);
		client.setPassword(password);
	}

	public SMSResult send(String id, String sender, String content, String receiver) throws Exception {

		SingleMessageSms message = new SingleMessageSms();
		message.setFrom(sender);
		message.setTo(receiver);
		message.setMsg(content);
		message.setId(id);

		try {
			SendSmsResponse response = client.sendSms(message);

			if (response.getStatusCode().equals("00") && response.getDetailCode().equals("000")) {
				return SMSResult.of(SMSStatus.SENT, translateMessageCode(response), null);
			} else if (response.getStatusCode().equals("01")) {
				return SMSResult.of(SMSStatus.SCHEDULED, translateMessageCode(response), null);
			} else {
				return SMSResult.of(SMSStatus.ERROR, translateMessageCode(response), null);
			}
		} catch (RestClientException e) {
			throw new AnterosSMSIntegrationException(e);
		}
	}

	private String translateMessageCode(BaseResponse response) {
		StringBuilder sb = new StringBuilder();
		if (response.getStatusCode().equals("00")) {
			sb.append("OK");
		} else if (response.getStatusCode().equals("01")) {
			sb.append("Agendada");
		} else if (response.getStatusCode().equals("02")) {
			sb.append("Enviada");
		} else if (response.getStatusCode().equals("03")) {
			sb.append("Entregue");
		} else if (response.getStatusCode().equals("04")) {
			sb.append("Não recebida");
		} else if (response.getStatusCode().equals("05")) {
			sb.append("Bloqueada - Sem cobertura");
		} else if (response.getStatusCode().equals("06")) {
			sb.append("Bloqueada - Lista negra");
		} else if (response.getStatusCode().equals("07")) {
			sb.append("Bloqueada - Número inválido");
		} else if (response.getStatusCode().equals("08")) {
			sb.append("Bloqueada - Conteúdo não permitido ou mensagem expirada");
		} else if (response.getStatusCode().equals("09")) {
			sb.append("Bloqueada");
		} else if (response.getStatusCode().equals("10")) {
			sb.append("Erro");
		}

		if (response.getStatusCode().equals("000")) {
			sb.append(" - Mensagem enviada");
		} else if (response.getStatusCode().equals("002")) {
			sb.append(" - Mensagem cancelada com sucesso");
		} else if (response.getStatusCode().equals("010")) {
			sb.append(" - Conteúdo da mensagem em branco");
		} else if (response.getStatusCode().equals("011")) {
			sb.append(" - Corpo da mensagem inválido");
		} else if (response.getStatusCode().equals("012")) {
			sb.append(" - Conteúdo da mensagem muita grande");
		} else if (response.getStatusCode().equals("013")) {
			sb.append(" - Número mobile destino incorreto ou incompleto");
		} else if (response.getStatusCode().equals("014")) {
			sb.append(" - Número mobile destino em branco");
		} else if (response.getStatusCode().equals("015")) {
			sb.append(" - Data agendamento inválida");
		} else if (response.getStatusCode().equals("016")) {
			sb.append(" - ID grande demais");
		} else if (response.getStatusCode().equals("017")) {
			sb.append(" - Parâmetro url inválida");
		} else if (response.getStatusCode().equals("018")) {
			sb.append(" - Campo origem inválido");
		} else if (response.getStatusCode().equals("021")) {
			sb.append(" - ID é obrigatório");
		} else if (response.getStatusCode().equals("080")) {
			sb.append(" - Mensagem com o mesmo ID já enviada");
		} else if (response.getStatusCode().equals("100")) {
			sb.append(" - Mensagem na fila");
		} else if (response.getStatusCode().equals("110")) {
			sb.append(" - Mensagem enviada para operadora");
		} else if (response.getStatusCode().equals("111")) {
			sb.append(" - Confirmação de mensagem indisponível");
		} else if (response.getStatusCode().equals("120")) {
			sb.append(" - Mensagem recebida pelo celular");
		} else if (response.getStatusCode().equals("130")) {
			sb.append(" - Mensagem bloqueada");
		} else if (response.getStatusCode().equals("131")) {
			sb.append(" - Mensagem bloqueada");
		} else if (response.getStatusCode().equals("132")) {
			sb.append(" - Mensagem já cancelada");
		} else if (response.getStatusCode().equals("133")) {
			sb.append(" - Conteúdo da mensagem sendo analisada");
		} else if (response.getStatusCode().equals("134")) {
			sb.append(" - Mensagem bloqueada por conteúdo proibido");
		} else if (response.getStatusCode().equals("135")) {
			sb.append(" - O id agregado é inválido ou inativo");
		} else if (response.getStatusCode().equals("136")) {
			sb.append(" - Mensagem expirada");
		} else if (response.getStatusCode().equals("140")) {
			sb.append(" - Número de celular não coberto");
		} else if (response.getStatusCode().equals("141")) {
			sb.append(" - Envio internacional não permitido");
		} else if (response.getStatusCode().equals("145")) {
			sb.append(" - Número de celular inativo");
		} else if (response.getStatusCode().equals("150")) {
			sb.append(" - Mensagem expirada na operadora");
		} else if (response.getStatusCode().equals("160")) {
			sb.append(" - Erro de rede na operadora");
		} else if (response.getStatusCode().equals("161")) {
			sb.append(" - Mensagem rejeitada pela operadora");
		} else if (response.getStatusCode().equals("161")) {
			sb.append(" - Mensagem cancelada ou bloqueada pela operadora");
		} else if (response.getStatusCode().equals("170")) {
			sb.append(" - Mensagem incorreta");
		} else if (response.getStatusCode().equals("171")) {
			sb.append(" - Número incorreto");
		} else if (response.getStatusCode().equals("172")) {
			sb.append(" - Parâmetro incorreto");
		} else if (response.getStatusCode().equals("180")) {
			sb.append(" - ID da mensagem não encontrado");
		} else if (response.getStatusCode().equals("190")) {
			sb.append(" - Erro desconhecido");
		} else if (response.getStatusCode().equals("200")) {
			sb.append(" - Mensagagens enviadas");
		} else if (response.getStatusCode().equals("210")) {
			sb.append(" - Mensagens agendadas, mas o limite da conta foi atingido");
		} else if (response.getStatusCode().equals("240")) {
			sb.append(" - Arquivo em branco ou não enviado");
		} else if (response.getStatusCode().equals("241")) {
			sb.append(" - Arquivo muito grande");
		} else if (response.getStatusCode().equals("242")) {
			sb.append(" - Erro lendo arquivo");
		} else if (response.getStatusCode().equals("300")) {
			sb.append(" - Mensagens recebidas encontradas");
		} else if (response.getStatusCode().equals("301")) {
			sb.append(" - Nenhuma mensagem recebida encontrada");
		} else if (response.getStatusCode().equals("400")) {
			sb.append(" - Entidade salva");
		} else if (response.getStatusCode().equals("900")) {
			sb.append(" - Erro de autenticação");
		} else if (response.getStatusCode().equals("901")) {
			sb.append(" - O tipo de conta não suporta esta operação.");
		} else if (response.getStatusCode().equals("990")) {
			sb.append(" - Limite de conta atingido - entre em contato com o suporte.");
		} else if (response.getStatusCode().equals("998")) {
			sb.append(" - Operação incorreta solicitada.");
		} else if (response.getStatusCode().equals("999")) {
			sb.append(" - Erro desconhecido.");
		}
		return sb.toString();
	}

	public SMSResult[] sendMultiple(String[] ids, String sender, String content, String... receiver) throws Exception {
		MultipleMessageSms multiple = new MultipleMessageSms();
		int index = 0;
		for (String rec : receiver) {
			MessageSmsElement smsElement = new MessageSmsElement();
			smsElement.setFrom(sender);
			smsElement.setMsg(content);
			smsElement.setTo(rec);
			smsElement.setId(ids[index]);
			multiple.addMessageSms(smsElement);
			index++;
		}

		Collection<SMSResult> result = new ArrayList<>();

		try {
			SendMultipleSmsResponse smsResponse = client.sendMultipleSms(multiple);
			List<SendSmsResponse> responses = smsResponse.getResponses();
			for (SendSmsResponse response : responses) {
				if (response.getStatusCode().equals("00") && response.getDetailCode().equals("000")) {
					result.add(SMSResult.of(SMSStatus.SENT, translateMessageCode(response), null));
				} else if (response.getStatusCode().equals("01")) {
					result.add(SMSResult.of(SMSStatus.SCHEDULED, translateMessageCode(response), null));
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, translateMessageCode(response), null));
				}
			}
			return result.toArray(new SMSResult[] {});
		} catch (RestClientException e) {
			throw new AnterosSMSIntegrationException(e);
		}

	}

	public SMSResult[] schedule(String[] ids, String sender, String content, Date scheduleDate, String... receiver)
			throws Exception {
		MultipleMessageSms multiple = new MultipleMessageSms();
		int index = 0;
		for (String rec : receiver) {
			MessageSmsElement smsElement = new MessageSmsElement();
			smsElement.setFrom(sender);
			smsElement.setMsg(content);
			smsElement.setTo(rec);
			smsElement.setSchedule(scheduleDate);
			smsElement.setId(ids[index]);
			multiple.addMessageSms(smsElement);
			index++;
		}

		Collection<SMSResult> result = new ArrayList<>();

		try {
			SendMultipleSmsResponse smsResponse = client.sendMultipleSms(multiple);
			List<SendSmsResponse> responses = smsResponse.getResponses();
			for (SendSmsResponse response : responses) {
				if (response.getStatusCode().equals("00") && response.getDetailCode().equals("000")) {
					result.add(SMSResult.of(SMSStatus.SENT, translateMessageCode(response), null));
				} else if (response.getStatusCode().equals("01")) {
					result.add(SMSResult.of(SMSStatus.SCHEDULED, translateMessageCode(response), null));
				} else if (response.getStatusCode().equals("02")) {
					result.add(SMSResult.of(SMSStatus.SENT, translateMessageCode(response), null));
				} else if (response.getStatusCode().equals("03")) {
					result.add(SMSResult.of(SMSStatus.RECEIVED, translateMessageCode(response), null));
				} else {
					result.add(SMSResult.of(SMSStatus.ERROR, translateMessageCode(response), null));
				}
			}
			return result.toArray(new SMSResult[] {});
		} catch (RestClientException e) {
			throw new AnterosSMSIntegrationException(e);
		}
	}

	public SMSResult getAccountBalance() throws Exception {
		throw new AnterosSMSIntegrationException("Saldo da conta não implementado por este provider. ZenviaSMS.");
	}

	@Override
	public SMSResult[] getStatus(String[] ids) throws Exception {
		Collection<SMSResult> result = new ArrayList<>();
		for (String id : ids) {
			GetSmsStatusResponse response = client.getSmsStatus(id);
			if (response.getStatusCode().equals("00") && response.getDetailCode().equals("000")) {
				result.add(SMSResult.of(SMSStatus.SENT, translateMessageCode(response), null));
			} else if (response.getStatusCode().equals("01")) {
				result.add(SMSResult.of(SMSStatus.SCHEDULED, translateMessageCode(response), null));
			} else {
				result.add(SMSResult.of(SMSStatus.ERROR, translateMessageCode(response), null));
			}
		}
		return result.toArray(new SMSResult[] {});
	}

	@Override
	public boolean isSupportsStatusQuery() {
		return true;
	}

	@Override
	public boolean isSupportsAccountBalance() {
		return false;
	}

}

/*
 * Code Description 00 Ok 01 Scheduled 02 Sent 03 Delivered 04 Not Received 05
 * Blocked - No Coverage 06 Blocked - Black listed 07 Blocked - Invalid Number
 * 08 Blocked - Content not allowed 08 Blocked - Message Expired 09 Blocked 10
 * Error detailCode Code Description 000 Message Sent 002 Message successfully
 * canceled 010 Empty message content 011 Message body invalid 012 Message
 * content overflow 013 Incorrect or incomplete ‘to’ mobile number 014 Empty
 * ‘to’ mobile number 015 Scheduling date invalid or incorrect 016 ID overflow
 * 017 Parameter ‘url’ is invalid or incorrect 018 Field ‘from’ invalid 021 ‘id’
 * fieldismandatory 080 Message with same ID already sent 100 Message Queued 110
 * Message sent to operator 111 Message confirmation unavailable 120 Message
 * received by mobile 130 Message blocked 131 Message blocked by predictive
 * cleansing 132 Message already canceled 133 Message content in analysis 134
 * Message blocked by forbidden content 135 Aggregate is Invalid or Inactive 136
 * Message expired 140 Mobile number not covered 141 International sending not
 * allowed 145 Inactive mobile number 150 Message expired in operator 160
 * Operator network error 161 Message rejected by operator 162 Message cancelled
 * or blocked by operator 170 Bad message 171 Bad number 172 Missing parameter
 * 180 Message ID notfound 190 Unknown error 200 Messages Sent 210 Messages
 * scheduled but Account Limit Reached 240 File empty or not sent 241 File too
 * large 242 File readerror 300 Received messages found 301 No received messages
 * found 400 Entity saved 900 Authentication error 901 Account type not support
 * this operation. 990 Account Limit Reached – Please contact support 998 Wrong
 * operation requested 999 Unknown Error
 * 
 */