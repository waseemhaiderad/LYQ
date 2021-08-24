package lnq.com.lnq.fragments.connections.export_contacts;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentExportUsersBinding;
import lnq.com.lnq.model.event_bus_models.EventBusExportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;


public class FragmentExportUsers extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentExportUsersBinding exportUsersBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentExportUsers() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        exportUsersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_export_users, container, false);
        return exportUsersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(exportUsersBinding.textViewExportUsersHeading);
        fontUtils.setTextViewRegularFont(exportUsersBinding.textViewExportUsersDescription);
        fontUtils.setTextViewMedium(exportUsersBinding.clearTextViewExport);
        fontUtils.setButtonMedium(exportUsersBinding.buttonCancel);

        exportUsersBinding.imageViewBack.setOnClickListener(this);
        exportUsersBinding.buttonCancel.setOnClickListener(this);
        exportUsersBinding.clearTextViewExport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
            case R.id.buttonCancel:
                getActivity().onBackPressed();
                break;
            case R.id.clearTextViewExport:
                EventBus.getDefault().post(new EventBusExportUsers());
                EventBus.getDefault().post(new EventBusUserSession("export_contacts"));
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME,true,null);
                getActivity().onBackPressed();
                break;
        }
    }

}
