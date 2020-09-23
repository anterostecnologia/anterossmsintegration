package br.com.anteros.sms.integration.sdk.zenvia.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JsonRootName(value="sendSmsMultiRequest")
public class MultipleMessageSms {

    private Integer aggregateId;

    @JsonProperty(value="sendSmsRequestList")
    private List<MessageSmsElement> messages = new ArrayList<MessageSmsElement>();

    public Integer getAggregateId() {
        return this.aggregateId;
    }

    public void setAggregateId(Integer aggregateId) {
        this.aggregateId = aggregateId;
    }

    public List<MessageSmsElement> getMessages() {
        return this.messages;
    }

    public boolean addMessageSms(MessageSmsElement messageSms) {
        return this.messages.add(messageSms);
    }
}

