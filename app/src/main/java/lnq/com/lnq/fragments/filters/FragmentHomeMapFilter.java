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
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentHomeMapFilterBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;

public class FragmentHomeMapFilter extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentHomeMapFilterBinding mapFilterBinding;

    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentHomeMapFilter() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapFilterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_map_filter, container, false);
        return mapFilterBinding.getRoot();
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
                        changeSelection(mapFilterBinding.checkBoxFavourites);
                        userFilter.add(Constants.FAVORITES);
                    }
                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                        changeSelection(mapFilterBinding.checkBoxVerifiedProfile);
                        userFilter.add(Constants.VERIFIED_PROFILE);
                    }
                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                        changeSelection(mapFilterBinding.checkBoxPendingLNQs);
                        userFilter.add(Constants.PENDING_LNQS);
                    }
                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                        changeSelection(mapFilterBinding.checkBoxOutstandingTasks);
                        userFilter.add(Constants.OUTSTANDING_TASKS);
                    }
                }
            }

            mapFilterBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
            mapFilterBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
            mapFilterBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
            mapFilterBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

            mapFilterBinding.imageViewBack.setOnClickListener(this);
            mapFilterBinding.mTvClearAll.setOnClickListener(this);
            mapFilterBinding.clearTextViewApply.setOnClickListener(this);
            mapFilterBinding.textViewFavorites.setOnClickListener(this);
            mapFilterBinding.textViewVerifiedProfiles.setOnClickListener(this);
            mapFilterBinding.textViewPendingLnq.setOnClickListener(this);
            mapFilterBinding.textViewOutstandingTasks.setOnClickListener(this);
            mapFilterBinding.viewCloseFilter.setOnClickListener(this);

        }
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(mapFilterBinding.textViewSortByHeading);
        fontUtils.setTextViewRegularFont(mapFilterBinding.clearTextViewDistance);
        fontUtils.setTextViewRegularFont(mapFilterBinding.textViewShowOnly);
        fontUtils.setTextViewRegularFont(mapFilterBinding.textViewFavorites);
        fontUtils.setTextViewRegularFont(mapFilterBinding.textViewVerifiedProfiles);
        fontUtils.setTextViewRegularFont(mapFilterBinding.textViewPendingLnq);
        fontUtils.setTextViewRegularFont(mapFilterBinding.textViewOutstandingTasks);
        fontUtils.setTextViewRegularFont(mapFilterBinding.clearTextViewApply);
    }

    private void changeSelection(AppCompatCheckBox compatCheckBox) {
        compatCheckBox.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                mapFilterBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                mapFilterBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                mapFilterBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                mapFilterBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
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
                EventBus.getDefault().post(new EventBusUserSession("map_filter"));
                getActivity().onBackPressed();
                break;
            case R.id.mTvClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                mapFilterBinding.checkBoxFavourites.setChecked(false);
                mapFilterBinding.checkBoxOutstandingTasks.setChecked(false);
                mapFilterBinding.checkBoxPendingLNQs.setChecked(false);
                mapFilterBinding.checkBoxVerifiedProfile.setChecked(false);
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
        if (buttonView == mapFilterBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, mapFilterBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == mapFilterBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, mapFilterBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == mapFilterBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, mapFilterBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == mapFilterBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, mapFilterBinding.checkBoxVerifiedProfile.isChecked());
        }
    }
}