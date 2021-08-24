package lnq.com.lnq.fragments.connections.import_contacts;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentImportUsersBinding;
import lnq.com.lnq.model.event_bus_models.EventBusImportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;


public class FragmentImportUsers extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentImportUsersBinding importUsersBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentImportUsers() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        importUsersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_import_users, container, false);
        return importUsersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewMedium(importUsersBinding.clearTextViewImport);
        fontUtils.setTextViewRegularFont(importUsersBinding.textViewImportUsersHeading);
        fontUtils.setTextViewRegularFont(importUsersBinding.textViewImportUsersDescription);
        fontUtils.setButtonMedium(importUsersBinding.buttonCancel);

        importUsersBinding.clearTextViewImport.setOnClickListener(this);
        importUsersBinding.buttonCancel.setOnClickListener(this);
        importUsersBinding.imageViewBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
            case R.id.buttonCancel:
                getActivity().onBackPressed();
                break;
            case R.id.clearTextViewImport:
                EventBus.getDefault().post(new EventBusImportUsers());
                EventBus.getDefault().post(new EventBusUserSession("contact_imported"));
                getActivity().onBackPressed();
                break;
        }
    }

}
