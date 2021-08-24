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
import lnq.com.lnq.databinding.FragmentRetractRequestBinding;
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


public class FragmentRetractRequest extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentRetractRequestBinding reqtractRequestBinding;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> mCallRetractRequest;

    //    Instance fields....
    private String userId, senderProfileId, receiverProfileId;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentRetractRequest() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reqtractRequestBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_retract_request, container, false);
        return reqtractRequestBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID);
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        }

//        Setting custom fonts....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewMedium(reqtractRequestBinding.clearTextViewCancel);
        fontUtils.setButtonMedium(reqtractRequestBinding.buttonOk);
        fontUtils.setTextViewRegularFont(reqtractRequestBinding.textViewRetractRequestHeading);
        fontUtils.setTextViewRegularFont(reqtractRequestBinding.textViewRetractRequestDes);

//        All event listeners....
        reqtractRequestBinding.imageViewBack.setOnClickListener(this);
        reqtractRequestBinding.clearTextViewCancel.setOnClickListener(this);
        reqtractRequestBinding.buttonOk.setOnClickListener(this);

        ValidUtils.buttonGradientColor(reqtractRequestBinding.clearTextViewCancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearTextViewCancel:
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.buttonOk:
                reqContactRequest(userId, senderProfileId, receiverProfileId);
                break;
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
        ((MainActivity) getActivity()).progressDialog.show();
//        mCallRetractRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId);
        mCallRetractRequest = Api.WEB_SERVICE.contactRequestCancel(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, senderProfileid, receiverProfileid);
        mCallRetractRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("retract_request"));
//                            ((MainActivity) getActivity()).showMessageDialog("success", "LNQ request retracted successfully.");
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "cancel",false));
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
