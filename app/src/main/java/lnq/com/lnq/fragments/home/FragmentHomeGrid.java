package lnq.com.lnq.fragments.home;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import lnq.com.lnq.adapters.HomeGridUsersAdapter;
import lnq.com.lnq.adapters.MentionChatAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.ClearTextView;
import lnq.com.lnq.databinding.FragmentHomeGridBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusGridUserClick;
import lnq.com.lnq.model.event_bus_models.EventBusGridUsersList;
import lnq.com.lnq.model.event_bus_models.EventBusMapSearchTextUpdate;
import lnq.com.lnq.model.event_bus_models.EventBusSendUsersList;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHomeGrid extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentHomeGridBinding homeGridBinding;
    private HomeGridClickHandler clickHandler;
    private LayoutInflater layoutInflater;
    private String searchSuggestion;

    //    Instance fields....
    private List<UpdateLocationData> updateLocationDataList = new ArrayList<>();

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callFavUnFavLnq;

    //    Adapter fields...
    HomeGridUsersAdapter homeGridUsersAdapter;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private String viewType;

    private MultiProfileRoomModel currentProfile;
    private MultiProfileRepositry multiProfileRepositry;

    private SlideUp slideUp;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;
    CardView topBarLayout;
    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();

    public FragmentHomeGrid() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeGridBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_grid, container, false);
        return homeGridBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        OverScrollDecoratorHelper.setUpOverScroll(homeGridBinding.recyclerViewGrid, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        topBarLayout = homeGridBinding.topBarContact.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.explore);
        imageViewContactGridTopBar.setImageResource(R.mipmap.icon_map_new);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);
        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), homeGridBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
//        homeBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(homeGridBinding.slideViewContacts)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    homeGridBinding.checkBoxFavourites.setChecked(false);
                                    homeGridBinding.checkBoxOutstandingTasks.setChecked(false);
                                    homeGridBinding.checkBoxPendingLNQs.setChecked(false);
                                    homeGridBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(homeGridBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(homeGridBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(homeGridBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(homeGridBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                        }
                                    }
                                     homeGridBinding.editTextSearch.requestFocus();
                                    homeGridBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
                                    homeGridBinding.viewHideTopBar.setVisibility(View.GONE);
                                    homeGridBinding.searchBarLayout.setVisibility(View.GONE);
                                } else {
                                    homeGridBinding.checkBoxFavourites.setChecked(false);
                                    homeGridBinding.checkBoxOutstandingTasks.setChecked(false);
                                    homeGridBinding.checkBoxPendingLNQs.setChecked(false);
                                    homeGridBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(homeGridBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(homeGridBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(homeGridBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(homeGridBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                        }
                                    }
                                    homeGridBinding.editTextSearch.requestFocus();
                                    homeGridBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                                    homeGridBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                                    homeGridBinding.searchBarLayout.setVisibility(View.VISIBLE);
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
                if (v.getTag() != null) {
                    if (v.getTag().equals("grid")) {
                        v.setTag("map");
                    } else {
                        v.setTag("grid");
                        EventBus.getDefault().post(new EventBusUserSession("map_view"));
//                    imageViewContactGridTopBar.setImageResource(R.mipmap.icon_grid_new);
                        getActivity().onBackPressed();
                    }
                }
            }
        });
    }

    private void init() {
        if (getActivity() != null) {
            multiProfileRepositry = new MultiProfileRepositry(getContext());

            multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
                @Override
                public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                    for (MultiProfileRoomModel data : multiProfileRoomModels) {
                        if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                            currentProfile = data;
                        }
                    }
                }
            });

            viewType = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.MAPVIEW, "");
            ;
            if (viewType.equals(Constants.GridClicked)) {
                homeGridBinding.shimmerLayoutGridMap.setVisibility(View.VISIBLE);
                homeGridBinding.shimmerLayoutGridMap.startShimmerAnimation();
            }

            if (!((MainActivity) getActivity()).fnCheckLocationPermission()) {
                homeGridBinding.textViewEmptyList.setVisibility(View.VISIBLE);
                homeGridBinding.textViewEmptyList.setText("There seems to be nobody around you Please enable your Location Services");
                homeGridBinding.btnLocation.setVisibility(View.VISIBLE);
            }

            homeGridBinding.btnLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).fnRequestLocationPermission(12);
                    Intent intentLocation = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intentLocation);
                }
            });

//        Registering event bus for different triggers....
            EventBus.getDefault().register(this);

//        Setting default values for views....
            ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.INVISIBLE);

//            changeButtonDrawable(homeGridBinding.layoutGrid, homeGridBinding.layoutMap);
            homeGridBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));
            toggleFilterButtonBackground();
            if (homeGridBinding.editTextSearch.getText().toString().isEmpty()) {
                homeGridBinding.imageViewClose.setVisibility(View.GONE);
            } else {
                homeGridBinding.imageViewClose.setVisibility(View.VISIBLE);
            }

//        Setting custom font....
            setCustomFont();

            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                if (userFilters != null) {
                    if (userFilters.contains(Constants.FAVORITES)) {
                        changeSelection(homeGridBinding.checkBoxFavourites);
                        userFilter.add(Constants.FAVORITES);
                    }
                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                        changeSelection(homeGridBinding.checkBoxVerifiedProfile);
                        userFilter.add(Constants.VERIFIED_PROFILE);
                    }
                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                        changeSelection(homeGridBinding.checkBoxPendingLNQs);
                        userFilter.add(Constants.PENDING_LNQS);
                    }
                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                        changeSelection(homeGridBinding.checkBoxOutstandingTasks);
                        userFilter.add(Constants.OUTSTANDING_TASKS);
                    }
                }
            }

            homeGridBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
            homeGridBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
            homeGridBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
            homeGridBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

            homeGridBinding.imageViewBack.setOnClickListener(this);
            homeGridBinding.textViewClearAll.setOnClickListener(this);
            homeGridBinding.mBtnApply.setOnClickListener(this);
            homeGridBinding.textViewFavorites.setOnClickListener(this);
            homeGridBinding.textViewVerifiedProfiles.setOnClickListener(this);
            homeGridBinding.textViewPendingLnq.setOnClickListener(this);
            homeGridBinding.textViewOutstandingTasks.setOnClickListener(this);

//        Setting layout manager for grid adapter recycler view....
            homeGridBinding.recyclerViewGrid.setLayoutManager(new GridLayoutManager(getActivity(), 2));

//        Setting item animator for recycler view....
            homeGridBinding.recyclerViewGrid.setItemAnimator(new DefaultItemAnimator());

//            searchSuggestion = LnqApplication.getInstance().sharedPreferences.getString("search_suggestion", "");
//            if (!searchSuggestion.isEmpty()) {
//                List<String> suggestionArray = Arrays.asList(searchSuggestion.split(","));
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, suggestionArray);
//                homeGridBinding.editTextSearch.setAdapter(arrayAdapter);
//                homeGridBinding.editTextSearch.setThreshold(1);
//            }

//        All event Listeners....
            homeGridBinding.editTextSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().isEmpty()) {
                        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                    }
                    if (homeGridBinding.editTextSearch.getText().toString().isEmpty()) {
                        homeGridBinding.imageViewClose.setVisibility(View.GONE);
                    } else {
                        homeGridBinding.imageViewClose.setVisibility(View.VISIBLE);
                    }
                    LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, s.toString()).apply();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            homeGridBinding.editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        ValidUtils.hideKeyboardFromFragment(getActivity(), homeGridBinding.getRoot());
                        EventBus.getDefault().post(new EventBusMapSearchTextUpdate());
                        EventBus.getDefault().post(new EventBusUserSession("grid_search"));
                        return true;
                    }
                    return false;
                }
            });

//        Setting click handler for data binding....
            clickHandler = new HomeGridClickHandler();
            homeGridBinding.setClickHandler(clickHandler);


            homeGridBinding.recyclerViewGrid.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ValidUtils.hideKeyboardFromFragment(getContext(), homeGridBinding.getRoot());
                    return false;
                }
            });
        }

        homeGridBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
                    homeGridBinding.searchBarLayout.setVisibility(View.GONE);
                }
            }
        });
        slideUp = new SlideUpBuilder(homeGridBinding.slideViewContacts)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            homeGridBinding.checkBoxFavourites.setChecked(false);
                            homeGridBinding.checkBoxOutstandingTasks.setChecked(false);
                            homeGridBinding.checkBoxPendingLNQs.setChecked(false);
                            homeGridBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(homeGridBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(homeGridBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(homeGridBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(homeGridBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                }
                            }
                            homeGridBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
                            homeGridBinding.viewHideTopBar.setVisibility(View.GONE);
                            homeGridBinding.searchBarLayout.setVisibility(View.GONE);
                        } else {
                            homeGridBinding.checkBoxFavourites.setChecked(false);
                            homeGridBinding.checkBoxOutstandingTasks.setChecked(false);
                            homeGridBinding.checkBoxPendingLNQs.setChecked(false);
                            homeGridBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(homeGridBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(homeGridBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(homeGridBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(homeGridBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                }
                            }
                            homeGridBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                            homeGridBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                            homeGridBinding.searchBarLayout.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(homeGridBinding.viewScroll)
                .build();

        homeGridBinding.mTvAccountHeading1.setOnClickListener(this);
        homeGridBinding.mBtnApply.setOnClickListener(this);
        homeGridBinding.imageViewBack.setOnClickListener(this);
        homeGridBinding.textViewClearAll.setOnClickListener(this);
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setEditTextSemiBold(homeGridBinding.editTextSearch);
    }

    private void changeButtonDrawable(LinearLayout buttonSelected, LinearLayout buttonDeselected) {
        buttonSelected.setSelected(true);
        buttonDeselected.setSelected(false);
//        buttonSelected.setBackgroundColor(getResources().getColor(R.color.colorAccentTeenTransparent));
//        buttonDeselected.setBackgroundColor(getResources().getColor(R.color.colorWhiteTransparent));
    }

    public static View makeMeBlink(View view, int duration, int offset) {

        Animation anim = new AlphaAnimation(0.3f, 1.0f);
        anim.setDuration(duration);
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
        return view;
    }

    private void toggleFilterButtonBackground() {
        updateLocationDataList.clear();
        String filters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
        if (filters != null)
            if (filters.isEmpty()) {
                homeGridBinding.buttonFilter.setVisibility(View.VISIBLE);
//                homeGridBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
//                homeGridBinding.horizontalScrollViewHomeGridFilters.setVisibility(View.GONE);
            } else {
                if (layoutInflater == null) {
                    layoutInflater = LayoutInflater.from(getContext());
                }
//                homeGridBinding.linearLayoutHomeGridFilter.removeAllViews();
                List<String> filterList = new ArrayList<>(Arrays.asList(filters.split(",")));
                if (filterList.size() > 0) {
                    for (int i = 0; i < filterList.size(); i++) {
                        View filterView = layoutInflater.inflate(R.layout.row_filter, null);
                        TextView textViewFilter = filterView.findViewById(R.id.textViewFilterRow);
                        textViewFilter.setText(filterList.get(i));
                        ImageView imageViewCloseFilter = filterView.findViewById(R.id.imageViewCloseFilter);
                        imageViewCloseFilter.setOnClickListener(view -> {
//                            homeGridBinding.linearLayoutHomeGridFilter.removeView(filterView);
                            filterList.remove(textViewFilter.getText().toString());
                            String filter = filterList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "");
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, filter).apply();
                            EventBus.getDefault().post(new EventBusUpdateFilters());
                            EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                            homeGridBinding.shimmerLayoutGridMap.setVisibility(View.GONE);
                            if (filterList.size() == 0) {
                                homeGridBinding.buttonFilter.setVisibility(View.VISIBLE);
//                                homeGridBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
//                                homeGridBinding.horizontalScrollViewHomeGridFilters.setVisibility(View.GONE);
                            }
                        });
//                        homeGridBinding.linearLayoutHomeGridFilter.addView(filterView);
                    }
                }
//                homeGridBinding.horizontalScrollViewHomeGridFilters.setVisibility(View.VISIBLE);
                homeGridBinding.buttonFilter.setVisibility(View.INVISIBLE);
//                homeGridBinding.clearTextViewFilter.setVisibility(View.VISIBLE);
            }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (callFavUnFavLnq != null && callFavUnFavLnq.isExecuted()) {
            callFavUnFavLnq.cancel();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetUsersList(EventBusGridUsersList eventBusGridUsersList) {
        if (viewType.equals(Constants.GridClicked)) {
            homeGridBinding.shimmerLayoutGridMap.setVisibility(View.INVISIBLE);
            homeGridBinding.shimmerLayoutGridMap.stopShimmerAnimation();
        }
        toggleFilterButtonBackground();
        updateLocationDataList.clear();
        updateLocationDataList.addAll(eventBusGridUsersList.getUpdateLocationDataList());
        if (updateLocationDataList.size() == 0) {
            homeGridBinding.textViewEmptyList.setVisibility(View.VISIBLE);
        } else {
            homeGridBinding.textViewEmptyList.setVisibility(View.GONE);
            homeGridUsersAdapter = new HomeGridUsersAdapter(getActivity(), updateLocationDataList);
            homeGridBinding.recyclerViewGrid.setAdapter(homeGridUsersAdapter);
            ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
            for (int i = 1; i < updateLocationDataList.size(); i++) {
                if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(updateLocationDataList.get(i).getSender_id())) {
                    mentionModelArrayList.add(new MentionModel(updateLocationDataList.get(i).getReceiver_id(), updateLocationDataList.get(i).getUser_name().toLowerCase(), updateLocationDataList.get(i).getUser_image(), updateLocationDataList.get(i).getProfile_id()));
                }
            }
            MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
            homeGridBinding.editTextSearch.setAdapter(mentionChatAdapter);
            homeGridBinding.editTextSearch.setThreshold(1);
            homeGridBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ValidUtils.hideKeyboardFromFragment(getActivity(), homeGridBinding.getRoot());
                    homeGridBinding.editTextSearch.setText(homeGridBinding.editTextSearch.getText());
                    EventBus.getDefault().post(new EventBusMapSearchTextUpdate());
                }
            });
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGridUserClick(final EventBusGridUserClick eventBusGridUserClick) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), homeGridBinding.getRoot());
        if (eventBusGridUserClick.getClickType().equals(Constants.PROFILE)) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.REQUEST_FROM, "Grid");
            bundle.putString("topBar", "explore");
            bundle.putString(EndpointKeys.USER_ID, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataList.get(eventBusGridUserClick.getPosition()).getReceiver_id() : updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id());
            bundle.putString(EndpointKeys.PROFILE_ID, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getProfile_id());
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, true, bundle);
            EventBus.getDefault().post(new EventBusUserSession("profile_viewed"));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new EventBusSendUsersList(updateLocationDataList, eventBusGridUserClick.getPosition()));
                }
            }, 100);
        } else if (eventBusGridUserClick.getClickType().equals(Constants.FAVORITE)) {
            reqFavUnFavLnq(Constants.FAVORITE, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataList.get(eventBusGridUserClick.getPosition()).getReceiver_id() : updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id(), updateLocationDataList.get(eventBusGridUserClick.getPosition()).getUser_name(), eventBusGridUserClick.getPosition(), currentProfile.getId(), updateLocationDataList.get(eventBusGridUserClick.getPosition()).getProfile_id());
        } else if (eventBusGridUserClick.getClickType().equals(Constants.UN_FAVORITE)) {
            reqFavUnFavLnq(Constants.UN_FAVORITE, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataList.get(eventBusGridUserClick.getPosition()).getReceiver_id() : updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id(), updateLocationDataList.get(eventBusGridUserClick.getPosition()).getUser_name(), eventBusGridUserClick.getPosition(), currentProfile.getId(), updateLocationDataList.get(eventBusGridUserClick.getPosition()).getProfile_id());
        } else if (eventBusGridUserClick.getClickType().equals(Constants.CHAT)) {
            Bundle bundleChat = new Bundle();
            bundleChat.putString(EndpointKeys.USER_ID, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationDataList.get(eventBusGridUserClick.getPosition()).getReceiver_id() : updateLocationDataList.get(eventBusGridUserClick.getPosition()).getSender_id());
            bundleChat.putString(EndpointKeys.PROFILE_ID, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getProfile_id());
            bundleChat.putString(EndpointKeys.USER_NAME, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getUser_name());
            bundleChat.putString(EndpointKeys.USER_AVATAR, EndpointUrls.IMAGES_BASE_URL + updateLocationDataList.get(eventBusGridUserClick.getPosition()).getUser_image());
            bundleChat.putString(EndpointKeys.IS_FAVORITE, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getIs_favorite());
            bundleChat.putString(EndpointKeys.USER_CONNECTION_STATUS, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getIs_connection());
            bundleChat.putString(EndpointKeys.IS_BLOCK, "");
            bundleChat.putString(EndpointKeys.THREAD_ID, updateLocationDataList.get(eventBusGridUserClick.getPosition()).getThread_id());
            EventBus.getDefault().post(new EventBusUserSession("chat_view"));
            ((MainActivity) getActivity()).fnLoadFragAdd("CHAT", true, bundleChat);
        }
    }

    private void reqFavUnFavLnq(final String status, final String userId, final String userName, final int position, String senderProfileId, String receiverProfileId) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callFavUnFavLnq = Api.WEB_SERVICE.favUnfavLNQ(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status);
        callFavUnFavLnq = Api.WEB_SERVICE.favUnfavLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileId, receiverProfileId);
        callFavUnFavLnq.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (status.equals(Constants.FAVORITE)) {
                                updateLocationDataList.get(position).setIs_favorite(Constants.FAVORITE);
                                EventBus.getDefault().post(new EventBusUserSession("favorite_user"));

                            } else {
                                updateLocationDataList.get(position).setIs_favorite("");
                                EventBus.getDefault().post(new EventBusUserSession("unfavorite_user"));

                            }
                            if (homeGridUsersAdapter != null) {
                                homeGridUsersAdapter.notifyItemChanged(position);
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

    public class HomeGridClickHandler {

        public void onMapClick(View view) {
            EventBus.getDefault().post(new EventBusUserSession("map_view"));
            getActivity().onBackPressed();
        }

        public void onGridClick(View view) {

        }

        public void onFilterClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.MAP_FILTER, true, null);
        }

        public void onSearchClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), homeGridBinding.getRoot());
            EventBus.getDefault().post(new EventBusMapSearchTextUpdate());
        }

        public void onQrCodeClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));

        }

        public void onCloseClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), homeGridBinding.getRoot());
            homeGridBinding.editTextSearch.setText("");
            LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "").apply();
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
            EventBus.getDefault().post(new EventBusMapSearchTextUpdate());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRefreshUserStatus(EventBusUpdateUserStatus eventBusUpdateUserStatus) {
        String userId = eventBusUpdateUserStatus.getUserId();
        int refreshUserIndex = -1;
        for (int i = 0; i < updateLocationDataList.size(); i++) {
            if (updateLocationDataList.get(i).getReceiver_id().equals(userId)) {
                refreshUserIndex = i;
                updateUserConnectionStatus(eventBusUpdateUserStatus.getUserStatus(), i, userId);
                break;
            } else if (updateLocationDataList.get(i).getSender_id().equals(userId)) {
                refreshUserIndex = i;
                updateUserConnectionStatus(eventBusUpdateUserStatus.getUserStatus(), i, userId);
                break;
            }
        }
        homeGridUsersAdapter.notifyItemChanged(refreshUserIndex);
    }

    private void updateUserConnectionStatus(String userStatus, int index, String userId) {
        if (userStatus.equals(Constants.CONTACTED)) {
            updateLocationDataList.get(index).setIs_connection(Constants.CONTACTED);
            updateLocationDataList.get(index).setSender_id(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
            updateLocationDataList.get(index).setReceiver_id(userId);
        } else if (userStatus.equals(Constants.CANCEL)) {
            updateLocationDataList.get(index).setIs_connection("");
        } else if (userStatus.equals(Constants.CONNECTED)) {
            updateLocationDataList.get(index).setIs_connection(Constants.CONNECTED);
        } else if (userStatus.equals(Constants.FAVORITE)) {
            updateLocationDataList.get(index).setIs_favorite(Constants.FAVORITE);
        } else if (userStatus.equals(Constants.UN_FAVORITE)) {
            updateLocationDataList.get(index).setIs_favorite("");
        } else if (userStatus.equals(Constants.LOCATION_HIDE)) {
            updateLocationDataList.get(index).setLocation(Constants.HIDDEN);
        } else if (userStatus.equals(Constants.LOCATION_SHOW)) {
            updateLocationDataList.get(index).setLocation(Constants.SHOWN);
        } else if (userStatus.equals(Constants.BLOCK)) {
            updateLocationDataList.remove(index);
            homeGridUsersAdapter.notifyItemRemoved(index);
        } else {
            updateLocationDataList.get(index).setIs_connection("");
        }
    }

    private void changeSelection(AppCompatCheckBox compatCheckBox) {
        compatCheckBox.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                homeGridBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                homeGridBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                homeGridBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                homeGridBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
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
                homeGridBinding.checkBoxFavourites.setChecked(false);
                homeGridBinding.checkBoxOutstandingTasks.setChecked(false);
                homeGridBinding.checkBoxPendingLNQs.setChecked(false);
                homeGridBinding.checkBoxVerifiedProfile.setChecked(false);
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
        if (buttonView == homeGridBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, homeGridBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == homeGridBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, homeGridBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == homeGridBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, homeGridBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == homeGridBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, homeGridBinding.checkBoxVerifiedProfile.isChecked());
        }
    }
}