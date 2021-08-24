package lnq.com.lnq.fragments.registeration.createprofile;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.CreateContactGroupAdapter;
import lnq.com.lnq.adapters.ProfileImagesAdapter;
import lnq.com.lnq.adapters.ShowProfilesImagesAdapter;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.image_compressor.Luban;
import lnq.com.lnq.custom.image_compressor.OnCompressListener;
import lnq.com.lnq.custom.views.camera.CameraPreview;
import lnq.com.lnq.custom.views.gallery.EventGalleryModel;
import lnq.com.lnq.custom.views.gallery.GalleryAdapter;
import lnq.com.lnq.custom.views.gallery.GalleryModel;
import lnq.com.lnq.databinding.FragCreateProfilePictureBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.connections.FragmentConnections;
import lnq.com.lnq.fragments.gallery.GalleryAdopter;
import lnq.com.lnq.fragments.gallery.GalleryFragmentFunctions;
import lnq.com.lnq.fragments.gallery.GalleryModelNew;
import lnq.com.lnq.model.ShowProfileImagesModel;
import lnq.com.lnq.model.event_bus_models.EventBusCameraPermission;
import lnq.com.lnq.model.event_bus_models.EventBusCaptureImage;
import lnq.com.lnq.model.event_bus_models.EventBusCheckPermission;
import lnq.com.lnq.model.event_bus_models.EventBusGalleryPermission;
import lnq.com.lnq.model.event_bus_models.EventBusLoadImage;
import lnq.com.lnq.model.event_bus_models.EventBusOpenCameraSecondaryProfile;
import lnq.com.lnq.model.event_bus_models.EventBusTakeNewPhoto;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;

public class FragmentCreateProfilePicture extends Fragment implements GalleryAdopter.onClickAdapterInterface {

    //    Constant fields....
    private static final int RC_STORAGE_PERMISSION = 2;

    //    Android fields....
    private FragCreateProfilePictureBinding createProfilePictureBinding;
    private CreateProfilePictureClickHandler clickHandler;
    private Camera camera;
    private CameraPreview cameraPreview;

    //    Callback fields....
    private Camera.PictureCallback pictureCallback;

    //    Adapter fields....
    private GalleryAdapter galleryAdapter;

    //    Instance fields....
    private String profileImagePath;
    private String profileImageFromProfiles;
    private AppCompatImageView imageViewBackTopBar;
    //    Gallery fields....
    private ArrayList<GalleryModel> galleryModelList;
    private String[] albumArray;
    private int lastIndex = -1;
    private String selectedImagePath;
    private boolean isImageSelected;
    String secondaryProfileId;
    //    Camera fields....
    private boolean cameraFront = false;
    private Bitmap imageBitmap;
    Bitmap bitmap;
    private AsyncTaskLoadImages asyncTaskLoadImages;
    BottomSheetDialogFragment myBottomSheet;
    private ConstraintLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    TextView mImgDone, mTvTitle;
    AppCompatImageView imageViewArrowDown;
    private int posOld = 0;
    GalleryAdopter adopter;
    GalleryFragmentFunctions fragmentNew;
    private int faceTrackingId;
    Dialog dialog;
    List<ShowProfileImagesModel> profileImagesList = new ArrayList<>();
    private List<MultiProfileRoomModel> proflieList = new ArrayList<>();
    ShowProfilesImagesAdapter showProfilesImagesAdapter;

    List<GalleryModelNew> galleryModelNews = new ArrayList<>();
    List<String> listOfSelected = new ArrayList<>();

    //    Font fields....
    private FontUtils fontUtils;

    int lastImageClickedIndex = -1;

    public FragmentCreateProfilePicture() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createProfilePictureBinding = DataBindingUtil.inflate(inflater, R.layout.frag_create_profile_picture, container, false);
        return createProfilePictureBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                List<String> result = (List<String>) bundle.getSerializable("bundleKey");
                Luban.with(getActivity())
                        .load(result)
                        .ignoreBy(100)
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                selectedImagePath = String.valueOf(file);
                                if (selectedImagePath != null && !selectedImagePath.equals("")) {
                                    Glide.with(getActivity())
                                            .load(selectedImagePath)
//                                    .apply(new RequestOptions().centerCrop())
//                                    .apply(new RequestOptions().circleCrop())
                                            .into(createProfilePictureBinding.imageViewProfilePicture);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }).launch();

//                myBottomSheet.dismiss();
                myBottomSheet.setCancelable(false);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView topBarLayout = createProfilePictureBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.profile_picture);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.INVISIBLE);

        mBottomSheetLayout = view.findViewById(R.id.galleryBottomSheet);
        mImgDone = view.findViewById(R.id.mImgDone);
        mTvTitle = view.findViewById(R.id.mTvTitle);
        imageViewArrowDown = view.findViewById(R.id.imageViewArrowDown);

        init();
        setCustomFonts();
    }

    private void init() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            createProfilePictureBinding.mPb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }
//        Registering event bus....
        EventBus.getDefault().register(this);
//        secondaryProfileId = LnqApplication.getInstance().sharedPreferences.getString("secondaryProfileId", "");

        galleryModelList = new ArrayList<>();
        if (getArguments() != null) {
            if (getArguments().getString(EndpointKeys.TYPE, "").equalsIgnoreCase("edit")) {
                createProfilePictureBinding.imageViewCapturePicture.setVisibility(View.GONE);
                createProfilePictureBinding.cameraView.setVisibility(View.GONE);
                createProfilePictureBinding.cardViewCameraContainer.setVisibility(View.GONE);
                createProfilePictureBinding.viewOrOne.setVisibility(View.GONE);
                createProfilePictureBinding.viewOrTwo.setVisibility(View.GONE);
                createProfilePictureBinding.textViewOr.setVisibility(View.GONE);
                createProfilePictureBinding.mImgCamera.setVisibility(View.GONE);
                createProfilePictureBinding.imageViewProfilePicture.setVisibility(View.VISIBLE);
                createProfilePictureBinding.imageViewProfilePictureBorder.setVisibility(View.VISIBLE);
                createProfilePictureBinding.buttonUploadPicture.setBackground(getResources().getDrawable(R.mipmap.btn_blue_newtheme));
                createProfilePictureBinding.buttonUploadPicture.setText(getResources().getString(R.string.okay));
                createProfilePictureBinding.buttonUploadPicture.setTextColor(getResources().getColor(R.color.colorWhite));
                isImageSelected = true;
                selectedImagePath = getArguments().getString(EndpointKeys.IMAGE_PATH, "");
                if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                    Glide.with(getActivity())
                            .load(selectedImagePath)
                            .into(createProfilePictureBinding.imageViewProfilePicture);
                }
            } else if (getArguments().getString(EndpointKeys.TYPE, "").equalsIgnoreCase("secondary_profile_image")) {
                MultiProfileRepositry repositry = new MultiProfileRepositry(getContext());
                repositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
                    @Override
                    public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                        proflieList.clear();
                        proflieList.addAll(multiProfileRoomModels);
                        for (MultiProfileRoomModel data : proflieList) {
                            if (data.getUser_avatar() != null && !data.getUser_avatar().isEmpty()) {
                                profileImagesList.add(new ShowProfileImagesModel(data.getUser_avatar(),false));
                            }
                        }
                        onShowProfileImages();
                    }
                });
                secondaryProfileId = getArguments().getString("secondaryProfileId");
                createProfilePictureBinding.imageViewCapturePicture.setVisibility(View.VISIBLE);
                createProfilePictureBinding.cameraView.setVisibility(View.VISIBLE);
                createProfilePictureBinding.cardViewCameraContainer.setVisibility(View.VISIBLE);
                createProfilePictureBinding.viewOrOne.setVisibility(View.VISIBLE);
                createProfilePictureBinding.viewOrTwo.setVisibility(View.VISIBLE);
                createProfilePictureBinding.textViewOr.setVisibility(View.VISIBLE);
                createProfilePictureBinding.mImgCamera.setVisibility(View.GONE);
                createProfilePictureBinding.imageViewProfilePicture.setVisibility(View.INVISIBLE);
                createProfilePictureBinding.imageViewProfilePictureBorder.setVisibility(View.INVISIBLE);
                createProfilePictureBinding.buttonUploadPicture.setText(getResources().getString(R.string.upload_photo));
                createProfilePictureBinding.buttonUploadPicture.setBackground(getResources().getDrawable(R.mipmap.btn_get_started));
                createProfilePictureBinding.buttonUploadPicture.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
                isImageSelected = false;
                if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
                    EventBus.getDefault().post(new EventBusOpenCameraSecondaryProfile());
                } else {
                    ((MainActivity) getActivity()).fnRequestCameraPermission(10);
                }
            } else {
                if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fnOpenCamera();
                        }
                    }, 500);
                } else {
                    ((MainActivity) getActivity()).fnRequestCameraPermission(3);
                }
            }
        } else {
            if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fnOpenCamera();
                    }
                }, 500);
            } else {
                ((MainActivity) getActivity()).fnRequestCameraPermission(3);
            }
        }

//        fragmentNew = new GalleryFragmentFunctions();
//        if (((MainActivity) getActivity()).fnCheckStoragePermission()) {
//            fragmentNew.loadGalleryData(getContext());
//        } else {
//            ((MainActivity) getActivity()).fnRequestStoragePermission(2);
//        }
//        galleryModelNews = fragmentNew.getListGallery();
//        listOfSelected = fragmentNew.getListSelection();
//
//        createProfilePictureBinding.mRv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
//        adopter = new GalleryAdopter(galleryModelNews, this);
//        createProfilePictureBinding.mRv.setAdapter(adopter);

//        Setting click listener for data binding....
        clickHandler = new CreateProfilePictureClickHandler(getActivity());
        createProfilePictureBinding.setClickHandler(clickHandler);

//        ValidUtils.buttonGradientColor(createProfilePictureBinding.buttonUploadPicture);
    }

    public Bitmap screenShot(CardView view) {
        bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return bitmap;
    }

    public void setCustomFonts() {
        fontUtils = FontUtils.getFontUtils(getActivity());

        fontUtils.setTextViewRegularFont(createProfilePictureBinding.textViewCreateProfilePictureDes);
        fontUtils.setButtonRegularFont(createProfilePictureBinding.buttonUploadPicture);
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
            if (camera == null && cameraPreview != null) {
                chooseCamera();
                cameraFront = false;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        lastIndex = -1;
    }


    public void getBitmap(String path) {
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            imageBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusOpenCamera(EventBusOpenCameraSecondaryProfile mObj) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fnOpenCamera();
            }
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectEvent(EventGalleryModel mObj) {
        if (lastIndex != mObj.getmPos()) {
            if (lastIndex != -1) {
                galleryModelList.get(lastIndex).setmSelected(false);
            }
            galleryModelList.get(mObj.getmPos()).setmSelected(true);
//            selectedImagePath = galleryModelList.get(mObj.getmPos()).getmPath();
            if (lastIndex != -1) {
                galleryAdapter.notifyItemChanged(lastIndex);
            }
            galleryAdapter.notifyItemChanged(mObj.getmPos());
            lastIndex = mObj.getmPos();
            if (selectedImagePath != null && !selectedImagePath.equals("")) {
                Glide.with(getActivity())
                        .load(selectedImagePath)
//                        .apply(new RequestOptions().centerCrop())
//                        .apply(new RequestOptions().circleCrop())
                        .into(createProfilePictureBinding.imageViewProfilePicture);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openCameraEvent(EventBusCameraPermission eventBusCameraPermission) {
        if (eventBusCameraPermission.isAllowed()) {
            fnOpenCamera();
        } else {
            if (((MainActivity) getActivity()).fnCheckStoragePermission()) {
                openGallery();
            } else {
                ((MainActivity) getActivity()).fnRequestStoragePermission(2);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void takeNewPhotoEvent(EventBusTakeNewPhoto eventBusTakeNewPhoto) {
        init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void albumArrayEvent(EventBusGalleryPermission eventBusGalleryPermission) {
        if (eventBusGalleryPermission.isAllowed()) {
            if (galleryModelList.size() == 0) {
                openGallery();
            }
        } else {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_REQUIRED, true, null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusCaptureImage(EventBusCaptureImage eventBusCaptureImage) {
        if (camera != null) {
            camera.takePicture(null, null, pictureCallback);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusCheckPermission(EventBusCheckPermission mObj) {
        if (galleryModelList.size() == 0) {
            openGallery();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusLoadImage(EventBusLoadImage mObj) {
        profileImageFromProfiles = profileImagesList.get(mObj.getIndex()).getImagePath();
        profileImagesList.get(mObj.getIndex()).setSelected(true);
        showProfilesImagesAdapter.notifyItemChanged(mObj.getIndex());
        if(lastImageClickedIndex != -1){
            profileImagesList.get(lastImageClickedIndex).setSelected(false);
            showProfilesImagesAdapter.notifyItemChanged(lastImageClickedIndex);
        }
        lastImageClickedIndex = mObj.getIndex();
    }

    //    Method to open mobile camera....
    public void fnOpenCamera() {
        camera = Camera.open();
        cameraPreview = new CameraPreview(getActivity(), getActivity(), camera);
        pictureCallback = getPictureCallback();
        createProfilePictureBinding.cameraView.addView(cameraPreview);
        int camerasNumber = Camera.getNumberOfCameras();
        if (camerasNumber > 1) {
            releaseCamera();
            chooseCamera();
            cameraFront = false;
        }
    }

    public void onShowProfileImages() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.cus_dialog_showprofile_images, null);
        RecyclerView recyclerViewAddGroupContactList = dialogView.findViewById(R.id.recyclerViewAddImages);
        Button clearTextViewCreateGroup = dialogView.findViewById(R.id.clearTextViewUse);
        Button clearTextViewCancel = dialogView.findViewById(R.id.clearTextViewCancel);

        recyclerViewAddGroupContactList.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        showProfilesImagesAdapter = new ShowProfilesImagesAdapter(getActivity(), profileImagesList);
        recyclerViewAddGroupContactList.setAdapter(showProfilesImagesAdapter);

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        clearTextViewCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileImageFromProfiles != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EndpointKeys.TYPE, "profile_image");
                    bundle.putString("secondaryProfileid", secondaryProfileId);
                    bundle.putString(EndpointKeys.IMAGE_PATH, profileImageFromProfiles);
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_LOOKS_GOOD, true, bundle);
                    dialog.cancel();
                }else {
                    ValidUtils.showCustomToast(getContext(), "Please select profile image you want to use.");
                }
            }
        });

        clearTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        try {
            dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

        } catch (Exception e) {

        }
    }

//    public void openGallery() {
//        asyncTaskLoadImages = new AsyncTaskLoadImages();
//        asyncTaskLoadImages.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        createProfilePictureBinding.recyclerViewGallery.setVisibility(View.VISIBLE);
//        createProfilePictureBinding.buttonUploadPicture.setText(getResources().getString(R.string.ok));
//        createProfilePictureBinding.buttonUploadPicture.setBackground(getResources().getDrawable(R.drawable.bg_verification_white_btn));
//        createProfilePictureBinding.buttonUploadPicture.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
//        createProfilePictureBinding.fabChooseFolder.show();
//        releaseCamera();
//        createProfilePictureBinding.imageViewCapturePicture.setVisibility(View.GONE);
//        createProfilePictureBinding.cameraView.setVisibility(View.GONE);
//        createProfilePictureBinding.cardViewCameraContainer.setVisibility(View.GONE);
//        createProfilePictureBinding.viewOrOne.setVisibility(View.GONE);
//        createProfilePictureBinding.viewOrTwo.setVisibility(View.GONE);
//        createProfilePictureBinding.textViewOr.setVisibility(View.GONE);
//        createProfilePictureBinding.mImgCamera.setVisibility(View.GONE);
//        createProfilePictureBinding.imageViewProfilePicture.setVisibility(View.VISIBLE);
//        createProfilePictureBinding.imageViewProfilePictureBorder.setVisibility(View.VISIBLE);
//        isImageSelected = true;
//    }

    @SuppressLint("ResourceType")
    public void openGallery() {
        asyncTaskLoadImages = new AsyncTaskLoadImages();
        asyncTaskLoadImages.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        createProfilePictureBinding.recyclerViewGallery.setVisibility(View.GONE);
        createProfilePictureBinding.buttonUploadPicture.setBackground(getResources().getDrawable(R.mipmap.btn_blue_newtheme));
        createProfilePictureBinding.buttonUploadPicture.setText(getResources().getString(R.string.okay));
        createProfilePictureBinding.buttonUploadPicture.setTextColor(getResources().getColor(R.color.colorWhite));
//        createProfilePictureBinding.fabChooseFolder.show();
        releaseCamera();
        createProfilePictureBinding.imageViewCapturePicture.setVisibility(View.GONE);
        createProfilePictureBinding.cameraView.setVisibility(View.GONE);
        createProfilePictureBinding.cardViewCameraContainer.setVisibility(View.GONE);
        createProfilePictureBinding.viewOrOne.setVisibility(View.GONE);
        createProfilePictureBinding.viewOrTwo.setVisibility(View.GONE);
        createProfilePictureBinding.textViewOr.setVisibility(View.GONE);
        createProfilePictureBinding.mImgCamera.setVisibility(View.GONE);
        createProfilePictureBinding.imageViewProfilePicture.setVisibility(View.VISIBLE);
        createProfilePictureBinding.imageViewProfilePictureBorder.setVisibility(View.VISIBLE);
        isImageSelected = true;
////        myBottomSheet = GalleryFragmentNew.newInstance("single");
//        myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
        fragmentNew = new GalleryFragmentFunctions();
        fragmentNew.loadGalleryData(getContext());
        galleryModelNews = fragmentNew.getListGallery();
        listOfSelected = fragmentNew.getListSelection();

        createProfilePictureBinding.mRv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        adopter = new GalleryAdopter(galleryModelNews, this);
        createProfilePictureBinding.mRv.setAdapter(adopter);
        mBottomSheetLayout.setVisibility(View.VISIBLE);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mBottomSheetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        mImgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> image = fragmentNew.onUploadClick(getActivity());
                Luban.with(getActivity())
                        .load(image)
                        .ignoreBy(100)
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                selectedImagePath = String.valueOf(file);
                                if (selectedImagePath != null && !selectedImagePath.equals("")) {
                                    Glide.with(getActivity())
                                            .load(selectedImagePath)
                                            .into(createProfilePictureBinding.imageViewProfilePicture);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }).launch();
            }
        });

        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentNew.onFoldersClick(getContext(), mTvTitle, adopter);
            }
        });

        imageViewArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentNew.onFoldersClick(getContext(), mTvTitle, adopter);
            }
        });

    }

//            int state = 0;
//        BottomSheetBehavior bottomSheetBehavior;
//        bottomSheetBehavior = BottomSheetBehavior.from(createProfilePictureBinding.getRoot());
//        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//            state = BottomSheetBehavior.STATE_COLLAPSED;
//        }
//        else {
//           state = BottomSheetBehavior.STATE_EXPANDED;
//        }
//        bottomSheetBehavior.setState(state);

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = (data, camera) -> {
            if (data != null) {
                if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
                    releaseCamera();
                    if (camera == null && cameraPreview != null) {
                        chooseCamera();
                        cameraFront = false;
                    }
                }
                LnqApplication.getInstance().editor.putString(EndpointKeys.AVATAR_FROM, Constants.SELFIE).apply();
                ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
                imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                    imageBitmap = flip(imageBitmap, FragmentProfileLooksGood.Direction.HORIZONTAL);
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
                    profileImagePath = f.getAbsolutePath();
                    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
                    if (bitmap != null) {
                        detectFaces(firebaseVisionImage, Constants.CAMERA, profileImagePath, bitmap);
                    }
                } catch (IOException e) {
                    ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                }
            }
        };
        return picture;
    }

    private void detectFaces(FirebaseVisionImage image, final String imageSelectType, final String imagePath, Bitmap bitmap1) {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .enableTracking()
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        if (faces.size() == 1) {
                                            LnqApplication.getInstance().editor.putInt("face_tracking_id", faces.get(0).getTrackingId());
                                            faceTrackingId = LnqApplication.getInstance().sharedPreferences.getInt("face_tracking_id", -1);
                                            if (faceTrackingId != -1) {
                                                if (faceTrackingId == faces.get(0).getTrackingId()) {
                                                    ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                                                    Bundle bundle = new Bundle();
                                                    if (imageSelectType.equals(Constants.CAMERA)) {
                                                        bundle.putString(EndpointKeys.AVATAR_FROM, Constants.CAMERA);
                                                    } else
                                                        bundle.putString(EndpointKeys.AVATAR_FROM, Constants.GALLERY);
                                                    bundle.putString(EndpointKeys.IMAGE_PATH, imagePath);
                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                    bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                    byte[] byteArray = stream.toByteArray();
                                                    bundle.putByteArray("bitmap", byteArray);
                                                    if (getArguments() != null) {
                                                        if (getArguments().getString(EndpointKeys.TYPE, "").equals("edit")) {
                                                            bundle.putString(EndpointKeys.TYPE, "edit");
                                                            bundle.putString("secondaryProfileid", secondaryProfileId);
                                                        } else if (getArguments().getString(EndpointKeys.TYPE, "").equals("secondary_profile_image")) {
                                                            bundle.putString(EndpointKeys.TYPE, "secondary_profile_image");
                                                            bundle.putString("secondaryProfileid", secondaryProfileId);
                                                        }
                                                    } else {
                                                        bundle.putString(EndpointKeys.TYPE, Constants.CREATE_PROFILE);
                                                    }
                                                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_LOOKS_GOOD, true, bundle);
                                                } else {
                                                    Toast.makeText(getContext(), "Face detected but not match with your primary face", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                                                Bundle bundle = new Bundle();
                                                LnqApplication.getInstance().editor.putInt("face_tracking_id", faces.get(0).getTrackingId());
                                                if (imageSelectType.equals(Constants.CAMERA)) {
                                                    bundle.putString(EndpointKeys.AVATAR_FROM, Constants.CAMERA);
                                                } else
                                                    bundle.putString(EndpointKeys.AVATAR_FROM, Constants.GALLERY);
                                                bundle.putString(EndpointKeys.IMAGE_PATH, imagePath);
                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                byte[] byteArray = stream.toByteArray();
                                                bundle.putByteArray("bitmap", byteArray);
                                                if (getArguments() != null) {
                                                    if (getArguments().getString(EndpointKeys.TYPE, "").equals("edit")) {
                                                        bundle.putString(EndpointKeys.TYPE, "edit");
                                                        bundle.putString("secondaryProfileid", secondaryProfileId);
                                                    } else if (getArguments().getString(EndpointKeys.TYPE, "").equals("secondary_profile_image")) {
                                                        bundle.putString(EndpointKeys.TYPE, "secondary_profile_image");
                                                        bundle.putString("secondaryProfileid", secondaryProfileId);
                                                    }
                                                } else {
                                                    bundle.putString(EndpointKeys.TYPE, Constants.CREATE_PROFILE);
                                                }
                                                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_LOOKS_GOOD, true, bundle);
                                            }
                                        } else if (faces.size() > 1) {
                                            ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                                            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.single_face_image));
                                            releaseCamera();
                                            chooseCamera();
                                        } else {
                                            ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                                            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.select_human_image));
                                            releaseCamera();
                                            chooseCamera();
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                                    }
                                });
    }

    public Bitmap flip(Bitmap src, FragmentProfileLooksGood.Direction type) {
        Matrix matrix = new Matrix();
        if (type == FragmentProfileLooksGood.Direction.VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        } else if (type == FragmentProfileLooksGood.Direction.HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }
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
                cameraFront = true;
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
                cameraFront = false;
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

    @Override
    public void onClickDealsItem(@NonNull GalleryModelNew objGallery) {
        int pos = galleryModelNews.indexOf(objGallery);
        if (listOfSelected.contains(galleryModelNews.get(pos).getPath())) {
            return;
        }
        galleryModelNews.get(posOld).setSelected(false);
        adopter.notifyItemChanged(posOld);
        listOfSelected.clear();
        listOfSelected.add(0, galleryModelNews.get(pos).getPath());
        galleryModelNews.get(pos).setSelected(true);
        adopter.notifyItemChanged(pos);
        posOld = pos;
    }

    //    Async task to load images from gallery to recycler view....
    private class AsyncTaskLoadImages extends AsyncTask<Boolean, Void, Boolean> {

        private AsyncTaskLoadImages() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createProfilePictureBinding.mPb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (createProfilePictureBinding.mPb.getVisibility() == View.VISIBLE) {
                String[] projection = new String[]{
                        MediaStore.Images.Media.DATA
                };
                // content:// style URI for the "primary" external storage volume
                // Make the query.
                Cursor cur = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, // Which columns to return
                        MediaStore.Images.Media.DATA + " like ? ",       // Which rows to return (all rows)
                        new String[]{"%" + "Camera" + "%"},       // Selection arguments (none)
                        null        // Ordering
                );
                if (cur != null && cur.moveToFirst()) {
//                int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//                int nameColumn = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    int datColum = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                    do {
                        galleryModelList.add(new GalleryModel("", cur.getString(datColum), "", false));
                    } while (cur.moveToNext());
                    cur.close();
                    // query for albums
                    String[] mAlbums = new String[]{"DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};
                    Cursor mCurAlbum = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mAlbums, null, null, null);
                    albumArray = new String[mCurAlbum.getCount() + 1];
                    albumArray[0] = "All Images";
                    while (mCurAlbum.moveToNext()) {
                        albumArray[mCurAlbum.getPosition() + 1] = mCurAlbum.getString((mCurAlbum.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                    }
                    mCurAlbum.close();
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean mVal) {
            super.onPostExecute(mVal);
            createProfilePictureBinding.mPb.setVisibility(View.INVISIBLE);
            if (mVal) {
                createProfilePictureBinding.recyclerViewGallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                galleryAdapter = new GalleryAdapter(getActivity(), galleryModelList);
                createProfilePictureBinding.recyclerViewGallery.setAdapter(galleryAdapter);
            } else {
                Toast.makeText(getActivity(), "No Pictures Found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CreateProfilePictureClickHandler {

        private Context context;

        public CreateProfilePictureClickHandler(Context context) {
            this.context = context;
        }

        public void onCaptureImageClick(View view) {
            if (createProfilePictureBinding.mPb.getVisibility() == View.VISIBLE) {
                return;
            }
            if (camera != null) {
                camera.takePicture(null, null, pictureCallback);
            }
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void onUploadClick(View view) {
            if (createProfilePictureBinding.mPb.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!isImageSelected) {
                if (((MainActivity) getActivity()).fnCheckStoragePermission()) {
                    EventBus.getDefault().post(new EventBusCheckPermission());
                } else {
                    ((MainActivity) getActivity()).fnRequestStoragePermission(RC_STORAGE_PERMISSION);
                }
            } else {
                if (selectedImagePath != null) {
                    LnqApplication.getInstance().editor.putString(EndpointKeys.AVATAR_FROM, Constants.GALLERY).apply();
                    getBitmap(selectedImagePath != null ? selectedImagePath : "");
                    screenShot(createProfilePictureBinding.cardViewCameraContainer1);
                    ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
                    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
                    if (bitmap != null) {
                        detectFaces(firebaseVisionImage, Constants.GALLERY, selectedImagePath, bitmap);
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.select_image_first), Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void onFabClick(View view) {
            if (createProfilePictureBinding.mPb.getVisibility() == View.VISIBLE) {
                return;
            }
            AlertDialog.Builder mAlertBuilder;
            mAlertBuilder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
            mAlertBuilder.setItems(albumArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int mSizeLst = galleryModelList.size();
                    galleryModelList.clear();
                    galleryAdapter.notifyItemRangeRemoved(0, mSizeLst);
                    String[] projection = {MediaStore.Images.Media.DATA};
                    switch (albumArray[i]) {
                        case "All Images":
                            Cursor curAll = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    projection, // Which columns to return
                                    null,       // Which rows to return (all rows)
                                    null,       // Selection arguments (none)
                                    null        // Ordering
                            );
                            if (curAll != null && curAll.moveToFirst()) {
                                int datColum = curAll.getColumnIndex(MediaStore.Images.Media.DATA);
                                do {
                                    galleryModelList.add(new GalleryModel("", curAll.getString(datColum), "", false));
                                } while (curAll.moveToNext());
                                curAll.close();
                            } else {
                                Toast.makeText(getActivity(), "No Pictures Found", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            Cursor cur = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    projection, // Which columns to return
                                    MediaStore.Images.Media.DATA + " like ? ",       // Which rows to return (all rows)
                                    new String[]{"%" + albumArray[i] + "%"},       // Selection arguments (none)
                                    null        // Ordering
                            );
                            int index = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            while (cur.moveToNext()) {
                                galleryModelList.add(new GalleryModel("", cur.getString(index), "", false));
                            }
                            cur.close();
                            break;
                    }
                    galleryAdapter.notifyItemRangeInserted(0, galleryModelList.size());
                }
            });
            mAlertBuilder.setNegativeButton("Cancel", null);
            mAlertBuilder.show();
        }

    }

}