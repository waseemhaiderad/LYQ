package lnq.com.lnq.fragments.setting.account.accountsetting;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentEnterEditNumberBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusPrimaryPhoneProfile;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEnterEditNumber extends Fragment implements View.OnClickListener {

    private FragmentEnterEditNumberBinding mBind;

    //    Instance fields....
    private boolean codeResend = false;

    //    Firebase fields....
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallback;
    private FirebaseAuth mAuth;

    private Call<RegisterLoginMainObject> callPhoneVerification;
    private Call<RegisterLoginMainObject> callIsPhoneUnique;

    public FragmentEnterEditNumber() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_edit_number, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        //        Registering event bus....
        EventBus.getDefault().register(this);

//        Firebase fields initialization....
        mAuth = FirebaseAuth.getInstance();

//        Setting edit texts to country code picker for validation....
        mBind.countryCodePicker.registerCarrierNumberEditText(mBind.editTextPhone);
        mBind.mBtnVerify.setOnClickListener(this);

        //        Phone auth callback....
        phoneAuthCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reqUpdatePhoneNumber();
                    }

                }, 3000);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_phone_number));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Quota exceeded");
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", e.getMessage());
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (codeResend) {
                    ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.code_sent));
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.code_sent));
                    Bundle mBundle = new Bundle();
                    mBundle.putString(EndpointKeys.PHONE, mBind.editTextPhone.getText().toString().trim());
                    mBundle.putString("verification_id", verificationId);
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VERIFY_CODE, true, mBundle);
                }
                EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
            }
        };
        mBind.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), mBind.getRoot());
                return false;
            }
        });
    }

    //    Event bus trigger when code is sent to user....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void verificationCode(final EventBusPhoneVerification mObj) {
        switch (mObj.getmFlag()) {
            case "mFverify":
                PhoneAuthCredential mCredential = PhoneAuthProvider.getCredential(mObj.getVerificationId(), mObj.getCode());
                mAuth.signInWithCredential(mCredential)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    reqUpdatePhoneNumber();
                                } else {
                                    EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_code));
                                        return;
                                    }
                                    ((MainActivity) getActivity()).showMessageDialog("error", task.getException().toString() + "");
                                }
                            }
                        });
                break;
            case "mFresend":
                codeResend = true;
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        mBind.countryCodePicker.getFullNumberWithPlus(),
                        0,
                        TimeUnit.SECONDS,
                        getActivity(),
                        phoneAuthCallback);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnVerify:
                fnValidateData(mBind.editTextPhone.getText().toString().trim());
                break;
        }
    }

    private void fnValidateData(String phoneNumber) {
        if (!((MainActivity) getActivity()).fnIsisOnline()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            return;
        }
        if (phoneNumber.isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_phone_number));
        } else if (!mBind.countryCodePicker.isValidFullNumber()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_phone_number));
        } else {
            codeResend = false;
            reqIsPhoneUnique(mBind.countryCodePicker.getFullNumberWithPlus());
        }
    }

    //    Method to request api to check if number is unique or not....
    private void reqIsPhoneUnique(final String phoneNumber) {
        ((MainActivity) getActivity()).progressDialog.show();
        callIsPhoneUnique = Api.WEB_SERVICE.isPhoneUnique(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), phoneNumber);
//        callIsPhoneUnique = Api.WEB_SERVICE.isPhoneUnique(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), phoneNumber);
        callIsPhoneUnique.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            ((MainActivity) getActivity()).progressDialog.show();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumber,
                                    0,
                                    TimeUnit.SECONDS,
                                    getActivity(),
                                    phoneAuthCallback);
                            break;
                        case 0:
                            ((MainActivity) getActivity()).progressDialog.dismiss();
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                } else {
                    ((MainActivity) getActivity()).progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
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

    //  Method to save user verification status on server
    private void reqUpdatePhoneNumber() {
        ValidUtils.hideKeyboardFromFragment(getContext(),mBind.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callPhoneVerification = Api.WEB_SERVICE.updateUserPhone(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, ""), mBind.countryCodePicker.getFullNumberWithPlus());
//        callPhoneVerification = Api.WEB_SERVICE.updateUserPhone(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, ""), mBind.countryCodePicker.getFullNumberWithPlus());
        callPhoneVerification.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            ((MainActivity) getActivity()).showMessageDialog("success", "Phone updated successfully.");
                            EventBus.getDefault().post(new EventBusUserSession("phone_no_updated"));
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PHONE, mBind.countryCodePicker.getFullNumberWithPlus()).apply();
                            FirebaseAuth.getInstance().signOut();
                            EventBus.getDefault().post(new EventBusPrimaryPhoneProfile());
                            ((MainActivity)getActivity()).popBackHomeFragment(Constants.PROFILE_FRAGMENT);
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
                EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
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

}