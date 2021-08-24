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
import android.widget.CompoundButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentActivityFiltersBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;

public class FragmentActivityFilters extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentActivityFiltersBinding filtersBinding;

    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentActivityFilters() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        filtersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activity_filters, container, false);
        return filtersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getActivity() != null) {

//            Setting custom font....
            setCustomFont();

            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                if (userFilters != null) {
                    if (userFilters.contains(Constants.FAVORITES)) {
                        changeSelection(filtersBinding.checkBoxFavourites);
                        userFilter.add(Constants.FAVORITES);
                    }
                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                        changeSelection(filtersBinding.checkBoxVerifiedProfile);
                        userFilter.add(Constants.VERIFIED_PROFILE);
                    }
                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                        changeSelection(filtersBinding.checkBoxPendingLNQs);
                        userFilter.add(Constants.PENDING_LNQS);
                    }
                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                        changeSelection(filtersBinding.checkBoxOutstandingTasks);
                        userFilter.add(Constants.OUTSTANDING_TASKS);
                    }
                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                        changeSelection(filtersBinding.checkBoxBlockedUsers);
                        userFilter.add(Constants.BLOCKED_USERS);
                    }
                }
            }
        }

        filtersBinding.imageViewBack.setOnClickListener(this);
        filtersBinding.textViewClearAll.setOnClickListener(this);
        filtersBinding.clearTextViewApply.setOnClickListener(this);
        filtersBinding.textViewFavorites.setOnClickListener(this);
        filtersBinding.textViewVerifiedProfiles.setOnClickListener(this);
        filtersBinding.textViewPendingLnq.setOnClickListener(this);
        filtersBinding.textViewOutstandingTasks.setOnClickListener(this);
        filtersBinding.textViewBlockedUsers.setOnClickListener(this);
        filtersBinding.viewCloseFilter.setOnClickListener(this);

        filtersBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
        filtersBinding.checkBoxBlockedUsers.setOnCheckedChangeListener(this);
        filtersBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
        filtersBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
        filtersBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(filtersBinding.textViewSortBy);
        fontUtils.setTextViewRegularFont(filtersBinding.clearTextViewMostRecent);
        fontUtils.setTextViewRegularFont(filtersBinding.textViewShowOnly);
        fontUtils.setTextViewRegularFont(filtersBinding.textViewFavorites);
        fontUtils.setTextViewRegularFont(filtersBinding.textViewVerifiedProfiles);
        fontUtils.setTextViewRegularFont(filtersBinding.textViewPendingLnq);
        fontUtils.setTextViewRegularFont(filtersBinding.textViewOutstandingTasks);
        fontUtils.setTextViewRegularFont(filtersBinding.textViewBlockedUsers);
        fontUtils.setTextViewRegularFont(filtersBinding.clearTextViewApply);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                filtersBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                filtersBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                filtersBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                filtersBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.textViewBlockedUsers:
                filtersBinding.checkBoxBlockedUsers.setChecked(!userFilter.contains(Constants.BLOCKED_USERS));
                break;
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.viewCloseFilter:
                getActivity().onBackPressed();
                break;
            case R.id.clearTextViewApply:
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, userFilter.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "")).apply();
                EventBus.getDefault().post(new EventBusUpdateFilters());
                EventBus.getDefault().post(new EventBusUserSession("activity_filter"));
                getActivity().onBackPressed();
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                filtersBinding.checkBoxBlockedUsers.setChecked(false);
                filtersBinding.checkBoxVerifiedProfile.setChecked(false);
                filtersBinding.checkBoxPendingLNQs.setChecked(false);
                filtersBinding.checkBoxOutstandingTasks.setChecked(false);
                filtersBinding.checkBoxFavourites.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
        }
    }

    private void changeSelection(AppCompatCheckBox checkBox) {
        checkBox.setChecked(true);
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
        if (buttonView == filtersBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, filtersBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == filtersBinding.checkBoxBlockedUsers) {
            isChecked(Constants.BLOCKED_USERS, filtersBinding.checkBoxBlockedUsers.isChecked());
        } else if (buttonView == filtersBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, filtersBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == filtersBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, filtersBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == filtersBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, filtersBinding.checkBoxVerifiedProfile.isChecked());
        }
    }

}