package lnq.com.lnq.fragments.connections.import_contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentImportUserPopUpBinding;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentFreezeAccountPopUp;
import lnq.com.lnq.model.event_bus_models.EventBusFreezeAccount;
import lnq.com.lnq.model.event_bus_models.EventBusImportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;

public class FragmentImportUserPopUp extends Fragment {

    private FragmentImportUserPopUpBinding binding;
    private ImprotUserPopUp yourDataPopUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_import_user_pop_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init(){
        yourDataPopUp = new ImprotUserPopUp();
        binding.setClickHandler(yourDataPopUp);
    }

    public class ImprotUserPopUp {

        public void onOkayCLick(View view){
            EventBus.getDefault().post(new EventBusImportUsers());
            EventBus.getDefault().post(new EventBusUserSession("contact_imported"));
            getActivity().onBackPressed();
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }
    }
}