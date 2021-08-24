package lnq.com.lnq.fragments.registeration;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.content.Loader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.databinding.FragmentLoginBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.registeration.Biomatric.BiometricCallbackPrint;
import lnq.com.lnq.fragments.registeration.Biomatric.BiometricManagerPrint;
import lnq.com.lnq.model.event_bus_models.EventBusLoginKeyboardUnregister;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.LogOut;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserSecondaryProfile;
import lnq.com.lnq.model.gson_converter_models.pushnotifications.PushNotificationMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.LogInData;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
    Code perfected by shariq ansari
*/
public class FragmentLogin extends Fragment {

    //    Android fields....
    private FragmentLoginBinding loginBinding;
    private Animation slideUpAnimation, slideDownAnimation;
    private Unregistrar unregister;
    private LogInClickListener logInClickListener;
    BiometricManagerPrint mBiometricManager;

    //    Api fields....
    private Call<RegisterLoginMainObject> callLogIn;
    private Call<PushNotificationMainObject> callNotification;
    private Call<LogOut> callLogOut;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private Response<RegisterLoginMainObject> response;
    private String password;

    Animation animShake;

    //    DataBase Repositry
    private MultiProfileRepositry multiProfileRepositry;

    private AlertDialog dialog;
    private String lastLoginId, lastLoginEmail, lastLoginPassword;

    public FragmentLogin() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return loginBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").isEmpty()) {

//            loginBinding.clearTextViewFingerPrint.setVisibility(View.INVISIBLE);
//            loginBinding.clearTextViewFingerPrint1.setVisibility(View.INVISIBLE);
            loginBinding.imageBiometric.setVisibility(View.INVISIBLE);
            loginBinding.imageBiometric1.setVisibility(View.INVISIBLE);

        } else {
//            loginBinding.clearTextViewFingerPrint.setVisibility(View.VISIBLE);
//            loginBinding.clearTextViewFingerPrint1.setVisibility(View.VISIBLE);
            loginBinding.imageBiometric.setVisibility(View.VISIBLE);
            loginBinding.imageBiometric1.setVisibility(View.VISIBLE);

        }

    }

    //   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.registered_user, menu);
        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        SampleApplication app = (SampleApplication)getContext();
//        menu.findItem(R.id.action_export_registration).setVisible(app.getRegistrationUpload() != null && app.getQRCodeGenerator() != null);
//        return true;
//    }


//    private void loadProfilePicture() {
//        final ImageView profileImageView = getActivity().findViewById(R.id.profileImage);
//        profileImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    profileImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                } else {
//                    profileImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//                final int width = profileImageView.getWidth();
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap colourBitmap = VerID.shared.getUserProfilePicture(user.getUserId());
//                        if (colourBitmap != null) {
//                            byte[] grayscale = VerID.shared.getPlatformUtils().bitmapToGrayscale(colourBitmap, ExifInterface.ORIENTATION_NORMAL);
//                            Bitmap grayscaleBitmap;
//                            if (grayscale != null) {
//                                grayscaleBitmap = VerID.shared.getPlatformUtils().grayscaleToBitmap(grayscale, colourBitmap.getWidth(), colourBitmap.getHeight());
//                            } else {
//                                grayscaleBitmap = colourBitmap;
//                            }
//                            if (grayscaleBitmap != null) {
//                                int size = Math.min(grayscaleBitmap.getWidth(), grayscaleBitmap.getHeight());
//                                int x = (int) ((double) grayscaleBitmap.getWidth() / 2.0 - (double) size / 2.0);
//                                int y = (int) ((double) grayscaleBitmap.getHeight() / 2.0 - (double) size / 2.0);
//                                grayscaleBitmap = Bitmap.createBitmap(grayscaleBitmap, x, y, size, size);
//                                grayscaleBitmap = Bitmap.createScaledBitmap(grayscaleBitmap, width, width, true);
//                                final RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), grayscaleBitmap);
//                                roundedBitmapDrawable.setCornerRadius((float) width / 2f);
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        profileImageView.setImageDrawable(roundedBitmapDrawable);
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });
//            }
//        });
//    }


    private void showExportError() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage(R.string.failed_to_export_registration)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    private void showImportError() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage(R.string.failed_to_import_registration)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }


    // @Override
    public void onLoaderReset(Loader loader) {

    }
    //endregion

    private void init() {
//        Registering event bus....
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).mBind.mViewBgTopBar.setVisibility(View.GONE);
//        ((MainActivity) getActivity()).mBind.mViewBgBottomBar.setVisibility(View.GONE);

//        Loading animations....
        slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

//        Setting custom font....
        setCustomFonts();

//        All event listeners....
        logInClickListener = new LogInClickListener(getActivity());
        loginBinding.setLoginClick(logInClickListener);

//        Registering event for keyboard visibility....
        registerEventsForKeyboardVisibility();

        animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

        lastLoginId = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
        lastLoginEmail = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "");
        lastLoginPassword = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getContext().getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected() || !fingerprintManager.hasEnrolledFingerprints() || lastLoginId.isEmpty()
                ) {
                    loginBinding.imageBiometric.setVisibility(View.GONE);
                    loginBinding.imageBiometric1.setVisibility(View.GONE);
                }
            }
        }

        BiometricCallbackPrint biometricCallbackPrint = new BiometricCallbackPrint() {
            @Override
            public void onSdkVersionNotSupported() {
                Toast.makeText(getContext(), getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onBiometricAuthenticationNotSupported() {
                loginBinding.imageBiometric.setVisibility(View.GONE);
                loginBinding.imageBiometric1.setVisibility(View.GONE);

            }

            @Override
            public void onBiometricAuthenticationNotAvailable() {
                loginBinding.imageBiometric.setVisibility(View.GONE);
                loginBinding.imageBiometric1.setVisibility(View.GONE);

            }

            @Override
            public void onBiometricAuthenticationPermissionNotGranted() {
                Toast.makeText(getContext(), getString(R.string.biometric_error_permission_not_granted), Toast.LENGTH_LONG).show();
                loginBinding.imageBiometric.setVisibility(View.GONE);
                loginBinding.imageBiometric1.setVisibility(View.GONE);

            }

            @Override
            public void onBiometricAuthenticationInternalError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(getContext(), getString(R.string.biometric_failure), Toast.LENGTH_LONG).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onAuthenticationCancelled() {
                Toast.makeText(getContext(), getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show();
                mBiometricManager.cancelAuthentication();
            }

            @Override
            public void onAuthenticationSuccessful() {
                String email = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "");
                String password = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASS, "");
                reqLogIn(email, password);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Toast.makeText(getContext(), helpString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Toast.makeText(getContext(), errString, Toast.LENGTH_LONG).show();
            }
        };
        loginBinding.imageBiometric.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mBiometricManager = new BiometricManagerPrint.BiometricBuilder(getContext())
                        .setTitle(getString(R.string.biometric_title))
                        .setSubtitle(getString(R.string.biometric_subtitle))
                        .setDescription(getString(R.string.biometric_description))
                        .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                        .build();


                //start authentication
                mBiometricManager.authenticate(biometricCallbackPrint);
            }
        });

        ValidUtils.textViewGradientColor(loginBinding.textViewSignInToYourAccount);
        ValidUtils.textViewGradientColor(loginBinding.textViewSignInYourAccount1);
        ValidUtils.textViewGradientColor(loginBinding.textViewForgotPassword);
        ValidUtils.textViewGradientColor(loginBinding.textViewForgotPassword1);
        ValidUtils.textViewGradientColor(loginBinding.textViewSignup);
        ValidUtils.textViewGradientColor(loginBinding.textViewSignUp1);

    }

    //    Method to set custom fonts to android views....
    private void setCustomFonts() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewBoldFont(loginBinding.textViewSignInToYourAccount);
        fontUtils.setTextViewBoldFont(loginBinding.textViewSignInYourAccount1);
        fontUtils.setEditTextRegularFont(loginBinding.editTextEmail);
        fontUtils.setEditTextRegularFont(loginBinding.editTextEmail1);
        fontUtils.setEditTextRegularFont(loginBinding.editTextPassword);
        fontUtils.setEditTextRegularFont(loginBinding.editTextPassword1);
//        fontUtils.setTextViewMedium(loginBinding.clearTextViewLogin);
//        fontUtils.setTextViewMedium(loginBinding.clearTextViewLogin1);
        fontUtils.setTextViewRegularFont(loginBinding.textViewForgotPassword);
        fontUtils.setTextViewRegularFont(loginBinding.textViewForgotPassword1);
    }

    //    Method to change focus of edit texts....
    private void changeFocusOfViewGroups(EditText editText) {
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
    }

    //    Method to toggle visibility of view groups....
    public void toggleVisibilityOfRoot(ViewGroup viewGroupShow, ViewGroup viewGroupHide) {
        viewGroupShow.setVisibility(View.VISIBLE);
        viewGroupHide.setVisibility(View.GONE);
    }

    private void registerEventsForKeyboardVisibility() {
        unregister = KeyboardVisibilityEvent.registerEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            if (loginBinding.editTextEmail.hasFocus()) {
                                changeFocusOfViewGroups(loginBinding.editTextEmail1);
                            } else {
                                changeFocusOfViewGroups(loginBinding.editTextPassword1);
                            }
                            loginBinding.mRoot1.startAnimation(slideUpAnimation);
                            toggleVisibilityOfRoot(loginBinding.mRoot1, loginBinding.mRoot);
                        } else {
                            loginBinding.editTextEmail.setText(loginBinding.editTextEmail1.getText().toString());
                            loginBinding.editTextPassword.setText(loginBinding.editTextPassword1.getText().toString());
                            if (loginBinding.editTextEmail1.hasFocus()) {
                                changeFocusOfViewGroups(loginBinding.editTextEmail);
                            } else {
                                changeFocusOfViewGroups(loginBinding.editTextPassword);
                            }
                            toggleVisibilityOfRoot(loginBinding.mRoot, loginBinding.mRoot1);
                            loginBinding.mRoot.startAnimation(slideDownAnimation);
                        }
                    }
                });
    }

    //    Cancelling http requests if in process....
    @Override
    public void onDestroy() {
        super.onDestroy();
        ValidUtils.hideKeyboardFromFragment(getContext(), loginBinding.getRoot());
        if (callLogIn != null && callLogIn.isExecuted()) {
            callLogIn.cancel();
        }
        if (callNotification != null && callNotification.isExecuted()) {
            callNotification.cancel();
        }
        if (unregister != null) {
            unregister.unregister();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    //    Method to validate user data....
    private void validateData(String email, String password) {
        if (!ValidUtils.isNetworkAvailable(getActivity())) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            loginBinding.clearTextViewLogin.setEnabled(true);
            loginBinding.clearTextViewLogin1.setEnabled(true);
            loginBinding.clearTextViewLogin.setVisibility(View.VISIBLE);
            loginBinding.clearTextViewLogin2.setVisibility(View.GONE);
            return;
        }
        if (email.isEmpty() || email.length() == 0) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.email_empty));
            loginBinding.clearTextViewLogin.setEnabled(true);
            loginBinding.clearTextViewLogin1.setEnabled(true);
            loginBinding.clearTextViewLogin.setVisibility(View.VISIBLE);
            loginBinding.clearTextViewLogin2.setVisibility(View.GONE);
        } else if (!ValidUtils.validateEmail(email)) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.invalid_email));
            loginBinding.clearTextViewLogin.setEnabled(true);
            loginBinding.clearTextViewLogin1.setEnabled(true);
            loginBinding.clearTextViewLogin.setVisibility(View.VISIBLE);
            loginBinding.clearTextViewLogin2.setVisibility(View.GONE);
        } else if (password.isEmpty() || password.length() == 0) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.password_empty));
            loginBinding.clearTextViewLogin.setEnabled(true);
            loginBinding.clearTextViewLogin1.setEnabled(true);
            loginBinding.clearTextViewLogin.setVisibility(View.VISIBLE);
            loginBinding.clearTextViewLogin2.setVisibility(View.GONE);
        } else {
            reqLogIn(email, password);
        }
    }

    //    Event bus method trigger to unregister keyboard visibility....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUnregisterKeyboard(EventBusLoginKeyboardUnregister mObj) {
        unregister = KeyboardVisibilityEvent.registerEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            if (loginBinding.editTextEmail.hasFocus()) {
                                changeFocusOfViewGroups(loginBinding.editTextEmail1);
                            } else {
                                changeFocusOfViewGroups(loginBinding.editTextPassword1);
                            }
                            loginBinding.mRoot1.setVisibility(View.GONE);
                            loginBinding.mRoot1.startAnimation(slideUpAnimation);
                            loginBinding.mRoot1.setVisibility(View.VISIBLE);
                            loginBinding.mRoot.setVisibility(View.GONE);
                        } else {
                            loginBinding.editTextEmail.setText(loginBinding.editTextEmail1.getText().toString());
                            loginBinding.editTextPassword.setText(loginBinding.editTextPassword1.getText().toString());
                            if (loginBinding.editTextEmail1.hasFocus()) {
                                changeFocusOfViewGroups(loginBinding.editTextEmail);
                            } else {
                                changeFocusOfViewGroups(loginBinding.editTextPassword);
                            }
                            loginBinding.mRoot1.setVisibility(View.GONE);
                            loginBinding.mRoot.startAnimation(slideDownAnimation);
                            loginBinding.mRoot.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    //     Method to log the user in....
    private void reqLogIn(String email, final String password) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        callLogIn = Api.WEB_SERVICE.login(EndpointKeys.X_API_KEY, Credentials.basic(email, ValidUtils.md5(password)), email, password);
//        callLogIn = Api.WEB_SERVICE.login(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(email, ValidUtils.md5(password)), email, password);
        callLogIn.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                loginBinding.clearTextViewLogin2.stopAnimation();
                loginBinding.clearTextViewLogin2.revertAnimation();
                loginBinding.clearTextViewLogin.setVisibility(View.VISIBLE);
                loginBinding.clearTextViewLogin2.setVisibility(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (unregister != null) {
                                unregister.unregister();
                            }
                            LnqApplication.getInstance().editor.putString("looged", "11").apply();
                            FragmentLogin.this.password = password;
                            FragmentLogin.this.response = response;
                            LogInData logIn = response.body().getLogin();
                            List<CreateUserSecondaryProfile> logInProfilesDataList = response.body().getUser_profiles();
                            if (logIn != null) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (!lastLoginId.isEmpty() && !lastLoginEmail.isEmpty() && !lastLoginPassword.isEmpty()
                                            && lastLoginId.equals(logIn.getUserId()) && lastLoginEmail.equals(logIn.getUserEmail()) && lastLoginPassword.equals(logIn.getUserPassword())
                                    ) {
                                        checkUserLoggedOnOtherDevice(logIn);
                                    } else {
                                        createVerificationOptionDialog(logIn);
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
                            ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getMessage().equalsIgnoreCase("Email Not Found")) {
                                loginBinding.editTextEmail.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorError), PorterDuff.Mode.SRC_ATOP);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginBinding.editTextEmail.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorBlackNewTheme), PorterDuff.Mode.SRC_ATOP);
                                        loginBinding.editTextEmail.setText("");
                                        loginBinding.editTextEmail1.setText("");
                                    }
                                }, 1000);

                                loginBinding.editTextEmail.startAnimation(animShake);
                                loginBinding.editTextEmail1.startAnimation(animShake);
                            } else if (response.body().getMessage().equalsIgnoreCase("Password incorrect")) {
                                loginBinding.editTextPassword.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorError), PorterDuff.Mode.SRC_ATOP);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginBinding.editTextPassword.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorBlackNewTheme), PorterDuff.Mode.SRC_ATOP);
                                        loginBinding.editTextPassword.setText("");
                                        loginBinding.editTextPassword1.setText("");
                                    }
                                }, 1000);
                                loginBinding.editTextPassword.startAnimation(animShake);
                                loginBinding.editTextPassword1.startAnimation(animShake);
                            }
                            loginBinding.clearTextViewLogin.setEnabled(true);
                            loginBinding.clearTextViewLogin1.setEnabled(true);
//                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                    ((MainActivity) getActivity()).mBind.mImgMessages.setImageResource(R.mipmap.chat_nc_black);
                    ((MainActivity) getActivity()).mBind.mImgContacts.setImageResource(R.mipmap.contact_nc_black);
                    ((MainActivity) getActivity()).mBind.mImgProfile.setImageResource(R.mipmap.profile_nc_black);
                    ((MainActivity) getActivity()).mBind.mImgAlerts.setImageResource(R.mipmap.activity_nc_black);
                    ((MainActivity) getActivity()).mBind.mImgHome.setImageResource(R.mipmap.map_nc_blue);
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                loginBinding.clearTextViewLogin.setEnabled(true);
                loginBinding.clearTextViewLogin1.setEnabled(true);
                loginBinding.clearTextViewLogin.setVisibility(View.VISIBLE);
                loginBinding.clearTextViewLogin2.setVisibility(View.GONE);
                loginBinding.clearTextViewLogin2.revertAnimation();
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

    private void createVerificationOptionDialog(LogInData logIn) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.cus_dialog_verification, null);
        TextView textCancel = dialogView.findViewById(R.id.textViewCancel);
        TextView textViewTouchId = dialogView.findViewById(R.id.textViewTouchId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getContext().getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected()) {
                    textViewTouchId.setVisibility(View.GONE);
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    textViewTouchId.setVisibility(View.GONE);
                }
            }
        }

//        textViewFaceId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_TYPE, "face").apply();
//                dialog.dismiss();
//                checkUserLoggedOnOtherDevice(logIn);
//            }
//        });

        textViewTouchId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_TYPE, "touch").apply();
                dialog.dismiss();
                checkUserLoggedOnOtherDevice(logIn);
            }
        });

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_TYPE, "touch").apply();
                dialog.dismiss();
                checkUserLoggedOnOtherDevice(logIn);
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

    private void checkUserLoggedOnOtherDevice(LogInData logIn) {
        if (!logIn.getIsLoggedIn().equalsIgnoreCase("0")) {
            LogInData logInData = response.body().getLogin();
            String userId = logInData.getUserId();
            reqLogOut(userId, logInData, password, response);
        } else {
            saveLoginUserData(logIn, password, response);
        }
    }

    public void showLoggedInDialog(String dialogType, String textMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = null;
        switch (dialogType) {
            case "success":
                view = LayoutInflater.from(getActivity()).inflate(R.layout.cus_dialog_success, null);
                break;
            case "error":
                view = LayoutInflater.from(getActivity()).inflate(R.layout.cus_dialog_error, null);
                break;
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        TextView text = view.findViewById(R.id.textViewMessageDialog);
        text.setText(textMessage);
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogInData logInData = response.body().getLogin();
                String userId = logInData.getUserId();
                reqLogOut(userId, logInData, password, response);
            }
        });
        dialog.show();
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

    private void reqLogOut(String id, final LogInData logIn, final String password, final Response<RegisterLoginMainObject> logInResponce) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
//        callLogOut = Api.WEB_SERVICE.logout(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), id, "", "");
        callLogOut = Api.WEB_SERVICE.logout(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), id, "", "");
        callLogOut.enqueue(new Callback<LogOut>() {
            @Override
            public void onResponse(Call<LogOut> call, Response<LogOut> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case "1":
                            saveLoginUserData(logIn, password, logInResponce);
                            break;
                        case "0":
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<LogOut> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
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

    //    Method to hit api to change notification status....
    private void reqNotificationsSet(final Response<RegisterLoginMainObject> loginResponse) {
//        callNotification = Api.WEB_SERVICE.notificationsSet(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.FCM_TOKEN, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.LAST_LOGIN, ""));
        callNotification = Api.WEB_SERVICE.notificationsSet(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.FCM_TOKEN, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.LAST_LOGIN, ""));
        callNotification.enqueue(new Callback<PushNotificationMainObject>() {
            @Override
            public void onResponse(Call<PushNotificationMainObject> call, Response<PushNotificationMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                        case 0:
                            switch (loginResponse.body().getLogin().getVerificationStatus()) {
                                case EndpointKeys.SIGN_UP:
                                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.VERIFICATION_ONE, false, null);
                                    ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    break;
                                case EndpointKeys.PHONE:
                                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.PROFILE_CREATE, false, null);
                                    ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    break;
                                case EndpointKeys.PROFILE:
                                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.PROFILE_PICTURE, false, null);
                                    ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    break;
                                case EndpointKeys.PROFILE_IMAGE:
                                    LnqApplication.getInstance().editor.putBoolean(EndpointKeys.IS_USER_LOGGED_IN, true);
                                    LnqApplication.getInstance().editor.putBoolean(EndpointKeys.SHOW_NOTIFICATION_DIALOG, false);
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.NOTIFICATION_STATUS, "1");

                                    LnqApplication.getInstance().editor.apply();
                                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.HOME, false, null);
                                    break;
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PushNotificationMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (getActivity() == null)
                    return;
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


    public class LogInClickListener {

        private Context context;

        LogInClickListener(Context context) {
            this.context = context;
        }

        public void onForgotPasswordClick(View view) {
            if (unregister != null) {
                unregister.unregister();
            }
            ValidUtils.hideKeyboardFromFragment(getActivity(), loginBinding.getRoot());
            loginBinding.mRoot1.setVisibility(View.GONE);
            loginBinding.mRoot.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.FORGOT_PASSWORD, true, null);
        }

        public void onSignUpClick(View view) {
            if (unregister != null) {
                unregister.unregister();
            }
            ValidUtils.hideKeyboardFromFragment(getActivity(), loginBinding.getRoot());
            loginBinding.mRoot1.setVisibility(View.GONE);
            loginBinding.mRoot.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SIGN_UP, true, null);
                }
            }, 50);
        }

        public void onLogInClick(View view) {
            switch (view.getId()) {
                case R.id.clearTextViewLogin:
                    loginBinding.clearTextViewLogin.setEnabled(false);
                    loginBinding.clearTextViewLogin1.setEnabled(false);
                    ValidUtils.hideKeyboardFromFragment(getActivity(), loginBinding.getRoot());
                    loginBinding.clearTextViewLogin.setVisibility(View.INVISIBLE);
                    loginBinding.clearTextViewLogin2.setVisibility(View.VISIBLE);
                    loginBinding.clearTextViewLogin2.startAnimation();
                    validateData(loginBinding.editTextEmail.getText().toString().trim(), loginBinding.editTextPassword.getText().toString().trim());
                    break;
                case R.id.clearTextViewLogin1:
                    ValidUtils.hideKeyboardFromFragment(getActivity(), loginBinding.getRoot());
                    loginBinding.clearTextViewLogin.setEnabled(false);
                    loginBinding.clearTextViewLogin1.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loginBinding.clearTextViewLogin.setVisibility(View.INVISIBLE);
                            loginBinding.clearTextViewLogin2.setVisibility(View.VISIBLE);
                            loginBinding.clearTextViewLogin2.startAnimation();
                            validateData(loginBinding.editTextEmail1.getText().toString().trim(), loginBinding.editTextPassword1.getText().toString().trim());
                        }
                    }, 400);
                    break;
            }
        }

        public void onShowHideClick(View view) {
            switch (view.getId()) {
                case R.id.textViewHideShow1:
                    if (loginBinding.textViewHideShow1.getText().toString().equalsIgnoreCase("Show")) {
                        loginBinding.editTextPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        loginBinding.textViewHideShow1.setText("Hide");
                        loginBinding.editTextPassword1.setSelection(loginBinding.editTextPassword1.getText().length());
                    } else {
                        loginBinding.textViewHideShow1.setText("Show");
                        loginBinding.editTextPassword1.setTransformationMethod(new PasswordTransformationMethod());
                        loginBinding.editTextPassword1.setSelection(loginBinding.editTextPassword1.getText().length());
                    }
                    break;
                case R.id.textViewHideShow:
                    if (loginBinding.textViewHideShow.getText().toString().equalsIgnoreCase("Show")) {
                        loginBinding.editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        loginBinding.textViewHideShow.setText("Hide");
                        loginBinding.editTextPassword.setSelection(loginBinding.editTextPassword.getText().length());
                    } else {
                        loginBinding.textViewHideShow.setText("Show");
                        loginBinding.editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                        loginBinding.editTextPassword.setSelection(loginBinding.editTextPassword.getText().length());
                    }
                    break;
            }
        }

        public void onRootClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), loginBinding.getRoot());
        }

    }

}