package lnq.com.lnq.fragments.status;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;

import lnq.com.lnq.databinding.FragmentUpdateStatusBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentProfileLooksGood;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusStatusUpdated;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentUpdateStatus extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentUpdateStatusBinding updateStatusBinding;

    //    Retrofit fields....
    private Call<CreateUserProfileMainObject> callUpdateStatus;

    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentProfile;

    private int previousLength;
    private boolean backSpace = true;

    public FragmentUpdateStatus() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateStatusBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_status, container, false);
        return updateStatusBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        multiProfileRepositry = new MultiProfileRepositry(getContext());
//        Setting status message in edit text....
//        updateStatusBinding.editTextStatusMessage.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_STATUS_MESSAGE, ""));

        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels){
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))){
                        currentProfile = data;
                    }
                }
                updateStatusBinding.editTextStatusMessage.setText(currentProfile.getUser_status_msg());
            }
        });

//        All click listeners....
        updateStatusBinding.imageViewBack.setOnClickListener(this);
        updateStatusBinding.clearTextUpdate.setOnClickListener(this);
        updateStatusBinding.viewStatus.setOnClickListener(this);
        updateStatusBinding.editTextStatusMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (backSpace) {
                    previousLength = s.length();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (previousLength > s.length()) {
                    updateStatusBinding.editTextStatusMessage.setText("");
                    backSpace = false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
            case R.id.view_status:
                ValidUtils.hideKeyboardFromFragment(getActivity(), updateStatusBinding.getRoot());
                getActivity().onBackPressed();
                break;
            case R.id.clearTextUpdate:
                if (!((MainActivity) getActivity()).fnIsisOnline()) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                    return;
                }
                String statusMessage = updateStatusBinding.editTextStatusMessage.getText().toString();
                if (!statusMessage.trim().isEmpty()) {
                    reqUpdateStatus(statusMessage, currentProfile.getId());
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.please_enter_status_message));
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callUpdateStatus != null && callUpdateStatus.isExecuted()) {
            callUpdateStatus.cancel();
        }
    }

    private void reqUpdateStatus(String status, String profileId) {
        ((MainActivity) getActivity()).progressDialog.show();
        ValidUtils.hideKeyboardFromFragment(getActivity(), updateStatusBinding.getRoot());
//        callUpdateStatus = Api.WEB_SERVICE.updateStatusMsg(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), status);
            callUpdateStatus = Api.WEB_SERVICE.updateStatusMsg(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), status, profileId);
        callUpdateStatus.enqueue(new Callback<CreateUserProfileMainObject>() {
            @Override
            public void onResponse(Call<CreateUserProfileMainObject> call, Response<CreateUserProfileMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_STATUS_MESSAGE, response.body().getUpdateStatusMsg().getUser_status_msg()).apply();
//                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            currentProfile.setUser_status_msg(response.body().getUpdateStatusMsg().getUser_status_msg());
                            multiProfileRepositry.updateTask(currentProfile);
                            EventBus.getDefault().post(new EventBusStatusUpdated(response.body().getUpdateStatusMsg().getUser_status_msg()));
                            EventBus.getDefault().post(new EventBusUserSession("status_updated"));
                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateUserProfileMainObject> call, Throwable error) {
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
