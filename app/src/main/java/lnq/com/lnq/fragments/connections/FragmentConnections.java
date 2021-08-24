package lnq.com.lnq.fragments.connections;

import android.annotation.SuppressLint;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ConnectionsGridAdapter;
import lnq.com.lnq.adapters.ConnectionsGroupListAdapter;
import lnq.com.lnq.adapters.ConnectionsListAdapter;
import lnq.com.lnq.adapters.CreateContactGroupAdapter;
import lnq.com.lnq.adapters.EventBusInviteLNQ;
import lnq.com.lnq.adapters.ExportContactsAdapter;
import lnq.com.lnq.adapters.MentionChatAdapter;
import lnq.com.lnq.adapters.SharedUserProfileAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.common.StringMethods;
import lnq.com.lnq.custom.views.ClearTextView;
import lnq.com.lnq.custom.views.LinearLayoutManagerWithSmoothScroller;
import lnq.com.lnq.custom.views.fast_scroller.models.AlphabetItem;
import lnq.com.lnq.databinding.FragmentConnectionsBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.fragments.fullprofileview.FragmentAbout;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.event_bus_models.EventBusAddContactGroupMembers;
import lnq.com.lnq.model.event_bus_models.EventBusConnectionView;
import lnq.com.lnq.model.event_bus_models.EventBusConnectionsFilter;
import lnq.com.lnq.model.event_bus_models.EventBusContactPermission;
import lnq.com.lnq.model.event_bus_models.EventBusGetImportedContacts;
import lnq.com.lnq.model.event_bus_models.EventBusGetLnqedContactList;
import lnq.com.lnq.model.event_bus_models.EventBusGroupProfileClick;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveMemberFromGroup;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusAlphabetClick;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusConnectionClick;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusDistanceClicked;
import lnq.com.lnq.model.gson_converter_models.Contacts.RemoveUserFromGroup;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.CreateUserGroup;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.ExportCSVModel;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.InviteLNQMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnections;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserContactGroupMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserGetGroupMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.SelectedExportContact;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.greenrobot.eventbus.EventBus.TAG;

public class FragmentConnections extends Fragment implements CreateContactGroupAdapter.OnCheckedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentConnectionsBinding connectionsBinding;
    private ConnectionsClickHandler clickHandler;
    private LayoutInflater layoutInflater;

    private List<String> userFilter = new ArrayList<>();
    private String sortType;
    private SlideUp slideUp;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;
    CardView topBarLayout;

    private String currentLayout = "lnq";

    //    Instance fields....
    private ArrayList<String> alphabetsList = new ArrayList<>();
    private List<UserConnections> userContactList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListImported = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataTempListImported = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListLNQ = new ArrayList<>();
    private List<UserConnectionsData> addUsersToGroup = new ArrayList<>();
    private List<UserContactGroupMainObject> userContactsGroupData = new ArrayList<>();
    private List<CreateUserGroup> userGetGroupData = new ArrayList<>();
    private List<InviteLNQMainObject> inviteLNQDataList = new ArrayList<>();
    private List<AlphabetItem> mAlphabetItems;
    private String searchSuggestion;

    //    Adapter fields....
    private ConnectionsListAdapter connectionsAdapter;
    private ConnectionsGridAdapter connectionsGridAdapter;

    //    Retrofit fields....
    private Call<UserConnectionsMainObject> callUserConnections;
    private Call<UserContactGroupMainObject> callUserCreateGroup;
    private Call<RemoveUserFromGroup> callRemoveUserFromGroup;
    private Call<UserGetGroupMainObject> callUserGetGroup;
    private Call<InviteLNQMainObject> callUserInvite;

    //    Font fields....
    private FontUtils fontUtils;

    String connectionView;
    private String profileId;
    Dialog dialog;
    String groupId;
    private CreateContactGroupAdapter createContactGroupAdapter;
    private ConnectionsGroupListAdapter connectionsGroupListAdapter;

    public FragmentConnections() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        connectionsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_connections, container, false);
        return connectionsBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        OverScrollDecoratorHelper.setUpOverScroll(connectionsBinding.recyclerViewConnections, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        topBarLayout = connectionsBinding.topBarContact.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.connections);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);
        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(connectionsBinding.slideViewContacts)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    connectionsBinding.checkBoxFavourites.setChecked(false);
                                    connectionsBinding.checkBoxBlockedUsers.setChecked(false);
                                    connectionsBinding.checkBoxLNQUsersOnly.setChecked(false);
                                    connectionsBinding.checkBoxOutstandingTasks.setChecked(false);
                                    connectionsBinding.checkBoxPendingLNQs.setChecked(false);
                                    connectionsBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(connectionsBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(connectionsBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(connectionsBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(connectionsBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                            if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                                changeSelection(connectionsBinding.checkBoxLNQUsersOnly);
                                                userFilter.add(Constants.LNQ_USER_ONLY);
                                            }
                                            if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                                changeSelection(connectionsBinding.checkBoxBlockedUsers);
                                                userFilter.add(Constants.BLOCKED_USERS);
                                            }
                                        }
                                    }
                                    connectionsBinding.editTextSearch.requestFocus();
                                    connectionsBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
                                    connectionsBinding.searchBarLayout.setVisibility(View.GONE);
                                } else {
                                    connectionsBinding.checkBoxFavourites.setChecked(false);
                                    connectionsBinding.checkBoxBlockedUsers.setChecked(false);
                                    connectionsBinding.checkBoxLNQUsersOnly.setChecked(false);
                                    connectionsBinding.checkBoxOutstandingTasks.setChecked(false);
                                    connectionsBinding.checkBoxPendingLNQs.setChecked(false);
                                    connectionsBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(connectionsBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(connectionsBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(connectionsBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(connectionsBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                            if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                                changeSelection(connectionsBinding.checkBoxLNQUsersOnly);
                                                userFilter.add(Constants.LNQ_USER_ONLY);
                                            }
                                            if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                                changeSelection(connectionsBinding.checkBoxBlockedUsers);
                                                userFilter.add(Constants.BLOCKED_USERS);
                                            }
                                        }
                                    }
                                    connectionsBinding.editTextSearch.requestFocus();
                                    connectionsBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                                    connectionsBinding.searchBarLayout.setVisibility(View.VISIBLE);
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
                    v.setTag("list");
                    if (currentLayout.equals("lnq")) {
                        changeButtonDrawable(connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.buttonGrid, connectionsBinding.clearTextViewList);
                        connectionsBinding.recyclerViewConnections.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        connectionsGridAdapter = new ConnectionsGridAdapter(getActivity(), userContactsDataListLNQ);
                        connectionsBinding.recyclerViewConnections.setAdapter(connectionsGridAdapter);
                    }
                    if (currentLayout.equals("imported")) {
                        changeButtonDrawable(connectionsBinding.buttonLNQImport, connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.buttonGrid, connectionsBinding.clearTextViewList);
                        connectionsBinding.recyclerViewConnections.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        connectionsGridAdapter = new ConnectionsGridAdapter(getActivity(), userContactsDataListImported);
                        connectionsBinding.recyclerViewConnections.setAdapter(connectionsGridAdapter);

                    }
                } else {
                    v.setTag("grid");
                    if (currentLayout.equals("lnq")) {
                        changeButtonDrawable(connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
                        connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
                        connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListLNQ);
                        connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                        connectionsBinding.recyclerViewConnections.setVisibility(View.VISIBLE);
                        connectionsBinding.recyclerViewAlphabets.setVisibility(View.VISIBLE);
                        connectionsBinding.recyclerViewGroups.setVisibility(View.GONE);
                    }
                    if (currentLayout.equals("imported")) {
                        changeButtonDrawable(connectionsBinding.buttonLNQImport, connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
                        connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
                        connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListImported);
                        connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                        connectionsBinding.recyclerViewConnections.setVisibility(View.VISIBLE);
                        connectionsBinding.recyclerViewAlphabets.setVisibility(View.VISIBLE);
                        connectionsBinding.recyclerViewGroups.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
//        Registering event bus for trigger of events....
        EventBus.getDefault().register(this);

        profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
//        connectionView = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.CONNECTIONVIEW, "");
//        currentLayout = connectionView;
        ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
//        LNQ Count
        connectionsBinding.textViewLnqCountsNumber.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.CONNECTION_COUNT, "0"));

//        Setting custom font....
        setCustomFont();

//        changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
        changeButtonDrawable(connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport, connectionsBinding.buttonLNQGroups);

        connectionsBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));

//        Getting user connections from api....
        reqContacts(connectionsBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
        reqShowAllGroup(profileId);
//        Setting layout managers of recycler view....
        connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));

//        Setting default item animator of recycler view....
        connectionsBinding.recyclerViewConnections.setItemAnimator(new DefaultItemAnimator());

//        Setting click handler for data binding....
        clickHandler = new ConnectionsClickHandler();
        connectionsBinding.setClickHandler(clickHandler);

        //        Checking id edit text search field is empty or not to toggle visibility of close icon....
        if (connectionsBinding.editTextSearch.getText().toString().isEmpty()) {
            connectionsBinding.imageViewClose.setVisibility(View.GONE);
        } else {
            connectionsBinding.imageViewClose.setVisibility(View.VISIBLE);
        }

//        searchSuggestion = LnqApplication.getInstance().sharedPreferences.getString("search_suggestion", "");
//        if (!searchSuggestion.isEmpty()) {
//            List<String> suggestionArray = Arrays.asList(searchSuggestion.split(","));
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, suggestionArray);
//            connectionsBinding.editTextSearch.setAdapter(arrayAdapter);
//            connectionsBinding.editTextSearch.setThreshold(1);
//        }

//        All event listeners....
        connectionsBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        hideDialog();
                        break;
                }
                return true;
            }
        });
        connectionsBinding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    connectionsBinding.imageViewClose.setVisibility(View.GONE);
                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                    toggleFilterButtonBackground();
                    reqContacts(connectionsBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
                } else {
                    connectionsBinding.imageViewClose.setVisibility(View.VISIBLE);
                }
                LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, s.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        connectionsBinding.editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.mRoot);
                    EventBus.getDefault().post(new EventBusUserSession("contacts_search"));
                    reqContacts(connectionsBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
                    return true;
                }
                return false;
            }
        });
        toggleFilterButtonBackground();

        connectionsBinding.recyclerViewConnections.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValidUtils.hideKeyboardFromFragment(getContext(), connectionsBinding.getRoot());
                return false;
            }
        });

        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
            String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
            if (userFilters != null) {
                if (userFilters.contains(Constants.FAVORITES)) {
                    changeSelection(connectionsBinding.checkBoxFavourites);
                    userFilter.add(Constants.FAVORITES);
                }
                if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                    changeSelection(connectionsBinding.checkBoxVerifiedProfile);
                    userFilter.add(Constants.VERIFIED_PROFILE);
                }
                if (userFilters.contains(Constants.PENDING_LNQS)) {
                    changeSelection(connectionsBinding.checkBoxPendingLNQs);
                    userFilter.add(Constants.PENDING_LNQS);
                }
                if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                    changeSelection(connectionsBinding.checkBoxOutstandingTasks);
                    userFilter.add(Constants.OUTSTANDING_TASKS);
                }
                if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                    changeSelection(connectionsBinding.checkBoxLNQUsersOnly);
                    userFilter.add(Constants.LNQ_USER_ONLY);
                }
                if (userFilters.contains(Constants.BLOCKED_USERS)) {
                    changeSelection(connectionsBinding.checkBoxBlockedUsers);
                    userFilter.add(Constants.BLOCKED_USERS);
                }
            }
        }
        String user_sorting = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL);
        if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_ALPHABETICAL)) {
            setSortBySelection(connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnDistance, connectionsBinding.mBtnRecentLNQs, connectionsBinding.mBtnRecentlyViewed);
        } else if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_DISTANC)) {
            setSortBySelection(connectionsBinding.mBtnDistance, connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnRecentLNQs, connectionsBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTLNQ)) {
            setSortBySelection(connectionsBinding.mBtnRecentLNQs, connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnDistance, connectionsBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTVIEWED)) {
            setSortBySelection(connectionsBinding.mBtnRecentlyViewed, connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnDistance, connectionsBinding.mBtnRecentLNQs);
        }

        connectionsBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
        connectionsBinding.checkBoxBlockedUsers.setOnCheckedChangeListener(this);
        connectionsBinding.checkBoxLNQUsersOnly.setOnCheckedChangeListener(this);
        connectionsBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
        connectionsBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
        connectionsBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

        connectionsBinding.imageViewBack.setOnClickListener(this);
        connectionsBinding.textViewClearAll.setOnClickListener(this);
        connectionsBinding.mBtnAlphabetical.setOnClickListener(this);
        connectionsBinding.mBtnDistance.setOnClickListener(this);
        connectionsBinding.mBtnRecentLNQs.setOnClickListener(this);
        connectionsBinding.mBtnApply.setOnClickListener(this);
        connectionsBinding.mBtnRecentlyViewed.setOnClickListener(this);
        connectionsBinding.mTvFavorites.setOnClickListener(this);
        connectionsBinding.mTvVerifiedProfiles.setOnClickListener(this);
        connectionsBinding.mTvPendingLnq.setOnClickListener(this);
        connectionsBinding.mTvOutstandingTasks.setOnClickListener(this);
        connectionsBinding.mTvLnqUsersOnly.setOnClickListener(this);
        connectionsBinding.mTvBlockedUsers.setOnClickListener(this);
        connectionsBinding.mTvAccountHeading1.setOnClickListener(this);

        slideUp = new SlideUpBuilder(connectionsBinding.slideViewContacts)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            connectionsBinding.checkBoxFavourites.setChecked(false);
                            connectionsBinding.checkBoxBlockedUsers.setChecked(false);
                            connectionsBinding.checkBoxLNQUsersOnly.setChecked(false);
                            connectionsBinding.checkBoxOutstandingTasks.setChecked(false);
                            connectionsBinding.checkBoxPendingLNQs.setChecked(false);
                            connectionsBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(connectionsBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(connectionsBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(connectionsBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(connectionsBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                    if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                        changeSelection(connectionsBinding.checkBoxLNQUsersOnly);
                                        userFilter.add(Constants.LNQ_USER_ONLY);
                                    }
                                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                        changeSelection(connectionsBinding.checkBoxBlockedUsers);
                                        userFilter.add(Constants.BLOCKED_USERS);
                                    }
                                }
                            }
                            connectionsBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
                            connectionsBinding.searchBarLayout.setVisibility(View.GONE);
                        } else {
                            connectionsBinding.checkBoxFavourites.setChecked(false);
                            connectionsBinding.checkBoxBlockedUsers.setChecked(false);
                            connectionsBinding.checkBoxLNQUsersOnly.setChecked(false);
                            connectionsBinding.checkBoxOutstandingTasks.setChecked(false);
                            connectionsBinding.checkBoxPendingLNQs.setChecked(false);
                            connectionsBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(connectionsBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(connectionsBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(connectionsBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(connectionsBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                    if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                        changeSelection(connectionsBinding.checkBoxLNQUsersOnly);
                                        userFilter.add(Constants.LNQ_USER_ONLY);
                                    }
                                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                        changeSelection(connectionsBinding.checkBoxBlockedUsers);
                                        userFilter.add(Constants.BLOCKED_USERS);
                                    }
                                }
                            }
                            connectionsBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                            connectionsBinding.searchBarLayout.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(connectionsBinding.viewScroll)
                .build();
    }

    private void changeButtonDrawable(Button buttonSelected, Button buttonDeselected) {
        buttonSelected.setSelected(true);
        buttonDeselected.setSelected(false);
        buttonSelected.setTextColor(getResources().getColor(R.color.colorAccentTeenTransparent));
        buttonDeselected.setTextColor(getResources().getColor(R.color.colorWhiteTransparent));
    }

    private void changeButtonDrawable(Button buttonSelected, Button buttonDeselected, Button buttonGroupSelected) {
        buttonSelected.setSelected(true);
        buttonDeselected.setSelected(false);
        buttonGroupSelected.setSelected(false);
        buttonSelected.setTextColor(getResources().getColor(R.color.colorWhite));
        buttonDeselected.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
        buttonGroupSelected.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
    }

    //    Method to set custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setEditTextSemiBold(connectionsBinding.editTextSearch);
        fontUtils.setTextViewRegularFont(connectionsBinding.textViewLnqCount);
        fontUtils.setTextViewRegularFont(connectionsBinding.textViewImportConnections);
        fontUtils.setTextViewRegularFont(connectionsBinding.textViewExportConnections);
        fontUtils.setTextViewSemiBold(connectionsBinding.textViewLnqCountsNumber);
        fontUtils.setTextViewSemiBold(connectionsBinding.clearTextViewLNQ);
        fontUtils.setButtonSemiBold(connectionsBinding.buttonLNQImport);
        fontUtils.setButtonSemiBold(connectionsBinding.buttonLNQGroups);
        fontUtils.setTextViewRegularFont(connectionsBinding.textViewSortBy);
        fontUtils.setTextViewRegularFont(connectionsBinding.textViewClearAll);
        fontUtils.setTextViewRegularFont(connectionsBinding.mTvFavorites);
        fontUtils.setTextViewRegularFont(connectionsBinding.mTvVerifiedProfiles);
        fontUtils.setTextViewRegularFont(connectionsBinding.mTvPendingLnq);
        fontUtils.setTextViewRegularFont(connectionsBinding.mTvOutstandingTasks);
        fontUtils.setTextViewRegularFont(connectionsBinding.mTvLnqUsersOnly);
        fontUtils.setTextViewRegularFont(connectionsBinding.mTvBlockedUsers);
        fontUtils.setButtonRegularFont(connectionsBinding.mBtnAlphabetical);
        fontUtils.setButtonRegularFont(connectionsBinding.mBtnDistance);
        fontUtils.setButtonRegularFont(connectionsBinding.mBtnRecentLNQs);
        fontUtils.setButtonRegularFont(connectionsBinding.mBtnRecentlyViewed);
        fontUtils.setTextViewRegularFont(connectionsBinding.mBtnApply);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callUserConnections != null && callUserConnections.isExecuted()) {
            callUserConnections.cancel();
        }
        if (callUserCreateGroup != null && callUserCreateGroup.isExecuted()) {
            callUserCreateGroup.cancel();
        }
        if (callUserGetGroup != null && callUserGetGroup.isExecuted()) {
            callUserGetGroup.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    //    Event bus method triggers when any contact is clicked....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fnContactsClickEvent(EventBusConnectionClick eventBusContactId) {
//        hideDialog();
        ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.getRoot());

        if (currentLayout.equals("lnq") || currentLayout.equals("list")) {
            if (!userContactsDataListLNQ.get(eventBusContactId.getmPos()).getUser_id().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                if (!userContactsDataListLNQ.get(eventBusContactId.getmPos()).getContact_status().equals("not_lnqed")) {
                    if (userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("connected") ||
                            userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("favorite") ||
                            userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("contacted") ||
                            userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", userContactsDataListLNQ.get(eventBusContactId.getmPos()).getUser_id());
                        bundle.putString("topBar", "contacts");
                        bundle.putString("profile_id", userContactsDataListLNQ.get(eventBusContactId.getmPos()).getProfile_id());
                        ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
                    }
                }
            }
        } else if (currentLayout.equals("imported") || currentLayout.equals("grid")) {
            if (!userContactsDataListImported.get(eventBusContactId.getmPos()).getUser_id().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                if (userContactsDataListImported.get(eventBusContactId.getmPos()).getContact_status().equals("not_lnqed")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_name", userContactsDataListImported.get(eventBusContactId.getmPos()).getUser_fname() + userContactsDataListImported.get(eventBusContactId.getmPos()).getUser_lname());
                    ((MainActivity) getActivity()).fnLoadFragAdd("CONTACT PROFILE VIEW", true, bundle);
                }
            }
        } else if (currentLayout.equals("imported") || currentLayout.equals("list")) {
            if (!userContactsDataListImported.get(eventBusContactId.getmPos()).getUser_id().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                if (userContactsDataListImported.get(eventBusContactId.getmPos()).getContact_status().equals("not_lnqed")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_name", userContactsDataListImported.get(eventBusContactId.getmPos()).getUser_fname() + userContactsDataListImported.get(eventBusContactId.getmPos()).getUser_lname());
                    ((MainActivity) getActivity()).fnLoadFragAdd("CONTACT PROFILE VIEW", true, bundle);


                }
            }
        } else if (currentLayout.equals("lnq") || currentLayout.equals("grid")) {
            if (!userContactsDataListLNQ.get(eventBusContactId.getmPos()).getUser_id().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                if (!userContactsDataListLNQ.get(eventBusContactId.getmPos()).getContact_status().equals("not_lnqed")) {
                    if (userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("connected") ||
                            userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("favorite") ||
                            userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("contacted") ||
                            userContactsDataListLNQ.get(eventBusContactId.getmPos()).getIs_connection().equals("")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", userContactsDataListLNQ.get(eventBusContactId.getmPos()).getUser_id());
                        bundle.putString("topBar", "contacts");
                        bundle.putString("profile_id", userContactsDataListLNQ.get(eventBusContactId.getmPos()).getProfile_id());
                        ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGroupProfileClick(EventBusGroupProfileClick mObj) {
        if (!userContactsDataListLNQ.get(mObj.getmPos()).getUser_id().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", mObj.getUserId());
            bundle.putString("profile_id", mObj.getProfileId());
            bundle.putString("topBar", "contacts");
            ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
        }
    }

    //    Event bus method triggers when any alphabet is clicked....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fnAlphaEvent(EventBusAlphabetClick eventBusAlphaId) {
        String alpha = alphabetsList.get(eventBusAlphaId.getmPoss());
        for (int i = 0; i < userContactsDataList.size(); i++) {
            if (!userContactsDataList.get(i).getUser_fname().isEmpty()) {
                if (userContactsDataList.get(i).getUser_fname().length() > 1) {
                    if (alpha.equalsIgnoreCase(userContactsDataList.get(i).getUser_fname().substring(0, 1))) {
                        connectionsBinding.recyclerViewConnections.smoothScrollToPosition(i);
                        break;
                    }
                }
            } else if (!userContactsDataList.get(i).getContact_name().isEmpty()) {
                if (userContactsDataList.get(i).getContact_name().length() > 1) {
                    if (alpha.equalsIgnoreCase(userContactsDataList.get(i).getContact_name().substring(0, 1))) {
                        connectionsBinding.recyclerViewConnections.smoothScrollToPosition(i);
                        break;
                    }
                }
            }
        }
    }

    //    Event bus method triggers when user import contacts to server from FragImportContacts fragment....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetImportedContacts(EventBusGetImportedContacts eventBusGetImportedContacts) {
        reqContacts(connectionsBinding.editTextSearch.getText().toString(),
                LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
    }

    //    Event bus method triggers when user allow runtime permission of contacts....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusContactPermission(EventBusContactPermission mObj) {
        if (mObj.getPermissionType().equals(EndpointKeys.IMPORT)) {
            if (mObj.getRequestCode() == 5) {
                showImportContactsFragment("local");
            } else {
                showImportContactsFragment("gmail");
            }
        } else {
            showExportContactsFragment("local_export");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusDistanceClicked(EventBusDistanceClicked busDistanceClicked) {
        Bundle bundle = new Bundle();
        bundle.putString(EndpointKeys.USER_ID, userContactsDataListLNQ.get(busDistanceClicked.getPosition()).getUser_id());
        bundle.putString(EndpointKeys.PROFILE_ID, userContactsDataListLNQ.get(busDistanceClicked.getPosition()).getProfile_id());
        bundle.putString("mFlag", "near_you");
        ((MainActivity) getActivity()).mFScreenName = Constants.HOME;
        ((MainActivity) getActivity()).fnLoadFragReplace(Constants.HOME, false, bundle);
    }

    private void showImportContactsFragment(String importType) {
        Bundle bundle = new Bundle();
        bundle.putString("import_type", importType);
        ((MainActivity) getActivity()).fnAddFragWithCustomAnimation(Constants.IMPORT_CONTACTS, true, bundle);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventBusGetLnqedContactList(userContactsDataList));
            }
        }, 100);
    }

    private void showExportContactsFragment(String importType) {
        Bundle bundle = new Bundle();
        bundle.putString("import_type", importType);
        ((MainActivity) getActivity()).fnAddFragWithCustomAnimation(Constants.EXPORT_CONTACTS, true, bundle);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventBusGetLnqedContactList(userContactsDataList));
            }
        }, 100);
    }

    private void showImportToGmailContactsFragment() {
        ((MainActivity) getActivity()).fnAddFragWithCustomAnimation(Constants.IMPORT_To_Gmail_CONTACTS, true, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventBusGetLnqedContactList(userContactsDataList));
            }
        }, 100);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateFilter(EventBusUpdateFilters eventBusUpdateFilters) {
        toggleFilterButtonBackground();
        changeButtonDrawable(connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport, connectionsBinding.buttonLNQGroups);
//        changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
        connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManager(getActivity()));
        reqContacts(connectionsBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusInviteLNQ(EventBusInviteLNQ eventBusInviteLNQ) {
        reqInviteLNQ(eventBusInviteLNQ.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getConnectionsView(EventBusConnectionView eventBusConnectionView) {
        if (connectionView.equals("grid") && currentLayout.equalsIgnoreCase("grid")) {
            connectionsBinding.recyclerViewConnections.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            connectionsGridAdapter = new ConnectionsGridAdapter(getActivity(), userContactsDataListLNQ);
            connectionsBinding.recyclerViewConnections.setAdapter(connectionsGridAdapter);
        } else {
            connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
            connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListLNQ);
            connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusAddGroupMembers(EventBusAddContactGroupMembers mObj) {
        addUsersToGroup.clear();
        for (UserConnectionsData userConnectionsData : userContactsDataListLNQ) {
            boolean isFound = false;
            for (UserConnectionsData usersProfileId : mObj.getExistingMembersList()) {
                if (userConnectionsData.getProfile_id().equalsIgnoreCase(usersProfileId.getProfile_id())) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                addUsersToGroup.add(userConnectionsData);
            }
        }
        if (addUsersToGroup.size() != 0) {
            for (int i = 0; i < addUsersToGroup.size(); i++) {
                addUsersToGroup.get(i).setSelected(false);
            }
            String groupID = mObj.getGroupId();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.cus_dialog_createcontactgroups, null);
            RecyclerView recyclerViewAddGroupContactList = dialogView.findViewById(R.id.recyclerViewAddGroupContactList);
            ClearTextView clearTextViewCreateGroup = dialogView.findViewById(R.id.clearTextViewCreateGroup);
            clearTextViewCreateGroup.setText("Add Member");
            ClearTextView clearTextViewCancel = dialogView.findViewById(R.id.clearTextViewCancel);
            EditText editTextGroupName = dialogView.findViewById(R.id.editTextEnterGroupName);
            TextInputLayout textInputLayoutGroupName = dialogView.findViewById(R.id.textInputLayoutGroupName);
            textInputLayoutGroupName.setVisibility(View.GONE);
            createContactGroupAdapter = new CreateContactGroupAdapter(getActivity(), addUsersToGroup);
            recyclerViewAddGroupContactList.setAdapter(createContactGroupAdapter);
            createContactGroupAdapter.setOnCheckedListener(FragmentConnections.this);
            recyclerViewAddGroupContactList.setLayoutManager(new LinearLayoutManager(getActivity()));

            builder.setView(dialogView);
            dialog = builder.create();
            dialog.show();

            clearTextViewCreateGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> reveiverIdList = new ArrayList<>();
                    List<String> receiverProfileIdList = new ArrayList<>();
                    for (int i = 0; i < userContactsGroupData.size(); i++) {
                        reveiverIdList.add(userContactsGroupData.get(i).getReceiver_ids());
                        receiverProfileIdList.add(userContactsGroupData.get(i).getReceiver_profile_ids());
                    }
                    String reveiverId = reveiverIdList.toString().replaceAll(", ", ",").replaceAll("[\\[.\\]]", "");
                    String receiverProfileId = receiverProfileIdList.toString().replaceAll(", ", ",").replaceAll("[\\[.\\]]", "");
                    reqCreateGroup(profileId, editTextGroupName.getText().toString(), reveiverId, receiverProfileId, groupID, "addGroup");
                    dialog.cancel();
                }
            });

            clearTextViewCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            try {
                dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

            } catch (Exception e) {

            }
        } else {
            ValidUtils.showCustomToast(getContext(), "All your contacts are already members of this group");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRemoveGroupMember(EventBusRemoveMemberFromGroup mObj) {
        String receverId = mObj.getRecevierId();
        String recevierProfileId = mObj.getRecevierProfileId();
        String groupId = mObj.getGroupId();
        reqRemoveMemberFromGroup(profileId, receverId, recevierProfileId, groupId, "0");
    }


    //    Method to hide popup dialog for imports....
//    private void hideDialog() {
//        if (connectionsBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && connectionsBinding.cardViewPopUpContactsContainer.getVisibility() == View.VISIBLE) {
//            connectionsBinding.imageViewArrowUp.setVisibility(View.GONE);
//            connectionsBinding.cardViewPopUpContactsContainer.setVisibility(View.GONE);
//        }
//    }

    //    Method to get all imported contacts or connections from server....
    private void reqContacts(String searchKey, String searchFilter) {
        userContactsDataList.clear();
        userContactsDataListLNQ.clear();
        userContactsDataListImported.clear();
        if (connectionsAdapter != null) {
            connectionsAdapter.notifyDataSetChanged();
        }
        connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.GONE);
        if (searchKey.isEmpty()) {
            userContactsDataTempListImported.clear();
        }
        if (currentLayout.equals("grid")) {
            connectionsBinding.shimmerLayoutGrid.setVisibility(View.VISIBLE);
            connectionsBinding.shimmerLayoutGrid.startShimmerAnimation();
            connectionsBinding.shimmerLayout.setVisibility(View.GONE);
            connectionsBinding.shimmerLayout.stopShimmerAnimation();
            connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.GONE);
        } else {
            connectionsBinding.shimmerLayout.setVisibility(View.VISIBLE);
            connectionsBinding.shimmerLayout.startShimmerAnimation();
            connectionsBinding.shimmerLayoutGrid.setVisibility(View.GONE);
            connectionsBinding.shimmerLayoutGrid.stopShimmerAnimation();
            connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.GONE);
        }
        callUserConnections = Api.WEB_SERVICE.contacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), searchKey, searchFilter, profileId);
        callUserConnections.enqueue(new Callback<UserConnectionsMainObject>() {
            public void onResponse(Call<UserConnectionsMainObject> call, Response<UserConnectionsMainObject> response) {
                connectionsBinding.shimmerLayout.setVisibility(View.GONE);
                connectionsBinding.shimmerLayout.stopShimmerAnimation();
                connectionsBinding.shimmerLayoutGrid.setVisibility(View.GONE);
                connectionsBinding.shimmerLayoutGrid.stopShimmerAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            userContactList = response.body().getUserContacts();
                            for (int i = 0; i < userContactList.size(); i++) {
                                UserConnections userContact = userContactList.get(i);
                                if (!userContact.getUser_data().getUser_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")))
                                    if (userContact.getUser_data().getContact_status().equals("not_lnqed")) {
                                        userContactsDataListImported.add(userContact.getUser_data());
                                    } else {
                                        if (userContact.getNote_on_user() != null) {
                                            userContact.getUser_data().setUserNote(userContact.getNote_on_user().getNote_description());
                                        }
                                        userContactsDataListLNQ.add(userContact.getUser_data());
                                    }
                            }
                            userContactsDataList.addAll(userContactsDataListLNQ);
                            userContactsDataList.addAll(userContactsDataListImported);
                            for (int i = 0; i < userContactsDataListImported.size(); i++) {
                                UserConnectionsData userContactsData = userContactsDataListImported.get(i);
                                if (userContactsData.getUser_fname() == null && userContactsData.getUser_lname() == null) {
                                    userContactsData.setUser_fname("");
                                    userContactsData.setUser_lname("");
                                }
                                if (userContactsData.getUser_current_position() == null) {
                                    userContactsData.setUser_current_position("");
                                }
                                if ((userContactsData.getUser_fname().trim() + userContactsData.getUser_lname().trim()).isEmpty()) {
                                    if (!userContactsData.getContact_name().isEmpty()) {
                                        userContactsData.setUser_fname(userContactsData.getContact_name());
                                    } else if (!userContactsData.getUser_phone().isEmpty()) {
                                        userContactsData.setUser_fname(userContactsData.getUser_phone());
                                    } else {
                                        userContactsData.setUser_fname(userContactsData.getUser_email());
                                    }
                                }
                            }
                            if (searchKey.isEmpty()) {
                                userContactsDataTempListImported.addAll(userContactsDataListImported);
                            }
                            if (userContactsDataListImported.size() > 0) {
                                sortList(userContactsDataListImported);
                            } else {
                                if (!searchKey.isEmpty()) {
                                    searchImportedContacts(searchKey);
                                }
                            }

                            if (userContactsDataListLNQ.size() > 0) {
                                sortList(userContactsDataListLNQ);
                                EventBus.getDefault().post(new EventBusGetLnqedContactList(userContactsDataList));
                            }
                            if (currentLayout.equalsIgnoreCase("lnq")) {
                                connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListLNQ);
                                connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                            } else {
                                if (userContactsDataListImported.size() == 0) {
                                    connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.VISIBLE);
                                }
                                connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListImported);
                                connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                            }
                            EventBus.getDefault().post(new EventBusConnectionView());
                            ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
                            for (int i = 1; i < userContactsDataListLNQ.size(); i++) {
                                if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(userContactsDataListLNQ.get(i).getUser_id())) {
                                    mentionModelArrayList.add(new MentionModel(userContactsDataListLNQ.get(i).getUser_id(), userContactsDataListLNQ.get(i).getUser_fname() + " " + userContactsDataListLNQ.get(i).getUser_lname().toLowerCase(), userContactsDataListLNQ.get(i).getUser_avatar(), userContactsDataListLNQ.get(i).getProfile_id()));
                                }
                            }
                            MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
                            connectionsBinding.editTextSearch.setAdapter(mentionChatAdapter);
                            connectionsBinding.editTextSearch.setThreshold(1);
                            connectionsBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.getRoot());
                                    connectionsBinding.editTextSearch.setText(connectionsBinding.editTextSearch.getText());
                                    reqContacts(connectionsBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
                                    toggleFilterButtonBackground();
                                }
                            });
                            break;
                        case 0:
                            if (response.body().getMessage() != null) {
                                if (response.body().getMessage().equalsIgnoreCase("No contacts found")) {
                                    if (!searchKey.isEmpty()) {
                                        searchImportedContacts(searchKey);
                                    }
                                    if (userContactsDataListImported.size() == 0) {
                                        connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.VISIBLE);
                                        connectionsBinding.shimmerLayoutGrid.setVisibility(View.GONE);
                                        connectionsBinding.shimmerLayout.setVisibility(View.GONE);
                                    } else {
                                        if (currentLayout.equalsIgnoreCase("lnq")) {
                                            connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListLNQ);
                                            connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                                        } else {
                                            connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListImported);
                                            connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserConnectionsMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                connectionsBinding.shimmerLayout.setVisibility(View.GONE);
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

    public void reqCreateGroup(String profile_id, String group_name, String receiver_id, String receiverProfile_id, String group_id, String type) {
        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        if (group_id != null && !group_id.isEmpty()) {
            callUserCreateGroup = Api.WEB_SERVICE.createUserGroupWithGroupId(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.ID, ""), profile_id, group_name, receiver_id, receiverProfile_id, group_id);
        } else {
            callUserCreateGroup = Api.WEB_SERVICE.createUserGroup(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                    sharedPreferences.getString(EndpointKeys.ID, ""), profileId, group_name, receiver_id, receiverProfile_id);
        }

        callUserCreateGroup.enqueue(new Callback<UserContactGroupMainObject>() {
            @Override
            public void onResponse(Call<UserContactGroupMainObject> call, Response<UserContactGroupMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            groupId = response.body().getCreateUserGroup().getId();
                            if (type.equalsIgnoreCase("addGroup")) {
                                for (int i = 0; i < userGetGroupData.size(); i++) {
                                    if (userGetGroupData.get(i).getId().equalsIgnoreCase(group_id)) {
                                        userGetGroupData.get(i).setMembers(response.body().getCreateUserGroup().getMembers());
                                        break;
                                    }
                                }
                            } else {
                                userGetGroupData.add(response.body().getCreateUserGroup());
                            }
                            connectionsGroupListAdapter.notifyDataSetChanged();
                            userContactsGroupData.clear();
                            connectionsBinding.textViewNoGroup.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserContactGroupMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                connectionsBinding.shimmerLayout.setVisibility(View.GONE);
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
                connectionsBinding.shimmerLayout.setVisibility(View.GONE);
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

    public void reqRemoveMemberFromGroup(String profile_id, String receiver_id, String receiverProfile_id, String group_id, String removeAllMembers) {
        ((MainActivity) getActivity()).progressDialog.show();
        callRemoveUserFromGroup = Api.WEB_SERVICE.removeMemberFromUserGroup(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), profile_id, receiver_id, receiverProfile_id, group_id, removeAllMembers);

        callRemoveUserFromGroup.enqueue(new Callback<RemoveUserFromGroup>() {
            @Override
            public void onResponse(Call<RemoveUserFromGroup> call, Response<RemoveUserFromGroup> response) {
                ((MainActivity) getActivity()).progressDialog.cancel();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            for (int i = 0; i < userGetGroupData.size(); i++) {
                                if (userGetGroupData.get(i).getId().equalsIgnoreCase(group_id)) {
                                    userGetGroupData.get(i).setMembers(response.body().getMembers());
                                    break;
                                }
                            }
                            connectionsGroupListAdapter.notifyDataSetChanged();
                            connectionsBinding.textViewNoGroup.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoveUserFromGroup> call, Throwable error) {
                ((MainActivity) getActivity()).progressDialog.cancel();
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                connectionsBinding.shimmerLayout.setVisibility(View.GONE);
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

    private void searchImportedContacts(String searchKey) {
        ArrayList<UserConnectionsData> tempImportedList = new ArrayList<>();
        for (int i = 0; i < userContactsDataTempListImported.size(); i++) {
            if (userContactsDataTempListImported.get(i).getUser_fname().toLowerCase().contains(searchKey)) {
                tempImportedList.add(userContactsDataTempListImported.get(i));
            }
        }
        userContactsDataListImported = tempImportedList;
    }

    private void sortList(List<UserConnectionsData> listOfConnections) {
        String sortingType = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL);
        if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_ALPHABETICAL)) {
            SortingUtils.sortContactsByString(listOfConnections, "alphabet");
            mAlphabetItems = new ArrayList<>();
            List<String> strAlphabets = new ArrayList<>();
            for (int i = 0; i < listOfConnections.size(); i++) {
                String name = "";
                if (!listOfConnections.get(i).getUser_fname().isEmpty()) {
                    name = listOfConnections.get(i).getUser_fname();
                } else {
                    name = listOfConnections.get(i).getContact_name();
                }
                if (name == null || name.trim().isEmpty())
                    continue;
                String word = name.substring(0, 1);
                if (StringMethods.getAlphabetsArray().contains(word)) {
                    if (!strAlphabets.contains(word)) {
                        strAlphabets.add(word);
                        mAlphabetItems.add(new AlphabetItem(i, word, false));
                    }
                }
            }
            connectionsBinding.recyclerViewAlphabets.setVisibility(View.VISIBLE);
            connectionsBinding.recyclerViewAlphabets.setRecyclerView((connectionsBinding.recyclerViewConnections));
            connectionsBinding.recyclerViewAlphabets.setUpAlphabet(mAlphabetItems);
        } else if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_DISTANC)) {
            SortingUtils.sortContactsByDouble(listOfConnections);
            connectionsBinding.recyclerViewAlphabets.setVisibility(View.GONE);
        } else if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_RECENTVIEWED)) {
            SortingUtils.sortContactsByString(listOfConnections, "recentViewed");
            connectionsBinding.recyclerViewAlphabets.setVisibility(View.GONE);
        } else if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, "").contains(EndpointKeys.SORT_RECENTLNQ)) {
            SortingUtils.sortContactsByString(listOfConnections, "recentLNQ");
            connectionsBinding.recyclerViewAlphabets.setVisibility(View.GONE);
        }
    }

    private void reqInviteLNQ(int position) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.mRoot);
        ((MainActivity) getActivity()).progressDialog.show();
        callUserInvite = Api.WEB_SERVICE.inviteLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                        sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                        sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""),
                userContactsDataListImported.get(position).getUser_fname(), userContactsDataListImported.get(position).getUser_lname(), userContactsDataListImported.get(position).getUser_email(), userContactsDataListImported.get(position).getUser_phone());
        callUserInvite.enqueue(new Callback<InviteLNQMainObject>() {
            @Override
            public void onResponse(Call<InviteLNQMainObject> call, Response<InviteLNQMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Invite sent successfully.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InviteLNQMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    @Override
    public void onChecked(View view, int position, boolean isChecked, UserConnectionsData exportContacts) {
        if (isChecked) {
            for (int i = 0; i < userContactsGroupData.size(); i++) {
                if (exportContacts.getProfile_id().equalsIgnoreCase(userContactsGroupData.get(i).getReceiver_profile_ids())) {
                    userContactsGroupData.remove(i);
                }
            }
            userContactsGroupData.add(new UserContactGroupMainObject(exportContacts.getUser_id(), exportContacts.getProfile_id()));
        } else {
            for (int i = 0; i < userContactsGroupData.size(); i++) {
                if (exportContacts.getProfile_id().equalsIgnoreCase(userContactsGroupData.get(i).getReceiver_profile_ids())) {
                    userContactsGroupData.remove(i);
                }
            }
        }
        exportContacts.setSelected(isChecked);
        createContactGroupAdapter.notifyItemChanged(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvFavorites:
                connectionsBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.mTvOutstandingTasks:
                connectionsBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.mTvPendingLnq:
                connectionsBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.mTvVerifiedProfiles:
                connectionsBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.mTvLnqUsersOnly:
                connectionsBinding.checkBoxLNQUsersOnly.setChecked(!userFilter.contains(Constants.LNQ_USER_ONLY));
                break;
            case R.id.mTvBlockedUsers:
                connectionsBinding.checkBoxBlockedUsers.setChecked(!userFilter.contains(Constants.BLOCKED_USERS));
                break;
            case R.id.mTvAccountHeading1:
            case R.id.imageViewBack:
                slideUp.hide();
                break;
            case R.id.mBtnApply:
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, userFilter.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "")).apply();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, sortType).apply();
                EventBus.getDefault().post(new EventBusUpdateFilters());
                EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                slideUp.hide();
                break;
            case R.id.mBtnAlphabetical:
                sortType = EndpointKeys.SORT_ALPHABETICAL;
                setSortBySelection(connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnDistance, connectionsBinding.mBtnRecentLNQs, connectionsBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnDistance:
                sortType = EndpointKeys.SORT_DISTANC;
                setSortBySelection(connectionsBinding.mBtnDistance, connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnRecentLNQs, connectionsBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentLNQs:
                sortType = EndpointKeys.SORT_RECENTLNQ;
                setSortBySelection(connectionsBinding.mBtnRecentLNQs, connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnDistance, connectionsBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentlyViewed:
                sortType = EndpointKeys.SORT_RECENTVIEWED;
                setSortBySelection(connectionsBinding.mBtnRecentlyViewed, connectionsBinding.mBtnAlphabetical, connectionsBinding.mBtnDistance, connectionsBinding.mBtnRecentLNQs);
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL).apply();
                connectionsBinding.checkBoxFavourites.setChecked(false);
                connectionsBinding.checkBoxBlockedUsers.setChecked(false);
                connectionsBinding.checkBoxLNQUsersOnly.setChecked(false);
                connectionsBinding.checkBoxOutstandingTasks.setChecked(false);
                connectionsBinding.checkBoxPendingLNQs.setChecked(false);
                connectionsBinding.checkBoxVerifiedProfile.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
        }
    }

    private void setSortBySelection(Button buttonSelected, Button... buttonDeselected) {
        buttonSelected.setBackground(getResources().getDrawable(R.drawable.bg_verification_blue_btn));
        buttonSelected.setTextColor(getResources().getColor(R.color.colorWhite));
        for (Button button : buttonDeselected) {
            button.setBackground(getResources().getDrawable(R.drawable.bg_blue_border_btn));
            button.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
        }
    }

    private void changeSelection(AppCompatCheckBox compatCheckBox) {
        compatCheckBox.setChecked(true);
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
        if (buttonView == connectionsBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, connectionsBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == connectionsBinding.checkBoxBlockedUsers) {
            isChecked(Constants.BLOCKED_USERS, connectionsBinding.checkBoxBlockedUsers.isChecked());
        } else if (buttonView == connectionsBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, connectionsBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == connectionsBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, connectionsBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == connectionsBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, connectionsBinding.checkBoxVerifiedProfile.isChecked());
        } else if (buttonView == connectionsBinding.checkBoxLNQUsersOnly) {
            isChecked(Constants.LNQ_USER_ONLY, connectionsBinding.checkBoxLNQUsersOnly.isChecked());
        }
    }

    public class ConnectionsClickHandler {

        public void onGridClick(View view) {
            if (currentLayout.equals("lnq") || currentLayout.equals("grid")) {
                changeButtonDrawable(connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.buttonGrid, connectionsBinding.clearTextViewList);
                connectionsBinding.recyclerViewConnections.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                connectionsGridAdapter = new ConnectionsGridAdapter(getActivity(), userContactsDataListLNQ);
                connectionsBinding.recyclerViewConnections.setAdapter(connectionsGridAdapter);
            }
            if (currentLayout.equals("imported") || currentLayout.equals("grid")) {
                changeButtonDrawable(connectionsBinding.buttonLNQImport, connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.buttonGrid, connectionsBinding.clearTextViewList);
                connectionsBinding.recyclerViewConnections.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                connectionsGridAdapter = new ConnectionsGridAdapter(getActivity(), userContactsDataListImported);
                connectionsBinding.recyclerViewConnections.setAdapter(connectionsGridAdapter);

            }
        }

        public void onListClick(View view) {
            if (currentLayout.equals("lnq") || currentLayout.equals("list")) {
                changeButtonDrawable(connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
                connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
                connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListLNQ);
                connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                connectionsBinding.recyclerViewConnections.setVisibility(View.VISIBLE);
                connectionsBinding.recyclerViewAlphabets.setVisibility(View.VISIBLE);
                connectionsBinding.recyclerViewGroups.setVisibility(View.GONE);
            }
            if (currentLayout.equals("imported") || currentLayout.equals("list")) {
                changeButtonDrawable(connectionsBinding.buttonLNQImport, connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQGroups);
//                changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
                connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
                connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListImported);
                connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
                connectionsBinding.recyclerViewConnections.setVisibility(View.VISIBLE);
                connectionsBinding.recyclerViewAlphabets.setVisibility(View.VISIBLE);
                connectionsBinding.recyclerViewGroups.setVisibility(View.GONE);
            }
        }

        public void onLNQUSersClick(View view) {
            currentLayout = "lnq";
//            changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
            changeButtonDrawable(connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport, connectionsBinding.buttonLNQGroups);
            connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
            connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListLNQ);
            connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
            connectionsBinding.recyclerViewConnections.setVisibility(View.VISIBLE);
            connectionsBinding.recyclerViewAlphabets.setVisibility(View.VISIBLE);
            connectionsBinding.recyclerViewGroups.setVisibility(View.GONE);
            connectionsBinding.shimmerLayout.setVisibility(View.GONE);
            connectionsBinding.shimmerLayoutGrid.setVisibility(View.GONE);
            sortList(userContactsDataListLNQ);
            if (userContactsDataListLNQ.size() == 0) {
                connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.VISIBLE);
                connectionsBinding.textViewNoGroup.setVisibility(View.GONE);
            } else {
                connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.GONE);
                connectionsBinding.textViewNoGroup.setVisibility(View.GONE);
            }
        }

        public void onLNQImportClick(View view) {
            currentLayout = "imported";
//            changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
            changeButtonDrawable(connectionsBinding.buttonLNQImport, connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQGroups);
            connectionsBinding.recyclerViewConnections.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
            connectionsAdapter = new ConnectionsListAdapter(getActivity(), userContactsDataListImported);
            connectionsBinding.recyclerViewConnections.setAdapter(connectionsAdapter);
            connectionsBinding.recyclerViewConnections.setVisibility(View.VISIBLE);
            connectionsBinding.recyclerViewAlphabets.setVisibility(View.VISIBLE);
            connectionsBinding.recyclerViewGroups.setVisibility(View.GONE);
            connectionsBinding.shimmerLayout.setVisibility(View.GONE);
            connectionsBinding.shimmerLayoutGrid.setVisibility(View.GONE);
            sortList(userContactsDataListImported);
            if (userContactsDataListImported.size() == 0) {
                connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.VISIBLE);
                connectionsBinding.textViewNoGroup.setVisibility(View.GONE);
            } else {
                connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.GONE);
                connectionsBinding.textViewNoGroup.setVisibility(View.GONE);
            }
        }

        public void onGroupClicked(View view) {
            connectionsBinding.recyclerViewGroups.setVisibility(View.VISIBLE);
//            changeButtonDrawable(connectionsBinding.clearTextViewList, connectionsBinding.buttonGrid);
            changeButtonDrawable(connectionsBinding.buttonLNQGroups, connectionsBinding.clearTextViewLNQ, connectionsBinding.buttonLNQImport);
            connectionsBinding.recyclerViewGroups.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
            connectionsGroupListAdapter = new ConnectionsGroupListAdapter(getActivity(), userGetGroupData);
            connectionsBinding.recyclerViewGroups.setAdapter(connectionsGroupListAdapter);
            connectionsBinding.recyclerViewConnections.setVisibility(View.GONE);
            connectionsBinding.recyclerViewAlphabets.setVisibility(View.GONE);
            connectionsBinding.shimmerLayout.setVisibility(View.GONE);
            connectionsBinding.shimmerLayoutGrid.setVisibility(View.GONE);
            if (userGetGroupData.size() == 0) {
                connectionsBinding.textViewNoGroup.setVisibility(View.VISIBLE);
                connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.GONE);
            } else {
                connectionsBinding.textViewNoGroup.setVisibility(View.GONE);
                connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.GONE);
            }
        }

//        public void onFilterClick(View view) {
//            ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.mRoot);
//            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.CONNECTION_FILTER, true, null);
//        }

        public void onSearchClick(View view) {
            reqContacts(connectionsBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
        }

//        public void onMenuClick(View view) {
//            if (connectionsBinding.imageViewArrowUp.getVisibility() == View.VISIBLE && connectionsBinding.cardViewPopUpContactsContainer.getVisibility() == View.VISIBLE) {
//                connectionsBinding.imageViewArrowUp.setVisibility(View.GONE);
//                connectionsBinding.cardViewPopUpContactsContainer.setVisibility(View.GONE);
//            } else {
//                connectionsBinding.imageViewArrowUp.setVisibility(View.VISIBLE);
//                connectionsBinding.cardViewPopUpContactsContainer.setVisibility(View.VISIBLE);
//            }
//        }

        public void onLnqCountClick(View view) {
//            hideDialog();
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_COUNTS, true, null);
            EventBus.getDefault().post(new EventBusUserSession("lnq_count_clicked"));
        }

        public void onImportClick(View view) {
//            hideDialog();
            if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
                showImportContactsFragment("local");
//                EventBus.getDefault().post(new EventBusUserSession(""));
            } else {
                ((MainActivity) getActivity()).fnRequestContactsPermission(5);
            }
        }

        public void onImportToGmailClick(View view) {
//            hideDialog();
            if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
                showImportContactsFragment("gmail");
            } else {
                ((MainActivity) getActivity()).fnRequestContactsPermission(9);
            }
        }

        public void onCreateGroupClick(View view) {
            onGroupClicked(view);
            if (userContactsDataListLNQ.size() != 0) {
//            hideDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.cus_dialog_createcontactgroups, null);
                RecyclerView recyclerViewAddGroupContactList = dialogView.findViewById(R.id.recyclerViewAddGroupContactList);
                Button clearTextViewCreateGroup = dialogView.findViewById(R.id.clearTextViewCreateGroup);
                Button clearTextViewCancel = dialogView.findViewById(R.id.clearTextViewCancel);
                EditText editTextGroupName = dialogView.findViewById(R.id.editTextEnterGroupName);
                TextInputLayout textInputLayoutGroupName = dialogView.findViewById(R.id.textInputLayoutGroupName);
                textInputLayoutGroupName.setVisibility(View.VISIBLE);

                for (int i = 0; i < userContactsDataListLNQ.size(); i++) {
                    userContactsDataListLNQ.get(i).setSelected(false);
                }

                createContactGroupAdapter = new CreateContactGroupAdapter(getActivity(), userContactsDataListLNQ);
                recyclerViewAddGroupContactList.setAdapter(createContactGroupAdapter);
                createContactGroupAdapter.setOnCheckedListener(FragmentConnections.this);
                recyclerViewAddGroupContactList.setLayoutManager(new LinearLayoutManager(getActivity()));

                builder.setView(dialogView);
                dialog = builder.create();
                dialog.show();

                clearTextViewCreateGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> reveiverIdList = new ArrayList<>();
                        List<String> receiverProfileIdList = new ArrayList<>();
                        for (int i = 0; i < userContactsGroupData.size(); i++) {
                            reveiverIdList.add(userContactsGroupData.get(i).getReceiver_ids());
                            receiverProfileIdList.add(userContactsGroupData.get(i).getReceiver_profile_ids());
                        }
                        String reveiverId = reveiverIdList.toString().replaceAll(", ", ",").replaceAll("[\\[.\\]]", "");
                        String receiverProfileId = receiverProfileIdList.toString().replaceAll(", ", ",").replaceAll("[\\[.\\]]", "");
                        if (!editTextGroupName.getText().toString().isEmpty()) {
                            reqCreateGroup(profileId, editTextGroupName.getText().toString(), reveiverId, receiverProfileId, groupId, "createGroup");
                            dialog.cancel();
                        } else {
                            Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                            editTextGroupName.startAnimation(animShake);
                            ((MainActivity) getActivity()).showMessageDialog("error", "Please enter group name.");
                        }
                    }
                });

                clearTextViewCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            }else {
                ValidUtils.showCustomToast(getContext(), "You do not have any LNQ contacts.");
            }
        }

        public void onExportClick(View view) {
//            hideDialog();
//            if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
            showExportContactsFragment("local_export");
//            } else {
//                ((MainActivity) getActivity()).fnRequestContactsPermission(7);
        }

//        public void onExportSalesforceClick(View view) {
//            hideDialog();
//            if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
//                showExportContactsFragment("salesforce_export");
//            } else {
//                ((MainActivity) getActivity()).fnRequestContactsPermission(11);
//            }
//        }
//
//        public void onExportCSVClick(View view) {
//            hideDialog();
//            if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
//                showExportContactsFragment("csv_export");
//            } else {
//                ((MainActivity) getActivity()).fnRequestContactsPermission(10);
//            }
//        }

        public void onSearchMemberClick(View view) {
            if (!((MainActivity) getActivity()).mFScreenName.contentEquals(Constants.HOME)) {
//                ((MainActivity) getActivity()).mBind.mViewBgBottomBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).mFScreenName = Constants.HOME;
                ((MainActivity) getActivity()).fnLoadFragReplace(Constants.HOME, false, null);
            } else if (!((MainActivity) getActivity()).mFScreenName.contentEquals(Constants.HOME_GRID)) {
//                ((MainActivity) getActivity()).mBind.mViewBgBottomBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).mFScreenName = Constants.HOME_GRID;
                ((MainActivity) getActivity()).fnLoadFragReplace(Constants.HOME_GRID, false, null);
            }
        }

        public void onCloseClick(View view) {
            LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "");
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "");
            LnqApplication.getInstance().editor.apply();
            connectionsBinding.editTextSearch.setText("");
            toggleFilterButtonBackground();
            ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.mRoot);
        }

        public void onQrCodeClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), connectionsBinding.getRoot());
//        if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
//        } else {
//            ((MainActivity) getActivity()).fnRequestCameraPermission(8);
//        }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusConnectionsFilter(EventBusConnectionsFilter connectionsFilter) {
        if (getActivity() != null) {
            toggleFilterButtonBackground();
            refreshExportContacts(true);
        }
    }

    //    Method to refresh users on map using reqUsersInRadius Request....
    private void refreshExportContacts(boolean isUpdateMapProfile) {

    }

    private void toggleFilterButtonBackground() {
        String filters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
        if (filters != null)
            if (filters.isEmpty()) {
                connectionsBinding.buttonFilter.setVisibility(View.VISIBLE);
//                connectionsBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
                connectionsBinding.horizontalScrollViewConnectionsFilters.setVisibility(View.GONE);
            } else {
                if (layoutInflater == null) {
                    layoutInflater = LayoutInflater.from(getContext());
                }
//                connectionsBinding.linearLayoutConnectionsFilter.removeAllViews();
                List<String> filterList = new ArrayList<>(Arrays.asList(filters.split(",")));
                if (filterList.size() > 0) {
                    for (int i = 0; i < filterList.size(); i++) {
                        View filterView = layoutInflater.inflate(R.layout.row_filter, null);
                        TextView textViewFilter = filterView.findViewById(R.id.textViewFilterRow);
                        textViewFilter.setText(filterList.get(i));
                        ImageView imageViewCloseFilter = filterView.findViewById(R.id.imageViewCloseFilter);
                        imageViewCloseFilter.setOnClickListener(view -> {
//                            connectionsBinding.linearLayoutConnectionsFilter.removeView(filterView);
                            filterList.remove(textViewFilter.getText().toString());
                            String filter = filterList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "");
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, filter).apply();
                            EventBus.getDefault().post(new EventBusUpdateFilters());
                            EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                            if (filterList.size() == 0) {
                                connectionsBinding.buttonFilter.setVisibility(View.VISIBLE);
//                                connectionsBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
                                connectionsBinding.horizontalScrollViewConnectionsFilters.setVisibility(View.GONE);
                            }
                        });
//                        connectionsBinding.linearLayoutConnectionsFilter.addView(filterView);
                    }
                }
                connectionsBinding.horizontalScrollViewConnectionsFilters.setVisibility(View.VISIBLE);
                connectionsBinding.buttonFilter.setVisibility(View.INVISIBLE);
//                connectionsBinding.clearTextViewFilter.setVisibility(View.VISIBLE);
            }
    }

}