package com.smatechnologies.msazure.vm.connector.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineCustomImage;
import com.microsoft.azure.management.compute.VirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachineOffer;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.compute.VirtualMachineSku;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Creatable;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.rest.LogLevel;

import com.smatechnologies.msazure.vm.config.ConnectorConfig;
import com.smatechnologies.msazure.vm.interfaces.IAzureVm;
import com.smatechnologies.msazure.vm.interfaces.IConstants;
import com.smatechnologies.msazure.vm.interfaces.IMessages;
import com.smatechnologies.msazure.vm.modules.CreateVMAttributes;
import com.smatechnologies.msazure.vm.modules.InternalIpAddresses;
import com.smatechnologies.msazure.vm.routines.Util;



public class AzureVmImpl implements IAzureVm {

	private static final String IPADR_PREFIX = "IPADR_";
	private static final String RANDOM_DISK_PREFIX = "dsk";
	@SuppressWarnings("unused")
	private static final String DEFAULT_PRIMARY_NETWORK_INDICATOR = "10.0.0.0/28";
	private static final String VM_INDICATOR_WINDOWS = "Windows";
	
	private final static Logger LOG = LoggerFactory.getLogger(AzureVmImpl.class);
	private static ConnectorConfig _ConnectorConfig = ConnectorConfig.getInstance();
	
	private Util _Util = new Util();
	private Azure _Azure = null;
	

	public ApplicationTokenCredentials createCredentials(
			) throws Exception { 
	
		ApplicationTokenCredentials credentials = null;
		
		try {
			LOG.debug("Creating credentials");
           credentials = new ApplicationTokenCredentials(_ConnectorConfig.getClientId(), _ConnectorConfig.getTenantId(), 
            		_ConnectorConfig.getKey(), AzureEnvironment.AZURE);
		} catch(Exception ex) {
			throw new Exception(ex);
		}
		return credentials;
	}
	
	public Azure connect(
			ApplicationTokenCredentials credentials
			) throws Exception { 
		
		try {
			LOG.debug("Connecting");
		    _Azure = Azure.configure()
		            .withLogLevel(LogLevel.NONE)
		            .authenticate(credentials)
		            .withSubscription(_ConnectorConfig.getSubscriptionId());
		} catch(Exception ex) {
			throw new Exception(ex);
		}
		return _Azure;
	}

	public VirtualMachine getVirtualMachine(
			String resourceGroup,
			String name
			) throws Exception {
		
		VirtualMachine virtualMachine = null;
		
		try {
			LOG.debug("Checking Resource Group (" + resourceGroup + ") for Virtual Machine (" + name + ")");
	        PagedList<VirtualMachine> virtualMachines = _Azure
	                .virtualMachines()
	                .listByResourceGroup(resourceGroup);
	        for (VirtualMachine machine : virtualMachines) {
	        	if(machine.name().equalsIgnoreCase(name)) {
	        		virtualMachine = machine; 
	        		break;
	        	}
	        }
		} catch(Exception ex) {
			throw new Exception(ex);
		}
		return virtualMachine;
	}
	
	public List<VirtualMachine> getVirtualMachineList(
			String resourceGroup
			) throws Exception {
		
		List<VirtualMachine> virtualMachineList = new ArrayList<VirtualMachine>();
		
		try {
	        PagedList<VirtualMachine> virtualMachines = _Azure
	                .virtualMachines()
	                .listByResourceGroup(resourceGroup);
	        for (VirtualMachine machine : virtualMachines) {
	        	virtualMachineList.add(machine);
	        }
		} catch(Exception ex) {
			throw new Exception(ex);
		}
		return virtualMachineList;
	}

    public boolean powerOffVirtualMachine(
    		VirtualMachine virtualMachine
			) throws Exception {
    	
    	boolean success = false;
    	
    	try {
   			LOG.debug("Powering off Virtual Machine (" + virtualMachine.name() + ")");
     		virtualMachine.powerOff();
    		success = true;
		} catch(Exception ex) {
			throw new Exception(ex);
		}
        return success;
    }

    public boolean startVirtualMachine(
    		VirtualMachine virtualMachine
			) throws Exception {
    	
    	boolean success = false;
    	
    	try {
   			LOG.debug("Starting Virtual Machine (" + virtualMachine.name() + ")");
    		virtualMachine.start();
    		success = true;
		} catch(Exception ex) {
			throw new Exception(ex);
		}
        return success;
    }

    public boolean restartVirtualMachine(
    		VirtualMachine virtualMachine
			) throws Exception {
    	
    	boolean success = false;
    	
    	try {
   			LOG.debug("Restarting Virtual Machine (" + virtualMachine.name() + ")");
    		virtualMachine.restart();
    		success = true;
		} catch(Exception ex) {
			throw new Exception(ex);
		}
        return success;
    }

    public boolean deleteVirtualMachine(
    		VirtualMachine virtualMachine
			) throws Exception {
    	
    	boolean success = false;
    	
    	try {
   			LOG.debug("Deleting Virtual Machine (" + virtualMachine.name() + ")");
    		_Azure.virtualMachines().deleteById(virtualMachine.id());
    		success = true;
		} catch(Exception ex) {
			throw new Exception(ex);
		}
        return success;
    }

    public Disk createDataDisk(
    		String region,
    		String resourceGroup,
    		Integer diskSize
			) throws Exception {

    	Disk dataDisk = null;
    	
    	try {
   			LOG.debug("Creating " + String.valueOf(diskSize) + " disk");
    		dataDisk = _Azure.disks().define(SdkContext.randomResourceName(RANDOM_DISK_PREFIX, 30))
    	            .withRegion(region)
    	            .withNewResourceGroup(resourceGroup)
    	            .withData()
    	            .withSizeInGB(diskSize)
    	            .create();
		} catch(Exception ex) {
			throw new Exception(ex);
		}
        return dataDisk;
    }
    public VirtualMachine createVirtualMachine(
    		String region,
    		String resourceGroup,
    		CreateVMAttributes vmAttributes,
    		boolean customImage
			) throws Exception {

    	VirtualMachine vm = null;
    	Region selectedRegion = null;
    	
    	try {
     		selectedRegion = Region.findByLabelOrName(convertRegionName(region));
    		if(selectedRegion == null) {
    			LOG.error(MessageFormat.format(IMessages.InvalidRegionMsg, region));
    			return vm;
    		}
    		if(!customImage) {
        		VirtualMachineImage image = getAvailableImage(vmAttributes.getImageName(), selectedRegion);
        		if(image == null) {
        			LOG.error(MessageFormat.format(IMessages.ImageNotFoundMsg, vmAttributes.getImageName(),region));
        			return vm;
        		}
       			LOG.debug("Creating virtual machine (" + vmAttributes.getName() + ") image (" + vmAttributes.getImageName() + ") in Region (" + selectedRegion.name() + ")");
        		Creatable<PublicIPAddress> publicIPAddressCreateable = _Azure.publicIPAddresses()
        				.define(IPADR_PREFIX + vmAttributes.getName())
        			    .withRegion(region)
        			    .withExistingResourceGroup(resourceGroup);
        		Creatable<Disk> diskCreateable = null;
        		if(vmAttributes.getDiskSize() != null) {
            		diskCreateable = _Azure.disks().define(SdkContext.randomResourceName(RANDOM_DISK_PREFIX, 30))
            	            .withRegion(region)
            	            .withExistingResourceGroup(resourceGroup)
            	            .withData()
            	            .withSizeInGB(Integer.parseInt(vmAttributes.getDiskSize()));
        		}
        		VirtualMachineSizeTypes virtualMachineSizeTypes = VirtualMachineSizeTypes.fromString(vmAttributes.getImageSize());
        		if(vmAttributes.getNetwork().equalsIgnoreCase("new")) {
	        		if(vmAttributes.getType().equals(VM_INDICATOR_WINDOWS)) {
                		if(vmAttributes.getType().equals(VM_INDICATOR_WINDOWS)) {
        	       		    vm = createStandardWindowsVirtualMachineOnNewNetwork(
        	       		    		region,
        	       		    		resourceGroup,
        	       		    		image,
        	       		    		vmAttributes,
        	       		    		publicIPAddressCreateable,
        	       		    		diskCreateable,
        	       		    		virtualMachineSizeTypes
        	       		    		);
                		} else {
        	       		    vm = createStandardLinuxVirtualMachineOnNewNetwork(
        	       		    		region,
        	       		    		resourceGroup,
        	       		    		image,
        	       		    		vmAttributes,
        	       		    		publicIPAddressCreateable,
        	       		    		diskCreateable,
        	       		    		virtualMachineSizeTypes
        	       		    		);
                		}
	        		}
         		} else {
        			// use existing network and subnet
        			if(vmAttributes.getNetwork().contains(IConstants.Characters.SLASH)) {
            			String values[] = _Util.tokenizeString(vmAttributes.getNetwork(), false, IConstants.Characters.SLASH);
            			Network existingNetwork = getExistingNetwork(resourceGroup, values[0]); 
            			if(existingNetwork != null) {
            	       		if(vmAttributes.getType().equals(VM_INDICATOR_WINDOWS)) {
            	       			createStandardWindowsVirtualMachineOnExistingNetwork(
            	       		    		region,
            	       		    		resourceGroup,
            	       		    		values[1],
            	       		    		existingNetwork,
            	       		    		image,
            	       		    		vmAttributes,
            	       		    		publicIPAddressCreateable,
            	       		    		diskCreateable,
            	       		    		virtualMachineSizeTypes
            	       		    		);      	       		
            	       		} else {
            	       		    vm = createStandardLinuxVirtualMachineOnExistingNetwork(
            	       		    		region,
            	       		    		resourceGroup,
            	       		    		values[1],
            	       		    		existingNetwork,
            	       		    		image,
            	       		    		vmAttributes,
            	       		    		publicIPAddressCreateable,
            	       		    		diskCreateable,
            	       		    		virtualMachineSizeTypes
            	       		    		);
            	       		}
             			} else  {
             				LOG.error(MessageFormat.format(IMessages.NetworkNotFoundMsg,values[0], resourceGroup, region));
            			}
        			} else {
        				LOG.error(MessageFormat.format(IMessages.InvalidNetworkMsg,vmAttributes.getNetwork()));
        			}
        		}
    		} else {
        		VirtualMachineCustomImage image = getAvailableCustomImage(vmAttributes.getImageName(), selectedRegion);
        		if(image != null) {
       				LOG.debug("Creating virtual machine (" + vmAttributes.getName() + ") image (" + vmAttributes.getImageName() + ") in Region (" + selectedRegion.name() + ")");
            		Creatable<PublicIPAddress> publicIPAddressCreateable = _Azure.publicIPAddresses()
            				.define(IPADR_PREFIX + vmAttributes.getName())
            			    .withRegion(region)
            			    .withExistingResourceGroup(resourceGroup);
            		Creatable<Disk> diskCreateable = null;
            		if(vmAttributes.getDiskSize() != null) {
            		diskCreateable = _Azure.disks().define(SdkContext.randomResourceName(RANDOM_DISK_PREFIX, 30))
            	            .withRegion(region)
            	            .withExistingResourceGroup(resourceGroup)
            	            .withData()
            	            .withSizeInGB(Integer.parseInt(vmAttributes.getDiskSize()));
            		}
            		VirtualMachineSizeTypes virtualMachineSizeTypes = VirtualMachineSizeTypes.fromString(vmAttributes.getImageSize());
            		if(vmAttributes.getNetwork().equalsIgnoreCase("new")) {
                		if(vmAttributes.getType().equals(VM_INDICATOR_WINDOWS)) {
        	       		    vm = createCustomWindowsVirtualMachineOnNewNetwork(
        	       		    		region,
        	       		    		resourceGroup,
        	       		    		image,
        	       		    		vmAttributes,
        	       		    		publicIPAddressCreateable,
        	       		    		diskCreateable,
        	       		    		virtualMachineSizeTypes
        	       		    		);
                		} else {
        	       		    vm = createCustomLinuxVirtualMachineOnNewNetwork(
        	       		    		region,
        	       		    		resourceGroup,
        	       		    		image,
        	       		    		vmAttributes,
        	       		    		publicIPAddressCreateable,
        	       		    		diskCreateable,
        	       		    		virtualMachineSizeTypes
        	       		    		);
                		}
            		} else {
            			// use existing network and subnet
            			if(vmAttributes.getNetwork().contains(IConstants.Characters.SLASH)) {
                			String values[] = _Util.tokenizeString(vmAttributes.getNetwork(), false, IConstants.Characters.SLASH);
                			Network existingNetwork = getExistingNetwork(resourceGroup, values[0]); 
                			if(existingNetwork != null) {
                	       		if(vmAttributes.getType().equals(VM_INDICATOR_WINDOWS)) {
                	       			createCustomWindowsVirtualMachineOnExistingNetwork(
                	       		    		region,
                	       		    		resourceGroup,
                	       		    		values[1],
                	       		    		existingNetwork,
                	       		    		image,
                	       		    		vmAttributes,
                	       		    		publicIPAddressCreateable,
                	       		    		diskCreateable,
                	       		    		virtualMachineSizeTypes
                	       		    		);      	       		
                	       		} else {
                	       		    vm = createCustomLinuxVirtualMachineOnExistingNetwork(
                	       		    		region,
                	       		    		resourceGroup,
                	       		    		values[1],
                	       		    		existingNetwork,
                	       		    		image,
                	       		    		vmAttributes,
                	       		    		publicIPAddressCreateable,
                	       		    		diskCreateable,
                	       		    		virtualMachineSizeTypes
                	       		    		);
                	       		}
                 			} else  {
                 				LOG.info(MessageFormat.format(IMessages.NetworkNotFoundMsg,values[0], resourceGroup, region));
                			}
            			} else {
            				LOG.info(MessageFormat.format(IMessages.InvalidNetworkMsg,vmAttributes.getNetwork()));
            			}
            		}
        		} else {
        			LOG.error(MessageFormat.format(IMessages.ImageNotFoundMsg, vmAttributes.getImageName(),region));
        		}
    		}
		} catch(Exception ex) {
			throw new Exception(ex);
		}
        return vm;
    }
    
	public List<InternalIpAddresses> listInterfaces(
			String resourceGroup
			) throws Exception {
		List<InternalIpAddresses> addresses = new ArrayList<InternalIpAddresses>();
				
		try {
			LOG.debug("Checking Resource Group (" + resourceGroup + ") interfaces");
	        PagedList<NetworkInterface> networkInterfaces = _Azure
	                .networkInterfaces()
	                .listByResourceGroup(resourceGroup);
	        for (NetworkInterface networkInterface : networkInterfaces) {
	        	InternalIpAddresses address = new InternalIpAddresses();
	        	if(networkInterface.virtualMachineId() != null) {
	        		int startOfName = networkInterface.virtualMachineId().lastIndexOf(IConstants.Characters.SLASH);
	        		if(startOfName > -1) {
	        			address.setVirtualMachineName(networkInterface.virtualMachineId().substring(startOfName + 1, networkInterface.virtualMachineId().length()).trim());
	        			address.setInternalIpAddress(networkInterface.primaryPrivateIP().toString().trim());
	        			addresses.add(address);
	        		}
	        	}
	        }
		} catch(Exception ex) {
			throw new Exception(ex);
		}
		return addresses;
	}

    public CreateVMAttributes extractVMAttributes(
    		String attributes
    		) throws Exception {
    	
    	CreateVMAttributes vmAttributes = new CreateVMAttributes();
    	
    	try {
    		String[] values = attributes.split(IConstants.Characters.COMMA);
    		vmAttributes.setType(values[0]);
    		vmAttributes.setName(values[1]);
    		vmAttributes.setImageName(values[2]);
    		vmAttributes.setImageSize(values[3]);
    		String disk = values[4];
    		if(!disk.equals(IConstants.Characters.SPACE)) {
        		vmAttributes.setDiskSize(values[4]);
    		}
    		vmAttributes.setAdminUser(values[5]);
    		vmAttributes.setAdminUserPassword(values[6]);
    		vmAttributes.setNetwork(values[7]);
    		vmAttributes.setAddressSpace(values[8]);
    		
		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return vmAttributes;
    }

    private String convertRegionName(
    		String name
    		) throws Exception {
    	
    	String knownName = null;
    	
    	try {
    		knownName = name.toLowerCase();
    		knownName = knownName.replaceAll(IConstants.Characters.SPACE, IConstants.Characters.EMPTY_STRING);
		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return knownName;
    }

    private VirtualMachineImage getAvailableImage(
    		String imageName,
    		Region region
    		) throws Exception {
    	
    	VirtualMachineImage image = null;
        VirtualMachinePublisher chosenVmPublisher;
    	
    	try {
    		// split image information
    		String[] imageInfo = imageName.split(IConstants.Characters.UNDERSCORE);
    		
        	List<VirtualMachinePublisher> vmPublishers = _Azure
                    .virtualMachineImages()
                    .publishers()
                    .listByRegion(region);
            
            for (VirtualMachinePublisher vmPublisher : vmPublishers) {
                 if (vmPublisher.name().equalsIgnoreCase(imageInfo[0])) {
                    chosenVmPublisher = vmPublisher;
                    for (VirtualMachineOffer vmOffer : chosenVmPublisher.offers().list()) {
                   	if(vmOffer.name().equalsIgnoreCase(imageInfo[1])) {
                    		for (VirtualMachineSku sku: vmOffer.skus().list()) {
                            	if(sku.name().equalsIgnoreCase(imageInfo[2])) {
                                	int imageCount = sku.images().list().size();
                                	if(imageCount > 0) {
                                    	image = sku.images().list().get(sku.images().list().size() - 1);
                                	}
                            	}	
                        	}
                        }
                    }
                }
           }
		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return image;
    }
    
    private VirtualMachineCustomImage getAvailableCustomImage(
    		String imageName,
    		Region region
    		) throws Exception {
    	
    	VirtualMachineCustomImage image = null;
    	
    	try {
    		List<VirtualMachineCustomImage> vmCustomImages = _Azure
                    .virtualMachineCustomImages()
                    .list();
            
            for (VirtualMachineCustomImage vmCustomImage : vmCustomImages) {
                 if (vmCustomImage.name().equalsIgnoreCase(imageName)) {
                	 image = vmCustomImage;
                 }
            }
		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return image;
    }
  
    private Network getExistingNetwork(
    		String resourceGroup, 
    		String name
    		) throws Exception {
    	
    	Network network = null;
    	
    	try {
    		List<Network> networks = _Azure
    				.networks()
    				.listByResourceGroup(resourceGroup);
    		
        	for (Network vNetwork : networks) {
        		if(vNetwork.name().equalsIgnoreCase(name)) {
        			network = vNetwork;
        			break;
        		}
        	}
		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return network;
    }
  
    private VirtualMachine createStandardWindowsVirtualMachineOnExistingNetwork(
    		String region,
    		String resourceGroup,
    		String subnet,
    		Network network,
    		VirtualMachineImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
    		virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
       	         .withRegion(region)
       	         .withExistingResourceGroup(resourceGroup)
       	         .withExistingPrimaryNetwork(network)
       	         .withSubnet(subnet)
       	         .withPrimaryPrivateIPAddressDynamic()
       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
       	         .withSpecificWindowsImageVersion(image.imageReference())
            	 .withAdminUsername(vmAttributes.getAdminUser())
            	 .withAdminPassword(vmAttributes.getAdminUserPassword())
       	         .withNewDataDisk(diskCreateable)
       	         .withSize(virtualMachineSizeTypes)
       	         .create();

		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }

    private VirtualMachine createStandardWindowsVirtualMachineOnNewNetwork(
    		String region,
    		String resourceGroup,
    		VirtualMachineImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
    		virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
       	         .withRegion(region)
       	         .withExistingResourceGroup(resourceGroup)
       	         .withNewPrimaryNetwork(vmAttributes.getAddressSpace())
       	         .withPrimaryPrivateIPAddressDynamic()
       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
       	         .withSpecificWindowsImageVersion(image.imageReference())
       	         .withAdminUsername(vmAttributes.getAdminUser())
       	         .withAdminPassword(vmAttributes.getAdminUserPassword())
       	         .withNewDataDisk(diskCreateable)
       	         .withSize(virtualMachineSizeTypes)
       	         .create();

		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }

    private VirtualMachine createCustomWindowsVirtualMachineOnExistingNetwork(
    		String region,
    		String resourceGroup,
    		String subnet,
    		Network network,
    		VirtualMachineCustomImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
    		if(diskCreateable == null) {
    			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
	       	         .withRegion(region)
	       	         .withExistingResourceGroup(resourceGroup)
	       	         .withExistingPrimaryNetwork(network)
	       	         .withSubnet(subnet)
	       	         .withPrimaryPrivateIPAddressDynamic()
	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
	       	         .withWindowsCustomImage(image.id())
	            	 .withAdminUsername(vmAttributes.getAdminUser())
	            	 .withAdminPassword(vmAttributes.getAdminUserPassword())
	       	         .withSize(virtualMachineSizeTypes)
	       	         .create();
    		} else {
    			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
	       	         .withRegion(region)
	       	         .withExistingResourceGroup(resourceGroup)
	       	         .withExistingPrimaryNetwork(network)
	       	         .withSubnet(subnet)
	       	         .withPrimaryPrivateIPAddressDynamic()
	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
	       	         .withWindowsCustomImage(image.id())
	            	 .withAdminUsername(vmAttributes.getAdminUser())
	            	 .withAdminPassword(vmAttributes.getAdminUserPassword())
	       	         .withNewDataDisk(diskCreateable)
	       	         .withSize(virtualMachineSizeTypes)
	       	         .create();
    		}
		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }
    
    private VirtualMachine createCustomWindowsVirtualMachineOnNewNetwork(
    		String region,
    		String resourceGroup,
    		VirtualMachineCustomImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
    		if(diskCreateable == null) {
    			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
	       	         .withRegion(region)
	       	         .withExistingResourceGroup(resourceGroup)
	                 .withNewPrimaryNetwork(vmAttributes.getAddressSpace())
	       	         .withPrimaryPrivateIPAddressDynamic()
	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
	       	         .withWindowsCustomImage(image.id())
	            	 .withAdminUsername(vmAttributes.getAdminUser())
	            	 .withAdminPassword(vmAttributes.getAdminUserPassword())
	       	         .withSize(virtualMachineSizeTypes)
	       	         .create();
    		} else {
    			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
   	       	         .withRegion(region)
   	       	         .withExistingResourceGroup(resourceGroup)
   	                 .withNewPrimaryNetwork(vmAttributes.getAddressSpace())
   	       	         .withPrimaryPrivateIPAddressDynamic()
   	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
   	       	         .withWindowsCustomImage(image.id())
   	            	 .withAdminUsername(vmAttributes.getAdminUser())
   	            	 .withAdminPassword(vmAttributes.getAdminUserPassword())
   	       	         .withNewDataDisk(diskCreateable)
   	       	         .withSize(virtualMachineSizeTypes)
   	       	         .create();
    		}

		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }

    private VirtualMachine createStandardLinuxVirtualMachineOnExistingNetwork(
    		String region,
    		String resourceGroup,
    		String subnet,
    		Network network,
    		VirtualMachineImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
    		virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
       	         .withRegion(region)
       	         .withExistingResourceGroup(resourceGroup)
       	         .withExistingPrimaryNetwork(network)
       	         .withSubnet(subnet)
       	         .withPrimaryPrivateIPAddressDynamic()
       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
            	 .withSpecificLinuxImageVersion(image.imageReference())
             	 .withRootUsername(vmAttributes.getAdminUser())
            	 .withRootPassword(vmAttributes.getAdminUserPassword())
       	         .withNewDataDisk(diskCreateable)
       	         .withSize(virtualMachineSizeTypes)
       	         .create();

		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }

    private VirtualMachine createStandardLinuxVirtualMachineOnNewNetwork(
    		String region,
    		String resourceGroup,
    		VirtualMachineImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
    		virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
       	         .withRegion(region)
       	         .withExistingResourceGroup(resourceGroup)
                 .withNewPrimaryNetwork(vmAttributes.getAddressSpace())
                 .withPrimaryPrivateIPAddressDynamic()
       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
            	 .withSpecificLinuxImageVersion(image.imageReference())
        	     .withRootUsername(vmAttributes.getAdminUser())
       	         .withRootPassword(vmAttributes.getAdminUserPassword())
       	         .withNewDataDisk(diskCreateable)
       	         .withSize(virtualMachineSizeTypes)
       	         .create();

		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }

    private VirtualMachine createCustomLinuxVirtualMachineOnExistingNetwork(
    		String region,
    		String resourceGroup,
    		String subnet,
    		Network network,
    		VirtualMachineCustomImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
    		if(diskCreateable == null) {
    			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
	       	         .withRegion(region)
	       	         .withExistingResourceGroup(resourceGroup)
	       	         .withExistingPrimaryNetwork(network)
	       	         .withSubnet(subnet)
	       	         .withPrimaryPrivateIPAddressDynamic()
	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
	       	         .withLinuxCustomImage(image.id())
	        	     .withRootUsername(vmAttributes.getAdminUser())
	       	         .withRootPassword(vmAttributes.getAdminUserPassword())
	       	         .withSize(virtualMachineSizeTypes)
	       	         .create();
    		} else {
    			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
   	       	         .withRegion(region)
   	       	         .withExistingResourceGroup(resourceGroup)
   	       	         .withExistingPrimaryNetwork(network)
   	       	         .withSubnet(subnet)
   	       	         .withPrimaryPrivateIPAddressDynamic()
   	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
   	       	         .withLinuxCustomImage(image.id())
   	        	     .withRootUsername(vmAttributes.getAdminUser())
   	       	         .withRootPassword(vmAttributes.getAdminUserPassword())
   	       	         .withNewDataDisk(diskCreateable)
   	       	         .withSize(virtualMachineSizeTypes)
   	       	         .create();
    		}
		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }

    private VirtualMachine createCustomLinuxVirtualMachineOnNewNetwork(
    		String region,
    		String resourceGroup,
    		VirtualMachineCustomImage image,
    		CreateVMAttributes vmAttributes,
    		Creatable<PublicIPAddress> publicIPAddressCreateable,
    		Creatable<Disk> diskCreateable,
    		VirtualMachineSizeTypes virtualMachineSizeTypes
    		) throws Exception {
    	
    	VirtualMachine virtualMachine = null;
    	try {
       		if(diskCreateable == null) {
       			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
	       	         .withRegion(region)
	       	         .withExistingResourceGroup(resourceGroup)
	                 .withNewPrimaryNetwork(vmAttributes.getAddressSpace())
	                 .withPrimaryPrivateIPAddressDynamic()
	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
	       	         .withLinuxCustomImage(image.id())
	        	     .withRootUsername(vmAttributes.getAdminUser())
	       	         .withRootPassword(vmAttributes.getAdminUserPassword())
	       	         .withSize(virtualMachineSizeTypes)
	       	         .create();
       		} else {
       			virtualMachine = _Azure.virtualMachines().define(vmAttributes.getName())
   	       	         .withRegion(region)
   	       	         .withExistingResourceGroup(resourceGroup)
   	                 .withNewPrimaryNetwork(vmAttributes.getAddressSpace())
   	                 .withPrimaryPrivateIPAddressDynamic()
   	       	         .withNewPrimaryPublicIPAddress(publicIPAddressCreateable)
   	       	         .withLinuxCustomImage(image.id())
   	        	     .withRootUsername(vmAttributes.getAdminUser())
   	       	         .withRootPassword(vmAttributes.getAdminUserPassword())
   	       	         .withNewDataDisk(diskCreateable)
   	       	         .withSize(virtualMachineSizeTypes)
   	       	         .create();
       		}

		} catch(Exception ex) {
			throw new Exception(ex);
		}
    	return virtualMachine;
    }

}
