package lnq.com.lnq.fragments.registeration;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.math.BigInteger;
import java.security.SecureRandom;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentEmailVerificationBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.registeration.phoneverification.FragmentPhoneVerificationEnterCode;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.ForgetPasswordNewModel;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentEmailVerification extends Fragment implements View.OnClickListener {

    private FragmentEmailVerificationBinding emailVerificationBinding;
    private static SecureRandom SECURE_RANDOM = new SecureRandom();

    //        Api fieds
    private Call<ForgetPasswordNewModel> mCallForgotPasswordNew;
    private Call<RegisterLoginMainObject> mCallForgotEmail;
    private Call<RegisterLoginMainObject> mCallForgotPassword;

    String email;

    //    Font fields....
    private FontUtils fontUtils;
    AppCompatImageView imageViewBackTopBar;

    public FragmentEmailVerification() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        emailVerificationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_email_verification, container, false);
        return emailVerificationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setCustomFonts();
        CardView topBarLayout = emailVerificationBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.reset_password);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void setCustomFonts() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewBoldFont(emailVerificationBinding.textViewVerifyPhoneNo);
        fontUtils.setEditTextRegularFont(emailVerificationBinding.editTextCode1);
        fontUtils.setEditTextRegularFont(emailVerificationBinding.editTextCode2);
        fontUtils.setEditTextRegularFont(emailVerificationBinding.editTextCode3);
        fontUtils.setEditTextRegularFont(emailVerificationBinding.editTextCode4);
        fontUtils.setEditTextRegularFont(emailVerificationBinding.editTextCode5);
        fontUtils.setEditTextRegularFont(emailVerificationBinding.editTextCode6);
        fontUtils.setTextViewRegularFont(emailVerificationBinding.textViewEnterDigitCode);
        fontUtils.setTextViewMedium(emailVerificationBinding.clearTextViewResetPassword);
    }

    private void init() {

        emailVerificationBinding.clearTextViewResetPassword.setOnClickListener(this);
        emailVerificationBinding.buttonResendEmailNew.setOnClickListener(this);

        emailVerificationBinding.editTextCode1.requestFocus();
        ((MainActivity) getActivity()).fnShowKeyboardFrom(emailVerificationBinding.editTextCode1);

        if (!emailVerificationBinding.editTextCode6.getText().toString().isEmpty()) {

            if (emailVerificationBinding.editTextCode1.getText().toString().isEmpty() && emailVerificationBinding.editTextCode2.getText().toString().isEmpty() &&
                    emailVerificationBinding.editTextCode3.getText().toString().isEmpty() && emailVerificationBinding.editTextCode4.getText().toString().isEmpty() &&
                    emailVerificationBinding.editTextCode5.getText().toString().isEmpty() && emailVerificationBinding.editTextCode6.getText().toString().isEmpty()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.enter_verification_code));
                return;
            }
            ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        }

        setListenersForTextWatcher();
        email = getArguments().getString(EndpointKeys.EMAIL);

        ValidUtils.textViewGradientColor(emailVerificationBinding.textViewVerifyPhoneNo);
        ValidUtils.textViewGradientColor(emailVerificationBinding.textViewEnterDigitCode);
        ValidUtils.buttonGradientColor(emailVerificationBinding.buttonResendEmailNew);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ValidUtils.hideKeyboardFromFragment(getActivity(), emailVerificationBinding.editTextCode1);
    }

    //    Method to request api for Change Password....
    private void reqForgotPassword(final String passtoken, final String password, final String confirmPassword) {
//        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        emailVerificationBinding.clearTextViewResetPassword.setVisibility(View.INVISIBLE);
        emailVerificationBinding.clearTextViewLogin2.setVisibility(View.VISIBLE);
        emailVerificationBinding.clearTextViewLogin2.startAnimation();
        mCallForgotPasswordNew = Api.WEB_SERVICE.forgotPasswordNew(EndpointKeys.X_API_KEY, passtoken, password, confirmPassword);
        mCallForgotPasswordNew.enqueue(new Callback<ForgetPasswordNewModel>() {
            @Override
            public void onResponse(Call<ForgetPasswordNewModel> call, Response<ForgetPasswordNewModel> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                emailVerificationBinding.clearTextViewLogin2.stopAnimation();
                emailVerificationBinding.clearTextViewResetPassword.setVisibility(View.VISIBLE);
                emailVerificationBinding.clearTextViewLogin2.setVisibility(View.GONE);
                emailVerificationBinding.clearTextViewLogin2.revertAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            ((MainActivity) getActivity()).showMessageDialog("success", "Password Changed");
                            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.LOGIN, true, null);
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", "Error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordNewModel> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                emailVerificationBinding.clearTextViewResetPassword.setVisibility(View.VISIBLE);
                emailVerificationBinding.clearTextViewLogin2.setVisibility(View.GONE);
                emailVerificationBinding.clearTextViewLogin2.revertAnimation();
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
    private void reqResendEmail(String email, String link) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        mCallForgotPassword = Api.WEB_SERVICE.forgotPassword(EndpointKeys.X_API_KEY, email, link);
//        mCallForgotPassword = Api.WEB_SERVICE.forgotPassword(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), email);
        mCallForgotPassword.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
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
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearTextViewResetPassword:
                if (emailVerificationBinding.editTextCode1.getText().toString().isEmpty() && emailVerificationBinding.editTextCode2.getText().toString().isEmpty() &&
                        emailVerificationBinding.editTextCode3.getText().toString().isEmpty() && emailVerificationBinding.editTextCode4.getText().toString().isEmpty() &&
                        emailVerificationBinding.editTextCode5.getText().toString().isEmpty() && emailVerificationBinding.editTextCode6.getText().toString().isEmpty()) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.enter_verification_code));
                    return;
                }
                ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);

                reqForgotPassword(emailVerificationBinding.editTextCode1.getText().toString() + emailVerificationBinding.editTextCode2.getText().toString() + emailVerificationBinding.editTextCode3.getText().toString() + emailVerificationBinding.editTextCode4.getText().toString() + emailVerificationBinding.editTextCode5.getText().toString() + emailVerificationBinding.editTextCode6.getText().toString(), emailVerificationBinding.editTextPassword.getText().toString(), emailVerificationBinding.editTextPasswordConfirm.getText().toString());
                break;
            case R.id.buttonResendEmailNew:
                String hyperlink = nextSessionId();
                String redriectLink = "http://lnq.demo.leadconcept.net/" + hyperlink;
                reqResendEmail(email, redriectLink);
                break;

        }
    }

    public static String nextSessionId() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }

    private void setListenersForTextWatcher() {
        emailVerificationBinding.editTextCode1.addTextChangedListener(new EditTextVerificationCodeListener(emailVerificationBinding.editTextCode2, emailVerificationBinding.editTextCode1));
        emailVerificationBinding.editTextCode2.addTextChangedListener(new EditTextVerificationCodeListener(emailVerificationBinding.editTextCode3, emailVerificationBinding.editTextCode2));
        emailVerificationBinding.editTextCode3.addTextChangedListener(new EditTextVerificationCodeListener(emailVerificationBinding.editTextCode4, emailVerificationBinding.editTextCode3));
        emailVerificationBinding.editTextCode4.addTextChangedListener(new EditTextVerificationCodeListener(emailVerificationBinding.editTextCode5, emailVerificationBinding.editTextCode4));
        emailVerificationBinding.editTextCode5.addTextChangedListener(new EditTextVerificationCodeListener(emailVerificationBinding.editTextCode6, emailVerificationBinding.editTextCode5));
    }


    class EditTextVerificationCodeListener implements TextWatcher {

        private EditText editTextCodeTo, editTextFrom;

        EditTextVerificationCodeListener(EditText editTextCodeTo, EditText editTextFrom) {
            this.editTextCodeTo = editTextCodeTo;
            this.editTextFrom = editTextFrom;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextFrom.getText().toString().length() == 1 && editTextCodeTo.getText().toString().length() == 0) {
                editTextCodeTo.requestFocus();
            }
        }
    }


}