package com.smatechnologies.msazure.vm.arguments;

import org.kohsuke.args4j.Option;

public class EncryptValueArguments {

	public static final String ValueArgumentDescriptionMsg = "Text string to encrypt";

	@Option(name="-v", required=true, usage= ValueArgumentDescriptionMsg)
	private String value = null;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
