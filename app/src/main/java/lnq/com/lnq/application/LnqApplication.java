package lnq.com.lnq.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.androidnetworking.AndroidNetworking;
import com.google.android.material.snackbar.Snackbar;

import androidx.collection.ArraySet;
import androidx.multidex.MultiDex;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;

public class LnqApplication extends Application {

    //    Android fields....
    public FrameLayout.LayoutParams mParams;
    public Snackbar mSnakeBar;
    public ArraySet<String> listImagePaths = new ArraySet<>();

    //    Shared preference fields....
    public SharedPreferences sharedPreferences;
    public HashMap<String, String> draftHasMap;
    public SharedPreferences.Editor editor;

    //    Instance fields....
    public boolean isFirstTime;
    private static LnqApplication singleton;

    public static LnqApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        AndroidNetworking.initialize(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        draftHasMap = new HashMap<>();
        deleteCache(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void snakeBar(View mRoot, String mMsg, int mBgCol) {
        mSnakeBar = Snackbar.make(mRoot, mMsg, Snackbar.LENGTH_LONG);
        mRoot = mSnakeBar.getView();
        mRoot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), mBgCol));
        TextView mTv = mRoot.findViewById(com.google.android.material.R.id.snackbar_text);
        mTv.setMaxLines(5);
        mParams = (FrameLayout.LayoutParams) mRoot.getLayoutParams();
        mParams.gravity = Gravity.TOP;
        mRoot.setLayoutParams(mParams);
        mSnakeBar.show();
    }

    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }
    public  boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
