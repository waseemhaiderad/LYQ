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
import lnq.com.lnq.databinding.FragmentImportUserPopUpBinding;
import lnq.com.lnq.databinding.FragmentRetractPopUPBinding;
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

public class FragmentRetractPopUP extends Fragment {

    private FragmentRetractPopUPBinding binding;
    private RetaractPopUP yourDataPopUp;

    private Call<UpdateLocationMainObject> mCallRetractRequest;
    //    Instance fields....
    private String userId, senderProfileId, receiverProfileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_retract_pop_u_p, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init(){
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID);
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        }
        yourDataPopUp = new RetaractPopUP();
        binding.setClickHandler(yourDataPopUp);
    }

    public class RetaractPopUP {

        public void onOkayCLick(View view){
            reqContactRequest(userId, senderProfileId, receiverProfileId);
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCallRetractRequest != null && mCallRetractRequest.isExecuted()) {
            mCallRetractRequest.cancel();
        }
    }

    private void reqContactRequest(final String userId, String senderProfileid, String receiverProfileid) {
        mCallRetractRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, senderProfileid, receiverProfileid);
        mCallRetractRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("retract_request"));
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "cancel",false));
                            getActivity().onBackPressed();
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
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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