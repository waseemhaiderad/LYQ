package lnq.com.lnq.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentHomeMapProfilePopupMenuBinding;
import lnq.com.lnq.databinding.FragmentHomeMapProfilePopupNewBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.model.event_bus_models.EventBusHidePopup;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHomeMapProfilePopupNew extends Fragment implements CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentHomeMapProfilePopupNewBinding popUpMapBinding;
    private PopupMenuMapClickHandler popupMenuMapClickHandler;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callFavUnFavLnq;
    private Call<RegisterLoginMainObject> callBlackWhiteStatus;
    private Call<RegisterLoginMainObject> callShowHideLocation;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private String userId, userName, userConnectionStatus, isUserFavorite, isLocationHidden, userImage, senderProfileId, receiverProfileId;

    public FragmentHomeMapProfilePopupNew() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        popUpMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_map_profile_popup_new, container, false);
        return popUpMapBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
    }

    private void init() {
//        Register event bus for different triggers....
        EventBus.getDefault().register(this);

//        Setting custom font....
        setCustomFont();

        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID);
            userName = getArguments().getString(EndpointKeys.USER_NAME);
            userImage = getArguments().getString(EndpointKeys.USER_AVATAR);
            userConnectionStatus = getArguments().getString(EndpointKeys.USER_CONNECTION_STATUS, "");
            isUserFavorite = getArguments().getString(EndpointKeys.IS_USER_FAVORITE, "");
            isLocationHidden = getArguments().getString(EndpointKeys.IS_LOCATION_HIDDEN, "");
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

            if (userConnectionStatus != null) {
                if (isUserFavorite.equals(Constants.FAVORITE)) {
                    popUpMapBinding.textViewUnFavorite.setText(getResources().getString(R.string.un_favorite));
                } else {
                    popUpMapBinding.textViewUnFavorite.setText(getResources().getString(R.string.favorite));
                }
                if (userConnectionStatus.equals(Constants.CONNECTED)) {
                    popUpMapBinding.textViewUnLnq.setVisibility(View.VISIBLE);
                    popUpMapBinding.viewDividerFour.setVisibility(View.VISIBLE);
                } else {
                    popUpMapBinding.textViewUnLnq.setVisibility(View.GONE);
                    popUpMapBinding.viewDividerFour.setVisibility(View.INVISIBLE);
                }
                if (isLocationHidden.equalsIgnoreCase(Constants.HIDDEN)) {
                    popUpMapBinding.switchCompatLocationHidden.setChecked(true);
                } else {
                    popUpMapBinding.switchCompatLocationHidden.setChecked(false);
                }
                popUpMapBinding.switchCompatLocationHidden.setOnCheckedChangeListener(this);
            }
        }

//        Setting click handler for data binding....
        popupMenuMapClickHandler = new PopupMenuMapClickHandler();
        popUpMapBinding.setClickHandler(popupMenuMapClickHandler);

//        All event listeners....
        popUpMapBinding.switchCompatFullyBlock.setOnCheckedChangeListener(this);

        popUpMapBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), popUpMapBinding.mRoot);
                return false;
            }
        });

    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewSemiBold(popUpMapBinding.textViewSendMessage);
        fontUtils.setTextViewRegularFont(popUpMapBinding.textViewLocationHidden);
        fontUtils.setTextViewSemiBold(popUpMapBinding.textViewUnFavorite);
        fontUtils.setTextViewRegularFont(popUpMapBinding.textViewUnLnq);
        fontUtils.setTextViewRegularFont(popUpMapBinding.textViewFullyBlocks);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.VISIBLE);
        if (callFavUnFavLnq != null && callFavUnFavLnq.isExecuted()) {
            callFavUnFavLnq.cancel();
        }
        if (callBlackWhiteStatus != null && callBlackWhiteStatus.isExecuted()) {
            callBlackWhiteStatus.cancel();
        }
        if (callShowHideLocation != null && callShowHideLocation.isExecuted()) {
            callShowHideLocation.cancel();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == popUpMapBinding.switchCompatFullyBlock) {
            if (isChecked) {
                reqBlockUnblock(Constants.BLOCK, senderProfileId, receiverProfileId);
            } else {
                reqBlockUnblock(Constants.UNBLOCK, senderProfileId, receiverProfileId);
            }
        } else if (buttonView == popUpMapBinding.switchCompatLocationHidden) {
            if (isChecked) {
                reqHideShowLocation(Constants.HIDE, senderProfileId, receiverProfileId);
            } else {
                reqHideShowLocation(Constants.SHOW, senderProfileId, receiverProfileId);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventHidePopup(EventBusHidePopup mObj) {
        getActivity().onBackPressed();
    }

    private void reqFavUnFavLnq(final String status, String senderProfileid, String receiverProfileid) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callFavUnFavLnq = Api.WEB_SERVICE.favUnfavLNQ(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status);
        callFavUnFavLnq = Api.WEB_SERVICE.favUnfavLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileid, receiverProfileid);
        callFavUnFavLnq.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, status, false));
                            getActivity().onBackPressed();
                            if (status.equals(Constants.FAVORITE)) {
                                EventBus.getDefault().post(new EventBusUserSession("favourite_user"));
                            } else {
                                EventBus.getDefault().post(new EventBusUserSession("unfavourite_user"));

                            }
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

    private void reqBlockUnblock(final String status, String senderProfileid, String recevierProfileid) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callBlackWhiteStatus = Api.WEB_SERVICE.blockUnblockLNQ(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status);
        callBlackWhiteStatus = Api.WEB_SERVICE.blockUnblockLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileid, recevierProfileid);
        callBlackWhiteStatus.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (status.equals(Constants.BLOCK)) {
                                EventBus.getDefault().post(new EventBusUserSession("blocked_user"));

                                ((MainActivity) getActivity()).showMessageDialog("success", userName + " is now in your Black list.");
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.BLOCK, false));
                            } else {
                                EventBus.getDefault().post(new EventBusUserSession("unblocked_user"));

                                ((MainActivity) getActivity()).showMessageDialog("success", userName + " is removed from your Black list.");
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.UNBLOCK, false));
                            }
                            getActivity().onBackPressed();
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

    private void reqHideShowLocation(final String status, String senderProfileid, String receiverProfileid) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callShowHideLocation = Api.WEB_SERVICE.hideShowLocation(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status);
        callShowHideLocation = Api.WEB_SERVICE.hideShowLocation(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileid, receiverProfileid);
        callShowHideLocation.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (status.equals(Constants.HIDE)) {
                                EventBus.getDefault().post(new EventBusUserSession("location_hidden"));

                                ((MainActivity) getActivity()).showMessageDialog("success", "Location hidden from " + userName);
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.LOCATION_HIDE, false));
                            } else {
                                EventBus.getDefault().post(new EventBusUserSession("location_shown"));
                                ((MainActivity) getActivity()).showMessageDialog("success", "Location shown to " + userName);
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.LOCATION_SHOW, false));
                            }
                            getActivity().onBackPressed();
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

    public class PopupMenuMapClickHandler {

        public void onSendMessageClick(View view) {
            ((MainActivity) getActivity()).fragmentManager.popBackStack();
            Bundle bundleChat = new Bundle();
            bundleChat.putString(EndpointKeys.USER_ID, userId);
            bundleChat.putString(EndpointKeys.USER_NAME, userName);
            bundleChat.putString(EndpointKeys.USER_AVATAR, EndpointUrls.IMAGES_BASE_URL + userImage);
            bundleChat.putString(EndpointKeys.IS_FAVORITE, isUserFavorite);
            bundleChat.putString(EndpointKeys.USER_CONNECTION_STATUS, userConnectionStatus);
            EventBus.getDefault().post(new EventBusUserSession("chat_view"));

            ((MainActivity) getActivity()).fnLoadFragAdd("CHAT", true, bundleChat);
        }

        public void onUnFavoriteClick(View view) {
            if (isUserFavorite.equals(Constants.FAVORITE)) {
                reqFavUnFavLnq(Constants.UN_FAVORITE, senderProfileId, receiverProfileId);
            } else {
                reqFavUnFavLnq(Constants.FAVORITE, senderProfileId, receiverProfileId);
            }
        }

        public void onUnLnqClick(View view) {
            ((MainActivity) getActivity()).fragmentManager.popBackStack();
            Bundle bundle = new Bundle();
            bundle.putString(EndpointKeys.USER_ID, userId);
            bundle.putString(EndpointKeys.USER_NAME, userName);
            bundle.putString(EndpointKeys.PROFILE_ID, receiverProfileId);
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.UNLNQ_POPUP, true, bundle);
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }

    }

}