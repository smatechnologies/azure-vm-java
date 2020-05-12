package com.smatechnologies.msazure.vm.connector.impl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.client.Client;
import javax.ws.rs.ext.ContextResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smatechnologies.msazure.vm.config.ConnectorConfig;
import com.smatechnologies.msazure.vm.interfaces.IConstants;
import com.smatechnologies.msazure.vm.interfaces.IOpConApi;
import com.smatechnologies.msazure.vm.routines.Util;
import com.smatechnologies.msazure.vm.routines.WSLogger;
import com.smatechnologies.opcon.restapiclient.DefaultClientBuilder;
import com.smatechnologies.opcon.restapiclient.WsErrorException;
import com.smatechnologies.opcon.restapiclient.WsException;
import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.OpconApiProfile;
import com.smatechnologies.opcon.restapiclient.jackson.DefaultObjectMapperProvider;
import com.smatechnologies.opcon.restapiclient.model.PropertyExpression;
import com.smatechnologies.opcon.restapiclient.model.Token;

public class OpConApiImpl implements IOpConApi {
	
	private static final String PropertyExpressionSuccessMsg =  "Expression evaluation ({0}) completed successfully";
	private static final String PropertyExpressionFailedMsg =   "Expression evaluation ({0}) failed : {1} : message {2}";

	private static final String CreateApplicationTokenSuccessMsg =    "Application {0} Token {1} successfully created";
	private static final String CreateApplicationTokenFailedMsg =     "Application Token create failed : {0}";

	private static final String TOKEN_APPLICATION_NAME = "CONNECTORS";

	private static final String UrlFormatTls = "https://{0}/api";
	private static final String UrlFormatNonTls = "http://{0}/api";

	private final static Logger LOG = LoggerFactory.getLogger(OpConApiImpl.class);
	private static ConnectorConfig _ConnectorConfig = ConnectorConfig.getInstance();
	
	private Util _Util = new Util();
	
	public OpconApi getClient(
			OpconApiProfile profile,
			boolean appClient,
			String userName,
			String password
			) throws Exception {
		
		OpconApi opconApi;
		Client client = null;
		ContextResolver<ObjectMapper> ctxObjectMapperProvider; 
		
		try {
			if(_ConnectorConfig.isDebug()) {
		        DefaultClientBuilder clientBuilder = DefaultClientBuilder.get()
		                .setTrustAllCert(true);
		        
		        client = clientBuilder.build();
				DefaultObjectMapperProvider objectMapperProvider = new DefaultObjectMapperProvider();
			    objectMapperProvider.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
	            client.register(new WSLogger(objectMapperProvider));
		        
	            ctxObjectMapperProvider = objectMapperProvider;
			} else {
		        DefaultClientBuilder clientBuilder = DefaultClientBuilder.get()
		                .setTrustAllCert(true);
		        
		        client = clientBuilder.build();
		        DefaultObjectMapperProvider objectMapperProvider = new DefaultObjectMapperProvider();
			    objectMapperProvider.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
	            ctxObjectMapperProvider = objectMapperProvider;
			}
            opconApi = new OpconApi(client, profile, new OpconApi.OpconApiListener() {

                @Override
                public void onFailed(WsException e) {
                    if (e.getResponse() == null) {
                        LOG.error("[OpconApi] A web service call has failed.", e);
                    } else if (e instanceof WsErrorException) {
                        LOG.warn("[OpconApi] A web service call return API Error: {}", e.getResponse().readEntity(String.class));
                    } else {
                        LOG.error("[OpconApi] A web service call has failed. Response: Header={} Body={}", e.getResponse().getHeaders(), e.getResponse().readEntity(String.class), e);
                    }
                }
            }, ctxObjectMapperProvider);
            if(appClient) {
            	opconApi.login(_ConnectorConfig.getOpconApiToken());
            } else {
            	opconApi.login(userName, password);
            }
			
		} catch (KeyManagementException | NoSuchAlgorithmException | WsException e) {
		    throw new Exception(e);
		}
		return opconApi;
	}	// END : getClient

	public Boolean updateOpConProperty(
			OpconApi opconApi,
			String propertyName, 
			String propertyValue
			) throws Exception {
		
		boolean success = false;
		PropertyExpression propertyExpression = new PropertyExpression();
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append("[[");
		sbuilder.append(propertyName);
		sbuilder.append("]]=\"");
		sbuilder.append(propertyValue);
		sbuilder.append("\"");
		propertyExpression.setExpression(sbuilder.toString());
		PropertyExpression resultExpression = opconApi.propertyExpressions().post(propertyExpression);
		if(resultExpression.getStatus().equalsIgnoreCase(IConstants.OpConApiResults.RESULT_SUCCESS)) {
			LOG.info(MessageFormat.format(PropertyExpressionSuccessMsg,  sbuilder.toString()));
			success = true;
		} else {
			LOG.error(MessageFormat.format(PropertyExpressionFailedMsg, sbuilder.toString(), resultExpression.getResult(), resultExpression.getMessage()));
			success = false;
		}
		return success;
	}	// END : updateOpConProperty
	
	public String createApplicationToken(
			OpconApi opconApi,
			String user,
			String password
			) throws Exception {
		
		String tokenId = null;
		
		Token token = opconApi.tokens().postApp(user, password, TOKEN_APPLICATION_NAME);
		if(token.getId() != null) {
			LOG.info(MessageFormat.format(CreateApplicationTokenSuccessMsg, TOKEN_APPLICATION_NAME, token.getId()));
			tokenId = token.getId();
		} else {
			LOG.error(MessageFormat.format(CreateApplicationTokenFailedMsg, "error"));
		}
		return tokenId;
	}

	public OpconApiProfile getOpConProfile(
			String address,
			boolean usingTls
			) throws Exception {
	
		OpconApiProfile profile = null;
		String url = null;
		
		if(usingTls) {
			url = MessageFormat.format(UrlFormatTls, address);
		} else {
			url = MessageFormat.format(UrlFormatNonTls, address);
		}
		profile = new OpconApiProfile(url);
		return profile;
	}
	
}
