package com.smatechnologies.msazure.vm.config;

public class ConnectorConfig {

	private static ConnectorConfig _ConnectorConfig = null;

	private String name = null;
	private String tenantId = null;
	private String subscriptionId = null;
	private String clientId = null;
	private String key = null;
	private String opconApiAddress = null;
	private boolean opconApiUsingTls = true;
	private String opconApiToken = null;
	private boolean debug = false;
	
	protected ConnectorConfig() {
	}

	public static ConnectorConfig getInstance() {
		if(_ConnectorConfig == null) {
			_ConnectorConfig = new ConnectorConfig();
		}
		return _ConnectorConfig;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOpconApiAddress() {
		return opconApiAddress;
	}

	public void setOpconApiAddress(String opconApiAddress) {
		this.opconApiAddress = opconApiAddress;
	}

	public boolean isOpconApiUsingTls() {
		return opconApiUsingTls;
	}

	public void setOpconApiUsingTls(boolean opconApiUsingTls) {
		this.opconApiUsingTls = opconApiUsingTls;
	}

	public String getOpconApiToken() {
		return opconApiToken;
	}

	public void setOpconApiToken(String opconApiToken) {
		this.opconApiToken = opconApiToken;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
}
