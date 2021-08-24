package lnq.com.lnq.fragments.lnqrequest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.databinding.FragmentAcceptPopUpBinding;
import lnq.com.lnq.databinding.FragmentAcceptRequestBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAcceptPopUp extends Fragment {

    //    Android fields....
    private FragmentAcceptPopUpBinding binding;
    private AcceptPopUP yourDataPopUp;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callAcceptRequest;
    private Call<UpdateLocationMainObject> callDeclineRequest;

    //    Instance fields....
    private String userId, userName, senderProfileId, receiverProfileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_accept_pop_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init(){
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID, "");
            userName = getArguments().getString(EndpointKeys.USER_NAME, "");
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        }
        if (userName != null) {
            binding.textViewRequestYourData.setText(userName + " Would like to LNQ");
        }
        yourDataPopUp = new AcceptPopUP();
        binding.setClickHandler(yourDataPopUp);
    }

    public class AcceptPopUP {

        public void onOkayCLick(View view){
            if (!((MainActivity) getActivity()).fnIsisOnline()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                return;
            }
            if (userId == null || userId.equals("")) {
                return;
            }
            reqAcceptContactRequest(userId, receiverProfileId, senderProfileId);
            getActivity().onBackPressed();
        }

        public void onMenuClick(View view) {
            if (!((MainActivity) getActivity()).fnIsisOnline()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                return;
            }
            if (userId == null || userId.equals("")) {
                return;
            }
            reqDeclineContactRequest(userId, senderProfileId, receiverProfileId);
        }

        public void onCancelClick(View view) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callAcceptRequest != null && callAcceptRequest.isExecuted()) {
            callAcceptRequest.cancel();
        }
        if (callDeclineRequest != null && callDeclineRequest.isExecuted()) {
            callDeclineRequest.cancel();
        }
    }

    private void reqAcceptContactRequest(final String userId, String senderProfileid, String receiverProfileid) {
//        callAcceptRequest = Api.WEB_SERVICE.contactRequestAccpet(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        callAcceptRequest = Api.WEB_SERVICE.contactRequestAccpet(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""), senderProfileid, receiverProfileid);
        callAcceptRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_accepted"));
                            EventBus.getDefault().post(new EventBusUpdateAll());
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "connected", false));
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
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

    private void reqDeclineContactRequest(final String userId, String senderProfileid, String receiverProfileid) {
//        callDeclineRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY,  DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        callDeclineRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""), senderProfileid, receiverProfileid);
        callDeclineRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_declined"));
                            EventBus.getDefault().post(new EventBusUpdateAll());
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "cancel", false));
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
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

}