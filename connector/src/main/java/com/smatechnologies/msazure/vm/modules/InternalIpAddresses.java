package com.smatechnologies.msazure.vm.modules;

public class InternalIpAddresses {
	
	private String virtualMachineName = null;
	private String internalIpAddress = null;
	
	public String getVirtualMachineName() {
		return virtualMachineName;
	}
	
	public void setVirtualMachineName(String virtualMachineName) {
		this.virtualMachineName = virtualMachineName;
	}
	
	public String getInternalIpAddress() {
		return internalIpAddress;
	}
	
	public void setInternalIpAddress(String internalIpAddress) {
		this.internalIpAddress = internalIpAddress;
	}

}
