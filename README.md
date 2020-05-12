# Azure VM Connector (Java)
Is an OpCon connector that can interact with virtual machines in Microsoft Azure Virtual Machines.
![diagrm](/docs/images/Connector_overview.png)

The job definitions are entered as Windows jobs using the Azure VM Job Sub-Type. When the job is scheduled by OpCon the arguments are passed to the connector and a completion code is returned.

The connector supports the following tasks to manage virtual machines.

- **Create Virtual Machine**: creates a virtual machine.
- **Delete Virtual Machine**: deletes a virtual machine.
- **List Virtual Machines**: lists the virtual machines in the resource group providing information about each machine.
- **PowerOff Virtual Machine**: performs a shuitdown of the virtual machine.
- **Restart Virtual Machine**: Restarts the virtual machine.
- **Start Virtual Machine**: Starts a virtual machine..

During the Create, Restart and Start tasks, the connector retrieves the address information for the virtual machine and this can be saved in global properties with opCon for future use.

# Prerequisites
- Microsoft Azure Account
- Execution requires a /java directory that contains a java 11 binary to execute the connector

# Instructions

For detailed information see the aszure-vm.md documentation.

# Disclaimer
No Support and No Warranty are provided by SMA Technologies for this project and related material. The use of this project's files is on your own risk.

SMA Technologies assumes no liability for damage caused by the usage of any of the files offered here via this Github repository.

# Prerequisites


# Instructions


# License
Copyright 2019 SMA Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

# Contributing
We love contributions, please read our [Contribution Guide](CONTRIBUTING.md) to get started!

# Code of Conduct
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code-of-conduct.md)
SMA Technologies has adopted the [Contributor Covenant](CODE_OF_CONDUCT.md) as its Code of Conduct, and we expect project participants to adhere to it. Please read the [full text](CODE_OF_CONDUCT.md) so that you can understand what actions will and will not be tolerated.
