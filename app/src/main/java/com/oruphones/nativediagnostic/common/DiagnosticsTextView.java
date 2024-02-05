package com.oruphones.nativediagnostic.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.oruphones.nativediagnostic.BaseUnusedActivity;




/**
 * Created by Raghava on 03-21-2018.
 */
public class DiagnosticsTextView extends AppCompatTextView {
    public DiagnosticsTextView(Context context) {
        super(context);
        try {
            init(context,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DiagnosticsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            init(context, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(26)
    public DiagnosticsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        try {


            if(BaseUnusedActivity.isIsAssistedApp()) {
                setEnabled(false);
                setClickable(false);
            }
            if (!isInEditMode()) {
                try {
                    Typeface tf = Typeface.createFromAsset(context.getAssets(),
                            "fonts/aileron_regular.ttf");
                    setTypeface(tf);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
