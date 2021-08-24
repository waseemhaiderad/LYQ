package lnq.com.lnq.fragments.home;

import android.annotation.SuppressLint;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.HomeMapProfilePagerAdapter;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentHomeMapProfileBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusCloseMapProfile;
import lnq.com.lnq.model.event_bus_models.EventBusIsFragmentVisible;
import lnq.com.lnq.model.event_bus_models.EventBusMapMarkerChange;
import lnq.com.lnq.model.event_bus_models.EventBusProfileClose;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;
import lnq.com.lnq.model.event_bus_models.EventBusGridUsersList;
import lnq.com.lnq.model.event_bus_models.EventBusMapLnqClick;
import lnq.com.lnq.model.event_bus_models.EventBusMapProfileClick;
import lnq.com.lnq.model.event_bus_models.EventBusMapSearchTextUpdate;
import lnq.com.lnq.model.event_bus_models.EventBusSendUsersList;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class FragmentHomeMapProfile extends Fragment implements View.OnTouchListener, TextWatcher, TextView.OnEditorActionListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentHomeMapProfileBinding mapProfileBinding;
    private HomeMapProfileClickHandler clickHandler;

    //    Adapter fields....
    private HomeMapProfilePagerAdapter homeMapProfilePagerAdapter;
    private List<UpdateLocationData> updateLocationDataArrayList = new ArrayList<>();

    //    Instance fields....
    private int clickedUserProfileIndex;
    private String searchSuggestion;

    private SlideUp slideUp;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;
    CardView topBarLayout;
    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentHomeMapProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_map_profile, container, false);
        return mapProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        topBarLayout = mapProfileBinding.topBarContact.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.explore);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);
        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), mapProfileBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(mapProfileBinding.slideViewContacts)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    mapProfileBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);

                                } else {
                                    mapProfileBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .withStartGravity(Gravity.TOP)
                        .withLoggingEnabled(true)
                        .withGesturesEnabled(true)
                        .withStartState(SlideUp.State.SHOWED)
                        .build();
            }
        });

        imageViewContactGridTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag().equals("grid")) {
                    v.setTag("map");
                    imageViewContactGridTopBar.setImageResource(R.mipmap.icon_map_new);
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataArrayList));
                            EventBus.getDefault().post(new EventBusUserSession("grid_view"));

                        }
                    }, 100);
                } else {
                    v.setTag("grid");
                    imageViewContactGridTopBar.setImageResource(R.mipmap.icon_grid_new);

                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
//        Registering event bus for different triggers....
        EventBus.getDefault().register(this);

//        Setting custom fonts....
        setCustomFont();

//        Setting default values for android views....
        mapProfileBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));
        toggleFilterButtonBackground();
        if (mapProfileBinding.editTextSearch.getText().toString().isEmpty()) {
            mapProfileBinding.imageViewClose.setVisibility(View.GONE);
        } else {
            mapProfileBinding.imageViewClose.setVisibility(View.VISIBLE);
        }

//        Setting click handler for data binding....
        clickHandler = new HomeMapProfileClickHandler();
        mapProfileBinding.setClickHandler(clickHandler);

//        All event listeners....
        mapProfileBinding.loopingViewPager.setOnTouchListener(this);
        mapProfileBinding.editTextSearch.addTextChangedListener(this);
        mapProfileBinding.editTextSearch.setOnEditorActionListener(this);

        mapProfileBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), mapProfileBinding.mRoot);
                return false;
            }
        });
        mapProfileBinding.loopingViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                EventBus.getDefault().post(new EventBusMapMarkerChange(position, updateLocationDataArrayList.get(position).getUser_lat(), updateLocationDataArrayList.get(position).getUser_long()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        searchSuggestion = LnqApplication.getInstance().sharedPreferences.getString("search_suggestion", "");
        if (!searchSuggestion.isEmpty()) {
            List<String> suggestionArray = Arrays.asList(searchSuggestion.split(","));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, suggestionArray);
            mapProfileBinding.editTextSearch.setAdapter(arrayAdapter);
            mapProfileBinding.editTextSearch.setThreshold(1);
        }

        slideUp = new SlideUpBuilder(mapProfileBinding.slideViewContacts)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            mapProfileBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
                        } else {
                            mapProfileBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(mapProfileBinding.viewScroll)
                .build();

        mapProfileBinding.mTvAccountHeading1.setOnClickListener(this);
        mapProfileBinding.imageViewBack.setOnClickListener(this);
        mapProfileBinding.textViewClearAll.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusFragmentVisible(EventBusIsFragmentVisible eventBusIsFragmentVisible) {
        for (int i = 0; i < updateLocationDataArrayList.size(); i++) {
            if (updateLocationDataArrayList.get(i).getSender_id().equals(eventBusIsFragmentVisible.getId()) ||
                    updateLocationDataArrayList.get(i).getReceiver_id().equals(eventBusIsFragmentVisible.getId())) {
                mapProfileBinding.loopingViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setEditTextSemiBold(mapProfileBinding.editTextSearch);
        fontUtils.setTextViewRegularFont(mapProfileBinding.clearTextViewDistance);
        fontUtils.setTextViewRegularFont(mapProfileBinding.textViewShowOnly);
        fontUtils.setTextViewRegularFont(mapProfileBinding.textViewFavorites);
        fontUtils.setTextViewRegularFont(mapProfileBinding.textViewVerifiedProfiles);
        fontUtils.setTextViewRegularFont(mapProfileBinding.textViewPendingLnq);
        fontUtils.setTextViewRegularFont(mapProfileBinding.textViewOutstandingTasks);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusMapProfileClose(EventBusCloseMapProfile mObj){
        getActivity().onBackPressed();
    }

    //    Getting users list from fragment home using event bus....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetUsersList(EventBusSendUsersList eventBusSendUsersList) {
        toggleFilterButtonBackground();
        updateLocationDataArrayList.clear();
        if (eventBusSendUsersList.getUpdateLocationDataList().size() > 0) {
            updateLocationDataArrayList.addAll(eventBusSendUsersList.getUpdateLocationDataList());
            homeMapProfilePagerAdapter = new HomeMapProfilePagerAdapter(getActivity(), updateLocationDataArrayList);
            mapProfileBinding.loopingViewPager.setAdapter(homeMapProfilePagerAdapter);
            clickedUserProfileIndex = eventBusSendUsersList.getIndex();
            mapProfileBinding.loopingViewPager.setCurrentItem(clickedUserProfileIndex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRefreshUserStatus(EventBusUpdateUserStatus eventBusUpdateUserStatus) {
        String userId = eventBusUpdateUserStatus.getUserId();
        int refreshUserIndex = -1;
        for (int i = 0; i < updateLocationDataArrayList.size(); i++) {
            if (updateLocationDataArrayList.get(i).getReceiver_id().equals(userId)) {
                refreshUserIndex = i;
                updateUserConnectionStatus(eventBusUpdateUserStatus.getUserStatus(), i, userId, eventBusUpdateUserStatus.isFromFcm());
                break;
            } else if (updateLocationDataArrayList.get(i).getSender_id().equals(userId)) {
                refreshUserIndex = i;
                updateUserConnectionStatus(eventBusUpdateUserStatus.getUserStatus(), i, userId, eventBusUpdateUserStatus.isFromFcm());
                break;
            }
        }
        homeMapProfilePagerAdapter = new HomeMapProfilePagerAdapter(getActivity(), updateLocationDataArrayList);
        mapProfileBinding.loopingViewPager.setAdapter(homeMapProfilePagerAdapter);
        if (!eventBusUpdateUserStatus.getUserStatus().equals("block"))
            if (refreshUserIndex != -1)
                mapProfileBinding.loopingViewPager.setCurrentItem(refreshUserIndex);
    }

    private void updateUserConnectionStatus(String userStatus, int index, String userId, boolean isFromFcm) {
        if (userStatus.equals(Constants.CONTACTED)) {
            updateLocationDataArrayList.get(index).setIs_connection(Constants.CONTACTED);
            if (isFromFcm) {
                updateLocationDataArrayList.get(index).setSender_id(userId);
                updateLocationDataArrayList.get(index).setReceiver_id(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
            } else {
                updateLocationDataArrayList.get(index).setSender_id(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
                updateLocationDataArrayList.get(index).setReceiver_id(userId);
            }
        } else if (userStatus.equals(Constants.CANCEL)) {
            updateLocationDataArrayList.get(index).setIs_connection("");
        } else if (userStatus.equals(Constants.CONNECTED)) {
            updateLocationDataArrayList.get(index).setIs_connection(Constants.CONNECTED);
        } else if (userStatus.equals(Constants.FAVORITE)) {
            updateLocationDataArrayList.get(index).setIs_favorite(Constants.FAVORITE);
        } else if (userStatus.equals(Constants.UN_FAVORITE)) {
            updateLocationDataArrayList.get(index).setIs_favorite("");
        } else if (userStatus.equals(Constants.LOCATION_HIDE)) {
            updateLocationDataArrayList.get(index).setLocation(Constants.HIDDEN);
        } else if (userStatus.equals(Constants.LOCATION_SHOW)) {
            updateLocationDataArrayList.get(index).setLocation(Constants.SHOWN);
        } else if (userStatus.equals(Constants.BLOCK)) {
            updateLocationDataArrayList.remove(index);
        } else {
            updateLocationDataArrayList.get(index).setIs_connection("");
        }
    }

    private void toggleFilterButtonBackground() {
        String filters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
        if (filters != null)
            if (filters.isEmpty()) {
                mapProfileBinding.buttonFilter.setVisibility(View.VISIBLE);
//                mapProfileBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
            } else {
                mapProfileBinding.buttonFilter.setVisibility(View.INVISIBLE);
//                mapProfileBinding.clearTextViewFilter.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        new GestureDetector(getActivity(), new TapGestureListener()).onTouchEvent(event);
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().isEmpty()) {
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
        }
        if (mapProfileBinding.editTextSearch.getText().toString().isEmpty()) {
            mapProfileBinding.imageViewClose.setVisibility(View.GONE);
        } else {
            mapProfileBinding.imageViewClose.setVisibility(View.VISIBLE);
        }
        LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, s.toString()).apply();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), mapProfileBinding.getRoot());
            slideUp.hide();
            EventBus.getDefault().post(new EventBusMapSearchTextUpdate());
            List<String> searchIndex = Arrays.asList(searchSuggestion.split(","));
            if (!searchIndex.contains(mapProfileBinding.editTextSearch.getText().toString())) {
                searchSuggestion = searchSuggestion + "," + mapProfileBinding.editTextSearch.getText().toString();
                List<String> suggestionArray = Arrays.asList(searchSuggestion.split(","));
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, suggestionArray);
                mapProfileBinding.editTextSearch.setAdapter(arrayAdapter);
            }
            LnqApplication.getInstance().editor.putString("search_suggestion", searchSuggestion).apply();
            return true;
        }
        return false;
    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            getActivity().onBackPressed();
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventLnqClick(EventBusMapLnqClick mObj) {
        if (mObj.getClickedType().equals(Constants.LNQ)) {
            showLNQUserFragment(mObj);
        } else if (mObj.getClickedType().equals(Constants.CONTACTED)) {
            showContactRequestFragment(mObj);
        } else if (mObj.getClickedType().equals(Constants.CONNECTED)) {
            showUNLNQUserFragment(mObj);
        } else if (mObj.getClickedType().equals(Constants.MORE)) {
            showMoreFragment(mObj);
        } else if (mObj.getClickedType().equals(Constants.FINISH)) {
            getActivity().onBackPressed();
        } else if (mObj.getClickedType().equals(Constants.PROFILE)) {
            showFullProfileFragment(mObj);
        } else {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_VERIFIED_FRAGMENT, true, null);
        }
    }

    public void showContactRequestFragment(EventBusMapLnqClick mObj) {
        Bundle bundle = new Bundle();
        if (updateLocationDataArrayList.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            bundle.putString(EndpointKeys.USER_ID, updateLocationDataArrayList.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataArrayList.get(mObj.getPosition()).getReceiver_id() : updateLocationDataArrayList.get(mObj.getPosition()).getSender_id());
            bundle.putString(EndpointKeys.PROFILE_ID, updateLocationDataArrayList.get(mObj.getPosition()).getProfile_id());
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.RETRACT_REQUEST_POPUP, true, bundle);
        } else {
            bundle.putString(EndpointKeys.USER_ID, updateLocationDataArrayList.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataArrayList.get(mObj.getPosition()).getReceiver_id() : updateLocationDataArrayList.get(mObj.getPosition()).getSender_id());
            bundle.putString(EndpointKeys.USER_NAME, updateLocationDataArrayList.get(mObj.getPosition()).getUser_name());
            bundle.putString(EndpointKeys.PROFILE_ID, updateLocationDataArrayList.get(mObj.getPosition()).getProfile_id());
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.ACCEPT_REQUEST_POPUP, true, bundle);
        }
    }

    public void showLNQUserFragment(EventBusMapLnqClick mObj) {
        Bundle bundle = new Bundle();
        bundle.putString(EndpointKeys.USER_ID, updateLocationDataArrayList.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataArrayList.get(mObj.getPosition()).getReceiver_id() : updateLocationDataArrayList.get(mObj.getPosition()).getSender_id());
        bundle.putString(EndpointKeys.USER_NAME, updateLocationDataArrayList.get(mObj.getPosition()).getUser_name());
        bundle.putString(EndpointKeys.PROFILE_ID, updateLocationDataArrayList.get(mObj.getPosition()).getProfile_id());
        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_USER_POPUP, true, bundle);
    }

    public void showUNLNQUserFragment(EventBusMapLnqClick mObj) {
        Bundle bundle = new Bundle();
        bundle.putString(EndpointKeys.USER_ID, updateLocationDataArrayList.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataArrayList.get(mObj.getPosition()).getReceiver_id() : updateLocationDataArrayList.get(mObj.getPosition()).getSender_id());
        bundle.putString(EndpointKeys.USER_NAME, updateLocationDataArrayList.get(mObj.getPosition()).getUser_name());
        bundle.putString(EndpointKeys.PROFILE_ID, updateLocationDataArrayList.get(mObj.getPosition()).getProfile_id());
        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.UNLNQ_POPUP, true, bundle);
    }

    public void showMoreFragment(EventBusMapLnqClick mObj) {
        Bundle bundle = new Bundle();
        bundle.putString(EndpointKeys.USER_ID, updateLocationDataArrayList.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataArrayList.get(mObj.getPosition()).getReceiver_id() : updateLocationDataArrayList.get(mObj.getPosition()).getSender_id());
        bundle.putString(EndpointKeys.PROFILE_ID, updateLocationDataArrayList.get(mObj.getPosition()).getProfile_id());
        bundle.putString(EndpointKeys.USER_NAME, updateLocationDataArrayList.get(mObj.getPosition()).getUser_name());
        bundle.putString(EndpointKeys.USER_AVATAR, updateLocationDataArrayList.get(mObj.getPosition()).getUser_image());
        bundle.putString(EndpointKeys.USER_CONNECTION_STATUS, updateLocationDataArrayList.get(mObj.getPosition()).getIs_connection());
        bundle.putString(EndpointKeys.IS_USER_FAVORITE, updateLocationDataArrayList.get(mObj.getPosition()).getIs_favorite());
        bundle.putString(EndpointKeys.IS_LOCATION_HIDDEN, updateLocationDataArrayList.get(mObj.getPosition()).getLocation());
        bundle.putString(EndpointKeys.IS_BLOCK, "");
        bundle.putString(EndpointKeys.THREAD_ID, updateLocationDataArrayList.get(mObj.getPosition()).getThread_id());

        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_MAP_NEW, true, bundle);
    }

    public void showFullProfileFragment(EventBusMapLnqClick mObj) {
        Bundle bundle = new Bundle();
        bundle.putString(EndpointKeys.USER_ID, updateLocationDataArrayList.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataArrayList.get(mObj.getPosition()).getReceiver_id() : updateLocationDataArrayList.get(mObj.getPosition()).getSender_id());
        bundle.putString(EndpointKeys.PROFILE_ID, updateLocationDataArrayList.get(mObj.getPosition()).getProfile_id());
        bundle.putString(EndpointKeys.IS_FAVORITE, updateLocationDataArrayList.get(mObj.getPosition()).getIs_favorite());
        bundle.putString(Constants.REQUEST_FROM, "map");
        bundle.putString("topBar", "explore");
        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, true, bundle);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().post(new EventBusProfileClose());
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public class HomeMapProfileClickHandler {

        public void onFilterClick(View view) {
            EventBus.getDefault().post(new EventBusMapProfileClick(0));
            EventBus.getDefault().post(new EventBusUserSession("filter_click"));

        }

        public void onGridClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataArrayList));
                    EventBus.getDefault().post(new EventBusUserSession("grid_view"));

                }
            }, 100);
        }

        public void onCloseClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), mapProfileBinding.getRoot());
            mapProfileBinding.editTextSearch.setText("");
            LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "").apply();
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
            EventBus.getDefault().post(new EventBusMapSearchTextUpdate());
        }

        public void onQrCodeClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("qrcode_click"));

        }

        public void onSearchClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), mapProfileBinding.getRoot());
            EventBus.getDefault().post(new EventBusMapSearchTextUpdate());
        }

    }

    private void changeSelection(AppCompatCheckBox compatCheckBox) {
        compatCheckBox.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                mapProfileBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                mapProfileBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                mapProfileBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                mapProfileBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.mTvAccountHeading1:
            case R.id.imageViewBack:
                slideUp.hide();
                break;
            case R.id.viewCloseFilter:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnApply:
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, userFilter.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "")).apply();
                EventBus.getDefault().post(new EventBusUpdateFilters());
                EventBus.getDefault().post(new EventBusUserSession("map_filter"));
                slideUp.hide();
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                mapProfileBinding.checkBoxFavourites.setChecked(false);
                mapProfileBinding.checkBoxOutstandingTasks.setChecked(false);
                mapProfileBinding.checkBoxPendingLNQs.setChecked(false);
                mapProfileBinding.checkBoxVerifiedProfile.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
        }
    }

    private void isChecked(String key, boolean isSelected) {
        if (isSelected) {
            userFilter.add(key);
        } else {
            userFilter.remove(key);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mapProfileBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, mapProfileBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == mapProfileBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, mapProfileBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == mapProfileBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, mapProfileBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == mapProfileBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, mapProfileBinding.checkBoxVerifiedProfile.isChecked());
        }
    }
}