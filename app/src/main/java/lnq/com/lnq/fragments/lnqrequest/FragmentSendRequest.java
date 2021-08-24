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
import lnq.com.lnq.databinding.FragmentSendRequestBinding;
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


public class FragmentSendRequest extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentSendRequestBinding sendRequestBinding;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callRequest;

    //    Instance fields....
    private String userId, userName, senderProfileId, receiverProfileId;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentSendRequest() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sendRequestBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_request, container, false);
        return sendRequestBinding.getRoot();
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
                sendRequestBinding.textViewSendRequestHeading.setText("LNQ with " + userName);
        }

//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(sendRequestBinding.textViewSendRequestHeading);
        fontUtils.setTextViewRegularFont(sendRequestBinding.textViewSendRequestDes);
        fontUtils.setTextViewMedium(sendRequestBinding.clearTextViewOk);
        fontUtils.setButtonMedium(sendRequestBinding.buttonCancel);

//        All event listeners....
        sendRequestBinding.imageViewBack.setOnClickListener(this);
        sendRequestBinding.buttonCancel.setOnClickListener(this);
        sendRequestBinding.clearTextViewOk.setOnClickListener(this);
        ValidUtils.buttonGradientColor(sendRequestBinding.buttonCancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.buttonCancel:
                getActivity().onBackPressed();
                break;
            case R.id.clearTextViewOk:
                if (!((MainActivity) getActivity()).fnIsisOnline()) {
                    LnqApplication.getInstance().snakeBar(sendRequestBinding.getRoot(), getResources().getString(R.string.wifi_internet_not_connected), getResources().getColor(R.color.colorError));
                    return;
                }
                if (userId == null || userId.equals(""))
                    return;
                reqContactRequest(userId, senderProfileId, receiverProfileId);
                break;
        }
    }

    private void reqContactRequest(final String userId, String senderProfileid, String recevierProfileId) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId,"map");
        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId,"map", senderProfileid, recevierProfileId);
        callRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", "Request sent to " + userName);
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_sent"));
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "contacted",false));
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