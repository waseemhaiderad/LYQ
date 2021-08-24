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
import lnq.com.lnq.api.Api;


import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentUnLnqUserBinding;
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

public class FragmentUnLnqUser extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentUnLnqUserBinding unLnqBinding;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callUnLnq;

    //    Instance fields....
    private String userId, senderProfileId, receiverProfileId;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentUnLnqUser() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        unLnqBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_lnq_user, container, false);

        return unLnqBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID, "");
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        }

//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(unLnqBinding.textViewUnLnqHeading);
        fontUtils.setTextViewRegularFont(unLnqBinding.textViewUnLnqDes);
        fontUtils.setTextViewMedium(unLnqBinding.clearTextViewOk);
        fontUtils.setButtonMedium(unLnqBinding.buttonCancel);

//        All event listeners....
        unLnqBinding.imageViewBack.setOnClickListener(this);
        unLnqBinding.buttonCancel.setOnClickListener(this);
        unLnqBinding.clearTextViewOk.setOnClickListener(this);

        ValidUtils.buttonGradientColor(unLnqBinding.buttonCancel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callUnLnq != null && callUnLnq.isExecuted()) {
            callUnLnq.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
            case R.id.buttonCancel:
                getActivity().onBackPressed();
                break;
            case R.id.clearTextViewOk:
                if (!userId.isEmpty()) {
                    reqFavUnFavLnq();
                }
                break;
        }
    }

    private void reqFavUnFavLnq() {
        ((MainActivity) getActivity()).progressDialog.show();
//        callUnLnq = Api.WEB_SERVICE.unLNQ(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId);
        callUnLnq = Api.WEB_SERVICE.unLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, senderProfileId, receiverProfileId);
        callUnLnq.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("unlnq_user"));
//                            ((MainActivity) getActivity()).showMessageDialog("success", "Unlink from " + userName + " successfully.");
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "",false));
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
