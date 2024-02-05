package com.oruphones.nativediagnostic.models;


import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.io.Serializable;

public class SummeryDataSet<T> extends DeviceInfoDataSet implements Serializable {

    T extra;
    public SummeryDataSet(String title, int drawableId, String value) {
        super(title, drawableId, value);
    }
    public SummeryDataSet(String title, int drawableId, String value, String additionalTestInfo) {
        super(title, drawableId, value, additionalTestInfo);
    }
    public SummeryDataSet(@StringRes int titleId, @DrawableRes int drawableId, String value) {
        super(titleId, drawableId, value);
    }

    public SummeryDataSet(@StringRes int titleId, @DrawableRes int drawableId, String value, String additionalTestInfo) {
        super(titleId, drawableId, value, additionalTestInfo);
    }


    public T getExtra() {
        return extra;
    }

    public void setExtra(T extra) {
        this.extra = extra;
    }
}
