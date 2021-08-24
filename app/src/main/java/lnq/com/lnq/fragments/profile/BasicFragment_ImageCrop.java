package lnq.com.lnq.fragments.profile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.util.Logger;
import com.isseiaoki.simplecropview.util.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.CompletableSource;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.image_compressor.Luban;
import lnq.com.lnq.custom.image_compressor.OnCompressListener;
import lnq.com.lnq.custom.views.camera.CameraPreview;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.gallery.GalleryFragmentNew;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentProfileLooksGood;
import lnq.com.lnq.model.event_bus_models.EventBusCameraPermission;
import lnq.com.lnq.model.event_bus_models.EventBusCaptureImage;
import lnq.com.lnq.utils.ValidUtils;


public class BasicFragment_ImageCrop extends Fragment {
    private String cachePath = "";
    private TransferUtility transferUtility;

    private static final String TAG = BasicFragment_ImageCrop.class.getSimpleName();

    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    private static final String PROGRESS_DIALOG = "ProgressDialog";
    private static final String KEY_FRAME_RECT = "FrameRect";
    private static final String KEY_SOURCE_URI = "SourceUri";

    // Views ///////////////////////////////////////////////////////////////////////////////////////
    private CropImageView mCropView;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private RectF mFrameRect = null;
    private Uri mSourceUri = null;
    private Camera camera;
    private CameraPreview cameraPreview;
    private boolean cameraFront = false;
    private Bitmap imageBitmap;
    private String profileImagePath;
    private LinearLayout cameraView;
    private HorizontalScrollView horizontalScrollView;
    private RelativeLayout relativeLayout;
    private View divider;
    AppCompatTextView textViewBack, textViewGallery, textViewSelfie;
    BottomSheetDialogFragment myBottomSheet;

    //    Callback fields....
    private Camera.PictureCallback pictureCallback;
    private int faceTrackingId;

    // Note: only the system can call this constructor by reflection.
    public BasicFragment_ImageCrop() {
    }

    public static BasicFragment_ImageCrop newInstance() {
        BasicFragment_ImageCrop fragment = new BasicFragment_ImageCrop();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@androidx.annotation.NonNull String requestKey, @androidx.annotation.NonNull Bundle bundle) {
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
                                File imagePath = file;
                                Glide.with(getActivity())
                                        .load(file)
                                        .into(mCropView);
                                mCropView.setVisibility(View.VISIBLE);
                                horizontalScrollView.setVisibility(View.VISIBLE);
                                relativeLayout.setVisibility(View.VISIBLE);
                                cameraView.setVisibility(View.GONE);
                                divider.setVisibility(View.VISIBLE);
                                releaseCamera();
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }).launch();

                myBottomSheet.dismiss();
            }
        });
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basic__image_crop, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransferUtility();
        cachePath = getContext().getCacheDir().getAbsolutePath();
        cameraView = view.findViewById(R.id.cameraView);
        horizontalScrollView = view.findViewById(R.id.tab_bar);
        relativeLayout = view.findViewById(R.id.relativeLayoutCropOptions);
        divider = view.findViewById(R.id.divider);
        // bind Views
        bindViews(view);

        if (savedInstanceState != null) {
            // restore data
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
            mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
        }

        if (mSourceUri == null) {
            String imagePath = getActivity().getIntent().getStringExtra("profileImage");
            int res = getResources().getIdentifier(imagePath, "string", getContext().getPackageName());

            if (imagePath != null && !imagePath.isEmpty()) {
                download(imagePath, mCropView);
            }

            // default data
//            mSourceUri = getUriFromDrawableResId(getContext(), res);
        }
        // load image
        mDisposable.add(loadImage(mSourceUri));
        ValidUtils.textViewGradientColor(textViewBack);
        ValidUtils.textViewGradientColor(textViewGallery);
        ValidUtils.textViewGradientColor(textViewSelfie);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save data
        outState.putParcelable(KEY_FRAME_RECT, mCropView.getActualCropRect());
        outState.putParcelable(KEY_SOURCE_URI, mCropView.getSourceUri());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDisposable.dispose();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            // reset frame rect
            mFrameRect = null;
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    mDisposable.add(loadImage(result.getData()));
                    break;
                case REQUEST_SAF_PICK_IMAGE:
                    mDisposable.add(loadImage(Utils.ensureUriPermission(getContext(), result)));
                    break;
            }
        }
    }

//    private void download(String objectKey, ImageView imageView) {
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
//                        Glide.with(getActivity())
//                                .asBitmap()
//                                .load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()))
//                                .into(new CustomTarget<Bitmap>() {
//                                    @Override
//                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                        imageView.setImageBitmap(resource);
//                                    }
//
//                                    @Override
//                                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                                    }
//                                });
//                    }
////                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
//
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

    private void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            Glide.with(getActivity()).
                    asBitmap().
                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
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
                        Glide.with(getActivity()).
                                asBitmap().
                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                                apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        imageView.setImageBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
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

    private Disposable loadImage(final Uri uri) {
        mSourceUri = uri;
        return new RxPermissions(getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(@NonNull Boolean granted)
                            throws Exception {
                        return granted;
                    }
                })
                .flatMapCompletable(new Function<Boolean, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Boolean aBoolean)
                            throws Exception {
                        return mCropView.load(uri)
                                .useThumbnail(true)
                                .initialFrameRect(mFrameRect)
                                .executeAsCompletable();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                    }
                });
    }

    private Disposable cropImage() {
        return mCropView.crop(mSourceUri)
                .executeAsSingle()
                .flatMap(new Function<Bitmap, SingleSource<Uri>>() {
                    @Override
                    public SingleSource<Uri> apply(@io.reactivex.annotations.NonNull Bitmap bitmap)
                            throws Exception {
                        return mCropView.save(bitmap)
                                .compressFormat(mCompressFormat)
                                .executeAsSingle(createSaveUri());
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable)
                            throws Exception {
                        showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissProgress();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Uri uri) throws Exception {
                        ((ImageCropActivity) getActivity()).startResultActivity(uri);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
                            throws Exception {
                    }
                });
    }

    // Bind views //////////////////////////////////////////////////////////////////////////////////

    private void bindViews(View view) {
        mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
        view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
        view.findViewById(R.id.button1_1).setOnClickListener(btnListener);
        view.findViewById(R.id.button3_4).setOnClickListener(btnListener);
        view.findViewById(R.id.button4_3).setOnClickListener(btnListener);
        view.findViewById(R.id.button9_16).setOnClickListener(btnListener);
        view.findViewById(R.id.button16_9).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonFree).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
        textViewGallery = view.findViewById(R.id.textViewGallery);
        textViewGallery.setOnClickListener(btnListener);
        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setOnClickListener(btnListener);
        textViewSelfie = view.findViewById(R.id.textViewSelfie);
        textViewSelfie.setOnClickListener(btnListener);
        view.findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(btnListener);
    }

    public void showProgress() {
        ProgressDialogFragmentImageCrop f = ProgressDialogFragmentImageCrop.getInstance();
        getFragmentManager().beginTransaction().add(f, PROGRESS_DIALOG).commitAllowingStateLoss();
    }

    public void dismissProgress() {
        if (!isResumed()) return;
        FragmentManager manager = getFragmentManager();
        if (manager == null) return;
        ProgressDialogFragmentImageCrop f = (ProgressDialogFragmentImageCrop) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    public Uri createSaveUri() {
        return createNewUri(getContext(), mCompressFormat);
    }

    public static String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/simplecropview");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public static Uri getUriFromDrawableResId(Context context, int drawableResId) {
        StringBuilder builder = new StringBuilder().append(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .append("://")
                .append(context.getResources().getResourcePackageName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceTypeName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceEntryName(drawableResId));
        return Uri.parse(builder.toString());
    }

    public static Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "scv" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.i("SaveUri = " + uri);
        return uri;
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }

    // Handle button event /////////////////////////////////////////////////////////////////////////

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonDone:
                    if (mCropView.getCroppedBitmap() != null) {
                        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(mCropView.getCroppedBitmap());
                        detectFaces(firebaseVisionImage);
                    }
                    break;
                case R.id.buttonFitImage:
                    mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
                    break;
                case R.id.button1_1:
                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                    break;
                case R.id.button3_4:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                    break;
                case R.id.button4_3:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                    break;
                case R.id.button9_16:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                    break;
                case R.id.button16_9:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                    break;
                case R.id.buttonCustom:
                    mCropView.setCustomRatio(7, 5);
                    break;
                case R.id.buttonFree:
                    mCropView.setCropMode(CropImageView.CropMode.FREE);
                    break;
                case R.id.buttonCircle:
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                    break;
                case R.id.buttonShowCircleButCropAsSquare:
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
                    break;
                case R.id.buttonRotateLeft:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.buttonRotateRight:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
                case R.id.textViewGallery:
                    onGalleryClick();
                    break;
                case R.id.textViewBack:
                    getActivity().onBackPressed();
                    break;
                case R.id.textViewSelfie:
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        if (camera != null) {
                            camera.takePicture(null, null, pictureCallback);
                        } else {
                            fnOpenCamera();
                        }
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 900);
                    }
                    break;
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void eventBusCaptureImage(EventBusCaptureImage eventBusCaptureImage) {
            if (camera != null) {
                camera.takePicture(null, null, pictureCallback);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void openCameraEvent(EventBusCameraPermission eventBusCameraPermission) {
            if (eventBusCameraPermission.isAllowed()) {
                fnOpenCamera();
            }
        }

        //    Method to open mobile camera....
        public void fnOpenCamera() {
            camera = Camera.open();
            cameraPreview = new CameraPreview(getActivity(), getActivity(), camera);
            pictureCallback = getPictureCallback();
            cameraView.setVisibility(View.VISIBLE);
            cameraView.removeAllViews();
            cameraView.addView(cameraPreview);
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                releaseCamera();
                chooseCamera();
                cameraFront = false;
            }
            mCropView.setVisibility(View.GONE);
            horizontalScrollView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        private Camera.PictureCallback getPictureCallback() {
            Camera.PictureCallback picture = (data, camera) -> {
                if (data != null) {
                    releaseCamera();
                    if (camera == null && cameraPreview != null) {
                        chooseCamera();
                        cameraFront = false;
                    }
                    LnqApplication.getInstance().editor.putString(EndpointKeys.AVATAR_FROM, Constants.SELFIE).apply();
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
                        mCropView.setVisibility(View.VISIBLE);
                        horizontalScrollView.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                        cameraView.setVisibility(View.GONE);
                        divider.setVisibility(View.VISIBLE);
                        mCropView.setImageBitmap(bitmap);
//                        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
//                        detectFaces(firebaseVisionImage, Constants.CAMERA, profileImagePath);
                    } catch (IOException e) {
//                        ((MainActivity) getActivity()).progressDialog.dismiss();
                    }
                }
            };
            return picture;
        }

        private void detectFaces(FirebaseVisionImage image) {
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
                                                faceTrackingId = LnqApplication.getInstance().sharedPreferences.getInt("face_tracking_id", 0);
                                                if (faceTrackingId != 0) {
                                                    if (faceTrackingId == faces.get(0).getTrackingId()) {
                                                        faceTrackingId = LnqApplication.getInstance().sharedPreferences.getInt("face_tracking_id", 0);
                                                        mDisposable.add(cropImage());
                                                    }else {
                                                        Toast.makeText(getContext(), "Face detected but not match with your primary face", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    faceTrackingId = LnqApplication.getInstance().sharedPreferences.getInt("face_tracking_id", 0);
                                                    mDisposable.add(cropImage());
                                                }
                                            } else if (faces.size() > 1) {
                                                ((ImageCropActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.single_face_image));
                                            } else {
                                                ((ImageCropActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.select_human_image));
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                                            ((MainActivity) getActivity()).progressDialog.dismiss();
                                        }
                                    });
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

        public void onGalleryClick() {
//            TedBottomPicker.Builder builder = TedBottomPicker.with(getActivity())
//                    .setPeekHeight(1000)
//                    .showGalleryTile(true);
//            builder.showCamera = false;
//            builder.show(uri -> Luban.with(getActivity())
//                    .load(uri)
//                    .ignoreBy(100)
//                    .setCompressListener(new OnCompressListener() {
//                        @Override
//                        public void onStart() {
//
//                        }
//
//                        @Override
//                        public void onSuccess(File file) {
//                            File imagePath = file;
//                            Glide.with(getActivity())
//                                    .load(file)
//                                    .into(mCropView);
//                            mCropView.setVisibility(View.VISIBLE);
//                            horizontalScrollView.setVisibility(View.VISIBLE);
//                            relativeLayout.setVisibility(View.VISIBLE);
//                            cameraView.setVisibility(View.GONE);
//                            divider.setVisibility(View.VISIBLE);
//                            releaseCamera();
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//                    }).launch());

            myBottomSheet = GalleryFragmentNew.newInstance("single");
            myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
        }
    };
}