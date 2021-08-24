package lnq.com.lnq.fragments.activity;

import android.Manifest;
import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.ActivityFullProfilePictureBinding;
import lnq.com.lnq.endpoints.EndpointKeys;

public class FullProfilePictureActivity extends AppCompatActivity {

    private ActivityFullProfilePictureBinding activityBinding;
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_profile_picture);
        context = this;
        createTransferUtility();
        init();
        CardView topBarLayout = activityBinding.tobBar.topBarContactCardView;
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog(FullProfilePictureActivity.this, R.style.BottomSheetDialogTheme);
                View view = LayoutInflater.from(FullProfilePictureActivity.this).inflate(R.layout.cus_bottomsheet_saveimages, null);
                AppCompatTextView saveImage = view.findViewById(R.id.saveImage);
                AppCompatTextView cancel = view.findViewById(R.id.cancel);
                saveImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityBinding.imageViewProfilePicture.setDrawingCacheEnabled(true);
                        saveImageToGallery(activityBinding.imageViewProfilePicture.getDrawingCache());
                        bottomSheet.hide();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheet.hide();
                    }
                });
                bottomSheet.setContentView(view);
                bottomSheet.show();
            }
        });

    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, context.getApplicationContext());
    }

    private void init() {

        String imagePath = getIntent().getStringExtra("profileImage");
        if (imagePath != null && !imagePath.isEmpty()) {
            download(imagePath, activityBinding.imageViewProfilePicture);
        }
    }

    void download(String objectKey, ImageView imageView) {
        final File fileDownload = new File(context.getCacheDir(), objectKey);

        TransferObserver transferObserver = transferUtility.download(
                Constants.BUCKET_NAME,
                objectKey,
                fileDownload
        );
        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED.equals(state)) {
                    if (context != null && imageView != null) {
                        Glide.with(context).
                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                                apply(new RequestOptions()).
                                diskCacheStrategy(DiskCacheStrategy.ALL).
                                into(imageView);
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
            }
        });
    }

    private void saveImageToGallery(Bitmap finalBitmap) {
//        if (checkStoragePermission()) {
        ActivityCompat.requestPermissions(FullProfilePictureActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/LNQ Chat");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fName = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FNAME, "");
            String fname = fName + n + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                Toast.makeText(FullProfilePictureActivity.this, "Your chat image has been saved to your camera roll.", Toast.LENGTH_SHORT).show();
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(FullProfilePictureActivity.this, new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

    }

//    public boolean checkStoragePermission() {
//        return ((MainActivity) FullPr).fnCheckPermission();
//    }
}