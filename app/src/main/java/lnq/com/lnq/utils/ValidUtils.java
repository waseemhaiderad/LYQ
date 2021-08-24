package lnq.com.lnq.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;

import lnq.com.lnq.R;

import static android.graphics.BitmapFactory.decodeFile;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ValidUtils {

    static String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    static String emailpattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";

    public static boolean validateEditTexts(EditText... testObj) {
        for (int i = 0; i < testObj.length; i++) {
            if (testObj[i].getText().toString().trim().equals(""))
                return false;
        }
        return true;
    }

    public static boolean validateMobileNumber(EditText... testObj) {
        for (int i = 0; i < testObj.length; i++) {
            if (testObj[i].getText().toString().length() != 10)
                return false;
        }
        return true;
    }


    public static boolean validateEmail(String email) {
        if (!email.trim().matches(emailpattern) && !email.trim().matches(emailpattern2))
            return false;
        return true;
    }

    public static boolean validateForDigits(EditText testObj, int noOfDigits) {

        if (testObj.getText().toString().length() != noOfDigits)
            return false;
        return true;
    }

    public static void showToast(Context c, String s) {
        Toast.makeText(c, s, Toast.LENGTH_LONG).show();
    }

    public static void showCustomToast(Context context, String message){
        if (context != null) {
            View view = LayoutInflater.from(context).inflate(R.layout.cus_toast_internet, null);
            TextView text = view.findViewById(R.id.textViewToast);
            text.setText(message);
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String getCountryCode(Context context) {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telMgr == null)
            return "";
        int simState = telMgr.getSimState();
        switch (simState) {
            //if sim is not available then country is find out using timezone id
            case TelephonyManager.SIM_STATE_ABSENT:
                TimeZone tz = TimeZone.getDefault();
                String timeZoneId = tz.getID();
                return timeZoneId;

            //if sim is available then telephony manager network country info is used
            case TelephonyManager.SIM_STATE_READY:
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String countryCodeValue = "";
                if (tm != null) {
                    countryCodeValue = tm.getNetworkCountryIso();
                }
                return countryCodeValue;
            default:
                return "";

        }
    }

    public static final String md5(final String password) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hideKeyboardFromActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFromFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static File createTempFile(File file) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/com.example.mlkit");
        if (!dir.exists() || !dir.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        if (file == null) {
            file = new File(dir, "original.jpg");
        }
        return file;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private static Bitmap compressImage(File imageFile, Bitmap bmp) {
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static Bitmap resizeImage(File imageFile, String path, ImageView view) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decodeFile(path, options);

            int photoW = options.outWidth;
            int photoH = options.outHeight;

            options.inJustDecodeBounds = false;
            options.inSampleSize = Math.min(photoW / view.getWidth(), photoH / view.getHeight());

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            bitmap = rotateImage(bitmap, rotationInDegrees);
            return compressImage(imageFile, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void textViewGradientColor(TextView textView) {
        TextPaint paint = textView.getPaint();
        float width = paint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#2cadf4"),
                        Color.parseColor("#2daef4"),
                        Color.parseColor("#4dd0e9"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
    }

    public static void buttonGradientColor(Button button) {
        TextPaint paint = button.getPaint();
        float width = paint.measureText(button.getText().toString());

        Shader textShader = new LinearGradient(0, 0, width, button.getTextSize(),
                new int[]{
                        Color.parseColor("#2cadf4"),
                        Color.parseColor("#2daef4"),
                        Color.parseColor("#4dd0e9"),
                }, null, Shader.TileMode.CLAMP);
        button.getPaint().setShader(textShader);
    }

    public static void setImage(int image, AppCompatImageView imageView){
        Glide.with(getApplicationContext())
                .load(image)
                .into(imageView);
    }

}
