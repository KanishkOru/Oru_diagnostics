package com.oruphones.nativediagnostic.Storage;

public class DuplicateFilesInfo {
	
	long aggregateDuplicateFileSize;
	long aggregateDuplicateFileCount;
	long duplicateFileSetCount;
	
	public DuplicateFilesInfo(long aggregateDuplicateFileSize, long aggregateDuplicateFileCount, long duplicateFileSetCount )
	{
		this.aggregateDuplicateFileSize = aggregateDuplicateFileSize;
		this.aggregateDuplicateFileCount = aggregateDuplicateFileCount;
		this.duplicateFileSetCount = duplicateFileSetCount;
	}

}
