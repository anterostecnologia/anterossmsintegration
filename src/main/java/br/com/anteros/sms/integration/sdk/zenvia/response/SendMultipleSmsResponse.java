package br.com.anteros.sms.integration.sdk.zenvia.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JsonRootName(value="sendSmsMultiResponse")
public class SendMultipleSmsResponse {
    @JsonProperty(value="sendSmsResponseList")
    private List<SendSmsResponse> responses = new ArrayList<SendSmsResponse>();

    public List<SendSmsResponse> getResponses() {
        return this.responses;
    }

    public void setResponses(List<SendSmsResponse> responses) {
        this.responses = responses;
    }
}

