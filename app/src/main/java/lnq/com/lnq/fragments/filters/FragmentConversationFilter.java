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
import lnq.com.lnq.databinding.FragmentConversationFilterBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;


public class FragmentConversationFilter extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentConversationFilterBinding conversationFilterBinding;

    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentConversationFilter() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        conversationFilterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation_filter, container, false);
        return conversationFilterBinding.getRoot();
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
                        changeSelection(conversationFilterBinding.checkBoxFavourites);
                        userFilter.add(Constants.FAVORITES);
                    }
                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                        changeSelection(conversationFilterBinding.checkBoxVerifiedProfile);
                        userFilter.add(Constants.VERIFIED_PROFILE);
                    }
                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                        changeSelection(conversationFilterBinding.checkBoxPendingLNQs);
                        userFilter.add(Constants.PENDING_LNQS);
                    }
                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                        changeSelection(conversationFilterBinding.checkBoxOutstandingTasks);
                        userFilter.add(Constants.OUTSTANDING_TASKS);
                    }
                }
            }

            conversationFilterBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
            conversationFilterBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
            conversationFilterBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
            conversationFilterBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

            conversationFilterBinding.imageViewBack.setOnClickListener(this);
            conversationFilterBinding.mTvClearAll.setOnClickListener(this);
            conversationFilterBinding.clearTextViewApply.setOnClickListener(this);
            conversationFilterBinding.textViewFavorites.setOnClickListener(this);
            conversationFilterBinding.textViewVerifiedProfiles.setOnClickListener(this);
            conversationFilterBinding.textViewPendingLnq.setOnClickListener(this);
            conversationFilterBinding.textViewOutstandingTasks.setOnClickListener(this);
            conversationFilterBinding.viewCloseFilter.setOnClickListener(this);

        }
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(conversationFilterBinding.textViewSortByHeading);
        fontUtils.setTextViewRegularFont(conversationFilterBinding.clearTextViewDistance);
        fontUtils.setTextViewRegularFont(conversationFilterBinding.textViewShowOnly);
        fontUtils.setTextViewRegularFont(conversationFilterBinding.textViewFavorites);
        fontUtils.setTextViewRegularFont(conversationFilterBinding.textViewVerifiedProfiles);
        fontUtils.setTextViewRegularFont(conversationFilterBinding.textViewPendingLnq);
        fontUtils.setTextViewRegularFont(conversationFilterBinding.textViewOutstandingTasks);
        fontUtils.setTextViewRegularFont(conversationFilterBinding.clearTextViewApply);
    }

    private void changeSelection(AppCompatCheckBox compatCheckBox) {
        compatCheckBox.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                conversationFilterBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                conversationFilterBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                conversationFilterBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                conversationFilterBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
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
                EventBus.getDefault().post(new EventBusUserSession("conversation_filter"));
                getActivity().onBackPressed();
                break;
            case R.id.mTvClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                conversationFilterBinding.checkBoxFavourites.setChecked(false);
                conversationFilterBinding.checkBoxOutstandingTasks.setChecked(false);
                conversationFilterBinding.checkBoxPendingLNQs.setChecked(false);
                conversationFilterBinding.checkBoxVerifiedProfile.setChecked(false);
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
        if (buttonView == conversationFilterBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, conversationFilterBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == conversationFilterBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, conversationFilterBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == conversationFilterBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, conversationFilterBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == conversationFilterBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, conversationFilterBinding.checkBoxVerifiedProfile.isChecked());
        }
    }
}
