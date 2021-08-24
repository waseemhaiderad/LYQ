package lnq.com.lnq.fragments.qrcode;


import android.Manifest;

import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.google.zxing.WriterException;

import net.glxn.qrgen.android.QRCode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ProfileImagesAdapter;
import lnq.com.lnq.adapters.ProfleSocialLinksAdapter;
import lnq.com.lnq.adapters.SelectProfilesAdapter;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.LinearLayoutManagerWithSmoothScroller;
import lnq.com.lnq.databinding.FragMyCodeBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusDisableQRButtons;
import lnq.com.lnq.model.event_bus_models.EventBusQRProfilesClick;
import lnq.com.lnq.model.event_bus_models.EventBusShowRecipetInfoFragment;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;


public class FragMyCode extends Fragment implements View.OnClickListener {
    private String cachePath = "";
    private TransferUtility transferUtility;

    public final static int QRcodeWidth = 500;
    private Bitmap bitmap;

    private FragMyCodeBinding myCodeBinding;
    private static final String TAG = "FragMyCode";

    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentProfileData;
    private SelectProfilesAdapter selectProfilesAdapter;
    private List<MultiProfileRoomModel> proflieList = new ArrayList<>();
    String profileId;

    public FragMyCode() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myCodeBinding = DataBindingUtil.inflate(inflater, R.layout.frag_my_code, container, false);
        return myCodeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransferUtility();
        cachePath = getContext().getCacheDir().getAbsolutePath();
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        WindowManager.LayoutParams layout = getActivity().getWindow().getAttributes();
        layout.screenBrightness = 0.5F;
        getActivity().getWindow().setAttributes(layout);
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                proflieList.clear();
                proflieList.addAll(multiProfileRoomModels);
                for (MultiProfileRoomModel data : proflieList) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentProfileData = data;
                        break;
                    }
                }
                if (proflieList.size() > 0) {
                    if (getContext() != null) {
                        sortListByDate(proflieList);
                        selectProfilesAdapter = new SelectProfilesAdapter(getContext(), proflieList);
                        myCodeBinding.recyclerViewSelectProdfile.setAdapter(selectProfilesAdapter);
                    }
                }
                if (currentProfileData.getUser_avatar() != null && !currentProfileData.getUser_avatar().isEmpty()) {
                    download(currentProfileData.getUser_avatar(), myCodeBinding.mImgProfilePicture);
                }
            }
        });
        myCodeBinding.recyclerViewSelectProdfile.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
        myCodeBinding.recyclerViewSelectProdfile.setItemAnimator(new DefaultItemAnimator());

        myCodeBinding.mTvName.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FNAME, "") + " " + LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_LNAME, ""));
        String companyName = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_COMPANY, "");
        if (companyName.length() > 10) {
            companyName = companyName.substring(0, 10) + "...";
        } else {
            companyName = companyName;
        }
        myCodeBinding.mTvCompany.setText(companyName);
        String position = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_CURRENT_POSITION, "");
        if (position.length() > 15) {
            position = position.substring(0, 15) + "  -  ";
        } else {
            position = position + "  -  ";
        }
        myCodeBinding.mTvPosition.setText(position);
        try {
            String name = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FNAME, "") + " " + LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_LNAME, "");
            String email = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "");
            String address = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_ADDRESS, "");
            String jobTitle = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_CURRENT_POSITION, "");
            String company = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_COMPANY, "");
            String phone = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, "");
            String id = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
            profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
            bitmap = TextToImageEncode(name, email, address, jobTitle, company, phone, id + "," + profileId + ", LNQ");
            myCodeBinding.mImgQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
        }
        myCodeBinding.mBtnSavetoPhoto.setOnClickListener(this);
        myCodeBinding.mBtnShareCode.setOnClickListener(this);
        myCodeBinding.mBtnSelectProfile.setOnClickListener(this);

        myCodeBinding.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCodeBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && myCodeBinding.cardViewPopUpSelectProfileContainer.getVisibility() == View.VISIBLE) {
                    myCodeBinding.cardViewPopUpSelectProfileContainer.setVisibility(View.GONE);
                    myCodeBinding.imageViewArrowUp.setVisibility(View.GONE);
                    myCodeBinding.mBtnSavetoPhoto.setClickable(true);
                    myCodeBinding.mBtnShareCode.setClickable(true);
                    EventBus.getDefault().post(new EventBusDisableQRButtons("enable"));
                }
            }
        });

    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity().getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, getActivity().getApplicationContext());
    }

//    void download(String objectKey, ImageView imageView) {
//        final File fileDownload = new File(getActivity().getCacheDir(), objectKey);
//
//        TransferObserver transferObserver = transferUtility.download(
//                Constants.BUCKET_NAME,
//                objectKey,
//                fileDownload
//        );
//        transferObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                Log.d(TAG, "onStateChanged: " + state);
//                if (TransferState.COMPLETED.equals(state)) {
//                    if (getActivity() != null) {
//                        Glide.with(getActivity()).
//                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
//                                apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar)).
//                                apply(new RequestOptions().circleCrop()).
//                                into(imageView);
//                    }
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                Log.e(TAG, "onError: ", ex);
//            }
//        });
//    }

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            Glide.with(getActivity()).
                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
                    apply(new RequestOptions().circleCrop()).
                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } else {
            final File fileDownload = new File(cachePath, objectKey);

            TransferObserver transferObserver = transferUtility.download(
                    Constants.BUCKET_NAME,
                    objectKey,
                    fileDownload
            );
            transferObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d(TAG, "onStateChanged: " + state);
                    if (TransferState.COMPLETED.equals(state)) {
                        LnqApplication.getInstance().listImagePaths.add(fileDownload.getAbsolutePath());
                        if (getActivity() != null) {
                            Glide.with(getActivity()).
                                    load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                                    apply(new RequestOptions().circleCrop()).
                                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e(TAG, "onError: ", ex);
                }
            });
        }
    }

    private void sortListByDate(List<MultiProfileRoomModel> modelArrayList) {
        Collections.sort(modelArrayList, new Comparator<MultiProfileRoomModel>() {
            public int compare(MultiProfileRoomModel obj1, MultiProfileRoomModel obj2) {
                return Integer.valueOf(obj1.getId()).compareTo(Integer.parseInt(obj2.getId()));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusQRProfilesClick(EventBusQRProfilesClick eventBusQRProfilesClick) {
        String user_image = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_avatar();
        if (user_image != null && !user_image.isEmpty()) {
            download(user_image, myCodeBinding.mImgProfilePicture);
        }
        myCodeBinding.mTvName.setText(eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_fname() + " " + eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_lname());
        String companyName = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_company();
        if (companyName.length() > 10) {
            companyName = companyName.substring(0, 10) + "...";
        } else {
            companyName = companyName;
        }
        myCodeBinding.mTvCompany.setText(companyName);
        String position = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_current_position();
        if (position.length() > 15) {
            position = position.substring(0, 15) + "  -  ";
        } else {
            position = position + "  -  ";
        }
        myCodeBinding.mTvPosition.setText(position);
        try {
            String name = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_fname() + " " + eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_lname();
            String email = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "");
            String address = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_address();
            String jobTitle = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_current_position();
            String company = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getUser_company();
            String phone = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, "");
            String id = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
            profileId = eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getId();
            bitmap = TextToImageEncode(name, email, address, jobTitle, company, phone, id + "," + profileId + ", LNQ");
            myCodeBinding.mImgQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
        }
        selectProfilesAdapter.notifyDataSetChanged();
        myCodeBinding.mBtnSavetoPhoto.setClickable(true);
        myCodeBinding.mBtnShareCode.setClickable(true);
        EventBus.getDefault().post(new EventBusDisableQRButtons("enable"));
        myCodeBinding.cardViewPopUpSelectProfileContainer.setVisibility(View.GONE);
        myCodeBinding.imageViewArrowUp.setVisibility(View.GONE);
        LnqApplication.getInstance().editor.putString("selectedProfileId", eventBusQRProfilesClick.getMultiProfileRoomModels().get(eventBusQRProfilesClick.getPosition()).getId()).apply();
    }

    private Bitmap TextToImageEncode(String name, String email, String address, String jobTitle, String company, String phone, String id) throws WriterException {
        // encode contact data as vcard using defaults
//        VCard johnDoe = new VCard(name)
//                .setEmail(email)
//                .setAddress(address)
//                .setTitle(jobTitle)
//                .setCompany(company)
//                .setPhoneNumber(phone);
//        johnDoe.setNote(id);
        String myQrCode = "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N:" + name + "\n" +
                "ORG:" + company + "\n" +
                "TITLE:" + jobTitle + "\n" +
                "TEL:" + phone + "\n" +
                "EMAIL:" + email + "\n" +
                "ADR:" + address + "\n" +
                "END:VCARD," + id;
        return QRCode.from(myQrCode).withColor(Color.rgb(85, 190, 247), 0xFFFFFFFF).bitmap();
//        BitMatrix bitMatrix;
//        try {
//            bitMatrix = new MultiFormatWriter().encode(
//                    Value,
//                    BarcodeFormat.DATA_MATRIX.QR_CODE,
//                    QRcodeWidth, QRcodeWidth, null
//            );
//
//        } catch (IllegalArgumentException Illegalargumentexception) {
//
//            return null;
//        }
//        int bitMatrixWidth = bitMatrix.getWidth();
//
//        int bitMatrixHeight = bitMatrix.getHeight();
//
//        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
//
//        for (int y = 0; y < bitMatrixHeight; y++) {
//            int offset = y * bitMatrixWidth;
//
//            for (int x = 0; x < bitMatrixWidth; x++) {
//
//                pixels[offset + x] = bitMatrix.get(x, y) ?
//                        getResources().getColor(R.color.colorPrimaryBlue) : getResources().getColor(R.color.colorWhite);
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
//
//        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
//
    }

    public void saveQRToPhone(Bitmap bitmap) {
        EventBus.getDefault().post(new EventBusUserSession("save_qr_code"));
        if (checkStoragePermission()) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            myDir.mkdirs();
            String fName = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FNAME, "");
            String fname = fName + ".png";

            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Toast.makeText(getActivity(), "Your QR code has been saved to your camera roll.", Toast.LENGTH_SHORT).show();
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "saveToPhone: " + e.getMessage());
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
    }

    private void SaveImage(Bitmap finalBitmap) {
        EventBus.getDefault().post(new EventBusUserSession("save_qr_code"));
        if (checkStoragePermission()) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/LNQ QR");
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
                Toast.makeText(getActivity(), "Your QR code has been saved to your camera roll.", Toast.LENGTH_SHORT).show();
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(getContext(), new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
    }

    public boolean checkStoragePermission() {
        return ((MainActivity) getActivity()).fnCheckPermission();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnSavetoPhoto:
                View v1 = getActivity().getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);
                SaveImage(bitmap);
                break;
            case R.id.mBtnShareCode:
                EventBus.getDefault().post(new EventBusShowRecipetInfoFragment());
                break;
            case R.id.mBtnSelectProfile:
                if (myCodeBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && myCodeBinding.cardViewPopUpSelectProfileContainer.getVisibility() == View.VISIBLE) {
                    myCodeBinding.cardViewPopUpSelectProfileContainer.setVisibility(View.GONE);
                    myCodeBinding.imageViewArrowUp.setVisibility(View.GONE);
                    EventBus.getDefault().post(new EventBusDisableQRButtons("enable"));
                    myCodeBinding.mBtnSavetoPhoto.setClickable(true);
                    myCodeBinding.mBtnShareCode.setClickable(true);
                } else {
                    int count = 0;
                    if (myCodeBinding.recyclerViewSelectProdfile.getAdapter() != null) {
                        count = myCodeBinding.recyclerViewSelectProdfile.getAdapter().getItemCount();
                    }
                    if (count <= 2) {
                        myCodeBinding.cardViewPopUpSelectProfileContainer.getLayoutParams().height = (int) getResources().getDimension(R.dimen._110sdp);
                    } else if (count <= 4) {
                        myCodeBinding.cardViewPopUpSelectProfileContainer.getLayoutParams().height = (int) getResources().getDimension(R.dimen._220sdp);
                    } else if (count <= 6) {
                        myCodeBinding.cardViewPopUpSelectProfileContainer.getLayoutParams().height = (int) getResources().getDimension(R.dimen._330sdp);
                    } else {
                        myCodeBinding.cardViewPopUpSelectProfileContainer.getLayoutParams().height = (int) getResources().getDimension(R.dimen._400sdp);
                    }
                    myCodeBinding.cardViewPopUpSelectProfileContainer.setVisibility(View.VISIBLE);
                    myCodeBinding.imageViewArrowUp.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new EventBusDisableQRButtons("disable"));
                    myCodeBinding.mBtnSavetoPhoto.setClickable(false);
                    myCodeBinding.mBtnShareCode.setClickable(false);
                }
        }
    }
}