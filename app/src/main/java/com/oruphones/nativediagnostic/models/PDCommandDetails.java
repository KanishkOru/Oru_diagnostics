package com.oruphones.nativediagnostic.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PDCommandDetails implements Serializable{

	@SerializedName("serialVersionUID")
	private static final long serialVersionUID = 1L;
	@SerializedName("sessionId")
	private Long sessionId;
	@SerializedName("commandName")
	private String commandName;
	@SerializedName("testStatus")
	private String testStatus;
	@SerializedName("startDateTime")
	private long startDateTime;
	@SerializedName("endDateTime")
	private long endDateTime;
	@SerializedName("message")
	private String message;

public Long getSessionId() {
	return sessionId;
}
public void setSessionId(Long sessionId) {
	this.sessionId = sessionId;
}
public String getCommandName() {
	return commandName;
}
public void setCommandName(String commandName) {
	this.commandName = commandName;
}
public String getTestStatus() {
	return testStatus;
}
public void setTestStatus(String testStatus) {
	this.testStatus = testStatus;
}
public long getStartDateTime() {
	return startDateTime;
}
public void setStartDateTime(long startDateTime) {
	this.startDateTime = startDateTime;
}
public long getEndDateTime() {
	return endDateTime;
}
public void setEndDateTime(long endDateTime) {
	this.endDateTime = endDateTime;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
}
