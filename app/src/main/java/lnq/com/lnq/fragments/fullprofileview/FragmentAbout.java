package lnq.com.lnq.fragments.fullprofileview;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentAboutBinding;
import lnq.com.lnq.model.event_bus_models.EventBusProfileSubPageClicked;
import lnq.com.lnq.model.event_bus_models.EventBusAboutUserData;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;

public class FragmentAbout extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentAboutBinding aboutBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentAbout() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aboutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        return aboutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            boolean isLnqUser = getArguments().getBoolean(Constants.IS_LNQ_USER, false);
            if (!isLnqUser) {
                aboutBinding.textViewTags.setVisibility(View.INVISIBLE);
                aboutBinding.textViewBio.setVisibility(View.INVISIBLE);
            }
        }

//        Setting custom font....
        setCustomFont();

    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(aboutBinding.textViewBio);
        fontUtils.setTextViewRegularFont(aboutBinding.textViewBioDetail);
        fontUtils.setTextViewRegularFont(aboutBinding.textViewTags);
        fontUtils.setTextViewRegularFont(aboutBinding.textViewTagDetail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetUserTags(EventBusAboutUserData eventBusUserTags) {
        if (!eventBusUserTags.getUserTags().isEmpty()) {
            List<String> tagsList = new ArrayList<>(Arrays.asList(eventBusUserTags.getUserTags().split(",")));
            SortingUtils.sortTagsList(tagsList);
            String tags = tagsList.toString().replaceAll(",", " ").replaceAll("[\\[.\\]]", "");
            aboutBinding.textViewTagDetail.setText(tags);
        } else {
            aboutBinding.textViewTagDetail.setText(eventBusUserTags.getfName() + " has not set tags yet.");
        }
        if (eventBusUserTags.getUserBio().isEmpty()) {
            aboutBinding.textViewBioDetail.setText(eventBusUserTags.getfName() + " has not set bio yet.");
        } else {
            aboutBinding.textViewBioDetail.setText(eventBusUserTags.getUserBio());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}