package lnq.com.lnq.fragments.fullprofileview;


import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.rengwuxian.materialedittext.Colors;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.AddGroupMemberAdapter;
import lnq.com.lnq.adapters.AddToGroupAdapter;
import lnq.com.lnq.adapters.CreateContactGroupAdapter;
import lnq.com.lnq.adapters.FullProfileViewPagerAdapter;
import lnq.com.lnq.adapters.ShowGroupNamesAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.ClearTextView;
import lnq.com.lnq.databinding.FragmentLnqUserProfileViewBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.fragments.connections.FragmentConnections;
import lnq.com.lnq.fragments.connections.export_contacts.FragmentExportContacts;
import lnq.com.lnq.fragments.connections.salesforce.OAuthTokens;
import lnq.com.lnq.fragments.connections.salesforce.OAuthUtil;
import lnq.com.lnq.fragments.profile.editprofile.EventBusContactInfo;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.PhoneContactsModel;
import lnq.com.lnq.model.event_bus_models.EventBusAddToGroup;
import lnq.com.lnq.model.event_bus_models.EventBusBlockedUnBlocked;
import lnq.com.lnq.model.event_bus_models.EventBusContactPermissionProfile;
import lnq.com.lnq.model.event_bus_models.EventBusContactsListUserData;
import lnq.com.lnq.model.event_bus_models.EventBusExportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusMuteChat;
import lnq.com.lnq.model.event_bus_models.EventBusSalesforceLogin;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateTaskHistory;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.CreateUserGroup;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.ExportCSVModel;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserContactGroupMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserGetGroupMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.SelectedExportContact;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.Contact;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.PhoneContact;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusNotesUserData;
import lnq.com.lnq.model.event_bus_models.EventBusProfileSubPageClicked;
import lnq.com.lnq.model.event_bus_models.EventBusAboutUserData;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshTaskNotes;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserZoomLevel;
import lnq.com.lnq.model.event_bus_models.EventBusUsersHistory;
import lnq.com.lnq.model.salesforce.SalesforceAttributes;
import lnq.com.lnq.model.salesforce.SalesforceContactModel;
import lnq.com.lnq.model.salesforce.SalesforceModel;
import lnq.com.lnq.model.userprofile.GetUserProfileData;
import lnq.com.lnq.model.userprofile.GetUserProfileMainObject;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class FragmentLnqUserProfileView extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private FragmentLnqUserProfileViewBinding profileViewBinding;

    //    Retrofit fields....
    private Call<GetUserProfileMainObject> callGetUserProfile;
    private Call<UpdateLocationMainObject> callFavUnFavLnq;
    private Call<RegisterLoginMainObject> callBlackWhiteStatus;
    private Call<RegisterLoginMainObject> callLogActivity;
    private Call<RegisterLoginMainObject> callShowHideLocation;
    private Call<UpdateLocationMainObject> callRequest;
    private Call<UpdateLocationMainObject> callAcceptRequest;
    private Call<UpdateLocationMainObject> callDeclineRequest;
    private Call<ExportCSVModel> exportCSVModelCall;
    private Call<UserGetGroupMainObject> callUserGetGroup;
    private Call<UserContactGroupMainObject> callUserCreateGroup;

    private List<CreateUserGroup> userGetGroupData = new ArrayList<>();
    private List<CreateUserGroup> userGetGroupDataNew = new ArrayList<>();
    List<String> groupNameList = new ArrayList<>();

    //    Instance fields....
    private Handler handler = new Handler();
    private Timer timer;
    private TimerTask timerTask;
    private int profileActivityLogCounter;
    private String activityType;
    private String userId, profileId;
    private String topBarFrom;
    private String userFrom = "";
    private GetUserProfileData getUserProfileData;
    Dialog exportDialog;
    private List<PhoneContactsModel> phoneContactsModelList = new ArrayList<>();
    private List<SelectedExportContact> tempSelectedExportContactsList = new ArrayList<>();
    private List<SelectedExportContact> selectedExportContactsList = new ArrayList<>();
    private PhoneNumberUtil phoneNumberUtil;
    private int totalExportContactCounter = 0;
    private String countryRegion;

    private boolean requestFrom;

    private String exportClickType;
    private SelectedExportContact selectedExportContact;
    private PhoneContactsModel foundedContactModel;
    private String contactFoundIn;
    private AlertDialog dialog;
    Dialog dialogAddToGroup;
    String senderProfileId;
    private AppCompatImageView imageViewSearchTopBar, imageViewBackTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;
    private SlideUp slideUp;
    private TextView textViewHeading;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentLnqUserProfileView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lnq_user_profile_view, container, false);
        return profileViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView topBarLayout = profileViewBinding.tobBar.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        imageViewSearchTopBar.setVisibility(View.GONE);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewContactGridTopBar.setVisibility(View.GONE);
        imageViewContactQRTopBar.setVisibility(View.GONE);
        init();
        createTransferUtility();
        cachePath = getContext().getCacheDir().getAbsolutePath();

        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        textViewHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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

    private void init() {
//        Registering event bus....
        EventBus.getDefault().register(this);

//        Starting timer to log activity for profile views....
        startTimer();

//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        setCustomFont();

//        Getting country region....
        countryRegion = ValidUtils.getCountryCode(getActivity());

//        Initialization of phone number utils....
        phoneNumberUtil = PhoneNumberUtil.getInstance();

//        Setting default view pagify 
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID, "");
            userFrom = getArguments().getString(Constants.REQUEST_FROM, "");
            profileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            topBarFrom = getArguments().getString("topBar", "");

            if (userFrom.equals("qr")) {
                senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("selectedProfileId", "");
            } else {
                senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
            }
            //        setting viewpagify adapter....
            final FullProfileViewPagerAdapter pagerAdapter = new FullProfileViewPagerAdapter(getActivity().getSupportFragmentManager(), true, profileId);
            profileViewBinding.viewPagify.setAdapter(pagerAdapter);
            String flag = getArguments().getString("mFlag", "");
            if (flag.equals("lnq_request")) {
                userId = getArguments().getString(EndpointKeys.USER_ID, "");
                profileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
                showDialouge();
            }else if (flag.equals("update_status")){
                reqGetUserProfile();
            }
            activityType = getArguments().getString(EndpointKeys.ACTIVITY_TYPE);
            if (userFrom.equalsIgnoreCase("mapSearch")) {
                reqGetUserProfileForMapUsers();
            } else {
                reqGetUserProfile();
            }
            if (activityType != null) {
                switch (activityType) {
                    case Constants.FAVORITED:
                    case Constants.UN_FAVORITED:
                    case Constants.REQUEST_ACCEPTED:
                    case Constants.BLOCKED_USER:
                    case Constants.UN_BLOCKED_USER:
                        profileViewBinding.viewPagify.setCurrentItemPosition(1);
                        break;
                    case Constants.UN_LNQ:
                    case Constants.CONTACTED:
                        profileViewBinding.viewPagify.setCurrentItemPosition(2);
                        break;
                    case Constants.TASK_EDITED:
                    case Constants.NOTE_EDITED:
                    case Constants.TASK_ADDED:
                    case Constants.NOTE_ADDED:
                        profileViewBinding.viewPagify.setCurrentItemPosition(0);
                        break;
                    case Constants.CONTACT_LIST:
                        profileViewBinding.viewPagify.setCurrentItemPosition(3);
                        break;
                }
            }
            int position = profileViewBinding.viewPagify.getCurrentItemPosition();
            if (position == 0) {
                profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(0));
                EventBus.getDefault().post(new EventBusUserSession("notes_task_view"));
            } else if (position == 1) {
                profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(1));
                EventBus.getDefault().post(new EventBusUserSession("about_view"));
            } else if (position == 2) {
                profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(2));
                EventBus.getDefault().post(new EventBusUserSession("history_view"));
            } else if (position == 3) {
                profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(3));
                EventBus.getDefault().post(new EventBusUserSession("contact_list"));
            }
        }

        Glide.with(getContext()).load(R.drawable.qloading).into(profileViewBinding.progressBar);

//        Setting all event listeners....
//        profileViewBinding.imageViewBack.setOnClickListener(this);
//        profileViewBinding.imageViewUpdateStatus.setOnClickListener(this);
//        profileViewBinding.imageViewSetting.setOnClickListener(this);
//        profileViewBinding.imageViewVisibility.setOnClickListener(this);
//        profileViewBinding.imageViewMenu.setOnClickListener(this);
        profileViewBinding.mRoot.setOnClickListener(this);
        profileViewBinding.relativeLayoutSendMessage.setOnClickListener(this);
//        profileViewBinding.relativeLayoutUnFavorite.setOnClickListener(this);
        profileViewBinding.textViewUnLnq.setOnClickListener(this);
        profileViewBinding.textViewCurrentLocation.setOnClickListener(this);
        profileViewBinding.buttonLnq.setOnClickListener(this);
        profileViewBinding.imageViewProfile.setOnClickListener(this);
        profileViewBinding.textViewNotes.setOnClickListener(this);
        profileViewBinding.textViewAbout.setOnClickListener(this);
        profileViewBinding.textViewHistory.setOnClickListener(this);
        profileViewBinding.textViewContactList.setOnClickListener(this);
        profileViewBinding.textViewProfileExportUser.setOnClickListener(this);
        profileViewBinding.textViewAddToGroup.setOnClickListener(this);
        profileViewBinding.viewPagify.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                    profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    EventBus.getDefault().post(new EventBusProfileSubPageClicked(0));
                    EventBus.getDefault().post(new EventBusUserSession("notes_task_view"));
                } else if (position == 1) {
                    profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                    profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    EventBus.getDefault().post(new EventBusProfileSubPageClicked(1));
                    EventBus.getDefault().post(new EventBusUserSession("about_view"));
                } else if (position == 2) {
                    profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                    profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    EventBus.getDefault().post(new EventBusProfileSubPageClicked(2));
                    EventBus.getDefault().post(new EventBusUserSession("history_view"));
                } else if (position == 3) {
                    profileViewBinding.textViewHistory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewContactList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                    profileViewBinding.textViewAbout.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                    profileViewBinding.textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewContactList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    profileViewBinding.textViewAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    profileViewBinding.textViewNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    EventBus.getDefault().post(new EventBusProfileSubPageClicked(3));
                    EventBus.getDefault().post(new EventBusUserSession("contact_list"));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        profileViewBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
                }
            }
        });

        if (topBarFrom.equalsIgnoreCase("activity")) {
            textViewHeading.setText(R.string.activity);
        } else if (topBarFrom.equalsIgnoreCase("messages")) {
            textViewHeading.setText(R.string.messages);
        } else if (topBarFrom.equalsIgnoreCase("contacts")) {
            textViewHeading.setText(R.string.connections);
        } else if (topBarFrom.equalsIgnoreCase("explore")) {
            textViewHeading.setText(R.string.explore);
        }
        ValidUtils.textViewGradientColor(textViewHeading);

        slideUp = new SlideUpBuilder(profileViewBinding.slideView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            profileViewBinding.tobBar.topBarContactCardView.setVisibility(View.VISIBLE);
                            profileViewBinding.viewHideTopBar.setVisibility(View.GONE);
                        } else {
                            profileViewBinding.tobBar.topBarContactCardView.setVisibility(View.INVISIBLE);
                            profileViewBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(profileViewBinding.viewScroll)
                .build();

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
//                        Glide.with(getActivity())
//                                .load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()))
//                                .apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                                .apply(new RequestOptions().circleCrop())
//                                .into(imageView);
////                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
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

    private void setCustomFont() {
        fontUtils.setTextViewRegularFont(profileViewBinding.textViewName);
        fontUtils.setTextViewRegularFont(profileViewBinding.textViewJobTitle);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewconnections);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewCompany);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewStatusMessage);
        fontUtils.setTextViewRegularFont(profileViewBinding.textViewHomeLocation);
        fontUtils.setTextViewRegularFont(profileViewBinding.textViewCurrentLocation);
        fontUtils.setTextViewRegularFont(profileViewBinding.textViewConnectionSince);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewConnectionDate);
        fontUtils.setTextViewRegularFont(profileViewBinding.textViewConnectionIn);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewConnectionLocation);
//        fontUtils.setTextViewSemiBold(profileViewBinding.textViewUnFavorite);
//        fontUtils.setTextViewRegularFont(profileViewBinding.textViewLocationHidden);
        fontUtils.setTextViewRegularFont(profileViewBinding.textViewUnLnq);
        fontUtils.setButtonSemiBold(profileViewBinding.buttonLnq);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewNotes);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewHistory);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewAbout);
        fontUtils.setTextViewSemiBold(profileViewBinding.textViewContactList);
    }


    public void showDialouge() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("LNQ Request");
        builder.setMessage("Do you want to accept the request?");
        builder.setPositiveButton("Accept Request", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                reqAcceptContactRequest(userId, profileId, senderProfileId);
            }
        });
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                reqDeclineContactRequest(userId, senderProfileId, profileId);
            }
        });
        // Create the AlertDialog object and return it
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (callGetUserProfile != null && callGetUserProfile.isExecuted()) {
            callGetUserProfile.cancel();
        }
        if (callFavUnFavLnq != null && callFavUnFavLnq.isExecuted()) {
            callFavUnFavLnq.cancel();
        }
        if (callBlackWhiteStatus != null && callBlackWhiteStatus.isExecuted()) {
            callBlackWhiteStatus.cancel();
        }
        if (callShowHideLocation != null && callShowHideLocation.isExecuted()) {
            callShowHideLocation.cancel();
        }
        if (callLogActivity != null && callLogActivity.isExecuted()) {
            callLogActivity.cancel();
        }
        if (callRequest != null && callRequest.isExecuted()) {
            callRequest.cancel();
        }
        if (callUserGetGroup != null && callUserGetGroup.isExecuted()) {
            callUserGetGroup.cancel();
        }
        stopTimer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSubPageClicked(EventBusProfileSubPageClicked mObj) {
        profileViewBinding.viewPagify.setCurrentItemPosition(mObj.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRefreshTaskNotes(EventBusRefreshTaskNotes eventBusRefreshTaskNotes) {
        if (userId != null) {
            reqGetUserProfile();
            stopTimer();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusAddToGroup(EventBusAddToGroup mObj) {
        if (userGetGroupDataNew.size() != 0) {
            String groupID = mObj.getGroupId();
            String groupName = mObj.getGroupName();
            List<String> reveiverIdList = new ArrayList<>();
            List<String> receiverProfileIdList = new ArrayList<>();

            reqCreateGroup(senderProfileId, groupName, userId, profileId, groupID, "addGroup");
        } else {
            ValidUtils.showCustomToast(getContext(), "All already exist in all groups");
        }
    }

    private void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        profileActivityLogCounter++;
                        if (profileActivityLogCounter == 59) {
                            stopTimer();
//                            logActivity();
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.mImgLookingFor:
                ((MainActivity) getActivity()).mFScreenName = Constants.LOOKING_FOR;
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOOKING_FOR, true, null);
                EventBus.getDefault().post(new EventBusUserSession("status_view"));
                break;
            case R.id.mImgSetting:
                ((MainActivity) getActivity()).mFScreenName = Constants.SETTING;
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SETTING, true, null);
                EventBus.getDefault().post(new EventBusUserSession("setting_view"));
                break;
            case R.id.mImgVisible:
                ((MainActivity) getActivity()).mFScreenName = Constants.VISIBLE;
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VISIBLE, true, null);
                EventBus.getDefault().post(new EventBusUserSession("visibility_view"));
                break;
//            case R.id.imageViewMenu:
//                if (profileViewBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && profileViewBinding.cardViewPopupProfile.getVisibility() == View.VISIBLE) {
//                    profileViewBinding.imageViewArrowUp.setVisibility(View.GONE);
//                    profileViewBinding.cardViewPopupProfile.setVisibility(View.GONE);
//                } else {
//                    profileViewBinding.imageViewArrowUp.setVisibility(View.VISIBLE);
//                    profileViewBinding.cardViewPopupProfile.setVisibility(View.VISIBLE);
//                }
//                break;
            case R.id.mRoot:
//                if (profileViewBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && profileViewBinding.cardViewPopupProfile.getVisibility() == View.VISIBLE) {
//                    profileViewBinding.imageViewArrowUp.setVisibility(View.GONE);
//                    profileViewBinding.cardViewPopupProfile.setVisibility(View.GONE);
//                }
                break;
            case R.id.textViewUnLnq:
//                if (getUserProfileData != null) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(EndpointKeys.USER_ID, userId);
//                    bundle.putString(EndpointKeys.PROFILE_ID, profileId);
//                    bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
//                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.UNLNQ_POPUP, true, bundle);
//                }
                if (getUserProfileData != null) {
                    if (getUserProfileData.getIs_connection().equals("")) {
                        if (userFrom.contains("Grid")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_USER_POPUP, true, bundle);
                        } else if (userFrom.contains("map")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_USER_POPUP, true, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_USER_POPUP, true, bundle);
//                            reqContactRequest(userId, "profile", senderProfileId, profileId);
                        }
                    } else if (getUserProfileData.getIs_connection().equals(Constants.CONTACTED)) {
                        Bundle bundle = new Bundle();
                        if (getUserProfileData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.RETRACT_REQUEST_POPUP, true, bundle);
                        } else {
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.ACCEPT_REQUEST_POPUP, true, bundle);
                        }
                    }else {
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(EndpointKeys.USER_ID, userId);
                        bundle1.putString(EndpointKeys.PROFILE_ID, profileId);
                        bundle1.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.UNLNQ_POPUP, true, bundle1);
                    }
                }
                break;
            case R.id.relativeLayoutUnFavorite:
                if (getUserProfileData != null) {
                    if (getUserProfileData.getIs_favorite().equals(Constants.FAVORITE)) {
                        reqFavUnFavLnq(Constants.UN_FAVORITE, senderProfileId, profileId);
                    } else {
                        reqFavUnFavLnq(Constants.FAVORITE, senderProfileId, profileId);
                    }
                }
                break;
            case R.id.relativeLayoutSendMessage:
                if (getUserProfileData != null) {
//                    if (profileViewBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && profileViewBinding.cardViewPopupProfile.getVisibility() == View.VISIBLE) {
//                        profileViewBinding.imageViewArrowUp.setVisibility(View.GONE);
//                        profileViewBinding.cardViewPopupProfile.setVisibility(View.GONE);
//                    } else {
//                        profileViewBinding.imageViewArrowUp.setVisibility(View.VISIBLE);
//                        profileViewBinding.cardViewPopupProfile.setVisibility(View.VISIBLE);
//                    }
                    Bundle bundleChat = new Bundle();
                    bundleChat.putString(EndpointKeys.USER_ID, userId);
                    bundleChat.putString(EndpointKeys.PROFILE_ID, profileId);
                    bundleChat.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                    bundleChat.putString(EndpointKeys.USER_AVATAR, getUserProfileData.getUser_avatar());
                    bundleChat.putString(EndpointKeys.IS_FAVORITE, getUserProfileData.getIs_favorite());
                    bundleChat.putString(EndpointKeys.USER_CONNECTION_STATUS, getUserProfileData.getIs_connection());
                    bundleChat.putString(EndpointKeys.IS_BLOCK, getUserProfileData.getIs_blocked());
                    bundleChat.putString(EndpointKeys.THREAD_ID, getUserProfileData.getThread_id());

                    EventBus.getDefault().post(new EventBusUserSession("conversation_view"));
                    ((MainActivity) getActivity()).fnLoadFragAdd("CHAT", true, bundleChat);
                }
                break;
            case R.id.textViewCurrentLocation:
                if (getUserProfileData != null)
                    if (getUserProfileData.getVisible_to() != null && getUserProfileData.getVisible_at() != null) {
                        if (getUserProfileData.getVisible_to().equals(Constants.NONE) || getUserProfileData.getVisible_to().equals(Constants.NEAR_BY)) {
                            if (!getUserProfileData.getVisible_at().equals(Constants.OFF_GRID)) {
                                if (getUserProfileData.getUser_lat() != null && getUserProfileData.getUser_long() != null && !getUserProfileData.getUser_lat().isEmpty() && !getUserProfileData.getUser_long().isEmpty()) {
                                    EventBus.getDefault().post(new EventBusUserZoomLevel(getUserProfileData.getId(), Double.parseDouble(getUserProfileData.getUser_lat()), Double.parseDouble(getUserProfileData.getUser_long()), getUserProfileData.getVisible_at()));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((MainActivity) getActivity()).popBackHomeFragment(Constants.HOME);
                                        }
                                    }, 200);
                                }
                            }
                        } else {
                            EventBus.getDefault().post(new EventBusUserZoomLevel(getUserProfileData.getId(), Double.parseDouble(getUserProfileData.getUser_lat()), Double.parseDouble(getUserProfileData.getUser_long()), "street"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity) getActivity()).popBackHomeFragment(Constants.HOME);
                                }
                            }, 200);
                        }
                    }
                break;
//            case R.id.imageViewUpdateStatus:
//                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOOKING_FOR, true, null);
//                EventBus.getDefault().post(new EventBusUserSession("status_view"));
//                break;
//            case R.id.imageViewVisibility:
//                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VISIBLE, true, null);
//                EventBus.getDefault().post(new EventBusUserSession("visibility_view"));
//                break;
//            case R.id.imageViewSetting:
//                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SETTING, true, null);
//                EventBus.getDefault().post(new EventBusUserSession("setting_view"));
//                break;
            case R.id.buttonLnq:
                if (getUserProfileData != null) {
                    if (getUserProfileData.getIs_connection().equals("")) {
                        if (userFrom.contains("Grid")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_USER_POPUP, true, bundle);
                        } else if (userFrom.contains("map")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_USER_POPUP, true, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_USER_POPUP, true, bundle);
//                            reqContactRequest(userId, "profile", senderProfileId, profileId);
                        }
                    } else if (getUserProfileData.getIs_connection().equals(Constants.CONTACTED)) {
                        Bundle bundle = new Bundle();
                        if (getUserProfileData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.RETRACT_REQUEST_POPUP, true, bundle);
                        } else {
                            bundle.putString(EndpointKeys.USER_ID, userId);
                            bundle.putString(EndpointKeys.PROFILE_ID, profileId);
                            bundle.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.ACCEPT_REQUEST_POPUP, true, bundle);
                        }
                    }else {
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(EndpointKeys.USER_ID, userId);
                        bundle1.putString(EndpointKeys.PROFILE_ID, profileId);
                        bundle1.putString(EndpointKeys.USER_NAME, getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.UNLNQ_POPUP, true, bundle1);
                    }
                }
                break;
            case R.id.imageViewProfile:
                if (getUserProfileData.getUser_avatar() != null) {

                    Intent myIntent = new Intent(getContext(),
                            FullProfilePictureActivity.class);
                    myIntent.putExtra("profileImage", getUserProfileData.getUser_avatar());
                    startActivity(myIntent);
                }
                break;
            case R.id.textViewHistory:
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(2));
                EventBus.getDefault().post(new EventBusUserSession("history_view"));
                break;
            case R.id.textViewNotes:
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(0));
                EventBus.getDefault().post(new EventBusUserSession("notes_task_view"));
                break;
            case R.id.textViewAbout:
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(1));
                EventBus.getDefault().post(new EventBusUserSession("about_view"));
                break;
            case R.id.textViewContactList:
                EventBus.getDefault().post(new EventBusProfileSubPageClicked(3));
                EventBus.getDefault().post(new EventBusUserSession("contact_list"));
                break;
            case R.id.textViewProfileExportUser:
                List<String> phoneData = Collections.singletonList(getUserProfileData.getUser_phone());
                List<String> emailData = Collections.singletonList(getUserProfileData.getUser_email());
                selectedExportContactsList.clear();
                selectedExportContactsList.add(new SelectedExportContact(getUserProfileData.getId(),
                        getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname(),
                        phoneData, emailData, getUserProfileData.getNote_on_user().getNote_description(),
                        getUserProfileData.getUser_address(), getUserProfileData.getUser_company(),
                        getUserProfileData.getUser_birthday(), "", getUserProfileData.getUser_avatar()));
                showExportContactsDialog();
                break;
            case R.id.textViewAddToGroup:
                if (userGetGroupData.size() != 0) {
                    if (userGetGroupDataNew.size() != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        View dialogView = inflater.inflate(R.layout.cus_dialog_addto_group, null);
                        RecyclerView recyclerViewAddGroupContactList = dialogView.findViewById(R.id.recyclerViewAddToGroup);
                        Button clearTextViewCancel = dialogView.findViewById(R.id.clearTextViewCancel);
                        AddToGroupAdapter addToGroupAdapter = new AddToGroupAdapter(getActivity(), userGetGroupDataNew);
                        recyclerViewAddGroupContactList.setAdapter(addToGroupAdapter);
                        recyclerViewAddGroupContactList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        builder.setView(dialogView);
                        dialogAddToGroup = builder.create();
                        dialogAddToGroup.show();

                        clearTextViewCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAddToGroup.cancel();
                            }
                        });

                        try {
                            dialogAddToGroup.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                        } catch (Exception e) {

                        }
                    } else {
                        ValidUtils.showCustomToast(getContext(), "This user is already in all groups.");
                    }
                }else {
                    ValidUtils.showCustomToast(getContext(), "You don't have any group right now.");
                }
                break;
        }
    }

    public void showExportContactsDialog() {
//        if (profileViewBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && profileViewBinding.cardViewPopupProfile.getVisibility() == View.VISIBLE) {
//            profileViewBinding.imageViewArrowUp.setVisibility(View.GONE);
//            profileViewBinding.cardViewPopupProfile.setVisibility(View.GONE);
//        } else {
//            profileViewBinding.imageViewArrowUp.setVisibility(View.VISIBLE);
//            profileViewBinding.cardViewPopupProfile.setVisibility(View.VISIBLE);
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.cus_dialog_exportcontacts, null);
        Button textViewMyPhone = dialogView.findViewById(R.id.textViewProfilePhoneContacts);
        Button textViewSalesForce = dialogView.findViewById(R.id.textViewProfileSalesForceConnections);
        Button textViewCSV = dialogView.findViewById(R.id.textViewProfileCSVConnections);
        Button textViewCancel = dialogView.findViewById(R.id.textViewProfileCancel);
        ValidUtils.buttonGradientColor(textViewCancel);

        textViewMyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyPhoneClick(v);
                exportDialog.dismiss();
            }
        });

        textViewSalesForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSalesforceClick(v);
                exportDialog.dismiss();
            }
        });

        textViewCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCSVClick(v);
                exportDialog.dismiss();
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDialog.dismiss();
            }
        });
        builder.setView(dialogView);
        exportDialog = builder.create();
        exportDialog.show();

        try {
            exportDialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

        } catch (Exception e) {

        }
    }

    public void onMyPhoneClick(View view) {
        exportClickType = "phone";
        if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
            exportUsers();
            EventBus.getDefault().post(new EventBusUserSession("export_contacts"));
        } else {
            ((MainActivity) getActivity()).fnRequestContactsPermission(9);
        }
    }

    public void onSalesforceClick(View view) {
        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOGIN_SALESFORCE, true, null);
    }

    public void onCSVClick(View view) {
        exportClickType = "csv";
        if (((MainActivity) getActivity()).fnCheckReadStoragePermission()) {
            try {
                exportTheDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ((MainActivity) getActivity()).fnRequestStoragePermission(9);
        }
    }

    public void exportUsers() {
        if (selectedExportContactsList.size() > 0) {
//            ((MainActivity) getActivity()).progressDialog.show();
            new GetAllContactsTask().execute();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSalesforceLogin(EventBusSalesforceLogin eventBusSalesforceLogin) {
        OAuthTokens tokens = OAuthUtil.Load(getApplicationContext());
        if (tokens != null) {
            String salesforceInstanceUrl = tokens.get_instance_url();
            if (salesforceInstanceUrl != null && !salesforceInstanceUrl.isEmpty()) {
                exportContactsToSalesforce(salesforceInstanceUrl, tokens.get_access_token());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusContactPermissionProfile(EventBusContactPermissionProfile eventBusContactPermissionProfile) {
        if (exportClickType != null) {
            if (exportClickType.equals("phone")) {
                exportUsers();
                EventBus.getDefault().post(new EventBusUserSession("export_contacts"));
            } else {
                try {
                    exportTheDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateUserStatus(EventBusUpdateUserStatus eventBusUpdateUserStatus) {
        if (getUserProfileData != null) {
            if (eventBusUpdateUserStatus.getUserStatus().equals("cancel") || eventBusUpdateUserStatus.getUserStatus().equals("")) {
                getUserProfileData.setIs_connection("");
                requestFrom = eventBusUpdateUserStatus.isFromFcm();
            } else if (eventBusUpdateUserStatus.getUserStatus().equals(Constants.CONNECTED)) {
                getUserProfileData.setIs_connection(Constants.CONNECTED);
                requestFrom = eventBusUpdateUserStatus.isFromFcm();
            } else if (eventBusUpdateUserStatus.getUserStatus().equals(Constants.CONTACTED)) {
                getUserProfileData.setIs_connection(Constants.CONTACTED);
                requestFrom = eventBusUpdateUserStatus.isFromFcm();
            }
            setUserConnectionStatus();
            reqGetUserProfileForLnqContactStatus();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateTaskhistory(EventBusUpdateTaskHistory mObj) {
        reqGetUserProfileForCompletedTask();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (getUserProfileData != null) {
            if (buttonView == profileViewBinding.switchCompatFullyBlock) {
                if (isChecked) {
                    reqBlockUnblock(Constants.BLOCK, senderProfileId, profileId);
                } else {
                    reqBlockUnblock(Constants.UNBLOCK, senderProfileId, profileId);
                }
            } else if (buttonView == profileViewBinding.switchCompatLocationHidden) {
                if (isChecked) {
                    reqHideShowLocation(Constants.HIDE, senderProfileId, profileId);
                } else {
                    reqHideShowLocation(Constants.SHOW, senderProfileId, profileId);
                }
            } else if (buttonView == profileViewBinding.switchCompatFavroiut) {
                if (isChecked) {
                    reqFavUnFavLnq(Constants.FAVORITE, senderProfileId, profileId);
                } else {
                    reqFavUnFavLnq(Constants.UN_FAVORITE, senderProfileId, profileId);
                }
            }
        }
    }

    private void setUserConnectionStatus() {
        if (getUserProfileData.getIs_connection() != null) {
            if (getUserProfileData.getIs_favorite().equals(Constants.FAVORITE)) {
                profileViewBinding.imageViewLinkedConnection.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionLocation.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionSince.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionDate.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionIn.setVisibility(View.VISIBLE);
                profileViewBinding.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                profileViewBinding.imageViewProfileLinked.setVisibility(View.GONE);
                profileViewBinding.imageViewProfile.setBackground(null);
//                profileViewBinding.textViewUnFavorite.setText(getResources().getString(R.string.un_favorite));
//                profileViewBinding.imageViewUnFavorite.setImageResource(R.drawable.icon_star_fill);
                profileViewBinding.switchCompatFavroiut.setChecked(true);
                profileViewBinding.textViewConnectionDate.setText(getUserProfileData.getConnection_date());
                if (getUserProfileData.getIs_connection().equals(Constants.CONTACTED)) {
                    profileViewBinding.viewPagify.setCurrentItemPosition(1);
                    profileViewBinding.buttonLnq.setTextColor(getResources().getColor(R.color.colorWhite));
                    if (getUserProfileData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) && getUserProfileData.getProfile_id().equals(profileId) && !requestFrom) {
                        profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getSender_location());
                        profileViewBinding.textViewConnectionSince.setText(getResources().getString(R.string.request_sent_on));
//                        profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_circle_teal));
                        profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                        profileViewBinding.buttonLnq.setText(getResources().getString(R.string.sent));
                        profileViewBinding.textViewUnLnq.setText(getResources().getString(R.string.sent));
                    } else {
//                        profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_circle_green));
                        profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                        profileViewBinding.buttonLnq.setText(getResources().getString(R.string.recv));
                        profileViewBinding.textViewUnLnq.setText(getResources().getString(R.string.recv));
                        profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getReceiver_location());
                        profileViewBinding.textViewConnectionSince.setText(getResources().getString(R.string.request_received_on));
                    }
                    profileViewBinding.imageViewFavoriteBorder.setImageResource(R.drawable.ic_fav_teen_border);
                } else if (getUserProfileData.getIs_connection().equals(Constants.CONNECTED)) {
                    profileViewBinding.viewPagify.setCurrentItemPosition(2);
                    profileViewBinding.buttonLnq.setVisibility(View.GONE);
                    profileViewBinding.textViewConnectionSince.setText(getResources().getString(R.string.since));
                    if (getUserProfileData.getProfile_id() != null && !getUserProfileData.getProfile_id().isEmpty()) {
                        if (getUserProfileData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) && getUserProfileData.getProfile_id().equals(profileId)) {
                            profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getSender_location());
                        } else {
                            profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getReceiver_location());
                        }
                    }
                } else if (getUserProfileData.getIs_connection().equals("")) {
                    profileViewBinding.viewPagify.setCurrentItemPosition(1);
                    profileViewBinding.imageViewFavoriteBorder.setImageResource(R.drawable.ic_fav_lnq_border);
                    profileViewBinding.imageViewLinkedConnection.setVisibility(View.GONE);
                    profileViewBinding.textViewConnectionSince.setVisibility(View.INVISIBLE);
                    profileViewBinding.textViewConnectionLocation.setVisibility(View.INVISIBLE);
                    profileViewBinding.textViewConnectionIn.setVisibility(View.INVISIBLE);
                    profileViewBinding.textViewConnectionDate.setVisibility(View.INVISIBLE);
//                    profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_circle_white));
//                    profileViewBinding.buttonLnq.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
//                    profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_grey_new));
//                    profileViewBinding.buttonLnq.setTextColor(getResources().getColor(R.color.colorWhite));
                    profileViewBinding.buttonLnq.setText(getResources().getString(R.string.lnq));
                    profileViewBinding.textViewUnLnq.setText(getResources().getString(R.string.lnq));
                }
            } else {
//                profileViewBinding.textViewUnFavorite.setText(getResources().getString(R.string.favorite));
//                profileViewBinding.imageViewUnFavorite.setImageResource(R.drawable.icon_star_stroke);
                profileViewBinding.switchCompatFavroiut.setChecked(false);
                profileViewBinding.imageViewProfileLinked.setVisibility(View.VISIBLE);
                profileViewBinding.imageViewLinkedConnection.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionSince.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionLocation.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionIn.setVisibility(View.VISIBLE);
                profileViewBinding.textViewConnectionDate.setVisibility(View.VISIBLE);
                profileViewBinding.imageViewFavoriteBorder.setVisibility(View.GONE);
                profileViewBinding.textViewConnectionDate.setText(getUserProfileData.getConnection_date());
                if (getUserProfileData.getIs_connection().equals("")) {
                    profileViewBinding.viewPagify.setCurrentItemPosition(1);
                    profileViewBinding.imageViewProfile.setBackground(getResources().getDrawable(R.drawable.bg_circle_grey_border));
                    profileViewBinding.imageViewProfileLinked.setVisibility(View.GONE);
                    profileViewBinding.imageViewLinkedConnection.setVisibility(View.INVISIBLE);
                    profileViewBinding.textViewConnectionSince.setVisibility(View.INVISIBLE);
                    profileViewBinding.textViewConnectionLocation.setVisibility(View.INVISIBLE);
                    profileViewBinding.textViewConnectionIn.setVisibility(View.INVISIBLE);
                    profileViewBinding.textViewConnectionDate.setVisibility(View.INVISIBLE);
//                    profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_circle_white));
//                    profileViewBinding.buttonLnq.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
//                    profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_grey_new));
//                    profileViewBinding.buttonLnq.setTextColor(getResources().getColor(R.color.colorWhite));
                    profileViewBinding.buttonLnq.setText(getResources().getString(R.string.lnq));
                    profileViewBinding.textViewUnLnq.setText(getResources().getString(R.string.lnq));
                } else if (getUserProfileData.getIs_connection().equals(Constants.CONNECTED)) {
                    profileViewBinding.viewPagify.setCurrentItemPosition(2);
                    profileViewBinding.imageViewProfile.setBackground(getResources().getDrawable(R.drawable.bg_circle_green_border));
                    profileViewBinding.imageViewProfileLinked.setBackground(getResources().getDrawable(R.drawable.bg_circle_green));
                    profileViewBinding.textViewConnectionSince.setText(getResources().getString(R.string.since));
//                    profileViewBinding.buttonLnq.setVisibility(View.GONE);
                    profileViewBinding.buttonLnq.setText("LNQ'd");
                    if (profileId != null && !profileId.isEmpty()) {
                        if (getUserProfileData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString("id", "")) && getUserProfileData.getProfile_id().equals(profileId)) {
                            profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getSender_location());
                        } else {
                            profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getReceiver_location());
                        }
                    }
                } else if (getUserProfileData.getIs_connection().equals(Constants.CONTACTED)) {
                    profileViewBinding.viewPagify.setCurrentItemPosition(1);
                    profileViewBinding.imageViewProfile.setBackground(getResources().getDrawable(R.drawable.bg_circle_teen_border));
                    profileViewBinding.imageViewProfileLinked.setBackground(getResources().getDrawable(R.drawable.bg_circle_teen));
                    profileViewBinding.buttonLnq.setTextColor(getResources().getColor(R.color.colorWhite));
                    profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                    if (profileId != null && !profileId.isEmpty()) {
                        if (getUserProfileData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) && getUserProfileData.getProfile_id().equals(profileId) && !requestFrom) {
                            profileViewBinding.viewPagify.setCurrentItemPosition(1);
                            profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getSender_location());
                            profileViewBinding.textViewConnectionSince.setText(getResources().getString(R.string.request_sent_on));
//                        profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_circle_teal));
                            profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                            profileViewBinding.buttonLnq.setText(getResources().getString(R.string.sent));
                            profileViewBinding.textViewUnLnq.setText(getResources().getString(R.string.sent));
                        } else {
                            profileViewBinding.viewPagify.setCurrentItemPosition(1);
                            profileViewBinding.textViewConnectionLocation.setText(getUserProfileData.getReceiver_location());
                            profileViewBinding.textViewConnectionSince.setText(getResources().getString(R.string.request_received_on));
                            profileViewBinding.buttonLnq.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                            profileViewBinding.buttonLnq.setText(getResources().getString(R.string.recv));
                            profileViewBinding.textViewUnLnq.setText(getResources().getString(R.string.recv));
                        }
                    }
                }
            }
        }
    }

    private void reqGetUserProfile() {
        profileViewBinding.textViewName.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewJobTitle.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewCompany.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.INVISIBLE);
//        profileViewBinding.buttonLnq.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfile.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewFavoriteBorder.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfileLinked.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewStatusMessage.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewHomeLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewCurrentLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.linearLayoutConnectionInfo.setVisibility(View.INVISIBLE);
        profileViewBinding.pagerView.setVisibility(View.INVISIBLE);
        profileViewBinding.viewPagify.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewHomeLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewCurrentLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewQuoteLeft.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewQuoteRight.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewLinkedConnection.setVisibility(View.INVISIBLE);
        profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.VISIBLE);
        profileViewBinding.shimmerLayoutFullProfile.startLayoutAnimation();
        profileViewBinding.recyclerViewGroupNames.setVisibility(View.GONE);

        if (profileId != null && !profileId.isEmpty()) {
            callGetUserProfile = Api.WEB_SERVICE.getUserActiveProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, senderProfileId, profileId);
        } else {
            callGetUserProfile = Api.WEB_SERVICE.getUserProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId);
        }
        callGetUserProfile.enqueue(new Callback<GetUserProfileMainObject>() {
            public void onResponse(Call<GetUserProfileMainObject> call, Response<GetUserProfileMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            reqShowAllGroup(senderProfileId);
                            profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                            profileViewBinding.shimmerLayoutFullProfile.stopShimmerAnimation();
                            profileViewBinding.textViewName.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewJobTitle.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewCompany.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewProfile.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewProfileLinked.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewStatusMessage.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewHomeLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewCurrentLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.linearLayoutConnectionInfo.setVisibility(View.VISIBLE);
                            profileViewBinding.pagerView.setVisibility(View.VISIBLE);
                            profileViewBinding.viewPagify.setVisibility(View.VISIBLE);

                            getUserProfileData = response.body().getGetUserProfile();

                            profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewHomeLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewCurrentLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewQuoteLeft.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewQuoteRight.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewLinkedConnection.setVisibility(View.VISIBLE);
                            profileViewBinding.buttonLnq.setVisibility(View.VISIBLE);
                            if (getUserProfileData.getCommon_connections().equalsIgnoreCase("")) {
                                profileViewBinding.textViewconnections.setVisibility(View.GONE);
                            } else {
                                profileViewBinding.textViewconnections.setText(getUserProfileData.getCommon_connections() + " common connections");
                            }
                            if (getUserProfileData.getIs_blocked().equals("blocked")) {
                                profileViewBinding.switchCompatFullyBlock.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatFullyBlock.setChecked(false);
                            }
                            if (getUserProfileData.getLocation().equalsIgnoreCase(Constants.HIDDEN)) {
                                profileViewBinding.switchCompatLocationHidden.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatLocationHidden.setChecked(false);
                            }
                            if (getUserProfileData.getIs_favorite().equalsIgnoreCase(Constants.FAVORITE)) {
                                profileViewBinding.switchCompatFavroiut.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatFavroiut.setChecked(false);
                            }
                            profileViewBinding.switchCompatFullyBlock.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatLocationHidden.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatFavroiut.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);

                            if (getUserProfileData.getUser_fname() != null && getUserProfileData.getUser_lname() != null) {
                                profileViewBinding.textViewName.setText(getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                                profileViewBinding.mTvAccountHeading1.setText(getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            }

                            if (getUserProfileData.getUser_current_position() != null && getUserProfileData.getUser_company() != null) {
                                String jobTitle = getUserProfileData.getUser_current_position();
                                String company = getUserProfileData.getUser_company();
                                if (jobTitle.isEmpty()) {
                                    profileViewBinding.textViewJobTitle.setText("");
                                } else {
                                    profileViewBinding.textViewJobTitle.setText(jobTitle + " . ");
                                }
                                profileViewBinding.textViewCompany.setText(company);
                            }

                            if (getUserProfileData.getUser_status_msg() != null) {
                                if (getUserProfileData.getUser_status_msg().isEmpty()) {
                                    profileViewBinding.textViewStatusMessage.setText("No status yet.");
                                } else {
                                    profileViewBinding.textViewStatusMessage.setText(getUserProfileData.getUser_status_msg());
                                }
                            }

                            if (getUserProfileData.getUser_address() != null) {
                                profileViewBinding.textViewHomeLocation.setText(getUserProfileData.getUser_address());
                            }

                            if (getUserProfileData.getLocation_name() != null && getUserProfileData.getDistance() != null) {
                                if (getUserProfileData.getLocation_name().isEmpty() && getUserProfileData.getDistance().isEmpty()) {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.GONE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.GONE);
                                } else {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.VISIBLE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.VISIBLE);
                                    if (getUserProfileData.getLocation_name().isEmpty()) {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(distance);
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getDistance() + " mi");
                                            }
                                        }
                                    } else {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + distance + " mi");
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + getUserProfileData.getDistance() + " mi");

                                            }
                                        }
                                    }
                                }
                            }
                            if (getUserProfileData.getUser_avatar() != null && !getUserProfileData.getUser_avatar().isEmpty()) {
                                download(getUserProfileData.getUser_avatar(), profileViewBinding.imageViewProfile);
                            }

                            setUserConnectionStatus();
                            EventBus.getDefault().post(new EventBusAboutUserData(getUserProfileData.getUser_bio(), getUserProfileData.getUser_interests(), getUserProfileData.getUser_fname()));
                            EventBus.getDefault().post(new EventBusNotesUserData(userId, getUserProfileData.getIs_connection(), getUserProfileData.getUser_fname(), getUserProfileData.getNote_on_user(), getUserProfileData.getTask_on_user()));
                            EventBus.getDefault().post(new EventBusUsersHistory(getUserProfileData.getIs_connection(), getUserProfileData.getIs_favorite(), getUserProfileData.getId(), getUserProfileData.getUser_history(), getUserProfileData.getUser_fname()));
                            EventBus.getDefault().post(new EventBusContactsListUserData(getUserProfileData.getUser_phone(), getUserProfileData.getSecondary_phones(), getUserProfileData.getUser_email(), getUserProfileData.getSecondary_emails(), getUserProfileData.getSocial_links(), getUserProfileData.getUser_fname()));
                            profileViewBinding.viewPagify.setVisibility(View.VISIBLE);
                            profileViewBinding.pagerView.setVisibility(View.VISIBLE);
                            break;
                        case 0:
                            profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                            profileViewBinding.shimmerLayoutFullProfile.stopShimmerAnimation();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    private void reqGetUserProfileForMapUsers() {
        profileViewBinding.textViewName.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewJobTitle.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewCompany.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.INVISIBLE);
//        profileViewBinding.buttonLnq.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfile.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewFavoriteBorder.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfileLinked.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewStatusMessage.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewHomeLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.textViewCurrentLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.linearLayoutConnectionInfo.setVisibility(View.INVISIBLE);
        profileViewBinding.pagerView.setVisibility(View.INVISIBLE);
        profileViewBinding.viewPagify.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewHomeLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewCurrentLocation.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewQuoteLeft.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewQuoteRight.setVisibility(View.INVISIBLE);
        profileViewBinding.imageViewLinkedConnection.setVisibility(View.INVISIBLE);
//        profileViewBinding.textViewconnections.setVisibility(View.INVISIBLE);

        profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.VISIBLE);
        profileViewBinding.shimmerLayoutFullProfile.startLayoutAnimation();
//        callGetUserProfile = Api.WEB_SERVICE.getUserProfile(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId);

        if (profileId != null && !profileId.isEmpty()) {
            callGetUserProfile = Api.WEB_SERVICE.getUserActiveProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, senderProfileId, profileId);
        } else {
            callGetUserProfile = Api.WEB_SERVICE.getUserProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId);
        }
        callGetUserProfile.enqueue(new Callback<GetUserProfileMainObject>() {
            public void onResponse(Call<GetUserProfileMainObject> call, Response<GetUserProfileMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                            profileViewBinding.shimmerLayoutFullProfile.stopShimmerAnimation();

                            profileViewBinding.textViewName.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewJobTitle.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewCompany.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.VISIBLE);
//                            profileViewBinding.buttonLnq.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewProfile.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewProfileLinked.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewStatusMessage.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewHomeLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.textViewCurrentLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.linearLayoutConnectionInfo.setVisibility(View.VISIBLE);
                            profileViewBinding.pagerView.setVisibility(View.VISIBLE);
                            profileViewBinding.viewPagify.setVisibility(View.VISIBLE);
//                            profileViewBinding.textViewconnections.setVisibility(View.VISIBLE);

                            getUserProfileData = response.body().getGetUserProfile();

                            profileViewBinding.imageViewProfileVerifiedTick.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewHomeLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewCurrentLocation.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewQuoteLeft.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewQuoteRight.setVisibility(View.VISIBLE);
                            profileViewBinding.imageViewLinkedConnection.setVisibility(View.VISIBLE);
                            profileViewBinding.buttonLnq.setVisibility(View.VISIBLE);
                            if (getUserProfileData.getCommon_connections().equalsIgnoreCase("")) {
                                profileViewBinding.textViewconnections.setVisibility(View.GONE);
                            } else {
                                profileViewBinding.textViewconnections.setText(getUserProfileData.getCommon_connections() + " common connections");
                            }
                            if (getUserProfileData.getIs_blocked().equals("blocked")) {
                                profileViewBinding.switchCompatFullyBlock.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatFullyBlock.setChecked(false);
                            }
                            if (getUserProfileData.getLocation().equalsIgnoreCase(Constants.HIDDEN)) {
                                profileViewBinding.switchCompatLocationHidden.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatLocationHidden.setChecked(false);
                            }
                            profileViewBinding.switchCompatFullyBlock.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatLocationHidden.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatFavroiut.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);

                            if (getUserProfileData.getUser_fname() != null && getUserProfileData.getUser_lname() != null) {
                                profileViewBinding.textViewName.setText(getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            }

                            if (getUserProfileData.getUser_current_position() != null && getUserProfileData.getUser_company() != null) {
                                String jobTitle = getUserProfileData.getUser_current_position();
                                String company = getUserProfileData.getUser_company();
                                if (jobTitle.isEmpty()) {
                                    profileViewBinding.textViewJobTitle.setText("");
                                } else {
                                    profileViewBinding.textViewJobTitle.setText(jobTitle + " . ");
                                }
                                profileViewBinding.textViewCompany.setText(company);
                            }

                            if (getUserProfileData.getUser_status_msg() != null) {
                                if (getUserProfileData.getUser_status_msg().isEmpty()) {
                                    profileViewBinding.textViewStatusMessage.setText("No status yet.");
                                } else {
                                    profileViewBinding.textViewStatusMessage.setText(getUserProfileData.getUser_status_msg());
                                }
                            }

                            if (getUserProfileData.getUser_address() != null) {
                                profileViewBinding.textViewHomeLocation.setText(getUserProfileData.getUser_address());
                            }

                            if (getUserProfileData.getLocation_name() != null && getUserProfileData.getDistance() != null) {
                                if (getUserProfileData.getLocation_name().isEmpty() && getUserProfileData.getDistance().isEmpty()) {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.GONE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.GONE);
                                } else {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.VISIBLE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.VISIBLE);
                                    if (getUserProfileData.getLocation_name().isEmpty()) {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(distance);
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getDistance() + " mi");
                                            }
                                        }
                                    } else {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + distance + " mi");
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + getUserProfileData.getDistance() + " mi");

                                            }
                                        }
                                    }
                                }
                            }
                            if (getUserProfileData.getUser_avatar() != null && !getUserProfileData.getUser_avatar().isEmpty()) {
                                download(getUserProfileData.getUser_avatar(), profileViewBinding.imageViewProfile);
                            }

                            profileViewBinding.viewPagify.setCurrentItemPosition(0);
                            EventBus.getDefault().post(new EventBusAboutUserData(getUserProfileData.getUser_bio(), getUserProfileData.getUser_interests(), getUserProfileData.getUser_fname()));
                            EventBus.getDefault().post(new EventBusNotesUserData(userId, getUserProfileData.getIs_connection(), getUserProfileData.getUser_fname(), getUserProfileData.getNote_on_user(), getUserProfileData.getTask_on_user()));
                            EventBus.getDefault().post(new EventBusUsersHistory(getUserProfileData.getIs_connection(), getUserProfileData.getIs_favorite(), getUserProfileData.getId(), getUserProfileData.getUser_history(), getUserProfileData.getUser_fname()));
                            EventBus.getDefault().post(new EventBusContactsListUserData(getUserProfileData.getUser_phone(), getUserProfileData.getSecondary_phones(), getUserProfileData.getUser_email(), getUserProfileData.getSecondary_emails(), getUserProfileData.getSocial_links(), getUserProfileData.getUser_fname()));
                            profileViewBinding.viewPagify.setVisibility(View.VISIBLE);
                            profileViewBinding.pagerView.setVisibility(View.VISIBLE);
                            break;
                        case 0:
                            profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                            profileViewBinding.shimmerLayoutFullProfile.stopShimmerAnimation();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    private void reqGetUserProfileForCompletedTask() {
//        callGetUserProfile = Api.WEB_SERVICE.getUserProfile(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId);
        callGetUserProfile = Api.WEB_SERVICE.getUserActiveProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, senderProfileId, profileId);
        callGetUserProfile.enqueue(new Callback<GetUserProfileMainObject>() {
            public void onResponse(Call<GetUserProfileMainObject> call, Response<GetUserProfileMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:

                            getUserProfileData = response.body().getGetUserProfile();
                            if (getUserProfileData.getCommon_connections().equalsIgnoreCase("")) {
                                profileViewBinding.textViewconnections.setVisibility(View.GONE);
                            } else {
                                profileViewBinding.textViewconnections.setText(getUserProfileData.getCommon_connections() + " common connections");
                            }
                            if (getUserProfileData.getIs_blocked().equals("blocked")) {
                                profileViewBinding.switchCompatFullyBlock.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatFullyBlock.setChecked(false);
                            }
                            if (getUserProfileData.getLocation().equalsIgnoreCase(Constants.HIDDEN)) {
                                profileViewBinding.switchCompatLocationHidden.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatLocationHidden.setChecked(false);
                            }
                            profileViewBinding.switchCompatFullyBlock.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatLocationHidden.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatFavroiut.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);

                            if (getUserProfileData.getUser_fname() != null && getUserProfileData.getUser_lname() != null) {
                                profileViewBinding.textViewName.setText(getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            }

                            if (getUserProfileData.getUser_current_position() != null && getUserProfileData.getUser_company() != null) {
                                String jobTitle = getUserProfileData.getUser_current_position();
                                String company = getUserProfileData.getUser_company();
                                if (jobTitle.isEmpty()) {
                                    profileViewBinding.textViewJobTitle.setText("");
                                } else {
                                    profileViewBinding.textViewJobTitle.setText(jobTitle + " . ");
                                }
                                profileViewBinding.textViewCompany.setText(company);
                            }

                            if (getUserProfileData.getUser_status_msg() != null) {
                                if (getUserProfileData.getUser_status_msg().isEmpty()) {
                                    profileViewBinding.textViewStatusMessage.setText("No status yet.");
                                } else {
                                    profileViewBinding.textViewStatusMessage.setText(getUserProfileData.getUser_status_msg());
                                }
                            }

                            if (getUserProfileData.getUser_address() != null) {
                                profileViewBinding.textViewHomeLocation.setText(getUserProfileData.getUser_address());
                            }

                            if (getUserProfileData.getLocation_name() != null && getUserProfileData.getDistance() != null) {
                                if (getUserProfileData.getLocation_name().isEmpty() && getUserProfileData.getDistance().isEmpty()) {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.GONE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.GONE);
                                } else {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.VISIBLE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.VISIBLE);
                                    if (getUserProfileData.getLocation_name().isEmpty()) {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(distance);
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getDistance() + " mi");
                                            }
                                        }
                                    } else {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + distance + " mi");
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + getUserProfileData.getDistance() + " mi");

                                            }
                                        }
                                    }
                                }
                            }
                            if (getUserProfileData.getUser_avatar() != null && !getUserProfileData.getUser_avatar().isEmpty()) {
                                download(getUserProfileData.getUser_avatar(), profileViewBinding.imageViewProfile);
                            }
                            profileViewBinding.viewPagify.setCurrentItemPosition(0);
                            EventBus.getDefault().post(new EventBusAboutUserData(getUserProfileData.getUser_bio(), getUserProfileData.getUser_interests(), getUserProfileData.getUser_fname()));
//                            EventBus.getDefault().post(new EventBusNotesUserData(userId, getUserProfileData.getIs_connection(), getUserProfileData.getUser_fname(), getUserProfileData.getNote_on_user(), getUserProfileData.getTask_on_user()));
                            EventBus.getDefault().post(new EventBusUsersHistory(getUserProfileData.getIs_connection(), getUserProfileData.getIs_favorite(), getUserProfileData.getId(), getUserProfileData.getUser_history(), getUserProfileData.getUser_fname()));
                            EventBus.getDefault().post(new EventBusContactsListUserData(getUserProfileData.getUser_phone(), getUserProfileData.getSecondary_phones(), getUserProfileData.getUser_email(), getUserProfileData.getSecondary_emails(), getUserProfileData.getSocial_links(), getUserProfileData.getUser_fname()));
                            profileViewBinding.viewPagify.setVisibility(View.VISIBLE);
                            profileViewBinding.pagerView.setVisibility(View.VISIBLE);
                            break;
                        case 0:
                            profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                            profileViewBinding.shimmerLayoutFullProfile.stopShimmerAnimation();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    private void reqGetUserProfileForLnqContactStatus() {
//        callGetUserProfile = Api.WEB_SERVICE.getUserProfile(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId);
        callGetUserProfile = Api.WEB_SERVICE.getUserActiveProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, senderProfileId, profileId);
        callGetUserProfile.enqueue(new Callback<GetUserProfileMainObject>() {
            public void onResponse(Call<GetUserProfileMainObject> call, Response<GetUserProfileMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:

                            getUserProfileData = response.body().getGetUserProfile();
                            if (getUserProfileData.getCommon_connections().equalsIgnoreCase("")) {
                                profileViewBinding.textViewconnections.setVisibility(View.GONE);
                            } else {
                                profileViewBinding.textViewconnections.setText(getUserProfileData.getCommon_connections() + " common connections");
                            }
                            if (getUserProfileData.getIs_blocked().equals("blocked")) {
                                profileViewBinding.switchCompatFullyBlock.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatFullyBlock.setChecked(false);
                            }
                            if (getUserProfileData.getLocation().equalsIgnoreCase(Constants.HIDDEN)) {
                                profileViewBinding.switchCompatLocationHidden.setChecked(true);
                            } else {
                                profileViewBinding.switchCompatLocationHidden.setChecked(false);
                            }
                            profileViewBinding.switchCompatFullyBlock.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatLocationHidden.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);
                            profileViewBinding.switchCompatFavroiut.setOnCheckedChangeListener(FragmentLnqUserProfileView.this);

                            if (getUserProfileData.getUser_fname() != null && getUserProfileData.getUser_lname() != null) {
                                profileViewBinding.textViewName.setText(getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname());
                            }

                            if (getUserProfileData.getUser_current_position() != null && getUserProfileData.getUser_company() != null) {
                                String jobTitle = getUserProfileData.getUser_current_position();
                                String company = getUserProfileData.getUser_company();
                                if (jobTitle.isEmpty()) {
                                    profileViewBinding.textViewJobTitle.setText("");
                                } else {
                                    profileViewBinding.textViewJobTitle.setText(jobTitle + " . ");
                                }
                                profileViewBinding.textViewCompany.setText(company);
                            }

                            if (getUserProfileData.getUser_status_msg() != null) {
                                if (getUserProfileData.getUser_status_msg().isEmpty()) {
                                    profileViewBinding.textViewStatusMessage.setText("No status yet.");
                                } else {
                                    profileViewBinding.textViewStatusMessage.setText(getUserProfileData.getUser_status_msg());
                                }
                            }

                            if (getUserProfileData.getUser_address() != null) {
                                profileViewBinding.textViewHomeLocation.setText(getUserProfileData.getUser_address());
                            }

                            if (getUserProfileData.getLocation_name() != null && getUserProfileData.getDistance() != null) {
                                if (getUserProfileData.getLocation_name().isEmpty() && getUserProfileData.getDistance().isEmpty()) {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.GONE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.GONE);
                                } else {
                                    profileViewBinding.textViewCurrentLocation.setVisibility(View.VISIBLE);
                                    profileViewBinding.imageViewCurrentLocation.setVisibility(View.VISIBLE);
                                    if (getUserProfileData.getLocation_name().isEmpty()) {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(distance);
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getDistance() + " mi");
                                            }
                                        }
                                    } else {
                                        if (getUserProfileData.getDistance().length() > 0) {
                                            if (Double.parseDouble(getUserProfileData.getDistance()) > 5.0 && getUserProfileData.getDistance().contains(".")) {
                                                String distance = getUserProfileData.getDistance().substring(0, getUserProfileData.getDistance().indexOf("."));
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + distance + " mi");
                                            } else {
                                                profileViewBinding.textViewCurrentLocation.setText(getUserProfileData.getLocation_name() + " . " + getUserProfileData.getDistance() + " mi");

                                            }
                                        }
                                    }
                                }
                            }

                            if (getUserProfileData.getUser_avatar() != null && !getUserProfileData.getUser_avatar().isEmpty()) {
                                download(getUserProfileData.getUser_avatar(), profileViewBinding.imageViewProfile);
                            }

                            EventBus.getDefault().post(new EventBusAboutUserData(getUserProfileData.getUser_bio(), getUserProfileData.getUser_interests(), getUserProfileData.getUser_fname()));
                            EventBus.getDefault().post(new EventBusNotesUserData(userId, getUserProfileData.getIs_connection(), getUserProfileData.getUser_fname(), getUserProfileData.getNote_on_user(), getUserProfileData.getTask_on_user()));
                            EventBus.getDefault().post(new EventBusUsersHistory(getUserProfileData.getIs_connection(), getUserProfileData.getIs_favorite(), getUserProfileData.getId(), getUserProfileData.getUser_history(), getUserProfileData.getUser_fname()));
                            EventBus.getDefault().post(new EventBusContactsListUserData(getUserProfileData.getUser_phone(), getUserProfileData.getSecondary_phones(), getUserProfileData.getUser_email(), getUserProfileData.getSecondary_emails(), getUserProfileData.getSocial_links(), getUserProfileData.getUser_fname()));
                            profileViewBinding.viewPagify.setVisibility(View.VISIBLE);
                            profileViewBinding.pagerView.setVisibility(View.VISIBLE);
                            break;
                        case 0:
                            profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                            profileViewBinding.shimmerLayoutFullProfile.stopShimmerAnimation();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                profileViewBinding.shimmerLayoutFullProfile.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    public void reqShowAllGroup(String profile_id) {
        userGetGroupData.clear();
        userGetGroupDataNew.clear();
        groupNameList.clear();
        callUserGetGroup = Api.WEB_SERVICE.getUserGroups(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), profile_id);

        callUserGetGroup.enqueue(new Callback<UserGetGroupMainObject>() {
            @Override
            public void onResponse(Call<UserGetGroupMainObject> call, Response<UserGetGroupMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            userGetGroupData = response.body().getGetUserGroup();

                            for (CreateUserGroup userConnectionsData : userGetGroupData) {
                                boolean isFound = false;
                                for (UserConnectionsData usersProfileId : userConnectionsData.getMembers()) {
                                    if (usersProfileId.getProfile_id().equalsIgnoreCase(profileId)) {
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (isFound) {
//                                    profileViewBinding.tagContainer.addTag(userConnectionsData.getGroup_name());
                                    groupNameList.add(userConnectionsData.getGroup_name());
                                    ShowGroupNamesAdapter showGroupNamesAdapter = new ShowGroupNamesAdapter(getContext(), groupNameList);
                                    profileViewBinding.recyclerViewGroupNames.setLayoutManager(new LinearLayoutManager(getContext()));
                                    profileViewBinding.recyclerViewGroupNames.setAdapter(showGroupNamesAdapter);
                                    profileViewBinding.recyclerViewGroupNames.setVisibility(View.VISIBLE);
                                } else {
                                    userGetGroupDataNew.add(userConnectionsData);
                                }

                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserGetGroupMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
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

    public void reqCreateGroup(String profile_id, String group_name, String receiver_id, String receiverProfile_id, String group_id, String type) {
        if (group_id != null && !group_id.isEmpty()) {
            callUserCreateGroup = Api.WEB_SERVICE.createUserGroupWithGroupId(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.ID, ""), profile_id, group_name, receiver_id, receiverProfile_id, group_id);
        } else {
            callUserCreateGroup = Api.WEB_SERVICE.createUserGroup(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.ID, ""), profileId, group_name, receiver_id, receiverProfile_id);
        }

        callUserCreateGroup.enqueue(new Callback<UserContactGroupMainObject>() {
            @Override
            public void onResponse(Call<UserContactGroupMainObject> call, Response<UserContactGroupMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            groupId = response.body().getCreateUserGroup().getId();
                            if (type.equalsIgnoreCase("addGroup")) {
                                for (int i = 0; i < userGetGroupData.size(); i++) {
                                    if (userGetGroupData.get(i).getId().equalsIgnoreCase(group_id)) {
                                        userGetGroupData.get(i).setMembers(response.body().getCreateUserGroup().getMembers());
                                        break;
                                    }
                                }
                            } else {
                                userGetGroupData.add(response.body().getCreateUserGroup());
                            }
                            reqShowAllGroup(senderProfileId);
                            dialogAddToGroup.cancel();
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            slideUp.hide();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            slideUp.hide();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserContactGroupMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
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


    private void reqFavUnFavLnq(final String status, String senderProfileid, String receiverProfileid) {
//        ((MainActivity) getActivity()).progressDialog.show();
//        callFavUnFavLnq = Api.WEB_SERVICE.favUnfavLNQ(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status);
        callFavUnFavLnq = Api.WEB_SERVICE.favUnfavLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileid, receiverProfileid);
        callFavUnFavLnq.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            stopTimer();
                            if (status.equals(Constants.FAVORITE)) {
                                if (getUserProfileData != null) {
                                    getUserProfileData.setIs_favorite(Constants.FAVORITE);
                                    setUserConnectionStatus();
                                    EventBus.getDefault().post(new EventBusUserSession("favorite_user"));
                                    EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.FAVORITE, false));
                                }
                            } else {
                                if (getUserProfileData != null) {
                                    getUserProfileData.setIs_favorite("");
                                    setUserConnectionStatus();
                                    EventBus.getDefault().post(new EventBusUserSession("unfavorite_user"));
                                    EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.UN_FAVORITE, false));
                                }
                            }
//                            if (profileViewBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && profileViewBinding.cardViewPopupProfile.getVisibility() == View.VISIBLE) {
//                                profileViewBinding.imageViewArrowUp.setVisibility(View.GONE);
//                                profileViewBinding.cardViewPopupProfile.setVisibility(View.GONE);
//                            }
                            slideUp.hide();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            slideUp.hide();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    private void reqBlockUnblock(final String status, String senderProfileid, String recevierProfileid) {
//        ((MainActivity) getActivity()).progressDialog.show();
//        callBlackWhiteStatus = Api.WEB_SERVICE.blockUnblockLNQ(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status);
        callBlackWhiteStatus = Api.WEB_SERVICE.blockUnblockLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileid, recevierProfileid);
        callBlackWhiteStatus.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            stopTimer();
                            if (status.equals("block")) {
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.BLOCK, false));
                                EventBus.getDefault().post(new EventBusBlockedUnBlocked("blocked"));
                                EventBus.getDefault().post(new EventBusUserSession("blocked_user"));
                                ((MainActivity) getActivity()).showMessageDialog("success", getUserProfileData.getUser_fname() + " is now in your Black list.");
                            } else {
                                EventBus.getDefault().post(new EventBusBlockedUnBlocked(""));
                                EventBus.getDefault().post(new EventBusUserSession("unblocked_user"));
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "unblock", false));
                                ((MainActivity) getActivity()).showMessageDialog("success", getUserProfileData.getUser_fname() + " is removed from your Black list.");
                            }
//                            if (profileViewBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && profileViewBinding.cardViewPopupProfile.getVisibility() == View.VISIBLE) {
//                                profileViewBinding.imageViewArrowUp.setVisibility(View.GONE);
//                                profileViewBinding.cardViewPopupProfile.setVisibility(View.GONE);
//                            }
                            slideUp.hide();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            slideUp.hide();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    private void logActivity() {
//        callLogActivity = Api.WEB_SERVICE.logActivity(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, "profile_viewed");
        callLogActivity = Api.WEB_SERVICE.logActivity(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, "profile_viewed");
        callLogActivity.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    private void reqHideShowLocation(final String status, String senderProfileid, String recevierProfileid) {
//        ((MainActivity) getActivity()).progressDialog.show();
//        callShowHideLocation = Api.WEB_SERVICE.hideShowLocation(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status);
        callShowHideLocation = Api.WEB_SERVICE.hideShowLocation(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileid, recevierProfileid);
        callShowHideLocation.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (status.equals(Constants.HIDE)) {
                                EventBus.getDefault().post(new EventBusUserSession("location_hidden"));

                                ((MainActivity) getActivity()).showMessageDialog("success", "Location hidden from " + getUserProfileData.getUser_fname());
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.LOCATION_HIDE, false));
                            } else {
                                EventBus.getDefault().post(new EventBusUserSession("location_shown"));
                                ((MainActivity) getActivity()).showMessageDialog("success", "Location shown to " + getUserProfileData.getUser_fname());
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.LOCATION_SHOW, false));
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    private void reqContactRequest(final String userId, String userFrom, String senderProfileid, String recevierProfileId) {
//        ((MainActivity) getActivity()).progressDialog.show();
//        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, userFrom);
        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, userFrom, senderProfileid, recevierProfileId);
        callRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            getUserProfileData.setIs_connection(Constants.CONTACTED);
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_sent"));
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.CONTACTED, false));
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    private void reqAcceptContactRequest(final String userId, String senderProfileid, String receiverProfileid) {
//        ((MainActivity) getActivity()).progressDialog.show();
//        callAcceptRequest = Api.WEB_SERVICE.contactRequestAccpet(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        callAcceptRequest = Api.WEB_SERVICE.contactRequestAccpet(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""), senderProfileid, receiverProfileid);
        callAcceptRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", "Request accepted successfully.");
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "connected", false));
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_accepted"));
//                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    private void reqDeclineContactRequest(final String userId, String senderProfileid, String recevierProfileId) {
//        ((MainActivity) getActivity()).progressDialog.show();
//        callDeclineRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        callDeclineRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""), senderProfileid, recevierProfileId);
        callDeclineRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", "LNQ request declined successfully.");
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "cancel", false));
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_declined"));
//                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    private void exportTheDB() throws IOException {
        File myFile;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String TimeStampDB = sdf.format(cal.getTime());

        myFile = new File(Environment.getExternalStorageDirectory() + "/Export_" + TimeStampDB + ".csv");
        myFile.createNewFile();
        FileOutputStream fOut = new FileOutputStream(myFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//        myOutWriter.append("Name;Phone Number;Email;Birthday;Job Title;Address;Note");
        myOutWriter.append("Name");
        myOutWriter.append(',');
        myOutWriter.append("Phone Number");
        myOutWriter.append(',');
        myOutWriter.append("Email");
        myOutWriter.append(',');
        myOutWriter.append("Birthday");
        myOutWriter.append(',');
        myOutWriter.append("Job Title");
        myOutWriter.append(',');
        myOutWriter.append("Address");
        myOutWriter.append(',');
        myOutWriter.append("Note");
        myOutWriter.append(',');
        myOutWriter.append("\n");

        if (selectedExportContactsList != null && selectedExportContactsList.size() > 0) {
            for (SelectedExportContact connectionsData : selectedExportContactsList) {
                String name = connectionsData.getName();
                String phoneNumber = connectionsData.getNumber().get(0);
                String email = connectionsData.getEmail().size() > 0 ? connectionsData.getEmail().get(0) : "";
                String birthday = connectionsData.getBirthday();
                String jobTitle = connectionsData.getJob();
                String address = connectionsData.getAddress();
                String note = connectionsData.getNote();

                myOutWriter.append(name);
                myOutWriter.append(',');
                myOutWriter.append(phoneNumber);
                myOutWriter.append(',');
                myOutWriter.append(email);
                myOutWriter.append(',');
                myOutWriter.append(birthday.replaceAll(",", " "));
                myOutWriter.append(',');
                myOutWriter.append(jobTitle);
                myOutWriter.append(',');
                myOutWriter.append(address.replaceAll(",", " "));
                myOutWriter.append(',');
                if (note != null && !note.isEmpty()) {
                    note = "Notes exported from LNQ on " + DateUtils.getDateForContactNote() + " " + note;
                    myOutWriter.append(note.replaceAll(",", " "));
                } else {
                    myOutWriter.append(note);
                }
                myOutWriter.append(',');
                myOutWriter.append("\n");
            }
            myOutWriter.close();
            fOut.close();
            exportCSV(myFile);
        }

    }

    public void exportCSV(File file) {
//        ((MainActivity) getActivity()).progressDialog.show();
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("csv", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file));
        exportCSVModelCall = Api.WEB_SERVICE.exportcsv(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                filePart,
                id);
        exportCSVModelCall.enqueue(new Callback<ExportCSVModel>() {
            @Override
            public void onResponse(Call<ExportCSVModel> call, Response<ExportCSVModel> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body() != null) {
                                ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported this contact as CSV.Please check your email.");
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ExportCSVModel> call, Throwable error) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
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

    class GetAllContactsTask extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            if (getActivity() != null) {
                PhoneContact phoneContact = new PhoneContact(getActivity());
                return phoneContact.getAllContacts();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);
            if (contacts != null) {
                if (contacts.size() > 0) {
                    getAllContacts(contacts);
                }
            }
        }
    }

    private void getAllContacts(List<Contact> contactList) {
        phoneContactsModelList.clear();
        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            HashMap<Integer, String> hashMapPhoneNumbers = contact.getPhones();
            HashMap<Integer, String> hashMapEmails = contact.getEmails();
            List<String> phoneList = new ArrayList<>();
            List<String> emailList = new ArrayList<>();
            if (hashMapPhoneNumbers != null) {
                for (Map.Entry<Integer, String> phoneEntry : hashMapPhoneNumbers.entrySet()) {
                    int key = phoneEntry.getKey();
                    String value = phoneEntry.getValue();
                    try {
                        if (!phoneNumberUtil.isValidNumber(phoneNumberUtil.parseAndKeepRawInput(value, Constants.DEFAULT_REGION))) {
                            value = phoneNumberUtil.format(phoneNumberUtil.parseAndKeepRawInput(value, countryRegion.toUpperCase()), PhoneNumberUtil.PhoneNumberFormat.E164);
                            hashMapPhoneNumbers.put(key, value);
                        }
                    } catch (NumberParseException e) {

                    }
                }
                for (Map.Entry<Integer, String> phoneEntry : hashMapPhoneNumbers.entrySet()) {
                    phoneList.add(phoneEntry.getValue());
                }
                if (hashMapEmails != null) {
                    for (Map.Entry<Integer, String> emailEntry : hashMapEmails.entrySet()) {
                        emailList.add(emailEntry.getValue());
                    }
                }
                phoneContactsModelList.add(new PhoneContactsModel(contact.getId(), contact.getDisplayName(), phoneList, emailList, contact.getImage(), false));
            }
        }
        tempSelectedExportContactsList.clear();
        tempSelectedExportContactsList.addAll(selectedExportContactsList);
        for (SelectedExportContact selectedExportContact : selectedExportContactsList) {
            int foundCounter = 0;
            for (PhoneContactsModel phoneContactsModel : phoneContactsModelList) {
                for (String phone : phoneContactsModel.getPhoneNumber()) {
                    for (String addedPhone : selectedExportContact.getNumber()) {
                        if (phone.equalsIgnoreCase(addedPhone)) {
                            foundedContactModel = phoneContactsModel;
                            contactFoundIn = "phone";
                            foundCounter++;
                        }
                    }
                }
                if (foundCounter == 0) {
                    for (String email : phoneContactsModel.getEmail()) {
                        for (String addedEmail : selectedExportContact.getEmail()) {
                            if (email.equalsIgnoreCase(addedEmail)) {
                                foundedContactModel = phoneContactsModel;
                                contactFoundIn = "email";
                                foundCounter++;
                            }
                        }
                    }
                }
            }
            if (foundCounter > 0) {
                this.selectedExportContact = selectedExportContact;
                selectedExportContactsList.remove(selectedExportContact);
            }
        }
        if (selectedExportContactsList.size() > 0) {
            for (SelectedExportContact selectedExportContact : selectedExportContactsList) {
                AddToContactsTask addToContactsTask = new AddToContactsTask();
                addToContactsTask.execute(selectedExportContact);
            }
        } else {
//            ((MainActivity) getActivity()).progressDialog.dismiss();
            showSameContactFoundDialog();
        }
    }

    private void showSameContactFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.cus_dialog_exportcontactsmatch, null);
        TextView textViewDescription = dialogView.findViewById(R.id.replaceContactDescription);
        Button textNo = dialogView.findViewById(R.id.textViewNo);
        Button textViewReplace = dialogView.findViewById(R.id.textViewReplace);
        TextView textViewNameLnqContact = dialogView.findViewById(R.id.textViewNameLnqContact);
        TextView textViewPhoneLnqContact = dialogView.findViewById(R.id.textViewPhoneLnqContact);
        TextView textViewEmailLnqContact = dialogView.findViewById(R.id.textViewEmailLnqContact);
        TextView textViewNamePhoneContact = dialogView.findViewById(R.id.textViewNamePhoneContact);
        TextView textViewNumberPhoneContact = dialogView.findViewById(R.id.textViewNumberPhoneContact);
        TextView textViewEmailPhoneContact = dialogView.findViewById(R.id.textViewEmailPhoneContact);
        AppCompatImageView imageViewProfilePhoneContact = dialogView.findViewById(R.id.imageViewProfilePhoneContact);
        AppCompatImageView imageViewProfileLnqContact = dialogView.findViewById(R.id.imageViewProfileLnqContact);
        ValidUtils.buttonGradientColor(textNo);


        if (selectedExportContact != null) {
//            Glide.with(getActivity())
//                    .load(getUserProfileData.getUser_avatar())
//                    .apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                    .apply(new RequestOptions().circleCrop())
//                    .into(imageViewProfileLnqContact);
            if (getUserProfileData.getUser_avatar() != null && !getUserProfileData.getUser_avatar().isEmpty()) {
                download(getUserProfileData.getUser_avatar(), imageViewProfileLnqContact);
            }
            textViewNameLnqContact.setText(selectedExportContact.getName());
            textViewPhoneLnqContact.setText(getUserProfileData.getUser_phone());
            textViewEmailLnqContact.setText(getUserProfileData.getUser_email());

//            Glide.with(getActivity())
//                    .load(getUserProfileData.getUser_avatar())
//                    .apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                    .apply(new RequestOptions().circleCrop())
//                    .into(imageViewProfilePhoneContact);
            if (getUserProfileData.getUser_avatar() != null && !getUserProfileData.getUser_avatar().isEmpty()) {
                download(getUserProfileData.getUser_avatar(), imageViewProfilePhoneContact);
            }
            textViewNamePhoneContact.setText(foundedContactModel.getName());
            textViewNumberPhoneContact.setText(foundedContactModel.getPhoneNumber().size() > 0 ? foundedContactModel.getPhoneNumber().get(0) : "");
            textViewEmailPhoneContact.setText(foundedContactModel.getEmail().size() > 0 ? foundedContactModel.getEmail().get(0) : "");
//            textViewDescription.setText("A contact\nName: " + selectedExportContact.getName() + "\nmatched with a contact with\n" + (contactFoundIn.equals("phone") ? "Phone: " : "Email: ") + (contactFoundIn.equals("phone") ? getUserProfileData.getUser_phone() : getUserProfileData.getUser_email()) + "\n Would you like to replace that contact?");
            textViewDescription.setText("This LNQ contact matched with this phone \ncontact. Would you like to replace it?");
        }

        textViewReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteContactTask deleteContactTask = new DeleteContactTask();
                deleteContactTask.execute(selectedExportContact);
                dialog.dismiss();
            }
        });

        textNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteContactTask deleteContactTask = new DeleteContactTask();
                deleteContactTask.execute(selectedExportContact);
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

    class DeleteContactTask extends AsyncTask<SelectedExportContact, Void, SelectedExportContact> {

        @Override
        protected SelectedExportContact doInBackground(SelectedExportContact... selectedExportContacts) {
            final ArrayList ops = new ArrayList();
            final ContentResolver cr = getActivity().getContentResolver();
            ops.add(ContentProviderOperation
                    .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?",
                            new String[]{foundedContactModel.getId()})
                    .build());
            try {
                cr.applyBatch(ContactsContract.AUTHORITY, ops);
                ops.clear();
            } catch (OperationApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (RemoteException e) {
                // System.out.println(" length :"+i);
            }
            return selectedExportContacts[0];
        }

        @Override
        protected void onPostExecute(SelectedExportContact selectedExportContact) {
            super.onPostExecute(selectedExportContact);
            AddToContactsTask addToContactsTask = new AddToContactsTask();
            addToContactsTask.execute(selectedExportContact);
        }
    }

    class AddToContactsTask extends AsyncTask<SelectedExportContact, Void, SelectedExportContact> {

        @Override
        protected SelectedExportContact doInBackground(SelectedExportContact... contactsEntities) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            String name = contactsEntities[0].getName();
            List<String> number = contactsEntities[0].getNumber();
            List<String> email = contactsEntities[0].getEmail();
            String note = contactsEntities[0].getNote();
            String birthday = contactsEntities[0].getBirthday();
            String address = contactsEntities[0].getAddress();
            String company = contactsEntities[0].getCompany();
            String job = contactsEntities[0].getJob();
            String image = contactsEntities[0].getImage();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            if (name != null && !name.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                name).build());
            }
            if (number != null && number.size() > 0) {
                for (String num : number) {
                    if (!num.isEmpty()) {
                        ops.add(ContentProviderOperation.
                                newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, num)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .build());
                    }
                }
            }
            if (email != null && email.size() > 0) {
                for (String eml : email) {
                    if (eml != null && !eml.isEmpty()) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, eml)
                                .build());
                    }
                }
            }
            if (note != null && !note.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, "Notes exported from LNQ on " + DateUtils.getDateForContactNote() + "\n" + note)
                        .build());
            }

            if (birthday != null && !birthday.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, birthday)
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                        .build());
            }
            if (company != null && !company.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, job)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .build());
            }
            if (address != null && !address.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                        .build());
            }
            Glide.with(getActivity())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            if (resource != null) {    // If an image is selected successfully
                                resource.compress(Bitmap.CompressFormat.PNG, 75, stream);

                                // Adding insert operation to operations list
                                // to insert Photo in the table ContactsContract.Data
                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                                        .build());

                                try {
                                    stream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (getActivity() != null)
                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {

            }
            return contactsEntities[0];
        }

        @Override
        protected void onPostExecute(SelectedExportContact selectedExportContact) {
            super.onPostExecute(selectedExportContact);
            if (getActivity() != null) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported this contact to phone contacts");
            }
        }
    }

    private void exportContactsToSalesforce(String salesforceUrl, String accessToken) {
        if (selectedExportContactsList.size() > 0) {
//            ((MainActivity) getActivity()).progressDialog.show();
            List<SalesforceModel> salesforceModelList = new ArrayList<>();

            for (int i = 0; i < selectedExportContactsList.size(); i++) {
                SelectedExportContact selectedExportContact = selectedExportContactsList.get(i);
                String name = selectedExportContact.getName();
                String lastName = "";
                String firstName = "";
                if (name.split("\\w+").length > 1) {

                    lastName = name.substring(name.lastIndexOf(" ") + 1);
                    firstName = name.substring(0, name.lastIndexOf(' '));
                    if (lastName.equals("")) {
                        lastName = firstName;
                    }
                } else {
                    firstName = name;
                }
                salesforceModelList.add(new SalesforceModel(firstName, lastName, selectedExportContact.getNumber().size() > 0 ? selectedExportContact.getNumber().get(0) : "", selectedExportContact.getEmail().size() > 0 ? selectedExportContact.getEmail().get(0) : "", new SalesforceAttributes("Contact", "ref" + i)));
            }
            if (salesforceModelList.size() > 0) {
                SalesforceContactModel salesforceContactModel = new SalesforceContactModel(salesforceModelList);
                Gson gson = new Gson();
                String json = gson.toJson(salesforceContactModel);
                AndroidNetworking.post(salesforceUrl + "/services/data/v34.0/composite/tree/Contact/")
                        .addHeaders("Authorization", "Bearer " + accessToken)
                        .setContentType("application/json")
                        .addStringBody(json)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
//                                ((MainActivity) getActivity()).progressDialog.dismiss();
                                if (response != null) {
                                    ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported this contact to SalesForce.");
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
//                                ((MainActivity) getActivity()).progressDialog.dismiss();
                                ValidUtils.showToast(getActivity(), anError.getMessage());
                            }
                        });
            }
        }
    }

}