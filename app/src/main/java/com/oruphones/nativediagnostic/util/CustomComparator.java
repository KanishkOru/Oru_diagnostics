package com.oruphones.nativediagnostic.util;

import androidx.annotation.NonNull;


import com.oruphones.nativediagnostic.api.AppInfo;
import com.oruphones.nativediagnostic.api.FileInfo;

import java.util.Comparator;

/**
 * Created by surya Polasanapalli on 21/10/2017.
 */
public class CustomComparator implements Comparator<Object> {
    private boolean isAscending = true;
    private SortBy mSortBy = SortBy.NAME;
    public enum SortBy{
        NAME,DATE,SIZE
    }

    public CustomComparator(boolean isAscending) {
        this.isAscending = isAscending;
    }
    public CustomComparator(boolean isAscending, SortBy sortBy) {
        this.isAscending = isAscending;
        this.mSortBy = sortBy;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof AppInfo && o2 instanceof AppInfo) {
            if (isAscending)
                return ((AppInfo) o1).getAppName().toString().compareToIgnoreCase(((AppInfo) o2).getAppName().toString());
            else
                return ((AppInfo) o2).getAppName().toString().compareToIgnoreCase(((AppInfo) o1).getAppName().toString());
        } else if (o1 instanceof FileInfo && o2 instanceof FileInfo) {
            if (isAscending)
                return sortFiles(mSortBy,(FileInfo) o1,(FileInfo) o2);
            else
                return sortFiles(mSortBy,(FileInfo) o2,(FileInfo) o1);
        }
        return 0;
    }

    private int sortFiles(SortBy sortBy, @NonNull  FileInfo fileInfo1, @NonNull FileInfo fileInfo2 ){
        switch (sortBy){
            case DATE:
                return fileInfo1.getCreatedDate().compareToIgnoreCase(fileInfo2.getCreatedDate());
            case SIZE:
                return Double.compare(fileInfo1.getFileSizeInDouble(), fileInfo2.getFileSizeInDouble());
            case NAME:
            default:
                return fileInfo1.getFileName().compareToIgnoreCase(fileInfo2.getFileName());
        }
    }
}
