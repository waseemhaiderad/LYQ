package lnq.com.lnq.fragments.setting.account;


import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentSyncContactsBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusConnectionView;
import lnq.com.lnq.model.event_bus_models.EventBusGetLnqedContactList;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnections;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContacts;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContactsMainObject;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSyncContacts extends Fragment implements View.OnClickListener {

    private FragmentSyncContactsBinding mBind;
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();
    private Call<UserConnectionsMainObject> callUserConnections;
    private List<UserConnections> userContactList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListImported = new ArrayList<>();

    private String profileId;

    public FragmentSyncContacts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_sync_contacts, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

        mBind.imageViewBack.setOnClickListener(this);
        mBind.mTvImportContacts.setOnClickListener(this);
        mBind.mImportImgLeftSign.setOnClickListener(this);
        mBind.mTvExportContacts.setOnClickListener(this);
        mBind.mImgExportLeftSign.setOnClickListener(this);
        OverScrollDecoratorHelper.setUpOverScroll(mBind.nestedScrollView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.mTvImportContacts:
            case R.id.mImportImgLeftSign:
                if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
                    if (userContactsDataList.size() > 0) {
                        showImportContactsFragment("local");
                    } else {
                        reqImportContacts("", "","import");
                    }
                } else {
                    ((MainActivity) getActivity()).fnRequestContactsPermission(5);
                }
                break;
            case R.id.mTvExportContacts:
            case R.id.mImgExportLeftSign:
                if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
                    if (userContactsDataList.size() > 0) {
                        showExportContactsFragment("local_export");
                    } else {
                        reqImportContacts("", "","export");
                    }
                } else {
                    ((MainActivity) getActivity()).fnRequestContactsPermission(7);
                }
                break;
        }
    }


    private void reqImportContacts(String searchKey, String searchFilter, String openFragment) {
        ((MainActivity) getActivity()).progressDialog.show();
        userContactsDataList.clear();
        callUserConnections = Api.WEB_SERVICE.contacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), searchKey, searchFilter, profileId);
        callUserConnections.enqueue(new Callback<UserConnectionsMainObject>() {
            public void onResponse(Call<UserConnectionsMainObject> call, Response<UserConnectionsMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    ((MainActivity) getActivity()).progressDialog.dismiss();
                    switch (response.body().getStatus()) {
                        case 1:
                            userContactList = response.body().getUserContacts();
                            for (int i = 0; i < userContactList.size(); i++) {
                                UserConnections userContact = userContactList.get(i);
                                userContactsDataList.add(userContact.getUser_data());
                            }
                            for (int i = 0; i < userContactsDataList.size(); i++) {
                                UserConnectionsData userContactsData = userContactsDataList.get(i);
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
                            if (userContactsDataList.size() > 0) {
                                String sortingType = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL);
                                if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_ALPHABETICAL)) {
                                    SortingUtils.sortContactsByString(userContactsDataList, "alphabet");
                                    List<String> strAlphabets = new ArrayList<>();
                                } else if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_DISTANC)) {
                                    SortingUtils.sortContactsByDouble(userContactsDataList);
                                } else if (sortingType.equalsIgnoreCase(EndpointKeys.SORT_RECENTVIEWED)) {
                                    SortingUtils.sortContactsByString(userContactsDataList, "recentViewed");
                                } else if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, "").contains(EndpointKeys.SORT_RECENTLNQ)) {
                                    SortingUtils.sortContactsByString(userContactsDataList, "recentLNQ");
                                }
                                EventBus.getDefault().post(new EventBusGetLnqedContactList(userContactsDataList));
                            }
                            EventBus.getDefault().post(new EventBusConnectionView());
                            if (openFragment.equals("import")) {
                                showImportContactsFragment("local");
                            } else {
                                showExportContactsFragment("local_export");
                            }
                            break;
                        case 0:
                            if (openFragment.equals("import")) {
                                showImportContactsFragment("local");
                            } else {
                                showExportContactsFragment("local_export");
                            }
                            if (response.body().getMessage() != null) {
                                if (response.body().getMessage().equalsIgnoreCase("No contacts found")) {
//                                    connectionsBinding.constraintLayoutNoLnqFound.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserConnectionsMainObject> call, Throwable error) {
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
}