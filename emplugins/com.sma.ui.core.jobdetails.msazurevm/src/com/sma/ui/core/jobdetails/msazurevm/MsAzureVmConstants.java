package com.sma.ui.core.jobdetails.msazurevm;

import com.sma.ui.core.widgets.base.ComboItem;

public interface MsAzureVmConstants {
	
	/**
	 * Constants shared by all Windows Jobs
	 */
	int COMMAND_LINE_LIMIT = 4000;

	String INVALID_COMMAND_LINE = "Invalid command line, please go back to the WINDOWS details to fix the command line.";
	String TOO_LONG_COMMAND_LINE = "Invalid command line, total length exceeds " + COMMAND_LINE_LIMIT + " characters.";
	String TOO_LONG_URL = "URL total length exceeds 2000 characters.";
	String TEXTBOX_CANNOT_BE_EMPTY = "{0} cannot be empty.";
	String BOTH_VALUES_CANNOT_BE_EMPTY = "Both {0} and {1} values cannot be empty.";
	String BOTH_VALUES_CANNOT_BE_DEFINED = "Both {0} and {1} values cannot be defined.";
	String VALUE_LESS_THAN_ONE_NOT_ALLOWED = "{0} a value less than 1 is not allowed.";
	String PARSE_JOB_COMMAND_ERROR = "Cannot parse the command line, this does not look like a valid {0}.";

	String WINDOWS_SERVERS_PROPERTY_NAME = "AZURE_WINDOWS_SERVERS";
	String LINUX_SERVERS_PROPERTY_NAME = "AZURE_LINUX_SERVERS";
	String SERVER_SIZES_PROPERTY_NAME = "AZURE_SERVER_SIZES";

	String LOCATION_PATH_TOKEN = "[[AzureVmPath]]";
	String LOCATION_PATH_NAME = "Connector Location";
	String LOCATION_PATH_TOKEN_NAME_TOOLTIP = "The name of a global property that contains the installed location of the Connector";

	String FAILURE_CRITERIA_TAB_NAME = "Failure Criteria";

	public interface ComboItemDefinitions {
		ComboItem [] TASKS = new ComboItem[] { new ComboItem ("Create Virtual Machine", MsAzureVmEnums.Task.create), 
				new ComboItem ("Delete Virtual Machine", MsAzureVmEnums.Task.delete), 
				new ComboItem ("List Virtual Machines", MsAzureVmEnums.Task.list), 
				new ComboItem ("PowerOff Virtual Machine", MsAzureVmEnums.Task.poweroff), 
				new ComboItem ("Deallocate Virtual Machine", MsAzureVmEnums.Task.deallocate), 
				new ComboItem ("Restart Machine", MsAzureVmEnums.Task.restart), 
				new ComboItem ("Start Virtual Machine", MsAzureVmEnums.Task.start), 
				new ComboItem ("Unknown", MsAzureVmEnums.Task.unknown)};
	
		ComboItem [] VM_TYPES = new ComboItem[] { new ComboItem ("Windows", MsAzureVmEnums.VmTypes.windows), 
				new ComboItem ("Linux", MsAzureVmEnums.VmTypes.linux),
				new ComboItem ("Unknown", MsAzureVmEnums.VmTypes.unknown)};
	
		String[] REGIONS = new String[] {"Australia Central","Australia Central 2","Australia East","Australia Southeast",
											   "Brasil South","Canada Central","Canada East","Central India","Central US","China East",
											   "China East 2","China North","China North 2","East Asia","East US","East US 2",
											   "France Central","France South","Germany Central","Germany North","Japan East",
											   "Japan West","Korea Central","Korea South","North Central US","North Europe",
											   "South Central US","South India","SouthEast Asia","UK South","UK West",
											   "West Central US","West Europe","West India","West US 2","West US" };
		String[] LINUX_SERVERS = new String[] {"Canonical_UbuntuServer_16.04-LTS","RedHat_RHEL_7.5","SUSE_SLES_15"};
	
		String[] WINDOWS_SERVERS = new String[] {"MicrosoftWindowsServer_WindowsServer_2016-Datacenter","MicrosoftWindowsServer_WindowsServer_2012-R2-Datacenter",
				"MicrosoftSQLServer_SQL2016SP2-WS2016_Standard" };
	
		String[] SERVER_SIZES = new String[] {"Standard_DS1_v2","Standard_DS2_v2","Standard_DS3_v2",
		"Standard_DS4_v2","Standard_DS5_v2" };
	}
	
	public interface Arguments { 
		String TASK_ARGUMENT = "t";
		String ATTRIBUTES_ARGUMENT = "at";
		String REGION_ARGUMENT = "r";
		String RESOURCE_GROUP_ARGUMENT = "rg";
		String VIRTUAL_MACHINE_ARGUMENT = "vm";
		String PRIVATE_IP_ADR_PROPERTY_ARGUMENT = "ippvt";
		String PUBLIC_IP_ADR_PROPERTY_ARGUMENT = "ippub";
		String CUSTOM_IMAGE_ARGUMENT = "ci";
	}
}

