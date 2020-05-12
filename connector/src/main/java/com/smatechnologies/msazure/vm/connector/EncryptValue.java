package com.smatechnologies.msazure.vm.connector;

import java.text.MessageFormat;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.msazure.vm.arguments.EncryptValueArguments;
import com.smatechnologies.msazure.vm.interfaces.IConstants;
import com.smatechnologies.msazure.vm.routines.Encryption;
import com.smatechnologies.msazure.vm.routines.Util;

public class EncryptValue {

	private static final String SeperatorLineMsg =                "---------------------------------------------------------------------";
	
	private static final String ProgramNameAndVersionMsg =        "EncryptValue          : Version {0}";
	private static final String DisplayValueArgumentMsg =         "-v  (value)           : {0}";
	private static final String EncryptedMsg =                    "ev : {0}";
	
	private final static Logger LOG = LoggerFactory.getLogger(EncryptValue.class);
	
	public static void main(String[] args) {

		EncryptValueArguments _EncryptValueArguments = new EncryptValueArguments();
		Encryption _Encryption = new Encryption();
	    CmdLineParser cmdLineParser = new CmdLineParser(_EncryptValueArguments);
		Util _Util = new Util();
		
		try {
			// get arguments
			cmdLineParser.parseArgument(args);
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(ProgramNameAndVersionMsg, IConstants.General.SOFTWARE_VERSION));
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(DisplayValueArgumentMsg, _EncryptValueArguments.getValue()));
			LOG.info(SeperatorLineMsg);
			byte[] encoded =  _Encryption.encode64(_EncryptValueArguments.getValue());
			String hexEncoded = _Encryption.encodeHexString(encoded);
			LOG.info(MessageFormat.format(EncryptedMsg, hexEncoded));
			LOG.info(SeperatorLineMsg);
		} catch (CmdLineException e) {
			LOG.error(e.getMessage());
			System.err.println(e.getMessage());
			cmdLineParser.printUsage(System.err);
			System.exit(1);
		} catch (Exception ex) {
			LOG.error(_Util.getExceptionDetails(ex));
			System.exit(1);
		}
		System.exit(0);
	} // END : main

}
