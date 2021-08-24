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
import lnq.com.lnq.databinding.FragmentUnLnqPopUpBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentUnLnqPopUp extends Fragment {

    private FragmentUnLnqPopUpBinding binding;
    private UnLnqPopUP yourDataPopUp;
    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callUnLnq;

    //    Instance fields....
    private String userId, senderProfileId, receiverProfileId, userName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_lnq_pop_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.VISIBLE);
        init();
    }

    public void init() {
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID, "");
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            userName = getArguments().getString(EndpointKeys.USER_NAME, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

            binding.textViewRequestYourData.setText("Un-link From " + userName + "?");
        }

        yourDataPopUp = new UnLnqPopUP();
        binding.setClickHandler(yourDataPopUp);
    }

    public class UnLnqPopUP {

        public void onOkayCLick(View view) {
            if (!userId.isEmpty()) {
                reqFavUnFavLnq();
            }
            getActivity().onBackPressed();
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }
    }

    private void reqFavUnFavLnq() {
        callUnLnq = Api.WEB_SERVICE.unLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, senderProfileId, receiverProfileId);
        callUnLnq.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("unlnq_user"));
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "", false));
                            getActivity().onBackPressed();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callUnLnq != null && callUnLnq.isExecuted()) {
            callUnLnq.cancel();
        }
        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.GONE);
    }
}