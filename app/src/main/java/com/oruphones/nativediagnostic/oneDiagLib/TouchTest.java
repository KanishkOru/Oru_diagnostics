package com.oruphones.nativediagnostic.oneDiagLib;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TouchTest extends View implements ITimerListener {
    public static final int TYPE_TOUCH_TEST_FINGER = 1;
    public static final int TYPE_TOUCH_TEST_SPEN = 2;
    public static final int TYPE_TOUCH_TEST_TSP_HOVERING = 3;
    public static final int TYPE_TOUCH_TEST_SPEN_HOVERING = 4;
    public static final int SQUARE_PLUS_PATTERN = 1;
    public static final int SQUARE_STAR_PATTERN = 2;
    public static final int FULL_SCREEN_PATTERN = 3;
    public static final int DOCOMO_FULL_SCREEN_PATTERN = 4;
    private static String TAG = TouchTest.class.getSimpleName();
    private Context context;
    private Paint gridPaint;
    private float boxWidth;
    private float boxHeight;
    private int touchTestType;
    private int touchTestPattern;
    private float mPreTouchedX = 0.0F;
    private float mPreTouchedY = 0.0F;
    private float mTouchedX = 0.0F;
    private float mTouchedY = 0.0F;
    private boolean isTouchDown;
    private Paint mClickPaint;
    private Paint mLinePaint;
    private Canvas drawingCanvas;
    private Bitmap canvasBitMap;
    private ArrayList<RectF> mXMatrixList;
    private ArrayList<Boolean> clickedStausArrayList;
    private HashMap<Integer, Integer> doubleClickMap;
    private int screenWidth;
    private int screenHeight;
    private Canvas mLineCanvas;
    private Bitmap mLineBitmap;
    private int defaultAirViewMasterOnOffMode;
    private int defaultFingerAirViewMode;
    private int defaultAirViewMode;
    private int numberOfTaps = 2;
    private boolean isFinish;
    private Paint mArrowsPaint;
    private org.pervacio.onediaglib.diagtests.DiagTimer diagTimer = null;
    private boolean isTripleTouchEnabled = true;
    private float rectWidth;
    private float rectHeight;
    private float mScreenHeight;
    private float mScreenWidth;
    private float RECT_WIDTH = 30.0F;
    private float RECT_HEIGHT = 30.0F;
    private HashMap<String, Integer> mEdgeTrackerMap;
    private boolean lineDrawingSetter = true;
    private int mDoubleClickMapSetterKey = 0;
    private String mEdgetrackerMapSetterKey = null;
    private boolean mFullScreen = false;
    private Drawable background;
    public boolean mTripleClickPerformed = false;
    CountDownTimer countDownTimer;
    private TestListener mTestFinishListener;

    public boolean ismTripleClickPerformed() {
        return this.mTripleClickPerformed;
    }

    public void setmTripleClickPerformed(boolean mTripleClickPerformed) {
        this.mTripleClickPerformed = mTripleClickPerformed;
    }

    public boolean isFinish() {
        return this.isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public void setCountDownTimer() {
        final Context context = this.getContext();
        View Activityview = this.getRootView();
        final String[] countValue = new String[]{""};
        this.countDownTimer = (new CountDownTimer(31000L, 1000L) {
            public void onTick(long millisUntilFinished) {
                countValue[0] = millisUntilFinished / 1000L + "";
                DLog.d(TAG, millisUntilFinished / 1000L + "");
                //View view = (LinearLayout) findViewById(R.id.TouchTest);
                // TODO work on Toast instead of snackbar
              Snackbar snackbar =  Snackbar.make(Activityview,"Timeout in : "+ countValue[0],Snackbar.LENGTH_SHORT);
              snackbar.show();
               // final Toast toast = Toast.makeText(context, "Timeout in : " + countValue[0], Toast.LENGTH_SHORT);
               // toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        snackbar.dismiss();
                    }
                }, 1000L);
            }

            public void onFinish() {
            }
        }).start();
    }

    public void stopCountTimer(){
        DLog.d(TAG, "Stop Timer Touch called");
        this.countDownTimer.cancel();
    }

    @SuppressLint({"NewApi"})
    private void init(int touchTestType, int touchTestPattern, int columnCount, int rowCount) {
        this.context = OruApplication.getAppContext();
        this.touchTestType = touchTestType;
        this.touchTestPattern = touchTestPattern;
        WindowManager wm = (WindowManager)this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        this.screenWidth = display.getWidth();
        this.screenHeight = display.getHeight();
        if (VERSION.SDK_INT < 13) {
            this.screenWidth = display.getWidth();
            this.screenHeight = display.getHeight();
        } else {
            Point size = new Point();
            if (this.mFullScreen && VERSION.SDK_INT >= 17) {
                display.getRealSize(size);
            } else {
                display.getSize(size);
            }

            this.screenWidth = size.x;
            this.screenHeight = size.y;
        }

        this.mLineBitmap = Bitmap.createBitmap(this.screenWidth, this.screenHeight, Config.ARGB_8888);
        this.mLineCanvas = new Canvas(this.mLineBitmap);
        this.canvasBitMap = Bitmap.createBitmap(this.screenWidth, this.screenHeight, Config.ARGB_8888);
        this.drawingCanvas = new Canvas(this.canvasBitMap);
        this.gridPaint = new Paint();
        this.gridPaint.setColor(-16777216);
        this.gridPaint.setStyle(Style.STROKE);
        this.boxWidth = (float)this.screenWidth / (float)columnCount;
        this.boxHeight = (float)this.screenHeight / (float)rowCount;
        this.mClickPaint = new Paint();
        this.mClickPaint.setAntiAlias(false);
        this.mClickPaint.setStyle(Style.FILL);
        this.mClickPaint.setColor(Color.parseColor("#76EE00"));
        this.mXMatrixList = new ArrayList();
        this.clickedStausArrayList = new ArrayList();
        this.doubleClickMap = new HashMap();
        this.mLinePaint = new Paint();
        this.mLinePaint.setAntiAlias(true);
        this.mLinePaint.setDither(true);
        this.mLinePaint.setColor(-16777216);
        this.mLinePaint.setStyle(Style.STROKE);
        this.mLinePaint.setStrokeJoin(Join.ROUND);
        this.mLinePaint.setStrokeCap(Cap.SQUARE);
        this.mLinePaint.setStrokeWidth(5.0F);
        float[] arrayOfFloat = new float[]{5.0F, 5.0F};
        DashPathEffect localDashPathEffect = new DashPathEffect(arrayOfFloat, 1.0F);
        this.mLinePaint.setPathEffect(localDashPathEffect);
        this.mArrowsPaint = new Paint();
        this.mArrowsPaint.setStyle(Style.STROKE);
        this.mArrowsPaint.setStrokeWidth(2.0F);
        this.mArrowsPaint.setColor(-65536);
        switch (touchTestPattern) {
            case 1:
                this.drawSquarePlusPatter();
                break;
            case 2:
                this.drawSquareXPattern();
                break;
            case 3:
                this.drawFullViewPattern();
                break;
            case 4:
                this.drawDocomoFullViewPattern(columnCount, rowCount);
        }

        this.getDefaultAirViewModes();
        this.diagTimer = new org.pervacio.onediaglib.diagtests.DiagTimer(this);
        this.diagTimer.startTimer(DiagTimer.MANUALTEST_TIMEOUT);
        float density = this.getContext().getResources().getDisplayMetrics().density;
        density /= 1.5F;
        this.RECT_WIDTH *= density;
        this.RECT_HEIGHT *= density;
        this.mEdgeTrackerMap = new HashMap();
        this.setCountDownTimer();
    }

    public TouchTest(int touchTestType, int touchTestPattern) {
        super(OruApplication.getAppContext());
        this.init(touchTestType, touchTestPattern, 13, 19);
    }

    public TouchTest(int touchTestType, int touchTestPattern, int mColumnCount, int mRowCount) {
        super(OruApplication.getAppContext());
        this.init(touchTestType, touchTestPattern, mColumnCount, mRowCount);
    }

    public TouchTest(int touchTestType, int touchTestPattern, boolean isFullScreenMode) {
        super(OruApplication.getAppContext());
        this.mFullScreen = isFullScreenMode;
        this.init(touchTestType, touchTestPattern, 13, 19);
    }

    public TouchTest(int touchTestType, int touchTestPattern, int mColumnCount, int mRowCount, boolean isFullScreenMode) {
        super(OruApplication.getAppContext());
        this.mFullScreen = isFullScreenMode;
        this.init(touchTestType, touchTestPattern, mColumnCount, mRowCount);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(-1);
        canvas.drawBitmap(this.canvasBitMap, 0.0F, 0.0F, (Paint)null);
        canvas.drawBitmap(this.mLineBitmap, 0.0F, 0.0F, (Paint)null);
        if (this.lineDrawingSetter) {
            canvas.drawPath(this.drawArrows(), this.mArrowsPaint);
        }

    }

    private Path drawArrows() {
        float boxHeight = (float)this.screenHeight / 19.0F;
        float boxWidth = (float)this.screenWidth / 11.0F;
        Path arrowsPath = new Path();
        arrowsPath.moveTo(10.0F, boxHeight / 2.0F);
        arrowsPath.lineTo(3.0F * boxWidth - (boxWidth - 2.0F), boxHeight / 2.0F);
        arrowsPath.moveTo(3.0F * boxWidth - (boxWidth - 2.0F) - 20.0F, boxHeight / 2.0F - 10.0F);
        arrowsPath.lineTo(3.0F * boxWidth - (boxWidth - 2.0F), boxHeight / 2.0F);
        arrowsPath.moveTo(3.0F * boxWidth - (boxWidth - 2.0F) - 20.0F, boxHeight / 2.0F + 10.0F);
        arrowsPath.lineTo(3.0F * boxWidth - (boxWidth - 2.0F), boxHeight / 2.0F);
        arrowsPath.moveTo((float)this.screenWidth - 2.0F * boxWidth, boxHeight / 2.0F);
        arrowsPath.lineTo((float)this.screenWidth - 20.0F, boxHeight / 2.0F);
        arrowsPath.moveTo((float)this.screenWidth - 20.0F, boxHeight / 2.0F);
        arrowsPath.lineTo((float)this.screenWidth - 20.0F, 2.0F * boxHeight);
        arrowsPath.moveTo((float)this.screenWidth - 10.0F, 2.0F * boxHeight - 15.0F);
        arrowsPath.lineTo((float)this.screenWidth - 20.0F, 2.0F * boxHeight);
        arrowsPath.moveTo((float)this.screenWidth - 30.0F, 2.0F * boxHeight - 15.0F);
        arrowsPath.lineTo((float)this.screenWidth - 20.0F, 2.0F * boxHeight);
        if (this.touchTestPattern == 2) {
            arrowsPath.moveTo(3.0F * boxWidth - (boxWidth - 2.0F) - 40.0F, boxHeight + 20.0F);
            arrowsPath.lineTo(2.0F * boxWidth, 3.0F * boxHeight);
            arrowsPath.moveTo(2.0F * boxWidth + 8.0F, 3.0F * boxHeight - 15.0F);
            arrowsPath.lineTo(2.0F * boxWidth, 3.0F * boxHeight);
            arrowsPath.moveTo(2.0F * boxWidth - 15.0F, 3.0F * boxHeight - 10.0F);
            arrowsPath.lineTo(2.0F * boxWidth, 3.0F * boxHeight);
            arrowsPath.moveTo((float)this.screenWidth - 1.5F * boxWidth, 1.5F * boxHeight);
            arrowsPath.lineTo((float)this.screenWidth - 2.0F * boxWidth, 3.0F * boxWidth);
            arrowsPath.moveTo((float)this.screenWidth - 2.0F * boxWidth + 10.0F, 3.0F * boxWidth - 10.0F);
            arrowsPath.lineTo((float)this.screenWidth - 2.0F * boxWidth, 3.0F * boxWidth);
            arrowsPath.moveTo((float)this.screenWidth - 2.0F * boxWidth - 10.0F, 3.0F * boxWidth - 10.0F);
            arrowsPath.lineTo((float)this.screenWidth - 2.0F * boxWidth, 3.0F * boxWidth);
        }

        arrowsPath.moveTo((float)this.screenWidth - boxWidth / 2.0F, (float)this.screenHeight - 1.5F * boxHeight);
        arrowsPath.lineTo((float)this.screenWidth - boxWidth / 2.0F, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.moveTo((float)this.screenWidth - boxWidth / 2.0F, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.lineTo((float)this.screenWidth - 2.0F * boxHeight, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.moveTo((float)this.screenWidth - boxWidth / 2.0F - 60.0F, (float)this.screenHeight - boxHeight / 2.0F - 20.0F);
        arrowsPath.lineTo((float)this.screenWidth - 2.0F * boxHeight, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.moveTo((float)this.screenWidth - boxWidth / 2.0F - 60.0F, (float)this.screenHeight - boxHeight / 2.0F + 20.0F);
        arrowsPath.lineTo((float)this.screenWidth - 2.0F * boxHeight, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.moveTo(boxWidth / 2.0F, (float)this.screenHeight - 1.5F * boxHeight);
        arrowsPath.lineTo(boxWidth / 2.0F, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.moveTo(boxWidth / 2.0F, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.lineTo(2.0F * boxHeight, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.moveTo(2.0F * boxHeight - 20.0F, (float)this.screenHeight - boxHeight / 2.0F - 20.0F);
        arrowsPath.lineTo(2.0F * boxHeight, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.moveTo(2.0F * boxHeight - 20.0F, (float)this.screenHeight - boxHeight / 2.0F + 20.0F);
        arrowsPath.lineTo(2.0F * boxHeight, (float)this.screenHeight - boxHeight / 2.0F);
        arrowsPath.close();
        return arrowsPath;
    }

    public void setTestFinishListener(TestListener mTestFinishListener) {
        this.mTestFinishListener = mTestFinishListener;
    }

    public void setTouchBoxColor(int color) {
        this.mClickPaint.setColor(color);
    }

    @RequiresApi(
            api = 29
    )
    public void setTouchBoxBg(Bitmap color) {
    }

    public void setTrippleTouchable(boolean status) {
        this.isTripleTouchEnabled = status;
    }

    private int getClickedCount(int position) {
        return (Integer)this.doubleClickMap.get(position);
    }

    private void setClickedPosition(int position) {
        this.doubleClickMap.put(position, this.getClickedCount(position) + 1);
    }

    private boolean isTripleClick(int position) {
        return (Integer)this.doubleClickMap.get(position) >= this.numberOfTaps;
    }

    public void setNumberOfTaps(int numberOfTaps) {
        this.numberOfTaps = numberOfTaps;
    }

    private void drawTopView() {
        for(int i = 1; i < 12; ++i) {
            RectF centerRect;
            if (this.touchTestType == 3) {
                centerRect = new RectF((0.0F + (float)i) * this.boxWidth, 1.0F * this.boxHeight, (1.0F + (float)i) * this.boxWidth, 2.0F * this.boxHeight);
            } else {
                centerRect = new RectF((0.0F + (float)i) * this.boxWidth, 0.0F * this.boxHeight, (1.0F + (float)i) * this.boxWidth, 1.0F * this.boxHeight);
            }

            this.mXMatrixList.add(centerRect);
            this.clickedStausArrayList.add(false);
            this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
            this.drawingCanvas.drawRect(centerRect, this.gridPaint);
        }

    }

    private void drawSquareXPattern() {
        this.drawBottomView();
        this.drawTopView();
        this.drawLeftSideView();
        this.drawRightSideView();
        this.drawX1();
        this.drawX2();
    }

    private void drawSquareDiamondPattern() {
        this.drawBottomView();
        this.drawTopView();
        this.drawLeftSideView();
        this.drawRightSideView();
    }

    private void drawSquarePlusPatter() {
        this.drawBottomView();
        this.drawTopView();
        this.drawLeftSideView();
        this.drawRightSideView();
        this.drawPlus1();
        this.drawPlus2();
    }

    private void drawBottomView() {
        for(int i = 1; i < 12; ++i) {
            RectF centerRect;
            if (this.touchTestType == 3) {
                centerRect = new RectF((0.0F + (float)i) * this.boxWidth, 17.0F * this.boxHeight, (1.0F + (float)i) * this.boxWidth, 18.0F * this.boxHeight);
            } else {
                centerRect = new RectF((0.0F + (float)i) * this.boxWidth, 18.0F * this.boxHeight, (1.0F + (float)i) * this.boxWidth, 19.0F * this.boxHeight);
            }

            this.mXMatrixList.add(centerRect);
            this.clickedStausArrayList.add(false);
            this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
            this.drawingCanvas.drawRect(centerRect, this.gridPaint);
        }

    }

    private void drawLeftSideView() {
        int i;
        RectF centerRect;
        if (this.touchTestType == 3) {
            for(i = 1; i < 18; ++i) {
                centerRect = new RectF(1.0F * this.boxWidth, (0.0F + (float)i) * this.boxHeight, 2.0F * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        } else {
            for(i = 0; i < 19; ++i) {
                centerRect = new RectF(0.0F * this.boxWidth, (0.0F + (float)i) * this.boxHeight, 1.0F * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        }

    }

    private void drawRightSideView() {
        int i;
        RectF centerRect;
        if (this.touchTestType == 3) {
            for(i = 1; i < 18; ++i) {
                centerRect = new RectF(11.0F * this.boxWidth, (0.0F + (float)i) * this.boxHeight, 12.0F * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        } else {
            for(i = 0; i < 19; ++i) {
                centerRect = new RectF(12.0F * this.boxWidth, (0.0F + (float)i) * this.boxHeight, 13.0F * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        }

    }

    private void drawX1() {
        float incrementalValue;
        int i;
        RectF centerRect;
        if (this.touchTestType == 3) {
            incrementalValue = 2.0F;

            for(i = 2; i < 17; ++i) {
                centerRect = new RectF((0.0F + incrementalValue) * this.boxWidth, (0.0F + (float)i) * this.boxHeight, (1.0F + incrementalValue) * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
                incrementalValue += 0.57F;
                this.mXMatrixList.add(centerRect);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.clickedStausArrayList.add(false);
            }
        } else {
            incrementalValue = 1.0F;

            for(i = 1; i < 18; ++i) {
                centerRect = new RectF((0.0F + incrementalValue) * this.boxWidth, (0.0F + (float)i) * this.boxHeight, (1.0F + incrementalValue) * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
                incrementalValue += 0.625F;
                this.mXMatrixList.add(centerRect);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.clickedStausArrayList.add(false);
            }
        }

    }

    private void drawX2() {
        float incrementalValue;
        int i;
        RectF centerRect;
        if (this.touchTestType == 3) {
            incrementalValue = 2.0F;

            for(i = 2; i < 17; ++i) {
                centerRect = new RectF((0.0F + incrementalValue) * this.boxWidth, (18.0F - (float)i) * this.boxHeight, (1.0F + incrementalValue) * this.boxWidth, (19.0F - (float)i) * this.boxHeight);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
                incrementalValue += 0.57F;
                this.mXMatrixList.add(centerRect);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.clickedStausArrayList.add(false);
            }
        } else {
            incrementalValue = 1.0F;

            for(i = 1; i < 18; ++i) {
                centerRect = new RectF((0.0F + incrementalValue) * this.boxWidth, (18.0F - (float)i) * this.boxHeight, (1.0F + incrementalValue) * this.boxWidth, (19.0F - (float)i) * this.boxHeight);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
                incrementalValue += 0.625F;
                this.mXMatrixList.add(centerRect);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.clickedStausArrayList.add(false);
            }
        }

    }

    private void drawPlus1() {
        int i;
        RectF centerRect;
        if (this.touchTestType == 3) {
            for(i = 1; i < 18; ++i) {
                centerRect = new RectF(6.0F * this.boxWidth, (0.0F + (float)i) * this.boxHeight, 7.0F * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        } else {
            for(i = 0; i < 19; ++i) {
                centerRect = new RectF(6.0F * this.boxWidth, (0.0F + (float)i) * this.boxHeight, 7.0F * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        }

    }

    private void drawPlus2() {
        for(int i = 1; i < 12; ++i) {
            RectF centerRect;
            if (this.touchTestType == 3) {
                centerRect = new RectF((0.0F + (float)i) * this.boxWidth, 9.0F * this.boxHeight, (1.0F + (float)i) * this.boxWidth, 10.0F * this.boxHeight);
            } else {
                centerRect = new RectF((0.0F + (float)i) * this.boxWidth, 9.0F * this.boxHeight, (1.0F + (float)i) * this.boxWidth, 10.0F * this.boxHeight);
            }

            this.mXMatrixList.add(centerRect);
            this.clickedStausArrayList.add(false);
            this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
            this.drawingCanvas.drawRect(centerRect, this.gridPaint);
        }

    }

    private void drawDiamondLeftBottom() {
        float incrementalValue = 1.0F;

        for(int i = 1; i < 10; ++i) {
            RectF centerRect = new RectF((0.0F + incrementalValue) * this.boxWidth, (8.0F + (float)i) * this.boxHeight, (1.0F + incrementalValue) * this.boxWidth, (9.0F + (float)i) * this.boxHeight);
            this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            incrementalValue += 0.625F;
            this.mXMatrixList.add(centerRect);
            this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
            this.clickedStausArrayList.add(false);
        }

    }

    private void drawDiamondLeftTop() {
        float incrementalValue = 1.0F;

        for(int i = 1; i < 10; ++i) {
            RectF centerRect = new RectF((0.0F + incrementalValue) * this.boxWidth, (10.0F - (float)i) * this.boxHeight, (1.0F + incrementalValue) * this.boxWidth, (11.0F - (float)i) * this.boxHeight);
            this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            incrementalValue += 0.625F;
            this.mXMatrixList.add(centerRect);
            this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
            this.clickedStausArrayList.add(false);
        }

    }

    private void drawDiamondRightTop() {
    }

    private void drawDiamondRightBottom() {
    }

    Boolean startTimer = true;

    @TargetApi(14)
    public boolean onTouchEvent(MotionEvent event) {
      //  this.diagTimer.restartTimer(org.pervacio.onediaglib.diagtests.DiagTimer.MANUALTEST_TIMEOUT);
      //  this.countDownTimer.cancel();

//        if (startTimer){
//            this.setCountDownTimer();
//            startTimer = false;
//        }


        if (event.getToolType(0) == 0) {
            return false;
        } else if (this.isFinish()) {
            return false;
        } else if (this.ismTripleClickPerformed()) {
            return false;
        } else if (this.isEventFromSpen(event) && this.touchTestType == 1) {
            return false;
        } else if (this.isEventFromFinger(event) && this.touchTestType == 2) {
            return false;
        } else {
            return this.touchTestType != 4 && this.touchTestType != 3 ? this.perFormTouchFunctionality(event) : false;
        }
    }

    public boolean onHoverEvent(MotionEvent event) {
       // this.diagTimer.restartTimer(org.pervacio.onediaglib.diagtests.DiagTimer.MANUALTEST_TIMEOUT);
//        this.countDownTimer.cancel();
//        this.setCountDownTimer();
        if (this.isFinish()) {
            return false;
        } else if (this.isEventFromSpen(event) && this.touchTestType == 3) {
            return false;
        } else if (this.isEventFromFinger(event) && this.touchTestType == 4) {
            return false;
        } else {
            return this.touchTestType != 1 && this.touchTestType != 2 ? this.perFormHoverFunctionality(event) : false;
        }
    }

    @TargetApi(14)
    private boolean isEventFromSpen(MotionEvent event) {
        if (VERSION.SDK_INT < 14) {
            return false;
        } else {
            return event != null && event.getToolType(0) == 2;
        }
    }

    @TargetApi(14)
    private boolean isEventFromFinger(MotionEvent event) {
        if (VERSION.SDK_INT < 14) {
            return false;
        } else {
            return event != null && event.getToolType(0) == 1;
        }
    }

    private boolean perFormHoverFunctionality(MotionEvent event) {
        switch (event.getAction()) {
            case 7:
                if (this.isTouchDown) {
                    for(int i = 0; i < event.getHistorySize(); ++i) {
                        this.mPreTouchedX = this.mTouchedX;
                        this.mPreTouchedY = this.mTouchedY;
                        this.mTouchedX = event.getHistoricalX(i);
                        this.mTouchedY = event.getHistoricalY(i);
                        this.drawRect(this.mTouchedX, this.mTouchedY, this.mClickPaint);
                        this.drawLine(this.mPreTouchedX, this.mPreTouchedY, this.mTouchedX, this.mTouchedY);
                    }

                    this.mPreTouchedX = this.mTouchedX;
                    this.mPreTouchedY = this.mTouchedY;
                    this.mTouchedX = event.getX();
                    this.mTouchedY = event.getY();
                    this.drawRect(this.mTouchedX, this.mTouchedY, this.mClickPaint);
                    this.drawLine(this.mPreTouchedX, this.mPreTouchedY, this.mTouchedX, this.mTouchedY);
                    this.isTouchDown = true;
                    return true;
                }
            case 8:
            default:
                break;
            case 9:
                this.mTouchedX = event.getX();
                this.mTouchedY = event.getY();
                this.drawRect(this.mTouchedX, this.mTouchedY, this.mClickPaint);
                this.isTouchDown = true;
                return true;
            case 10:
                if (this.isTouchDown) {
                    this.mPreTouchedX = this.mTouchedX;
                    this.mPreTouchedY = this.mTouchedY;
                    this.mTouchedX = event.getX();
                    this.mTouchedY = event.getY();
                    if (this.mPreTouchedX == this.mTouchedX && this.mPreTouchedY == this.mTouchedX) {
                        this.drawPoint(this.mTouchedX, this.mTouchedY);
                    }

                    this.isTouchDown = false;
                    return true;
                }
        }

        return false;
    }

    private boolean perFormTouchFunctionality(MotionEvent event) {
        int index;
        switch (event.getAction()) {
            case 0:
                this.mTouchedX = event.getX();
                this.mTouchedY = event.getY();
                int m = (int)(this.mTouchedX / this.rectWidth);
                int n = (int)(this.mTouchedY / this.rectHeight);
                StringBuilder mBuilder = new StringBuilder();
                mBuilder.append(n);
                mBuilder.append(m);
                if (this.mEdgetrackerMapSetterKey != null && !mBuilder.toString().equals(this.mEdgetrackerMapSetterKey)) {
                    this.mEdgeTrackerMap.put(this.mEdgetrackerMapSetterKey, (Integer) null);
                }

                this.drawRect(this.mTouchedX, this.mTouchedY, this.mClickPaint);
                this.isTouchDown = true;
                return true;
            case 1:
                if (this.isTouchDown) {
                    this.mPreTouchedX = this.mTouchedX;
                    this.mPreTouchedY = this.mTouchedY;
                    this.mTouchedX = event.getX();
                    this.mTouchedY = event.getY();
                    if (this.mPreTouchedX == this.mTouchedX && this.mPreTouchedY == this.mTouchedX) {
                        this.drawPoint(this.mTouchedX, this.mTouchedY);
                    }

                    this.isTouchDown = false;

                    for(index = 0; index < this.mXMatrixList.size(); ++index) {
                        RectF currentRect = (RectF)this.mXMatrixList.get(index);
                        if (currentRect.contains(this.mTouchedX, this.mTouchedY)) {
                            this.setClickedPosition(index);
                            this.mDoubleClickMapSetterKey = index;
                        }
                    }

                    index = (int)(this.mTouchedX / this.rectWidth);
                    int j = (int)(this.mTouchedY / this.rectHeight);
                    StringBuilder builder = new StringBuilder();
                    builder.append(j);
                    builder.append(index);
                    if (this.mEdgeTrackerMap.get(builder.toString()) != null) {
                        int temp = (Integer)this.mEdgeTrackerMap.get(builder.toString());
                        ++temp;
                        this.mEdgeTrackerMap.put(builder.toString(), temp);
                    } else {
                        this.mEdgeTrackerMap.put(builder.toString(), 0);
                    }

                    this.mEdgetrackerMapSetterKey = builder.toString();
                    this.traverseTrippleClickMap();
                    return true;
                }
                break;
            case 2:
                if (this.isTouchDown) {
                    for(index = 0; index < event.getHistorySize(); ++index) {
                        this.mPreTouchedX = this.mTouchedX;
                        this.mPreTouchedY = this.mTouchedY;
                        this.mTouchedX = event.getHistoricalX(index);
                        this.mTouchedY = event.getHistoricalY(index);
                        this.drawRect(this.mTouchedX, this.mTouchedY, this.mClickPaint);
                        this.drawLine(this.mPreTouchedX, this.mPreTouchedY, this.mTouchedX, this.mTouchedY);
                    }

                    this.mPreTouchedX = this.mTouchedX;
                    this.mPreTouchedY = this.mTouchedY;
                    this.mTouchedX = event.getX();
                    this.mTouchedY = event.getY();
                    this.drawRect(this.mTouchedX, this.mTouchedY, this.mClickPaint);
                    this.drawLine(this.mPreTouchedX, this.mPreTouchedY, this.mTouchedX, this.mTouchedY);
                    this.isTouchDown = true;
                    return true;
                }
        }

        return false;
    }

    public void lineDrawingStatusSetter(boolean status) {
        this.lineDrawingSetter = status;
    }

    private void drawLine(float PreTouchedX, float PreTouchedY, float TouchedX, float TouchedY) {
        if (this.lineDrawingSetter) {
            this.mLineCanvas.drawLine(PreTouchedX, PreTouchedY, TouchedX, TouchedY, this.mLinePaint);
            int i = 0;
            int j = 0;
//            boolean k = false;
//            boolean m = false;
            if (PreTouchedX >= TouchedX) {
                i = (int)PreTouchedX;
                j = (int)TouchedX;
            }

            int k;
            int m;
            if (PreTouchedY < TouchedY) {
                k = (int)TouchedY;
                m = (int)PreTouchedY;
            } else {
                k = (int)PreTouchedY;
                m = (int)TouchedY;
            }

            this.invalidate(new Rect(j - 6, m - 6, i + 6, k + 6));
        }

    }

    private void drawPoint(float TouchedX, float TouchedY) {
        if (this.lineDrawingSetter) {
            this.drawingCanvas.drawPoint(TouchedX, TouchedY, this.mLinePaint);
            this.invalidate(new Rect(-6 + (int)TouchedX, -6 + (int)TouchedY, 6 + (int)TouchedX, 6 + (int)TouchedY));
        }

    }

    private void drawRect(float currentX, float currentY, Paint paramPaint) {
        float boxHeight = (float)this.screenHeight / 19.0F;
        float boxWidth = (float)this.screenWidth / 11.0F;
        Paint xPatternPaint = new Paint();
        xPatternPaint.setColor(-16777216);
        xPatternPaint.setStyle(Style.STROKE);
        int i = (int)(currentX / boxWidth);
        int j = (int)(currentY / boxHeight);
        float f3 = boxWidth * (float)i;
        float f4 = boxHeight * (float)j;
        if (j <= 18 && i <= 10) {
            for(int index = 0; index < this.mXMatrixList.size(); ++index) {
                RectF currentRect = (RectF)this.mXMatrixList.get(index);
                if (currentRect.contains(currentX, currentY)) {
                    this.drawingCanvas.drawRect(currentRect, paramPaint);
                    this.clickedStausArrayList.remove(index);
                    this.clickedStausArrayList.add(index, true);
                    Rect dstRect = new Rect();
                    currentRect.round(dstRect);
                    this.drawingCanvas.drawRect(currentRect, xPatternPaint);
                    this.invalidate(dstRect);
                    TestResult result;
                    if (this.isTripleTouchEnabled && this.isTripleClick(index) && !this.isFinish() && !this.ismTripleClickPerformed()) {
                        result = new TestResult();
                        result.setResultCode(5);
                        result.setResultDescription("Triple touch performed");
                        if (this.mTestFinishListener != null) {
                            if (this.diagTimer != null) {
//                                this.diagTimer.stopTimer();
//                                this.countDownTimer.cancel();
//                                this.countDownTimer.start();
                            }

                            this.mTestFinishListener.onTestEnd(result);
                            this.setmTripleClickPerformed(true);
                            this.clearClickedMap();
                        }
                    }

                    if (this.mDoubleClickMapSetterKey != index) {
                        this.doubleClickMap.put(this.mDoubleClickMapSetterKey, 0);
                    }

                    if (this.isTouchTestPass() && !this.isFinish()) {
                        if (this.diagTimer != null) {
                            this.diagTimer.stopTimer();
                            this.countDownTimer.cancel();
                        }

                        result = new TestResult();
                        result.setResultCode(0);
                        if (this.mTestFinishListener != null) {
                            this.setIsFinish(true);
                            this.mTestFinishListener.onTestEnd(result);
                        }
                    }
                }
            }

            this.invalidate(new Rect((int)(f3 - 1.0F), (int)(f4 - 1.0F), (int)(1.0F + f3 + boxWidth), (int)(1.0F + f4 + boxHeight)));
        }
    }

    private boolean isTouchTestPass() {
        for(int i = 0; i < this.clickedStausArrayList.size(); ++i) {
            if (!(Boolean)this.clickedStausArrayList.get(i)) {
                return false;
            }
        }

        return true;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.setDefaultAirViewMode();
        if (this.diagTimer != null) {
            this.diagTimer.stopTimer();
            this.countDownTimer.cancel();
        }

        if (this.mLineBitmap != null) {
            this.mLineBitmap.recycle();
            this.mLineBitmap = null;
        }

        if (this.canvasBitMap != null) {
            this.canvasBitMap.recycle();
            this.canvasBitMap = null;
        }

    }

    public boolean isSpenHoveringSupported() {
        String str = VERSION.RELEASE;
        if (this.context == null) {
            return false;
        } else if (!this.isSpenSupported()) {
            return false;
        } else {
            PackageManager localPackageManager = this.context.getPackageManager();
            return localPackageManager != null ? localPackageManager.hasSystemFeature("com.sec.feature.hovering_ui") : false;
        }
    }

    public boolean isSpenSupported() {
        File path = new File("/sys/class/sec/sec_epen");
        return path.isDirectory();
    }

    public boolean isTspHoverSupported() {
        String phoneModel = Build.MODEL;
        String deviceManufacturer = Build.MANUFACTURER;
        int version = VERSION.SDK_INT;
        if (!deviceManufacturer.equalsIgnoreCase("samsung")) {
            return false;
        } else if (version < 14) {
            return false;
        } else {
            return !phoneModel.equalsIgnoreCase("SGH-T889") && !phoneModel.equalsIgnoreCase("SM-P607T") && !phoneModel.equalsIgnoreCase("SM-N910T") && !phoneModel.equalsIgnoreCase("GT-I9192") && !phoneModel.equalsIgnoreCase("SM-N910V") && !phoneModel.equalsIgnoreCase("SM-N910W8") && !phoneModel.equalsIgnoreCase("SCH-I605") && !phoneModel.equalsIgnoreCase("GT-N7100") && !phoneModel.equalsIgnoreCase("SM-T807P") && !phoneModel.equalsIgnoreCase("SM-G386T") && !phoneModel.equalsIgnoreCase("SM-N915T") && !phoneModel.equalsIgnoreCase("SM-G386T1") && !phoneModel.equalsIgnoreCase("SM-G386W") ? this.hasFeature("com.sec.feature.hovering_ui") : false;
        }
    }

    private boolean hasFeature(String feature) {
        FeatureInfo[] featureInfos = this.context.getPackageManager().getSystemAvailableFeatures();
        FeatureInfo[] var3 = featureInfos;
        int var4 = featureInfos.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            FeatureInfo info = var3[var5];
            if (!TextUtils.isEmpty(info.name) && feature.equalsIgnoreCase(info.name)) {
                return true;
            }
        }

        return false;
    }

    private void getDefaultAirViewModes() {
        if (this.touchTestType == 3) {
            try {
                this.defaultAirViewMasterOnOffMode = System.getInt(this.context.getContentResolver(), "air_view_master_onoff");
                System.putInt(this.context.getContentResolver(), "air_view_master_onoff", 1);
            } catch (Exception var4) {
                DLog.e(TAG, var4.getMessage(), var4);
            }

            try {
                this.defaultFingerAirViewMode = System.getInt(this.context.getContentResolver(), "finger_air_view");
                System.putInt(this.context.getContentResolver(), "finger_air_view", 1);
            } catch (Exception var3) {
                DLog.e(TAG, var3.getMessage(), var3);
            }

            try {
                this.defaultAirViewMode = System.getInt(this.context.getContentResolver(), "air_view_mode");
                System.putInt(this.context.getContentResolver(), "air_view_mode", 2);
            } catch (Exception var2) {
                DLog.e(TAG, var2.getMessage(), var2);
            }
        }

    }

    private void setDefaultAirViewMode() {
        if (this.touchTestType == 3) {
            try {
                System.putInt(this.context.getContentResolver(), "air_view_master_onoff", this.defaultAirViewMasterOnOffMode);
                System.putInt(this.context.getContentResolver(), "air_view_mode", this.defaultAirViewMode);
                System.putInt(this.context.getContentResolver(), "finger_air_view", this.defaultFingerAirViewMode);
            } catch (Exception var2) {
                DLog.e(TAG, var2.getMessage(), var2);
            }
        }

    }

    public void timeout() {
        TestResult result = new TestResult();
        result.setResultCode(3);
        result.setResultDescription("Time out");
        if (this.mTestFinishListener != null) {
            if (this.diagTimer != null) {
                this.diagTimer.stopTimer();
                this.countDownTimer.cancel();
            }

            this.setIsFinish(true);
            this.mTestFinishListener.onTestEnd(result);
        }

    }

    private void drawFullViewPattern() {
        for(int j = 0; j < 13; ++j) {
            for(int i = 0; i < 19; ++i) {
                RectF centerRect = new RectF((0.0F + (float)j) * this.boxWidth, (0.0F + (float)i) * this.boxHeight, (1.0F + (float)j) * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        }

    }

    private void drawDocomoFullViewPattern(int columnCount, int rowCount) {
        for(int j = 0; j < columnCount; ++j) {
            for(int i = 0; i < rowCount; ++i) {
                RectF centerRect = new RectF((0.0F + (float)j) * this.boxWidth, (0.0F + (float)i) * this.boxHeight, (1.0F + (float)j) * this.boxWidth, (1.0F + (float)i) * this.boxHeight);
                this.mXMatrixList.add(centerRect);
                this.clickedStausArrayList.add(false);
                this.doubleClickMap.put(this.mXMatrixList.size() - 1, 0);
                this.drawingCanvas.drawRect(centerRect, this.gridPaint);
            }
        }

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mScreenWidth = (float)(right - left);
        this.mScreenHeight = (float)(bottom - top);
        this.initRectHeightWidth();
    }

    private void initRectHeightWidth() {
        int numHorizontalRects = (int)(this.mScreenWidth / this.RECT_WIDTH);
        this.rectWidth = this.mScreenWidth / (float)numHorizontalRects;
        int numVerticalRects = (int)(this.mScreenHeight / this.RECT_HEIGHT);
        this.rectHeight = this.mScreenHeight / (float)numVerticalRects;
    }

    private void traverseTrippleClickMap() {
        int numVerticalRects = (int)(this.mScreenHeight / this.rectHeight);
        int numHorizontalRects = (int)(this.mScreenWidth / this.rectWidth);

        for(int i = 0; i < numVerticalRects; ++i) {
            for(int j = 0; j < numHorizontalRects; ++j) {
                StringBuilder keyBuilder = new StringBuilder();
                keyBuilder.append(i);
                keyBuilder.append(j);
                if (this.mEdgeTrackerMap.get(keyBuilder.toString()) != null && (Integer)this.mEdgeTrackerMap.get(keyBuilder.toString()) == this.numberOfTaps && this.isTripleTouchEnabled) {
                    DLog.d(TAG, "TRIPPLE CLICK KEY " + keyBuilder.toString() + " COUNT " + this.mEdgeTrackerMap.get(keyBuilder.toString()));
                    TestResult result = new TestResult();
                    result.setResultCode(5);
                    result.setResultDescription("Triple touch performed");
                    if (this.mTestFinishListener != null) {
                        if (this.diagTimer != null) {
                            this.diagTimer.stopTimer();
                            this.countDownTimer.cancel();
                        }

                        this.mTestFinishListener.onTestEnd(result);
                        this.setmTripleClickPerformed(true);
                        this.clearClickedMap();
                    }
                }
            }
        }

    }

    private void clearClickedMap() {
        this.mEdgeTrackerMap.clear();
        Iterator var1 = this.doubleClickMap.keySet().iterator();

        while(var1.hasNext()) {
            int key = (Integer)var1.next();
            int clickedCount = this.getClickedCount(key);
            if (clickedCount == 0) {
                this.doubleClickMap.put(key, 0);
            } else {
                this.doubleClickMap.put(key, 1);
            }
        }

    }

    public void resumeTest() {
//        if (null != this.diagTimer) {
//            this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
//            this.countDownTimer.cancel();
//            this.setCountDownTimer();
//        }

        this.setIsFinish(false);
        this.setmTripleClickPerformed(false);
    }
}
