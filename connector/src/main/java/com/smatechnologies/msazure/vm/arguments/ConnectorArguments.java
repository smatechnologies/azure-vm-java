package com.smatechnologies.msazure.vm.arguments;

import org.kohsuke.args4j.Option;

public class ConnectorArguments {

	private static final String TaskDescriptionMsg = "(Required) The task to execute";
	private static final String ResourceGroupNameDescriptionMsg = "(Required) Resource Group Name";

	private static final String AttributesDescriptionMsg = "(optional) Attributes used to create a virtual machine";
	private static final String RegionDescriptionMsg = "(Optional) Region Name";
	private static final String VirtualMachineNameDescriptionMsg = "(Optional) Virtual Machine name";
	private static final String PropertiesInstanceIpPrivateMsg = "(Optional) Insert the private IPADR into the defined property";
	private static final String PropertiesInstanceIpPublicMsg = "(Optional) Insert the public IPADR into the defined property";
	private static final String CustomImageMsg = "(Optional) Used for create operation to indicate custom image to be created";
	
	private static final String SetupMsg = "(Optional) Used for OpCon Rest-Api connection setup";
	
	// general
	@Option(name="-t",usage= TaskDescriptionMsg)
	private String task = null;

	@Option(name="-rg",usage=ResourceGroupNameDescriptionMsg)
	private String resourceGroup = null;

	@Option(name="-at",usage=AttributesDescriptionMsg)
	private String attributes = null;

	@Option(name="-r",usage=RegionDescriptionMsg)
	private String region = null;

	@Option(name="-vm",usage=VirtualMachineNameDescriptionMsg)
	private String virtualMachine = null;
	
	@Option(name="-ippvt",usage=PropertiesInstanceIpPrivateMsg)
	private String propertyIpPrivate = null;

	@Option(name="-ippub",usage=PropertiesInstanceIpPublicMsg)
	private String propertyIpPublic = null;

	@Option(name="-ci",usage=CustomImageMsg)
	private boolean customImage = false;
	
	@Option(name="--setup", usage = SetupMsg)
	private boolean setup = false;
	
	@Option(name="-username", usage = SetupMsg)
    private String userName = null;

	@Option(name="-password", usage = SetupMsg)
    private String password = null;

	@Option(name="-address", usage = SetupMsg)
    private String address = null;
	
	@Option(name="-tls", usage = SetupMsg)
    private boolean usingTls = false;
	
	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getResourceGroup() {
		return resourceGroup;
	}

	public void setResourceGroup(String resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getVirtualMachine() {
		return virtualMachine;
	}

	public void setVirtualMachine(String virtualMachine) {
		this.virtualMachine = virtualMachine;
	}

	public String getPropertyIpPrivate() {
		return propertyIpPrivate;
	}

	public void setPropertyIpPrivate(String propertyIpPrivate) {
		this.propertyIpPrivate = propertyIpPrivate;
	}
	
	public String getPropertyIpPublic() {
		return propertyIpPublic;
	}

	public void setPropertyIpPublic(String propertyIpPublic) {
		this.propertyIpPublic = propertyIpPublic;
	}

	public boolean isCustomImage() {
		return customImage;
	}

	public void setCustomImage(boolean customImage) {
		this.customImage = customImage;
	}

	public boolean isSetup() {
		return setup;
	}

	public void setSetup(boolean setup) {
		this.setup = setup;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isUsingTls() {
		return usingTls;
	}

	public void setUsingTls(boolean usingTls) {
		this.usingTls = usingTls;
	}
	

}
