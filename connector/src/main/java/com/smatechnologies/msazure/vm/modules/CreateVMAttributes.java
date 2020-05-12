package com.smatechnologies.msazure.vm.modules;

public class CreateVMAttributes {
	
	private String type = null;
	private String name = null;
	private String imageName = null;
	private String imageSize = null;
	private String diskSize = null;
	private String adminUser = null;
	private String adminUserPassword = null;
	private String network = null;
	private String addressSpace = null;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	public String getImageSize() {
		return imageSize;
	}
	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}
	
	public String getDiskSize() {
		return diskSize;
	}
	
	public void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}
	
	public String getAdminUser() {
		return adminUser;
	}
	
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}
	
	public String getAdminUserPassword() {
		return adminUserPassword;
	}
	
	public void setAdminUserPassword(String adminUserPassword) {
		this.adminUserPassword = adminUserPassword;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getAddressSpace() {
		return addressSpace;
	}

	public void setAddressSpace(String addressSpace) {
		this.addressSpace = addressSpace;
	}

}
