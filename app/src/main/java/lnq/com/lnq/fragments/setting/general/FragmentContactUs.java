package lnq.com.lnq.fragments.setting.general;


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
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.databinding.FragmentContactUsBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.defaultsetting.UserContactUs;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserData;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentContactUs extends Fragment implements View.OnClickListener {


    private FragmentContactUsBinding mBind;

    private Call<UserContactUs> mCallContactUs;
    String subject, message;

    public FragmentContactUs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.imageViewBack.setOnClickListener(this);
        mBind.mBtnSubmit.setOnClickListener(this);
        subject= mBind.mEtContact.getText().toString();
        message= mBind.mEtMessage.getText().toString();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBtnSubmit:
                reqContactUs(subject,message);
                break;
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
        }

    }

    private void reqContactUs(final String subject, String message) {
        ((MainActivity) getActivity()).progressDialog.show();
        mCallContactUs = Api.WEB_SERVICE.contactUs(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), subject, message);
        mCallContactUs.enqueue(new Callback<UserContactUs>() {
            @Override
            public void onResponse(Call<UserContactUs> call, Response<UserContactUs> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(EndpointKeys.SUBJECT, response.body().getSubject());
                            LnqApplication.getInstance().editor.putString(EndpointKeys.MESSAGE, response.body().getMessage());
                            ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.contacted));

                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserContactUs> call, Throwable error) {
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


