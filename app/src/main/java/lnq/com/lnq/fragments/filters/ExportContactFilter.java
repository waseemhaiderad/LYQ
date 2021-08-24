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
import lnq.com.lnq.databinding.FragmentExportFiltersBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;

public class ExportContactFilter extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields...
    FragmentExportFiltersBinding exportFiltersBinding;

    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();
    private String sortType;

    //    Font fields....
    private FontUtils fontUtils;

    public ExportContactFilter() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        exportFiltersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_export_filters, container, false);
        return exportFiltersBinding.getRoot();
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
                    changeSelection(exportFiltersBinding.checkBoxFavourites);
                    userFilter.add(Constants.FAVORITES);
                }
                if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                    changeSelection(exportFiltersBinding.checkBoxVerifiedProfile);
                    userFilter.add(Constants.VERIFIED_PROFILE);
                }
                if (userFilters.contains(Constants.PENDING_LNQS)) {
                    changeSelection(exportFiltersBinding.checkBoxPendingLNQs);
                    userFilter.add(Constants.PENDING_LNQS);
                }
                if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                    changeSelection(exportFiltersBinding.checkBoxOutstandingTasks);
                    userFilter.add(Constants.OUTSTANDING_TASKS);
                }
                if (userFilters.contains(Constants.LNQ_USER_ONLY)) {
                    changeSelection(exportFiltersBinding.checkBoxLNQUsersOnly);
                    userFilter.add(Constants.LNQ_USER_ONLY);
                }
                if (userFilters.contains(Constants.BLOCKED_USERS)) {
                    changeSelection(exportFiltersBinding.checkBoxBlockedUsers);
                    userFilter.add(Constants.BLOCKED_USERS);
                }
            }
        }
        String user_sorting = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL);
        if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_ALPHABETICAL)) {
            setSortBySelection(exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnRecentLNQs, exportFiltersBinding.mBtnRecentlyViewed);
        } else if (user_sorting.equalsIgnoreCase(EndpointKeys.SORT_DISTANC)) {
            setSortBySelection(exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnRecentLNQs, exportFiltersBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTLNQ)) {
            setSortBySelection(exportFiltersBinding.mBtnRecentLNQs, exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnRecentlyViewed);
        } else if (user_sorting.contains(EndpointKeys.SORT_RECENTVIEWED)) {
            setSortBySelection(exportFiltersBinding.mBtnRecentlyViewed, exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnRecentLNQs);
        }

        exportFiltersBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
        exportFiltersBinding.checkBoxBlockedUsers.setOnCheckedChangeListener(this);
        exportFiltersBinding.checkBoxLNQUsersOnly.setOnCheckedChangeListener(this);
        exportFiltersBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
        exportFiltersBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
        exportFiltersBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

        exportFiltersBinding.imageViewBack.setOnClickListener(this);
        exportFiltersBinding.textViewClearAll.setOnClickListener(this);
        exportFiltersBinding.mBtnAlphabetical.setOnClickListener(this);
        exportFiltersBinding.mBtnDistance.setOnClickListener(this);
        exportFiltersBinding.mBtnRecentLNQs.setOnClickListener(this);
        exportFiltersBinding.mBtnApply.setOnClickListener(this);
        exportFiltersBinding.mBtnRecentlyViewed.setOnClickListener(this);
        exportFiltersBinding.mTvFavorites.setOnClickListener(this);
        exportFiltersBinding.mTvVerifiedProfiles.setOnClickListener(this);
        exportFiltersBinding.mTvPendingLnq.setOnClickListener(this);
        exportFiltersBinding.mTvOutstandingTasks.setOnClickListener(this);
        exportFiltersBinding.mTvLnqUsersOnly.setOnClickListener(this);
        exportFiltersBinding.mTvBlockedUsers.setOnClickListener(this);
        exportFiltersBinding.viewCloseFilter.setOnClickListener(this);
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(exportFiltersBinding.textViewSortBy);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.textViewClearAll);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mTvShowOnly);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mTvFavorites);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mTvVerifiedProfiles);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mTvPendingLnq);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mTvOutstandingTasks);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mTvLnqUsersOnly);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mTvBlockedUsers);
        fontUtils.setButtonRegularFont(exportFiltersBinding.mBtnAlphabetical);
        fontUtils.setButtonRegularFont(exportFiltersBinding.mBtnDistance);
        fontUtils.setButtonRegularFont(exportFiltersBinding.mBtnRecentLNQs);
        fontUtils.setButtonRegularFont(exportFiltersBinding.mBtnRecentlyViewed);
        fontUtils.setTextViewRegularFont(exportFiltersBinding.mBtnApply);
    }

    private void setSortBySelection(Button buttonSelected, Button... buttonDeselected) {
        buttonSelected.setBackground(getResources().getDrawable(R.drawable.bg_verification_white_btn));
        buttonSelected.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
        for (Button button : buttonDeselected) {
            button.setBackground(getResources().getDrawable(R.drawable.bg_login_emailreset_btn));
            button.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvFavorites:
                exportFiltersBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.mTvOutstandingTasks:
                exportFiltersBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.mTvPendingLnq:
                exportFiltersBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.mTvVerifiedProfiles:
                exportFiltersBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.mTvLnqUsersOnly:
                exportFiltersBinding.checkBoxLNQUsersOnly.setChecked(!userFilter.contains(Constants.LNQ_USER_ONLY));
                break;
            case R.id.mTvBlockedUsers:
                exportFiltersBinding.checkBoxBlockedUsers.setChecked(!userFilter.contains(Constants.BLOCKED_USERS));
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
                EventBus.getDefault().post(new EventBusUserSession("export_filter"));
                EventBus.getDefault().post(new EventBusUpdateFilters());
                getActivity().onBackPressed();
                break;
            case R.id.mBtnAlphabetical:
                sortType = EndpointKeys.SORT_ALPHABETICAL;
                setSortBySelection(exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnRecentLNQs, exportFiltersBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnDistance:
                sortType = EndpointKeys.SORT_DISTANC;
                setSortBySelection(exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnRecentLNQs, exportFiltersBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentLNQs:
                sortType = EndpointKeys.SORT_RECENTLNQ;
                setSortBySelection(exportFiltersBinding.mBtnRecentLNQs, exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnRecentlyViewed);
                break;
            case R.id.mBtnRecentlyViewed:
                sortType = EndpointKeys.SORT_RECENTVIEWED;
                setSortBySelection(exportFiltersBinding.mBtnRecentlyViewed, exportFiltersBinding.mBtnAlphabetical, exportFiltersBinding.mBtnDistance, exportFiltersBinding.mBtnRecentLNQs);
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SORTING, EndpointKeys.SORT_ALPHABETICAL).apply();
                exportFiltersBinding.checkBoxFavourites.setChecked(false);
                exportFiltersBinding.checkBoxBlockedUsers.setChecked(false);
                exportFiltersBinding.checkBoxLNQUsersOnly.setChecked(false);
                exportFiltersBinding.checkBoxOutstandingTasks.setChecked(false);
                exportFiltersBinding.checkBoxPendingLNQs.setChecked(false);
                exportFiltersBinding.checkBoxVerifiedProfile.setChecked(false);
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
        if (buttonView == exportFiltersBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, exportFiltersBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == exportFiltersBinding.checkBoxBlockedUsers) {
            isChecked(Constants.BLOCKED_USERS, exportFiltersBinding.checkBoxBlockedUsers.isChecked());
        } else if (buttonView == exportFiltersBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, exportFiltersBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == exportFiltersBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, exportFiltersBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == exportFiltersBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, exportFiltersBinding.checkBoxVerifiedProfile.isChecked());
        } else if (buttonView == exportFiltersBinding.checkBoxLNQUsersOnly) {
            isChecked(Constants.LNQ_USER_ONLY, exportFiltersBinding.checkBoxLNQUsersOnly.isChecked());
        }
    }
}