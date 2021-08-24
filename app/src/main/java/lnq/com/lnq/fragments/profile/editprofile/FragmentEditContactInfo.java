package lnq.com.lnq.fragments.profile.editprofile;


import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ProfileEditContactInfoEmailAdapter;
import lnq.com.lnq.adapters.ProfileEditContactInfoPhoneAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.databinding.FragmentFragmentEditContactInfoBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.profile.FragmentProfile;
import lnq.com.lnq.fragments.registeration.phoneverification.FragmentPhoneVerificationSendCode;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryPhones;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSocial;
import lnq.com.lnq.model.event_bus_models.EventBusSecondaryEmailAdded;
import lnq.com.lnq.model.event_bus_models.EventBusSecondaryPhoneAdded;
import lnq.com.lnq.model.gson_converter_models.EditSocialLinksMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditContactInfo extends Fragment implements View.OnClickListener {

    FragmentFragmentEditContactInfoBinding contactInfoBinding;
    private boolean codeResend = false;
    private int countryCodeSelector = 1;

    private ArrayList<String> ContactList = new ArrayList<String>();
    ProfileEditContactInfoPhoneAdapter adapter;
    private FragmentEditContactInfo.SendCodeClickListener sendCodeClickListener;
    String secondaryPhone;

    //    Api fields....
    private Call<RegisterLoginMainObject> callIsPhoneUnique;
    private Call<RegisterLoginMainObject> callSecondaryPhone;

    //    Firebase fields....
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallback;
    private FirebaseAuth mAuth;

    public FragmentEditContactInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fragment_edit_contact_info, container, false);
        return contactInfoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init() {
        EventBus.getDefault().register(this);
//        Firebase fields initialization....
        mAuth = FirebaseAuth.getInstance();
        contactInfoBinding.countryCodePicker.registerCarrierNumberEditText(contactInfoBinding.editTextPhone);
        sendCodeClickListener = new SendCodeClickListener(getActivity());
        contactInfoBinding.setClickListener(sendCodeClickListener);
        contactInfoBinding.mBtnSaveChangee.setOnClickListener(this);
        contactInfoBinding.mImgBack.setOnClickListener(this);
        contactInfoBinding.mTvCreateContactHeading.setOnClickListener(this);
        secondaryPhone = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SECONDARY_PHONES, "");
        if (!secondaryPhone.isEmpty()) {
            ContactList = new ArrayList(Arrays.asList(secondaryPhone.split(",")));
            contactInfoBinding.recyclerViewContactEditPhone.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ProfileEditContactInfoPhoneAdapter(getContext(), ContactList);
            contactInfoBinding.recyclerViewContactEditPhone.setAdapter(adapter);
        }
        phoneAuthCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
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
                        reqSecondaryNumber(contactInfoBinding.countryCodePicker.getFullNumberWithPlus());
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
                    mBundle.putString(EndpointKeys.PHONE, contactInfoBinding.editTextPhone.getText().toString().trim());
                    mBundle.putString("verification_id", verificationId);
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VERIFY_SECONDARY_CODE, true, mBundle);
                }
                EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
            }
        };

//        Events for country code dialog shows listeners....
        contactInfoBinding.countryCodePicker.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
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

        contactInfoBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), contactInfoBinding.mRoot);
                return false;
            }
        });


    }

    //    Event bus trigger when code is sent to user....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void verificationCode(final EventBusPhoneVerification mObj) {
        switch (mObj.getmFlag()) {
            case "mFverify":
                ((MainActivity) getActivity()).progressDialog.show();
                PhoneAuthCredential mCredential = PhoneAuthProvider.getCredential(mObj.getVerificationId(), mObj.getCode());
                mAuth.signInWithCredential(mCredential)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    reqSecondaryNumber(contactInfoBinding.countryCodePicker.getFullNumberWithPlus());
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
                        contactInfoBinding.countryCodePicker.getFullNumberWithPlus(),
                        0,
                        TimeUnit.SECONDS,
                        getActivity(),
                        phoneAuthCallback);
                break;
        }
    }

    //    Method to request api to check if number is unique or not....
    private void reqIsPhoneUnique(final String phoneNumber) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), contactInfoBinding.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callIsPhoneUnique = Api.WEB_SERVICE.isPhoneUnique(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), phoneNumber);
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

    private void reqSecondaryPhoneRemove(String secondaryPhoneRemove, int position) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), contactInfoBinding.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callIsPhoneUnique = Api.WEB_SERVICE.secondaryPhonesRemove(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), secondaryPhoneRemove);
        callIsPhoneUnique.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    ContactList.remove(position);
                    adapter.notifyDataSetChanged();
                    ContactList.remove(secondaryPhone);
                    secondaryPhone = TextUtils.join(",", ContactList);
                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_PHONES, secondaryPhone).apply();
                    ((MainActivity) getActivity()).showMessageDialog("success", "Phone removed successfully.");
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRemoveSecondaryPhones(EventBusRemoveSecondaryPhones eventBusRemoveSecondaryPhones) {
        reqSecondaryPhoneRemove(ContactList.get(eventBusRemoveSecondaryPhones.getPosition()), eventBusRemoveSecondaryPhones.getPosition());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvCreateContactHeading:
            case R.id.mImgBack:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnSaveChangee:
                fnValidateData(contactInfoBinding.editTextPhone.getText().toString().trim(), "0");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (callIsPhoneUnique != null && callIsPhoneUnique.isExecuted()) {
            callIsPhoneUnique.cancel();
        }
        if (callSecondaryPhone != null && callSecondaryPhone.isExecuted()) {
            callSecondaryPhone.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    //    Method to validate data....
    private void fnValidateData(String phoneNumber, String buttonNumber) {
        if (!((MainActivity) getActivity()).fnIsisOnline()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            return;
        }
        if (phoneNumber.isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_phone_number));
        } else {
            reqIsPhoneUnique(buttonNumber.equals("0") ? contactInfoBinding.countryCodePicker.getFullNumberWithPlus() : contactInfoBinding.countryCodePicker.getFullNumberWithPlus());
        }
    }

    private void reqSecondaryNumber(String number) {
        ((MainActivity) getActivity()).progressDialog.show();
        callSecondaryPhone = Api.WEB_SERVICE.secondaryPhones(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), number);
        callSecondaryPhone.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_PHONES, number);
                            if (secondaryPhone.isEmpty()) {
                                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_PHONES, number);
                            } else {
                                secondaryPhone += "," + number;
                                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_PHONES, secondaryPhone).apply();
                            }
                            ContactList.add(number);
                            adapter = new ProfileEditContactInfoPhoneAdapter(getContext(), ContactList);
                            contactInfoBinding.recyclerViewContactEditPhone.setAdapter(adapter);
                            EventBus.getDefault().post(new EventBusSecondaryPhoneAdded());
                            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.PROFILE_FRAGMENT, true, null);
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

    public class SendCodeClickListener {
        private Context context;

        SendCodeClickListener(Context context) {
            this.context = context;
        }

        public void onRootClick(View view) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(contactInfoBinding.mRoot);
        }

    }

}