package com.smatechnologies.msazure.vm.connector.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.smatechnologies.msazure.vm.arguments.ConnectorArguments;
import com.smatechnologies.msazure.vm.config.ConnectorConfig;
import com.smatechnologies.msazure.vm.enums.Task;
import com.smatechnologies.msazure.vm.interfaces.IAzureVm;
import com.smatechnologies.msazure.vm.interfaces.IConstants;
import com.smatechnologies.msazure.vm.interfaces.IMessages;
import com.smatechnologies.msazure.vm.interfaces.IOpConApi;
import com.smatechnologies.msazure.vm.modules.CreateVMAttributes;
import com.smatechnologies.msazure.vm.modules.InternalIpAddresses;
import com.smatechnologies.msazure.vm.routines.Encryption;
import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.OpconApiProfile;

public class VmConnectorImpl {

	private static final String OpConApiConnectionFailedMsg = "Connection to OpCon API of System {0} failed";

	private final static Logger LOG = LoggerFactory.getLogger(VmConnectorImpl.class);
	private static ConnectorConfig _ConnectorConfig = ConnectorConfig.getInstance();

	private IAzureVm _IAzureVm = new AzureVmImpl();
	private IOpConApi _IOpConApi = new OpConApiImpl();
	private Encryption _Encryption = new Encryption();
	
	private Hashtable<String, String> htblPrivateAddresses = new Hashtable<String, String>();
	
	private OpconApi opconApi = null;
	private OpconApiProfile opconApiProfile = null;
	
	
	public boolean processTaskRequest(
			ConnectorArguments _ConnectorArguments
			) throws Exception {
		
		boolean success = false;
		VirtualMachine virtualMachine = null;
		
		htblPrivateAddresses.clear();
  		LOG.debug("Get credentials");
    	ApplicationTokenCredentials credentials = _IAzureVm.createCredentials();
    	if(credentials == null) {
    		LOG.info("Creating Azure credentials Failed");
        	return false;
    	}
   		LOG.debug("Connect to Azure");
       	Azure azure = _IAzureVm.connect(credentials);
    	if(azure == null) {
    		LOG.error("Connecting to Azure Failed");
        	return false;
    	}
    	
    	Task task = Task.valueOf(_ConnectorArguments.getTask());
		
		switch (task) {
		
			case create:
				if(_ConnectorArguments.getRegion() == null) {
					LOG.error("create function required Region argument");
			    	return false;
				}
				if(_ConnectorArguments.getAttributes() == null) {
					LOG.error("create function required Attributes argument");
			    	return false;
				}

				opconApiProfile = _IOpConApi.getOpConProfile(_ConnectorConfig.getOpconApiAddress(), _ConnectorConfig.isOpconApiUsingTls());
				opconApi = _IOpConApi.getClient(opconApiProfile, true, null, null);
				if(opconApi == null) {
					LOG.error(MessageFormat.format(OpConApiConnectionFailedMsg, _ConnectorConfig.getOpconApiAddress()));
					return false;
				}
		    	
				CreateVMAttributes vmAttributes = _IAzureVm.extractVMAttributes(_ConnectorArguments.getAttributes());
				virtualMachine = _IAzureVm.createVirtualMachine(_ConnectorArguments.getRegion(), _ConnectorArguments.getResourceGroup(), vmAttributes,
						_ConnectorArguments.isCustomImage());
	        	if(virtualMachine == null) {
	        		LOG.info("Create of virtual machine (" + vmAttributes.getName() + ") in region (" + _ConnectorArguments.getRegion()
	    							+ "), resource group ( " + _ConnectorArguments.getResourceGroup() + ") failed");
	        	} else {
					List<InternalIpAddresses> addresses = _IAzureVm.listInterfaces(_ConnectorArguments.getResourceGroup());
					for(InternalIpAddresses address : addresses) {
						htblPrivateAddresses.put(address.getVirtualMachineName(), address.getInternalIpAddress());
					}
					LOG.info("Create of virtual machine (" + vmAttributes.getName() + ") succeeded");
	            	setProperties(htblPrivateAddresses.get(virtualMachine.name().trim()), _ConnectorArguments.getPropertyIpPrivate(), 
	            			virtualMachine.getPrimaryPublicIPAddress().ipAddress(), _ConnectorArguments.getPropertyIpPublic());
	            	success = true;
	        	}
				break;
	
			case deallocate:
				if(_ConnectorArguments.getVirtualMachine() == null) {
					LOG.error("deallocate function required Virtual Machine Name argument");
			    	return false;
				}
				virtualMachine = getVirtualMachine(_ConnectorArguments);
				if(virtualMachine == null) {
					LOG.error("Failed to find virtual machine (" + _ConnectorArguments.getVirtualMachine() + 
							") in Resource Group (" + _ConnectorArguments.getResourceGroup() + ")");
			    	return false;
				}
			    success =  _IAzureVm.deallocateVirtualMachine(virtualMachine); 
	        	if(!success) {
	        		LOG.error("Deallocate of virtual machine (" + virtualMachine.name() + ") failed");
	        	} else {
	            	LOG.info("Deallocate of virtual machine (" + virtualMachine.name() + ") succeeded");
	        	}
				break;

			case delete:
				if(_ConnectorArguments.getVirtualMachine() == null) {
					LOG.error("delete function required Virtual Machine Name argument");
			    	return false;
				}
				virtualMachine = getVirtualMachine(_ConnectorArguments);
				if(virtualMachine == null) {
					LOG.error("Failed to find virtual machine (" + _ConnectorArguments.getVirtualMachine() + 
							") in Resource Group (" + _ConnectorArguments.getResourceGroup() + ")");
			    	return false;
				}
			    success =  _IAzureVm.deleteVirtualMachine(virtualMachine); 
	        	if(!success) {
	        		LOG.error("Delete of virtual machine (" + virtualMachine.name() + ") failed");
	        	} else {
	        		LOG.info("Delete of virtual machine (" + virtualMachine.name() + ") succeeded");
	        	}
				break;
	
			case list:
				htblPrivateAddresses.clear();
				List<InternalIpAddresses> listAddresses = _IAzureVm.listInterfaces(_ConnectorArguments.getResourceGroup());
				for(InternalIpAddresses address : listAddresses) {
					htblPrivateAddresses.put(address.getVirtualMachineName(), address.getInternalIpAddress());
				}
				List<VirtualMachine> virtualMachines =  _IAzureVm.getVirtualMachineList(_ConnectorArguments.getResourceGroup()); 
				LOG.info(IMessages.ReportSeparatorLine);
				LOG.info(IMessages.Line, "List of virtual machines in resource Group (" + _ConnectorArguments.getResourceGroup() + ")");
				LOG.info(IMessages.ReportSeparatorLine);
				LOG.info(StringUtils.rightPad("Virtual Machine Name", 25, IConstants.Characters.SPACE) 
	        			+ StringUtils.rightPad("Region", 20, IConstants.Characters.SPACE) 
	        			+ StringUtils.rightPad("Current State", 30, IConstants.Characters.SPACE) 
	        			+ StringUtils.rightPad("IP Private", 15, IConstants.Characters.SPACE) 
	        			+ StringUtils.rightPad("IP Public", 15, IConstants.Characters.SPACE) 
	        			+ StringUtils.rightPad("OS Type", 10, IConstants.Characters.SPACE));
				LOG.info(IMessages.ReportSeparatorLine);
				if(!virtualMachines.isEmpty()) {
					for(VirtualMachine vm : virtualMachines) {
						String name = StringUtils.rightPad(vm.name(), 25, IConstants.Characters.SPACE);
						String region = StringUtils.rightPad(vm.region().label(), 20, IConstants.Characters.SPACE);
						String state = StringUtils.rightPad(vm.powerState().toString(), 30, IConstants.Characters.SPACE);
						String privateIPAdr = htblPrivateAddresses.get(vm.name().trim());
						String ipPrivate = null;
						if(privateIPAdr != null) {
							ipPrivate = StringUtils.rightPad(privateIPAdr, 15, IConstants.Characters.SPACE);
						} else  {
							ipPrivate = StringUtils.rightPad("N/A", 15, IConstants.Characters.SPACE);
						}
						String ipPublic = null;
						if(vm.getPrimaryPublicIPAddress().ipAddress() != null) {
							ipPublic = StringUtils.rightPad(vm.getPrimaryPublicIPAddress().ipAddress(), 15, IConstants.Characters.SPACE);
						} else {
							ipPublic = StringUtils.rightPad("N/A", 15, IConstants.Characters.SPACE);
						}
						String ostype = StringUtils.rightPad(vm.osType().name(), 10, IConstants.Characters.SPACE);
						LOG.info(name 
			        			+ region 
			        			+ state 
			        			+ ipPrivate
			        			+ ipPublic
			        			+ ostype);
					}
				} else {
					LOG.info("No Virtual machines found in Resource Group (" + _ConnectorArguments.getResourceGroup() + ")");
					
				}
				LOG.info(IMessages.ReportSeparatorLine);
				success = true;
	        	break;

			case poweroff:
				if(_ConnectorArguments.getVirtualMachine() == null) {
					LOG.error("poweroff function required Virtual Machine Name argument");
			    	return false;
				}
				virtualMachine = getVirtualMachine(_ConnectorArguments);
				if(virtualMachine == null) {
					LOG.error("Failed to find virtual machine (" + _ConnectorArguments.getVirtualMachine() + 
							") in Resource Group (" + _ConnectorArguments.getResourceGroup() + ")");
			    	return false;
				}
			    success =  _IAzureVm.powerOffVirtualMachine(virtualMachine); 
	        	if(!success) {
	        		LOG.error("PowerOff of virtual machine (" + virtualMachine.name() + ") failed");
	        	} else {
	            	LOG.info("PowerOff of virtual machine (" + virtualMachine.name() + ") succeeded");
	        	}
				break;
				
			case restart:
				if(_ConnectorArguments.getVirtualMachine() == null) {
					LOG.error("restart function required Virtual Machine Name argument");
			    	return false;
				}
				
				opconApiProfile = _IOpConApi.getOpConProfile(_ConnectorConfig.getOpconApiAddress(), _ConnectorConfig.isOpconApiUsingTls());
				opconApi = _IOpConApi.getClient(opconApiProfile, true, null, null);
				if(opconApi == null) {
					LOG.error(MessageFormat.format(OpConApiConnectionFailedMsg, _ConnectorConfig.getOpconApiAddress()));
					return false;
				}
		    	
				virtualMachine = getVirtualMachine(_ConnectorArguments);
				if(virtualMachine == null) {
					LOG.error("Failed to find virtual machine (" + _ConnectorArguments.getVirtualMachine() + 
							") in Resource Group (" + _ConnectorArguments.getResourceGroup() + ")");
			    	return false;
				}
			    success =  _IAzureVm.restartVirtualMachine(virtualMachine); 
	        	if(!success) {
	        		LOG.error("Restart of virtual machine (" + virtualMachine.name() + ") failed");
	        	} else {
					List<InternalIpAddresses> restartAddresses = _IAzureVm.listInterfaces(_ConnectorArguments.getResourceGroup());
					for(InternalIpAddresses address : restartAddresses) {
						htblPrivateAddresses.put(address.getVirtualMachineName(), address.getInternalIpAddress());
					}
					LOG.info("Restart of virtual machine (" + virtualMachine.name() + ") succeeded");
	            	setProperties(htblPrivateAddresses.get(virtualMachine.name().trim()), _ConnectorArguments.getPropertyIpPrivate(), 
	            			virtualMachine.getPrimaryPublicIPAddress().ipAddress(), _ConnectorArguments.getPropertyIpPublic());
	        	}
				break;
	
			case start:
				if(_ConnectorArguments.getVirtualMachine() == null) {
					LOG.error("start function required Virtual Machine Name argument");
			    	return false;
				}

				opconApiProfile = _IOpConApi.getOpConProfile(_ConnectorConfig.getOpconApiAddress(), _ConnectorConfig.isOpconApiUsingTls());
				opconApi = _IOpConApi.getClient(opconApiProfile, true, null, null);
				if(opconApi == null) {
					LOG.error(MessageFormat.format(OpConApiConnectionFailedMsg, _ConnectorConfig.getOpconApiAddress()));
					return false;
				}
		    	
				virtualMachine = getVirtualMachine(_ConnectorArguments);
				if(virtualMachine == null) {
					LOG.error("Failed to find virtual machine (" + _ConnectorArguments.getVirtualMachine() + 
							") in Resource Group (" + _ConnectorArguments.getResourceGroup() + ")");
			    	return false;
				}
				success =  _IAzureVm.startVirtualMachine(virtualMachine); 
	        	if(!success) {
	        		LOG.error("Start  of virtual machine (" + virtualMachine.name() + ") failed");
	        	} else {
					List<InternalIpAddresses> startAddresses = _IAzureVm.listInterfaces(_ConnectorArguments.getResourceGroup());
					for(InternalIpAddresses address : startAddresses) {
						htblPrivateAddresses.put(address.getVirtualMachineName(), address.getInternalIpAddress());
					}
					LOG.info("Start  of virtual machine (" + virtualMachine.name() + ") succeeded");
	            	setProperties(htblPrivateAddresses.get(virtualMachine.name().trim()), _ConnectorArguments.getPropertyIpPrivate(), 
	            			virtualMachine.getPrimaryPublicIPAddress().ipAddress(), _ConnectorArguments.getPropertyIpPublic());
	        	}
				break;

		}
		return success;
	}

	private VirtualMachine getVirtualMachine(
			ConnectorArguments _AzureConnectorArguments
			) throws Exception {
		
		VirtualMachine virtualMachine = null;
		
		try {
	   		LOG.debug("Get virtual machine (" + _AzureConnectorArguments.getVirtualMachine() + ") in Resource Group (" + _AzureConnectorArguments.getResourceGroup() + ")");
			virtualMachine = _IAzureVm.getVirtualMachine(_AzureConnectorArguments.getResourceGroup(), _AzureConnectorArguments.getVirtualMachine());
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return virtualMachine;
	} 

	private void setProperties(
			String privateIP, 
			String ipPrivatePropertyName, 
			String publicIP, 
			String ipPublicPropertyName
			) throws Exception {
		
		try {
			LOG.debug("Property values privateIP {" + privateIP + "} ipPrivatePropertyName {" + ipPrivatePropertyName +
					"} publicIP {" + publicIP + "} ipPublicPropertyName {" + ipPublicPropertyName + "}");
			if(ipPrivatePropertyName != null) {
				_IOpConApi.updateOpConProperty(opconApi, ipPrivatePropertyName, privateIP);
			}
			if(ipPublicPropertyName != null) {
				_IOpConApi.updateOpConProperty(opconApi, ipPublicPropertyName, publicIP);
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		}
	}	// END : setProperties


	
	
	
	
	
	public int getApplicationToken(
			ConnectorArguments _ConnectorArguments,
			String configFileName
			) throws Exception {
		
		int result = -1;
		String token = null;
		
		// create OpconAPI client connection
		OpconApiProfile profile = _IOpConApi.getOpConProfile(_ConnectorArguments.getAddress(), _ConnectorArguments.isUsingTls());
		opconApi = _IOpConApi.getClient(profile, false, _ConnectorArguments.getUserName(), _ConnectorArguments.getPassword());
		if(opconApi == null) {
			LOG.error(MessageFormat.format(OpConApiConnectionFailedMsg, _ConnectorConfig.getOpconApiAddress()));
			return 1;
		}
		token = _IOpConApi.createApplicationToken(opconApi, _ConnectorArguments.getUserName(), _ConnectorArguments.getPassword());
		if(token !=  null) {
			// encode it
			byte[] encoded =  _Encryption.encode64(token);
			String hexEncoded = _Encryption.encodeHexString(encoded);

			Wini ini = new Wini(new File(configFileName));
			ini.put(IConstants.Configuration.OPCONAPI_HEADER, IConstants.Configuration.OPCONAPI_ADDRESS, _ConnectorArguments.getAddress());
			if(_ConnectorArguments.isUsingTls()) {
				ini.put(IConstants.Configuration.OPCONAPI_HEADER, IConstants.Configuration.OPCONAPI_USING_TLS, "True");
			} else {
				ini.put(IConstants.Configuration.OPCONAPI_HEADER, IConstants.Configuration.OPCONAPI_USING_TLS, "False");
			}
			ini.put(IConstants.Configuration.OPCONAPI_HEADER, IConstants.Configuration.OPCONAPI_TOKEN, hexEncoded);
			ini.store();
			LOG.info("Configuration file updated");
		}
		return result;
	}

}
