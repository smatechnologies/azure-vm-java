package com.smatechnologies.msazure.vm.interfaces;

import java.util.List;

import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.smatechnologies.msazure.vm.modules.CreateVMAttributes;
import com.smatechnologies.msazure.vm.modules.InternalIpAddresses;

public interface IAzureVm {
	
    public ApplicationTokenCredentials createCredentials() throws Exception;
	public Azure connect(ApplicationTokenCredentials credentials) throws Exception;
	
    public VirtualMachine getVirtualMachine(String resourceGroup, String name) throws Exception;
	public List<VirtualMachine> getVirtualMachineList(String resourceGroup) throws Exception;
	
    public boolean powerOffVirtualMachine(VirtualMachine virtualMachine) throws Exception;
    public boolean startVirtualMachine(VirtualMachine virtualMachine) throws Exception;
    public boolean restartVirtualMachine(VirtualMachine virtualMachine) throws Exception;
    public boolean deleteVirtualMachine(VirtualMachine virtualMachine) throws Exception;
 
    public CreateVMAttributes extractVMAttributes(String attributes) throws Exception;
    public Disk createDataDisk(String region, String resourceGroup, Integer diskSize) throws Exception;
    public VirtualMachine createVirtualMachine(String region, String resourceGroup, CreateVMAttributes vmAttributes, boolean customImage) throws Exception;
    
    public List<InternalIpAddresses> listInterfaces(String resourceGroup) throws Exception;
    
}
