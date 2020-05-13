package com.smatechnologies.msazure.vm.connector;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.msazure.vm.arguments.ConnectorArguments;
import com.smatechnologies.msazure.vm.config.ConnectorConfig;
import com.smatechnologies.msazure.vm.connector.impl.VmConnectorImpl;
import com.smatechnologies.msazure.vm.enums.Task;
import com.smatechnologies.msazure.vm.interfaces.IConstants;
import com.smatechnologies.msazure.vm.routines.Util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.util.StatusPrinter;

public class VmConnector {

	private static final String ResourceGroupMissingMsg = "Required Argument Resource Group missing";

	private static final String SeperatorLineMsg =                              "-------------------------------------------------------------------------------------------------------";
	private static final String ClientVersionDisplayMsg =                       "Azure Connector                      : Version {0}";
	private static final String ResourceGroupMsg =                              "-rg    (Resource Group name)         : {0}";
	private static final String TaskMsg =                                       "-t     (task)                        : {0}";
	private static final String VirtualMachineNameMsg =                         "-vm    (Virtual Machine Name)        : {0}";
	private static final String IPPubPropertyNameMsg =                          "-ippub (OpCon Property Name)         : {0}";
	private static final String IPPvtPropertyNameMsg =                          "-ippvt (OpCon Property Name)         : {0}";
	private static final String RegionMsg =                                     "-r     (Region name)                 : {0}";
	private static final String AttributesMsg =                                 "-at    (Attributes)                  : {0}";
	private static final String CustomImageMsg =                                "-ci    (custom image)                : {0}";
	
	private final static Logger LOG = LoggerFactory.getLogger(VmConnector.class);
	private static ConnectorConfig _ConnectorConfig = ConnectorConfig.getInstance();
	private static Util _Util = new Util();
	
	public static void main(String[] args) {
		VmConnector _VmConnector = new VmConnector();
		VmConnectorImpl _VmConnectorImpl = new VmConnectorImpl();
		ConnectorArguments _ConnectorArguments = new ConnectorArguments();
	    CmdLineParser cmdLineParser = new CmdLineParser(_ConnectorArguments);
		String workingDirectory = null;
		String configFileName = null;
		boolean result = false;
		
		try {
			// get arguments
			cmdLineParser.parseArgument(args);
			// go get information from config file
			workingDirectory = System.getProperty(IConstants.General.SYSTEM_USER_DIRECTORY);
			// go get information from config file
			configFileName = workingDirectory + File.separator + IConstants.General.CONFIG_FILE_NAME;
			if(_ConnectorArguments.isSetup()) {
				_VmConnectorImpl.getApplicationToken(_ConnectorArguments, configFileName);
				System.exit(0);
			}
        	Preferences iniPrefs = new IniPreferences(new Ini(new File(configFileName)));
        	// insert values into  configuration
        	_ConnectorConfig = _Util.getConfigInformation(iniPrefs, _ConnectorConfig);
			_VmConnector.setLogger(_ConnectorConfig.isOpconApiUsingTls());
			if(_ConnectorArguments.getResourceGroup() == null) {
				LOG.error(ResourceGroupMissingMsg);
				System.exit(1);
			}
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(ClientVersionDisplayMsg,IConstants.General.SOFTWARE_VERSION));
			LOG.info(SeperatorLineMsg);
           	LOG.info(MessageFormat.format(ResourceGroupMsg,_ConnectorArguments.getResourceGroup()));
        	LOG.info(MessageFormat.format(TaskMsg,_ConnectorArguments.getTask()));
        	if(!_ConnectorArguments.getTask().equals(Task.list.name())) {
               	LOG.info(MessageFormat.format(VirtualMachineNameMsg,_ConnectorArguments.getVirtualMachine()));
     			if(_ConnectorArguments.getPropertyIpPrivate() != null) {
    	        	LOG.info(MessageFormat.format(IPPvtPropertyNameMsg,_ConnectorArguments.getPropertyIpPrivate()));
    			}
     			if(_ConnectorArguments.getPropertyIpPublic() != null) {
    	        	LOG.info(MessageFormat.format(IPPubPropertyNameMsg,_ConnectorArguments.getPropertyIpPublic()));
    			}
     			if(!_ConnectorArguments.getTask().equals(Task.create.name())) {
         			if(_ConnectorArguments.getRegion() != null) {
        	        	LOG.info(MessageFormat.format(RegionMsg,_ConnectorArguments.getRegion()));
        			}
         			if(_ConnectorArguments.getAttributes() != null) {
        	        	LOG.info(MessageFormat.format(AttributesMsg,_VmConnector.hidePassword(_ConnectorArguments.getAttributes())));
        			}
    	        	LOG.info(MessageFormat.format(CustomImageMsg,_ConnectorArguments.isCustomImage()));
     			}
        	}
         	result = _VmConnectorImpl.processTaskRequest(_ConnectorArguments);
			if(!result) {
				System.exit(1);
			}
		} catch (CmdLineException e) {
			LOG.error(e.getMessage());
			System.err.println(e.getMessage());
			cmdLineParser.printUsage(System.err);
			System.exit(1);
		} catch (Exception ex) {
			LOG.error( _Util.getExceptionDetails(ex));
			System.exit(1);
		}
		System.exit(0);
	}

	private void setLogger(
			boolean isDebug
			) throws Exception {

        //Debug mode
        if(isDebug) {
            System.setProperty(IConstants.LogBackConstants.LEVEL_STDOUT_KEY, IConstants.LogBackConstants.LEVEL_DEBUG_VALUE);
            System.setProperty(IConstants.LogBackConstants.LEVEL_FILE_KEY, IConstants.LogBackConstants.LEVEL_DEBUG_VALUE);
            System.setProperty(IConstants.LogBackConstants.STDOUT_PATTERN_KEY, IConstants.LogBackConstants.STDOUT_PATTERN_DEBUG_VALUE);
        } else {
            System.clearProperty(IConstants.LogBackConstants.LEVEL_STDOUT_KEY);
            System.clearProperty(IConstants.LogBackConstants.LEVEL_FILE_KEY);
            System.clearProperty(IConstants.LogBackConstants.STDOUT_PATTERN_KEY);
        }

        //Restart logback
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        ContextInitializer contextInitializer = new ContextInitializer(loggerContext);
        URL url = contextInitializer.findURLOfDefaultConfigurationFile(true);

        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        loggerContext.reset();
        joranConfigurator.doConfigure(url);

        StatusPrinter.printIfErrorsOccured(loggerContext);
		
	}

	private String hidePassword(
			String attributes
			) throws Exception {
		
		String[] values = attributes.split(IConstants.Characters.COMMA);
		StringBuilder builder = new StringBuilder();
		builder.append(values[0]);
		builder.append(IConstants.Characters.COMMA);
		builder.append(values[1]);
		builder.append(IConstants.Characters.COMMA);
		builder.append(values[2]);
		builder.append(IConstants.Characters.COMMA);
		builder.append(values[3]);
		builder.append(IConstants.Characters.COMMA);
		builder.append(values[4]);
		builder.append(IConstants.Characters.COMMA);
		builder.append(values[5]);
		builder.append(IConstants.Characters.COMMA);
		builder.append("HIDDEN");
		builder.append(IConstants.Characters.COMMA);
		builder.append(values[7]);
		builder.append(IConstants.Characters.COMMA);
		builder.append(values[8]);
		return builder.toString();
	}
	
}
