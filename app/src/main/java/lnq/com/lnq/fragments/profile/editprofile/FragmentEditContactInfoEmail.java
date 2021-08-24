package lnq.com.lnq.fragments.profile.editprofile;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ProfileEditContactInfoEmailAdapter;
import lnq.com.lnq.adapters.ProfileEditContactInfoPhoneAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentFragmentEditContactInfoBinding;
import lnq.com.lnq.databinding.FragmentFragmentEditContactInfoEmailBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryEmails;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryPhones;
import lnq.com.lnq.model.event_bus_models.EventBusSecondaryEmailAdded;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditContactInfoEmail extends Fragment implements View.OnClickListener {

    FragmentFragmentEditContactInfoEmailBinding contactInfoEmailBinding;
    List<String> contacteditEmail = new ArrayList<>();
    ProfileEditContactInfoEmailAdapter adapter;
    private ArrayList<String> emailList = new ArrayList<String>();
    String secondaryEmail = "";
    Animation animShake;

    //    Api fields....
    private Call<RegisterLoginMainObject> callSecondaryEmail;

    public FragmentEditContactInfoEmail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactInfoEmailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fragment_edit_contact_info_email, container, false);
        return contactInfoEmailBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    }

    public void init() {
        EventBus.getDefault().register(this);
        contactInfoEmailBinding.mBtnSaveChange.setOnClickListener(this);
        contactInfoEmailBinding.mImgBack.setOnClickListener(this);
        contactInfoEmailBinding.mTvCreateContactHeading.setOnClickListener(this);
        secondaryEmail = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SECONDARY_EMAILS, "");
        if (!secondaryEmail.isEmpty()) {
            emailList = new ArrayList(Arrays.asList(secondaryEmail.split(",")));

            contactInfoEmailBinding.recyclerViewContactEditEmail.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ProfileEditContactInfoEmailAdapter(getContext(), emailList);
            contactInfoEmailBinding.recyclerViewContactEditEmail.setAdapter(adapter);
            adapter.notifyDataSetChanged();
//                    EventBus.getDefault().post(new EventBusContactInfoEmail(email));
        }

        contactInfoEmailBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), contactInfoEmailBinding.mRoot);
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvCreateContactHeading:
            case R.id.mImgBack:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnSaveChange:
                if (ValidUtils.validateEmail(contactInfoEmailBinding.mEtEmail.getText().toString().trim())) {
                    if (!contactInfoEmailBinding.mEtEmail.getText().toString().isEmpty())
                        reqSecondaryEmail(contactInfoEmailBinding.mEtEmail.getText().toString());
                    break;
                }else {
                    contactInfoEmailBinding.mEtEmail.startAnimation(animShake);
                }
        }
    }

    private void reqSecondaryEmail(String email) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), contactInfoEmailBinding.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callSecondaryEmail = Api.WEB_SERVICE.secondaryEmails(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), email);
        callSecondaryEmail.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    if (emailList.contains(email)){
                        ((MainActivity) getActivity()).showMessageDialog("error", "Secondary email already exist.");
                    }else {
                        ((MainActivity) getActivity()).showMessageDialog("success", "Secondary email added successfully.");
                    }
                    switch (response.body().getStatus()) {
                        case 1:
                            if (secondaryEmail.isEmpty()) {
                                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_EMAILS, email).apply();
                            } else {
                                secondaryEmail += "," + email;
                                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_EMAILS, secondaryEmail).apply();
                            }
                            emailList.add(email);
                            adapter = new ProfileEditContactInfoEmailAdapter(getContext(), emailList);
                            contactInfoEmailBinding.recyclerViewContactEditEmail.setLayoutManager(new LinearLayoutManager(getActivity()));
                            contactInfoEmailBinding.recyclerViewContactEditEmail.setAdapter(adapter);
                            EventBus.getDefault().post(new EventBusSecondaryEmailAdded());
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

    private void reqSecondaryEmailRemove(String secondaryEmailRemove, int position) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), contactInfoEmailBinding.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callSecondaryEmail = Api.WEB_SERVICE.secondaryEmailsRemove(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), secondaryEmailRemove);
        callSecondaryEmail.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    emailList.remove(position);
                    adapter.notifyDataSetChanged();
                    emailList.remove(secondaryEmail);
                    secondaryEmail = TextUtils.join(",", emailList);
                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SECONDARY_EMAILS, secondaryEmail).apply();
                    ((MainActivity) getActivity()).showMessageDialog("success", "Email removed successfully.");
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
    public void eventBusSecondaryEmailRemove(EventBusRemoveSecondaryEmails eventBusRemoveSecondaryEmails) {
        reqSecondaryEmailRemove(emailList.get(eventBusRemoveSecondaryEmails.getPosition()), eventBusRemoveSecondaryEmails.getPosition());
    }
}