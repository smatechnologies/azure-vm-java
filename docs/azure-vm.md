# Azure VM

Azure VM is an OpCon Connector for Windows that uses the Azure Java SDK to interact with Azure virtual machines. Provides tasks to manage 
virtual machines...

It consists of a single program **AzureVM.exe**.

## Installation

### Environment

- The AzureVM.exe connector needs **Java version 11** to function
  - An embedded JavaRuntimeEnvironment 11 is included along with the delivery zip / tar files. Once the archive extracted, "/java" directory contains the JRE binaries.

### Instructions
Download AzureVM_Windows.zip file from the desired [release available here](https://github.com/SMATechnologies/azure-vm-java/releases).

After download, extract the zip file to the location you'd like to install the connector to. Once unzipped, everything needed should be located under the root folder of that directory.

After extract, copy the Enterprise Manager Job-Subtype from the /emplugins directory to dropins directory of each Enterprise Manager that will create job
definitions (if the directory does not exist, create it).

Restart Enterprise Manager and a new Windows Job Sub-Type Azure Storage should be visible (if not restart Enterprise Manager using 'Run as Administrator'). 

Create a global property **AzureVmPath** that contains the full path of the installation directory.

Create the OpCon global properties associated with the drop-down lists used when creating virtual machines. The provided lists are not complete and items can be added or removed from the drop down-lists by simply editing the global properties.

**AZURE_WINDOWS_SERVERS** Create the global property and add the values contained in **Windows Image List** using a comma to separate them. The doubles quotes surrounding the values must be retained. These values will then be visible in the drop-down list.
**AZURE_LINUX_SERVERS** Create the global property and add the values contained in **Linux Image List** using a comma to separate them. The doubles quotes surrounding the values must be retained. These values will then be visible in the drop-down list.
**AZURE_SERVER_SIZES** Create the global property and add the values contained in **Server Size List** using a comma to separate them. The doubles quotes surrounding the values must be retained. These values will then be visible in the drop-down list.
 
## Configuration
The Azure Vm connector uses a configuration file **Connector.config** that contains the Azure account information.

The account information consists of the subscription ID, the tenant ID, the client ID and secret key. These values can be trieved from the Azure environment.

The connection subsction ID, tenant ID, client ID and the secret key must be encrypted using the **Encrypt.exe** program.

The encryption tool provides basic encryption capabilities to prevent configuration information from being displayed in clear text.

The connector also connects to the OpCon environment to save virtual machine addresses in global properties. This connection uses a application token when connecting to OpCon through the OpCon Rest-API. The application token must be encrypted using the **Encrypt.exe** program.

The connector provides a **--setup** switch which can be used to set the OpCon Api information in the Connector.config. An application token of **CONNECTORS** is created and the returned token is encrypted and inserted in Connector.config as OPCONAPI_TOKEN. 

When using this switch the following arguments are required.
 
Argument       | Description
-------------- | ---------------
-username      | An OpCon user name that is a member of the ocadm role.
-password      | The password of the OpCon user.
-address       | The address and port number of the OpCon Rest-API server - this information in inserted into the Connector.config as OPCON-ADDRESS.
-tls           | Indicates if the OpCon Rest-API requires a tls connection - this information is inserted into the Connector.config as OPCONAPI_USING_TLS.

```
AzureVM.exe --setup -username name -password pwd -address server:port -tls

```

**Connector.config** file example:
```
[CONNECTOR]
NAME=Azure VM Connector
DEBUG=OFF

[MSAZURE]
TENANT = (encrypted value)
SUBSCRIPTION = (encrypted value)
CLIENT = (encrypted value)
KEY = (encrypted value)

[OPCON API]
OPCONAPI_ADDRESS = address:port
OPCONAPI_USING_TLS = True
OPCONAPI_TOKEN = (encrypted value)

```

Keyword             | Type | Description
------------------- | ---- | -----------
TENANT              | Text | **encrypted value** is the tenant ID encrypted using the using the **Encrypt.exe** program. 
SUBSCRIPTION        | Text | **encrypted value** is the subscription ID encrypted using the using the **Encrypt.exe** program. 
CLIENT              | Text | **encrypted value** is the client ID encrypted using the using the **Encrypt.exe** program. 
KEY                 | Text | **encrypted value** is the secret key encrypted using the using the **Encrypt.exe** program. 

OPCON_API_ADDRESS   | Text | is the address of the OpCon System and in inserted into the configuration file using the --setup switch on **AzureVM.exe**
OPCON_API_USING_TLS | Text | indicates if the OpCon System is using tls and in inserted into the configuration file using the --setup switch on **AzureVM.exe**
OPCON_API_TOKEN     | Text | **encrypted value** is the application token used to connect to the OpCon System and into inserted in the configuration file using the --setup switch on **AzureVM.exe**


### Encrypt Utility
The Encrypt utility uses standard 64 bit encryption.

Supports a -v argument and displays the encrypted value

On Windows, example on how to encrypt the value "abcdefg":
```
Encrypt -v abcdefg

```

## Exit Codes
The `AzureVM` exits `0` when the performed request succeeds. Otherwise `AzureVM` exits `1` on failure.

## AzureVM Arguments
The AzureVM connector requires arguments to be given to function. It uses the principle of Tasks, where each task performs an action or a combination of actions against Azure Virtual Machines.

### Global
Arguments  | Description
---------- | -----------
**-rg**    | (Mandatory) The Resource group associated with the Azure request.
**-t**     | (Mandatory) The task to perform.

### Create Virtual Machine
Can be used to create a new container within the storage account.

Arguments  | Description
---------- | -----------
**-t**     | Value is **create**
**-vm**    | Required field containing the name of the virtual machine to create.
**-ippvt** | Optional field containing the name of the global property to insert the virtual machine private ipaddress into.
**-ippub** | Optional field containing the name of the global property to insert the virtual machine public ipaddress into.
**-r**     | Required field containing the region where the virtual machine should be created.
**-at**    | Required field containing the attributes that will be used to create the virtual machine.
**-ci**    | Optional field indicating if the image to be used to crate the virtual machine is a custom image.

Usage
```
AzureVM.exe -rg MY_RESOURCEGROUP -t create -vm MY_VMACHINE -ippvt MY_VMACHINE_PVT -ippub MY_VMACHINE_PUB -at "linux,LINUX003,Canonical_UbuntuServer_18.04-LTS,Standard_DS3_v2,50,myadmin,<SmaEncrypt>Ohj+VFLoM3YX0WxNNN4vJQ==</SmaEncrypt>,LzLabsEU/default,LzLabsEU/default,"
```
### Delete Virtual Machine
Can be used to delete a virtual machine.

Arguments  | Description
---------- | -----------
**-t**     | Value is **delete**
**-vm**    | Required field containing the name of the virtual machine to delete.

Usage
```
AzureVM.exe -rg MY_RESOURCEGROUP -t delete -vm MY_VMACHINE
```
### List Virtual Machines
Can be used to list information about virtual machines in the resource group.

Arguments  | Description
---------- | -----------
**-t**     | Value is **list**

Usage
```
AzureVM.exe -rg MY_RESOURCEGROUP -t list 
```
Infomation returned
```
------------------------------------------------------------------------------------------------------------------ 
Virtual Machine Name     Region              Current State                 IP Private     IP Public      OS Type    
------------------------------------------------------------------------------------------------------------------ 
LINUX001                 West Europe         PowerState/running            10.1.0.17      52.178.65.0    LINUX      
LINUX002                 West Europe         PowerState/running            10.1.0.16      13.95.134.20   LINUX      
LINUX003                 West Europe         PowerState/running            10.1.0.18      13.95.106.31   LINUX      
OpCon                    West Europe         PowerState/running            10.1.0.6       52.166.248.28  WINDOWS    
SDM2                     West Europe         PowerState/stopped            10.1.0.9       168.63.108.63  LINUX      
------------------------------------------------------------------------------------------------------------------ 
```
### Deallocate Virtual Machine
Can be used to deallocate a virtual machine resources (alternate to PowerOff).

Arguments  | Description
---------- | -----------
**-t**     | Value is **deallocate**
**-vm**    | Required field containing the name of the virtual machine to power off.

Usage
```
AzureVM.exe -rg MY_RESOURCEGROUP -t poweroff -vm MY_VMACHINE
### PowerOff Virtual Machine
Can be used to poweroff a virtual machine.

Arguments  | Description
---------- | -----------
**-t**     | Value is **poweroff**
**-vm**    | Required field containing the name of the virtual machine to power off.

Usage
```
AzureVM.exe -rg MY_RESOURCEGROUP -t poweroff -vm MY_VMACHINE
```
### Restart Virtual Machine
Can be used to restart a virtual machine.

Arguments  | Description
---------- | -----------
**-t**     | Value is **restart**
**-vm**    | Required field containing the name of the virtual machine to restart.
**-ippvt** | Optional field containing the name of the global property to insert the virtual machine private ipaddress into.
**-ippub** | Optional field containing the name of the global property to insert the virtual machine public ipaddress into.

Usage
```
AzureVM.exe -rg MY_RESOURCEGROUP -t restart -vm MY_VMACHINE -ippvt MY_VMACHINE_PVT -ippub MY_VMACHINE_PUB
```
### Start Virtual Machine
Can be used to start a virtual machine.

Arguments  | Description
---------- | -----------
**-t**     | Value is **start**
**-vm**    | Required field containing the name of the virtual machine to start.
**-ippvt** | Optional field containing the name of the global property to insert the virtual machine private ipaddress into.
**-ippub** | Optional field containing the name of the global property to insert the virtual machine public ipaddress into.

Usage
```
AzureVM.exe -rg MY_RESOURCEGROUP -t start -vm MY_VMACHINE -ippvt MY_VMACHINE_PVT -ippub MY_VMACHINE_PUB
```
## AzureVM Job Sub-Type
The AzureVM connector provides a Job Sub-Type that can be used to simplify job definitions within OpCon.

![jobsubtype](/docs/images/azure_vm_subtype.PNG)

Select the Task from the drop-down list and enter the required values. Only values associated with the task will be enabled. Once a task has been saved, the task type cannot be changed.

When creating a virtual machine, select the attribute values from the various drop-down lists. 
Selecting the type from the **Virtual Machine Type** drop-down list (either Windows or Linux, will populate the Image Name drop-down lists with images associated with the Virtual Machine Type). 
Select the region from the **Region** drop-down list.
Select the image that will be used to create the virtual machine from the **Image Name** drop-down list.
If the image is a custom image, check the **Custom Image** checkbox. When selecting a custom image, a Disk Size is not required and the image size should match the size used when creating the custom image.
Select the image size from the **Image Size** drop-down list.
Enter a disk size in giga-bytes in the **Disk Size (Gb)** field when creating a non custom image.
Enter the networking requirements for the virtual machine in the **Network** and **Address Space** fields. To add a virtual machine to an existing Virtual network/subnet enter this name in the fields.
Enter a user name for the administrator for this virtual machine into the **Admin Userid** field taking into account the naming requirements for your virtual machine type.
Enter the password that will be associated with the admin user in the **Admin Userid Password** field (encrypted global properties can be used to hide the password).

### AzureVM Job Sub-Type drop-down lists
The AzureVM Job Sub-Type uses multiple drop-down lists that present options when creating a virtual machine. These drop-down lists are populated from values extracted from OpCon global properties. This means it is possible to update the values that appear in these lists.
 
**AZURE_WINDOWS_SERVERS** contains a list of images that can be used to create Windows virtual machines.
**AZURE_LINUX_SERVERS** contains a list of images that can be used to create Linux virtual machines.
**AZURE_SERVER_SIZES** contains a list of image sizes that can be used to create virtual machines.

##Windows Image List
```
"MicrosoftSQLServer_SQL2012SP3-WS2012R2_Enterprise"
"MicrosoftSQLServer_SQL2012SP4-WS2012R2_Enterprise"
"MicrosoftSQLServer_SQL2012SP4-WS2012R2_Express"
"MicrosoftSQLServer_SQL2012SP4-WS2012R2_Standard"
"MicrosoftSQLServer_SQL2014SP2-WS2012R2_Enterprise"
"MicrosoftSQLServer_SQL2014SP2-WS2012R2_Express"
"MicrosoftSQLServer_SQL2014SP2-WS2012R2_Standard"
"MicrosoftSQLServer_sql2014sp3-ws2012r2_enterprise"
"MicrosoftSQLServer_sql2014sp3-ws2012r2_express"
"MicrosoftSQLServer_sql2014sp3-ws2012r2_standard"
"MicrosoftSQLServer_SQL2016-WS2012R2_Enterprise"
"MicrosoftSQLServer_SQL2016SP1-WS2016_Enterprise"
"MicrosoftSQLServer_SQL2016SP1-WS2016_Express"
"MicrosoftSQLServer_SQL2016SP1-WS2016_Standard"
"MicrosoftSQLServer_SQL2016SP2-WS2016_Enterprise"
"MicrosoftSQLServer_SQL2016SP2-WS2016_Express"
"MicrosoftSQLServer_SQL2016SP2-WS2016_Standard"
"MicrosoftSQLServer_SQL2017-WS2016_Enterprise
"MicrosoftWindowsServer_WindowsServer_2012-Datacenter"
"MicrosoftWindowsServer_WindowsServer_2012-R2-Datacenter"
"MicrosoftWindowsServer_WindowsServer_2016-Datacenter"
"MicrosoftWindowsServer_WindowsServer_2019-Datacenter"
```
##Linux Image List
```
"Canonical_UbuntuServer_12.04.3-LTS"
"Canonical_UbuntuServer_12.04.4-LTS"
"Canonical_UbuntuServer_12.04.5-LTS"
"Canonical_UbuntuServer_14.04.0-LTS"
"Canonical_UbuntuServer_14.04.1-LTS"
"Canonical_UbuntuServer_14.04.2-LTS"
"Canonical_UbuntuServer_14.04.2-LTS"
"Canonical_UbuntuServer_14.04.3-LTS"
"Canonical_UbuntuServer_14.04.4-LTS"
"Canonical_UbuntuServer_14.04.5-LTS"
"Canonical_UbuntuServer_16.04-LTS"
"Canonical_UbuntuServer_16.04.0"
"Canonical_UbuntuServer_18.04"
"Canonical_UbuntuServer_18.10"
"RedHat_RHEL_6.7"
"RedHat_RHEL_6.8"
"RedHat_RHEL_6.9"
"RedHat_RHEL_6.10"
"RedHat_RHEL_7.2"
"RedHat_RHEL_7.3"
"RedHat_RHEL_7.4"
"RedHat_RHEL_7.5"
"SUSE_SLES_11-SP4"
"SUSE_SLES_12-SP4"
"SUSE_SLES_15"
"SUSE_SLES_15"
"MicrosoftSQLServer_SQL2017-RHEL7_Enterprise"
"MicrosoftSQLServer_SQL2017-RHEL7_Express"
"MicrosoftSQLServer_SQL2017-RHEL7_Standard"
"MicrosoftSQLServer_SQL2017-SLES12SP2_Enterprise"
"MicrosoftSQLServer_SQL2017-SLES12SP2_Express"
"MicrosoftSQLServer_SQL2017-Ubuntu1604_Enterprise"
"MicrosoftSQLServer_SQL2017-Ubuntu1604_Express"
```
##Server Size List
```
"Standard_A0"
"Standard_A1"
"Standard_A2"
"Standard_A3"
"Standard_A5"
"Standard_A4"
"Standard_A6"
"Standard_A7”
“Basic_A0”
“Basic_A1”
“Basic_A2”
“Basic_A3”
“Basic_A4"
"Standard_D1_v2"
"Standard_D2_v2"
"Standard_D3_v2"
"Standard_D4_v2"
"Standard_D5_v2"
"Standard_D11_v2"
"Standard_D12_v2"
"Standard_D13_v2"
"Standard_D14_v2"
"Standard_D15_v2"
"Standard_D2_v2_Promo"
"Standard_D3_v2_Promo"
"Standard_D4_v2_Promo"
"Standard_D5_v2_Promo"
"Standard_D11_v2_Promo"
"Standard_D12_v2_Promo"
"Standard_D13_v2_Promo"
"Standard_D14_v2_Promo"
"Standard_F1"
"Standard_F2"
"Standard_F4"
"Standard_F8"
"Standard_F16"
"Standard_A1_v2"
"Standard_A2m_v2"
"Standard_A2_v2"
"Standard_A4m_v2"
"Standard_A4_v2"
"Standard_A8m_v2"
"Standard_A8_v2"
"Standard_DS1"
"Standard_DS2"
"Standard_DS3"
"Standard_DS4"
"Standard_DS11"
"Standard_DS12"
"Standard_DS13"
"Standard_DS14"
"Standard_D2_v3"
"Standard_D4_v3"
"Standard_D8_v3"
"Standard_D16_v3"
"Standard_D32_v3"
"Standard_D1"
"Standard_D2"
"Standard_D3"
"Standard_D4"
"Standard_D11"
"Standard_D12"
"Standard_D13"
"Standard_D14"
"Standard_B1ms"
"Standard_B1s"
"Standard_B2ms"
"Standard_B2s"
"Standard_B4ms"
"Standard_B8ms"
"Standard_DS1_v2"
"Standard_DS2_v2"
"Standard_DS3_v2"
"Standard_DS4_v2"
"Standard_DS5_v2"
"Standard_DS11-1_v2"
"Standard_DS11_v2"
"Standard_DS12-1_v2"
"Standard_DS12-2_v2"
"Standard_DS12_v2"
"Standard_DS13-2_v2"
"Standard_DS13-4_v2"
"Standard_DS13_v2"
"Standard_DS14-4_v2"
"Standard_DS14-x8_v2"
"Standard_DS14_v2"
"Standard_DS2_v2_Promo"
"Standard_DS3_v2_Promo"
"Standard_DS4_v2_Promo"
"Standard_DS5_v2_Promo"
"Standard_DS11_v2_Promo"
"Standard_DS12_v2_Promo"
"Standard_DS13_v2_Promo"
"Standard_DS14_v2_Promo"
"Standard_F1s"
"Standard_F2s"
"Standard_F4s"
"Standard_F8s"
"Standard_F16s"
"Standard_D64_v3"
"Standard_D2s_v3"
"Standard_D4s_v3"
"Standard_D8s_v3"
"Standard_D16s_v3"
"Standard_D32s_v3"
"Standard_D64s_v3"
"Standard_E2_v3"
"Standard_E4_v3"
"Standard_E8_v3"
"Standard_E16_v3"
"Standard_E20_v3"
"Standard_E32_v3"
"Standard_E64i_v3"
"Standard_E64_v3"
"Standard_E2s_v3"
"Standard_E4-2s_v3"
"Standard_E4s_v3"
"Standard_E8-2s_v3"
"Standard_E8-4s_v3"
"Standard_E8s_v3"
"Standard_E16-4s_v3"
"Standard_E16-8s_v3"
"Standard_E16s_v3"
"Standard_E20s_v3"
"Standard_E32-8s_v3"
"Standard_E32-16s_v3"
"Standard_E32s_v3"
"Standard_E64-16s_v3"
"Standard_E64-32s_v3"
"Standard_E64is_v3"
"Standard_E64s_v3"
"Standard_DS15_v2"
"Standard_F2s_v2"
"Standard_F4s_v2"
"Standard_F8s_v2"
"Standard_F16s_v2"
"Standard_F32s_v2"
"Standard_F64s_v2"
"Standard_F72s_v2"
"Standard_G1"
"Standard_G2"
"Standard_G3"
"Standard_G4"
"Standard_G5"
"Standard_GS1"
"Standard_GS2"
"Standard_GS3"
"Standard_GS4"
"Standard_GS4-4"
"Standard_GS4-8"
"Standard_GS5"
"Standard_GS5-8"
"Standard_GS5-16"
"Standard_L4s"
"Standard_L8s"
"Standard_L16s"
"Standard_L32s"
```