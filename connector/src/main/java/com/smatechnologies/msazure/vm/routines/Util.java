package com.smatechnologies.msazure.vm.routines;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.msazure.vm.config.ConnectorConfig;
import com.smatechnologies.msazure.vm.interfaces.IConstants;
import com.smatechnologies.msazure.vm.interfaces.IMessages;

public class Util {

	private final static Logger LOG = LoggerFactory.getLogger(Util.class);
	private Encryption _Encryption = new Encryption();

	public ConnectorConfig getConfigInformation(Preferences iniPrefs, ConnectorConfig config) throws Exception {
		
		try {
			// general
			config.setName(iniPrefs.node(IConstants.Configuration.CONNECTOR_HEADER).get(IConstants.Configuration.CONNECTOR_NAME, null));
			String debug = iniPrefs.node(IConstants.Configuration.CONNECTOR_HEADER).get(IConstants.Configuration.CONNECTOR_DEBUG, null);
			if(debug.equalsIgnoreCase(IConstants.General.DEBUG_ON)) {
				config.setDebug(true);
			}
			// MSAZURE
			String tenant = iniPrefs.node(IConstants.Configuration.MSAZURE_HEADER).get(IConstants.Configuration.MSAZURE_TENANT, null);
			if(tenant != null) {
				config.setTenantId(decryptEncodedValue(tenant));
			}
			String subscription = iniPrefs.node(IConstants.Configuration.MSAZURE_HEADER).get(IConstants.Configuration.MSAZURE_SUBSCRIPTION, null);
			if(subscription != null) {
				config.setSubscriptionId(decryptEncodedValue(subscription));
			}
			String client = iniPrefs.node(IConstants.Configuration.MSAZURE_HEADER).get(IConstants.Configuration.MSAZURE_CLIENT, null);
			if(client != null) {
				config.setClientId(decryptEncodedValue(client));
			}			
			String key = iniPrefs.node(IConstants.Configuration.MSAZURE_HEADER).get(IConstants.Configuration.MSAZURE_KEY, null);			
			if(key != null) {
				config.setKey(decryptEncodedValue(key));
			}
			// OpCon API
			config.setOpconApiAddress(iniPrefs.node(IConstants.Configuration.OPCONAPI_HEADER).get(IConstants.Configuration.OPCONAPI_ADDRESS, null));
			config.setOpconApiUsingTls(iniPrefs.node(IConstants.Configuration.OPCONAPI_HEADER).getBoolean(IConstants.Configuration.OPCONAPI_USING_TLS, true));
			String token = iniPrefs.node(IConstants.Configuration.OPCONAPI_HEADER).get(IConstants.Configuration.OPCONAPI_TOKEN, null);			
			if(token != null) {
				config.setOpconApiToken(decryptEncodedValue(token));
			}
			LOG.debug(IMessages.SeparatorLine);
			LOG.debug("Configuration information");
			LOG.debug("General Name                 {" + config.getName() + "}");
			LOG.debug("General Debug                {" + config.isDebug() + "}");
			LOG.debug("MSAzure TenantID             {" + config.getTenantId() + "}");
			LOG.debug("MSAzure SubscriptionID       {" + config.getSubscriptionId() + "}");
			LOG.debug("MSAzure ClientID             {" + config.getClientId() + "}");
			LOG.debug("MSAzure Key                  {" + config.getKey() + "}");
			LOG.debug("OpConAPI address             {" + config.getOpconApiAddress() + "}");
			LOG.debug("OpConAPI using TLS           {" + config.isOpconApiUsingTls() + "}");
			LOG.debug("OpConAPI token               {" + config.getOpconApiToken() + "}");
			LOG.debug(IMessages.SeparatorLine);
		} catch (Exception ex) {
			throw new Exception (ex);
		}
		return config;
	}	// END : getAgentConfigInformation

	public String getExceptionDetails(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionDetails = sw.toString();
		return exceptionDetails;
	}

	public String[] tokenizeString(String parameters, boolean keepQuote, String delimiter) {
		final char QUOTE = IConstants.Characters.QUOTE.toCharArray()[0];
		final char BACK_SLASH = IConstants.Characters.BACK_SLASH.toCharArray()[0];
		char prevChar = 0;
		char currChar = 0;
		StringBuffer sb = new StringBuffer(parameters.length());

		if (!keepQuote) {
			for (int i = 0; i < parameters.length(); i++) {
				if (i > 0) {
					prevChar = parameters.charAt(i - 1);
				}
				currChar = parameters.charAt(i);

				if (currChar != QUOTE || (currChar == QUOTE && prevChar == BACK_SLASH)) {
					sb.append(parameters.charAt(i));
				}
			}

			if (sb.length() > 0) {
				parameters = sb.toString();
			}
		}
		return parameters.split(delimiter);
	}	// END : tokenizeString

	public String decryptEncodedValue(
			String password
			) throws Exception {
		
		String decryptedPassword = null;
		
		try {
			if(password != null) {
				if(password.length() == 0) {
					// empty password
					LOG.error("Missing encrypted value");
					System.exit(401);
				} else {
	 				byte[] bencrypted = _Encryption.decodeHexString(password);
	 				decryptedPassword = _Encryption.decode64(bencrypted);
				}
			} else {
				LOG.error("Missing encrypted value");
				System.exit(99);
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return decryptedPassword;
	}	// END : decryptEncodedPassword

}
