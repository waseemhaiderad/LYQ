package lnq.com.lnq.fragments.lnqrequest;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.databinding.FragmentAcceptRequestBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAcceptRequest extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentAcceptRequestBinding acceptRequestBinding;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callAcceptRequest;
    private Call<UpdateLocationMainObject> callDeclineRequest;

    //    Instance fields....
    private String userId, userName, senderProfileId, receiverProfileId;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentAcceptRequest() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        acceptRequestBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_accept_request, container, false);
        return acceptRequestBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID, "");
            userName = getArguments().getString(EndpointKeys.USER_NAME, "");
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        }
        if (userName != null) {
            acceptRequestBinding.textViewAcceptRequestHeading.setText(userName + " Would like to LNQ");
        }

//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(acceptRequestBinding.textViewAcceptRequestHeading);
        fontUtils.setTextViewRegularFont(acceptRequestBinding.textViewAcceptRequestDes);
        fontUtils.setTextViewMedium(acceptRequestBinding.clearTextViewAccept);
        fontUtils.setButtonMedium(acceptRequestBinding.buttonDecline);

//        All event listeners....
        acceptRequestBinding.imageViewBack.setOnClickListener(this);
        acceptRequestBinding.clearTextViewAccept.setOnClickListener(this);
        acceptRequestBinding.buttonDecline.setOnClickListener(this);

        ValidUtils.buttonGradientColor(acceptRequestBinding.buttonDecline);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.clearTextViewAccept:
                if (!((MainActivity) getActivity()).fnIsisOnline()) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                    return;
                }
                if (userId == null || userId.equals("")) {
                    return;
                }
                reqAcceptContactRequest(userId, receiverProfileId, senderProfileId);
                break;
            case R.id.buttonDecline:
                if (!((MainActivity) getActivity()).fnIsisOnline()) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                    return;
                }
                if (userId == null || userId.equals("")) {
                    return;
                }
                reqDeclineContactRequest(userId, senderProfileId, receiverProfileId);
                break;
        }
    }

    private void reqAcceptContactRequest(final String userId, String senderProfileid, String receiverProfileid) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callAcceptRequest = Api.WEB_SERVICE.contactRequestAccpet(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        callAcceptRequest = Api.WEB_SERVICE.contactRequestAccpet(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""), senderProfileid, receiverProfileid);
        callAcceptRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_accepted"));

//                            ((MainActivity) getActivity()).showMessageDialog("success", "Request accepted successfully.");
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "connected", false));
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

    private void reqDeclineContactRequest(final String userId, String senderProfileid, String receiverProfileid) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callDeclineRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY,  DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        callDeclineRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), userId, LnqApplication.getInstance().sharedPreferences.getString("id", ""), senderProfileid, receiverProfileid);
        callDeclineRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_declined"));

//                            ((MainActivity) getActivity()).showMessageDialog("success", "LNQ request declined successfully.");
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "cancel", false));
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