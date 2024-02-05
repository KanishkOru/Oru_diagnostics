package com.oruphones.nativediagnostic.oneDiagLib;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;

public class TestDisplay extends View implements View.OnTouchListener, ITimerListener {
    private static String TAG = TestDisplay.class.getSimpleName();
    private LinearLayout layout;
    private int[] colors = new int[]{-1, -12303292, -16777216, -65536, -16711936, -16776961};
    private int colorIdx = 0;
    private Paint paintCanvas = new Paint();
    private TestListener testListener;
    private DiagTimer diagTimer;
    private boolean displayText = false;
    private String mDisplayString = "Tap to proceed ";

    public TestDisplay() {
        super(OruApplication.getAppContext());
        this.paintCanvas.setStyle(Style.FILL);
        this.paintCanvas.setColor(-1);
        this.paintCanvas.setColor(this.colors[0]);
        this.setOnTouchListener(this);
        this.diagTimer = new DiagTimer(this);
        this.diagTimer.startTimer(DiagTimer.MANUALTEST_TIMEOUT);
    }

    public void setDisplayText(boolean displayText) {
        this.displayText = displayText;
    }

    public void setDisplayText(boolean displayText, String displayString) {
        this.displayText = displayText;
        this.mDisplayString = displayString;
    }

    public void setOnTestCompleteListener(TestListener testListener) {
        this.testListener = testListener;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = this.getWidth();
        int y = this.getHeight();
        canvas.drawRect(0.0F, 0.0F, (float)x, (float)y, this.paintCanvas);
        if (this.displayText) {
            this.drawText(canvas, this.paintCanvas, this.mDisplayString);
        }

    }

    private void drawText(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Align.LEFT);
        if (this.paintCanvas.getColor() == -1) {
            paint.setColor(Color.rgb(0, 0, 0));
        } else {
            paint.setColor(Color.rgb(255, 255, 255));
        }

        paint.setTextSize(50.0F);
        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = (float)cWidth / 2.0F - (float)r.width() / 2.0F - (float)r.left;
        float y = (float)cHeight / 2.0F + (float)r.height() / 2.0F - (float)r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    public boolean onTouch(View v, MotionEvent event) {


        if (event.getAction() == 1) {

            this.validateNextAndProcess(v);
            if (this.diagTimer != null) {

               this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
            }
        }

        return true;
    }

    private void validateNextAndProcess(View view) {
        ++this.colorIdx;
        if (this.colorIdx < this.colors.length) {
            DLog.d(TAG, "entered : colors");
            this.paintCanvas.setStyle(Style.FILL);
            this.paintCanvas.setColor(this.colors[this.colorIdx]);
            this.invalidate();
        } else {
            DLog.d("TestDisplay", "Display : Finished test");
            TestResult testResult = new TestResult();
            testResult.setResultCode(8);
            if (this.testListener != null) {
                this.testListener.onTestEnd(testResult);
            }
        }

    }

    public void timeout() {
        TestResult testResult = new TestResult();
        testResult.setResultCode(3);
        if (this.testListener != null) {
            this.testListener.onTestEnd(testResult);
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.diagTimer != null) {
            this.diagTimer.stopTimer();
        }

    }
}

