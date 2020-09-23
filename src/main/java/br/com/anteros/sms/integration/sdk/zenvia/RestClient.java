package br.com.anteros.sms.integration.sdk.zenvia;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.sms.integration.sdk.zenvia.domain.ReceivedMessageFilter;
import br.com.anteros.sms.integration.sdk.zenvia.exception.RestClientException;
import br.com.anteros.sms.integration.sdk.zenvia.exception.UnauthorizedException;
import br.com.anteros.sms.integration.sdk.zenvia.request.MultipleMessageSms;
import br.com.anteros.sms.integration.sdk.zenvia.request.SingleMessageSms;
import br.com.anteros.sms.integration.sdk.zenvia.response.CancelSmsResponse;
import br.com.anteros.sms.integration.sdk.zenvia.response.GetSmsStatusResponse;
import br.com.anteros.sms.integration.sdk.zenvia.response.ReceivedMessageResponse;
import br.com.anteros.sms.integration.sdk.zenvia.response.SendMultipleSmsResponse;
import br.com.anteros.sms.integration.sdk.zenvia.response.SendSmsResponse;


/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RestClient {
    private static final String DEFAULT_REST_SERVER_BASE_URL = "https://api-rest.zenvia360.com.br";
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private final String baseUrl;
    private final String sendSmsUrl;
    private final String sendMultipleSmsUrl;
    private final String cancelSmsUrl;
    private final String getSmsStatusUrl;
    private final String listUnreadMessagesUrl;
    private final String searchReceivedMessagesUrl;
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);
    private String username;
    private String password;

    public RestClient() {
        String restServerUrl = System.getProperty("zenvia.rest.server.url");
        this.baseUrl = (StringUtils.isBlank((String)restServerUrl) ? "https://api-rest.zenvia360.com.br" : restServerUrl) + "/services";
        this.sendSmsUrl = this.baseUrl + "/send-sms";
        this.sendMultipleSmsUrl = this.baseUrl + "/send-sms-multiple";
        this.cancelSmsUrl = this.baseUrl + "/cancel-sms";
        this.getSmsStatusUrl = this.baseUrl + "/get-sms-status";
        this.listUnreadMessagesUrl = this.baseUrl + "/received/list";
        this.searchReceivedMessagesUrl = this.baseUrl + "/received/search";
    }
    
    public RestClient(String url) {
        String restServerUrl = url;
        this.baseUrl = (StringUtils.isBlank((String)restServerUrl) ? "https://api-rest.zenvia360.com.br" : restServerUrl) + "/services";
        this.sendSmsUrl = this.baseUrl + "/send-sms";
        this.sendMultipleSmsUrl = this.baseUrl + "/send-sms-multiple";
        this.cancelSmsUrl = this.baseUrl + "/cancel-sms";
        this.getSmsStatusUrl = this.baseUrl + "/get-sms-status";
        this.listUnreadMessagesUrl = this.baseUrl + "/received/list";
        this.searchReceivedMessagesUrl = this.baseUrl + "/received/search";
    }

    public SendSmsResponse sendSms(SingleMessageSms messageSms) throws RestClientException, UnauthorizedException {
        SendSmsResponse result = this.callPost(this.sendSmsUrl, messageSms, SendSmsResponse.class);
        return result;
    }

    public SendMultipleSmsResponse sendMultipleSms(MultipleMessageSms multipleMessageSms) throws RestClientException, UnauthorizedException {
        SendMultipleSmsResponse result = this.callPost(this.sendMultipleSmsUrl, multipleMessageSms, SendMultipleSmsResponse.class);
        return result;
    }

    public CancelSmsResponse cancelSms(String id) throws RestClientException, UnauthorizedException {
        if (StringUtils.isBlank((String)id)) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        CancelSmsResponse result = this.callPost(this.cancelSmsUrl + "/" + id, null, CancelSmsResponse.class);
        return result;
    }

    public GetSmsStatusResponse getSmsStatus(String id) throws RestClientException, UnauthorizedException {
        if (StringUtils.isBlank((String)id)) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        GetSmsStatusResponse result = this.callGet(this.getSmsStatusUrl + "/" + id, null, GetSmsStatusResponse.class);
        return result;
    }

    public ReceivedMessageResponse listUnreadMessages() throws RestClientException, UnauthorizedException {
        ReceivedMessageResponse result = this.callPost(this.listUnreadMessagesUrl, null, ReceivedMessageResponse.class);
        return result;
    }

    public ReceivedMessageResponse searchReceivedMessages(ReceivedMessageFilter filter) throws RestClientException, UnauthorizedException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Object[] urlWithPathParams = new String[]{this.searchReceivedMessagesUrl, formatter.format(filter.getStart()), formatter.format(filter.getEnd())};
        String url = StringUtils.join((Object[])urlWithPathParams, (char)'/');
        ArrayList<NameValuePair> queryParams = new ArrayList<NameValuePair>();
        if (filter.definesReferenceMessageId()) {
            queryParams.add((NameValuePair)new BasicNameValuePair("mtId", filter.getReferenceMessageId()));
        }
        if (filter.definesMobile()) {
            queryParams.add((NameValuePair)new BasicNameValuePair("mobile", filter.getMobile()));
        }
        ReceivedMessageResponse result = this.callGet(url, queryParams, ReceivedMessageResponse.class);
        return result;
    }

    private void setHeaders(HttpMessage httpMessage) {
        httpMessage.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        httpMessage.setHeader("Authorization", RestClient.getAuthorizationHeader(this.username, this.password));
    }

    private String serialize(Object object) throws RestClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");
            dateFormat.setTimeZone(timeZone);
            mapper.setDateFormat((DateFormat)dateFormat);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
            String jsonString = mapper.writeValueAsString(object);
            log.debug("Serialized object {} as JSON : {}", (Object)object.getClass().getName(), (Object)jsonString);
            return jsonString;
        }
        catch (JsonProcessingException e) {
            log.error("ERROR", (Throwable)e);
            throw new RestClientException((Throwable)e);
        }
    }

    private <T> T deserialize(String jsonResponse, Class<T> clazz) throws RestClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.handleException(mapper, jsonResponse);
            mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
            Object t = mapper.readValue(jsonResponse, clazz);
            return (T)t;
        }
        catch (IOException e) {
            log.error("ERROR", (Throwable)e);
            throw new RestClientException(e);
        }
    }

    private void handleException(ObjectMapper mapper, String jsonResponse) throws IOException, RestClientException {
        JsonNode rootJsonNode = mapper.readTree(jsonResponse);
        JsonNode exceptionNode = rootJsonNode.get("exception");
        if (exceptionNode != null) {
            JsonNode messageNode = exceptionNode.get("message");
            throw new RestClientException(messageNode == null ? jsonResponse : messageNode.toString());
        }
    }


    private static String getAuthorizationHeader(String username, String password) {
        String usernamePassword = username + ":" + password;
        String encodeBase64String = new String(Base64.encodeBase64((byte[])usernamePassword.getBytes()));
        String authorizationHeader = "Basic " + encodeBase64String;
        return authorizationHeader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private <T> T executeHttpCall(HttpClient httpClient, HttpRequestBase httpRequest, Class<T> clazz) throws RestClientException, UnauthorizedException {
        try {
            log.info("Executing request {}", (Object)httpRequest.getRequestLine());
            HttpResponse resp = httpClient.execute((HttpUriRequest)httpRequest);

            if (resp != null && resp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                throw new UnauthorizedException();
            }

            try {
                T result;
                HttpEntity entity = resp.getEntity();
                String jsonResponse = EntityUtils.toString((HttpEntity)entity);
                log.debug("JSON response to unmarshall as {} : {}", (Object)clazz.getName(), (Object)jsonResponse);
                T t = result = this.deserialize(jsonResponse, clazz);
                return t;
            }
            finally {
                HttpClientUtils.closeQuietly((HttpResponse)resp);
            }
        }
        catch (IOException e) {
            log.error("ERROR", (Throwable)e);
            throw new RestClientException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T callGet(String url, List<NameValuePair> queryParams, Class<T> responseClass) throws RestClientException, UnauthorizedException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            T result;
            if (queryParams != null && !queryParams.isEmpty()) {
                StringBuilder urlSb = new StringBuilder(url);
                if (!url.endsWith("?")) {
                    urlSb.append("?");
                }
                String paramString = URLEncodedUtils.format(queryParams, (String)"utf-8");
                urlSb.append(paramString);
                url = urlSb.toString();
            }
            HttpGet httpGet = new HttpGet(url);
            this.setHeaders((HttpMessage)httpGet);
            T t = result = this.executeHttpCall((HttpClient)httpClient, (HttpRequestBase)httpGet, responseClass);
            return t;
        }
        finally {
            HttpClientUtils.closeQuietly((HttpClient)httpClient);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T callPost(String url, Object object, Class<T> responseClass) throws RestClientException, UnauthorizedException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            Object en;
            HttpPost httpPost = new HttpPost(url);
            this.setHeaders((HttpMessage)httpPost);
            if (object != null) {
                String json = this.serialize(object);
                en = new StringEntity(json, ContentType.APPLICATION_JSON);
                httpPost.setEntity((HttpEntity)en);
            }
            T result = this.executeHttpCall((HttpClient)httpClient, (HttpRequestBase)httpPost, responseClass);
            en = result;
            return (T)en;
        }
        finally {
            HttpClientUtils.closeQuietly((HttpClient)httpClient);
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

