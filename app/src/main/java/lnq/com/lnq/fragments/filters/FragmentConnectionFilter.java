package lnq.com.lnq.fragments.filters;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentConnectionFilterBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;

public class FragmentConnectionFilter extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentConnectionFilterBinding connectionFiltersBinding;

    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();
    private String sortType;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentConnectionFilter() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        connectionFiltersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_connection_filter, container, false);
        return connectionFiltersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Setting custom font....
        setCustomFont();

        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
            String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
            if (userFilters != null) {
                if (userFilters.contains(Constants.FAVORITES)) {
                    changeSelection(connectionFiltersBinding.checkBoxFavourites);
                    userFilter.add(Constants.FAVORITES);
                }
                if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                    changeSelection(connectionFiltersBinding.checkBoxVerifiedProfile);
                    userFilter.add(Constants.VERIFIED_PROFILE);
                }
                if (userFilters.contains(Constants.PENDING_LNQS)) {
                    changeSelection(connectionFiltersBinding.checkBoxPendingLNQs);
                    userFilter.add(Constants.PENDING_LNQS);
                }
                if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                    changeSelection(connectionFiltersBinding.checkBoxOutstandingTasks);
                    userFilter.add(Constants.OUTSTANDING_TASKS);
                }
                if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                    changeSelection(connectionFiltersBinding.checkBoxLNQUsersOnly);
                    userFilter.add(Constants.LNQ_USER_ONLY);
                }
                if (userFilters.contains(Constants.BLOCKED_USERS)) {
                    changeSelection(connectionFiltersBinding.checkBoxBlockedUsers);
                    userFilter.add(Constants.BLOCKED_USERS);
                }
            }
        }
        String user_sorting = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL);
        if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_ALPHABETICAL)) {
            setSortBySelection(connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnRecentLNQs, connectionFiltersBinding.mBtnRecentlyViewed);
        } else if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_DISTANC)) {
            setSortBySelection(connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnRecentLNQs, connectionFiltersBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTLNQ)) {
            setSortBySelection(connectionFiltersBinding.mBtnRecentLNQs, connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTVIEWED)) {
            setSortBySelection(connectionFiltersBinding.mBtnRecentlyViewed, connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnRecentLNQs);
        }

        connectionFiltersBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
        connectionFiltersBinding.checkBoxBlockedUsers.setOnCheckedChangeListener(this);
        connectionFiltersBinding.checkBoxLNQUsersOnly.setOnCheckedChangeListener(this);
        connectionFiltersBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
        connectionFiltersBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
        connectionFiltersBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

        connectionFiltersBinding.imageViewBack.setOnClickListener(this);
        connectionFiltersBinding.textViewClearAll.setOnClickListener(this);
        connectionFiltersBinding.mBtnAlphabetical.setOnClickListener(this);
        connectionFiltersBinding.mBtnDistance.setOnClickListener(this);
        connectionFiltersBinding.mBtnRecentLNQs.setOnClickListener(this);
        connectionFiltersBinding.mBtnApply.setOnClickListener(this);
        connectionFiltersBinding.mBtnRecentlyViewed.setOnClickListener(this);
        connectionFiltersBinding.mTvFavorites.setOnClickListener(this);
        connectionFiltersBinding.mTvVerifiedProfiles.setOnClickListener(this);
        connectionFiltersBinding.mTvPendingLnq.setOnClickListener(this);
        connectionFiltersBinding.mTvOutstandingTasks.setOnClickListener(this);
        connectionFiltersBinding.mTvLnqUsersOnly.setOnClickListener(this);
        connectionFiltersBinding.mTvBlockedUsers.setOnClickListener(this);
        connectionFiltersBinding.viewCloseFilter.setOnClickListener(this);

    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.textViewSortBy);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.textViewClearAll);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mTvShowOnly);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mTvFavorites);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mTvVerifiedProfiles);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mTvPendingLnq);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mTvOutstandingTasks);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mTvLnqUsersOnly);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mTvBlockedUsers);
        fontUtils.setButtonRegularFont(connectionFiltersBinding.mBtnAlphabetical);
        fontUtils.setButtonRegularFont(connectionFiltersBinding.mBtnDistance);
        fontUtils.setButtonRegularFont(connectionFiltersBinding.mBtnRecentLNQs);
        fontUtils.setButtonRegularFont(connectionFiltersBinding.mBtnRecentlyViewed);
        fontUtils.setTextViewRegularFont(connectionFiltersBinding.mBtnApply);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvFavorites:
                connectionFiltersBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.mTvOutstandingTasks:
                connectionFiltersBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.mTvPendingLnq:
                connectionFiltersBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.mTvVerifiedProfiles:
                connectionFiltersBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.mTvLnqUsersOnly:
                connectionFiltersBinding.checkBoxLNQUsersOnly.setChecked(!userFilter.contains(Constants.LNQ_USER_ONLY));
                break;
            case R.id.mTvBlockedUsers:
                connectionFiltersBinding.checkBoxBlockedUsers.setChecked(!userFilter.contains(Constants.BLOCKED_USERS));
                break;
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.viewCloseFilter:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnApply:
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, userFilter.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "")).apply();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, sortType).apply();
                EventBus.getDefault().post(new EventBusUpdateFilters());
                EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                getActivity().onBackPressed();
                break;
            case R.id.mBtnAlphabetical:
                sortType = EndpointKeys.SORT_ALPHABETICAL;
                setSortBySelection(connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnRecentLNQs, connectionFiltersBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnDistance:
                sortType = EndpointKeys.SORT_DISTANC;
                setSortBySelection(connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnRecentLNQs, connectionFiltersBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentLNQs:
                sortType = EndpointKeys.SORT_RECENTLNQ;
                setSortBySelection(connectionFiltersBinding.mBtnRecentLNQs, connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentlyViewed:
                sortType = EndpointKeys.SORT_RECENTVIEWED;
                setSortBySelection(connectionFiltersBinding.mBtnRecentlyViewed, connectionFiltersBinding.mBtnAlphabetical, connectionFiltersBinding.mBtnDistance, connectionFiltersBinding.mBtnRecentLNQs);
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL).apply();
                connectionFiltersBinding.checkBoxFavourites.setChecked(false);
                connectionFiltersBinding.checkBoxBlockedUsers.setChecked(false);
                connectionFiltersBinding.checkBoxLNQUsersOnly.setChecked(false);
                connectionFiltersBinding.checkBoxOutstandingTasks.setChecked(false);
                connectionFiltersBinding.checkBoxPendingLNQs.setChecked(false);
                connectionFiltersBinding.checkBoxVerifiedProfile.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
        }
    }

    private void setSortBySelection(Button buttonSelected, Button... buttonDeselected) {
        buttonSelected.setBackground(getResources().getDrawable(R.drawable.bg_verification_white_btn));
        buttonSelected.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
        for (Button button : buttonDeselected) {
            button.setBackground(getResources().getDrawable(R.drawable.bg_login_emailreset_btn));
            button.setTextColor(getResources().getColor(R.color.colorWhite));
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
        if (buttonView == connectionFiltersBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, connectionFiltersBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == connectionFiltersBinding.checkBoxBlockedUsers) {
            isChecked(Constants.BLOCKED_USERS, connectionFiltersBinding.checkBoxBlockedUsers.isChecked());
        } else if (buttonView == connectionFiltersBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, connectionFiltersBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == connectionFiltersBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, connectionFiltersBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == connectionFiltersBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, connectionFiltersBinding.checkBoxVerifiedProfile.isChecked());
        } else if (buttonView == connectionFiltersBinding.checkBoxLNQUsersOnly) {
            isChecked(Constants.LNQ_USER_ONLY, connectionFiltersBinding.checkBoxLNQUsersOnly.isChecked());
        }
    }
}