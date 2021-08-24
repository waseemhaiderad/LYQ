package lnq.com.lnq.fragments.qrcode;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentRecipientInfoBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBuSetDataRecipentFragment;
import lnq.com.lnq.model.event_bus_models.EventBusSendReceipetInfo;
import lnq.com.lnq.model.gson_converter_models.qr_code.InviteUserMainObject;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentRecipientInfo extends Fragment implements View.OnClickListener {

    FragmentRecipientInfoBinding mBinding;
    FragmentManager fragmentManager;

    // Instance Fields
    String fName, lName, email, phone, country;
    private AppCompatImageView imageViewBackTopBar, imageViewSearchTopBar, imageViewDropdownContacts;
    CardView topBarLayout;
    TextView textViewHeading;

    Call<InviteUserMainObject> inviteUserMainObjectCall;
    String name, firstName, lastName, emailAddress, phoneNumber;

    public FragmentRecipientInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipient_info, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
        topBarLayout = mBinding.topBarContact.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewSearchTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.add_connection);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewSearchTopBar.setVisibility(View.GONE);
        imageViewDropdownContacts.setVisibility(View.GONE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void init() {
        if (getArguments() != null) {
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
            emailAddress = getArguments().getString("email");
            phoneNumber = getArguments().getString("phone");

            mBinding.editTextFirstName.setText(firstName);
            mBinding.editTextLastName.setText(lastName);
            mBinding.editTextEmail.setText(emailAddress);
            mBinding.editTextPhone.setText(phoneNumber);

        }

//        EventBus.getDefault().register(this);
        mBinding.countryCodePicker.registerCarrierNumberEditText(mBinding.editTextPhone);
        fragmentManager = getFragmentManager();
        mBinding.buttonSendMyInfo.setOnClickListener(this);
        mBinding.editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    fnValidateData();
                    ValidUtils.hideKeyboardFromFragment(getActivity(), mBinding.getRoot());
                    handled = true;
                }
                return handled;
            }
        });

        mBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), mBinding.mRoot);
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSendMyInfo:
                fnValidateData();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    private void fnValidateData() {
        fName = mBinding.editTextFirstName.getText().toString();
        lName = mBinding.editTextLastName.getText().toString();
        email = mBinding.editTextEmail.getText().toString();
        phone = mBinding.editTextPhone.getText().toString();
        country = mBinding.countryCodePicker.getFullNumberWithPlus();

        if (!((MainActivity) getActivity()).fnIsisOnline()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            return;
        }
        if (fName.isEmpty() && lName.isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_all_fields));
        } else if (fName.isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_first_name));
        } else if (lName.isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_last_name));
        } else {
            inviteUser(email, country);
        }
    }

    public void inviteUser(final String email, String phoneNum) {
        ((MainActivity) getActivity()).progressDialog.show();
        inviteUserMainObjectCall = Api.WEB_SERVICE.invite(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), email, phoneNum);
        inviteUserMainObjectCall.enqueue(new Callback<InviteUserMainObject>() {
            @Override
            public void onResponse(Call<InviteUserMainObject> call, Response<InviteUserMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response.body() != null && response.isSuccessful()) {
                    int stause = response.body().getStatus();
                    switch (stause) {
                        case 1:
                            if (response.body().getMessage().equals("Error: User not found")) {

                                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.OPTIONAL_NOTES, true, null);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(new EventBusSendReceipetInfo(fName, lName, mBinding.countryCodePicker.getFullNumberWithPlus(), email));
                                    }
                                }, 100);
                            } else {
                                if (response.body().getGetUserProfile().getId() != null) {
                                    if (!response.body().getGetUserProfile().getId().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("UserData", response.body().getGetUserProfile());
                                        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SEND_INVITE_REQUEST, true, bundle);
                                    } else {
                                        Toast.makeText(getActivity(), "It's your own profile.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            break;
                        case 0:
                            break;

                    }
                }

            }

            @Override
            public void onFailure(Call<InviteUserMainObject> call, Throwable error) {
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
}