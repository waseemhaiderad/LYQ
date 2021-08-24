package lnq.com.lnq.fragments.registeration.profileverification;


import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.hardware.Camera;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.custom.views.camera.CameraPreview;
import lnq.com.lnq.databinding.FragmentProfileVerifyTakePhotoBinding;
import lnq.com.lnq.utils.ValidUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfileVerifyTakePhoto extends Fragment implements View.OnClickListener {

    private FragmentProfileVerifyTakePhotoBinding mBind;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private Camera.PictureCallback mPicture;
    private boolean cameraFront = false;
    AppCompatImageView imageViewBackTopBar;

    public FragmentProfileVerifyTakePhoto() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_verify_take_photo, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        CardView topBarLayout = mBind.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.verify_profile_picture);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void init() {
        if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
            fnOpenCamera();
        }

        mBind.mImgDot.setOnClickListener(this);

        ValidUtils.textViewGradientColor(mBind.mTvVerifyProfileTakePhotoDes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImgDot:
                if (mCamera != null) {
                    mCamera.takePicture(null, null, mPicture);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
            releaseCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
            if (mCamera == null) {
                chooseCamera();
                cameraFront = false;
                Log.d("nu", "null");
            } else {
                Log.d("nu", "no null");
            }
        }
    }

    //    Method to open mobile camera....
    public void fnOpenCamera() {
        mCamera = Camera.open();
        mCameraPreview = new CameraPreview(getActivity(), getActivity(), mCamera);
        mPicture = getPictureCallback();
        mBind.mCameraView.addView(mCameraPreview);
        int camerasNumber = Camera.getNumberOfCameras();
        if (camerasNumber > 1) {
            releaseCamera();
            chooseCamera();
            cameraFront = false;
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data != null) {
                    if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
                        releaseCamera();
                        if (mCamera == null) {
                            chooseCamera();
                            cameraFront = false;
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("mFlag", "camera");
                    bundle.putByteArray("data", data);
                    bundle.putString("type", "verify_profile");
                    ((MainActivity) getActivity()).fnLoadFragAdd("PROFILE LOOKS GOOD", true, bundle);
                }
            }
        };
        return picture;
    }

    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;

    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;

            }

        }
        return cameraId;
    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mCameraPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mCameraPreview.refreshCamera(mCamera);
            }
        }
    }
}
