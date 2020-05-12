package com.smatechnologies.msazure.vm.interfaces;

public interface IMessages {

	public static final String SeparatorLine =              "----------------------------------------------------------------------------";
	public static final String ExceptionDetailsLine = "Exception Details : {0}";
	
	public static final String ReportSeparatorLine = "------------------------------------------------------------------------------------------------------------------";
	public static final String Line = "{0} : {1}";						

	public static final String InvalidRegionMsg =  "Invalid Region Supplied  : {0}";
	public static final String ImageNotFoundMsg =  "Virtual Machine Image {0} not found in region {1}";

	public static final String NetworkNotFoundMsg = "Network {0} not found in region {1} location {2}";
	public static final String InvalidNetworkMsg = "Network {0} not valid, format is network/subnet";

	public static final String VirtualMachineMissingResourceGroupMsg = "Required argument Resource Group (-vrg) missing";

}
