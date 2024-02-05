 package com.oruphones.nativediagnostic.common;

 import android.content.Context;
 import android.graphics.Typeface;
 import android.util.AttributeSet;

 import androidx.appcompat.widget.AppCompatButton;

 import com.oruphones.nativediagnostic.BaseUnusedActivity;

 /**
 * Created by Satya on 14-Mar-18.
 */

public class CustomButton extends AppCompatButton {
    public CustomButton(Context context) {
        super(context);
        try {
            init(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            init(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "fonts/aileron_regular.ttf");
            setTypeface(tf);


            if(BaseUnusedActivity.isIsAssistedApp()){
                setEnabled(false);
                setVisibility(GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
