package lnq.com.lnq.fragments.profile.editprofile;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.databinding.FragmentEditBioBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserData;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEditBio extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentEditBioBinding mBind;

    //    Retrofit fields....
    private Call<CreateUserProfileMainObject> mCallUpdateUserBio;

    //    Font fields....
    private FontUtils fontUtils;
    private MultiProfileRoomModel currentProfile;

    private MultiProfileRepositry multiProfileRepositry;

    public FragmentEditBio() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_bio, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        ((MainActivity) getActivity()).fnShowKeyboardFrom(mBind.mRoot);
//        mBind.mEtBio.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_BIO, ""));
        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels){
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))){
                        mBind.mEtBio.setText(data.getUser_bio());
                        currentProfile = data;
                    }
                }
            }
        });
        mBind.mEtBio.requestFocus();
        mBind.mEtBio.setSelection(mBind.mEtBio.getText().length());

//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(mBind.mTvEditBioHeading);
        fontUtils.setTextViewRegularFont(mBind.mTvEditBioDes);
        fontUtils.setEditTextRegularFont(mBind.mEtBio);
        fontUtils.setTextViewRegularFont(mBind.mBtnSaveChange);

        mBind.mImgBack.setOnClickListener(this);
        mBind.mBtnSaveChange.setOnClickListener(this);
        mBind.mTvEditBioHeading.setOnClickListener(this);

        mBind.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), mBind.mRoot);
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvEditBioHeading:
            case R.id.mImgBack:
                ValidUtils.hideKeyboardFromFragment(getContext(), mBind.getRoot());
                getActivity().onBackPressed();
                break;
            case R.id.mBtnSaveChange:
                if (!((MainActivity) getActivity()).fnIsisOnline()) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                    return;
                }
                if (mBind.mEtBio.getText().toString().isEmpty()) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_bio));
                    return;
                }
                reqUpdateUserBio(mBind.mEtBio.getText().toString(), currentProfile.getId());
                break;
        }
    }

    private void reqUpdateUserBio(final String bio, String profileId) {
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(mBind.mRoot);
        ((MainActivity) getActivity()).progressDialog.show();
//        mCallUpdateUserBio = Api.WEB_SERVICE.updateUserBio(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), bio);
        mCallUpdateUserBio = Api.WEB_SERVICE.updateUserBio(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), bio, profileId);
        mCallUpdateUserBio.enqueue(new Callback<CreateUserProfileMainObject>() {
            @Override
            public void onResponse(Call<CreateUserProfileMainObject> call, Response<CreateUserProfileMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            currentProfile.setUser_bio(bio);
                            multiProfileRepositry.updateTask(currentProfile);
                            ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.bio_updated));
                            EventBus.getDefault().post(new EventBusUserSession("bio_updated"));
                            EventBus.getDefault().post(new EventBusRefreshUserData());
                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateUserProfileMainObject> call, Throwable error) {
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