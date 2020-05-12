# Azure VM

Azure VM is an OpCon Connector for Windows that uses the Azure Java SDK to interact with Azure virtual machines. Provides tasks to manage 
virtual machines...

It consists of a single program **AzureVM.exe**.

## Installation

### Environment

- The AzureVM.exe connector needs **Java version 11** to function
  - An embedded JavaRuntimeEnvironment 11 is included along with the delivery zip / tar files. Once the archive extracted, "/java" directory contains the JRE binaries.

### Windows Instructions
Download AzureVM_Windows.zip file from the desired [release available here](https://github.com/SMATechnologies/azure-vm-java/releases).

After download, extract the zip file to the location you'd like to install the connector to. Once unzipped, everything needed should be located under the root folder of that directory.

After extract, copy the Enterprise Manager Job-Subtype from the /emplugins directory to dropins directory of each Enterprise Manager that will create job
definitions (if the directory does not exist, create it).

Restart Enterprise Manager and a new Windows Job Sub-Type Azure Storage should be visible (if not restart Enterprise Manager using 'Run as Administrator'). 

Create a global property **AzureVmPath** that contains the full path of the installation directory.
 
## Configuration
The Azure Vm connector uses a configuration file **Connector.config** that contains the Azure account information.

The account information consists of the subscription ID, the tenant ID, the client ID and secret key. These values can be trieved from the Azure environment.

The connection subsction ID, tenant ID, client ID and secret key must be encrypted using the **Encrypt.exe** program.

The encryption tool provides basic encryption capabilities to prevent information being displayed in clear text.

The connector also connects to the OpCon environment to save virtual machine addresses in global properties. This connection uses a application token when connecting to OpCon through the OpCon Rest-API.  

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
OPCON_API_TOKEN     | Text | is the application token used to connect to the OpCon System and into inserted in the configuration file using the --setup switch on **AzureVM.exe**

### EncryptValue Utility
The EncryptValue utility uses standard 64 bit encryption.

Supports a -v argument and displays the encrypted value

On Windows, example on how to encrypt the value "abcdefg":
```
EncryptValue -v abcdefg

```

## Exit Codes
The `AzureVM` exits `0` when the performed request succeeds. Otherwise `AzureVM` exits `1` on failure.

## AzureVM Arguments
The AzureVM connector requires arguments to be given to function. It uses the principle of Tasks, where each task performs an action or a combination of actions against Azure Virtual Machines.

### Global
Arguments | Description
--------- | -----------
**-rg**  | (Mandatory) The Resource group associated with the request.
**-t**   | (Mandatory) The task to perform.

### containercreate
Can be used to create a new container within the storage account.

Arguments | Description
--------- | -----------
**-t**  | Value is **containercreate**
**-cn** | Required field for containercreate and consists of the name of the container to create.

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t containercreate -cn MY_CONTAINER
```
### containerdelete
Can be used to delete containers within the storage account.

Arguments | Description
--------- | -----------
**-t**  | Value is **containerdelete**
**-cn** | Required field for containerdelete and consists of the name of the container to create. Supports wild cards (? and *).

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t containerdelete -cn MY_CONT????ER
```
### containerlist
Can be used to list container within the storage account.

Arguments | Description
--------- | -----------
**-t**  | Value is **containerlist**
**-cn** | Required field for containerlist and consists of the name of the container to list. Supports wild cards (? and *).

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t containerdelete -cn *
```
### filearrival
Can be used to monitor for the aarival of a file in a specific container. It should eb noted that before starting the task, any previous
existing versions of the file must be removed from the container.

Arguments | Description
--------- | -----------
**-t**  | Value is **filearrival**
**-cn** | Required field for filearrival and consists of the name of the container that the file will be placed in.
**-fn** | Required field for filearrival and consists of the name of the file.
**-wt** | Required field for filearrival and consists of the maximum time in minutes to wait for the file. A value of 0 will wait indefinitely for the file to arrive.
**-fs** | Required field for filearrival and consists of the time in seconds for the file size to be static to determine if the file aarival is complete. Default value is 5 seconds.
**-pd** | Required field for filearrival and consists of the time in seconds to wait before the initial check. Default value is 5.
**-pi** | Required field for filearrival and consists of the time in seconds between checks. Default value is 3. 

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t filearrival -cn MY_CONTAINER -fn MY_FILE -wt 15 -fs 5 -pd 3 -pi 2
```
### filedelete
Can be used to delete files within containers within the storage account.

Arguments | Description
--------- | -----------
**-t**  | Value is **filedelete**
**-cn** | Required field for filedelete and consists of the name of the container to delete files from. Supports wild cards (? and *).
**-fn** | Required field for filedelete and consists of the name of the file to delete. Supports wild cards (? and *).

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t filedelete -cn * -fn MY_FILE???
```
### filedownload
Can be used to download files from a container within the storage account. The files are downloaded to locations relative to the azure-storage connector installation. Before downloading files, the files must not exist in the target directoy. 

Arguments | Description
--------- | -----------
**-t**  | Value is **filedownload**
**-cn** | Required field for filedownload and consists of the name of the container to download files from. 
**-fn** | Required field for filedownload and consists of the name of the file(s) to download. Supports wild cards (? and *).
**-di** | Required field for filedownload and consists of the full path of the directory to download the files to.

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t filedownload -cn MY_CONTAINER -fn MY_FILE??? -di c:\DOWNLOAD\MY_DIRECTORY
```
### filelist
Can be used to list files within containers within the storage account.

Arguments | Description
--------- | -----------
**-t**  | Value is **filelist**
**-cn** | Required field for filelist and consists of the name of the container to list files from. Supports wild cards (? and *).
**-fn** | Required field for filelist and consists of the name of the file to list. Supports wild cards (? and *).

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t filedelete -cn * -fn *
```
### fileupload
Can be used to upload files from a directory to a container within the storage account. The files are uploaded from locations relative to the azure-storage connector installation. Before downloading files, the files must not exist in the target directoy. 

Arguments | Description
--------- | -----------
**-t**  | Value is **fileupload**
**-cn** | Required field for fileupload and consists of the name of the container to upload files to. 
**-fn** | Required field for fileupload and consists of the name of the file(s) to upload. Supports wild cards (? and *).
**-di** | Required field for fileupload and consists of the full path of the directory to upload the files from.
**-ov** | Optional field for fileupload and indicates if existing files can be overwritten.

Usage
```
AzureStorage.exe -sa MY_ACCOUNT -t fileupload -cn MY_CONTAINER -fn MY_FILE??? -di c:\UPOAD\MY_DIRECTORY -ov
```
## AzureStorage Job Sub-Type
The AzureStorage connector provides a Job Sub-Type that can be used to simplify job definitions within OpCon.

![jobsubtype](/docs/images/azure_storage_subtype.PNG)

When using the Job Sub-Type, fill in the Account name (this must be a name defined in a STORAGE definition in the Connector.config file.

Select the Task from the drop-down list and enter the required values. Only values associated with the task will be enabled. Once a task has been saved, the task type cannot be changed.
