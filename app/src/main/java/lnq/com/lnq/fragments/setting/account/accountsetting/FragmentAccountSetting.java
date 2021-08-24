package lnq.com.lnq.fragments.setting.account.accountsetting;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.FlagKit;
import lnq.com.lnq.databinding.FragmentAccountSettingBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusDeleteAccount;
import lnq.com.lnq.model.event_bus_models.EventBusFreezeAccount;
import lnq.com.lnq.model.event_bus_models.EventBusRequestYourData;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.LogOut;
import lnq.com.lnq.model.gson_converter_models.pushnotifications.PushNotificationMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAccountSetting extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private FragmentAccountSettingBinding mBind;
    private PhoneNumberUtil phoneNumberUtil;

    private Call<RegisterLoginMainObject> callDeleteAccount;
    private Call<RegisterLoginMainObject> callISFrozen;
    private Call<LogOut> callLogOut;
    private Call<PushNotificationMainObject> callNotification;
    private Call<RegisterLoginMainObject> callRequestData;


    public FragmentAccountSetting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_account_setting, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        mBind.imageViewBack.setOnClickListener(this);
        mBind.mTvGeneralHeading1.setOnClickListener(this);

        LnqApplication.getInstance().sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mBind.mTvEnableLocation.setOnClickListener(this);
        mBind.mImgEnableLocationRightSign.setOnClickListener(this);
        mBind.mTvEnableCamera.setOnClickListener(this);
        mBind.mImgEnableCameraRightSign.setOnClickListener(this);
        mBind.mTvEnableAccessContacts.setOnClickListener(this);
        mBind.mImgEnableAccessContactsRightSign.setOnClickListener(this);
        mBind.mImgEditEmail.setOnClickListener(this);
        mBind.mImgEditPassword.setOnClickListener(this);
        mBind.mImgEditPhoneNumber.setOnClickListener(this);
        mBind.mTvDeleteAccount.setOnClickListener(this);
        mBind.mTvFreezeAccount.setOnClickListener(this);
        mBind.mTvRequestYourData.setOnClickListener(this);
        mBind.mScLocationHidden.setOnCheckedChangeListener(this);
        if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.NOTIFICATION_STATUS, "").equals("0")) {
            mBind.mScLocationHidden.setChecked(false);
        } else {
            mBind.mScLocationHidden.setChecked(true);
        }

        phoneNumberUtil = PhoneNumberUtil.getInstance();

        String phoneNumber = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, "");
        try {
            if (phoneNumberUtil.isValidNumber(phoneNumberUtil.parseAndKeepRawInput(phoneNumber, Constants.DEFAULT_REGION))) {
                String countryRegionCode = phoneNumberUtil.getRegionCodeForNumber(phoneNumberUtil.parseAndKeepRawInput(phoneNumber, Constants.DEFAULT_REGION));
                phoneNumber = phoneNumberUtil.format(phoneNumberUtil.parse(phoneNumber, Constants.DEFAULT_REGION), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                mBind.imageViewFlag.setImageDrawable(FlagKit.drawableWithFlag(getActivity(), countryRegionCode.toLowerCase().trim()));
            }
        } catch (Exception e) {

        }
        mBind.mTvPhoneNumber.setEnabled(false);
        mBind.mTvPassword.setEnabled(false);
        mBind.mTvEmailAddress.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""));
        mBind.mTvPhoneNumber.setText(phoneNumber);
        mBind.mTvPassword.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASS, ""));

        OverScrollDecoratorHelper.setUpOverScroll(mBind.nestedScrollView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRequestYourData(EventBusRequestYourData mObj) {
        requestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusFreezeAcc(EventBusFreezeAccount mObj) {
        IsFrozen("1");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusDeleteAccount(EventBusDeleteAccount mObj) {
        deleteAccount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvGeneralHeading1:
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.mTvEnableLocation:
            case R.id.mImgEnableLocationRightSign:
                if (!((MainActivity) getActivity()).fnCheckLocationPermission()) {
                    ((MainActivity) getActivity()).fnRequestLocationPermission(4);
                }
                Intent intentLocation = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intentLocation);
                break;
            case R.id.mTvEnableCamera:
            case R.id.mImgEnableCameraRightSign:
                if (!((MainActivity) getActivity()).fnCheckCameraPermission()) {
                    ((MainActivity) getActivity()).fnRequestCameraPermission(3);
                }
                break;
            case R.id.mTvEnableAccessContacts:
            case R.id.mImgEnableAccessContactsRightSign:
                if (!((MainActivity) getActivity()).fnCheckContactsPermission()) {
                    ((MainActivity) getActivity()).fnRequestContactsPermission(5);
                }
                break;
            case R.id.mImgEditEmail:
                ((MainActivity) getActivity()).fnLoadFragAdd("EDIT EMAIL", true, null);
                break;
            case R.id.mImgEditPassword:
                ((MainActivity) getActivity()).fnLoadFragAdd("EDIT PASSWORD", true, null);
                break;
            case R.id.mImgEditPhoneNumber:
                ((MainActivity) getActivity()).fnLoadFragAdd("EDIT PHONE", true, null);
                break;
            case R.id.mTvDeleteAccount:
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_DELETE_ACCOUNT, true, null);
                break;
            case R.id.mTvFreezeAccount:
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_FREEZE_ACCOUNT, true, null);
                break;
            case R.id.mTvRequestYourData:
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_REQUEST_DATA, true, null);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (callDeleteAccount != null && callDeleteAccount.isExecuted()) {
            callDeleteAccount.cancel();
        }
        if (callRequestData != null && callRequestData.isExecuted()) {
            callRequestData.cancel();
        }
        LnqApplication.getInstance().sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void requestData() {
        ((MainActivity) getActivity()).progressDialog.show();
//        callRequestData = Api.WEB_SERVICE.requestData(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        callRequestData = Api.WEB_SERVICE.requestData(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        callRequestData.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("data_requested"));
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
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

    private void deleteAccount() {
        ((MainActivity) getActivity()).progressDialog.show();
//        callDeleteAccount = Api.WEB_SERVICE.deleteAccount(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        callDeleteAccount = Api.WEB_SERVICE.deleteAccount(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        callDeleteAccount.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("delete_account"));
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
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

    private void IsFrozen(String freeze) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callISFrozen = Api.WEB_SERVICE.freeze(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), freeze);
        callISFrozen = Api.WEB_SERVICE.freeze(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), freeze);
        callISFrozen.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            reqLogOut();
                            EventBus.getDefault().post(new EventBusUserSession("freeze_account"));
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

    private void reqLogOut() {
        ((MainActivity) getActivity()).progressDialog.show();
//        callLogOut = Api.WEB_SERVICE.logout(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), "1", LnqApplication.getInstance().sharedPreferences.getString("fcm_token", ""));
        callLogOut = Api.WEB_SERVICE.logout(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), "1", LnqApplication.getInstance().sharedPreferences.getString("fcm_token", ""));
        callLogOut.enqueue(new Callback<LogOut>() {
            @Override
            public void onResponse(Call<LogOut> call, Response<LogOut> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case "1":
                            EventBus.getDefault().post(new EventBusUserSession("sign_out"));
                            FirebaseAuth.getInstance().signOut();
                            ((MainActivity) getActivity()).logOut();
                            break;
                        case "0":
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<LogOut> call, Throwable error) {
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

    private void reqNotificationsSet(String notification_status) {
//        callNotification = Api.WEB_SERVICE.notificationsSet(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.FCM_TOKEN, ""), notification_status,LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.LAST_LOGIN,""));
        callNotification = Api.WEB_SERVICE.notificationsSet(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.FCM_TOKEN, ""), notification_status, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.LAST_LOGIN, ""));
        callNotification.enqueue(new Callback<PushNotificationMainObject>() {
            @Override
            public void onResponse(Call<PushNotificationMainObject> call, Response<PushNotificationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("push_notifications"));
                        case 0:
                            EventBus.getDefault().post(new EventBusUserSession("push_notifications"));
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PushNotificationMainObject> call, Throwable error) {
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            LnqApplication.getInstance().editor.putString(EndpointKeys.NOTIFICATION_STATUS, "1");
            LnqApplication.getInstance().editor.apply();
            reqNotificationsSet("1");
        } else if (!isChecked) {
            LnqApplication.getInstance().editor.putString(EndpointKeys.NOTIFICATION_STATUS, "0");
            LnqApplication.getInstance().editor.apply();
            reqNotificationsSet("0");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mBind.mTvEmailAddress.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""));
    }
}