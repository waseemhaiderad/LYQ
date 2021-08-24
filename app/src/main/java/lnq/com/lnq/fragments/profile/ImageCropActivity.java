package lnq.com.lnq.fragments.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.adamstyrc.cookiecutter.CookieCutterImageView;
import com.adamstyrc.cookiecutter.CookieCutterShape;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.utils.FontUtils;

public class ImageCropActivity extends AppCompatActivity {

    private static final String TAG = ImageCropActivity.class.getSimpleName();
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading . . .");
        progressDialog.setCancelable(false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, BasicFragment_ImageCrop.newInstance()).commit();
        }

        // apply custom font
        FontUtils.setFont(findViewById(R.id.root_layout));
    }

    public void showMessageDialog(String dialogType, String textMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = null;
        switch (dialogType) {
            case "success":
                view = LayoutInflater.from(this).inflate(R.layout.cus_dialog_success, null);
                break;
            case "error":
                view = LayoutInflater.from(this).inflate(R.layout.cus_dialog_error, null);
                break;
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        TextView text = view.findViewById(R.id.textViewMessageDialog);
        text.setText(textMessage);
        dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
//        startActivity(ResultImageCrop.createIntent(this, uri));
        try {
            String localPath = uri.toString();
            File f = new File(getCacheDir(), System.currentTimeMillis() + ".jpeg");
            try {
                f.createNewFile();
                copyFile(new File(getRealPathFromURI(Uri.parse(localPath))), f);
                Intent intent = new Intent();
                intent.putExtra("image", f.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel inChannel = new FileInputStream(sourceFile).getChannel();
        FileChannel outChannel = new FileOutputStream(destFile).getChannel();
        try {
            long i = inChannel.transferTo(0, inChannel.size(), outChannel);
            Log.i("Info",i + "");
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
        boolean isDeleted = sourceFile.delete();
        if(isDeleted){
            Log.i("Info","Deleted Yuhu");
        }

    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}