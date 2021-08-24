package lnq.com.lnq.fragments.profile.editprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentEditTagsPopUpBinding;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentDeleteAccountPopUp;
import lnq.com.lnq.model.event_bus_models.EventBusDeleteAccount;
import lnq.com.lnq.model.event_bus_models.EventBusEditTagsPopUp;

public class FragmentEditTagsPopUp extends Fragment {

    private FragmentEditTagsPopUpBinding binding;
    private EditTagsPopUp yourDataPopUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_tags_pop_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
        init();
    }

    public void init() {
        yourDataPopUp = new EditTagsPopUp();
        binding.setClickHandler(yourDataPopUp);
    }

    public class EditTagsPopUp {

        public void onOkayCLick(View view) {
            getActivity().onBackPressed();
            EventBus.getDefault().post(new EventBusEditTagsPopUp("okay"));
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
            EventBus.getDefault().post(new EventBusEditTagsPopUp("cancel"));
        }
    }
}