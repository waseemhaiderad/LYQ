package lnq.com.lnq.fragments.qrcode;

import android.Manifest;
import android.app.ProgressDialog;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ConnectionsGridAdapter;
import lnq.com.lnq.adapters.ConnectionsListAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.common.StringMethods;
import lnq.com.lnq.custom.views.camera.CameraPreview;
import lnq.com.lnq.custom.views.fast_scroller.models.AlphabetItem;
import lnq.com.lnq.databinding.FragShareQrCodeBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentProfileLooksGood;
import lnq.com.lnq.model.event_bus_models.EventBuSetDataRecipentFragment;
import lnq.com.lnq.model.event_bus_models.EventBusConnectionView;
import lnq.com.lnq.model.event_bus_models.EventBusDisableQRButtons;
import lnq.com.lnq.model.event_bus_models.EventBusGetLnqedContactList;
import lnq.com.lnq.model.event_bus_models.EventBusOpenRecipentFragment;
import lnq.com.lnq.model.event_bus_models.EventBusQrCodeScanSuccess;
import lnq.com.lnq.model.event_bus_models.EventBusShowRecipetInfoFragment;
import lnq.com.lnq.model.event_bus_models.EventBusShowScanScreen;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnections;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsMainObject;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragShareQrCode extends Fragment implements View.OnClickListener {

    private FragShareQrCodeBinding mBind;

    private FragmentManager fragmentManager;
    private FragMyCode fragMyCode;
    private FragScanCode fragScanCode;
    private FragmentSendInfo fragmentSendInfo;
    ProgressDialog progressDialog;
    public String imageFile;
    Bitmap imageBitmap;
    public static final int RC_TAKE_PICTURE = 105;

    private Camera camera;
    private CameraPreview cameraPreview;
    //    Callback fields....
    private Camera.PictureCallback pictureCallback;
    private boolean cameraFront = true;

    private Call<UserConnectionsMainObject> callUserConnections;
    private List<UserConnections> userContactList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListImported = new ArrayList<>();

    private String profileId;
    int curBrightnessValue;
    WindowManager.LayoutParams layout;
    private AppCompatImageView imageViewBackTopBar, imageViewSearchTopBar, imageViewDropdownContacts;
    private SlideUp slideUp;
    CardView topBarLayout;
    TextView textViewHeading;

    public FragShareQrCode() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.frag_share_qr_code, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.GONE);
        init();
        topBarLayout = mBind.topBarContact.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewSearchTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.lnq_code);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.INVISIBLE);
        imageViewSearchTopBar.setVisibility(View.GONE);
        imageViewDropdownContacts.setVisibility(View.GONE);
    }

    private void init() {
        EventBus.getDefault().register(this);

        profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

        try {
            curBrightnessValue = Settings.System.getInt(getActivity().getContentResolver()
                    , Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        LnqApplication.getInstance().editor.putInt("currentBrightness", curBrightnessValue);
        layout = getActivity().getWindow().getAttributes();
        layout.screenBrightness = curBrightnessValue;
        getActivity().getWindow().setAttributes(layout);
        fragmentManager = getChildFragmentManager();
        fragMyCode = new FragMyCode();
        fragScanCode = new FragScanCode();
        fragmentSendInfo = new FragmentSendInfo();

        fnChangeButtonDrawable(mBind.mBtnMyCode, mBind.mBtnScan, mBind.mBtnPlus);
        mBind.mViewBgBottomBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.GONE);
        mBind.imageViewBack.setVisibility(View.VISIBLE);
        mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
        mBind.buttonSendMyInfo.setVisibility(View.GONE);
        mBind.viewDivider.setVisibility(View.GONE);
        mBind.buttonImportPhoneContacts.setVisibility(View.GONE);
        //initializing
//        progressDialog = new ProgressDialog(getActivity());
//        //setting Title
//        progressDialog.setTitle("Please wait...");
//        // Setting Message
//        progressDialog.setMessage("Loading scanner...");
//        progressDialog.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mFlQrCodeContainer, fragMyCode, "MY CODE");
        fragmentTransaction.add(R.id.mFlScanQrCodeContainer, fragScanCode, "SCAN CODE");
        fragmentTransaction.add(R.id.mFlQrCodeContainer, fragmentSendInfo, "SEND INFO");
        fragmentTransaction.show(fragMyCode);
        fragmentTransaction.hide(fragmentSendInfo);
        fragmentTransaction.hide(fragScanCode);
        fragmentTransaction.commit();
//            }
//        }, 500);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (progressDialog != null) {
//                    progressDialog.dismiss();
//                }
//            }
//        }, 3000);

        mBind.mBtnMyCode.setOnClickListener(this);
        mBind.mBtnMyCode1.setOnClickListener(this);
        mBind.mBtnScan.setOnClickListener(this);
        mBind.mBtnPlus.setOnClickListener(this);
        mBind.buttonSendMyInfo.setOnClickListener(this);
        mBind.imageViewBack.setOnClickListener(this);
        mBind.buttonImportPhoneContacts.setOnClickListener(this);
    }

    private void fnChangeButtonDrawable(Button buttonSelected, Button... buttonDeselected) {
        buttonSelected.setSelected(true);
        for (Button button : buttonDeselected) {
            button.setSelected(false);
            button.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
        }
        buttonSelected.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void fnChangeButtonDrawable1(Button buttonSelected, Button... buttonDeselected) {
        buttonSelected.setSelected(true);
        for (Button button : buttonDeselected) {
            button.setSelected(false);
            button.setTextColor(getResources().getColor(R.color.colorWhite));
        }
        buttonSelected.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callUserConnections != null && callUserConnections.isExecuted()) {
            callUserConnections.cancel();
        }
        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.VISIBLE);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusQrCodeSuccess(EventBusQrCodeScanSuccess eventBusQrCodeScanSuccess) {
        mBind.buttonImportPhoneContacts.setVisibility(View.GONE);
        mBind.viewDivider.setVisibility(View.GONE);
        mBind.buttonSendMyInfo.setVisibility(View.GONE);
        mBind.imageViewSuccess.setVisibility(View.VISIBLE);
        mBind.textViewSuccess.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBind.buttonImportPhoneContacts.setVisibility(View.VISIBLE);
                mBind.viewDivider.setVisibility(View.VISIBLE);
                mBind.buttonSendMyInfo.setVisibility(View.VISIBLE);
                mBind.imageViewSuccess.setVisibility(View.GONE);
                mBind.textViewSuccess.setVisibility(View.GONE);
            }
        }, 3000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusShowReceiptInfoFragment(EventBusShowRecipetInfoFragment eventBusShowRecipetInfoFragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.VISIBLE);
        mBind.imageViewBack.setVisibility(View.GONE);
        fnChangeButtonDrawable(mBind.mBtnPlus, mBind.mBtnScan, mBind.mBtnMyCode);
        mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
        mBind.buttonSendMyInfo.setVisibility(View.GONE);
        mBind.viewDivider.setVisibility(View.GONE);
        mBind.buttonImportPhoneContacts.setVisibility(View.GONE);
        fragmentTransaction.show(fragmentSendInfo);
        fragmentTransaction.hide(fragScanCode);
        fragmentTransaction.hide(fragMyCode);
        fragmentTransaction.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusShowScanScreen(EventBusShowScanScreen eventBusShowScanScreen) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fnChangeButtonDrawable1(mBind.mBtnScan1, mBind.mBtnMyCode1);
        mBind.mViewBgBottomBar.setVisibility(View.GONE);
        fragmentTransaction.hide(fragMyCode);
        fragmentTransaction.hide(fragmentSendInfo);
        fragmentTransaction.show(fragScanCode);
        fragmentTransaction.commit();
        ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.GONE);
        mBind.imageViewBack.setVisibility(View.VISIBLE);
        mBind.buttonSendMyInfo.setVisibility(View.VISIBLE);
        mBind.viewDivider.setVisibility(View.VISIBLE);
        fragScanCode.mScannerView.startCamera();
        mBind.buttonImportPhoneContacts.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusEnableDisableBButton(EventBusDisableQRButtons mObj) {
        if (mObj.getType().equals("disable")) {
            mBind.mBtnScan.setClickable(false);
            mBind.mBtnPlus.setClickable(false);
            mBind.imageViewBack.setClickable(false);
        } else {
            mBind.mBtnScan.setClickable(true);
            mBind.mBtnPlus.setClickable(true);
            mBind.imageViewBack.setClickable(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetData(EventBuSetDataRecipentFragment mObj) {
        String firstName = mObj.getfName();
        String emailAddress = mObj.getEmail();
        String phoneNumber = mObj.getPhone();
        String lastName = mObj.getlName();

        if (phoneNumber.isEmpty()) {
            phoneNumber = "";
        }
        if (emailAddress.isEmpty()) {
            emailAddress = "";
        }
        Bundle bundle = new Bundle();
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("email", emailAddress);
        bundle.putString("phone", phoneNumber);
        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.RECIPENT_INFO, true, bundle);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.mBtnScan:
                fnChangeButtonDrawable1(mBind.mBtnScan1, mBind.mBtnMyCode1);
                mBind.mLlShareQrCodeButtons1.setVisibility(View.VISIBLE);
                mBind.mLlShareQrCodeButtons.setVisibility(View.GONE);
                ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
                mBind.topBarContact.topBarCardView.setVisibility(View.GONE);
                mBind.mBtnPlus.setVisibility(View.GONE);
                layout.screenBrightness = curBrightnessValue;
                getActivity().getWindow().setAttributes(layout);
                if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
                    EventBus.getDefault().post(new EventBusShowScanScreen());
                } else {
                    ((MainActivity) getActivity()).fnRequestCameraPermission(8);
                }
                break;
            case R.id.mBtnMyCode1:
            case R.id.mBtnMyCode:
            case R.id.imageViewBack:
                mBind.mLlShareQrCodeButtons1.setVisibility(View.GONE);
                mBind.mLlShareQrCodeButtons.setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.VISIBLE);
                textViewHeading.setText(R.string.lnq_code);
                ValidUtils.textViewGradientColor(textViewHeading);
                imageViewBackTopBar.setVisibility(View.INVISIBLE);
                mBind.topBarContact.topBarCardView.setVisibility(View.VISIBLE);
                mBind.mBtnPlus.setVisibility(View.VISIBLE);
                layout.screenBrightness = 0.5f;
                getActivity().getWindow().setAttributes(layout);
                ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.VISIBLE);
                mBind.imageViewBack.setVisibility(View.VISIBLE);
                fnChangeButtonDrawable(mBind.mBtnMyCode, mBind.mBtnScan, mBind.mBtnPlus);
                mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                fragmentTransaction.show(fragMyCode);
                fragmentTransaction.hide(fragScanCode);
                fragmentTransaction.hide(fragmentSendInfo);
                fragmentTransaction.commit();
                mBind.buttonSendMyInfo.setVisibility(View.GONE);
                mBind.viewDivider.setVisibility(View.GONE);
                mBind.buttonImportPhoneContacts.setVisibility(View.GONE);
                fragScanCode.mScannerView.stopCamera();
                break;
            case R.id.mBtnPlus:
//                mBind.mLlShareQrCodeButtons1.setVisibility(View.GONE);
//                mBind.mLlShareQrCodeButtons.setVisibility(View.VISIBLE);
//                ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.VISIBLE);
//                textViewHeading.setText(R.string.add_connection);
//                ValidUtils.textViewGradientColor(textViewHeading);
//                imageViewBackTopBar.setVisibility(View.VISIBLE);
//                imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getActivity().onBackPressed();
//                    }
//                });
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.RECIPENT_INFO, true, null);
                WindowManager.LayoutParams layout2 = getActivity().getWindow().getAttributes();
                layout2.screenBrightness = curBrightnessValue;
                getActivity().getWindow().setAttributes(layout2);
//                ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
//                ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.VISIBLE);
//                mBind.imageViewBack.setVisibility(View.VISIBLE);
//                fnChangeButtonDrawable(mBind.mBtnPlus, mBind.mBtnScan, mBind.mBtnMyCode);
//                mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
//                mBind.buttonSendMyInfo.setVisibility(View.GONE);
//                mBind.viewDivider.setVisibility(View.GONE);
//                mBind.buttonImportPhoneContacts.setVisibility(View.GONE);
//                fragmentTransaction.show(fragmentSendInfo);
//                fragmentTransaction.hide(fragScanCode);
//                fragmentTransaction.hide(fragMyCode);
//                fragScanCode.mScannerView.stopCamera();
//                fragmentTransaction.commit();
                break;
            case R.id.buttonSendMyInfo:
//                ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
//                ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.VISIBLE);
//                mBind.imageViewBack.setVisibility(View.GONE);
//                fnChangeButtonDrawable(mBind.mBtnPlus, mBind.mBtnScan, mBind.mBtnMyCode);
//                mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
//                mBind.buttonSendMyInfo.setVisibility(View.GONE);
//                mBind.viewDivider.setVisibility(View.GONE);
//                mBind.buttonImportPhoneContacts.setVisibility(View.GONE);
//                fragmentTransaction.show(fragmentSendInfo);
//                fragmentTransaction.hide(fragScanCode);
//                fragmentTransaction.hide(fragMyCode);
//                fragmentTransaction.commit();
                ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
                layout.screenBrightness = curBrightnessValue;
                getActivity().getWindow().setAttributes(layout);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (camera != null) {
                        camera.takePicture(null, null, pictureCallback);
                    } else {
                        fnOpenCamera();
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 900);
                }
                return;
            case R.id.buttonImportPhoneContacts:
                layout.screenBrightness = curBrightnessValue;
                getActivity().getWindow().setAttributes(layout);
                if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
                    if (userContactsDataList.size() > 0) {
                        showImportContactsFragment("local");
                    } else {
                        reqContacts("", "");
                    }
                } else {
                    ((MainActivity) getActivity()).fnRequestContactsPermission(5);
                }
                break;
        }
    }

    //    Method to open mobile camera....
    public void fnOpenCamera() {
        camera = Camera.open();
        cameraPreview = new CameraPreview(getActivity(), getActivity(), camera);
        pictureCallback = getPictureCallback();
        mBind.cameraView.setVisibility(View.VISIBLE);
        mBind.cameraView.removeAllViews();
        mBind.cameraView.addView(cameraPreview);
        int camerasNumber = Camera.getNumberOfCameras();
        if (camerasNumber > 1) {
            releaseCamera();
            chooseCamera();
            cameraFront = true;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = (data, camera) -> {
            if (data != null) {
                releaseCamera();
                if (camera == null && cameraPreview != null) {
                    chooseCamera();
                    cameraFront = true;
                }
                imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                    imageBitmap = flip(imageBitmap);
                }
                try {
                    File f = new File(getActivity().getCacheDir(), System.currentTimeMillis() + ".jpeg");
                    f.createNewFile();

                    Bitmap bitmap = imageBitmap;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    imageFile = f.getAbsolutePath();
                    mBind.cameraView.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("ocrBitmap", bitmapdata);
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.OCR_IMAGEPATH, true, bundle);
                } catch (IOException e) {
                }
            }
        };
        return picture;
    }

    public Bitmap flip(Bitmap src) {
        Matrix matrix = new Matrix();
//        if (type == FragmentOCRImagePath.Direction.VERTICAL) {
//            matrix.preScale(1.0f, -1.0f);
//        } else if (type == FragmentOCRImagePath.Direction.HORIZONTAL) {
//            matrix.preScale(-1.0f, 1.0f);
//        } else {
//            return src;
//        }
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = true;
                break;

            }
        }
        return cameraId;
    }

    public void chooseCamera() {
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                camera = Camera.open(cameraId);
                pictureCallback = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                camera = Camera.open(cameraId);
                pictureCallback = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        }
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            pictureCallback = null;
        }
    }

    private void reqContacts(String searchKey, String searchFilter) {
        ((MainActivity) getActivity()).progressDialog.show();
        userContactsDataList.clear();
        callUserConnections = Api.WEB_SERVICE.contacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), searchKey, searchFilter, profileId);
        callUserConnections.enqueue(new Callback<UserConnectionsMainObject>() {
            public void onResponse(Call<UserConnectionsMainObject> call, Response<UserConnectionsMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    ((MainActivity) getActivity()).progressDialog.dismiss();
                    switch (response.body().getStatus()) {
                        case 1:
                            userContactList = response.body().getUserContacts();
                            for (int i = 0; i < userContactList.size(); i++) {
                                UserConnections userContact = userContactList.get(i);
                                userContactsDataList.add(userContact.getUser_data());
                            }
                            for (int i = 0; i < userContactsDataList.size(); i++) {
                                UserConnectionsData userContactsData = userContactsDataList.get(i);
                                if (userContactsData.getUser_fname() == null && userContactsData.getUser_lname() == null) {
                                    userContactsData.setUser_fname("");
                                    userContactsData.setUser_lname("");
                                }
                                if (userContactsData.getUser_current_position() == null) {
                                    userContactsData.setUser_current_position("");
                                }
                                if ((userContactsData.getUser_fname().trim() + userContactsData.getUser_lname().trim()).isEmpty()) {
                                    if (!userContactsData.getContact_name().isEmpty()) {
                                        userContactsData.setUser_fname(userContactsData.getContact_name());
                                    } else if (!userContactsData.getUser_phone().isEmpty()) {
                                        userContactsData.setUser_fname(userContactsData.getUser_phone());
                                    } else {
                                        userContactsData.setUser_fname(userContactsData.getUser_email());
                                    }
                                }
                            }
                            if (userContactsDataList.size() > 0) {
                                String sortingType = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL);
                                if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_ALPHABETICAL)) {
                                    SortingUtils.sortContactsByString(userContactsDataList, "alphabet");
                                    List<String> strAlphabets = new ArrayList<>();
                                } else if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_DISTANC)) {
                                    SortingUtils.sortContactsByDouble(userContactsDataList);
                                } else if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_RECENTVIEWED)) {
                                    SortingUtils.sortContactsByString(userContactsDataList, "recentViewed");
                                } else if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, "").contains(EndpointKeys.SORT_RECENTLNQ)) {
                                    SortingUtils.sortContactsByString(userContactsDataList, "recentLNQ");
                                }
                                EventBus.getDefault().post(new EventBusGetLnqedContactList(userContactsDataList));
                            }
                            EventBus.getDefault().post(new EventBusConnectionView());
                            showImportContactsFragment("local");
                            break;
                        case 0:
                            showImportContactsFragment("local");
                            if (response.body().getMessage() != null) {
                                if (response.body().getMessage().equalsIgnoreCase("No contacts found")) {
//                                    connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserConnectionsMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    private void showImportContactsFragment(String importType) {
        Bundle bundle = new Bundle();
        bundle.putString("import_type", importType);
        ((MainActivity) getActivity()).fnAddFragWithCustomAnimation(Constants.IMPORT_CONTACTS, true, bundle);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventBusGetLnqedContactList(userContactsDataList));
            }
        }, 100);
    }
}