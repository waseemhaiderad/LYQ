package lnq.com.lnq.fragments.setting.account;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentVisibilitySettingBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.EventBusVisibilityStatusChanged;
import lnq.com.lnq.model.gson_converter_models.visibilitysettings.SetVisibilityMainObject;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentVisibilitySetting extends Fragment implements View.OnClickListener {

    private FragmentVisibilitySettingBinding mBind;

    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentProfile;

    //    Retrofit fields....
    private Call<SetVisibilityMainObject> callSetVisibility;
    int click = 1;
    int click1 = 1;
    String visible_at, visible_to;

    public FragmentVisibilitySetting() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_visibility_setting, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        EventBus.getDefault().register(this);
        mBind.imageViewBack.setOnClickListener(this);
        mBind.mBtnUpdateVisibility.setOnClickListener(this);
        mBind.mImgArrowSetDefaultVisibility.setOnClickListener(this);
        mBind.mTvSetDefaultVisibility.setOnClickListener(this);
        mBind.mImgArrowShowToSeePolicy.setOnClickListener(this);
        mBind.mTvVisibilitySettingWork.setOnClickListener(this);
        mBind.mTvShowToSeePolicy.setOnClickListener(this);
        mBind.mTvAccount1.setOnClickListener(this);
        mBind.mImgArrowVisibilitySettingWork.setOnClickListener(this);
        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentProfile = data;
                    }
                }
                fnVisibility();
            }
        });
        OverScrollDecoratorHelper.setUpOverScroll(mBind.nestedScrollView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvAccount1:
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnUpdateVisibility:
                ((MainActivity) getActivity()).fnLoadFragAdd("VISIBLE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("visibilty_view"));
                break;
            case R.id.mTvSetDefaultVisibility:
            case R.id.mImgArrowSetDefaultVisibility:
                reqSetVisibility(Constants.NEAR_BY, Constants.CITY, currentProfile.getId());
                break;
            case R.id.mTvShowToSeePolicy:
            case R.id.mImgArrowShowToSeePolicy:
                if (click == 1) {
                    mBind.mImgArrowShowToSeePolicy.setImageResource(R.drawable.icon_menu_down);
                    mBind.mTvLoremOpen.setVisibility(View.VISIBLE);
                    click = 0;
                } else if (click == 0) {
                    mBind.mImgArrowShowToSeePolicy.setImageResource(R.drawable.icon_menu_right);
                    mBind.mTvLoremOpen.setVisibility(View.GONE);
                    click = 1;
                }
                break;
            case R.id.mTvVisibilitySettingWork:
            case R.id.mImgArrowVisibilitySettingWork:
                if (click1 == 1) {
                    mBind.mImgArrowVisibilitySettingWork.setImageResource(R.drawable.icon_menu_down);
                    mBind.mTvLoremOpen1.setVisibility(View.VISIBLE);
                    click1 = 0;
                } else if (click1 == 0) {
                    mBind.mImgArrowVisibilitySettingWork.setImageResource(R.drawable.icon_menu_right);
                    mBind.mTvLoremOpen1.setVisibility(View.GONE);
                    click1 = 1;
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusVisibilityChanged(EventBusVisibilityStatusChanged eventBusVisibilityStatusChanged) {
        fnVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (callSetVisibility != null && callSetVisibility.isExecuted()) {
            callSetVisibility.cancel();
        }
    }

    private void reqSetVisibility(final String visibile_to, final String visible_at, String profileId) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callSetVisibility = Api.WEB_SERVICE.setVisibility(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), visibile_to, visible_at);
        callSetVisibility = Api.WEB_SERVICE.setVisibility(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), visibile_to, visible_at, profileId);
        callSetVisibility.enqueue(new Callback<SetVisibilityMainObject>() {
            @Override
            public void onResponse(Call<SetVisibilityMainObject> call, Response<SetVisibilityMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            EventBus.getDefault().post(new EventBusUserSession("visibility_updated"));
//                            LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_TO, visibile_to);
//                            LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_AT, visible_at);
//                            LnqApplication.getInstance().editor.apply();
                            currentProfile.setVisible_to(visibile_to);
                            currentProfile.setVisible_at(visible_at);
                            multiProfileRepositry.updateTask(currentProfile);
                            fnVisibility();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }

                }
            }

            @Override
            public void onFailure(Call<SetVisibilityMainObject> call, Throwable error) {
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

    void fnVisibility() {
        String value = "";
        String value1 = "";

//        visible_at = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VISIBLE_AT, "");
//        visible_to = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VISIBLE_TO, "");
        visible_at = currentProfile.getVisible_at();
        visible_to = currentProfile.getVisible_to();

        if (visible_to.isEmpty()) {
            value = "People Near Me";
        } else if (visible_to.equals("near_by")) {
            value = "People Near Me";
        } else if (visible_to.equals("every_one")) {
            value = "Everyone";
        }
        if (visible_at.isEmpty()) {
            value1 = "City";
        } else if (visible_at.equals("global")) {
            value1 = "Global Region";
        } else if (visible_at.equals("local")) {
            value1 = "Local Region";
        } else if (visible_at.equals("city")) {
            value1 = "City";
        }
        mBind.mTvOn.setText(value);
        mBind.mTvExactCity.setText(value1);

    }

}
