package com.charter.provisioning.hss.external;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.charter.provisioning.hss.common.SoapMessage;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.SoapServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HSSSubscriberProxy {
	
	private HssServiceConfig serviceConfig;

	@Autowired
	public HSSSubscriberProxy(HssServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}

	public String sendAndReceive(SoapMessage message, String transactionId) throws SoapServiceException {
		
		String response;
		try {
			response = handleRequest(message, transactionId);
		} catch (SoapServiceException | IOException | URISyntaxException e) {
			String errorMsg = "Protocol related error while sending message";
			throw new SoapServiceException(errorMsg);
		}
		return response;
	}
	
	private String handleRequest(SoapMessage message, String transactionId) throws SoapServiceException, IOException, URISyntaxException {

		HttpUriRequest method;
		HttpResponse httpResponse = null;
		String response = null;
		SoapMessage respMessage;
		try {
			method = createMethod(message, transactionId);

			log.debug("[{}] - URI = {}",transactionId,method.getRequestLine().getUri());
			log.debug("[{}] - Query = {}",transactionId,method.getURI().getQuery());
			log.debug("[{}] - Host = {}",transactionId,method.getURI().getHost());
			log.debug("[{}] - Port = {}",transactionId,method.getURI().getPort());
			log.debug("[{}] - Raw Query = {}",transactionId,method.getURI().getRawQuery());
			
			HttpClientContext context = getClientContext(message);

			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));

			HttpClient client = HttpClientBuilder.create().setDefaultHeaders(headers).build();
			httpResponse = client.execute(method, context);

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			log.debug("statusCode={}", statusCode);

			if (isStatusError(statusCode)) {
				String errorMsg = "HTTP method execution failed, response statusLine=" + httpResponse.getStatusLine();
				log.error(errorMsg);
				throw new SoapServiceException(errorMsg);
				
			} else if (null != httpResponse.getEntity()) {
				
				respMessage = SoapMessage.class.newInstance();
				HttpEntity entity = httpResponse.getEntity();
				InputStream is = entity.getContent();
				byte[] bytes = IOUtils.toByteArray(is);
				respMessage.setMessage(bytes);

				response = respMessage.getPayload();
				log.debug("[{}] - Getting the response : {}", transactionId, response);
			}
			
		} catch (Exception e) {
			String errorMsg = "Protocol related error while sending message";
			throw new SoapServiceException(errorMsg, e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
		}
		return response;
	}

	private HttpUriRequest createMethod(SoapMessage message, String transactionId) throws SoapServiceException{
		
        HttpPost postMethod = new HttpPost(message.getPropertyValue(serviceConfig.getUrl()));
        
        log.debug("[{}] - *** createStringRequestEntity from SOAPProtocolmessage {}",transactionId, message.getSoapXml());
        HttpEntity requestEntity = EntityBuilder.create().setContentType(ContentType.TEXT_XML).setText(message.getSoapXml()).build();
        
        if (requestEntity != null) {
        	postMethod.setEntity(requestEntity);
        	postMethod.setHeader(message.getSoapAction(),message.getSoapHeader());
        }
        return postMethod;
	}

	private HttpClientContext getClientContext(SoapMessage message) throws URISyntaxException {
		
		String txId = message.getPropertyValue(serviceConfig.getTxId());
		txId = (txId != null) ? txId : "";

		String user = message.getPropertyValue(serviceConfig.getUser());
		String password = message.getPropertyValue(serviceConfig.getPassword());
		user = (user != null) ? user : "";
		password = (password != null) ? password : "";
		String endpointUrl = message.getPropertyValue(serviceConfig.getUrl());
		
		return getClientContext(user, password, endpointUrl);
	}

	private HttpClientContext getClientContext(String user, String password, String endpointUrl) throws URISyntaxException {

		HttpClientContext context = HttpClientContext.create();
        URI endpointUri = new URI(endpointUrl);
        
    	URIBuilder builder = new URIBuilder();
    	builder.setScheme(endpointUri.getScheme());
    	builder.setHost(endpointUri.getHost());
    	builder.setPort(endpointUri.getPort());
    	builder.setPath(endpointUri.getPath());
    	
    	if ( !StringUtils.isEmpty(user) && !StringUtils.isEmpty(password) )
    	{
    		log.debug("Username and password specified, set credentials provider");
    		
    		CredentialsProvider credsProvider = new BasicCredentialsProvider();    	    	
    		credsProvider.setCredentials(
                new AuthScope(endpointUri.getHost(), endpointUri.getPort()),
                new UsernamePasswordCredentials(user, password));    	

    		context.setCredentialsProvider(credsProvider);    	
    		context.setAttribute(serviceConfig.getCredentialsProvider(),credsProvider);    	    		
    	}
    	return context;		
	}
	
	private boolean isStatusError(int statusCode) {
        int scat = statusCode / 100;
        return scat == 3 || scat == 4;
    }
	
}
