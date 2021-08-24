package lnq.com.lnq.fragments.registeration;


import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.SecureRandom;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentEmailSentBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentEmailSent extends Fragment {

    //    Android fields....
    private FragmentEmailSentBinding emailSentBinding;
    private EmailSendClickListener emailSendClickListener;
    private static SecureRandom SECURE_RANDOM = new SecureRandom();

    //    Api fields....
    private Call<RegisterLoginMainObject> mCallForgotEmail;
    private Call<RegisterLoginMainObject> mCallForgotPassword;

    //    Instance fields....
    private String emailSentType, email, firstName, lastName, phone;
    //    Font fields....
    private FontUtils fontUtils;

    public FragmentEmailSent() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        emailSentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_email_sent, container, false);
        return emailSentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setCustomFonts();
    }

    public void setCustomFonts() {
        fontUtils = FontUtils.getFontUtils(getActivity());

        fontUtils.setTextViewBoldFont(emailSentBinding.textViewEmailSent);
        fontUtils.setTextViewRegularFont(emailSentBinding.textViewEmailSentDes);
        fontUtils.setTextViewRegularFont(emailSentBinding.clearTextViewSignIn);
        fontUtils.setButtonMedium(emailSentBinding.buttonResendEmail);
    }

    private void init() {
//        Setting events using binding....
        emailSendClickListener = new EmailSendClickListener(getActivity());
        emailSentBinding.setClickListener(emailSendClickListener);

        if (getArguments() != null) {
            emailSentType = getArguments().getString(EndpointKeys.EMAIL_SENT_TYPE);
            email = getArguments().getString(EndpointKeys.EMAIL);
            firstName = getArguments().getString(EndpointKeys.FIRST_NAME);
            lastName = getArguments().getString(EndpointKeys.LAST_NAME);
            phone = getArguments().getString(EndpointKeys.PHONE);

            emailSentBinding.textViewEmailSentDes.setText("An email has been sent to " + email + " with a link to reset your password.");
        }

        ValidUtils.textViewGradientColor(emailSentBinding.textViewEmailSent);
        ValidUtils.textViewGradientColor(emailSentBinding.textViewEmailSentDes);
        ValidUtils.buttonGradientColor(emailSentBinding.mBtnSignUp);
    }

    //    Cancelling http requests if in process....
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCallForgotEmail != null && mCallForgotEmail.isExecuted()) {
            mCallForgotEmail.cancel();
        }
        if (mCallForgotPassword != null && mCallForgotPassword.isExecuted()) {
            mCallForgotPassword.cancel();
        }
    }

    //    Method to request api for forgot email....
    private void reqForgotEmail(final String firstName, final String lastName, final String phoneNumber) {
//        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        emailSentBinding.buttonResendEmail.setVisibility(View.INVISIBLE);
        emailSentBinding.buttonResendEmail2.setVisibility(View.VISIBLE);
        emailSentBinding.buttonResendEmail2.startAnimation();
        mCallForgotEmail = Api.WEB_SERVICE.forgotEmail(EndpointKeys.X_API_KEY, firstName, lastName, phoneNumber);
//        mCallForgotEmail = Api.WEB_SERVICE.forgotEmail(EndpointKeys.X_API_KEY,DateUtils.getCurrentTime(), firstName, lastName, phoneNumber);
        mCallForgotEmail.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                emailSentBinding.buttonResendEmail2.stopAnimation();
                emailSentBinding.buttonResendEmail.setVisibility(View.VISIBLE);
                emailSentBinding.buttonResendEmail.setVisibility(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            emailSentBinding.textViewEmailSentDes.setText(response.body().getMessage());
//                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
                            emailSentBinding.textViewEmailSentDes.setText(response.body().getMessage());
//                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                emailSentBinding.buttonResendEmail.setVisibility(View.VISIBLE);
                emailSentBinding.buttonResendEmail.setVisibility(View.GONE);
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

    //    Method to request api for forgot password....
    private void reqForgotPassword(String email, String hyperLink) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        emailSentBinding.buttonResendEmail.setVisibility(View.INVISIBLE);
        emailSentBinding.buttonResendEmail2.setVisibility(View.VISIBLE);
        emailSentBinding.buttonResendEmail2.startAnimation();
        mCallForgotPassword = Api.WEB_SERVICE.forgotPassword(EndpointKeys.X_API_KEY, email, hyperLink);
//        mCallForgotPassword = Api.WEB_SERVICE.forgotPassword(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), email);
        mCallForgotPassword.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                emailSentBinding.buttonResendEmail2.stopAnimation();
                emailSentBinding.buttonResendEmail.setVisibility(View.VISIBLE);
                emailSentBinding.buttonResendEmail2.setVisibility(View.GONE);
                emailSentBinding.buttonResendEmail2.revertAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            emailSentBinding.textViewEmailSentDes.setText(response.body().getMessage());
                            break;
                        case 0:
//                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            emailSentBinding.textViewEmailSentDes.setText(response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                emailSentBinding.buttonResendEmail.setVisibility(View.VISIBLE);
                emailSentBinding.buttonResendEmail2.setVisibility(View.GONE);
                emailSentBinding.buttonResendEmail2.revertAnimation();
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        LnqApplication.getInstance().snakeBar(((MainActivity) getActivity()).mBind.mRoot, "Wifi Or Internet Is Not Connected", R.color.colorError);
                    } else {
                        LnqApplication.getInstance().snakeBar(((MainActivity) getActivity()).mBind.mRoot, error.getMessage() + "", R.color.colorError);
                    }
                } else {
                    LnqApplication.getInstance().snakeBar(((MainActivity) getActivity()).mBind.mRoot, "Wifi Or Internet Is Not Connected", R.color.colorError);
                }
            }
        });
    }

    public class EmailSendClickListener {
        private Context context;

        EmailSendClickListener(Context context) {
            this.context = context;
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void onSignInClick(View view) {
            ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.LOGIN, false, null);
            if (context != null) {
                for (int i = 0; i < ((MainActivity) getActivity()).fragmentManager.getBackStackEntryCount(); ++i) {
                    ((MainActivity) getActivity()).fragmentManager.popBackStack();
                }
            }
        }

        public void onSignUpClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.SIGN_UP, false, null);
            if (context != null) {
                for (int i = 0; i < ((MainActivity) getActivity()).fragmentManager.getBackStackEntryCount(); ++i) {
                    ((MainActivity) getActivity()).fragmentManager.popBackStack();
                }
            }
        }

        public void onSendEmailClick(View view) {
            if (((MainActivity) getActivity()).progressDialog.isShowing()) {
                return;
            }
            if (!((MainActivity) getActivity()).fnIsisOnline()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                return;
            }
            if (emailSentType.equals(Constants.FORGOT_EMAIL)) {
                reqForgotEmail(firstName, lastName, phone);
            } else {
                String hyperlink = nextSessionId();
                String redriectLink = "http://lnq.demo.leadconcept.net/" + hyperlink;
                reqForgotPassword(email, redriectLink);
            }
        }
    }

    public static String nextSessionId() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }

}
