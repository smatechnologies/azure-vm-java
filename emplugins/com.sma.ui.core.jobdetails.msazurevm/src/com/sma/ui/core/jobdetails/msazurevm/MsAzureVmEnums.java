package com.sma.ui.core.jobdetails.msazurevm;

public class MsAzureVmEnums {

	public static enum Task {
		create,
		deallocate,
		delete,
		list,
		poweroff,
		restart,
		start,
		unknown
	};

	public static enum VmTypes {
		windows,
		linux,
		unknown
	};

}
