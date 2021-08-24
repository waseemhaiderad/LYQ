package lnq.com.lnq.fragments.registeration.forgotemail;


import android.app.Dialog;
import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentForgotEmailBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForgotEmail extends Fragment {

    //    Android fields....
    private FragmentForgotEmailBinding forgotEmailBinding;
    private Animation slideUpAnimation, slideDownAnimation;
    private Unregistrar unregister;
    private ForgotEmailClickListener forgotEmailClickListener;

    //    Api fields....
    private Call<RegisterLoginMainObject> callForgotEmail;
    AppCompatImageView imageViewBackTopBar;
    private boolean codeResend = false;
    private int countryCodeSelector = 1;

    private Call<RegisterLoginMainObject> callPhoneVerification;
    private Call<RegisterLoginMainObject> callIsPhoneUnique;

    //    Firebase fields....
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallback;
    private FirebaseAuth mAuth;
    //    Font fields....
    private FontUtils fontUtils;


    public FragmentForgotEmail() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        forgotEmailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_email, container, false);
        return forgotEmailBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setCustomFont();
        CardView topBarLayout = forgotEmailBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.send_code);
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
//        Loading animation....
        slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

//        Setting event listener using binding....
        forgotEmailClickListener = new ForgotEmailClickListener(getActivity());
        forgotEmailBinding.setClickListener(forgotEmailClickListener);

        forgotEmailBinding.countryCodePicker.registerCarrierNumberEditText(forgotEmailBinding.editTextPhone);
        forgotEmailBinding.countryCodePicker1.registerCarrierNumberEditText( forgotEmailBinding.editTextPhone1);

//        Registering event listener for keyboard visibility....
        registerEventForKeyboardVisibility();

        ValidUtils.textViewGradientColor(forgotEmailBinding.textViewTroubleLogin1);
        ValidUtils.textViewGradientColor(forgotEmailBinding.textViewTroubleLogin);
        ValidUtils.textViewGradientColor(forgotEmailBinding.textViewEnterEmail1);
        ValidUtils.textViewGradientColor(forgotEmailBinding.textViewEnterEmail);

        forgotEmailBinding.countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                if (countryCodeSelector == 0) {
                    forgotEmailBinding.countryCodePicker1.setFullNumber(forgotEmailBinding.countryCodePicker.getFullNumber());
                }
            }
        });

        forgotEmailBinding.countryCodePicker1.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                if (countryCodeSelector == 1) {
                    forgotEmailBinding.countryCodePicker.setFullNumber(forgotEmailBinding.countryCodePicker1.getFullNumber());
                }
            }
        });

        forgotEmailBinding.countryCodePicker.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
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

        forgotEmailBinding.countryCodePicker1.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
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


    }



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
                        forgotEmailBinding.countryCodePicker.getFullNumberWithPlus(),
                        0,
                        TimeUnit.SECONDS,
                        getActivity(),
                        phoneAuthCallback);
                break;
        }

    }

    //    Method to set custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewBoldFont(forgotEmailBinding.textViewTroubleLogin);
        fontUtils.setTextViewRegularFont(forgotEmailBinding.textViewEnterEmail);
        fontUtils.setEditTextRegularFont(forgotEmailBinding.editTextFirstName);
        fontUtils.setEditTextRegularFont(forgotEmailBinding.editTextLastName);
        fontUtils.setEditTextRegularFont(forgotEmailBinding.editTextPhone);
        fontUtils.setTextViewMedium(forgotEmailBinding.clearTextViewSendEmail);


        fontUtils.setTextViewBoldFont(forgotEmailBinding.textViewTroubleLogin1);
        fontUtils.setTextViewRegularFont(forgotEmailBinding.textViewEnterEmail1);
        fontUtils.setEditTextRegularFont(forgotEmailBinding.editTextFirstName1);
        fontUtils.setEditTextRegularFont(forgotEmailBinding.editTextLastName1);
        fontUtils.setEditTextRegularFont(forgotEmailBinding.editTextPhone1);
        fontUtils.setTextViewMedium(forgotEmailBinding.clearTextViewSendEmail1);
    }

    private void registerEventForKeyboardVisibility() {
        unregister = KeyboardVisibilityEvent.registerEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            if (forgotEmailBinding.editTextFirstName.hasFocus()) {
                                changeFocus(forgotEmailBinding.editTextFirstName1);
                            } else if (forgotEmailBinding.editTextLastName.hasFocus()) {
                                changeFocus(forgotEmailBinding.editTextLastName1);
                            } else if (forgotEmailBinding.editTextPhone.hasFocus()) {
                                changeFocus(forgotEmailBinding.editTextPhone1);
                            }
                            forgotEmailBinding.mRoot1.startAnimation(slideUpAnimation);
                            toggleVisibilityOfRoot(forgotEmailBinding.mRoot1, forgotEmailBinding.mRoot);
                        } else {
                            forgotEmailBinding.editTextFirstName.setText(forgotEmailBinding.editTextFirstName1.getText().toString());
                            forgotEmailBinding.editTextLastName.setText(forgotEmailBinding.editTextLastName1.getText().toString());
                            forgotEmailBinding.editTextPhone.setText(forgotEmailBinding.editTextPhone1.getText().toString());
                            if (forgotEmailBinding.editTextFirstName1.hasFocus()) {
                                changeFocus(forgotEmailBinding.editTextFirstName);
                            } else if (forgotEmailBinding.editTextLastName1.hasFocus()) {
                                changeFocus(forgotEmailBinding.editTextLastName);
                            } else if (forgotEmailBinding.editTextPhone1.hasFocus()) {
                                changeFocus(forgotEmailBinding.editTextPhone);
                            }
                            toggleVisibilityOfRoot(forgotEmailBinding.mRoot, forgotEmailBinding.mRoot1);
                            forgotEmailBinding.mRoot.startAnimation(slideDownAnimation);
                        }
                    }
                });
    }

    //    Method to toggle visibility of view groups....
    public void toggleVisibilityOfRoot(ViewGroup viewGroupShow, ViewGroup viewGroupHide) {
        viewGroupShow.setVisibility(View.VISIBLE);
        viewGroupHide.setVisibility(View.GONE);
    }

    //    Method to change focus of edit texts....
    private void changeFocus(EditText editText) {
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
    }

    //    Cancelling http request if in process...
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callForgotEmail != null && callForgotEmail.isExecuted()) {
            callForgotEmail.cancel();
        }
        if (unregister != null) {
            unregister.unregister();
        }
    }

    //    Method to validate user data....
    private void fnValidateData(String firstName, String lastName, String phoneNumber, String buttonNumber) {
        if (!((MainActivity) getActivity()).fnIsisOnline()) {
            forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
            forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            return;
        }
        if (firstName.trim().isEmpty()) {
            forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
            forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_first_name));
        } else if (lastName.trim().isEmpty()) {
            forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
            forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_last_name));
        } else if (phoneNumber.isEmpty()) {
            forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
            forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_phone_number));
        }else if (buttonNumber.equals("0") ? !forgotEmailBinding.countryCodePicker.isValidFullNumber() : !forgotEmailBinding.countryCodePicker1.isValidFullNumber()) {
            forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
            forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_phone_number));
        } else {
            reqIsPhoneUnique(buttonNumber.equals("0") ? forgotEmailBinding.countryCodePicker.getFullNumberWithPlus() : forgotEmailBinding.countryCodePicker1.getFullNumberWithPlus());
            reqForgotEmail(firstName, lastName, phoneNumber);
        }
    }

    private void reqVerificationStatus() {
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(forgotEmailBinding.mRoot);
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        callPhoneVerification = Api.WEB_SERVICE.phoneVerification(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), forgotEmailBinding.editTextFirstName.getText().toString().trim(), forgotEmailBinding.editTextLastName.getText().toString().trim(), forgotEmailBinding.countryCodePicker.getFullNumberWithPlus(), EndpointKeys.PHONE, "");
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

    //    Method to request api to check if number is unique or not....
    private void reqIsPhoneUnique(final String phoneNumber) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), forgotEmailBinding.getRoot());
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        callIsPhoneUnique = Api.WEB_SERVICE.isPhoneUnique(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), phoneNumber);
//        callIsPhoneUnique = Api.WEB_SERVICE.isPhoneUnique(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), phoneNumber);
        callIsPhoneUnique.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
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

    //    Method to request forgot email api....
    private void reqForgotEmail(final String firstName, final String lastName, final String phoneNumber) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        callForgotEmail = Api.WEB_SERVICE.forgotEmail(EndpointKeys.X_API_KEY, firstName, lastName, phoneNumber);
//        callForgotEmail = Api.WEB_SERVICE.forgotEmail(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), firstName, lastName, phoneNumber);
        callForgotEmail.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                forgotEmailBinding.clearTextViewSendEmail2.stopAnimation();
                forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
                forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
                forgotEmailBinding.clearTextViewSendEmail2.revertAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            reqVerificationStatus();
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.EMAIL_SENT_TYPE, Constants.FORGOT_EMAIL);
                            bundle.putString(EndpointKeys.FIRST_NAME, firstName);
                            bundle.putString(EndpointKeys.LAST_NAME, lastName);
                            bundle.putString(EndpointKeys.PHONE, phoneNumber);
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EMAIL_SENT, true, bundle);
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
                forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
                forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
                forgotEmailBinding.clearTextViewSendEmail2.revertAnimation();
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

    public class ForgotEmailClickListener {

        private Context context;

        ForgotEmailClickListener(Context context) {
            this.context = context;
        }

        public void onBackClick(View view) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(forgotEmailBinding.mRoot);
            getActivity().onBackPressed();
        }

        public void onSendEmailClick(View view) {
            if (((MainActivity) getActivity()).progressDialog.isShowing()) {
                return;
            }
            if (!((MainActivity) getActivity()).fnIsisOnline()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                return;
            }
            switch (view.getId()) {
                case R.id.clearTextViewSendEmail:
                    ValidUtils.hideKeyboardFromFragment(getContext(), forgotEmailBinding.getRoot());
                    forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.INVISIBLE);
                    forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.VISIBLE);
                    forgotEmailBinding.clearTextViewSendEmail2.startAnimation();
                    fnValidateData(forgotEmailBinding.editTextFirstName.getText().toString(), forgotEmailBinding.editTextLastName.getText().toString(), forgotEmailBinding.editTextPhone.getText().toString().trim(), "0");
                    break;
                case R.id.clearTextViewSendEmail1:
                    ValidUtils.hideKeyboardFromFragment(getContext(), forgotEmailBinding.getRoot());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            forgotEmailBinding.clearTextViewSendEmail.setVisibility(View.INVISIBLE);
                            forgotEmailBinding.clearTextViewSendEmail2.setVisibility(View.VISIBLE);
                            forgotEmailBinding.clearTextViewSendEmail2.startAnimation();
                            fnValidateData(forgotEmailBinding.editTextFirstName1.getText().toString(), forgotEmailBinding.editTextLastName1.getText().toString(), forgotEmailBinding.editTextPhone1.getText().toString().trim(), "1");
                        }
                    }, 400);
                    break;
            }
        }

        public void onRootClick(View view) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(forgotEmailBinding.mRoot);
        }
    }

}
