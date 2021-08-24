package lnq.com.lnq.fragments.setting.account.accountsetting;

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
import lnq.com.lnq.databinding.FragmentDeleteAccountPopUpBinding;
import lnq.com.lnq.model.event_bus_models.EventBusDeleteAccount;
import lnq.com.lnq.model.event_bus_models.EventBusFreezeAccount;

public class FragmentDeleteAccountPopUp extends Fragment {

    private FragmentDeleteAccountPopUpBinding binding;
    private DeleteAccountPopUp yourDataPopUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_account_pop_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init(){
        yourDataPopUp = new DeleteAccountPopUp();
        binding.setClickHandler(yourDataPopUp);
    }

    public class DeleteAccountPopUp {

        public void onOkayCLick(View view){
            EventBus.getDefault().post(new EventBusDeleteAccount());
            getActivity().onBackPressed();
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }
    }
}