package lnq.com.lnq.fragments.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ProfileImagesAdapter;
import lnq.com.lnq.adapters.ProfleSocialLinksAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.image_compressor.Luban;
import lnq.com.lnq.custom.image_compressor.OnCompressListener;
import lnq.com.lnq.databinding.FragmentProfileBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.fragments.profile.editprofile.EventBusContactInfo;
import lnq.com.lnq.fragments.profile.editprofile.EventBusContactInfoEmail;
import lnq.com.lnq.model.event_bus_models.EventBusGetEditProfileImage;
import lnq.com.lnq.model.event_bus_models.EventBusPrimaryEmailProfile;
import lnq.com.lnq.model.event_bus_models.EventBusPrimaryPhoneProfile;
import lnq.com.lnq.model.event_bus_models.EventBusProfilesClick;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserData;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserSecondaryProfileData;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryEmails;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryPhones;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSocial;
import lnq.com.lnq.model.event_bus_models.EventBusSecondaryEmailAdded;
import lnq.com.lnq.model.event_bus_models.EventBusSecondaryPhoneAdded;
import lnq.com.lnq.model.event_bus_models.EventBusSocialMedia;
import lnq.com.lnq.model.event_bus_models.EventBusStatusUpdated;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.profile_information.ChangeUserProfileStatusMainObject;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.model.userprofile.SocialMediaLinksModel;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class FragmentProfile extends Fragment implements View.OnClickListener {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private FragmentProfileBinding profileBinding;
    private ProfileClickHandler clickHandler;
    private PhoneNumberUtil phoneNumberUtil;

    //    Instance fields....
    private String profileImagePath = "";
    List<SocialMediaLinksModel> socialLinks = new ArrayList<>();
    private ArrayList<String> emailList = new ArrayList<String>();
    private ArrayList<String> phoneList = new ArrayList<String>();
    private List<String> linksList = new ArrayList<>();
    private String socialLink;
    private ProfleSocialLinksAdapter adapter;
    private List<MultiProfileRoomModel> proflieList = new ArrayList<>();
    private MultiProfileRepositry repositry;
    private MultiProfileRoomModel currentProfileData;
    private ProfileImagesAdapter profileImagesAdapter;
    private boolean isFirstTimeDataLoadedFromRoom;
    View dialogView;
    Dialog dialog;
    String secondaryProfileId;
    private boolean isCalled = false;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;
    CardView topBarLayout;
    public String mFScreenName = "";

    //    Retrofit fields....
    private Call<CreateUserProfileMainObject> callUploadProfile;
    private Call<ChangeUserProfileStatusMainObject> callChangeProfileStatus;

    public FragmentProfile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return profileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransferUtility();
        cachePath = getContext().getCacheDir().getAbsolutePath();
        init();
        topBarLayout = profileBinding.topBarProfile.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewSettingTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.your_profile);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);
        imageViewContactGridTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), profileBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusUserSession("setting_view"));
                mFScreenName = Constants.SETTING;
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SETTING, true, null);
            }
        });
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "verify");
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_VERIFY_OPTIONS, true, bundle);
            }
        });
        OverScrollDecoratorHelper.setUpOverScroll(profileBinding.scrollView);
    }

    private void init() {
        EventBus.getDefault().register(this);
        repositry = new MultiProfileRepositry(getContext());

//        ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.INVISIBLE);
//        ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.VISIBLE);

        profileBinding.recyclerViewMultiProfiles.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        profileBinding.recyclerViewMultiProfiles.setItemAnimator(new DefaultItemAnimator());

        repositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                if (!isFirstTimeDataLoadedFromRoom) {
                    proflieList.clear();
                    proflieList.addAll(multiProfileRoomModels);
                    for (MultiProfileRoomModel data : proflieList) {
                        if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                            currentProfileData = data;
                            setUserSecondaryProfileData();
                            setUserSecondaryStatusMessage();
                            break;
                        }
                    }
                    if (proflieList.size() > 0) {
                        if (getContext() != null) {
                            sortListByDate(proflieList);
                            profileImagesAdapter = new ProfileImagesAdapter(getContext(), proflieList);
                            profileBinding.recyclerViewMultiProfiles.setVisibility(View.VISIBLE);
                            profileBinding.recyclerViewMultiProfiles.setAdapter(profileImagesAdapter);
                        }
                    }
                    isFirstTimeDataLoadedFromRoom = true;
                }
            }
        });

        phoneNumberUtil = PhoneNumberUtil.getInstance();

        String phoneNumber = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, "");
        try {
            if (phoneNumberUtil.isValidNumber(phoneNumberUtil.parseAndKeepRawInput(phoneNumber, Constants.DEFAULT_REGION))) {
                phoneNumber = phoneNumberUtil.format(phoneNumberUtil.parse(phoneNumber, Constants.DEFAULT_REGION), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            }
        } catch (Exception e) {

        }

//        Setting click handler for data binding....
        clickHandler = new ProfileClickHandler();
        profileBinding.setClickHandler(clickHandler);
//        profileBinding.fabShareQrCode.setOnClickListener(this);
//        profileBinding.imageViewProfileVerifiedTick.setOnClickListener(this);
        profileBinding.imageViewProfileImage.setOnClickListener(this);

        profileBinding.textViewPrimaryPhone.setEnabled(false);
        profileBinding.textViewPrimaryEamil.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""));
        profileBinding.textViewPrimaryPhone.setText(phoneNumber);
    }

    private void sortListByDate(List<MultiProfileRoomModel> modelArrayList) {
        Collections.sort(modelArrayList, new Comparator<MultiProfileRoomModel>() {
            public int compare(MultiProfileRoomModel obj1, MultiProfileRoomModel obj2) {
                return Integer.valueOf(obj1.getId()).compareTo(Integer.parseInt(obj2.getId()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (callChangeProfileStatus != null && callChangeProfileStatus.isExecuted()) {
            callChangeProfileStatus.cancel();
        }
        if (callUploadProfile != null && callUploadProfile.isExecuted()) {
            callUploadProfile.cancel();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGetProfileImagePath(EventBusGetEditProfileImage mObj) {
        if (mObj.getProfileImagePath() != null) {
            profileImagePath = mObj.getProfileImagePath();
            secondaryProfileId = mObj.getSecondaryProfileId();
            Bitmap bitmap = mObj.getBitmapImagePath();
            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                downloadUploaded(profileImagePath, profileBinding.imageViewProfileImage);
            }
//            if (profileImagePath != null && !profileImagePath.isEmpty()) {
            Luban.with(getActivity())
                    .load(profileImagePath)
                    .ignoreBy(100)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            if (bitmap != null) {
                                File f = new File(getContext().getCacheDir(), ".png");
                                try {
                                    f.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                                byte[] bitmapdata = bos.toByteArray();
                                try {
                                    FileOutputStream fos = new FileOutputStream(f);
                                    fos.write(bitmapdata);
                                    fos.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                reqUploadProfileImage(f);
                            } else {
                                reqUploadProfileImage(file);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    }).launch();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUpdateStatus(EventBusStatusUpdated mObj) {
        profileBinding.textViewStatusMessage.setText(mObj.getStatusMessage());
        currentProfileData.setUser_status_msg(mObj.getStatusMessage());
        repositry.updateTask(currentProfileData);
        if (mObj.getStatusMessage().equals("")) {
            profileBinding.imageViewQuoteLeft.setVisibility(View.GONE);
            profileBinding.imageViewQuoteRight.setVisibility(View.GONE);
        } else {
            profileBinding.imageViewQuoteLeft.setVisibility(View.VISIBLE);
            profileBinding.imageViewQuoteRight.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRefreshData(EventBusRefreshUserData mObj) {
        ValidUtils.hideKeyboardFromFragment(getContext(), profileBinding.getRoot());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                repositry.getProfileByID(currentProfileData.getId())
                        .observe(getActivity(), new Observer<MultiProfileRoomModel>() {
                            @Override
                            public void onChanged(MultiProfileRoomModel multiProfileRoomModel) {
                                currentProfileData = multiProfileRoomModel;
                                setUserSecondaryProfileData();
                            }
                        });
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                repositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
                    @Override
                    public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                        proflieList.clear();
                        proflieList.addAll(multiProfileRoomModels);
                        if (proflieList.size() > 0) {
                            if (getContext() != null) {
                                sortListByDate(proflieList);
                                profileImagesAdapter = new ProfileImagesAdapter(getContext(), proflieList);
                                profileBinding.recyclerViewMultiProfiles.setVisibility(View.VISIBLE);
                                profileBinding.recyclerViewMultiProfiles.setAdapter(profileImagesAdapter);
                            }
                        }
                    }
                });
            }
        }, 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusProfileClick(EventBusProfilesClick mObj) {
        ValidUtils.hideKeyboardFromFragment(getContext(), profileBinding.getRoot());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        dialogView = inflater.inflate(R.layout.cus_dialog_activeprofile, null);
        Button textCancel = dialogView.findViewById(R.id.textViewCancelProfile);
        Button textViewYes = dialogView.findViewById(R.id.textViewYes);
        ValidUtils.buttonGradientColor(textCancel);
        AppCompatTextView textViewUserTitlePopUp = dialogView.findViewById(R.id.textViewUserTitlePopUp);
        AppCompatTextView textViewUserCompanyPopUp = dialogView.findViewById(R.id.textViewUserCompanyPopUp);
        AppCompatImageView imageViewPopupImage = dialogView.findViewById(R.id.imageViewMutilpleProfileImagePopUp);

        if (mObj.getUserImage() != null && !mObj.getUserImage().isEmpty()) {
            download(mObj.getUserImage(), imageViewPopupImage);
        }

        textViewUserTitlePopUp.setText(mObj.getUserJobTitle());
        textViewUserCompanyPopUp.setText(mObj.getUserCompany());


        textViewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqChangeProfileStatus(mObj.getProfileId(), mObj.getPosition());
                dialog.dismiss();
            }
        });

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        try {
            dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);
        } catch (Exception e) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRefreshUserSecondaryProfileData(EventBusRefreshUserSecondaryProfileData mObj) {
        proflieList.add(mObj.getMultiProfileRoomModel());
        profileImagesAdapter.notifyItemInserted(proflieList.size() - 1);
    }

    private void setUserSecondaryProfileData() {
        socialLinks.clear();
        linksList.clear();

        if (currentProfileData.getUser_avatar() != null && !currentProfileData.getUser_avatar().isEmpty()) {
            download(currentProfileData.getUser_avatar(), profileBinding.imageViewProfileImage);
        } else {
            profileBinding.imageViewProfileImage.setImageResource(R.drawable.avatar);
        }
        profileBinding.textViewFirstName.setText(currentProfileData.getUser_fname());
        profileBinding.textViewLastName.setText(currentProfileData.getUser_lname());
        profileBinding.textViewCompany.setText(currentProfileData.getUser_company());
        profileBinding.textViewBirthday.setText(currentProfileData.getUser_birthday());
        profileBinding.textViewTitle.setText(currentProfileData.getUser_current_position());
        profileBinding.textViewHomeBase.setText(currentProfileData.getUser_address());
        profileBinding.textViewWork.setVisibility(View.VISIBLE);
        String bio = currentProfileData.getUser_bio();
        if (!bio.isEmpty()) {
            profileBinding.textViewBioo.setVisibility(View.GONE);
            profileBinding.textViewBio.setVisibility(View.VISIBLE);
            profileBinding.textViewBio.setText(bio);
        } else {
            profileBinding.textViewBioo.setVisibility(View.VISIBLE);
            profileBinding.textViewBio.setVisibility(View.GONE);
        }
        profileBinding.textViewGender.setText(currentProfileData.getUser_gender());
        String tags = currentProfileData.getUser_interests();
        if (!tags.isEmpty()) {
            profileBinding.Intrests.setVisibility(View.GONE);
            profileBinding.tagContainer.removeAllTags();
            List<String> tagsList = new ArrayList<>(Arrays.asList(tags.split(",")));
            SortingUtils.sortTagsList(tagsList);
            for (int i = 0; i < tagsList.size(); i++) {
                profileBinding.tagContainer.addTag(tagsList.get(i));
            }
        } else {
            profileBinding.Intrests.setVisibility(View.VISIBLE);
            profileBinding.tagContainer.removeAllTags();
        }

        socialLink = currentProfileData.getSocial_links();
        if (!socialLink.isEmpty()) {
            linksList.addAll(Arrays.asList(socialLink.split("\\s*,\\s*")));
            for (String link : linksList) {
                socialLinks.add(new SocialMediaLinksModel(link));
            }
        }
        profileBinding.recyclerViewSocialMedia.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfleSocialLinksAdapter(getContext(), socialLinks, "profile");
        profileBinding.recyclerViewSocialMedia.setAdapter(adapter);

        String secondaryEmail = currentProfileData.getSecondary_emails();
        if (!secondaryEmail.isEmpty()) {
            emailList = new ArrayList(Arrays.asList(secondaryEmail.split(",")));
            setEmailInEmailList();
        }

        String secondaryPhone = currentProfileData.getSecondary_phones();
        if (!secondaryPhone.isEmpty()) {
            phoneList = new ArrayList(Arrays.asList(secondaryPhone.split(",")));
            setPhoneInEmailList();
        }
    }

    private void setUserSecondaryStatusMessage() {
        if (currentProfileData.getUser_status_msg().equals("")) {
            profileBinding.textViewStatusMessage.setText("Looking for IOS Developer");
            profileBinding.imageViewQuoteLeft.setVisibility(View.VISIBLE);
            profileBinding.imageViewQuoteRight.setVisibility(View.VISIBLE);
        } else {
            profileBinding.textViewStatusMessage.setText(currentProfileData.getUser_status_msg());
            profileBinding.imageViewQuoteLeft.setVisibility(View.VISIBLE);
            profileBinding.imageViewQuoteRight.setVisibility(View.VISIBLE);
        }
    }

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            Glide.with(getActivity()).
                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
//                    apply(new RequestOptions().centerCrop()).
        apply(new RequestOptions().circleCrop()).
                    apply(new RequestOptions().placeholder(R.drawable.avatar))
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
//                                    apply(new RequestOptions().centerCrop()).
        apply(new RequestOptions().circleCrop()).
                                    apply(new RequestOptions().placeholder(R.drawable.avatar))
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

//    void downloadUploaded(String objectKey, ImageView imageView) {
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
//                    Glide.with(getActivity()).
//                            load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
//                            apply(new RequestOptions().circleCrop()).
//                            into(imageView);
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

    void downloadUploaded(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            Glide.with(getActivity()).
                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
                    apply(new RequestOptions().circleCrop())
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
                        Glide.with(getActivity()).
                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                                apply(new RequestOptions().circleCrop())
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

    //     Method to upload profile image....
    private void reqUploadProfileImage(File file) {
        ((MainActivity) getActivity()).progressDialog.show();
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        final RequestBody profileId;
        if (secondaryProfileId != null && !secondaryProfileId.isEmpty()) {
            profileId = RequestBody.create(MediaType.parse("text/plain"), secondaryProfileId);
        } else {
            profileId = RequestBody.create(MediaType.parse("text/plain"), currentProfileData.getId());
        }
        final RequestBody avatar_from = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString("avatar_from", ""));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("user_image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        callUploadProfile = Api.WEB_SERVICE.updateProfileImage(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), filePart, id, profileId, avatar_from);
        callUploadProfile.enqueue(new Callback<CreateUserProfileMainObject>() {
            @Override
            public void onResponse(Call<CreateUserProfileMainObject> call, Response<CreateUserProfileMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (secondaryProfileId != null && !secondaryProfileId.isEmpty()) {
                                repositry.getProfileByID(secondaryProfileId).observe(getActivity(), new Observer<MultiProfileRoomModel>() {
                                    @Override
                                    public void onChanged(MultiProfileRoomModel profileRoomModel) {
                                        if (!isCalled) {
                                            profileRoomModel.setUser_avatar(response.body().getUpdateProfileImage().getUser_avatar());
                                            repositry.updateTask(profileRoomModel);
                                            isCalled = true;
                                        }
                                    }
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        repositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
                                            @Override
                                            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                                                proflieList.clear();
                                                proflieList.addAll(multiProfileRoomModels);
                                                if (proflieList.size() > 0) {
                                                    if (getContext() != null) {
                                                        sortListByDate(proflieList);
                                                        profileImagesAdapter = new ProfileImagesAdapter(getContext(), proflieList);
                                                        profileBinding.recyclerViewMultiProfiles.setVisibility(View.VISIBLE);
                                                        profileBinding.recyclerViewMultiProfiles.setAdapter(profileImagesAdapter);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }, 1000);
                            } else {
                                currentProfileData.setUser_avatar(response.body().getUpdateProfileImage().getUser_avatar());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        repositry.updateTask(currentProfileData);
                                        repositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
                                            @Override
                                            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                                                proflieList.clear();
                                                proflieList.addAll(multiProfileRoomModels);
                                                if (proflieList.size() > 0) {
                                                    if (getContext() != null) {
                                                        sortListByDate(proflieList);
                                                        profileImagesAdapter = new ProfileImagesAdapter(getContext(), proflieList);
                                                        profileBinding.recyclerViewMultiProfiles.setVisibility(View.VISIBLE);
                                                        profileBinding.recyclerViewMultiProfiles.setAdapter(profileImagesAdapter);
                                                    }
                                                }
                                            }
                                        });
                                        download(currentProfileData.getUser_avatar(), profileBinding.imageViewProfileImage);
                                    }
                                }, 1500);
                            }
                            EventBus.getDefault().post(new EventBusUserSession("profile_img_updated"));
                            ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.profile_image_updated));
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateUserProfileMainObject> call, Throwable error) {
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

    //     Method to change profile status and profile data....
    private void reqChangeProfileStatus(String profileId, int position) {
        callChangeProfileStatus = Api.WEB_SERVICE.switchActiveUserProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), profileId);
        callChangeProfileStatus.enqueue(new Callback<ChangeUserProfileStatusMainObject>() {
            @Override
            public void onResponse(Call<ChangeUserProfileStatusMainObject> call, Response<ChangeUserProfileStatusMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (!currentProfileData.getId().equals(profileId)) {
                                currentProfileData.setProfile_status("non-active");
                                repositry.updateTask(currentProfileData);
                                currentProfileData = proflieList.get(position);
                                currentProfileData.setProfile_status("active");
                                repositry.updateTask(currentProfileData);
                                LnqApplication.getInstance().editor.putString("activeProfile", profileId).apply();
                                LnqApplication.getInstance().editor.putString(EndpointKeys.PROFILE_CREATED_DATE, currentProfileData.getCreated_at()).apply();
                                profileImagesAdapter.notifyDataSetChanged();
                                setUserSecondaryProfileData();
                                setUserSecondaryStatusMessage();
                                int total = 0;
                                ((MainActivity) getActivity()).mBind.textViewActivityCount.setVisibility(View.GONE);
                                ((MainActivity) getActivity()).mBind.textViewCount.setVisibility(View.GONE);
                                ((MainActivity) getActivity()).mBind.textViewCount.setText(String.valueOf(total));
                                ((MainActivity) getActivity()).mBind.textViewActivityCount.setText(String.valueOf(total));
                                LnqApplication.getInstance().editor.putString(Constants.CHAT_COUNT, String.valueOf(total)).apply();
                                LnqApplication.getInstance().editor.putString(Constants.ACTIVITY_COUNT, String.valueOf(total)).apply();
                                ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            }
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ChangeUserProfileStatusMainObject> call, Throwable error) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.fabShareQrCode:
//                ValidUtils.hideKeyboardFromFragment(getActivity(), profileBinding.getRoot());
//                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
//                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
//                return;
            case R.id.imageViewProfileVerifiedTick:
                ((MainActivity) getActivity()).fnLoadFragAdd("PROFILE VERIFIED", true, null);
                break;
            case R.id.imageViewProfileImage:
                Intent myIntent = new Intent(getContext(), FullProfilePictureActivity.class);
                myIntent.putExtra("profileImage", currentProfileData.getUser_avatar());
                startActivity(myIntent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void socialMediaLinks(EventBusSocialMedia eventBusSocialMedia) {
        ValidUtils.hideKeyboardFromFragment(getContext(), profileBinding.getRoot());
        SocialMediaLinksModel socialMediaLinksModel = new SocialMediaLinksModel(eventBusSocialMedia.getSocialLink());
        socialLinks.add(socialMediaLinksModel);
        adapter.notifyDataSetChanged();
        profileUpdateSuccess("Social Links updated successfully..");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSecondaryEmailAdded(EventBusSecondaryEmailAdded eventBusSecondaryEmailAdded) {
        String secondaryEmail = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SECONDARY_EMAILS, "");
        if (!secondaryEmail.isEmpty()) {
            emailList = new ArrayList(Arrays.asList(secondaryEmail.split(",")));
            setEmailInEmailList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSecondaryPhoneAdded(EventBusSecondaryPhoneAdded eventBusSecondaryPhoneAdded) {
        String secondaryPhone = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SECONDARY_PHONES, "");
        if (!secondaryPhone.isEmpty()) {
            phoneList = new ArrayList(Arrays.asList(secondaryPhone.split(",")));
            setPhoneInEmailList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusPrimaryPhoneProfile(EventBusPrimaryPhoneProfile eventBusPrimaryPhoneProfile) {
        phoneNumberUtil = PhoneNumberUtil.getInstance();

        String phoneNumber = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, "");
        try {
            if (phoneNumberUtil.isValidNumber(phoneNumberUtil.parseAndKeepRawInput(phoneNumber, Constants.DEFAULT_REGION))) {
                phoneNumber = phoneNumberUtil.format(phoneNumberUtil.parse(phoneNumber, Constants.DEFAULT_REGION), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            }
        } catch (Exception e) {

        }
        profileBinding.textViewPrimaryPhone.setEnabled(false);
        profileBinding.textViewPrimaryPhone.setText(phoneNumber);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusPrimaryEmailAdded(EventBusPrimaryEmailProfile eventBusPrimaryEmailProfile) {
        profileBinding.textViewPrimaryEamil.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void contactInfoUpdated(EventBusContactInfo eventBusContactInfo) {
        TextView textView = new TextView(getActivity());
        textView.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
        textView.setTextSize(16.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 5;
        textView.setText(eventBusContactInfo.getPhone());
        textView.setLayoutParams(layoutParams);
        profileBinding.listViewPhone.addView(textView);
        profileUpdateSuccess("Contact Info updated successfully");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void contactInfoUpdatedEmail(EventBusContactInfoEmail eventBusContactInfoEmail) {
//        listEmail.add(eventBusContactInfoEmailil.getEmail());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRemoveSocial(EventBusRemoveSocial eventBusRemoveSocial) {
        socialLinks.remove(eventBusRemoveSocial.getPosition());
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSecondaryPhoneRemove(EventBusRemoveSecondaryPhones eventBusRemoveSecondaryPhones) {
        phoneList.remove(eventBusRemoveSecondaryPhones.getPosition());
        setPhoneInEmailList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSecondaryEmailRemove(EventBusRemoveSecondaryEmails eventBusRemoveSecondaryEmails) {
        emailList.remove(eventBusRemoveSecondaryEmails.getPosition());
        setEmailInEmailList();
    }

    private void setEmailInEmailList() {
        profileBinding.listViewEmail.removeAllViews();
        for (String email : emailList) {
            TextView textView = new TextView(getActivity());
            textView.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
            textView.setTextSize(16.0f);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = 5;
            textView.setText(email);
            textView.setLayoutParams(layoutParams);
            profileBinding.listViewEmail.addView(textView);
        }
    }

    private void setPhoneInEmailList() {
        profileBinding.listViewPhone.removeAllViews();
        for (String phone : phoneList) {
            TextView textView = new TextView(getActivity());
            textView.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
            textView.setTextSize(16.0f);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = 5;
            textView.setText(phone);
            textView.setLayoutParams(layoutParams);
            profileBinding.listViewPhone.addView(textView);
        }
    }

    public void profileUpdateSuccess(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = null;
        alertDialogBuilder.setTitle("Success");
        alertDialog.setTitle(message);
        AlertDialog finalAlertDialog = alertDialog;
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finalAlertDialog.dismiss();
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finalAlertDialog.dismiss();
            }
        }, 1000);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void editProfileImage() {
        Intent myIntent = new Intent(getContext(),
                ImageCropActivity.class);
        myIntent.putExtra("profileImage", currentProfileData.getUser_avatar());
        startActivityForResult(myIntent, 404);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 404 && resultCode == RESULT_OK) {
            String uri = data.getStringExtra("image");
            Bundle bundle = new Bundle();
            bundle.putString(EndpointKeys.TYPE, "edit");
            bundle.putString(EndpointKeys.IMAGE_PATH, uri);
            LnqApplication.getInstance().editor.putString(EndpointKeys.AVATAR_FROM, Constants.SELFIE).apply();
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_LOOKS_GOOD, true, bundle);
        }
    }

    public class ProfileClickHandler {

        public void onEditProfileImageClick(View view) {
            editProfileImage();
        }

        public void onEditWorkClick(View view) {
//            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EDIT_WORK_HISTORY, true, null);
        }

        public void onEditPhoneClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EDIT_CONTACT_INFO, true, null);
        }

        public void onEditParimaryPhoneClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd("EDIT PHONE", true, null);
        }

        public void onEditPhoneClickEmail(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd("EDIT EMAIL", true, null);
        }

        public void onEditEmail(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EDIT_CONTACT_INFO_EMAIL, true, null);
        }

        public void onEditSocialMediaClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EDIT_SOCIAL_LINKS, true, null);
        }

        public void onEditBasicInfoClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EDIT_PROFILE_FRAGMENT, true, null);
        }

        public void onCreateBasicInfoClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.CREATE_MULTI_PROFILE_FRAGMENT, true, null);
        }

        public void onEditBioClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EDIT_BIO, true, null);
        }

        public void onEditTagsClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EDIT_TAGS, true, null);
        }

        public void onEditStatusClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOOKING_FOR, true, null);
            EventBus.getDefault().post(new EventBusUserSession("status_view"));
        }
    }
}