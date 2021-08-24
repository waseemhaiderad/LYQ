package lnq.com.lnq.fragments.registeration.phoneverification;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentPhoneVerificationSendCodeBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPhoneVerificationSendCode extends Fragment {

    //    Instance fields....
    private boolean codeResend = false;
    private int countryCodeSelector = 1;

    //    Firebase fields....
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallback;
    private FirebaseAuth mAuth;

    //    Api fields....
    private Call<RegisterLoginMainObject> callPhoneVerification;
    private Call<RegisterLoginMainObject> callIsPhoneUnique;

    //    Animation fields....
    private Animation slideUpAnimation, slideDownAnimation;

    //    Android fields....
    private Unregistrar unregister;
    private FragmentPhoneVerificationSendCodeBinding sendCodeBinding;
    private SendCodeClickListener sendCodeClickListener;
    AppCompatImageView imageViewBackTopBar;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentPhoneVerificationSendCode() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendCodeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_verification_send_code, container, false);
        return sendCodeBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView topBarLayout = sendCodeBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.send_code);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("back", "back");
                ((MainActivity) getActivity()).fnLoadFragReplace(Constants.SIGN_UP, false, bundle);
            }
        });
        textViewHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("back", "back");
                ((MainActivity) getActivity()).fnLoadFragReplace(Constants.SIGN_UP, false, bundle);
            }
        });
        init();
        setCustomFont();
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewBoldFont(sendCodeBinding.textViewWelcome);
        fontUtils.setEditTextRegularFont(sendCodeBinding.editTextFirstName);
        fontUtils.setEditTextRegularFont(sendCodeBinding.editTextLastName);
        fontUtils.setEditTextRegularFont(sendCodeBinding.editTextPhone);
        fontUtils.setTextViewRegularFont(sendCodeBinding.textViewWeWillSendYou);
        fontUtils.setTextViewRegularFont(sendCodeBinding.textViewPleaseEnter);
        fontUtils.setTextViewMedium(sendCodeBinding.clearTextViewSendVerification);

        fontUtils.setTextViewBoldFont(sendCodeBinding.textViewWelcome1);
        fontUtils.setEditTextRegularFont(sendCodeBinding.editTextFirstName1);
        fontUtils.setEditTextRegularFont(sendCodeBinding.editTextLastName1);
        fontUtils.setEditTextRegularFont(sendCodeBinding.editTextPhone1);
        fontUtils.setTextViewRegularFont(sendCodeBinding.textViewWeWillSendYou1);
        fontUtils.setTextViewRegularFont(sendCodeBinding.textViewPleaseEnter1);
        fontUtils.setTextViewMedium(sendCodeBinding.clearTextViewSendVerification1);

    }

    private void init() {
//        Registering event bus....
        EventBus.getDefault().register(this);

//        Getting data from previous fragment....
        if (getArguments() != null) {
            if (getArguments().getString("mFlag", "").equals("phone")) {
                sendCodeBinding.textInputLayoutFirstName.setVisibility(View.GONE);
                sendCodeBinding.textInputLayoutLastName.setVisibility(View.GONE);
            }
        }

//        Loading animation....
        slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

//        Firebase fields initialization....
        mAuth = FirebaseAuth.getInstance();

//        Setting edit texts to country code picker for validation....
        sendCodeBinding.countryCodePicker.registerCarrierNumberEditText(sendCodeBinding.editTextPhone);
        sendCodeBinding.countryCodePicker1.registerCarrierNumberEditText(sendCodeBinding.editTextPhone1);

//        Setting event listener using binding....
        sendCodeClickListener = new SendCodeClickListener(getActivity());
        sendCodeBinding.setClickListener(sendCodeClickListener);

//        Registering event for keyboard visibility....
        unregister = KeyboardVisibilityEvent.registerEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            if (sendCodeBinding.editTextFirstName.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextFirstName1);
                            } else if (sendCodeBinding.editTextLastName.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextLastName1);
                            } else if (sendCodeBinding.editTextNickName.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextNickName1);
                            } else if (sendCodeBinding.editTextPhone.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextPhone1);
                            }
                            sendCodeBinding.mRoot1.startAnimation(slideUpAnimation);
                            toggleVisibilityOfRoot(sendCodeBinding.mRoot1, sendCodeBinding.mRoot);
                        } else {
                            sendCodeBinding.editTextFirstName.setText(sendCodeBinding.editTextFirstName1.getText().toString());
                            sendCodeBinding.editTextLastName.setText(sendCodeBinding.editTextLastName1.getText().toString());
                            sendCodeBinding.editTextPhone.setText(sendCodeBinding.editTextPhone1.getText().toString());
                            sendCodeBinding.editTextNickName.setText(sendCodeBinding.editTextNickName1.getText().toString());
                            if (sendCodeBinding.editTextFirstName1.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextFirstName);
                            } else if (sendCodeBinding.editTextLastName1.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextLastName);
                            } else if (sendCodeBinding.editTextNickName1.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextNickName);
                            } else if (sendCodeBinding.editTextPhone.hasFocus()) {
                                changeFocus(sendCodeBinding.editTextPhone1);
                            }
                            toggleVisibilityOfRoot(sendCodeBinding.mRoot, sendCodeBinding.mRoot1);
                            sendCodeBinding.mRoot.startAnimation(slideDownAnimation);
                        }
                    }
                });

//        Phone auth callback....
        phoneAuthCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                final Dialog mDialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
                mDialog.setCancelable(false);
                mDialog.setContentView(R.layout.cus_dialog_success);
                TextView text = mDialog.findViewById(R.id.textViewMessageDialog);
                text.setText(getResources().getString(R.string.number_already_verified));
                mDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        reqVerificationStatus();
                    }

                }, 3000);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_phone_number));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    ((MainActivity) getActivity()).showMessageDialog("error", e.getMessage());
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", e.getMessage());
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (codeResend) {
                    ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.code_sent));
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.code_sent));
                    Bundle mBundle = new Bundle();
                    mBundle.putString(EndpointKeys.FIRST_NAME, sendCodeBinding.editTextFirstName.getText().toString().trim());
                    mBundle.putString(EndpointKeys.LAST_NAME, sendCodeBinding.editTextLastName.getText().toString().trim());
                    mBundle.putString(EndpointKeys.PHONE, sendCodeBinding.editTextPhone.getText().toString().trim());
                    mBundle.putString("verification_id", verificationId);
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VERIFICATION_TWO, true, mBundle);
                }
                EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
            }
        };

//        Events for country code country select listeners....
        sendCodeBinding.countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                if (countryCodeSelector == 0) {
                    sendCodeBinding.countryCodePicker1.setFullNumber(sendCodeBinding.countryCodePicker.getFullNumber());
                }
            }
        });

        sendCodeBinding.countryCodePicker1.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                if (countryCodeSelector == 1) {
                    sendCodeBinding.countryCodePicker.setFullNumber(sendCodeBinding.countryCodePicker1.getFullNumber());
                }
            }
        });

//        Events for country code dialog shows listeners....
        sendCodeBinding.countryCodePicker.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
                countryCodeSelector = 0;
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {

            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {

            }
        });

        sendCodeBinding.countryCodePicker1.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
                countryCodeSelector = 1;
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {

            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {

            }
        });

        ValidUtils.textViewGradientColor(sendCodeBinding.textViewWelcome1);
        ValidUtils.textViewGradientColor(sendCodeBinding.textViewWelcome);
        ValidUtils.textViewGradientColor(sendCodeBinding.textViewWeWillSendYou1);
        ValidUtils.textViewGradientColor(sendCodeBinding.textViewWeWillSendYou);
        ValidUtils.textViewGradientColor(sendCodeBinding.textViewPleaseEnter1);
        ValidUtils.textViewGradientColor(sendCodeBinding.textViewPleaseEnter);
    }

    //    Method to change focus of edit texts....
    private void changeFocus(EditText editText) {
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
    }

    //    Method to toggle visibility of view groups....
    public void toggleVisibilityOfRoot(ViewGroup viewGroupShow, ViewGroup viewGroupHide) {
        viewGroupShow.setVisibility(View.VISIBLE);
        viewGroupHide.setVisibility(View.GONE);
    }

    //    Cancelling http requests if in process....
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (callPhoneVerification != null && callPhoneVerification.isExecuted()) {
            callPhoneVerification.cancel();
        }
        if (callIsPhoneUnique != null && callIsPhoneUnique.isExecuted()) {
            callIsPhoneUnique.cancel();
        }
        if (unregister != null) {
            unregister.unregister();
        }
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
                                    reqVerificationStatus();
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
                        sendCodeBinding.countryCodePicker.getFullNumberWithPlus(),
                        0,
                        TimeUnit.SECONDS,
                        getActivity(),
                        phoneAuthCallback);
                break;
        }

    }

    //    Method to validate data....
    private void fnValidateData(String firstName, String lastName, String nikName, String phoneNumber, String buttonNumber) {
        if (!((MainActivity) getActivity()).fnIsisOnline()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            sendCodeBinding.clearTextViewSendVerification.setVisibility(View.VISIBLE);
            sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.GONE);
            return;
        }
        if (firstName.isEmpty()) {
            sendCodeBinding.clearTextViewSendVerification.setVisibility(View.VISIBLE);
            sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_first_name));
        } else if (lastName.isEmpty()) {
            sendCodeBinding.clearTextViewSendVerification.setVisibility(View.VISIBLE);
            sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_last_name));
        } else if (phoneNumber.isEmpty()) {
            sendCodeBinding.clearTextViewSendVerification.setVisibility(View.VISIBLE);
            sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_phone_number));
        } else if (buttonNumber.equals("0") ? !sendCodeBinding.countryCodePicker.isValidFullNumber() : !sendCodeBinding.countryCodePicker1.isValidFullNumber()) {
            sendCodeBinding.clearTextViewSendVerification.setVisibility(View.VISIBLE);
            sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_phone_number));
        } else {
            codeResend = false;
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FNAME, firstName);
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_LNAME, lastName);
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PHONE, buttonNumber.equals("0") ? sendCodeBinding.countryCodePicker.getFullNumberWithPlus() : sendCodeBinding.countryCodePicker1.getFullNumberWithPlus());
            LnqApplication.getInstance().editor.apply();
            reqIsPhoneUnique(buttonNumber.equals("0") ? sendCodeBinding.countryCodePicker.getFullNumberWithPlus() : sendCodeBinding.countryCodePicker1.getFullNumberWithPlus());
        }
    }

    //    Method to request api to check if number is unique or not....
    private void reqIsPhoneUnique(final String phoneNumber) {
        callIsPhoneUnique = Api.WEB_SERVICE.isPhoneUnique(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), phoneNumber);
//        callIsPhoneUnique = Api.WEB_SERVICE.isPhoneUnique(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), phoneNumber);
        callIsPhoneUnique.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                sendCodeBinding.clearTextViewSendVerification2.stopAnimation();
                sendCodeBinding.clearTextViewSendVerification.setVisibility(View.VISIBLE);
                sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.GONE);
                sendCodeBinding.clearTextViewSendVerification2.revertAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumber,
                                    0,
                                    TimeUnit.SECONDS,
                                    getActivity(),
                                    phoneAuthCallback);
                            break;
                        case 0:
                            ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                } else {
                    ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                sendCodeBinding.clearTextViewSendVerification.setVisibility(View.VISIBLE);
                sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.GONE);
                sendCodeBinding.clearTextViewSendVerification2.revertAnimation();
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
    private void reqVerificationStatus() {
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(sendCodeBinding.mRoot);
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        callPhoneVerification = Api.WEB_SERVICE.phoneVerification(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), sendCodeBinding.editTextFirstName.getText().toString().trim(), sendCodeBinding.editTextLastName.getText().toString().trim(), sendCodeBinding.countryCodePicker.getFullNumberWithPlus(), EndpointKeys.PHONE, sendCodeBinding.editTextNickName.getText().toString().trim());
//        callPhoneVerification = Api.WEB_SERVICE.phoneVerification(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), sendCodeBinding.editTextFirstName.getText().toString().trim(), sendCodeBinding.editTextLastName.getText().toString().trim(), sendCodeBinding.countryCodePicker.getFullNumberWithPlus(), EndpointKeys.PHONE);
        callPhoneVerification.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_STATUS, EndpointKeys.PHONE).apply();
                            FirebaseAuth.getInstance().signOut();
                            EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
                            ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.PROFILE_CREATE, false, null);
                            ((MainActivity) getActivity()).showMessageDialog("success", "Phone verification successful");
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

    public class SendCodeClickListener {
        private Context context;

        SendCodeClickListener(Context context) {
            this.context = context;
        }

        public void onSendCodeClick(View view) {
            switch (view.getId()) {
                case R.id.clearTextViewSendVerification:
                    ValidUtils.hideKeyboardFromFragment(getActivity(), sendCodeBinding.getRoot());
                    sendCodeBinding.clearTextViewSendVerification.setVisibility(View.INVISIBLE);
                    sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.VISIBLE);
                    sendCodeBinding.clearTextViewSendVerification2.startAnimation();
                    fnValidateData(sendCodeBinding.editTextFirstName.getText().toString(), sendCodeBinding.editTextLastName.getText().toString(), sendCodeBinding.editTextNickName.getText().toString(), sendCodeBinding.editTextPhone.getText().toString().trim(), "0");
                    break;
                case R.id.clearTextViewSendVerification1:
                    ValidUtils.hideKeyboardFromFragment(getActivity(), sendCodeBinding.getRoot());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendCodeBinding.clearTextViewSendVerification.setVisibility(View.INVISIBLE);
                            sendCodeBinding.clearTextViewSendVerification2.setVisibility(View.VISIBLE);
                            sendCodeBinding.clearTextViewSendVerification2.startAnimation();
                            fnValidateData(sendCodeBinding.editTextFirstName1.getText().toString(), sendCodeBinding.editTextLastName1.getText().toString(), sendCodeBinding.editTextNickName1.getText().toString(), sendCodeBinding.editTextPhone1.getText().toString().trim(), "1");
                        }
                    }, 400);
                    break;
            }
        }

        public void onRootClick(View view) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(sendCodeBinding.mRoot);
        }

        public void backPressed(View view) {
            logOut();
            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.SIGN_UP, false, null);
        }

    }

    public void logOut() {
        LnqApplication.getInstance().editor.putBoolean(EndpointKeys.IS_USER_LOGGED_IN, false);
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
        LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_TO, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_AT, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTION_COUNT, "");
        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, "");
        LnqApplication.getInstance().editor.apply();
    }

}