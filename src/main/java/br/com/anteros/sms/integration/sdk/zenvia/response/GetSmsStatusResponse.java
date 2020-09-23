package br.com.anteros.sms.integration.sdk.zenvia.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value="getSmsStatusResp")
public class GetSmsStatusResponse
extends BaseResponse {
    private String id;
    private Date received;
    private String shortcode;
    private String mobileOperatorName;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getReceived() {
        return this.received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public String getShortcode() {
        return this.shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getMobileOperatorName() {
        return this.mobileOperatorName;
    }

    public void setMobileOperatorName(String mobileOperatorName) {
        this.mobileOperatorName = mobileOperatorName;
    }
}

