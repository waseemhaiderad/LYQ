package lnq.com.lnq.fragments.fullprofileview;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
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
import java.util.zip.Inflater;

import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentContactListBinding;
import lnq.com.lnq.model.event_bus_models.EventBusContactsListUserData;
import lnq.com.lnq.model.event_bus_models.EventBusProfileSubPageClicked;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;


public class FragmentContactList extends Fragment {

    private FragmentContactListBinding contactListBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentContactList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_list, container, false);
        return contactListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Registering event bus for different triggers....
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            boolean isLnqUser = getArguments().getBoolean(Constants.IS_LNQ_USER, false);
            if (!isLnqUser) {
                contactListBinding.rootAlldata.setVisibility(View.INVISIBLE);
            }
        }
//        Setting custom font....
        setCustomFont();
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(contactListBinding.textViewPhoneDetail);
        fontUtils.setTextViewRegularFont(contactListBinding.textViewEmailDetail);
        fontUtils.setTextViewRegularFont(contactListBinding.textViewSecondryPhoneDetail);
        fontUtils.setTextViewRegularFont(contactListBinding.textViewSecondryEmailDetail);
        fontUtils.setTextViewRegularFont(contactListBinding.textViewSocialMediaDetail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetUserContactList(EventBusContactsListUserData eventBusContactsListUserData) {
        contactListBinding.textViewPhoneDetail.setText(eventBusContactsListUserData.getPhone());
        contactListBinding.textViewEmailDetail.setText(eventBusContactsListUserData.getEmail());

        if (!eventBusContactsListUserData.getSecondryPhone().isEmpty()) {
            List<String> phonelist = new ArrayList<>(Arrays.asList(eventBusContactsListUserData.getSecondryPhone().split(",")));
            StringBuilder stringBuilderPhone = new StringBuilder();
            for (String phone : phonelist) {
                stringBuilderPhone.append(phone).append("\n");
            }
            contactListBinding.textViewSecondryPhoneDetail.setText(stringBuilderPhone.toString());
        } else {
            contactListBinding.textViewSecondryPhoneDetail.setText(eventBusContactsListUserData.getfName() + " has not set secondary Phone # yet.");
        }
        if (!eventBusContactsListUserData.getSecondryEmail().isEmpty()) {
            List<String> emailList = new ArrayList<>(Arrays.asList(eventBusContactsListUserData.getSecondryEmail().split(",")));
            StringBuilder stringBuilderEmail = new StringBuilder();
            for (String email : emailList) {
                stringBuilderEmail.append(email).append("\n");
            }
            contactListBinding.textViewSecondryEmailDetail.setText(stringBuilderEmail.toString());
        } else {
            contactListBinding.textViewSecondryEmailDetail.setText(eventBusContactsListUserData.getfName() + " has not set secondary Email yet.");
        }
        if (!eventBusContactsListUserData.getSocialMedia().isEmpty()) {
            List<String> socialList = new ArrayList<>(Arrays.asList(eventBusContactsListUserData.getSocialMedia().split(",")));
            StringBuilder stringBuilderSocial = new StringBuilder();
            for (String social : socialList) {
                stringBuilderSocial.append(social).append("\n");
            }
            contactListBinding.textViewSocialMediaDetail.setText(stringBuilderSocial.toString());
        } else {
            contactListBinding.textViewSocialMediaDetail.setText(eventBusContactsListUserData.getfName() + " has not set Social media links yet.");
        }


    }
}