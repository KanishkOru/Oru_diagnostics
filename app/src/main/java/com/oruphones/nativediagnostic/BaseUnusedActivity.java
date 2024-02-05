package com.oruphones.nativediagnostic;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.oruphones.nativediagnostic.util.ThemeUtil;


public abstract class BaseUnusedActivity extends AppCompatActivity {

    private static String TAG = BaseUnusedActivity.class.getSimpleName();
    public Toolbar mToolbar = null;
    private static boolean isAssistedApp = false;
    public static final int ROBOTO_LIGHT = 0;
    public static final int ROBOTO_MEDIUM = 1;
    public static final int ROBOTO_REGULAR = 2;
    public static final int ROBOTO_THIN = 3;
    public static final int AILERON_THIN=4;
    public static final int AILERON_REGULAR=5;
    public static final int AILERON_LIGHT=6;
    public static final int WRITE_SETTINGS = 1;


    public static boolean settings = false;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);

        LayoutInflater _layoutInflator = (LayoutInflater) getSystemService
                (LAYOUT_INFLATER_SERVICE);
        View convertView = _layoutInflator.inflate(getLayoutResource(), null);
        setContentView(convertView);
        context = this;
        if (!isFullscreenActivity()) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            //mToolbar.setTitleTextAppearance(this,R.style.textStyle_title);
          //  mToolbar.inflateMenu(R.menu.main_menu);
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(getToolBarName());
            if ( ((setBackButton()) || setHomeButton())  ) {
                int icon_id = R.drawable.ic_back;
               if(setHomeButton()) {
                    icon_id = R.drawable.ic_home;
                }
                Drawable icon = ContextCompat.getDrawable(context, icon_id);
               if(icon!=null){
                   icon.setColorFilter(ThemeUtil.getColorsFromAttrs(this,R.attr.toolbarTextColor), PorterDuff.Mode.SRC_ATOP);
                   actionBar.setHomeAsUpIndicator(icon);
                   actionBar.setDisplayShowHomeEnabled(true);
               }

            } else {
                Drawable icon = getResources().getDrawable(R.drawable.logo_sprint);
                actionBar.setHomeAsUpIndicator(icon);
                actionBar.setDisplayShowHomeEnabled(false);
            }
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setBackButton()) {
                        onBackPressed();
                    } else if(setHomeButton()) {
                        /*Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();*/
                    }

                }
            });
        }
    }

    public void updateTitle(String title){
        if(!isFullscreenActivity() && mToolbar!=null){
            ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(title);
        }

    }

    protected abstract String getToolBarName();

    protected abstract boolean setBackButton();

    protected boolean setHomeButton() {
        return false;
    }

    protected boolean showExitButton() {
        return false;
    }

    protected boolean isFullscreenActivity() {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    protected abstract int getLayoutResource();

    public int getResourceID(String testName, int drawableType) {
        String name = testName.replaceAll(" ", "_");
        int value = 0;
        TypedArray typedArray = null;
        try {
            int arrayL = getResources().getIdentifier(name, "array", getPackageName());
            typedArray = getResources().obtainTypedArray(arrayL);
            value = typedArray.getResourceId(drawableType, 0);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != typedArray)
                typedArray.recycle();
            return value;
        }
    }



    // check internet connection
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) BaseUnusedActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) BaseUnusedActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    //ASSISTED app
    public static void setIsAssistedApp(String product) {
        isAssistedApp = "ASSISTED".equalsIgnoreCase(product) || "STORE_ASSISTED".equalsIgnoreCase(product);
    }

    public static boolean isIsAssistedApp() {
        return isAssistedApp;
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /*
   static GIFMovieView gifMovieView = null;
    public GIFMovieView getGIFMovieView(Context context, String testName) {
         LogUtil.printLog("gif", "testName= " + testName);
        InputStream stream = null;
        try {
             LogUtil.printLog("gif", "Gif Image Name= " + testName);
            stream = context.getAssets().open(manualtestGifMap.get(testName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(BaseActivity.gifMovieView != null) {
            BaseActivity.gifMovieView.destroyDrawingCache();
            BaseActivity.gifMovieView = null;
        }
        gifMovieView = new GIFMovieView(context, stream);
        gifMovieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return gifMovieView;
    }*/

    public void setFontToView(TextView tv, int type) {
        Typeface tf = null;
        if (type == ROBOTO_LIGHT) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        } else if (type == ROBOTO_MEDIUM) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_medium.ttf");
        } else if (type == ROBOTO_REGULAR) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        } else if (type == ROBOTO_THIN) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
        } else if (type == AILERON_THIN) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/aileron_thin.ttf");
        } else if (type == AILERON_LIGHT) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/aileron_light.ttf");
        } else if (type == AILERON_REGULAR) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/aileron_regular.ttf");
        } else {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        }
        if (tv != null)
            tv.setTypeface(tf);
    }


    protected String getText(@NonNull TextView tv){
        return tv.getText().toString();
    }

    protected void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        setFinishOnTouchOutside(false);
        setOrientation(this);
    }
    // Method
    private static void setOrientation(Activity context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        else
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}