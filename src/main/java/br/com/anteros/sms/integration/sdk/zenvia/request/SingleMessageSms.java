package br.com.anteros.sms.integration.sdk.zenvia.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value="sendSmsRequest")
public class SingleMessageSms
extends AbstractMessageSms {
    @JsonProperty(value="aggregateId")
    private Integer aggregatorId;

    public Integer getAggregatorId() {
        return this.aggregatorId;
    }

    public void setAggregatorId(Integer aggregatorId) {
        this.aggregatorId = aggregatorId;
    }
}

