package lnq.com.lnq.fragments.connections.export_contacts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ExportContactsAdapter;
import lnq.com.lnq.adapters.ExportContactsListAdapter;
import lnq.com.lnq.adapters.MentionChatAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.LinearLayoutManagerWithSmoothScroller;
import lnq.com.lnq.custom.views.fast_scroller.models.AlphabetItem;
import lnq.com.lnq.databinding.FragmentExportContactsBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.fragments.connections.salesforce.GlobalState;
import lnq.com.lnq.fragments.connections.salesforce.Login_Salesforce;
import lnq.com.lnq.fragments.connections.salesforce.OAuthTokens;
import lnq.com.lnq.fragments.connections.salesforce.OAuthUtil;
import lnq.com.lnq.fragments.fullprofileview.FragmentLnqUserProfileView;
import lnq.com.lnq.model.FoundedContactModel;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.attachements.SendAttachementsModel;
import lnq.com.lnq.model.event_bus_models.EventBusContactPermission;
import lnq.com.lnq.model.event_bus_models.EventBusSalesforceLogin;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChat;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateExportFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusAlphabetClick;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.ExportCSVModel;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContactsMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts.ImportContactsModel;
import lnq.com.lnq.model.PhoneContactsModel;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContactsModel;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContacts;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.SelectedExportContact;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatData;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.Contact;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.PhoneContact;
import lnq.com.lnq.model.event_bus_models.EventBusExportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusGetLnqedContactList;
import lnq.com.lnq.model.salesforce.SalesforceAttributes;
import lnq.com.lnq.model.salesforce.SalesforceContactModel;
import lnq.com.lnq.model.salesforce.SalesforceModel;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class FragmentExportContacts extends Fragment implements ExportContactsAdapter.OnCheckedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TransferUtility transferUtility;

    //    Android fields....
    private FragmentExportContactsBinding exportContactsBinding;
    private ExportContactsClickHandler clickHandler;
    SQLiteDatabase sampleDB;
    Dialog dialog;
    private LayoutInflater layoutInflater;
    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private ArrayList<String> alphabetsList = new ArrayList<>();
    private List<PhoneContactsModel> phoneContactsModelList = new ArrayList<>();
    private List<ExportContactsModel> exportContactsModelList = new ArrayList<>();
    private List<ImportContactsModel> contactsModelList = new ArrayList<>();
    private List<SelectedExportContact> selectedExportContactsList = new ArrayList<>();
    private List<SelectedExportContact> tempSelectedExportContactsList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();
    private List<ExportContacts> exportContactsList = new ArrayList<>();

    //    Contacts related instance fields....
    private int totalExportContactCounter = 0;
    private int totalFoundedContactCounter = 0;
    private String countryRegion;
    private PhoneNumberUtil phoneNumberUtil;
    String importType;
    private String searchSuggestion;

    private List<AlertDialog> dialogList = new ArrayList<>();

    //    Retrofit fields....
    private Call<ExportContactsMainObject> callExportscontacts;
    private Call<ExportCSVModel> exportCSVModelCall;

    //    Adapter fields....
    private ExportContactsAdapter exportContactsAdapter;
    private ExportContactsListAdapter exportContactsListAdapter;
    private List<AlphabetItem> mAlphabetItems = new ArrayList<>();

    private List<FoundedContactModel> foundedContactModels = new ArrayList<>();

    private List<String> userFilter = new ArrayList<>();
    private String sortType;
    private SlideUp slideUp;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewBackTopBar;
    CardView topBarLayout;

    public FragmentExportContacts() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        exportContactsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_export_contacts, container, false);
        return exportContactsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransferUtility();
        init();
        OverScrollDecoratorHelper.setUpOverScroll(exportContactsBinding.recyclerViewExportContacts, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        topBarLayout = exportContactsBinding.topBarImportContact.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.export_connections);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);

        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        textViewHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(exportContactsBinding.slideViewContacts)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    exportContactsBinding.checkBoxFavourites.setChecked(false);
                                    exportContactsBinding.checkBoxBlockedUsers.setChecked(false);
                                    exportContactsBinding.checkBoxLNQUsersOnly.setChecked(false);
                                    exportContactsBinding.checkBoxOutstandingTasks.setChecked(false);
                                    exportContactsBinding.checkBoxPendingLNQs.setChecked(false);
                                    exportContactsBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(exportContactsBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(exportContactsBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(exportContactsBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(exportContactsBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                            if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                                changeSelection(exportContactsBinding.checkBoxLNQUsersOnly);
                                                userFilter.add(Constants.LNQ_USER_ONLY);
                                            }
                                            if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                                changeSelection(exportContactsBinding.checkBoxBlockedUsers);
                                                userFilter.add(Constants.BLOCKED_USERS);
                                            }
                                        }
                                    }
                                    exportContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.VISIBLE);
                                    exportContactsBinding.viewHideTopBar.setVisibility(View.GONE);
                                    exportContactsBinding.searchBarLayout.setVisibility(View.GONE);
                                } else {
                                    exportContactsBinding.checkBoxFavourites.setChecked(false);
                                    exportContactsBinding.checkBoxBlockedUsers.setChecked(false);
                                    exportContactsBinding.checkBoxLNQUsersOnly.setChecked(false);
                                    exportContactsBinding.checkBoxOutstandingTasks.setChecked(false);
                                    exportContactsBinding.checkBoxPendingLNQs.setChecked(false);
                                    exportContactsBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(exportContactsBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(exportContactsBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(exportContactsBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(exportContactsBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                            if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                                changeSelection(exportContactsBinding.checkBoxLNQUsersOnly);
                                                userFilter.add(Constants.LNQ_USER_ONLY);
                                            }
                                            if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                                changeSelection(exportContactsBinding.checkBoxBlockedUsers);
                                                userFilter.add(Constants.BLOCKED_USERS);
                                            }
                                        }
                                    }
                                    exportContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                                    exportContactsBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                                    exportContactsBinding.searchBarLayout.setVisibility(View.VISIBLE);
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
    }

    private void init() {
        if (getArguments() != null) {
            importType = getArguments().getString("import_type", "local_export");
//        Registering event bus for multiple triggers....
            EventBus.getDefault().register(this);

//        Getting country region....
            countryRegion = ValidUtils.getCountryCode(getActivity());

//        Initialization of phone number utils....
            phoneNumberUtil = PhoneNumberUtil.getInstance();

//        Setting custom font....
            setCustomFont();

            exportContactsBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));

            exportContactsBinding.recyclerViewExportContacts.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));

//        Setting item animators of recycler view....
            exportContactsBinding.recyclerViewExportContacts.setItemAnimator(new DefaultItemAnimator());

//        Setting click handler for data binding....
            clickHandler = new ExportContactsClickHandler();
            exportContactsBinding.setClickHandler(clickHandler);

//        All event listeners....
            exportContactsBinding.editTextSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().isEmpty()) {
                        exportContactsBinding.imageViewClose.setVisibility(View.GONE);
                        LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                        toggleFilterButtonBackground();
                    } else {
                        exportContactsBinding.imageViewClose.setVisibility(View.VISIBLE);
                    }
                    LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, s.toString()).apply();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            exportContactsBinding.editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.mRoot);
                        exportContactsAdapter.getFilter().filter(exportContactsBinding.editTextSearch.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
            toggleFilterButtonBackground();

            exportContactsBinding.recyclerViewExportContacts.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ValidUtils.hideKeyboardFromFragment(getContext(), exportContactsBinding.getRoot());
                    return false;
                }
            });
        }

        exportContactsBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
                    exportContactsBinding.searchBarLayout.setVisibility(View.GONE);
                }
            }
        });

        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
            String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
            if (userFilters != null) {
                if (userFilters.contains(Constants.FAVORITES)) {
                    changeSelection(exportContactsBinding.checkBoxFavourites);
                    userFilter.add(Constants.FAVORITES);
                }
                if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                    changeSelection(exportContactsBinding.checkBoxVerifiedProfile);
                    userFilter.add(Constants.VERIFIED_PROFILE);
                }
                if (userFilters.contains(Constants.PENDING_LNQS)) {
                    changeSelection(exportContactsBinding.checkBoxPendingLNQs);
                    userFilter.add(Constants.PENDING_LNQS);
                }
                if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                    changeSelection(exportContactsBinding.checkBoxOutstandingTasks);
                    userFilter.add(Constants.OUTSTANDING_TASKS);
                }
                if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                    changeSelection(exportContactsBinding.checkBoxLNQUsersOnly);
                    userFilter.add(Constants.LNQ_USER_ONLY);
                }
                if (userFilters.contains(Constants.BLOCKED_USERS)) {
                    changeSelection(exportContactsBinding.checkBoxBlockedUsers);
                    userFilter.add(Constants.BLOCKED_USERS);
                }
            }
        }
        String user_sorting = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL);
        if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_ALPHABETICAL)) {
            setSortBySelection(exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnRecentLNQs, exportContactsBinding.mBtnRecentlyViewed);
        } else if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_DISTANC)) {
            setSortBySelection(exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnRecentLNQs, exportContactsBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTLNQ)) {
            setSortBySelection(exportContactsBinding.mBtnRecentLNQs, exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTVIEWED)) {
            setSortBySelection(exportContactsBinding.mBtnRecentlyViewed, exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnRecentLNQs);
        }

        exportContactsBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
        exportContactsBinding.checkBoxBlockedUsers.setOnCheckedChangeListener(this);
        exportContactsBinding.checkBoxLNQUsersOnly.setOnCheckedChangeListener(this);
        exportContactsBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
        exportContactsBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
        exportContactsBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

        exportContactsBinding.imageViewBack.setOnClickListener(this);
        exportContactsBinding.textViewClearAll.setOnClickListener(this);
        exportContactsBinding.mBtnAlphabetical.setOnClickListener(this);
        exportContactsBinding.mBtnDistance.setOnClickListener(this);
        exportContactsBinding.mBtnRecentLNQs.setOnClickListener(this);
        exportContactsBinding.mBtnApply.setOnClickListener(this);
        exportContactsBinding.mBtnRecentlyViewed.setOnClickListener(this);
        exportContactsBinding.mTvFavorites.setOnClickListener(this);
        exportContactsBinding.mTvVerifiedProfiles.setOnClickListener(this);
        exportContactsBinding.mTvPendingLnq.setOnClickListener(this);
        exportContactsBinding.mTvOutstandingTasks.setOnClickListener(this);
        exportContactsBinding.mTvLnqUsersOnly.setOnClickListener(this);
        exportContactsBinding.mTvBlockedUsers.setOnClickListener(this);
        exportContactsBinding.mTvAccountHeading1.setOnClickListener(this);

        slideUp = new SlideUpBuilder(exportContactsBinding.slideViewContacts)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            exportContactsBinding.checkBoxFavourites.setChecked(false);
                            exportContactsBinding.checkBoxBlockedUsers.setChecked(false);
                            exportContactsBinding.checkBoxLNQUsersOnly.setChecked(false);
                            exportContactsBinding.checkBoxOutstandingTasks.setChecked(false);
                            exportContactsBinding.checkBoxPendingLNQs.setChecked(false);
                            exportContactsBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(exportContactsBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(exportContactsBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(exportContactsBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(exportContactsBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                    if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                        changeSelection(exportContactsBinding.checkBoxLNQUsersOnly);
                                        userFilter.add(Constants.LNQ_USER_ONLY);
                                    }
                                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                        changeSelection(exportContactsBinding.checkBoxBlockedUsers);
                                        userFilter.add(Constants.BLOCKED_USERS);
                                    }
                                }
                            }
                            exportContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.VISIBLE);
                            exportContactsBinding.viewHideTopBar.setVisibility(View.GONE);
                            exportContactsBinding.searchBarLayout.setVisibility(View.GONE);
                        } else {
                            exportContactsBinding.checkBoxFavourites.setChecked(false);
                            exportContactsBinding.checkBoxBlockedUsers.setChecked(false);
                            exportContactsBinding.checkBoxLNQUsersOnly.setChecked(false);
                            exportContactsBinding.checkBoxOutstandingTasks.setChecked(false);
                            exportContactsBinding.checkBoxPendingLNQs.setChecked(false);
                            exportContactsBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(exportContactsBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(exportContactsBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(exportContactsBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(exportContactsBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                    if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                                        changeSelection(exportContactsBinding.checkBoxLNQUsersOnly);
                                        userFilter.add(Constants.LNQ_USER_ONLY);
                                    }
                                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                        changeSelection(exportContactsBinding.checkBoxBlockedUsers);
                                        userFilter.add(Constants.BLOCKED_USERS);
                                    }
                                }
                            }
                            exportContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                            exportContactsBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                            exportContactsBinding.searchBarLayout.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(exportContactsBinding.viewScroll)
                .build();
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity().getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, getActivity().getApplicationContext());
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewMedium(exportContactsBinding.clearTextViewExport);
        fontUtils.setEditTextSemiBold(exportContactsBinding.editTextSearch);
        fontUtils.setTextViewRegularFont(exportContactsBinding.textViewSortBy);
        fontUtils.setTextViewRegularFont(exportContactsBinding.textViewClearAll);
        fontUtils.setTextViewRegularFont(exportContactsBinding.mTvFavorites);
        fontUtils.setTextViewRegularFont(exportContactsBinding.mTvVerifiedProfiles);
        fontUtils.setTextViewRegularFont(exportContactsBinding.mTvPendingLnq);
        fontUtils.setTextViewRegularFont(exportContactsBinding.mTvOutstandingTasks);
        fontUtils.setTextViewRegularFont(exportContactsBinding.mTvLnqUsersOnly);
        fontUtils.setTextViewRegularFont(exportContactsBinding.mTvBlockedUsers);
        fontUtils.setButtonRegularFont(exportContactsBinding.mBtnAlphabetical);
        fontUtils.setButtonRegularFont(exportContactsBinding.mBtnDistance);
        fontUtils.setButtonRegularFont(exportContactsBinding.mBtnRecentLNQs);
        fontUtils.setButtonRegularFont(exportContactsBinding.mBtnRecentlyViewed);
        fontUtils.setTextViewRegularFont(exportContactsBinding.mBtnApply);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUserContactsDataList(EventBusGetLnqedContactList eventBusGetLnqedContactList) {
        if (exportContactsAdapter != null)
            exportContactsAdapter.notifyItemRangeRemoved(0, exportContactsList.size());
        userContactsDataList.clear();
        contactsModelList.clear();
        exportContactsList.clear();
        userContactsDataList = eventBusGetLnqedContactList.getGetLnqContactList();
        for (int i = 0; i < userContactsDataList.size(); i++) {
            UserConnectionsData userContactsData = userContactsDataList.get(i);
            exportContactsModelList.add(new ExportContactsModel(userContactsData.getUser_id(), userContactsData.getUser_fname() + " " + userContactsData.getUser_lname(), userContactsData.getUser_avatar(), userContactsData.getUser_phone(), userContactsData.getContact_status(), userContactsData.getIs_connection(),
                    false, userContactsData.getIs_favorite(), userContactsData.getIs_blocked(), userContactsData.getUser_email(), userContactsData.getUserNote(), userContactsData.getUser_address(), userContactsData.getUser_company(), userContactsData.getUser_birthday(), userContactsData.getUser_current_position(), userContactsData.getContact_id()));
        }
        exportContactsAdapter = new ExportContactsAdapter(getActivity(), exportContactsList);
        exportContactsBinding.recyclerViewExportContacts.setAdapter(exportContactsAdapter);
        exportContactsAdapter.setOnCheckedListener(this);
        for (int i = 0; i < exportContactsModelList.size(); i++) {
            List<String> phones = new ArrayList<>();
            List<String> email = new ArrayList<>();
            for (int j = 0; j < exportContactsModelList.size(); j++) {
                if (exportContactsModelList.get(i).getContactId().equals(exportContactsModelList.get(j).getContactId())) {
                    phones.add(exportContactsModelList.get(j).getPhone());
                    email.add(exportContactsModelList.get(j).getEmail());
                }
            }
            exportContactsList.add(new ExportContacts(exportContactsModelList.get(i).getId(), exportContactsModelList.get(i).getName(), exportContactsModelList.get(i).getImage(), phones,
                    exportContactsModelList.get(i).getContactStatus(), exportContactsModelList.get(i).getConnectionStatus(), false, exportContactsModelList.get(i).getIsFavorite(), exportContactsModelList.get(i).getIsBlocked(), email,
                    exportContactsModelList.get(i).getNote(), exportContactsModelList.get(i).getAddress(), exportContactsModelList.get(i).getCompany(), exportContactsModelList.get(i).getBirthday(), exportContactsModelList.get(i).getJob(), exportContactsModelList.get(i).getContactId()));
        }
        ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
        for (int i = 1; i < exportContactsList.size(); i++) {
            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(exportContactsModelList.get(i).getId())) {
                mentionModelArrayList.add(new MentionModel(exportContactsModelList.get(i).getId(), exportContactsModelList.get(i).getName().toLowerCase(), exportContactsModelList.get(i).getImage(), ""));
            }
        }
        MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
        exportContactsBinding.editTextSearch.setAdapter(mentionChatAdapter);
        exportContactsBinding.editTextSearch.setThreshold(1);
        exportContactsBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.getRoot());
                exportContactsBinding.editTextSearch.setText(exportContactsBinding.editTextSearch.getText());
                exportContactsAdapter.getFilter().filter(exportContactsBinding.editTextSearch.getText().toString());
            }
        });
        Collections.sort(exportContactsList, new Comparator<ExportContacts>() {
            @Override
            public int compare(ExportContacts o1, ExportContacts o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < exportContactsList.size(); i++) {
            String word = exportContactsList.get(i).getName().substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }
        exportContactsAdapter.notifyItemRangeInserted(0, exportContactsList.size());
        exportContactsBinding.recyclerViewAlphabets.setRecyclerView((exportContactsBinding.recyclerViewExportContacts));
        exportContactsBinding.recyclerViewAlphabets.setUpAlphabet(mAlphabetItems);

        //        Checking phone contact permission....
        /*if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
            for (int i = 0; i < userContactsDataList.size(); i++) {
                UserConnectionsData userContactsData = userContactsDataList.get(i);
                contactsModelList.add(new ImportContactsModel(userContactsData.getUser_id(), userContactsData.getUser_fname() + " " + userContactsData.getUser_lname(), userContactsData.getUser_avatar(), userContactsData.getUser_phone(), userContactsData.getContact_status(), userContactsData.getUser_email()));
            }
            if (importType.equalsIgnoreCase("local_export")) {
                new GetAllContactsTask().execute();
            } else if (importType.equalsIgnoreCase("salesforce_export")) {
                new GetAllContactsTask().execute();
            } else if (importType.equalsIgnoreCase("csv_export")) {
                new GetAllContactsTask().execute();
            }
        } else {
            ((MainActivity) getActivity()).fnRequestContactsPermission(5);
        }*/
    }

    class GetAllContactsTask extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            if (getActivity() != null) {
                PhoneContact phoneContact = new PhoneContact(getActivity());
                return phoneContact.getAllContacts();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);
            if (contacts != null) {
                if (contacts.size() > 0) {
                    getAllContacts(contacts);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void getAllContacts(List<Contact> contactList) {
        phoneContactsModelList.clear();
        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            HashMap<Integer, String> hashMapPhoneNumbers = contact.getPhones();
            HashMap<Integer, String> hashMapEmails = contact.getEmails();
            List<String> phoneList = new ArrayList<>();
            List<String> emailList = new ArrayList<>();
            if (hashMapPhoneNumbers != null) {
                for (Map.Entry<Integer, String> phoneEntry : hashMapPhoneNumbers.entrySet()) {
                    int key = phoneEntry.getKey();
                    String value = phoneEntry.getValue();
                    try {
                        if (!phoneNumberUtil.isValidNumber(phoneNumberUtil.parseAndKeepRawInput(value, Constants.DEFAULT_REGION))) {
                            value = phoneNumberUtil.format(phoneNumberUtil.parseAndKeepRawInput(value, countryRegion.toUpperCase()), PhoneNumberUtil.PhoneNumberFormat.E164);
                            hashMapPhoneNumbers.put(key, value);
                        }
                    } catch (NumberParseException e) {

                    }
                }
                for (Map.Entry<Integer, String> phoneEntry : hashMapPhoneNumbers.entrySet()) {
                    phoneList.add(phoneEntry.getValue());
                }
                if (hashMapEmails != null) {
                    for (Map.Entry<Integer, String> emailEntry : hashMapEmails.entrySet()) {
                        emailList.add(emailEntry.getValue());
                    }
                }
                phoneContactsModelList.add(new PhoneContactsModel(contact.getId(), contact.getDisplayName(), phoneList, emailList, contact.getImage(), false));
            }
        }
        tempSelectedExportContactsList.clear();
        tempSelectedExportContactsList.addAll(selectedExportContactsList);
        for (SelectedExportContact selectedExportContact : tempSelectedExportContactsList) {
            FoundedContactModel foundedContactModel = new FoundedContactModel();
            int foundCounter = 0;
            for (PhoneContactsModel phoneContactsModel : phoneContactsModelList) {
                for (String phone : phoneContactsModel.getPhoneNumber()) {
                    for (String addedPhone : selectedExportContact.getNumber()) {
                        if (phone.equalsIgnoreCase(addedPhone)) {
                            foundedContactModel.contactId = phoneContactsModel.getId();
                            foundedContactModel.phoneContactsModel = phoneContactsModel;
                            foundedContactModel.foundContactType = "phone";
                            foundCounter++;
                        }
                    }
                }
                if (foundCounter == 0) {
                    for (String email : phoneContactsModel.getEmail()) {
                        for (String addedEmail : selectedExportContact.getEmail()) {
                            if (email.equalsIgnoreCase(addedEmail)) {
                                foundedContactModel.contactId = phoneContactsModel.getId();
                                foundedContactModel.phoneContactsModel = phoneContactsModel;
                                foundedContactModel.foundContactType = "email";
                                foundCounter++;
                            }
                        }
                    }
                }
            }
            if (foundCounter > 0) {
                foundedContactModel.selectedExportContact = selectedExportContact;
                foundedContactModels.add(foundedContactModel);
                selectedExportContactsList.remove(selectedExportContact);
            }
        }
        if (selectedExportContactsList.size() > 0) {
            for (SelectedExportContact selectedExportContact : selectedExportContactsList) {
                AddToContactsTask addToContactsTask = new AddToContactsTask();
                addToContactsTask.execute(selectedExportContact);
            }
        } else {
            ((MainActivity) getActivity()).progressDialog.dismiss();
            if (foundedContactModels.size() > 0) {
//                for (int i = 0; i < foundedContactModels.size(); i++) {
                showSameContactFoundDialog();
//                }
            } else {
                ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + tempSelectedExportContactsList.size() + " connections to phone contacts.");
            }
        }
    }

    @Override
    public void onChecked(View view, int position, boolean isChecked, ExportContacts exportContacts) {
        if (isChecked) {
            for (int i = 0; i < selectedExportContactsList.size(); i++) {
                if (exportContacts.getContactId().equalsIgnoreCase(selectedExportContactsList.get(i).getContactId())) {
                    selectedExportContactsList.remove(i);
                }
            }
            selectedExportContactsList.add(new SelectedExportContact(exportContacts.getContactId(), exportContacts.getName(), exportContacts.getPhone(), exportContacts.getEmail(), exportContacts.getNote(), exportContacts.getAddress(), exportContacts.getCompany(), exportContacts.getBirthday(), exportContacts.getJob(), exportContacts.getImage()));
        } else {
            for (int i = 0; i < selectedExportContactsList.size(); i++) {
                if (exportContacts.getContactId().equalsIgnoreCase(selectedExportContactsList.get(i).getContactId())) {
                    selectedExportContactsList.remove(i);
                }
            }
        }
        exportContacts.setSelected(isChecked);
        exportContactsAdapter.notifyItemChanged(position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusExportUsers(EventBusExportUsers eventBusExportUsers) {
        if (selectedExportContactsList.size() > 0) {
            ((MainActivity) getActivity()).progressDialog.show();
            new GetAllContactsTask().execute();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSalesforceLogin(EventBusSalesforceLogin eventBusSalesforceLogin) {
        OAuthTokens tokens = OAuthUtil.Load(getApplicationContext());
        if (tokens != null) {
            String salesforceInstanceUrl = tokens.get_instance_url();
            if (salesforceInstanceUrl != null && !salesforceInstanceUrl.isEmpty()) {
                exportContactsToSalesforce(salesforceInstanceUrl, tokens.get_access_token());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusContactPermission(EventBusContactPermission mObj) {
        if (mObj.getPermissionType().equalsIgnoreCase(EndpointKeys.EXPORT) && mObj.getRequestCode() == 7) {
            EventBus.getDefault().post(new EventBusExportUsers());
            EventBus.getDefault().post(new EventBusUserSession("export_contacts"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateFilter(EventBusUpdateFilters eventBusUpdateFilters) {
        toggleFilterButtonBackground();
        reqContacts(exportContactsBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
    }

    private void exportContactsToSalesforce(String salesforceUrl, String accessToken) {
        if (selectedExportContactsList.size() > 0) {
            ((MainActivity) getActivity()).progressDialog.show();
            List<SalesforceModel> salesforceModelList = new ArrayList<>();

            for (int i = 0; i < selectedExportContactsList.size(); i++) {
                SelectedExportContact selectedExportContact = selectedExportContactsList.get(i);
                String name = selectedExportContact.getName();
                String lastName = "";
                String firstName = "";
                if (name.split("\\w+").length > 1) {

                    lastName = name.substring(name.lastIndexOf(" ") + 1);
                    firstName = name.substring(0, name.lastIndexOf(' '));
                    if (lastName.equals("")) {
                        lastName = firstName;
                    }
                } else {
                    firstName = name;
                }
                salesforceModelList.add(new SalesforceModel(firstName, lastName, selectedExportContact.getNumber().size() > 0 ? selectedExportContact.getNumber().get(0) : "", selectedExportContact.getEmail().size() > 0 ? selectedExportContact.getEmail().get(0) : "", new SalesforceAttributes("Contact", "ref" + i)));
            }
            if (salesforceModelList.size() > 0) {
                SalesforceContactModel salesforceContactModel = new SalesforceContactModel(salesforceModelList);
                Gson gson = new Gson();
                String json = gson.toJson(salesforceContactModel);
                AndroidNetworking.post(salesforceUrl + "/services/data/v34.0/composite/tree/Contact/")
                        .addHeaders("Authorization", "Bearer " + accessToken)
                        .setContentType("application/json")
                        .addStringBody(json)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((MainActivity) getActivity()).progressDialog.dismiss();
                                if (response != null) {
                                    ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + selectedExportContactsList.size() + " connections to SalesForce.");
                                    getActivity().onBackPressed();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                ((MainActivity) getActivity()).progressDialog.dismiss();
                                ValidUtils.showToast(getActivity(), anError.getMessage());
                            }
                        });
            }
        }
    }

    //    Event bus method triggers when any alphabet is clicked....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fnAlphaEvent(EventBusAlphabetClick eventBusAlphaId) {
        String alpha = alphabetsList.get(eventBusAlphaId.getmPoss());
        for (int i = 0; i < phoneContactsModelList.size(); i++) {
            if (!phoneContactsModelList.get(i).getName().isEmpty()) {
                if (phoneContactsModelList.get(i).getName().length() > 1) {
                    if (alpha.equalsIgnoreCase(phoneContactsModelList.get(i).getName().substring(0, 1))) {
                        exportContactsBinding.recyclerViewExportContacts.smoothScrollToPosition(i);
                        break;
                    }
                }
            }
        }
    }

    class AddToContactsTask extends AsyncTask<SelectedExportContact, Void, SelectedExportContact> {

        @Override
        protected SelectedExportContact doInBackground(SelectedExportContact... contactsEntities) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            String name = contactsEntities[0].getName();
            List<String> number = contactsEntities[0].getNumber();
            List<String> email = contactsEntities[0].getEmail();
            String note = contactsEntities[0].getNote();
            String birthday = contactsEntities[0].getBirthday();
            String address = contactsEntities[0].getAddress();
            String company = contactsEntities[0].getCompany();
            String job = contactsEntities[0].getJob();
            String image = contactsEntities[0].getImage();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            if (name != null && !name.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                name).build());
            }
            if (number != null && number.size() > 0) {
                for (String num : number) {
                    if (!num.isEmpty()) {
                        ops.add(ContentProviderOperation.
                                newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, num)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .build());
                    }
                }
            }
            if (email != null && email.size() > 0) {
                for (String eml : email) {
                    if (eml != null && !eml.isEmpty()) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, eml)
                                .build());
                    }
                }
            }
            if (note != null && !note.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, "Notes exported from LNQ on " + DateUtils.getDateForContactNote() + "\n" + note)
                        .build());
            }

            if (birthday != null && !birthday.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, birthday)
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                        .build());
            }
            if (company != null && !company.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, job)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .build());
            }
            if (address != null && !address.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                        .build());
            }
            Glide.with(getActivity())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            if (resource != null) {    // If an image is selected successfully
                                resource.compress(Bitmap.CompressFormat.PNG, 75, stream);

                                // Adding insert operation to operations list
                                // to insert Photo in the table ContactsContract.Data
                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                                        .build());

                                try {
                                    stream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                if (getActivity() != null)
                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {

            }
            return contactsEntities[0];
        }

        @Override
        protected void onPostExecute(SelectedExportContact selectedExportContact) {
            super.onPostExecute(selectedExportContact);
            totalExportContactCounter++;
            if (totalExportContactCounter == selectedExportContactsList.size()) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).progressDialog.dismiss();
                    if (foundedContactModels.size() > 0) {
//                        for (int i = 0; i < foundedContactModels.size(); i++) {
                        showSameContactFoundDialog();
//                        }
                    } else {
                        ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + tempSelectedExportContactsList.size() + " connections to phone contacts");
                        totalExportContactCounter = 0;
                        getActivity().onBackPressed();
                    }
                }
            }
        }
    }

    private void showSameContactFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.cus_dialog_exportcontactsmatch, null);
        TextView textViewDescription = dialogView.findViewById(R.id.replaceContactDescription);
        Button textNo = dialogView.findViewById(R.id.textViewNo);
        Button textViewReplace = dialogView.findViewById(R.id.textViewReplace);
        TextView textViewNameLnqContact = dialogView.findViewById(R.id.textViewNameLnqContact);
        TextView textViewPhoneLnqContact = dialogView.findViewById(R.id.textViewPhoneLnqContact);
        TextView textViewEmailLnqContact = dialogView.findViewById(R.id.textViewEmailLnqContact);
        TextView textViewNamePhoneContact = dialogView.findViewById(R.id.textViewNamePhoneContact);
        TextView textViewNumberPhoneContact = dialogView.findViewById(R.id.textViewNumberPhoneContact);
        TextView textViewEmailPhoneContact = dialogView.findViewById(R.id.textViewEmailPhoneContact);
        AppCompatImageView imageViewProfilePhoneContact = dialogView.findViewById(R.id.imageViewProfilePhoneContact);
        AppCompatImageView imageViewProfileLnqContact = dialogView.findViewById(R.id.imageViewProfileLnqContact);
        ValidUtils.buttonGradientColor(textNo);

        FoundedContactModel foundedContactModel = foundedContactModels.get(totalFoundedContactCounter);
        if (foundedContactModel != null) {
            if (foundedContactModel.selectedExportContact.getImage() != null && !foundedContactModel.selectedExportContact.getImage().isEmpty()) {
                download(foundedContactModel.selectedExportContact.getImage(), imageViewProfileLnqContact);
            }

            textViewNameLnqContact.setText(foundedContactModel.selectedExportContact.getName());
            textViewPhoneLnqContact.setText(foundedContactModel.selectedExportContact.getNumber().get(0));
            textViewEmailLnqContact.setText(foundedContactModel.selectedExportContact.getEmail().get(0));

            if (foundedContactModel.selectedExportContact.getImage() != null && !foundedContactModel.selectedExportContact.getImage().isEmpty()) {
                download(foundedContactModel.selectedExportContact.getImage(), imageViewProfilePhoneContact);
            }
            textViewNamePhoneContact.setText(foundedContactModel.phoneContactsModel.getName());
            textViewNumberPhoneContact.setText(foundedContactModel.phoneContactsModel.getPhoneNumber().size() > 0 ? foundedContactModel.phoneContactsModel.getPhoneNumber().get(0) : "");
            textViewEmailPhoneContact.setText(foundedContactModel.phoneContactsModel.getEmail().size() > 0 ? foundedContactModel.phoneContactsModel.getEmail().get(0) : "");

            textViewDescription.setText("This LNQ contact matched with this phone \ncontact. Would you like to replace it?");

//            textViewDescription.setText("A contact\nName: " + foundedContactModel.selectedExportContact.getName() + "\nmatched with a contact with\n" + (foundedContactModel.foundContactType.equals("phone") ? "Phone: " : "Email: ") + (foundedContactModel.foundContactType.equals("phone") ? foundedContactModel.selectedExportContact.getNumber().get(0) : foundedContactModel.selectedExportContact.getEmail().get(0)) + "\n Would you like to replace that contact?");
        }
        textViewReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteContactTask deleteContactTask = new DeleteContactTask();
                deleteContactTask.execute(foundedContactModel);
                dialog.dismiss();
            }
        });

        textNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
//                if (totalFoundedContactCounter == foundedContactModels.size() - 1) {
//                    if (getActivity() != null) {
//                        ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + tempSelectedExportContactsList.size() + " connections to phone contacts");
//                        totalFoundedContactCounter = 0;
//                        getActivity().onBackPressed();
//                    }
//                } else {
//                    totalFoundedContactCounter++;
//                    showSameContactFoundDialog();
//                }
                DeleteContactTask deleteContactTask = new DeleteContactTask();
                deleteContactTask.execute(foundedContactModel);
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
        try {
            dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);
        } catch (Exception e) {

        }
    }

    void download(String objectKey, ImageView imageView) {
        final File fileDownload = new File(getActivity().getCacheDir(), objectKey);

        TransferObserver transferObserver = transferUtility.download(
                Constants.BUCKET_NAME,
                objectKey,
                fileDownload
        );
        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + state);
                if (TransferState.COMPLETED.equals(state)) {
                    Glide.with(getActivity())
                            .load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()))
                            .apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                            .apply(new RequestOptions().circleCrop())
                            .into(imageView);
//                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));

                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "onError: ", ex);
            }
        });
    }

    class DeleteContactTask extends AsyncTask<FoundedContactModel, Void, FoundedContactModel> {

        @Override
        protected FoundedContactModel doInBackground(FoundedContactModel... foundedContactModels) {
            final ArrayList ops = new ArrayList();
            final ContentResolver cr = getActivity().getContentResolver();
            ops.add(ContentProviderOperation
                    .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?",
                            new String[]{foundedContactModels[0].contactId})
                    .build());
            try {
                cr.applyBatch(ContactsContract.AUTHORITY, ops);
                ops.clear();
            } catch (OperationApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (RemoteException e) {
                // System.out.println(" length :"+i);
            }
            return foundedContactModels[0];
        }

        @Override
        protected void onPostExecute(FoundedContactModel foundedContactModel) {
            super.onPostExecute(foundedContactModel);
            AddDeletedContactsTask addDeletedContactsTask = new AddDeletedContactsTask();
            addDeletedContactsTask.execute(foundedContactModel.selectedExportContact);
        }
    }

    class AddDeletedContactsTask extends AsyncTask<SelectedExportContact, Void, SelectedExportContact> {

        @Override
        protected SelectedExportContact doInBackground(SelectedExportContact... contactsEntities) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            String name = contactsEntities[0].getName();
            List<String> number = contactsEntities[0].getNumber();
            List<String> email = contactsEntities[0].getEmail();
            String note = contactsEntities[0].getNote();
            String birthday = contactsEntities[0].getBirthday();
            String address = contactsEntities[0].getAddress();
            String company = contactsEntities[0].getCompany();
            String job = contactsEntities[0].getJob();
            String image = contactsEntities[0].getImage();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            if (name != null && !name.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                name).build());
            }
            if (number != null && number.size() > 0) {
                for (String num : number) {
                    if (!num.isEmpty()) {
                        ops.add(ContentProviderOperation.
                                newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, num)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .build());
                    }
                }
            }
            if (email != null && email.size() > 0) {
                for (String eml : email) {
                    if (eml != null && !eml.isEmpty()) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, eml)
                                .build());
                    }
                }
            }
            if (note != null && !note.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, "Notes exported from LNQ on " + DateUtils.getDateForContactNote() + "\n" + note)
                        .build());
            }
            if (birthday != null && !birthday.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, birthday)
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                        .build());
            }
            if (company != null && !company.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, job)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .build());
            }
            if (address != null && !address.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                        .build());
            }

            Glide.with(getActivity())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            if (resource != null) {    // If an image is selected successfully
                                resource.compress(Bitmap.CompressFormat.PNG, 75, stream);

                                // Adding insert operation to operations list
                                // to insert Photo in the table ContactsContract.Data
                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                                        .build());

                                try {
                                    stream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (getActivity() != null)
                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {

            }
            return contactsEntities[0];
        }

        @Override
        protected void onPostExecute(SelectedExportContact selectedExportContact) {
            super.onPostExecute(selectedExportContact);
            totalFoundedContactCounter++;
            if (totalFoundedContactCounter == foundedContactModels.size()) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).progressDialog.dismiss();
                    ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + tempSelectedExportContactsList.size() + " connections to phone contacts");
                    totalFoundedContactCounter = 0;
                    getActivity().onBackPressed();
                }
            } else {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                showSameContactFoundDialog();
            }
        }
    }

    public class ExportContactsClickHandler {

        public void onCloseClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.getRoot());
            exportContactsBinding.editTextSearch.setText("");
            exportContactsAdapter.getFilter().filter(exportContactsBinding.editTextSearch.getText().toString());
        }

        public void onSearchClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.getRoot());
            exportContactsAdapter.getFilter().filter(exportContactsBinding.editTextSearch.getText().toString());
        }

        public void onSelectAllClick(View view) {
            exportContactsBinding.imageViewDeSelectAllContacts.setVisibility(View.VISIBLE);
            exportContactsBinding.imageViewSelectAllContacts.setVisibility(View.INVISIBLE);
            selectedExportContactsList.clear();
            for (int i = 0; i < exportContactsList.size(); i++) {
                selectedExportContactsList.add(new SelectedExportContact(exportContactsList.get(i).getContactId(), exportContactsList.get(i).getName(), exportContactsList.get(i).getPhone(), exportContactsList.get(i).getEmail(), exportContactsList.get(i).getNote(), exportContactsList.get(i).getAddress(), exportContactsList.get(i).getCompany(), exportContactsList.get(i).getBirthday(), exportContactsList.get(i).getJob(), exportContactsList.get(i).getImage()));
                exportContactsList.get(i).setSelected(true);
                exportContactsAdapter.notifyItemChanged(i);
            }
        }

        public void onDeselectAllClick(View view) {
            exportContactsBinding.imageViewSelectAllContacts.setVisibility(View.VISIBLE);
            exportContactsBinding.imageViewDeSelectAllContacts.setVisibility(View.INVISIBLE);
            selectedExportContactsList.clear();
            for (int i = 0; i < exportContactsList.size(); i++) {
                exportContactsList.get(i).setSelected(false);
                exportContactsAdapter.notifyItemChanged(i);
            }
        }

        public void onExportClick(View view) {
            if (selectedExportContactsList.size() == 0) {
                Toast.makeText(getContext(), "No contact selected", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.cus_dialog_exportcontactsnew, null);
                TextView textViewDescription = dialogView.findViewById(R.id.lnqDescription);
                Button textViewMyPhone = dialogView.findViewById(R.id.textViewPhoneContacts);
                Button textViewSalesForce = dialogView.findViewById(R.id.textViewSalesForceConnections);
                Button textViewCSV = dialogView.findViewById(R.id.textViewCSVConnections);
                Button textViewCancel = dialogView.findViewById(R.id.textViewCancel);
                ValidUtils.buttonGradientColor(textViewCancel);
                RecyclerView recyclerViewExportList = dialogView.findViewById(R.id.recyclerViewExportList);

                exportContactsListAdapter = new ExportContactsListAdapter(getActivity(), selectedExportContactsList);
                recyclerViewExportList.setAdapter(exportContactsListAdapter);
                recyclerViewExportList.setLayoutManager(new LinearLayoutManager(getActivity()));

                int count = 0;
                if (recyclerViewExportList.getAdapter() != null) {
                    count = recyclerViewExportList.getAdapter().getItemCount();
                }
                if (count <= 1) {
                    recyclerViewExportList.getLayoutParams().height = (int) getResources().getDimension(R.dimen._70sdp);
                } else if (count <= 2) {
                    recyclerViewExportList.getLayoutParams().height = (int) getResources().getDimension(R.dimen._140sdp);
                } else {
                    recyclerViewExportList.getLayoutParams().height = (int) getResources().getDimension(R.dimen._195sdp);
                }

                textViewDescription.setText("You have selected" + " " + selectedExportContactsList.size() + " " + "contacts.Where \n would you like to export these \n connections to.");

                textViewMyPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMyPhoneClick(v);
                        dialog.dismiss();
                    }
                });

                textViewSalesForce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSalesforceClick(v);
                        dialog.dismiss();
                    }
                });

                textViewCSV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCSVClick(v);
                        dialog.dismiss();
                    }
                });

                textViewCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                builder.setView(dialogView);
                dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }

            }
        }

        public void onCancelClick(View view) {
            dialog.cancel();
        }

        public void onMyPhoneClick(View view) {
            if (selectedExportContactsList.size() > 0) {
                if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
                    EventBus.getDefault().post(new EventBusExportUsers());
                    EventBus.getDefault().post(new EventBusUserSession("export_contacts"));
                } else {
                    ((MainActivity) getActivity()).fnRequestContactsPermission(7);
                }
            }
        }

        public void onSalesforceClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOGIN_SALESFORCE, true, null);
        }

        public void onCSVClick(View view) {
            if (((MainActivity) getActivity()).fnCheckReadStoragePermission()) {
                try {
                    exportTheDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((MainActivity) getActivity()).fnRequestStoragePermission(10);
            }
        }

//           if (importType.equalsIgnoreCase("local_export")) {
//            if (selectedExportContactsList.size() > 0) {
//                ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + selectedExportContactsList.size() + " connections to your Device");
//                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EXPORT_USERS, true, null);
//            }
//        } else if (importType.equalsIgnoreCase("salesforce_export")) {
//            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOGIN_SALESFORCE, true, null);
//        } else if (importType.equalsIgnoreCase("csv_export")) {
//            if (((MainActivity) getActivity()).fnCheckReadStoragePermission()) {
//                try {
//                    exportTheDB();
//                    ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + userContactsDataList.size() + " connections to your CSV");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                ((MainActivity) getActivity()).fnRequestStoragePermission(10);
//            }
//        }

        public void onFilterClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.mRoot);
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.EXPORT_CONTACTS_FILTER, true, null);
        }

        public void onAllStartClick(View view) {

        }

        public void onQrCodeClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.getRoot());
//            if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
//            } else {
//                ((MainActivity) getActivity()).fnRequestCameraPermission(8);
//            }
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

    }

    private void exportTheDB() throws IOException {
        File myFile;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String TimeStampDB = sdf.format(cal.getTime());

        myFile = new File(Environment.getExternalStorageDirectory() + "/Export_" + TimeStampDB + ".csv");
        myFile.createNewFile();
        FileOutputStream fOut = new FileOutputStream(myFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//        myOutWriter.append("Name;Phone Number;Email;Birthday;Job Title;Address;Note");
        myOutWriter.append("Name");
        myOutWriter.append(',');
        myOutWriter.append("Phone Number");
        myOutWriter.append(',');
        myOutWriter.append("Email");
        myOutWriter.append(',');
        myOutWriter.append("Birthday");
        myOutWriter.append(',');
        myOutWriter.append("Job Title");
        myOutWriter.append(',');
        myOutWriter.append("Address");
        myOutWriter.append(',');
        myOutWriter.append("Note");
        myOutWriter.append(',');
        myOutWriter.append("\n");

        if (selectedExportContactsList != null && selectedExportContactsList.size() > 0) {
            for (SelectedExportContact connectionsData : selectedExportContactsList) {
                String name = connectionsData.getName();
                String phoneNumber = connectionsData.getNumber().get(0);
                String email = connectionsData.getEmail().size() > 0 ? connectionsData.getEmail().get(0) : "";
                String birthday = connectionsData.getBirthday();
                String jobTitle = connectionsData.getJob();
                String address = connectionsData.getAddress();
                String note = connectionsData.getNote();

                myOutWriter.append(name);
                myOutWriter.append(',');
                myOutWriter.append(phoneNumber);
                myOutWriter.append(',');
                myOutWriter.append(email);
                myOutWriter.append(',');
                myOutWriter.append(birthday.replace(",", " "));
                myOutWriter.append(',');
                myOutWriter.append(jobTitle);
                myOutWriter.append(',');
                myOutWriter.append(address.replace(",", " "));
                myOutWriter.append(',');
                if (note != null && !note.isEmpty()) {
                    note = "Notes exported from LNQ on " + DateUtils.getDateForContactNote() + " " + note;
                    myOutWriter.append(note.replaceAll(",", " "));
                } else {
                    note = "";
                    myOutWriter.append(note.replaceAll(",", " "));
                }
                myOutWriter.append(',');
                myOutWriter.append("\n");
            }
            myOutWriter.close();
            fOut.close();
            exportCSV(myFile);
        }

    }

    public void exportCSV(File file) {
        ((MainActivity) getActivity()).progressDialog.show();
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("csv", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file));
        exportCSVModelCall = Api.WEB_SERVICE.exportcsv(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                filePart,
                id);
        exportCSVModelCall.enqueue(new Callback<ExportCSVModel>() {
            @Override
            public void onResponse(Call<ExportCSVModel> call, Response<ExportCSVModel> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body() != null) {
                                ((MainActivity) getActivity()).showMessageDialog("success", "You've successfully exported " + selectedExportContactsList.size() + " connections as CSV.Please check your email.");
                                getActivity().onBackPressed();
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ExportCSVModel> call, Throwable error) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusExportFilters(EventBusUpdateExportFilters exportFilters) {
        if (getActivity() != null) {
            toggleFilterButtonBackground();
            refreshExportContacts(true);
        }
    }

    //    Method to refresh users with filters....
    private void refreshExportContacts(boolean isUpdateMapProfile) {

    }

    private void toggleFilterButtonBackground() {
        String filters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
        if (filters != null)
            if (filters.isEmpty()) {
                exportContactsBinding.buttonFilter.setVisibility(View.VISIBLE);
//                exportContactsBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
//                exportContactsBinding.horizontalScrollViewExportConnectionsFilters.setVisibility(View.GONE);
            } else {
                if (layoutInflater == null) {
                    layoutInflater = LayoutInflater.from(getContext());
                }
//                exportContactsBinding.linearLayoutExportConnectionsFilter.removeAllViews();
                List<String> filterList = new ArrayList<>(Arrays.asList(filters.split(",")));
                if (filterList.size() > 0) {
                    for (int i = 0; i < filterList.size(); i++) {
                        View filterView = layoutInflater.inflate(R.layout.row_filter, null);
                        TextView textViewFilter = filterView.findViewById(R.id.textViewFilterRow);
                        textViewFilter.setText(filterList.get(i));
                        ImageView imageViewCloseFilter = filterView.findViewById(R.id.imageViewCloseFilter);
                        int finalI = i;
                        imageViewCloseFilter.setOnClickListener(view -> {
//                            exportContactsBinding.linearLayoutExportConnectionsFilter.removeView(filterView);
                            filterList.remove(textViewFilter.getText().toString());
                            String filter = filterList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "");
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, filter).apply();
                            EventBus.getDefault().post(new EventBusUpdateFilters());
                            EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                            if (filterList.size() == 0) {
                                exportContactsBinding.buttonFilter.setVisibility(View.VISIBLE);
//                                exportContactsBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
//                                exportContactsBinding.horizontalScrollViewExportConnectionsFilters.setVisibility(View.GONE);
                            }
                        });
//                        exportContactsBinding.linearLayoutExportConnectionsFilter.addView(filterView);
                    }
                }
//                exportContactsBinding.horizontalScrollViewExportConnectionsFilters.setVisibility(View.VISIBLE);
                exportContactsBinding.buttonFilter.setVisibility(View.INVISIBLE);
//                exportContactsBinding.clearTextViewFilter.setVisibility(View.VISIBLE);
            }
    }

    //    Method to get all imported contacts or connections from server....
    private void reqContacts(String searchKey, String searchFilter) {
        exportContactsList.clear();
        ValidUtils.hideKeyboardFromFragment(getActivity(), exportContactsBinding.mRoot);
        exportContactsBinding.progressBar.setVisibility(View.VISIBLE);
//        callExportscontacts = Api.WEB_SERVICE.exportContacts(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().
//                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
//                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
//                sharedPreferences.getString(EndpointKeys.ID, ""), searchKey);

        callExportscontacts = Api.WEB_SERVICE.exportContacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), searchKey);

        callExportscontacts.enqueue(new Callback<ExportContactsMainObject>() {
            public void onResponse(Call<ExportContactsMainObject> call, Response<ExportContactsMainObject> response) {
                exportContactsBinding.progressBar.setVisibility(View.INVISIBLE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:

                            break;
                        case 0:
                            if (response.body().getMessage() != null) {
                                if (response.body().getMessage().equalsIgnoreCase("No contacts found")) {

                                }
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ExportContactsMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                exportContactsBinding.shimmerLayout.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    private void setSortBySelection(Button buttonSelected, Button... buttonDeselected) {
        buttonSelected.setBackground(getResources().getDrawable(R.drawable.bg_verification_blue_btn));
        buttonSelected.setTextColor(getResources().getColor(R.color.colorWhite));
        for (Button button : buttonDeselected) {
            button.setBackground(getResources().getDrawable(R.drawable.bg_blue_border_btn));
            button.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvFavorites:
                exportContactsBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.mTvOutstandingTasks:
                exportContactsBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.mTvPendingLnq:
                exportContactsBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.mTvVerifiedProfiles:
                exportContactsBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.mTvLnqUsersOnly:
                exportContactsBinding.checkBoxLNQUsersOnly.setChecked(!userFilter.contains(Constants.LNQ_USER_ONLY));
                break;
            case R.id.mTvBlockedUsers:
                exportContactsBinding.checkBoxBlockedUsers.setChecked(!userFilter.contains(Constants.BLOCKED_USERS));
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
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, sortType).apply();
                EventBus.getDefault().post(new EventBusUserSession("export_filter"));
                EventBus.getDefault().post(new EventBusUpdateFilters());
                slideUp.hide();
                break;
            case R.id.mBtnAlphabetical:
                sortType = EndpointKeys.SORT_ALPHABETICAL;
                setSortBySelection(exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnRecentLNQs, exportContactsBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnDistance:
                sortType = EndpointKeys.SORT_DISTANC;
                setSortBySelection(exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnRecentLNQs, exportContactsBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentLNQs:
                sortType = EndpointKeys.SORT_RECENTLNQ;
                setSortBySelection(exportContactsBinding.mBtnRecentLNQs, exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentlyViewed:
                sortType = EndpointKeys.SORT_RECENTVIEWED;
                setSortBySelection(exportContactsBinding.mBtnRecentlyViewed, exportContactsBinding.mBtnAlphabetical, exportContactsBinding.mBtnDistance, exportContactsBinding.mBtnRecentLNQs);
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL).apply();
                exportContactsBinding.checkBoxFavourites.setChecked(false);
                exportContactsBinding.checkBoxBlockedUsers.setChecked(false);
                exportContactsBinding.checkBoxLNQUsersOnly.setChecked(false);
                exportContactsBinding.checkBoxOutstandingTasks.setChecked(false);
                exportContactsBinding.checkBoxPendingLNQs.setChecked(false);
                exportContactsBinding.checkBoxVerifiedProfile.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
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
        if (buttonView == exportContactsBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, exportContactsBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == exportContactsBinding.checkBoxBlockedUsers) {
            isChecked(Constants.BLOCKED_USERS, exportContactsBinding.checkBoxBlockedUsers.isChecked());
        } else if (buttonView == exportContactsBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, exportContactsBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == exportContactsBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, exportContactsBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == exportContactsBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, exportContactsBinding.checkBoxVerifiedProfile.isChecked());
        } else if (buttonView == exportContactsBinding.checkBoxLNQUsersOnly) {
            isChecked(Constants.LNQ_USER_ONLY, exportContactsBinding.checkBoxLNQUsersOnly.isChecked());
        }
    }

}