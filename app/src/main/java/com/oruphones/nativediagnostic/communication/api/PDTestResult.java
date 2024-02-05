package com.oruphones.nativediagnostic.communication.api;
//Object for parsing test result recieved from device
public class PDTestResult {
	private String name;
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
