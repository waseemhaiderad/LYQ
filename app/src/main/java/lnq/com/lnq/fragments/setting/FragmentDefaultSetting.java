package lnq.com.lnq.fragments.setting;

import android.content.Context;

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

import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentDefaultSettingBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.defaultsetting.DefaultSetting;
import lnq.com.lnq.model.event_bus_models.EventBusMapSettingChanged;
import lnq.com.lnq.model.event_bus_models.EventBusMapGridViewChanged;
import lnq.com.lnq.model.event_bus_models.EventBusMapView;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDefaultSetting extends Fragment {

    FragmentDefaultSettingBinding defaultSettingBinding;
    private DefaultStatusClickHandler clickHandler;

    //    Retrofit fields....
    private Call<DefaultSetting> callSetVisibility;

    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentProfile;

    public FragmentDefaultSetting() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        defaultSettingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_default_setting, container, false);
        return defaultSettingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        multiProfileRepositry = new MultiProfileRepositry(getContext());
//        Setting visibility selections....
//        onMapViewClicked(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.MAPVIEW, ""));
//        onConnectionClicked(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.CONNECTIONVIEW, ""));

        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentProfile = data;
                    }
                }
                onMapViewClicked(currentProfile.getHome_default_view());
                onConnectionClicked(currentProfile.getContact_default_view());
            }
        });

//        Setting click handler for data binding....
        clickHandler = new FragmentDefaultSetting.DefaultStatusClickHandler(getActivity());
        defaultSettingBinding.setClickHandler(clickHandler);
    }

    private void onMapViewClicked(String clickedTyped) {
        switch (clickedTyped) {
            case Constants.MapClicked:
                defaultSettingBinding.viewDividerNoboby.setVisibility(View.VISIBLE);
                defaultSettingBinding.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                defaultSettingBinding.textViewNobody.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                defaultSettingBinding.textViewEveryOne.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.GridClicked:
                defaultSettingBinding.viewDividerNoboby.setVisibility(View.INVISIBLE);
                defaultSettingBinding.viewDividerEveryOne.setVisibility(View.VISIBLE);
                defaultSettingBinding.textViewNobody.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                defaultSettingBinding.textViewEveryOne.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            default:
                defaultSettingBinding.viewDividerNoboby.setVisibility(View.VISIBLE);
                defaultSettingBinding.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                defaultSettingBinding.textViewNobody.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                defaultSettingBinding.textViewEveryOne.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
        }
    }

    private void onConnectionClicked(String clickedType) {
        switch (clickedType) {
            case Constants.ListClicked:
                defaultSettingBinding.viewDividerCity.setVisibility(View.INVISIBLE);
                defaultSettingBinding.viewDividerOffGrid.setVisibility(View.VISIBLE);
                defaultSettingBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                defaultSettingBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.ConnectionGrid:
                defaultSettingBinding.viewDividerCity.setVisibility(View.VISIBLE);
                defaultSettingBinding.viewDividerOffGrid.setVisibility(View.INVISIBLE);
                defaultSettingBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                defaultSettingBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            default:
                defaultSettingBinding.viewDividerCity.setVisibility(View.INVISIBLE);
                defaultSettingBinding.viewDividerOffGrid.setVisibility(View.VISIBLE);
                defaultSettingBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                defaultSettingBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
        }
    }

    public void requestMapViewChanges(String viewClicked, String type, String profileId) {
        if (type.equalsIgnoreCase("Map") || type.equalsIgnoreCase("MapGrid")) {
            callSetVisibility = Api.WEB_SERVICE.setDefaultSetting(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), viewClicked, "", profileId);
        } else {
            callSetVisibility = Api.WEB_SERVICE.setDefaultSetting(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), "", viewClicked, profileId);
        }
        callSetVisibility.enqueue(new Callback<DefaultSetting>() {
            @Override
            public void onResponse(Call<DefaultSetting> call, Response<DefaultSetting> response) {
                if (response.isSuccessful() && response != null) {
                    DefaultSetting defaultSetting = response.body();
                    switch (defaultSetting.getStatus()) {
                        case 1:
                            if (type.equalsIgnoreCase("Map") || type.equalsIgnoreCase("MapGrid")) {
                                if (defaultSetting.getSetDefaultSetting().getHomeDefaultView().equals("grid")) {
//                                    LnqApplication.getInstance().editor.putString(EndpointKeys.MAPVIEW, Constants.GridClicked);
//                                    LnqApplication.getInstance().editor.apply();
                                    currentProfile.setHome_default_view(Constants.GridClicked);
                                    multiProfileRepositry.updateTask(currentProfile);
                                } else {
//                                    LnqApplication.getInstance().editor.putString(EndpointKeys.MAPVIEW, Constants.MapClicked);
//                                    LnqApplication.getInstance().editor.apply();
                                    currentProfile.setHome_default_view(Constants.MapClicked);
                                    multiProfileRepositry.updateTask(currentProfile);
                                }
                            } else {
                                if (defaultSetting.getSetDefaultSetting().getContactDefaultView().equals("list")) {
//                                    LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTIONVIEW, Constants.ListClicked);
//                                    LnqApplication.getInstance().editor.apply();
                                    currentProfile.setHome_default_view(Constants.ListClicked);
                                    multiProfileRepositry.updateTask(currentProfile);
                                } else {
//                                    LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTIONVIEW, Constants.ConnectionGrid);
//                                    LnqApplication.getInstance().editor.apply();
                                    currentProfile.setHome_default_view(Constants.ConnectionGrid);
                                    multiProfileRepositry.updateTask(currentProfile);
                                }
                            }
//                            EventBus.getDefault().post(new EventBusMapView());
//                            EventBus.getDefault().post(new EventBusMapGridViewChanged());
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<DefaultSetting> call, Throwable t) {
                if (type.equalsIgnoreCase("Map") || type.equalsIgnoreCase("MapGrid")) {
                    LnqApplication.getInstance().editor.putString(EndpointKeys.MAPVIEW, "");
                    LnqApplication.getInstance().editor.apply();
                } else {
                    LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTIONVIEW, "");
                    LnqApplication.getInstance().editor.apply();
                }
            }
        });
    }

    public class DefaultStatusClickHandler {

        private Context context;

        DefaultStatusClickHandler(Context context) {
            this.context = context;
        }

        public void onDefaultSettingClick(View view) {

        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void onMapClick(View view) {
            requestMapViewChanges(Constants.MapClicked, "Map", currentProfile.getId());
            EventBus.getDefault().post(new EventBusMapSettingChanged());
            onMapViewClicked(Constants.MapClicked);
        }

        public void onGridClick(View view) {
            requestMapViewChanges(Constants.GridClicked, "MapGrid", currentProfile.getId());
            EventBus.getDefault().post(new EventBusMapSettingChanged());
            onMapViewClicked(Constants.GridClicked);
        }

        public void onListClick(View view) {
            requestMapViewChanges(Constants.ListClicked, "List", currentProfile.getId());
            onConnectionClicked(Constants.ListClicked);
        }

        public void onConnectionsGridClick(View view) {
            requestMapViewChanges(Constants.ConnectionGrid, "ConnectionsGrid", currentProfile.getId());
            onConnectionClicked(Constants.ConnectionGrid);
        }
    }
}
