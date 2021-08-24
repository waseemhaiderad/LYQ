package lnq.com.lnq.fragments.registeration.createprofile;


import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentProfileLooksGoodBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusOnBackPressed;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserData;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.Contact;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.PhoneContact;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserSecondaryProfile;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.gson_converter_models.visibilitysettings.SetVisibilityMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusGetEditProfileImage;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.custom.image_compressor.Luban;
import lnq.com.lnq.custom.image_compressor.OnCompressListener;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class FragmentProfileLooksGood extends Fragment {

    public enum Direction {VERTICAL, HORIZONTAL}

    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private FragmentProfileLooksGoodBinding profileLooksGoodBinding;
    private LooksGoodClickHandler clickHandler;

    //    Instance fields....
    private String profileImagePath, imageType, profileImagePathCreateProfile, imageFromProfile;
    //    Font fields....
    private FontUtils fontUtils;
    private AppCompatImageView imageViewBackTopBar;
    private Bitmap bmp;

    //    DataBase
    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentProfile;
    private String profileId, secondaryProfileid;

    //    Api fields....
    private Call<RegisterLoginMainObject> mCallUploadProfile;
    private Call<SetVisibilityMainObject> callSetVisibility;

    public FragmentProfileLooksGood() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileLooksGoodBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_looks_good, container, false);
        return profileLooksGoodBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransferUtility();
        cachePath = getContext().getCacheDir().getAbsolutePath();
        init();
        setCustomFonts();
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext().getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, getContext().getApplicationContext());
    }


    public void setCustomFonts() {
        fontUtils = FontUtils.getFontUtils(getActivity());

        fontUtils.setTextViewRegularFont(profileLooksGoodBinding.textViewLooksGoodDes);
        fontUtils.setTextViewMedium(profileLooksGoodBinding.clearTextViewLooksGood);
    }

    private void init() {
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        if (getArguments() != null) {
            secondaryProfileid = getArguments().getString("secondaryProfileid", "");
            imageType = getArguments().getString(EndpointKeys.TYPE, "");
            if (imageType.equalsIgnoreCase("edit")) {
                profileImagePath = getArguments().getString(EndpointKeys.IMAGE_PATH, "");
                CardView topBarLayout = profileLooksGoodBinding.tobBar.topBarCardView;
                imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
                TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
                textViewHeading.setText(R.string.looking_good);
                ValidUtils.textViewGradientColor(textViewHeading);
                imageViewBackTopBar.setVisibility(View.VISIBLE);
                imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                Glide.with(getActivity())
                        .load(profileImagePath)
                        .apply(new RequestOptions().centerCrop())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileLooksGoodBinding.imageViewProfile);
            } else if (imageType.equalsIgnoreCase("secondary_profile_image")) {
                CardView topBarLayout = profileLooksGoodBinding.tobBar.topBarCardView;
                imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
                TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
                textViewHeading.setText(R.string.looking_good);
                ValidUtils.textViewGradientColor(textViewHeading);
                profileImagePathCreateProfile = getArguments().getString(EndpointKeys.IMAGE_PATH, "");
                imageViewBackTopBar.setVisibility(View.VISIBLE);
                imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                byte[] byteArray = getArguments().getByteArray("bitmap");
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Glide.with(getActivity())
                        .load(bmp)
                        .apply(new RequestOptions().centerCrop())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileLooksGoodBinding.imageViewProfile);

            } else if (imageType.equalsIgnoreCase(Constants.CREATE_PROFILE)) {
                CardView topBarLayout = profileLooksGoodBinding.tobBar.topBarCardView;
                imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
                TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
                textViewHeading.setText(R.string.looking_good);
                ValidUtils.textViewGradientColor(textViewHeading);
                profileImagePathCreateProfile = getArguments().getString(EndpointKeys.IMAGE_PATH, "");
                imageViewBackTopBar.setVisibility(View.VISIBLE);
                imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                byte[] byteArray = getArguments().getByteArray("bitmap");
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Glide.with(getActivity())
                        .load(bmp)
                        .apply(new RequestOptions().centerCrop())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileLooksGoodBinding.imageViewProfile);
            } else if (imageType.equalsIgnoreCase("verify_profile")) {
                CardView topBarLayout = profileLooksGoodBinding.tobBar.topBarCardView;
                imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
                TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
                textViewHeading.setText(R.string.looking_good);
                ValidUtils.textViewGradientColor(textViewHeading);
                profileImagePathCreateProfile = getArguments().getString(EndpointKeys.IMAGE_PATH, "");
                imageViewBackTopBar.setVisibility(View.VISIBLE);
                imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                byte[] byteArray = getArguments().getByteArray("data");
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Glide.with(getActivity())
                        .load(bmp)
                        .apply(new RequestOptions().centerCrop())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileLooksGoodBinding.imageViewProfile);

            } else {
                imageFromProfile = getArguments().getString(EndpointKeys.IMAGE_PATH, "");
                CardView topBarLayout = profileLooksGoodBinding.tobBar.topBarCardView;
                imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
                TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
                textViewHeading.setText(R.string.looking_good);
                ValidUtils.textViewGradientColor(textViewHeading);
                profileImagePathCreateProfile = getArguments().getString(EndpointKeys.IMAGE_PATH, "");
                imageViewBackTopBar.setVisibility(View.VISIBLE);
                imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                download(imageFromProfile, profileLooksGoodBinding.imageViewProfile);
            }
            multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
                @Override
                public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                    for (MultiProfileRoomModel data : multiProfileRoomModels) {
                        if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                            currentProfile = data;
                        }
                    }
                }
            });
        }

//        Setting click handler for data binding....
        clickHandler = new LooksGoodClickHandler(getActivity());
        profileLooksGoodBinding.setClickHandler(clickHandler);

    }

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            imageFromProfile = cachePath + "/" + objectKey;
            Glide.with(getContext()).
                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
                     apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().circleCrop())
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
                        Glide.with(getContext()).
                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                                apply(new RequestOptions().centerCrop())
                                .apply(new RequestOptions().circleCrop())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callSetVisibility != null && callSetVisibility.isExecuted()) {
            callSetVisibility.cancel();
        }
        if (mCallUploadProfile != null && mCallUploadProfile.isExecuted()) {
            mCallUploadProfile.cancel();
        }
    }

    //     Method to upload profile image....
    private void reqUploadProfileImage(File file) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        profileLooksGoodBinding.clearTextViewLooksGood.setVisibility(View.INVISIBLE);
        profileLooksGoodBinding.clearTextViewLogin2.setVisibility(View.VISIBLE);
        profileLooksGoodBinding.clearTextViewLogin2.startAnimation();
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        final RequestBody avatar_from = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString("avatar_from", ""));
        final RequestBody image_type = RequestBody.create(MediaType.parse("text/plain"), "user_avatar");
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("user_image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        mCallUploadProfile = Api.WEB_SERVICE.uploadProfileImage(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), filePart, id, avatar_from, image_type);
//        mCallUploadProfile = Api.WEB_SERVICE.uploadProfileImage(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), filePart, id, avatar_from, image_type);
        mCallUploadProfile.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                    profileLooksGoodBinding.clearTextViewLogin2.stopAnimation();
                    profileLooksGoodBinding.clearTextViewLooksGood.setVisibility(View.VISIBLE);
                    profileLooksGoodBinding.clearTextViewLogin2.setVisibility(View.GONE);
                    profileLooksGoodBinding.clearTextViewLogin2.revertAnimation();
                    switch (response.body().getStatus()) {
                        case 1:
                            if (getArguments().getString(EndpointKeys.TYPE, "").equals("edit")) {
                                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_AVATAR, response.body().getUploadProfileImage().getUser_avatar());
                                LnqApplication.getInstance().editor.apply();
                                EventBus.getDefault().post(new EventBusRefreshUserData());
                                ((MainActivity) getActivity()).popBackHomeFragment(Constants.PROFILE);
                            } else {
                                List<CreateUserSecondaryProfile> logInProfilesDataList = response.body().getUser_profiles();
                                int i = 0;
//                                if (logInProfilesDataList.get(i).getProfile_status().equalsIgnoreCase("active")) {
                                if (logInProfilesDataList != null && logInProfilesDataList.size() != 0) {
                                    LnqApplication.getInstance().editor.putString("activeProfile", logInProfilesDataList.get(i).getId());
                                    profileId = logInProfilesDataList.get(i).getId();
                                    multiProfileRepositry.insertProfilesData(
                                            logInProfilesDataList.get(i).getId(),
                                            logInProfilesDataList.get(i).getUser_id(),
                                            logInProfilesDataList.get(i).getUser_fname(),
                                            logInProfilesDataList.get(i).getUser_lname(),
                                            logInProfilesDataList.get(i).getUser_nickname(),
                                            logInProfilesDataList.get(i).getUser_avatar(),
                                            logInProfilesDataList.get(i).getAvatar_from(),
                                            logInProfilesDataList.get(i).getUser_cnic(),
                                            logInProfilesDataList.get(i).getUser_address(),
                                            logInProfilesDataList.get(i).getUser_phone(),
                                            logInProfilesDataList.get(i).getSecondary_phones(),
                                            logInProfilesDataList.get(i).getSecondary_emails(),
                                            logInProfilesDataList.get(i).getUser_current_position(),
                                            logInProfilesDataList.get(i).getUser_company(),
                                            logInProfilesDataList.get(i).getUser_birthday(),
                                            logInProfilesDataList.get(i).getUser_bio(),
                                            logInProfilesDataList.get(i).getUser_status_msg(),
                                            logInProfilesDataList.get(i).getUser_tags(),
                                            logInProfilesDataList.get(i).getUser_interests(),
                                            logInProfilesDataList.get(i).getUser_gender(),
                                            logInProfilesDataList.get(i).getHome_default_view(),
                                            logInProfilesDataList.get(i).getContact_default_view(),
                                            logInProfilesDataList.get(i).getSocial_links(),
                                            logInProfilesDataList.get(i).getProfile_status(),
                                            logInProfilesDataList.get(i).getCreated_at(),
                                            logInProfilesDataList.get(i).getUpdated_at(),
                                            logInProfilesDataList.get(i).getVisibleTo(),
                                            logInProfilesDataList.get(i).getVisibleAt()
                                    );
                                }
                                reqSetVisibility(Constants.NEAR_BY, Constants.CITY, profileId);
                                LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_STATUS, response.body().getUploadProfileImage().getVerification_status());
                                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_AVATAR, response.body().getUploadProfileImage().getUser_avatar());
                                LnqApplication.getInstance().editor.apply();

                            }
                            if (response.body().getUploadProfileImage().getUser_avatar() != null && !response.body().getUploadProfileImage().getUser_avatar().isEmpty()) {
                                if (currentProfile != null) {
                                    currentProfile.setUser_avatar(response.body().getUploadProfileImage().getUser_avatar());
                                    multiProfileRepositry.updateTask(currentProfile);
                                }
                            }
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                profileLooksGoodBinding.clearTextViewLooksGood.setVisibility(View.VISIBLE);
                profileLooksGoodBinding.clearTextViewLogin2.setVisibility(View.GONE);
                profileLooksGoodBinding.clearTextViewLogin2.revertAnimation();
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
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

    private void reqSetVisibility(final String visibleTo, final String visibleAt, String profileId) {
//        callSetVisibility = Api.WEB_SERVICE.setVisibility(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), visibleTo, visibleAt);
        callSetVisibility = Api.WEB_SERVICE.setVisibility(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), visibleTo, visibleAt, profileId);
        callSetVisibility.enqueue(new Callback<SetVisibilityMainObject>() {
            @Override
            public void onResponse(Call<SetVisibilityMainObject> call, Response<SetVisibilityMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            currentProfile.setVisible_to(visibleTo);
//                            currentProfile.setVisible_at(visibleAt);
//                            multiProfileRepositry.updateTask(currentProfile);
                            ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.WELCOME_LNQ, false, null);
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<SetVisibilityMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
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

    public class LooksGoodClickHandler {
        private Context context;

        public LooksGoodClickHandler(Context context) {
            this.context = context;
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void looksGoodClick(View view) {
            if (getArguments().getString(EndpointKeys.TYPE, "").equals(Constants.CREATE_PROFILE) || getArguments().getString(EndpointKeys.TYPE, "").equals(Constants.EDIT_PROFILE) || getArguments().getString(EndpointKeys.TYPE, "").equals("edit") || getArguments().getString(EndpointKeys.TYPE, "").equals("secondary_profile_image") || getArguments().getString(EndpointKeys.TYPE, "").equals("profile_image") || getArguments().getString(EndpointKeys.TYPE, "").equals("verify_profile")) {
                if (getArguments().getString(EndpointKeys.TYPE, "").equals("edit")) {
                    ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                    EventBus.getDefault().post(new EventBusGetEditProfileImage(profileImagePath, "", null));
                    ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_PICTURE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_LOOKS_GOOD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else if (getArguments().getString(EndpointKeys.TYPE, "").equals("secondary_profile_image")) {
                    if (secondaryProfileid != null && !secondaryProfileid.isEmpty()) {
                        EventBus.getDefault().post(new EventBusGetEditProfileImage("", secondaryProfileid, bmp));
                        ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_PICTURE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_LOOKS_GOOD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.CREATE_MULTI_PROFILE_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }else if (getArguments().getString(EndpointKeys.TYPE, "").equals("profile_image")) {
                    if (secondaryProfileid != null && !secondaryProfileid.isEmpty()) {
                        LnqApplication.getInstance().editor.putString("avatar_from", "gallery").apply();
                        EventBus.getDefault().post(new EventBusGetEditProfileImage(imageFromProfile, secondaryProfileid, null));
                        ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_PICTURE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_LOOKS_GOOD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.CREATE_MULTI_PROFILE_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                } else if (getArguments().getString(EndpointKeys.TYPE, "").equals("verify_profile")) {
                            ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_PICTURE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_LOOKS_GOOD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_VERIFY_TAKE_PHOTO, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            ((MainActivity) getActivity()).fragmentManager.popBackStack(Constants.PROFILE_VERIFY_OPTIONS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    Luban.with(getActivity())
                            .load(profileImagePathCreateProfile)
                            .ignoreBy(100)
                            .setCompressListener(new OnCompressListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onSuccess(File file) {
                                    reqUploadProfileImage(file);
                                }

                                @Override
                                public void onError(Throwable e) {
                                }
                            }).launch();
                }
            } else {
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.ID_VERIFY, false, null);
            }
        }

    }
}