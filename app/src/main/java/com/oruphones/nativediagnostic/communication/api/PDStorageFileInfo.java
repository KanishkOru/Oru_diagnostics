package com.oruphones.nativediagnostic.communication.api;

public class PDStorageFileInfo {

	public static final String FILE_TYPE_IMAGE = "IMAGE";
	public static final String FILE_TYPE_AUDIO = "AUDIO";
	public static final String FILE_TYPE_VIDEO = "VIDEO";
	public static final String FILE_TYPE_OTHER = "OTHER";

	private String key;
	private String name;
	private long createdDate;
	private long size;
	// IMAGE,VIDEO,AUDIO,OTHER
	private String fileType;
	private String filePath;
	private boolean duplicate;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
	
	

}