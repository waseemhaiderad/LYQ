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
import lnq.com.lnq.databinding.FragmentFreezeAccountPopUpBinding;
import lnq.com.lnq.databinding.FragmentRequestYourDataPopUpBinding;
import lnq.com.lnq.model.event_bus_models.EventBusFreezeAccount;
import lnq.com.lnq.model.event_bus_models.EventBusRequestYourData;

public class FragmentFreezeAccountPopUp extends Fragment {

    private FragmentFreezeAccountPopUpBinding binding;
    private FreezeAccountPopUp yourDataPopUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_freeze_account_pop_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init(){
        yourDataPopUp = new FreezeAccountPopUp();
        binding.setClickHandler(yourDataPopUp);
    }

    public class FreezeAccountPopUp {

        public void onOkayCLick(View view){
            EventBus.getDefault().post(new EventBusFreezeAccount());
            getActivity().onBackPressed();
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }
    }
}