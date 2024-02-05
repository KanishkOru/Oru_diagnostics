package com.oruphones.nativediagnostic.common;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.oruphones.nativediagnostic.R;


public class NumberSelectView extends LinearLayout {
    Button negativeBtn;
    Button possitiveBtn;
    EditText numEditText;
    int selectedNumber = -1;
    private Context mContext;
    public NumberSelectView(Context context) {
        super(context);
        //initView();
    }

    public NumberSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public NumberSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //initView();
    }

    private void initView() {
        inflate(mContext, R.layout.number_select_layout, this);
        negativeBtn = findViewById(R.id.neg_btn);
        possitiveBtn = findViewById(R.id.pos_btn);
        numEditText = findViewById(R.id.num_edit);
        numEditText.setFocusable(true);
        numEditText.setFocusableInTouchMode(true);
/*        if(BaseActivity.isIsAssistedApp()){
            setEnabled(false);
            setVisibility(GONE);
        }*/
        negativeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int numToDecrease = getSelectedNumber();
                if(numToDecrease > 0){
                    numEditText.setText(String.valueOf(numToDecrease - 1));
                }
            }
        });

        possitiveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int numToIncrease = getSelectedNumber();
                if(numToIncrease < 9){
                    numEditText.setText(String.valueOf(numToIncrease + 1));
                }
            }
        });
    }

    public void addTextChangedListener(TextWatcher textWatcher){
        numEditText.addTextChangedListener(textWatcher);
    }
    public int getSelectedNumber() {
        return Integer.parseInt(numEditText.getText().toString());
    }

    public void reuestForFocus() {
        numEditText.requestFocus();
    }

}