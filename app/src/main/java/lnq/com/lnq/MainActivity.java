package lnq.com.lnq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.AbdAllahAbdElFattah13.linkedinsdk.ui.LinkedInUser;
import com.AbdAllahAbdElFattah13.linkedinsdk.ui.linkedin_builder.LinkedInBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.giphy.sdk.ui.Giphy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.people.v1.PeopleScopes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.databinding.FragmentFrgamentMapChangePopUppBinding;
import lnq.com.lnq.databinding.MainActivityBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.firebase.SwipeAppService;
import lnq.com.lnq.fragments.activity.FragmentActivity;
import lnq.com.lnq.fragments.chat.FragmentChat;
import lnq.com.lnq.fragments.chat.FragmentConversation;
import lnq.com.lnq.fragments.chat.FragmentGroupChat;
import lnq.com.lnq.fragments.chat.NewMessageFragment;
import lnq.com.lnq.fragments.connections.FragmentConnections;
import lnq.com.lnq.fragments.connections.FragmentLnqCounts;
import lnq.com.lnq.fragments.connections.export_contacts.FragmentExportContacts;
import lnq.com.lnq.fragments.connections.export_contacts.FragmentExportUsers;
import lnq.com.lnq.fragments.connections.import_contacts.EventBusGoogleSignInAccount;
import lnq.com.lnq.fragments.connections.import_contacts.FragmentImportContacts;
import lnq.com.lnq.fragments.connections.import_contacts.FragmentImportUserPopUp;
import lnq.com.lnq.fragments.connections.import_contacts.FragmentImportUsers;
import lnq.com.lnq.fragments.connections.salesforce.Login_Salesforce;
import lnq.com.lnq.fragments.filters.ExportContactFilter;
import lnq.com.lnq.fragments.filters.FragmentActivityFilters;
import lnq.com.lnq.fragments.filters.FragmentConnectionFilter;
import lnq.com.lnq.fragments.filters.FragmentConversationFilter;
import lnq.com.lnq.fragments.filters.FragmentHomeMapFilter;
import lnq.com.lnq.fragments.fullprofileview.FragmentEditNote;
import lnq.com.lnq.fragments.fullprofileview.FragmentEditTask;
import lnq.com.lnq.fragments.fullprofileview.FragmentLnqUserProfileView;
import lnq.com.lnq.fragments.fullprofileview.FragmentUnLnqUserProfileView;
import lnq.com.lnq.fragments.gallery.GalleryActivity;
import lnq.com.lnq.fragments.gallery.GalleryFragmentNew;
import lnq.com.lnq.fragments.home.FragmentHome;
import lnq.com.lnq.fragments.home.FragmentHomeGrid;
import lnq.com.lnq.fragments.home.FragmentHomeMapProfile;
import lnq.com.lnq.fragments.home.FragmentHomeMapProfilePopupMenu;
import lnq.com.lnq.fragments.home.FragmentHomeMapProfilePopupNew;
import lnq.com.lnq.fragments.home.FrgamentMapChangePopUpp;
import lnq.com.lnq.fragments.idverification.FragIdentificationVerify;
import lnq.com.lnq.fragments.idverification.FragIdentityVerifySuccess;
import lnq.com.lnq.fragments.lnqrequest.FragmentAcceptPopUp;
import lnq.com.lnq.fragments.lnqrequest.FragmentAcceptRequest;
import lnq.com.lnq.fragments.lnqrequest.FragmentLnqSendPopUp;
import lnq.com.lnq.fragments.lnqrequest.FragmentRetractPopUP;
import lnq.com.lnq.fragments.lnqrequest.FragmentRetractRequest;
import lnq.com.lnq.fragments.lnqrequest.FragmentSendRequest;
import lnq.com.lnq.fragments.lnqrequest.FragmentUnLnqPopUp;
import lnq.com.lnq.fragments.lnqrequest.FragmentUnLnqUser;
import lnq.com.lnq.fragments.onboardtutorials.FragmentTutorialBase;
import lnq.com.lnq.fragments.profile.FragmentProfile;
import lnq.com.lnq.fragments.profile.createmultipleprofiles.FragmentCreateProfileInfo;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditBio;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditContactInfo;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditContactInfoEmail;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditProfile;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditSocialLinks;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditTags;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditTagsPopUp;
import lnq.com.lnq.fragments.profile.editprofile.FragmentVerifySecondaryPhone;
import lnq.com.lnq.fragments.profile.editprofile.FragmentWorkHistory;
import lnq.com.lnq.fragments.qrcode.FragMyCode;
import lnq.com.lnq.fragments.qrcode.FragScanCode;
import lnq.com.lnq.fragments.qrcode.FragShareQrCode;
import lnq.com.lnq.fragments.qrcode.FragmentInviteSendRequest;
import lnq.com.lnq.fragments.qrcode.FragmentOCRImagePath;
import lnq.com.lnq.fragments.qrcode.FragmentOptionalNotes;
import lnq.com.lnq.fragments.qrcode.FragmentRecipientInfo;
import lnq.com.lnq.fragments.registeration.FragmentEmailSent;
import lnq.com.lnq.fragments.registeration.FragmentEmailVerification;
import lnq.com.lnq.fragments.registeration.FragmentLogin;
import lnq.com.lnq.fragments.registeration.FragmentSignUp;
import lnq.com.lnq.fragments.registeration.FragmentSignUpTermsConditions;
import lnq.com.lnq.fragments.registeration.FragmentWelcomeLnq;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentCreateProfile;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentCreateProfilePicture;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentProfileLooksGood;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentProfileRequired;
import lnq.com.lnq.fragments.registeration.forgotemail.FragmentForgotEmail;
import lnq.com.lnq.fragments.registeration.forgotpassword.FragmentForgotPassword;
import lnq.com.lnq.fragments.registeration.phoneverification.FragmentPhoneVerificationEnterCode;
import lnq.com.lnq.fragments.registeration.phoneverification.FragmentPhoneVerificationSendCode;
import lnq.com.lnq.fragments.registeration.profileverification.FragmentProfileVerified;
import lnq.com.lnq.fragments.registeration.profileverification.FragmentProfileVerifyOption;
import lnq.com.lnq.fragments.registeration.profileverification.FragmentProfileVerifyPicture;
import lnq.com.lnq.fragments.registeration.profileverification.FragmentProfileVerifyTakePhoto;
import lnq.com.lnq.fragments.setting.FragmentDefaultSetting;
import lnq.com.lnq.fragments.setting.FragmentSetting;
import lnq.com.lnq.fragments.setting.SignOutPopUp;
import lnq.com.lnq.fragments.setting.account.FragmentSyncContacts;
import lnq.com.lnq.fragments.setting.account.FragmentVisibilitySetting;
import lnq.com.lnq.fragments.setting.account.FragmentYourProfile;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentAccountSetting;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentDeleteAccountPopUp;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentEditEmail;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentEditPassword;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentEditPhoneNumber;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentEnterEditNumber;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentFreezeAccountPopUp;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentRequestYourDataPopUp;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentSuccessPassword;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentUpdateNumberVerifyCode;
import lnq.com.lnq.fragments.setting.account.blockusers.FragmentBlockedUsers;
import lnq.com.lnq.fragments.setting.account.blockusers.FragmentBlockedUsersSetting;
import lnq.com.lnq.fragments.setting.general.FragmentCommunityGuidLine;
import lnq.com.lnq.fragments.setting.general.FragmentContactUs;
import lnq.com.lnq.fragments.setting.general.FragmentFAQs;
import lnq.com.lnq.fragments.setting.general.FragmentPrivacy;
import lnq.com.lnq.fragments.setting.general.FragmentTermsAndConditions;
import lnq.com.lnq.fragments.setting.general.FragmentVisibilityPolicy;
import lnq.com.lnq.fragments.splash.FragmentSplash;
import lnq.com.lnq.fragments.splash.Fragment_VersionDetails;
import lnq.com.lnq.fragments.status.FragmentUpdateStatus;
import lnq.com.lnq.fragments.visibility.FragmentVisibilityStatus;
import lnq.com.lnq.model.event_bus_models.EventBusActivityCount;
import lnq.com.lnq.model.event_bus_models.EventBusCameraPermission;
import lnq.com.lnq.model.event_bus_models.EventBusCaptureImage;
import lnq.com.lnq.model.event_bus_models.EventBusContactPermission;
import lnq.com.lnq.model.event_bus_models.EventBusContactPermissionProfile;
import lnq.com.lnq.model.event_bus_models.EventBusFacebookData;
import lnq.com.lnq.model.event_bus_models.EventBusGalleryPermission;
import lnq.com.lnq.model.event_bus_models.EventBusGetChatImagePath;
import lnq.com.lnq.model.event_bus_models.EventBusIsFragmentVisible;
import lnq.com.lnq.model.event_bus_models.EventBusLinkedInData;
import lnq.com.lnq.model.event_bus_models.EventBusLocationPermission;
import lnq.com.lnq.model.event_bus_models.EventBusLogOut;
import lnq.com.lnq.model.event_bus_models.EventBusLoginKeyboardUnregister;
import lnq.com.lnq.model.event_bus_models.EventBusMapLocationUpdate;
import lnq.com.lnq.model.event_bus_models.EventBusMapView;
import lnq.com.lnq.model.event_bus_models.EventBusOpenCameraSecondaryProfile;
import lnq.com.lnq.model.event_bus_models.EventBusShowScanScreen;
import lnq.com.lnq.model.event_bus_models.EventBusTotalCount;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChat;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatCount;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatNew;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.LogOut;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.ChatThread;
import lnq.com.lnq.model.gson_converter_models.conversation.GetChatThread;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserSecondaryProfile;
import lnq.com.lnq.model.gson_converter_models.pushnotifications.PushNotificationMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.LogInData;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    public ArraySet<String> listImagePaths = new ArraySet<>();
    final int RC_INTENT = 200;
    final int RC_API_CHECK = 100;

    //    Android fields....
    private Context context;
    public MainActivityBinding mBind;
    public ProgressDialog progressDialog;
    int badgeCount = 0;

    //    Fragment manager fields....
    private Fragment mFrag;
    private Class fragmentClass;
    public FragmentManager fragmentManager;

    //    Location fields....
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    //    Retrofit Call....
    private Call<ChatThread> chatThreadCall;
    private Call<RegisterLoginMainObject> sessionCall;
    private Call<RegisterLoginMainObject> callLogIn;
    private Call<PushNotificationMainObject> callNotification;
    private Call<LogOut> callLogOut;
    private Unregistrar unregister;
    private Response<RegisterLoginMainObject> response;
    private String password;

    //    Instance fields...
    List<GetChatThread> chatThreadList = new ArrayList<>();
    private CallbackManager mCallbackManager;
    public boolean isFromImport;
    String mapView;
    String groupChatThreadId;

    //    Instance fields....
    public String mFScreenName = "";
    public String profileId;
    public boolean sendLocationName = true;
    int curBrightnessValue;
    WindowManager.LayoutParams layout;
    private String lastLoginId, lastLoginEmail, lastLoginPassword;

    //    DataBase
    private MultiProfileRepositry multiProfileRepositry;

    //    Google api fields....
    GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient googleSignInClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.main_activity);
        context = this;

        multiProfileRepositry = new MultiProfileRepositry(this);

        EventBus.getDefault().post(new EventBusUserSession("app_launch"));
        mapView = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.MAPVIEW, "");
        layout = getWindow().getAttributes();
        curBrightnessValue = LnqApplication.getInstance().sharedPreferences.getInt("currentBrightness", 0);
        Uri uri = getIntent().getData();
        if (uri != null) {
            String userData = uri.toString();
            List<String> params = new ArrayList<>();
            List<String> params1 = new ArrayList<>();
            List<String> params2 = new ArrayList<>();
            List<String> params3 = new ArrayList<>();
            List<String> params4 = new ArrayList<>();
            params = Arrays.asList(userData.split("/"));
            params1 = Arrays.asList(params.get(5).split("\\?+"));
            params2 = Arrays.asList(params1.get(1).split("&"));
            LocalDateTime date = LocalDateTime.now();
            int seconds = date.toLocalTime().toSecondOfDay();
            params3 = Arrays.asList(params2.get(1).split("="));
            params4 = Arrays.asList(params2.get(2).split("="));
            String email = params3.get(1);
            String password = params4.get(1);
            if (seconds > Integer.parseInt(params2.get(0))) {
                ValidUtils.showCustomToast(this, "You are using expired link");
            } else {
                lastLoginId = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
                lastLoginEmail = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "");
                lastLoginPassword = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "");
                String loginInfo = LnqApplication.getInstance().sharedPreferences.getString("looged", "");
                if (loginInfo.isEmpty()) {
                    reqLogIn(email, password);
                }
            }
        }
        init();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("notification_type", "").equalsIgnoreCase("")) {
                if (!LnqApplication.getInstance().isFirstTime) {
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    Bundle bundle = new Bundle();
                    if (getIntent() != null) {
                        bundle.putBoolean(EndpointKeys.OPEN_LOGIN, getIntent().getBooleanExtra(EndpointKeys.OPEN_LOGIN, false));
                    }
                    fnLoadFragReplace(Constants.SPLASH, false, bundle);
                    LnqApplication.getInstance().isFirstTime = true;
                } else {
                    checkUserVerificationStatus();
                }
            } else {
                getNotificationBundle();
            }
        } else {
            if (!LnqApplication.getInstance().isFirstTime) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                Bundle bundle = new Bundle();
                if (getIntent() != null) {
                    bundle.putBoolean(EndpointKeys.OPEN_LOGIN, getIntent().getBooleanExtra(EndpointKeys.OPEN_LOGIN, false));
                }
                fnLoadFragReplace(Constants.SPLASH, false, bundle);
                LnqApplication.getInstance().isFirstTime = true;
            } else {
                checkUserVerificationStatus();
            }
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                LnqApplication.getInstance().editor.putString(EndpointKeys.FCM_TOKEN, deviceToken).apply();
            }
        });

        //facebook login process
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        loginResult.getAccessToken().getToken();
                        loginResult.getAccessToken().getUserId();
                        loginResult.getAccessToken().getApplicationId();
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject mJsonObj, GraphResponse response) {
                                        progressDialog.cancel();
                                        try {
                                            String id = mJsonObj.getString("id");
                                            EventBus.getDefault().post(new EventBusFacebookData(("http://www.facebook.com/" + id)));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name,last_name, email, gender,birthday,picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();
                        progressDialog.show();
                    }

                    @Override
                    public void onCancel() {
                        progressDialog.cancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        progressDialog.cancel();
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                }, 1000);
                            }
                        }
                    }
                }
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        String userData= uri.toString();
        List<String> params = new ArrayList<>();
        List<String> params1 = new ArrayList<>();
        List<String> params2 = new ArrayList<>();
        List<String> params3 = new ArrayList<>();
        List<String> params4 = new ArrayList<>();
        params = Arrays.asList(userData.split("/"));
        params1 = Arrays.asList(params.get(5).split("\\?+"));
        params2 = Arrays.asList(params1.get(1).split("&"));
        LocalDateTime date = LocalDateTime.now();
        int seconds = date.toLocalTime().toSecondOfDay();
        params3 = Arrays.asList(params2.get(1).split("="));
        params4 = Arrays.asList(params2.get(2).split("="));
        String email = params3.get(1);
        String password = params4.get(1);
        if (seconds > Integer.parseInt(params2.get(0))){
            ValidUtils.showCustomToast(this, "You are using expired link");
        }else {
            lastLoginId = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
            lastLoginEmail = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "");
            lastLoginPassword = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "");
            String loginInfo = LnqApplication.getInstance().sharedPreferences.getString("looged", "");
            if (loginInfo.isEmpty()) {
                reqLogIn(email, password);
            }
        }
    }

    //     Method to log the user in....
    private void reqLogIn(String email, final String password) {
        callLogIn = Api.WEB_SERVICE.loginMagicLink(EndpointKeys.X_API_KEY, Credentials.basic(email, ValidUtils.md5(password)), email, password, "1");
//        callLogIn = Api.WEB_SERVICE.login(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(email, ValidUtils.md5(password)), email, password);
        callLogIn.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (unregister != null) {
                                unregister.unregister();
                            }
                            LnqApplication.getInstance().editor.putString("looged", "11").apply();
                            multiProfileRepositry.deleteAllProfiles();
                            MainActivity.this.password = password;
                            MainActivity.this.response = response;
                            LogInData logIn = response.body().getLogin();
                            List<CreateUserSecondaryProfile> logInProfilesDataList = response.body().getUser_profiles();
                            if (logIn != null) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (!lastLoginId.isEmpty() && !lastLoginEmail.isEmpty() && !lastLoginPassword.isEmpty()
                                            && lastLoginId.equals(logIn.getUserId()) && lastLoginEmail.equals(logIn.getUserEmail()) && lastLoginPassword.equals(logIn.getUserPassword())
                                    ) {
                                        checkUserLoggedOnOtherDevice(logIn);
                                    }else {
                                        checkUserLoggedOnOtherDevice(logIn);
                                    }
                                } else {
                                    checkUserLoggedOnOtherDevice(logIn);
                                }
                                if (logInProfilesDataList.size() > 0) {
                                    for (int i = 0; i < logInProfilesDataList.size(); i++) {
                                        if (logInProfilesDataList.get(i).getProfile_status().equalsIgnoreCase("active")) {
                                            LnqApplication.getInstance().editor.putString("activeProfile", logInProfilesDataList.get(i).getId());
                                        }
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
                                }
                            }
                            break;
                        case 0:
                            break;
                    }
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_black);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_blue);
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(MainActivity.this, "Poor internet connection");
                    }
                } else {
                    showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }


    private void checkUserLoggedOnOtherDevice(LogInData logIn) {
            saveLoginUserData(logIn, password, response);
    }

    private void saveLoginUserData(LogInData logIn, String password, Response<RegisterLoginMainObject> response) {
        LnqApplication.getInstance().editor.putString(EndpointKeys.ID, logIn.getUserId());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_EMAIL, logIn.getUserEmail());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_TYPE, logIn.getUserType());
        LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_STATUS, logIn.getVerificationStatus());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FNAME, logIn.getUserFirstName());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_LNAME, logIn.getUserLastName());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_AVATAR, logIn.getUserAvatar());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PHONE, logIn.getUserPhone());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIRTHDAY, logIn.getUserBirthday());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIO, logIn.getUserBio());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_ADDRESS, logIn.getUserAddress());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_CURRENT_POSITION, logIn.getUserCurrentPosition());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_COMPANY, logIn.getUserCompany());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_STATUS_MESSAGE, logIn.getUserStatusMessage());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_INTRESTS, logIn.getUser_interests());
        LnqApplication.getInstance().editor.putString(EndpointKeys.PROFILE_CREATED_DATE, logIn.getCreatedAt());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASSWORD, logIn.getUserPassword());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASS, password);
        LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_TO, logIn.getVisibleTo());
        LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_AT, logIn.getVisibleAt());
        LnqApplication.getInstance().editor.putBoolean(EndpointKeys.IS_USER_LOGGED_IN, true);
        LnqApplication.getInstance().editor.putString(EndpointKeys.IS_LOGGEN_IN, logIn.getIsLoggedIn());
        LnqApplication.getInstance().editor.putString(EndpointKeys.IS_FROZEN, logIn.getIs_frozen());
        LnqApplication.getInstance().editor.putString(EndpointKeys.STATUS_DATE, logIn.getStatus_date());
        LnqApplication.getInstance().editor.putString(EndpointKeys.LAST_LOGIN, logIn.getLastLogin());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_EMAILS, logIn.getSecondaryEmail());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_PHONES, logIn.getSecondaryPhones());
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SOCIAL_LINK, logIn.getSocial_links());
        LnqApplication.getInstance().editor.apply();
        EventBus.getDefault().post(new EventBusUserSession("sign_in"));
        reqNotificationsSet(response);
    }

    //    Method to hit api to change notification status....
    private void reqNotificationsSet(final Response<RegisterLoginMainObject> loginResponse) {
//        callNotification = Api.WEB_SERVICE.notificationsSet(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.FCM_TOKEN, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.LAST_LOGIN, ""));
        callNotification = Api.WEB_SERVICE.notificationsSet(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.FCM_TOKEN, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.LAST_LOGIN, ""));
        callNotification.enqueue(new Callback<PushNotificationMainObject>() {
            @Override
            public void onResponse(Call<PushNotificationMainObject> call, Response<PushNotificationMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                        case 0:
                            switch (loginResponse.body().getLogin().getVerificationStatus()) {
                                case EndpointKeys.SIGN_UP:
                                    fnLoadFragReplace(Constants.VERIFICATION_ONE, false, null);
                                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    break;
                                case EndpointKeys.PHONE:
                                    fnLoadFragReplace(Constants.PROFILE_CREATE, false, null);
                                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    break;
                                case EndpointKeys.PROFILE:
                                    fnLoadFragReplace(Constants.PROFILE_PICTURE, false, null);
                                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    break;
                                case EndpointKeys.PROFILE_IMAGE:
                                    LnqApplication.getInstance().editor.putBoolean(EndpointKeys.IS_USER_LOGGED_IN, true);
                                    LnqApplication.getInstance().editor.putBoolean(EndpointKeys.SHOW_NOTIFICATION_DIALOG, false);
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.NOTIFICATION_STATUS, "1");
                                    LnqApplication.getInstance().editor.apply();
                                    fnLoadFragReplace(Constants.HOME, false, null);
                                    break;
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PushNotificationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (MainActivity.this == null)
                    return;
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(MainActivity.this, "Poor internet connection");
                    }
                } else {
                    showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMapView(EventBusMapView eventBusMapView) {
        mapView = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.MAPVIEW, "");
    }

    private void checkUserVerificationStatus() {
        if (LnqApplication.getInstance().sharedPreferences.getBoolean(EndpointKeys.IS_USER_LOGGED_IN, false)) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            switch (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VERIFICATION_STATUS, "")) {
                case EndpointKeys.SIGN_UP:
                    fnLoadFragReplace(Constants.VERIFICATION_ONE, false, null);
                    break;
                case EndpointKeys.FIRSTNAME_LASTNAME:
                    fnLoadFragReplace(Constants.VERIFICATION_ONE, false, null);
                    break;
                case EndpointKeys.PHONE:
                    fnLoadFragReplace(Constants.PROFILE_CREATE, false, null);
                    break;
                case EndpointKeys.PROFILE:
                    fnLoadFragReplace(Constants.PROFILE_PICTURE, false, null);
                    break;
                case EndpointKeys.PROFILE_IMAGE:
                    EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                    fnLoadFragReplace(Constants.HOME, false, null);
                    break;
            }
        } else {
            fnLoadFragReplace(Constants.LOGIN, false, null);
        }
        EventBus.getDefault().post(new EventBusUserSession("app_launch"));
        EventBus.getDefault().post(new EventBusUserSession("app_active"));
    }

    public void getNotificationBundle() {
        Bundle notificationBundle = new Bundle();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("notification_type") != null) {
                if (getIntent().getExtras().getString("notification_type", "").equals("chat")) {
                    notificationBundle.putString("mFlag", "chat");
                    String senderId = getIntent().getExtras().getString("sender_id", "");
                    String senderProfileId = getIntent().getExtras().getString("sender_profile_id", "");
                    notificationBundle.putString(EndpointKeys.THREAD_ID, getIntent().getExtras().getString("thread_id", ""));
                    notificationBundle.putString(EndpointKeys.USER_ID, senderId);
                    notificationBundle.putString(EndpointKeys.PROFILE_ID, senderProfileId);
                    mBind.mImgBack.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    EventBus.getDefault().post(new EventBusUserSession("chat_view"));
                    EventBus.getDefault().post(new EventBusUserSession("msg_recieved"));
                    fnLoadFragAdd(Constants.CHAT_FRAGMENT, false, notificationBundle);
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_blue);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_black);
                } else if (getIntent().getExtras().getString("notification_type", "").equals("group_chat")) {
                    String threadId = getIntent().getExtras().getString("thread_id", "");
                    mBind.mImgBack.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
                    notificationBundle.putString(EndpointKeys.USER_ID, threadId);
                    EventBus.getDefault().post(new EventBusUserSession("group_chat_view"));
                    EventBus.getDefault().post(new EventBusUserSession("msg_recieved"));
                    getChatThread(threadId);
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_blue);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_black);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                } else if (getIntent().getExtras().getString("notification_type", "").equals("lnq_request_accepted")) {
                    notificationBundle.putString("mFlag", "lnq_request_accepted");
                    notificationBundle.putString(EndpointKeys.USER_ID, getIntent().getExtras().getString("sender_id", ""));
                    notificationBundle.putString(EndpointKeys.PROFILE_ID, getIntent().getExtras().getString("sender_profile_id", ""));
                    mBind.mImgBack.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    EventBus.getDefault().post(new EventBusUserSession("contact_view"));
                    fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, false, notificationBundle);
                } else if (getIntent().getExtras().getString("notification_type", "").equals("lnq_request")) {

                    EventBus.getDefault().post(new EventBusUserSession("lnq_requested_received"));

                    notificationBundle.putString("mFlag", "lnq_request");
                    notificationBundle.putString(EndpointKeys.USER_ID, getIntent().getExtras().getString("sender_id", ""));
                    notificationBundle.putString(EndpointKeys.PROFILE_ID, getIntent().getExtras().getString("sender_profile_id", ""));
                    notificationBundle.putString("body", getIntent().getExtras().getString("body"));
                    mBind.mImgBack.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    EventBus.getDefault().post(new EventBusUserSession("contact_view"));
                    fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, false, notificationBundle);
                } else if (getIntent().getExtras().getString("notification_type", "").equals("update_status") ||
                        getIntent().getExtras().getString("notification_type", "").equals("contact_update") ||
                        getIntent().getExtras().getString("notification_type", "").equals("address_update")) {
                    notificationBundle.putString("mFlag", "update_status");
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mViewBgTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    notificationBundle.putString(EndpointKeys.USER_ID, getIntent().getExtras().getString("sender_id", ""));
                    notificationBundle.putString(EndpointKeys.PROFILE_ID, getIntent().getExtras().getString("sender_profile_id", ""));
                    notificationBundle.putString("body", getIntent().getExtras().getString("body"));
                    EventBus.getDefault().post(new EventBusUserSession("contact_view"));
                    fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, false, notificationBundle);
                } else if (getIntent().getExtras().getString("notification_type", "").equalsIgnoreCase("near_you")) {
                    notificationBundle.putString("mFlag", "near_you");
                    notificationBundle.putString(EndpointKeys.USER_ID, getIntent().getExtras().getString("sender_id", ""));
                    notificationBundle.putString(EndpointKeys.PROFILE_ID, getIntent().getExtras().getString("sender_profile_id", ""));
                    EventBus.getDefault().post(new EventBusUserSession("map_view"));
                    fnLoadFragReplace(Constants.HOME, false, notificationBundle);
                }else if (getIntent().getExtras().getString("notification_type", "").equalsIgnoreCase("logout")){
                    EventBus.getDefault().post(new EventBusLogOut());
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void init() {
        profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.CURRENT_FRAGMENT, "").apply();
        Giphy.INSTANCE.configure(this,"xsCX6Jx7W1qQRIlU54D0YMKc80BiclZ5",true,null);
        int activityCount = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.ACTIVITY_COUNT, "0"));
        int chatCount = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.CHAT_COUNT, "0"));
        LnqApplication.getInstance().editor.putString(Constants.TOTAL_COUNT, String.valueOf(activityCount + chatCount)).apply();
        EventBus.getDefault().post(new EventBusTotalCount());

        LnqApplication.getInstance().editor.putString(EndpointKeys.RECEIVER_ID, "");
        LnqApplication.getInstance().editor.apply();
        //        Registering event bus....
        EventBus.getDefault().register(this);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        mBind.mImgBack.setOnClickListener(this);
        mBind.mImgHome.setOnClickListener(this);
        mBind.mImgContacts.setOnClickListener(this);
        mBind.mImgSetting.setOnClickListener(this);
        mBind.mImgAlerts.setOnClickListener(this);
        mBind.mImgMessages.setOnClickListener(this);
        mBind.mImgProfile.setOnClickListener(this);
        mBind.mImgVisible.setOnClickListener(this);
        mBind.mImgLookingFor.setOnClickListener(this);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading . . .");
        progressDialog.setCancelable(false);
    }

    public void progressBarQNewTheme(int visibility) {
//        Glide.with(this)
//                .load(R.drawable.q_loading_blue)
//                .into(mBind.progressQ);
//        mBind.progressQ.setVisibility(visibility);
    }

    @Override
    public void onBackStackChanged() {
        if (fragmentManager.findFragmentById(R.id.main_frag) != null) {
            LnqApplication.getInstance().editor.putString(EndpointKeys.CURRENT_FRAGMENT, "").apply();
            switch (fragmentManager.findFragmentById(R.id.main_frag).getTag()) {
                case Constants.HOME:
                    mBind.mImgBack.setVisibility(View.INVISIBLE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    if (curBrightnessValue != 0) {
                        layout.screenBrightness = curBrightnessValue;
                        getWindow().setAttributes(layout);
                    }
                    break;
                case Constants.MAP_PROFILE:
                    mBind.mImgBack.setVisibility(View.VISIBLE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    break;
                case Constants.CHAT_FRAGMENT:
                    LnqApplication.getInstance().editor.putString(EndpointKeys.CURRENT_FRAGMENT, "chat");
                    LnqApplication.getInstance().editor.apply();
                    mBind.mImgBack.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    break;
                case Constants.GROUP_CHAT_FRAGMENT:
                    LnqApplication.getInstance().editor.putString(EndpointKeys.CURRENT_FRAGMENT, Constants.GROUP_CHAT_FRAGMENT);
                    LnqApplication.getInstance().editor.apply();
                    mBind.mImgBack.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    break;
                case Constants.SHARE_QR_CODE:
                    mBind.mBottomBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mImgBack.setVisibility(View.GONE);
                    break;
                case Constants.PROFILE_FRAGMENT:
                    mBind.mImgBack.setVisibility(View.INVISIBLE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    if (curBrightnessValue != 0) {
                        layout.screenBrightness = curBrightnessValue;
                        getWindow().setAttributes(layout);
                    }
                    break;
                case Constants.CONTACTS:
                case Constants.ALERTS:
                case Constants.HOME_GRID:
                    mBind.mImgBack.setVisibility(View.INVISIBLE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    if (curBrightnessValue != 0) {
                        layout.screenBrightness = curBrightnessValue;
                        getWindow().setAttributes(layout);
                    }
                    break;
                case Constants.LOGIN:
                    EventBus.getDefault().post(new EventBusLoginKeyboardUnregister());
                    break;
                case Constants.MAP_FILTER:
                case Constants.ACTIVITY_FILTER:
                case Constants.CONNECTION_FILTER:
                case Constants.EXPORT_CONTACTS_FILTER:
                case Constants.CONVERSATION_FILTER:
                case Constants.VISIBLE:
                case Constants.DEFAULT_SETTING:
                case Constants.LOOKING_FOR:
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.GONE);
                    break;
                case Constants.SETTING:
                case Constants.ACCOUNT_SETTING:
                case Constants.YOUR_PROFILE:
                case Constants.SYNC_CONTACTS:
                case Constants.BLOCKED_USERS_SETTING:
                case Constants.FAQS:
                case Constants.COMMUNITY_GUIDLINES:
                case Constants.VISIBILITY_POLICY:
                case Constants.PRIVACY_POLICY:
                case Constants.TERMS_AND_CONDITIONS:
                case Constants.CONTACT_US:
                case Constants.EDIT_PHONE:
                case Constants.EDIT_PASSWORD:
                case Constants.EDIT_EMAIL:
                case Constants.ENTER_EDIT_NUMBER:
                case Constants.VISIBILITY_SETTING:
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mImgBack.setVisibility(View.VISIBLE);
                    break;
                case Constants.MESSAGE:
                    EventBus.getDefault().post(new EventBusUpdateChatNew());
//                    mBind.mViewBgBottomBar.setVisibility(View.INVISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mImgBack.setVisibility(View.INVISIBLE);
                    if (curBrightnessValue != 0) {
                        layout.screenBrightness = curBrightnessValue;
                        getWindow().setAttributes(layout);
                    }
                    break;
                case Constants.LNQ_USER:
                case Constants.UNLNQ_USER:
                case Constants.RETRACT_REQUEST:
                case Constants.ACCEPT_REQUEST:
                case Constants.EDIT_TASK:
                case Constants.EDIT_NOTE:
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mViewBgTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                case Constants.PROFILE_VERIFIED_FRAGMENT:
                case Constants.IMPORT_USERS:
                case Constants.EXPORT_USERS:
                case Constants.LNQ_COUNTS:
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    break;
                case Constants.IMPORT_CONTACTS:
                case Constants.EXPORT_CONTACTS:
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mImgBack.setVisibility(View.VISIBLE);
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
                    if (curBrightnessValue != 0) {
                        layout.screenBrightness = curBrightnessValue;
                        getWindow().setAttributes(layout);
                    }
                    break;
                case Constants.POPUP_MAP_NEW:
                case Constants.POPUP_REQUEST_DATA:
                case Constants.POPUP_SIGNOUT:
                case Constants.POPUP_MAP_TYPES:
                case Constants.POPUP_FREEZE_ACCOUNT:
                case Constants.POPUP_DELETE_ACCOUNT:
                case Constants.POPUP_MENU:
                case Constants.POPUP_EDIT_TAGS:
                case Constants.POPUP_IMPORT_USERS:
                case Constants.LNQ_USER_POPUP:
                case Constants.RETRACT_REQUEST_POPUP:
                case Constants.ACCEPT_REQUEST_POPUP:
                case Constants.UNLNQ_POPUP:
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    mBind.mImgBack.setVisibility(View.VISIBLE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    mBind.mTopBar.setVisibility(View.GONE);
                    break;
                case Constants.LNQ_CONTACT_PROFILE_VIEW:
                case Constants.CONTACT_PROFILE_VIEW:
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mViewBgTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    break;
                case Constants.EDIT_PROFILE_FRAGMENT:
                case Constants.CREATE_MULTI_PROFILE_FRAGMENT:
                case Constants.EDIT_TAGS:
                case Constants.EDIT_BIO:
                case Constants.PROFILE_PICTURE:
                case Constants.EDIT_SOCIAL_LINKS:
                case Constants.EDIT_WORK_HISTORY:
                case Constants.VERIFY_CODE:
                case Constants.VERIFY_SECONDARY_CODE:
                case Constants.PROFILE_LOOKS_GOOD:
                    mBind.mViewBgTopBar.setVisibility(View.GONE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.GONE);
                    break;
                case Constants.NEW_MESSAGE:
//                    mBind.mViewBgBottomBar.setVisibility(View.INVISIBLE);
                    mBind.mTopBar.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.VISIBLE);
                    break;
                case Constants.SEND_INVITE_REQUEST:
                    mBind.mViewBgTopBar.setVisibility(View.GONE);
                    mBind.mTopBar.setVisibility(View.GONE);
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    mBind.mBottomBar.setVisibility(View.GONE);
                    break;
                case Constants.EDIT_CONTACT_INFO:
                case Constants.EDIT_CONTACT_INFO_EMAIL:
                    mBind.mTopBar.setVisibility(View.GONE);
                    break;
                case Constants.LOGIN_SALESFORCE:
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusLogOut(EventBusLogOut eventBusLogOut) {
        logOut();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusChatCount(EventBusUpdateChatCount eventBusUpdateChatCount) {
        getChatThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        EventBus.getDefault().unregister(this);
        if (chatThreadCall != null && chatThreadCall.isExecuted()) {
            chatThreadCall.cancel();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImgBack:
                onBackPressed();
                break;
            case R.id.mImgLookingFor:
                if (!mFScreenName.contentEquals(Constants.LOOKING_FOR)) {
                    EventBus.getDefault().post(new EventBusUserSession("status_view"));
                    mFScreenName = Constants.LOOKING_FOR;
                    fnLoadFragAdd(Constants.LOOKING_FOR, true, null);
                }
                break;
            case R.id.mImgSetting:
                if (!mFScreenName.contentEquals(Constants.SETTING)) {
                    EventBus.getDefault().post(new EventBusUserSession("setting_view"));
                    mFScreenName = Constants.SETTING;
                    fnLoadFragAdd(Constants.SETTING, true, null);
                }
                break;
            case R.id.mImgVisible:
                if (!mFScreenName.contentEquals(Constants.VISIBLE)) {
                    if (fnCheckLocationPermission()) {
                        EventBus.getDefault().post(new EventBusUserSession("visibility_view"));
                        mFScreenName = Constants.VISIBLE;
                        fnLoadFragAdd(Constants.VISIBLE, true, null);
                    } else {
                        fnRequestLocationPermission(6);
                    }
                }
                break;
            case R.id.mImgHome:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                if (!mFScreenName.contentEquals(Constants.HOME)) {
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_black);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_blue);
                    EventBus.getDefault().post(new EventBusUserSession("map_view"));
//                    mBind.mViewBgBottomBar.setVisibility(View.GONE);
                    mFScreenName = Constants.HOME;
                    fnLoadFragReplace(Constants.HOME, false, null);
                }
                break;
            case R.id.mImgAlerts:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                if (!mFScreenName.contentEquals(Constants.ALERTS)) {
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_black);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_blue);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_black);
                    EventBus.getDefault().post(new EventBusUserSession("activity_view"));
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    mFScreenName = Constants.ALERTS;
                    fnLoadFragReplace(Constants.ALERTS, false, null);
                }
                break;
            case R.id.mImgProfile:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                if (!mFScreenName.contentEquals(Constants.PROFILE_FRAGMENT)) {
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_black);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_blue);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_black);
                    EventBus.getDefault().post(new EventBusUserSession("profile_view"));
//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    mFScreenName = Constants.PROFILE_FRAGMENT;
                    fnLoadFragReplace(Constants.PROFILE_FRAGMENT, false, null);
                }
                break;
            case R.id.mImgContacts:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                if (!mFScreenName.contentEquals(Constants.CONTACTS)) {
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_black);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_blue);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_black);
                    EventBus.getDefault().post(new EventBusUserSession("contact_view"));

//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    mFScreenName = Constants.CONTACTS;
                    fnLoadFragReplace(Constants.CONTACTS, false, null);
                }
                break;
            case R.id.mImgMessages:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                if (!mFScreenName.contentEquals(Constants.MESSAGE)) {
                    mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_blue);
                    mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    mBind.mImgHome.setImageResource(R.mipmap.map_nc_black);
                    EventBus.getDefault().post(new EventBusUserSession("thread_view"));

//                    mBind.mViewBgBottomBar.setVisibility(View.VISIBLE);
                    mFScreenName = Constants.MESSAGE;
                    fnLoadFragReplace(Constants.MESSAGE, false, null);
                }
                break;
        }
    }

    // function for add fragment
    public void fnLoadFragAdd(String mType, boolean mBackStack, Bundle mBundle) {
        mFrag = null;
        fragmentClass = null;
        switch (mType) {
            case Constants.FORGOT_PASSWORD:
                fragmentClass = FragmentForgotPassword.class;
                break;
            case Constants.TERMS_CONDITION:
                fragmentClass = FragmentSignUpTermsConditions.class;
                break;
            case Constants.PROFILE_PICTURE:
                fragmentClass = FragmentCreateProfilePicture.class;
                break;
            case Constants.VERIFY_PROFILE:
                fragmentClass = FragmentProfileVerifyPicture.class;
                break;
            case Constants.VERIFY_PROFILE_SUCCESS:
                fragmentClass = FragIdentityVerifySuccess.class;
                break;
            case Constants.SIGN_UP:
                fragmentClass = FragmentSignUp.class;
                break;
            case Constants.LOOKING_FOR:
                fragmentClass = FragmentUpdateStatus.class;
                break;
            case Constants.SETTING:
                fragmentClass = FragmentSetting.class;
                break;
            case Constants.VISIBLE:
                fragmentClass = FragmentVisibilityStatus.class;
                break;
            case Constants.EDIT_PROFILE_FRAGMENT:
                fragmentClass = FragmentEditProfile.class;
                break;
            case Constants.EDIT_SOCIAL_LINKS:
                fragmentClass = FragmentEditSocialLinks.class;
                break;
            case Constants.EDIT_WORK_HISTORY:
                fragmentClass = FragmentWorkHistory.class;
                break;
            case Constants.VERIFICATION_TWO:
                fragmentClass = FragmentPhoneVerificationEnterCode.class;
                break;
            case Constants.PROFILE_LOOKS_GOOD:
                fragmentClass = FragmentProfileLooksGood.class;
                break;
            case Constants.PROFILE_VERIFY_OPTIONS:
                fragmentClass = FragmentProfileVerifyOption.class;
                break;
            case Constants.ID_VERIFY:
                fragmentClass = FragIdentificationVerify.class;
                break;
            case Constants.EMAIL_SENT:
                fragmentClass = FragmentEmailSent.class;
                break;
            case Constants.EMAIL_VERIFICATION:
                fragmentClass = FragmentEmailVerification.class;
                break;
            case Constants.FORGOT_EMAIL:
                fragmentClass = FragmentForgotEmail.class;
                break;
            case Constants.PROFILE_REQUIRED:
                fragmentClass = FragmentProfileRequired.class;
                break;
            case Constants.PROFILE_VERIFY_TAKE_PHOTO:
                fragmentClass = FragmentProfileVerifyTakePhoto.class;
                break;
            case Constants.SHARE_QR_CODE:
                fragmentClass = FragShareQrCode.class;
                break;
            case Constants.SCAN_CODE:
                fragmentClass = FragScanCode.class;
                break;
            case Constants.MY_CODE:
                fragmentClass = FragMyCode.class;
                break;
            case Constants.VISIBILITY_SETTING:
                fragmentClass = FragmentVisibilitySetting.class;
                break;
            case Constants.HOME_GRID:
                fragmentClass = FragmentHomeGrid.class;
                break;
            case Constants.MAP_FILTER:
                fragmentClass = FragmentHomeMapFilter.class;
                break;
            case Constants.MAP_PROFILE:
                fragmentClass = FragmentHomeMapProfile.class;
                break;
            case Constants.LNQ_USER:
                fragmentClass = FragmentSendRequest.class;
                break;
            case Constants.UNLNQ_USER:
                fragmentClass = FragmentUnLnqUser.class;
                break;
            case Constants.RETRACT_REQUEST:
                fragmentClass = FragmentRetractRequest.class;
                break;
            case Constants.RETRACT_REQUEST_POPUP:
                fragmentClass = FragmentRetractPopUP.class;
                break;
            case Constants.PROFILE_VERIFIED_FRAGMENT:
                fragmentClass = FragmentProfileVerified.class;
                break;
            case Constants.POPUP_MENU:
                fragmentClass = FragmentHomeMapProfilePopupMenu.class;
                break;
            case Constants.POPUP_MAP_NEW:
                fragmentClass = FragmentHomeMapProfilePopupNew.class;
                break;
            case Constants.POPUP_MAP_TYPES:
                fragmentClass = FrgamentMapChangePopUpp.class;
                break;
            case Constants.POPUP_SIGNOUT:
                fragmentClass = SignOutPopUp.class;
                break;
            case Constants.POPUP_REQUEST_DATA:
                fragmentClass = FragmentRequestYourDataPopUp.class;
                break;
            case Constants.POPUP_FREEZE_ACCOUNT:
                fragmentClass = FragmentFreezeAccountPopUp.class;
                break;
            case Constants.POPUP_DELETE_ACCOUNT:
                fragmentClass = FragmentDeleteAccountPopUp.class;
                break;
            case Constants.POPUP_EDIT_TAGS:
                fragmentClass = FragmentEditTagsPopUp.class;
                break;
            case Constants.POPUP_IMPORT_USERS:
                fragmentClass = FragmentImportUserPopUp.class;
                break;
            case Constants.LNQ_USER_POPUP:
                fragmentClass = FragmentLnqSendPopUp.class;
                break;
            case Constants.IMPORT_USERS:
                fragmentClass = FragmentImportUsers.class;
                break;
            case Constants.EXPORT_USERS:
                fragmentClass = FragmentExportUsers.class;
                break;
            case Constants.LNQ_COUNTS:
                fragmentClass = FragmentLnqCounts.class;
                break;
            case Constants.ACCEPT_REQUEST:
                fragmentClass = FragmentAcceptRequest.class;
                break;
            case Constants.ACCEPT_REQUEST_POPUP:
                fragmentClass = FragmentAcceptPopUp.class;
                break;
            case Constants.UNLNQ_POPUP:
                fragmentClass = FragmentUnLnqPopUp.class;
                break;
            case Constants.CONTACT_PROFILE_VIEW:
                fragmentClass = FragmentUnLnqUserProfileView.class;
                break;
            case Constants.LNQ_CONTACT_PROFILE_VIEW:
                fragmentClass = FragmentLnqUserProfileView.class;
                break;
            case Constants.EDIT_TAGS:
                fragmentClass = FragmentEditTags.class;
                break;
            case Constants.EDIT_BIO:
                fragmentClass = FragmentEditBio.class;
                break;
            case Constants.EDIT_NOTE:
                fragmentClass = FragmentEditNote.class;
                break;
            case Constants.EDIT_TASK:
                fragmentClass = FragmentEditTask.class;
                break;
            case Constants.BLOCKED_USERS_FRAGMENT:
                fragmentClass = FragmentBlockedUsers.class;
                break;
            case Constants.ACTIVITY_FILTER:
                fragmentClass = FragmentActivityFilters.class;
                break;
            case Constants.CHAT_FRAGMENT:
                fragmentClass = FragmentChat.class;
                break;
            case Constants.OPEN_GALLERY_FRGAMENT:
                fragmentClass = GalleryFragmentNew.class;
                break;
            case Constants.GROUP_CHAT_FRAGMENT:
                fragmentClass = FragmentGroupChat.class;
                break;
            case Constants.YOUR_PROFILE:
                fragmentClass = FragmentYourProfile.class;
                break;
            case Constants.SYNC_CONTACTS:
                fragmentClass = FragmentSyncContacts.class;
                break;
            case Constants.BLOCKED_USERS_SETTING:
                fragmentClass = FragmentBlockedUsersSetting.class;
                break;
            case Constants.FAQS:
                fragmentClass = FragmentFAQs.class;
                break;
            case Constants.COMMUNITY_GUIDLINES:
                fragmentClass = FragmentCommunityGuidLine.class;
                break;
            case Constants.VISIBILITY_POLICY:
                fragmentClass = FragmentVisibilityPolicy.class;
                break;
            case Constants.CONNECTION_FILTER:
                fragmentClass = FragmentConnectionFilter.class;
                break;
            case Constants.ACCOUNT_SETTING:
                fragmentClass = FragmentAccountSetting.class;
                break;
            case Constants.PRIVACY_POLICY:
                fragmentClass = FragmentPrivacy.class;
                break;
            case Constants.TERMS_AND_CONDITIONS:
                fragmentClass = FragmentTermsAndConditions.class;
                break;
            case Constants.CONTACT_US:
                fragmentClass = FragmentContactUs.class;
                break;
            case Constants.EDIT_EMAIL:
                fragmentClass = FragmentEditEmail.class;
                break;
            case Constants.EDIT_PASSWORD:
                fragmentClass = FragmentEditPassword.class;
                break;
            case Constants.EDIT_PHONE:
                fragmentClass = FragmentEditPhoneNumber.class;
                break;
            case Constants.ENTER_EDIT_NUMBER:
                fragmentClass = FragmentEnterEditNumber.class;
                break;
            case Constants.SUCCESS_PASSWORD:
                fragmentClass = FragmentSuccessPassword.class;
                break;
            case Constants.VERIFY_CODE:
                fragmentClass = FragmentUpdateNumberVerifyCode.class;
                break;
            case Constants.VERIFY_SECONDARY_CODE:
                fragmentClass = FragmentVerifySecondaryPhone.class;
                break;
            case Constants.NEW_MESSAGE:
                fragmentClass = NewMessageFragment.class;
                break;
            case Constants.EXPORT_CONTACTS_FILTER:
                fragmentClass = ExportContactFilter.class;
                break;
            case Constants.SEND_INVITE_REQUEST:
                fragmentClass = FragmentInviteSendRequest.class;
                break;
            case Constants.EDIT_CONTACT_INFO_EMAIL:
                fragmentClass = FragmentEditContactInfoEmail.class;
                break;
            case Constants.EDIT_CONTACT_INFO:
                fragmentClass = FragmentEditContactInfo.class;
                break;
            case Constants.CONVERSATION_FILTER:
                fragmentClass = FragmentConversationFilter.class;
                break;
            case Constants.DEFAULT_SETTING:
                fragmentClass = FragmentDefaultSetting.class;
                break;
            case Constants.LOGIN_SALESFORCE:
                fragmentClass = Login_Salesforce.class;
                break;
            case Constants.VERSION_DETAILS:
                fragmentClass = Fragment_VersionDetails.class;
                break;
            case Constants.OCR_IMAGEPATH:
                fragmentClass = FragmentOCRImagePath.class;
                break;
            case Constants.CREATE_MULTI_PROFILE_FRAGMENT:
                fragmentClass = FragmentCreateProfileInfo.class;
                break;
            case Constants.RECIPENT_INFO:
                fragmentClass = FragmentRecipientInfo.class;
                break;
            case Constants.OPTIONAL_NOTES:
                fragmentClass = FragmentOptionalNotes.class;
                break;
        }
        try {
            mFrag = (Fragment) fragmentClass.newInstance();
            if (mBundle != null) {
                mFrag.setArguments(mBundle);
            }
            if (mBackStack) {
                fragmentManager.beginTransaction().add(R.id.main_frag, mFrag, mType).addToBackStack(mType).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.main_frag, mFrag, mType).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // function for replace fragment
    public void fnLoadFragReplace(String mType, boolean mBackStack, Bundle mBundle) {
        mFrag = null;
        fragmentClass = null;
        switch (mType) {
            case Constants.LOGIN:
                fragmentClass = FragmentLogin.class;
                break;
            case Constants.TUTORIAL_BASE:
                fragmentClass = FragmentTutorialBase.class;
                break;
            case Constants.PROFILE_PICTURE:
                fragmentClass = FragmentCreateProfilePicture.class;
                break;
            case Constants.SPLASH:
                fragmentClass = FragmentSplash.class;
                break;
            case Constants.SIGN_UP:
                fragmentClass = FragmentSignUp.class;
                break;
            case Constants.HOME:
                mBind.mBottomBar.setVisibility(View.VISIBLE);
                mBind.mTopBar.setVisibility(View.GONE);
//                if (mapView.equalsIgnoreCase("map") || mapView.equalsIgnoreCase("")) {
                fragmentClass = FragmentHome.class;
//                } else {
//                    fragmentClass = FragmentHomeGrid.class;
//                }
                break;
            case Constants.ALERTS:
                setAlerts();
                fragmentClass = FragmentActivity.class;
                break;
            case Constants.PROFILE_FRAGMENT:
                fragmentClass = FragmentProfile.class;
                break;
            case Constants.CONTACTS:
                fragmentClass = FragmentConnections.class;
                break;
            case Constants.MESSAGE:
                fragmentClass = FragmentConversation.class;
                break;
            case Constants.VERIFICATION_ONE:
                fragmentClass = FragmentPhoneVerificationSendCode.class;
                break;
            case Constants.PROFILE_CREATE:
                fragmentClass = FragmentCreateProfile.class;
                break;
            case Constants.WELCOME_LNQ:
                fragmentClass = FragmentWelcomeLnq.class;
                break;
            case Constants.IDENTITY_VERIFY_SUCCESS:
                fragmentClass = FragIdentityVerifySuccess.class;
                break;
        }
        try {
            mFrag = (Fragment) fragmentClass.newInstance();
            if (mBundle != null) {
                mFrag.setArguments(mBundle);
            }
            if (mBackStack) {
                fragmentManager.beginTransaction().replace(R.id.main_frag, mFrag, mType).addToBackStack(mType).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.main_frag, mFrag, mType).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFragmentVisible(String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        return fragment != null && fragment.isVisible();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                EventBus.getDefault().post(new EventBusCaptureImage());
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    // function commitAllowingStateLoss fragment
    public void fnLoadFragReplaceCommitAllowing(String mType, boolean mBackStack, Bundle mBundle) {
        switch (mType) {
            case Constants.TUTORIAL_BASE:
                fragmentClass = FragmentTutorialBase.class;
                break;
        }
        try {
            mFrag = (Fragment) fragmentClass.newInstance();
            if (mBundle != null) {
                mFrag.setArguments(mBundle);
            }
            if (mBackStack) {
                fragmentManager.beginTransaction().replace(R.id.main_frag, mFrag, mType).addToBackStack(mType).commitAllowingStateLoss();
            } else {
                fragmentManager.beginTransaction().replace(R.id.main_frag, mFrag, mType).commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // global functions
    public void fnLocationTracking() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(8000);
        mLocationRequest.setFastestInterval(6000);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        EventBus.getDefault().post(new EventBusMapLocationUpdate(location));
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    //    Method to pop back to home fragment....
    public void popBackHomeFragment(String tag) {
        for (int i = fragmentManager.getBackStackEntryCount() - 1; i >= 0; i--) {
            if (!fragmentManager.getBackStackEntryAt(i).getName().equalsIgnoreCase(tag)) {
                fragmentManager.popBackStack();
            } else {
                break;
            }
        }
    }

    public void logOut() {
        LnqApplication.getInstance().editor.putBoolean(EndpointKeys.IS_USER_LOGGED_IN, false);
//        LnqApplication.getInstance().editor.putString(EndpointKeys.ID, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_STATUS, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FNAME, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_LNAME, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_AVATAR, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_STATUS_MESSAGE, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_INTRESTS, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_CURRENT_POSITION, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_ADDRESS, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_COMPANY, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIRTHDAY, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIO, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PHONE, "");
//        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_EMAIL, "");
//        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASSWORD, "");
//        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASS, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_TO, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_AT, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTION_COUNT, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, "");
        LnqApplication.getInstance().editor.putString("looged", "").apply();
        LnqApplication.getInstance().editor.apply();
        if (context != null) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack();
            }
            fnLoadFragReplace(Constants.LOGIN, false, null);
            ((MainActivity) this).progressDialog.dismiss();
        }
        multiProfileRepositry.deleteAllProfiles();
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

    public void fnHideKeyboardForcefully(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void fnShowKeyboardFrom(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public boolean fnIsisOnline() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean fnCheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void fnRequestPermission(int resultCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, resultCode);
        }
    }

    public void fnRequestLocationPermission(int resultCOde) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, resultCOde);
        }
    }

    public boolean fnCheckLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean fnCheckContactsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void fnRequestContactsPermission(int resultCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, resultCode);
        }
    }

    public boolean fnCheckCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean fnCheckStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean fnCheckReadStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void fnRequestCameraPermission(int resultCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, resultCode);
        }
    }

    public void fnRequestStoragePermission(int resultCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, resultCode);
        }
    }

    String currentPhotoPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            List<String> paths = Matisse.obtainPathResult(data);
            EventBus.getDefault().post(new EventBusGetChatImagePath(paths.get(0)));
        } else if (requestCode == RC_INTENT) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                googleSignInClient.signOut();
                EventBus.getDefault().post(new EventBusGoogleSignInAccount(acct));
            } else {
//                Toast.makeText(context, result.getStatus().toString() + "\nmsg: " + result.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 123 && data != null) {
            if (resultCode == RESULT_OK) {
                //Successfully signed in
                LinkedInUser user = data.getParcelableExtra("social_login");
                EventBus.getDefault().post(new EventBusLinkedInData(user));
            } else {
                if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                    //Handle : user denied access to account

                } else if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_FAILED) {

                    //Handle : Error in API : see logcat output for details
                    Log.e("LINKEDIN ERROR", data.getStringExtra("err_message"));
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == 2000) {
            /*mBitmap = ValidUtils.resizeImage(imageFile, imageFile.getPath(), imageViewOCR);
            if (mBitmap != null) {
                mTextView.setText(null);
                imageViewOCR.setImageBitmap(mBitmap);
            }*/
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Bundle bundle = new Bundle();
            bundle.putByteArray("ocr", byteArray);
            fnLoadFragAdd(Constants.OCR_IMAGEPATH, true, bundle);
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            Gson mGson = new Gson();
//                    val mData = mGson.fromJson<List<String>>(data?.getStringExtra("result"), object : TypeToken<List<String>>() {}.type)
            Toast.makeText(context, "Gallery", Toast.LENGTH_SHORT).show();
        }
    }

    public void openCamera() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                /*Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.lnq",
//                        photoFile);*/
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(photoFile.getAbsolutePath()));
//                startActivityForResult(takePictureIntent, 2000);
//            }
//        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2000);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int count = 0;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                count++;
            }
        }
        //      2 request code for permission request from gallery
        if (requestCode == 2) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusGalleryPermission(true));
            } else {
                EventBus.getDefault().post(new EventBusGalleryPermission(false));
            }
            //      3 request code for permission request from camera
        } else if (requestCode == 3) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusCameraPermission(true));
            } else {
                EventBus.getDefault().post(new EventBusCameraPermission(false));
            }
            //      4 request code for permission request from location
        } else if (requestCode == 4) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusLocationPermission());
            }
        }
        //      5 request code for permission request from contact
        else if (requestCode == 5) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusContactPermission(EndpointKeys.IMPORT, requestCode));
            }
        }
//        6 request for permission request for visibility click permissions...
        else if (requestCode == 6) {
            if (count == grantResults.length) {
                mFScreenName = Constants.VISIBLE;
                fnLoadFragAdd(Constants.VISIBLE, true, null);
            }
        } else if (requestCode == 7) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusContactPermission(EndpointKeys.EXPORT, requestCode));
            }
        } else if (requestCode == 8) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusShowScanScreen());
            }
        } else if (requestCode == 9) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusContactPermissionProfile());
            }
        } else if (requestCode == 10) {
            if (count == grantResults.length) {
                EventBus.getDefault().post(new EventBusOpenCameraSecondaryProfile());
            }
        }
    }

    public void fnAddFragWithCustomAnimation(String mType, boolean mBackStack, Bundle mBundle) {
        mFrag = null;
        fragmentClass = null;
        switch (mType) {
            case Constants.IMPORT_CONTACTS:
                fragmentClass = FragmentImportContacts.class;
                break;
            case Constants.EXPORT_CONTACTS:
                fragmentClass = FragmentExportContacts.class;
                break;
        }
        try {
            mFrag = (Fragment) fragmentClass.newInstance();
            if (mBundle != null) {
                mFrag.setArguments(mBundle);
            }
            if (mBackStack) {
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.exit_to_left, R.anim.exit_to_right).add(R.id.main_frag, mFrag, mType).addToBackStack(mType).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.main_frag, mFrag, mType).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getChatThread() {
        chatThreadList.clear();
//        chatThreadCall = Api.WEB_SERVICE.getUserChat(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));
        chatThreadCall = Api.WEB_SERVICE.getUserGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""), profileId);
        chatThreadCall.enqueue(new Callback<ChatThread>() {
            @Override
            public void onResponse(Call<ChatThread> call, Response<ChatThread> response) {
                if (response.isSuccessful() && response != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            chatThreadList.addAll(response.body().getGetChatThreads());
                            int total = 0;
                            for (int i = 0; i < chatThreadList.size(); i++) {
                                if (chatThreadList.get(i).getIs_muted() != null && !chatThreadList.get(i).getIs_muted().equals("muted"))
                                    total = total + Integer.parseInt(chatThreadList.get(i).getCount());
                            }
                            if (total != 0) {
                                mBind.mBottomBar.setVisibility(View.VISIBLE);
                                mBind.textViewCount.setVisibility(View.VISIBLE);
                                mBind.textViewCount.setText(String.valueOf(total));

                                LnqApplication.getInstance().editor.putString(Constants.CHAT_COUNT, String.valueOf(total)).apply();
                                int activityCount = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.ACTIVITY_COUNT, "0"));
                                LnqApplication.getInstance().editor.putString(Constants.TOTAL_COUNT, String.valueOf(total + activityCount)).apply();
                                EventBus.getDefault().post(new EventBusTotalCount());

                            } else {

                                LnqApplication.getInstance().editor.putString(Constants.CHAT_COUNT, String.valueOf(total)).apply();
                                int activityCount = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.ACTIVITY_COUNT, "0"));
                                LnqApplication.getInstance().editor.putString(Constants.TOTAL_COUNT, String.valueOf(total + activityCount)).apply();
                                EventBus.getDefault().post(new EventBusTotalCount());

                                mBind.textViewCount.setVisibility(View.GONE);
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatThread> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                progressDialog.dismiss();
//                if (error != null) {
//                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
//                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
//                    } else {
//                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
//                    }
//                } else {
//                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
//                }
            }
        });
    }

    public void getChatThread(String threadId) {
//        chatThreadCall = Api.WEB_SERVICE.getUserChat(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));
        chatThreadCall = Api.WEB_SERVICE.getUserGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""), profileId);
        chatThreadCall.enqueue(new Callback<ChatThread>() {
            @Override
            public void onResponse(Call<ChatThread> call, Response<ChatThread> response) {
                if (response.isSuccessful() && response != null) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            if(chatThreadList.size() > 0){
                            GetChatThread foundedChatThread = null;
                            for (GetChatThread getChatThread : response.body().getGetChatThreads()) {
                                if (getChatThread.getGroupchat_thread_id() != null) {
                                    if (getChatThread.getGroupchat_thread_id().equals(threadId)) {
                                        foundedChatThread = getChatThread;
                                        break;
                                    }
                                }
                            }
                            if (foundedChatThread != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString(EndpointKeys.GROUP_USERNAMES, foundedChatThread.getUserNames());
                                bundle.putString(EndpointKeys.GROUP_NAME, foundedChatThread.getGroup_name());
                                bundle.putString(EndpointKeys.GROUP_CHAT_THREAD_ID, foundedChatThread.getGroupchat_thread_id());
                                bundle.putString(EndpointKeys.USER_AVATAR, foundedChatThread.getUserAvatar());
                                bundle.putString(EndpointKeys.GROUP_USERS_IDS, foundedChatThread.getParticipant_ids());
                                bundle.putString(EndpointKeys.CHAT_UNREAD_COUNT, foundedChatThread.getCount());
                                bundle.putString(EndpointKeys.GROUP_USER_NAMES, foundedChatThread.getUserNames());
                                bundle.putString(EndpointKeys.GROUP_USERS_PROFILE_IDS, foundedChatThread.getParticipant_profile_ids());
                                fnLoadFragAdd(Constants.GROUP_CHAT_FRAGMENT, false, bundle);
                            }
//                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatThread> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                progressDialog.dismiss();
//                if (error != null) {
//                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
//                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
//                    } else {
//                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
//                    }
//                } else {
//                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
//                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getActivityCount(EventBusActivityCount eventBusActivityCount) {
        int count = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.ACTIVITY_COUNT, "0"));

        if (count != 0) {
            mBind.mBottomBar.setVisibility(View.VISIBLE);
            mBind.textViewActivityCount.setVisibility(View.VISIBLE);
            mBind.textViewActivityCount.setText(String.valueOf(count));
        } else if (count == 0) {
            mBind.textViewActivityCount.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTotalCount(EventBusTotalCount eventBusTotalCount) {
        badgeCount = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.TOTAL_COUNT, "0"));
        if (badgeCount == 0) {
            ShortcutBadger.removeCount(context); //for 1.1.4+
        } else
            ShortcutBadger.applyCount(context, badgeCount); //for 1.1.4+

    }

    public void setAlerts() {
        LnqApplication.getInstance().editor.putString(EndpointKeys.CURRENT_FRAGMENT, "alerts").apply();
        LnqApplication.getInstance().editor.putString(Constants.ACTIVITY_COUNT, "0").apply();
        EventBus.getDefault().post(new EventBusActivityCount());
        int total = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.TOTAL_COUNT, "0"));
        total = total - Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.ACTIVITY_COUNT, "0"));
        LnqApplication.getInstance().editor.putString(Constants.TOTAL_COUNT, String.valueOf(total)).apply();
        EventBus.getDefault().post(new EventBusTotalCount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setUserSessionBus(EventBusUserSession eventBusUserSession) {
        setUserSession(eventBusUserSession.getAction_type());
    }

    public void setUserSession(String action_type) {
        sessionCall = Api.WEB_SERVICE.setUserSession(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "")
                        , LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                LnqApplication.getInstance().sharedPreferences.getString("id", ""), action_type);

        sessionCall.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                if (response.body() != null) {
                    if (response.body().getFrozenDate() != null)
                        LnqApplication.getInstance().editor.putString(EndpointKeys.STATUS_DATE, response.body().getFrozenDate()).apply();
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable t) {

            }
        });
    }

    public void googleSignIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // The serverClientId is an OAuth 2.0 web client ID
                .requestServerAuthCode(getString(R.string.clientID))
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                        new Scope(PeopleScopes.CONTACTS_READONLY),
                        new Scope(PeopleScopes.USER_EMAILS_READ),
                        new Scope(PeopleScopes.USERINFO_EMAIL),
                        new Scope(PeopleScopes.CONTACTS),
                        new Scope(PeopleScopes.USER_PHONENUMBERS_READ))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage(this, this)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                    .build();
            mGoogleApiClient.connect();
        } else {
            googleSignInUsingIntent();
        }
    }

    private void googleSignInUsingIntent() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_INTENT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = mGoogleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(), RC_API_CHECK);
        dialog.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (isFromImport) {
            googleSignInUsingIntent();
            isFromImport = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void signOutOfGoogle() {
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
    }

}