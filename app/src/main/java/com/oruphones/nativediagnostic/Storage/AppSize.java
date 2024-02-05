package com.oruphones.nativediagnostic.Storage;

public class AppSize {

	public long codeSize = 0;
	public long dataSize = 0;
	public long cacheSize = 0;

	public AppSize(long codeSize, long dataSize, long cacheSize) {
		this.codeSize = codeSize;
		this.dataSize = dataSize;
		this.cacheSize = cacheSize;
	}

	public long getCodeSize() {
		return codeSize;
	}

	public void setCodeSize(long codeSize) {
		this.codeSize = codeSize;
	}

	public long getDataSize() {
		return dataSize;
	}

	public void setDataSize(long dataSize) {
		this.dataSize = dataSize;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

}
