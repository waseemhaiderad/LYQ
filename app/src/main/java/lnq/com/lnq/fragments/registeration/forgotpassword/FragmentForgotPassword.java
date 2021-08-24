package lnq.com.lnq.fragments.registeration.forgotpassword;


import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentForgotPasswordBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentForgotPassword extends Fragment {

    //    Android fields....
    private FragmentForgotPasswordBinding forgotPasswordBinding;
    private ForgotPasswordClickListeners forgotPasswordClickListeners;
    private static SecureRandom SECURE_RANDOM = new SecureRandom();

    //    Api fields....
    private Call<RegisterLoginMainObject> callForgotPassword;
    AppCompatImageView imageViewBackTopBar;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentForgotPassword() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        forgotPasswordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false);
        return forgotPasswordBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        CardView topBarLayout = forgotPasswordBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.forgot_password1);
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
//        Setting custom font....
        setCustomFont();

//        Setting events using binding....
        forgotPasswordClickListeners = new ForgotPasswordClickListeners(getActivity());
        forgotPasswordBinding.setClickListener(forgotPasswordClickListeners);

        ValidUtils.textViewGradientColor(forgotPasswordBinding.textViewTroubleLogIn);
        ValidUtils.textViewGradientColor(forgotPasswordBinding.textViewEnterEmail);
        ValidUtils.textViewGradientColor(forgotPasswordBinding.textViewForgotEmail);
    }

    //    Cancelling http request of in process....
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callForgotPassword != null && callForgotPassword.isExecuted()) {
            callForgotPassword.cancel();
        }
    }

    //    Method to set custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewBoldFont(forgotPasswordBinding.textViewTroubleLogIn);
        fontUtils.setTextViewRegularFont(forgotPasswordBinding.textViewEnterEmail);
        fontUtils.setEditTextRegularFont(forgotPasswordBinding.editTextEmail);
        fontUtils.setTextViewMedium(forgotPasswordBinding.clearTextViewSendEmail);
        fontUtils.setTextViewRegularFont(forgotPasswordBinding.textViewForgotEmail);
    }

    //    Method to request api for forgot password....
    private void reqForgotPassword(final String email, String link) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), forgotPasswordBinding.mRoot);
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        forgotPasswordBinding.clearTextViewSendEmail.setVisibility(View.INVISIBLE);
        forgotPasswordBinding.clearTextViewSendEmail2.setVisibility(View.VISIBLE);
        forgotPasswordBinding.clearTextViewSendEmail2.startAnimation();
        callForgotPassword = Api.WEB_SERVICE.forgotPassword(EndpointKeys.X_API_KEY, email, link);
//        callForgotPassword = Api.WEB_SERVICE.forgotPassword(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), email);
        callForgotPassword.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                forgotPasswordBinding.clearTextViewSendEmail2.stopAnimation();
                forgotPasswordBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
                forgotPasswordBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
                forgotPasswordBinding.clearTextViewSendEmail2.revertAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.EMAIL_SENT_TYPE, Constants.FORGOT_PASSWORD);
                            bundle.putString(EndpointKeys.EMAIL, email);
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EMAIL_VERIFICATION, true, bundle);
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
                forgotPasswordBinding.clearTextViewSendEmail.setVisibility(View.VISIBLE);
                forgotPasswordBinding.clearTextViewSendEmail2.setVisibility(View.GONE);
                forgotPasswordBinding.clearTextViewSendEmail2.revertAnimation();
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

    public class ForgotPasswordClickListeners {

        private Context context;

        ForgotPasswordClickListeners(Context context) {
            this.context = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onForgotPasswordClick(View view) {
            if (((MainActivity) getActivity()).progressDialog.isShowing()) {
                return;
            }
            if (!((MainActivity) getActivity()).fnIsisOnline()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                return;
            }
            String email = forgotPasswordBinding.editTextEmail.getText().toString().trim();
            if (email.isEmpty() || email.length() == 0) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.email_empty));
            } else if (!ValidUtils.validateEmail(email)) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_email));
            } else {
                String hyperlink = nextSessionId();
                LocalDateTime date = LocalDateTime.now();
                int seconds = date.toLocalTime().toSecondOfDay();
                seconds = seconds + 1800;
//                String redriectLink = "http://lnq.demo.leadconcept.net/"+hyperlink;
                String redriectLink = "https://lnq.leadconcept.info" + "?" + seconds;
                reqForgotPassword(email, redriectLink);
            }

        }

        public void onForgotEmailClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.FORGOT_EMAIL, true, null);
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

    }

    public static String nextSessionId() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }

}