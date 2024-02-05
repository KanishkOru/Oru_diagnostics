package com.oruphones.nativediagnostic.oneDiagLib;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.ScreenTestResults;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;

public class DeadPixelTest extends View implements ITimerListener {
    private static String TAG = DeadPixelTest.class.getSimpleName();
    private static final float STROKE_WIDTH = 5.0F;
    private Paint mPaint;
    private int penColor;
    private int[] screenColors;
    private ScreenTest mCurrentScreenTest;
    private int colorID;
    private float mStartX;
    private float mStartY;
    private Context context;
    private static float RADIUS = 80.0F;
    GestureDetector gestureDetector;
    private TestListener testListener;
    private DiagTimer diagTimer;
    private String imagePath;
    private ScreenTestResults testResults;
    private int savedPoints = 0;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private Stack<CirclePoint> circlePoints;
    CountDownTimer countDownTimer;
    GestureDetector.SimpleOnGestureListener gestureListener;

    public void setCountDownTimer() {
        final Context context = this.getContext();
        final String[] countValue = new String[]{""};
        this.countDownTimer = (new CountDownTimer(30000L, 1000L) {
            public void onTick(long millisUntilFinished) {
                countValue[0] = millisUntilFinished / 1000L + "";
                DLog.d(TAG, millisUntilFinished / 1000L + "");
                final Toast toast = Toast.makeText(context, "Timeout in : " + countValue[0], Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, 1000L);
            }

            public void onFinish() {
            }
        }).start();
    }

    public void stopTimer(){
        DLog.d(TAG, "Timer Stopped called");
        this.countDownTimer.cancel();
    }

    public DeadPixelTest(ScreenTest screenTest) {
        super(OruApplication.getAppContext());
       this.gestureListener = new NamelessClass_1();
        this.context = OruApplication.getAppContext();
        this.mCurrentScreenTest = screenTest;
        if (this.mCurrentScreenTest.equals(ScreenTest.DEADPIXEL)) {
            this.screenColors = new int[]{-16777216, -1};
            RADIUS = 80.0F;
        } else if (this.mCurrentScreenTest.equals(ScreenTest.SCREEN_DISCOLOR)) {
            this.screenColors = new int[]{-1, -7829368};
            RADIUS = 160.0F;
        } else {
            this.screenColors = new int[]{-7829368, -1};
            RADIUS = 160.0F;
        }

        this.init(-65536);
        this.diagTimer = new DiagTimer(this);
        this.diagTimer.startTimer(DiagTimer.MANUALTEST_TIMEOUT);
        this.setCountDownTimer();
    }
    class NamelessClass_1 extends GestureDetector.SimpleOnGestureListener {
        NamelessClass_1() {
        }

        public boolean onDoubleTap(MotionEvent e) {
            DeadPixelTest.this.undoLast();
            if (DeadPixelTest.this.diagTimer != null) {
//                DeadPixelTest.this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
//                DeadPixelTest.this.countDownTimer.cancel();
//                DeadPixelTest.this.setCountDownTimer();
            }

            return super.onDoubleTap(e);
        }

        public void onLongPress(MotionEvent e) {
            if (DeadPixelTest.this.diagTimer != null) {
//                DeadPixelTest.this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
//                DeadPixelTest.this.countDownTimer.cancel();
//                DeadPixelTest.this.setCountDownTimer();
            }

            DeadPixelTest.this.onTouchEventCircle(e);
            super.onLongPress(e);
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (DeadPixelTest.this.diagTimer != null) {
               // DeadPixelTest.this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
//                DeadPixelTest.this.countDownTimer.cancel();
//                DeadPixelTest.this.setCountDownTimer();
            }

            boolean result = false;

            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 100.0F && Math.abs(velocityX) > 100.0F) {
                    if (diffX < 0.0F) {
                        DeadPixelTest.this.changeScreenColor();
                    }

                    result = true;
                }
            } catch (Exception var8) {
                var8.printStackTrace();
            }

            return result;
        }
    }

    public DeadPixelTest(int[] colors, int outlineColor) {
        super(OruApplication.getAppContext());

        class NamelessClass_1 extends GestureDetector.SimpleOnGestureListener {
            NamelessClass_1() {
            }

            public boolean onDoubleTap(MotionEvent e) {
                DeadPixelTest.this.undoLast();
                if (DeadPixelTest.this.diagTimer != null) {
                    DeadPixelTest.this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
                   DeadPixelTest.this.countDownTimer.cancel();
                    DeadPixelTest.this.setCountDownTimer();
                }

                return super.onDoubleTap(e);
            }

            public void onLongPress(MotionEvent e) {
                if (DeadPixelTest.this.diagTimer != null) {
                    DeadPixelTest.this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
                    DeadPixelTest.this.countDownTimer.cancel();
                   DeadPixelTest.this.setCountDownTimer();
                }

                DeadPixelTest.this.onTouchEventCircle(e);
                super.onLongPress(e);
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (DeadPixelTest.this.diagTimer != null) {
                    DeadPixelTest.this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
                    DeadPixelTest.this.countDownTimer.cancel();
                   DeadPixelTest.this.setCountDownTimer();
                }

                boolean result = false;

                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 100.0F && Math.abs(velocityX) > 100.0F) {
                        if (diffX < 0.0F) {
                            DeadPixelTest.this.changeScreenColor();
                        }

                        result = true;
                    }
                } catch (Exception var8) {
                    var8.printStackTrace();
                }

                return result;
            }
        }

        this.gestureListener = new NamelessClass_1();
        this.context = OruApplication.getAppContext();
        this.screenColors = colors;
        this.init(outlineColor);
        this.diagTimer = new DiagTimer(this);
        this.diagTimer.startTimer(DiagTimer.MANUALTEST_TIMEOUT);
        this.setCountDownTimer();
    }

    public void changeScreenColor() {
        ++this.colorID;
        if (this.colorID < this.screenColors.length) {
            if (this.diagTimer != null) {
//                this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
//                this.countDownTimer.cancel();
//                this.setCountDownTimer();
            }

            this.savedPoints = this.circlePoints.size();
            this.setBackgroundColor(this.screenColors[this.colorID]);
            this.invalidate();
        } else {
            this.testResults.setResultCode(8);
            this.testResults.setPath(this.imagePath);
            if (this.circlePoints.size() > 0) {
                this.testResults.setResultCode(1);
            } else {
                this.testResults.setResultCode(0);
            }

            if (this.testListener != null) {
                this.testListener.onTestEnd(this.testResults);
            }
        }

    }

    public void setOnTestCompleteListener(TestListener testListener) {
        this.testListener = testListener;
        this.testResults = new ScreenTestResults();
    }

    private void init(int outlineColor) {
        this.mPaint = new Paint(4);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(outlineColor);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeWidth(5.0F);
        this.circlePoints = new Stack();
        this.setBackgroundColor(this.screenColors[0]);
        this.gestureDetector = new GestureDetector(this.context, this.gestureListener);
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return true;
    }

    private void onTouchEventCircle(MotionEvent event) {
        this.mStartX = event.getX();
        this.mStartY = event.getY();
       CirclePoint circlePoint = new CirclePoint(this.mStartX, this.mStartY, RADIUS);
        this.circlePoints.push(circlePoint);
        this.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Iterator<CirclePoint> iterator = this.circlePoints.iterator();

        while(iterator.hasNext()) {
           CirclePoint point = (CirclePoint)iterator.next();
            canvas.drawCircle(point.x, point.y, point.radius, this.mPaint);
        }

    }

    public void undoLast() {
        if (!this.circlePoints.isEmpty()) {
            if (this.circlePoints.size() > this.savedPoints) {
                this.circlePoints.pop();
            }

            this.invalidate();
        }
    }

    private Bitmap createBitmap() {
        int width = this.getRootView().getWidth();
        int height = this.getRootView().getHeight();
        Bitmap imageBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(imageBitmap);
        Iterator<CirclePoint> iterator = this.circlePoints.iterator();

        while(iterator.hasNext()) {
            CirclePoint point = (CirclePoint)iterator.next();
            canvas.drawCircle(point.x, point.y, point.radius, this.mPaint);
        }

        return imageBitmap;
    }

    private void createAndSaveImage() {
        Bitmap bitmap = this.createBitmap();
        String rootPath = Environment.getExternalStorageDirectory().toString();
        String localDirName = "DisplayTest";
        File localDirFile = new File(rootPath + File.separator + localDirName);
        if (!localDirFile.exists() && !localDirFile.mkdir()) {
            AppUtils.printLog("DeadPixelTest", "Unable to create the directory", (Throwable)null, 4);
            throw new RuntimeException(Integer.toString(259));
        } else {
            String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())).format(new Date());
            String localPath = localDirName + File.separator + "" + this.mCurrentScreenTest + "_" + timeStamp + ".jpg";
            this.imagePath = rootPath + File.separator + localPath;

            try {
                File imageFile = new File(this.imagePath);
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception var10) {
                var10.printStackTrace();
            }

        }
    }

    public void timeout() {
        this.testResults.setResultCode(3);
        if (this.testListener != null) {
            this.testListener.onTestEnd(this.testResults);
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.diagTimer != null) {
            this.diagTimer.stopTimer();
            this.countDownTimer.cancel();
        }

    }

    public void resumeTest() {
        if (this.diagTimer != null) {
            this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
            this.countDownTimer.cancel();
            this.setCountDownTimer();
        }

    }

    public class CirclePoint {
        float x;
        float y;
        float radius;

        public CirclePoint(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    public static enum ScreenTest {
        DEADPIXEL,
        SCREEN_DISCOLOR,
        SCREEN_BURN,
        LCD_SHADOW;

        private ScreenTest() {
        }
    }
}

