package com.oruphones.nativediagnostic.models;


import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;


import com.oruphones.nativediagnostic.PervacioApplication;

import java.io.Serializable;

public class DeviceInfoDataSet implements Serializable {
   String title;
   int drawableId;
   String value;
   boolean hideStatus;
   String additionalTestInfo;
   int titleColor;

    public DeviceInfoDataSet(String title, @DrawableRes int drawableId, String value) {
        this.title = title;
        this.drawableId = drawableId;
        this.value = value;
    }

    public DeviceInfoDataSet(String title, @DrawableRes int drawableId, String value, String additionalTestInfo) {
        this.title = title;
        this.drawableId = drawableId;
        this.value = value;
        this.additionalTestInfo = additionalTestInfo;
    }

    public DeviceInfoDataSet(@StringRes int titleId, @DrawableRes int drawableId, String value) {
        this.title = PervacioApplication.getAppContext().getString(titleId);
        this.drawableId = drawableId;
        this.value = value;
    }

    public DeviceInfoDataSet(@StringRes int titleId, @DrawableRes int drawableId, String value, String additionalTestInfo) {
        this.title = PervacioApplication.getAppContext().getString(titleId);
        this.drawableId = drawableId;
        this.value = value;
        this.additionalTestInfo = additionalTestInfo;
    }

    public String getTitle() {
        return title;
    }

    @DrawableRes
    public int getDrawableId() {
        return drawableId;
    }
    public  boolean hasIcon(){
        return drawableId>0;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHideStatus() {
        return hideStatus;
    }

    public void setHideStatus(boolean hideStatus) {
        this.hideStatus = hideStatus;
    }

    public String getAdditionalTestInfo() {
        return additionalTestInfo;
    }
    public void setAdditionalTestInfo(String additionalTestInfo) {
        this.additionalTestInfo = additionalTestInfo;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

}
