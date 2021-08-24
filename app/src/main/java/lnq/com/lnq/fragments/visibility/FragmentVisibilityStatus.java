package lnq.com.lnq.fragments.visibility;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.GroupVisibiltyAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.LinearLayoutManagerWithSmoothScroller;
import lnq.com.lnq.databinding.FragmentVisibilityStatusBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateGroupOffGridVisibilty;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateGroupVisibilty;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.EventBusVisibilityStatusChanged;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.CreateUserGroup;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserGetGroupMainObject;
import lnq.com.lnq.model.gson_converter_models.visibilitysettings.SetVisibilityMainObject;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentVisibilityStatus extends Fragment {

    //    Android fields....
    private FragmentVisibilityStatusBinding visibilityStatusBinding;
    private VisibilityStatusClickHandler clickHandler;

    //    Retrofit fields....
    private Call<SetVisibilityMainObject> callSetVisibility;

    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentProfile;
    private Call<UserGetGroupMainObject> callUserGetGroup;
    private List<CreateUserGroup> userGetGroupData = new ArrayList<>();
    private List<CreateUserGroup> userGetGroupDataTemp = new ArrayList<>();

    String profileId;
    GroupVisibiltyAdapter groupVisibiltyAdapter;

    public FragmentVisibilityStatus() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        visibilityStatusBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_visibility_status, container, false);
        return visibilityStatusBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        multiProfileRepositry = new MultiProfileRepositry(getContext());
//        Setting visibility selections....
//        changeStreetLevelSelection(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VISIBLE_TO, ""));
//        changeLocationSelection(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VISIBLE_AT, ""));

        profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentProfile = data;
                    }
                }
                if (currentProfile.getVisible_to() != null && currentProfile.getVisible_at() != null) {
                    changeStreetLevelSelection(currentProfile.getVisible_to());
                    changeLocationSelection(currentProfile.getVisible_at());
                    LnqApplication.getInstance().editor.putString("visible_to", currentProfile.getVisible_to()).apply();
                    LnqApplication.getInstance().editor.putString("visible_at", currentProfile.getVisible_at()).apply();
                }
            }
        });

        visibilityStatusBinding.recyclerViewGroupDataVisibility.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getContext()));

        reqShowAllGroup(profileId);

//        Setting click handler for data binding....
        clickHandler = new VisibilityStatusClickHandler(getActivity());
        visibilityStatusBinding.setClickHandler(clickHandler);
    }

    private void changeStreetLevelSelection(String selectionType) {
        switch (selectionType) {
            case Constants.NONE:
                visibilityStatusBinding.viewDividerNoboby.setVisibility(View.VISIBLE);
                visibilityStatusBinding.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerPeopleNearMe.setVisibility(View.INVISIBLE);

                visibilityStatusBinding.imageViewNobody.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_nobody_white));
                visibilityStatusBinding.imageViewEveryOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                visibilityStatusBinding.imageViewPeopleNearMe.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_people_near_me_transparent));

                visibilityStatusBinding.textViewNobody.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewPeopleNearMe.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewEveryOne.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.NEAR_BY:
                visibilityStatusBinding.viewDividerNoboby.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerPeopleNearMe.setVisibility(View.VISIBLE);

                visibilityStatusBinding.imageViewNobody.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                visibilityStatusBinding.imageViewEveryOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                visibilityStatusBinding.imageViewPeopleNearMe.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_people_near_me_white));

                visibilityStatusBinding.textViewNobody.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewPeopleNearMe.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewEveryOne.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.EVERY_ONE:
                visibilityStatusBinding.viewDividerNoboby.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerEveryOne.setVisibility(View.VISIBLE);
                visibilityStatusBinding.viewDividerPeopleNearMe.setVisibility(View.INVISIBLE);

                visibilityStatusBinding.imageViewNobody.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                visibilityStatusBinding.imageViewEveryOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_everyone_white));
                visibilityStatusBinding.imageViewPeopleNearMe.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_people_near_me_transparent));

                visibilityStatusBinding.textViewNobody.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewPeopleNearMe.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewEveryOne.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            default:
                visibilityStatusBinding.viewDividerNoboby.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerPeopleNearMe.setVisibility(View.VISIBLE);

                visibilityStatusBinding.imageViewNobody.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                visibilityStatusBinding.imageViewEveryOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                visibilityStatusBinding.imageViewPeopleNearMe.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_people_near_me_white));

                visibilityStatusBinding.textViewNobody.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewPeopleNearMe.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewEveryOne.setTextColor(getResources().getColor(R.color.colorWhiteTrans));
                break;
        }
    }

    private void changeLocationSelection(String selectionType) {
        switch (selectionType) {
            case Constants.OFF_GRID:
                visibilityStatusBinding.viewDividerCity.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerOffGrid.setVisibility(View.VISIBLE);

                visibilityStatusBinding.imageViewOffGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_off_grid_white));
                visibilityStatusBinding.imageViewGlobalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                visibilityStatusBinding.imageViewLocalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                visibilityStatusBinding.imageViewCity.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_city_transparent));

                visibilityStatusBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewGlobalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewLocalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.GLOBAL:
                visibilityStatusBinding.viewDividerCity.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerGlobalRegion.setVisibility(View.VISIBLE);
                visibilityStatusBinding.viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                visibilityStatusBinding.imageViewOffGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                visibilityStatusBinding.imageViewGlobalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_global_region_white));
                visibilityStatusBinding.imageViewLocalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                visibilityStatusBinding.imageViewCity.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_city_transparent));

                visibilityStatusBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewGlobalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewLocalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.LOCAL:
                visibilityStatusBinding.viewDividerCity.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerLocalRegion.setVisibility(View.VISIBLE);
                visibilityStatusBinding.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                visibilityStatusBinding.imageViewOffGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                visibilityStatusBinding.imageViewGlobalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                visibilityStatusBinding.imageViewLocalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_local_region_white));
                visibilityStatusBinding.imageViewCity.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_city_transparent));

                visibilityStatusBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewGlobalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewLocalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.CITY:
                visibilityStatusBinding.viewDividerCity.setVisibility(View.VISIBLE);
                visibilityStatusBinding.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                visibilityStatusBinding.imageViewOffGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                visibilityStatusBinding.imageViewGlobalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                visibilityStatusBinding.imageViewLocalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                visibilityStatusBinding.imageViewCity.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_city_white));

                visibilityStatusBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewGlobalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewLocalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            default:
                visibilityStatusBinding.viewDividerCity.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                visibilityStatusBinding.viewDividerLocalRegion.setVisibility(View.VISIBLE);
                visibilityStatusBinding.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                visibilityStatusBinding.imageViewOffGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                visibilityStatusBinding.imageViewGlobalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                visibilityStatusBinding.imageViewLocalRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_local_region_white));
                visibilityStatusBinding.imageViewCity.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_city_transparent));

                visibilityStatusBinding.textViewCity.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewGlobalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewLocalRegion.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                visibilityStatusBinding.textViewOffGrid.setTextColor(getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
        }
    }

    private void reqSetVisibility(final String visibile_to, final String visible_at, String profileId) {
        callSetVisibility = Api.WEB_SERVICE.setVisibility(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), visibile_to, visible_at, profileId);
        callSetVisibility.enqueue(new Callback<SetVisibilityMainObject>() {
            @Override
            public void onResponse(Call<SetVisibilityMainObject> call, Response<SetVisibilityMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (!visibile_to.isEmpty())
//                                LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_TO, visibile_to);
                                currentProfile.setVisible_to(visibile_to);
                            multiProfileRepositry.updateTask(currentProfile);
                            if (!visible_at.isEmpty())
//                                LnqApplication.getInstance().editor.putString(EndpointKeys.VISIBLE_AT, visible_at);
//                            LnqApplication.getInstance().editor.apply();
                                currentProfile.setVisible_at(visible_at);
                            multiProfileRepositry.updateTask(currentProfile);
                            EventBus.getDefault().post(new EventBusVisibilityStatusChanged());
                            EventBus.getDefault().post(new EventBusUserSession("visibility_updated"));
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

    private void reqSetGroupVisibility(String groupId, String profileId, final String visibile_to, final String visible_at) {
        callSetVisibility = Api.WEB_SERVICE.setGroupVisibility(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), groupId, LnqApplication.getInstance().sharedPreferences.getString("id", ""), profileId, visibile_to, visible_at);
        callSetVisibility.enqueue(new Callback<SetVisibilityMainObject>() {
            @Override
            public void onResponse(Call<SetVisibilityMainObject> call, Response<SetVisibilityMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
//                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
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

    public void reqShowAllGroup(String profile_id) {
        userGetGroupData.clear();
        callUserGetGroup = Api.WEB_SERVICE.getUserGroups(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), profile_id);

        callUserGetGroup.enqueue(new Callback<UserGetGroupMainObject>() {
            @Override
            public void onResponse(Call<UserGetGroupMainObject> call, Response<UserGetGroupMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            userGetGroupData = response.body().getGetUserGroup();
                            for (int i = 0; i < userGetGroupData.size(); i++) {
                                if (userGetGroupData.get(i).getVisible_to() != null && userGetGroupData.get(i).getVisible_at() != null){
                                    userGetGroupDataTemp.add(userGetGroupData.get(i));
                                }

                            }
                            groupVisibiltyAdapter = new GroupVisibiltyAdapter(getContext(), userGetGroupDataTemp);
                            visibilityStatusBinding.recyclerViewGroupDataVisibility.setAdapter(groupVisibiltyAdapter);
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserGetGroupMainObject> call, Throwable error) {
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

    public class VisibilityStatusClickHandler {

        private Context context;

        VisibilityStatusClickHandler(Context context) {
            this.context = context;
        }

        public void onVisibilitySettingClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VISIBILITY_SETTING, true, null);
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void onPeopleNearMeClick(View view) {
            reqSetVisibility(Constants.NEAR_BY, "", currentProfile.getId());
            changeStreetLevelSelection(Constants.NEAR_BY);
        }

        public void onEveryOneClick(View view) {
            reqSetVisibility(Constants.EVERY_ONE, "", currentProfile.getId());
            changeStreetLevelSelection(Constants.EVERY_ONE);
        }

        public void onOffGridClick(View view) {
            reqSetVisibility("", Constants.OFF_GRID, currentProfile.getId());
            changeLocationSelection(Constants.OFF_GRID);
        }

        public void onGlobalRegionClick(View view) {
            reqSetVisibility("", Constants.GLOBAL, currentProfile.getId());
            changeLocationSelection(Constants.GLOBAL);
        }

        public void onLocalRegionClick(View view) {
            reqSetVisibility("", Constants.LOCAL, currentProfile.getId());
            changeLocationSelection(Constants.LOCAL);
        }

        public void onCityClick(View view) {
            reqSetVisibility("", Constants.CITY, currentProfile.getId());
            changeLocationSelection(Constants.CITY);
        }

        public void onNobodyClick(View view) {
            reqSetVisibility(Constants.NONE, "", currentProfile.getId());
            changeStreetLevelSelection(Constants.NONE);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateGroupVisibilty(EventBusUpdateGroupVisibilty mObj){
        if (mObj.getType().equalsIgnoreCase(Constants.NONE)){
            reqSetGroupVisibility(mObj.getGroupId(), profileId, Constants.NONE, "");
        }else if (mObj.getType().equalsIgnoreCase(Constants.NEAR_BY)){
            reqSetGroupVisibility(mObj.getGroupId(), profileId, Constants.NEAR_BY, "");
        }else if (mObj.getType().equalsIgnoreCase(Constants.EVERY_ONE)){
            reqSetGroupVisibility(mObj.getGroupId(), profileId, Constants.EVERY_ONE, "");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateGroupOffGridVisibilty(EventBusUpdateGroupOffGridVisibilty mObj){
        if (mObj.getType().equalsIgnoreCase(Constants.OFF_GRID)){
            reqSetGroupVisibility(mObj.getGroupId(), profileId, "", Constants.OFF_GRID);
        } else if (mObj.getType().equalsIgnoreCase(Constants.GLOBAL)){
            reqSetGroupVisibility(mObj.getGroupId(), profileId, "", Constants.GLOBAL);
        }else if (mObj.getType().equalsIgnoreCase(Constants.LOCAL)){
            reqSetGroupVisibility(mObj.getGroupId(), profileId, "", Constants.LOCAL);
        }else if (mObj.getType().equalsIgnoreCase(Constants.CITY)){
            reqSetGroupVisibility(mObj.getGroupId(), profileId, "", Constants.CITY);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
