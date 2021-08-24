package lnq.com.lnq.fragments.setting.account.accountsetting;

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
import lnq.com.lnq.databinding.FragmentEditPasswordBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEditPassword extends Fragment implements View.OnClickListener {

    private FragmentEditPasswordBinding mBind;

    //    Api fields....
    private Call<RegisterLoginMainObject> callUpdatePass;

    public FragmentEditPassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_password, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.mBtnGoBack.setOnClickListener(this);
        mBind.mBtnUpdatePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnGoBack:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnUpdatePassword:
                if (!mBind.editTextOldPassword.getText().toString().isEmpty()) {
                    if (mBind.editTextOldPassword.getText().toString().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASS, ""))) {
                        if (!mBind.editTextNewPassWord.getText().toString().isEmpty()) {
                            reqUpdatePass(mBind.editTextOldPassword.getText().toString(), mBind.editTextNewPassWord.getText().toString());
                        } else {
                            ((MainActivity) getActivity()).showMessageDialog("error", "Enter New Password");
                        }
                    } else {
                        ((MainActivity) getActivity()).showMessageDialog("error", "Password does not match");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Enter Old Password");
                }
                break;
        }
    }

    private void reqUpdatePass(String oldPass, final String newPass) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), mBind.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callUpdatePass = Api.WEB_SERVICE.updatePassword(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), oldPass, newPass);
//        callUpdatePass = Api.WEB_SERVICE.updatePassword(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), oldPass, newPass);
        callUpdatePass.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASS, newPass);
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_PASSWORD, ValidUtils.md5(newPass));
                            LnqApplication.getInstance().editor.apply();
                            EventBus.getDefault().post(new EventBusUserSession("pass_updated"));
                            ((MainActivity) getActivity()).progressDialog.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "password");
                            ((MainActivity) getActivity()).fnLoadFragAdd("SUCCESS PASSWORD", true, bundle);
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
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