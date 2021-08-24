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
import lnq.com.lnq.databinding.FragmentLnqSendPopUpBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.connections.import_contacts.FragmentImportUserPopUp;
import lnq.com.lnq.model.event_bus_models.EventBusImportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentLnqSendPopUp extends Fragment {

    private FragmentLnqSendPopUpBinding binding;
    private Call<UpdateLocationMainObject> callRequest;
    private String userId, userName, senderProfileId, receiverProfileId;
    private LnqUserPopUp yourDataPopUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lnq_send_pop_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID);
            userName = getArguments().getString(EndpointKeys.USER_NAME);
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
            if (userName != null)
                binding.textViewRequestYourData.setText("LNQ with " + userName);
        }

        yourDataPopUp = new LnqUserPopUp();
        binding.setClickHandler(yourDataPopUp);
    }

    private void reqContactRequest(final String userId, String senderProfileid, String recevierProfileId) {
        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId,"map", senderProfileid, recevierProfileId);
        callRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", "Request sent to " + userName);
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_sent"));
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "contacted",false));
                            break;
                        case 0:
//                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
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

    public class LnqUserPopUp {

        public void onOkayCLick(View view){
            if (!((MainActivity) getActivity()).fnIsisOnline()) {
                LnqApplication.getInstance().snakeBar(binding.getRoot(), getResources().getString(R.string.wifi_internet_not_connected), getResources().getColor(R.color.colorError));
                return;
            }
            if (userId == null || userId.equals(""))
                return;
            reqContactRequest(userId, senderProfileId, receiverProfileId);
            getActivity().onBackPressed();
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }
    }
}