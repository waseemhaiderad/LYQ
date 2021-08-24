package lnq.com.lnq.fragments.registeration;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentSignUpBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.ValidUtils;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 *    Code perfected by shariq ansari
 */
public class FragmentSignUp extends Fragment {

    //    Android fields....
    private FragmentSignUpBinding signUpBinding;
    private Animation slideUpAnimation, slideDownAnimation;
    private Unregistrar unregister;
    private SignUpClickListeners signUpClickListeners;

    //Animation
    Animation animShake;

    //    Font fields....
    private FontUtils fontUtils;

    //    Api fields....
    private Call<RegisterLoginMainObject> callSignUp;

    public FragmentSignUp() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        signUpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        return signUpBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        //Animation
        animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    }

    private void init() {
        if (getActivity() != null) {
            if (getArguments() != null){
                String email = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "");
                String password = LnqApplication.getInstance().sharedPreferences.getString("backPassword", "");
                signUpBinding.editTextEmail.setText(email);
                signUpBinding.editTextEmail1.setText(email);
                signUpBinding.editTextPassword.setText(password);
                signUpBinding.editTextPassword1.setText(password);
                signUpBinding.editTextConfirmPassword.setText(password);
                signUpBinding.editTextConfirmPassword1.setText(password);
            }
//        Loading animations....
            slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            slideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

//            Setting custom font....
            setCustomFont();

//        All event listeners....
            signUpClickListeners = new SignUpClickListeners(getActivity());
            signUpBinding.setSignUpClick(signUpClickListeners);

            signUpBinding.editTextEmail1.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        signUpBinding.textInputLayoutEmail.setError("");
                        signUpBinding.textInputLayoutEmail1.setError("");
                    }
                }
            });
            signUpBinding.editTextPassword1.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        signUpBinding.textInputLayoutPassword.setError("");
                        signUpBinding.textInputLayoutPassword1.setError("");
                    }
                }
            });

            signUpBinding.editTextConfirmPassword1.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        signUpBinding.textInputLayoutConfirmPassword.setError("");
                        signUpBinding.textInputLayoutConfirmPassword1.setError("");
                    }
                }
            });
        }

        ValidUtils.textViewGradientColor(signUpBinding.textViewSignInYourAccount);
        ValidUtils.textViewGradientColor(signUpBinding.textViewSignInYourAccount1);
        ValidUtils.textViewGradientColor(signUpBinding.textViewSignIn);
        ValidUtils.textViewGradientColor(signUpBinding.textViewSignIn1);
        ValidUtils.textViewGradientColor(signUpBinding.textViewByAccept);
        ValidUtils.textViewGradientColor(signUpBinding.textViewByAccept1);
        ValidUtils.textViewGradientColor(signUpBinding.textViewTermsServices);
        ValidUtils.textViewGradientColor(signUpBinding.textViewTermsServices1);
        ValidUtils.textViewGradientColor(signUpBinding.textViewAnd);
        ValidUtils.textViewGradientColor(signUpBinding.textViewAnd1);
    }

    //    Method to set custom fonts to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewBoldFont(signUpBinding.textViewSignInYourAccount);
        fontUtils.setTextViewBoldFont(signUpBinding.textViewSignInYourAccount1);
        fontUtils.setEditTextRegularFont(signUpBinding.editTextEmail);
        fontUtils.setEditTextRegularFont(signUpBinding.editTextEmail1);
        fontUtils.setEditTextRegularFont(signUpBinding.editTextPassword);
        fontUtils.setEditTextRegularFont(signUpBinding.editTextPassword1);
        fontUtils.setEditTextRegularFont(signUpBinding.editTextConfirmPassword);
        fontUtils.setEditTextRegularFont(signUpBinding.editTextConfirmPassword1);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewSignIn);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewSignIn1);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewByAccept);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewByAccept1);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewTermsServices);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewTermsServices1);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewAnd);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewAnd1);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewPrivacyPolicy);
        fontUtils.setTextViewRegularFont(signUpBinding.textViewPrivacyPolicy1);
//        fontUtils.setTextViewMedium(signUpBinding.clearTextViewCreateAccount);
//        fontUtils.setTextViewMedium(signUpBinding.clearTextViewCreateAccount1);
    }

    //    Method to change the focus of edit text....
    private void changeFocusOfEditTexts(EditText editText) {
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
    }

    //    Registering event of keyboard visibility....
    @Override
    public void onStart() {
        super.onStart();
        unregister = KeyboardVisibilityEvent.registerEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            if (signUpBinding.editTextEmail.hasFocus()) {
                                changeFocusOfEditTexts(signUpBinding.editTextEmail1);
                            } else if (signUpBinding.editTextPassword.hasFocus()) {
                                changeFocusOfEditTexts(signUpBinding.editTextPassword1);
                            } else {
                                changeFocusOfEditTexts(signUpBinding.editTextConfirmPassword1);
                            }
                            signUpBinding.mRoot1.startAnimation(slideUpAnimation);
                            toggleVisibilityOfRoot(signUpBinding.mRoot1, signUpBinding.mRoot);
                        } else {
                            signUpBinding.editTextEmail.setText(signUpBinding.editTextEmail1.getText().toString());
                            signUpBinding.editTextPassword.setText(signUpBinding.editTextPassword1.getText().toString());
                            signUpBinding.editTextConfirmPassword.setText(signUpBinding.editTextConfirmPassword1.getText().toString());
                            if (signUpBinding.editTextEmail1.hasFocus()) {
                                changeFocusOfEditTexts(signUpBinding.editTextEmail);
                            } else if (signUpBinding.editTextPassword1.hasFocus()) {
                                changeFocusOfEditTexts(signUpBinding.editTextPassword);
                            } else {
                                changeFocusOfEditTexts(signUpBinding.editTextConfirmPassword);
                            }
                            toggleVisibilityOfRoot(signUpBinding.mRoot, signUpBinding.mRoot1);
                            signUpBinding.mRoot.startAnimation(slideDownAnimation);
                        }
                    }
                });
    }

    //    Method to toggle visibility of view groups....
    public void toggleVisibilityOfRoot(ViewGroup viewGroupShow, ViewGroup viewGroupHide) {
        viewGroupShow.setVisibility(View.VISIBLE);
        viewGroupHide.setVisibility(View.GONE);
    }

    //    Unregistering event for keyboard visibility....
    @Override
    public void onStop() {
        super.onStop();
        if (unregister != null) {
            unregister.unregister();
        }
    }

    //    Cancelling http request if currently is working....
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callSignUp != null && callSignUp.isExecuted()) {
            callSignUp.cancel();
        }
    }

    //    Method to validate user data....
    private void validateData(String email, String password, String confirmPassword) {
        if (!ValidUtils.isNetworkAvailable(getActivity())) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
            signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
            return;
        }
        if (email.isEmpty()) {
            signUpBinding.textInputLayoutEmail.setError(getResources().getString(R.string.email_empty));
            signUpBinding.textInputLayoutEmail1.setError(getResources().getString(R.string.email_empty));
            signUpBinding.editTextEmail1.startAnimation(animShake);
            signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
            signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
        } else if (!ValidUtils.validateEmail(email)) {
            signUpBinding.textInputLayoutEmail.setError(getResources().getString(R.string.please_enter_a_valid_email_address));
            signUpBinding.textInputLayoutEmail1.setError(getResources().getString(R.string.please_enter_a_valid_email_address));
            signUpBinding.editTextEmail1.startAnimation(animShake);
            signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
            signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
        } else if (password.isEmpty()) {
            signUpBinding.textInputLayoutPassword.setError(getResources().getString(R.string.password_empty));
            signUpBinding.textInputLayoutPassword1.setError(getResources().getString(R.string.password_empty));
            signUpBinding.editTextPassword1.startAnimation(animShake);
            signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
            signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
        } else if (confirmPassword.isEmpty()) {
            signUpBinding.textInputLayoutConfirmPassword.setError(getResources().getString(R.string.password_empty));
            signUpBinding.textInputLayoutConfirmPassword1.setError(getResources().getString(R.string.password_empty));
            signUpBinding.editTextConfirmPassword1.startAnimation(animShake);
            signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
            signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
        } else if (!password.equals(confirmPassword)) {
            signUpBinding.textInputLayoutConfirmPassword.setError(getResources().getString(R.string.password_did_not_matched));
            signUpBinding.textInputLayoutConfirmPassword1.setError(getResources().getString(R.string.password_did_not_matched));
            signUpBinding.editTextConfirmPassword1.startAnimation(animShake);
            signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
            signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
        } else {
            reqSignUp(email, password);
        }
    }

    //   Method to call sign up api....
    private void reqSignUp(String email, final String password) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
//        callSignUp = Api.WEB_SERVICE.signup(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), email, password);
        callSignUp = Api.WEB_SERVICE.signup(EndpointKeys.X_API_KEY, email, password);
        callSignUp.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                signUpBinding.clearTextViewCreateAccount2.stopAnimation();
                signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
                signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
                signUpBinding.clearTextViewCreateAccount2.revertAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            switch (response.body().getSignup().getVerification_status()) {
                                case EndpointKeys.SIGN_UP:
                                    LnqApplication.getInstance().editor.putString("backPassword", password);

                                    LnqApplication.getInstance().editor.putString(EndpointKeys.ID, response.body().getSignup().getId());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_EMAIL, response.body().getSignup().getUser_email());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_TYPE, response.body().getSignup().getUser_type());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_STATUS, response.body().getSignup().getVerification_status());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FNAME, response.body().getSignup().getUser_fname());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_LNAME, response.body().getSignup().getUser_lname());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_AVATAR, response.body().getSignup().getUser_avatar());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PHONE, response.body().getSignup().getUser_phone());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIRTHDAY, response.body().getSignup().getUser_birthday());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIO, response.body().getSignup().getUser_bio());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_STATUS_MESSAGE, response.body().getSignup().getUser_status_msg());
                                    LnqApplication.getInstance().editor.putBoolean(EndpointKeys.SHOW_NOTIFICATION_DIALOG, true).apply();
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASSWORD, response.body().getSignup().getUser_pass());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.LAST_LOGIN, response.body().getSignup().getLastLogin());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASS, password);
                                    LnqApplication.getInstance().editor.putBoolean(EndpointKeys.IS_USER_LOGGED_IN, true);
                                    LnqApplication.getInstance().editor.apply();
                                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.VERIFICATION_ONE, false, null);
                                    ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    break;
                            }
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
                signUpBinding.clearTextViewCreateAccount.setVisibility(View.VISIBLE);
                signUpBinding.clearTextViewCreateAccount2.setVisibility(View.GONE);
                signUpBinding.clearTextViewCreateAccount2.revertAnimation();
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

    public class SignUpClickListeners {

        private Context context;

        public SignUpClickListeners(Context context) {
            this.context = context;
        }

        public void onSignInClick(View view) {
            if (unregister != null) {
                unregister.unregister();
            }
            ValidUtils.hideKeyboardFromFragment(getActivity(), signUpBinding.getRoot());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (((MainActivity) getActivity()).fragmentManager.getBackStackEntryCount() == 0) {
                        ((MainActivity) getActivity()).fnLoadFragReplace(Constants.LOGIN, false, null);
                    } else {
                        (getActivity()).onBackPressed();
                    }
                }
            }, 100);
        }

        public void onTermsServicesClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), signUpBinding.getRoot());
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.TERMS_CONDITION, true, null);
        }

        public void onPrivacyPolicyClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), signUpBinding.getRoot());
            Bundle bundle = new Bundle();
            bundle.putString("privacy", "privacyText");
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.TERMS_CONDITION, true, bundle);
        }

        public void onCreateAccountClick(View view) {
            switch (view.getId()) {
                case R.id.clearTextViewCreateAccount:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ValidUtils.hideKeyboardFromFragment(getActivity(), signUpBinding.getRoot());
                        signUpBinding.clearTextViewCreateAccount.setVisibility(View.INVISIBLE);
                        signUpBinding.clearTextViewCreateAccount2.setVisibility(View.VISIBLE);
                        signUpBinding.clearTextViewCreateAccount2.startAnimation();
                        validateData(signUpBinding.editTextEmail.getText().toString().trim(), signUpBinding.editTextPassword.getText().toString().trim(), signUpBinding.editTextConfirmPassword.getText().toString().trim());
                    }
                    break;
                case R.id.clearTextViewCreateAccount1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ValidUtils.hideKeyboardFromFragment(getActivity(), signUpBinding.getRoot());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                signUpBinding.clearTextViewCreateAccount.setVisibility(View.INVISIBLE);
                                signUpBinding.clearTextViewCreateAccount2.setVisibility(View.VISIBLE);
                                signUpBinding.clearTextViewCreateAccount2.startAnimation();
                                validateData(signUpBinding.editTextEmail1.getText().toString().trim(), signUpBinding.editTextPassword1.getText().toString().trim(), signUpBinding.editTextConfirmPassword1.getText().toString().trim());
                            }
                        }, 400);
                    }
                    break;
            }
        }

        public void onRootClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), signUpBinding.getRoot());
        }

        public void onLnqLogoClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), signUpBinding.getRoot());
        }

        public void onShowHideClick(View view) {
            switch (view.getId()) {
                case R.id.textViewHideShow1:
                    if (signUpBinding.textViewHideShow1.getText().toString().equalsIgnoreCase("Show")) {
                        signUpBinding.editTextPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        signUpBinding.textViewHideShow1.setText("Hide");
                        signUpBinding.editTextPassword1.setSelection(signUpBinding.editTextPassword1.getText().length());
                    } else {
                        signUpBinding.textViewHideShow1.setText("Show");
                        signUpBinding.editTextPassword1.setTransformationMethod(new PasswordTransformationMethod());
                        signUpBinding.editTextPassword1.setSelection(signUpBinding.editTextPassword1.getText().length());
                    }
                    break;
                case R.id.textViewHideShow:
                    if (signUpBinding.textViewHideShow.getText().toString().equalsIgnoreCase("Show")) {
                        signUpBinding.editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        signUpBinding.textViewHideShow.setText("Hide");
                        signUpBinding.editTextPassword.setSelection(signUpBinding.editTextPassword.getText().length());
                    } else {
                        signUpBinding.textViewHideShow.setText("Show");
                        signUpBinding.editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                        signUpBinding.editTextPassword.setSelection(signUpBinding.editTextPassword.getText().length());
                    }
                    break;
                case R.id.textViewHideShowConfirm1:
                    if (signUpBinding.textViewHideShowConfirm1.getText().toString().equalsIgnoreCase("Show")) {
                        signUpBinding.editTextConfirmPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        signUpBinding.textViewHideShowConfirm1.setText("Hide");
                        signUpBinding.editTextConfirmPassword1.setSelection(signUpBinding.editTextConfirmPassword1.getText().length());
                    } else {
                        signUpBinding.textViewHideShowConfirm1.setText("Show");
                        signUpBinding.editTextConfirmPassword1.setTransformationMethod(new PasswordTransformationMethod());
                        signUpBinding.editTextConfirmPassword1.setSelection(signUpBinding.editTextConfirmPassword1.getText().length());
                    }
                    break;
                case R.id.textViewHideShowConfirm:
                    if (signUpBinding.textViewHideShowConfirm.getText().toString().equalsIgnoreCase("Show")) {
                        signUpBinding.editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        signUpBinding.textViewHideShowConfirm.setText("Hide");
                        signUpBinding.editTextConfirmPassword.setSelection(signUpBinding.editTextConfirmPassword.getText().length());
                    } else {
                        signUpBinding.textViewHideShowConfirm.setText("Show");
                        signUpBinding.editTextConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                        signUpBinding.editTextConfirmPassword.setSelection(signUpBinding.editTextConfirmPassword.getText().length());
                    }
                    break;
            }
        }

    }

}