package lnq.com.lnq.fragments.setting.account.accountsetting;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentEditPhoneNumberBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.application.LnqApplication;


public class FragmentEditPhoneNumber extends Fragment implements View.OnClickListener {

    private FragmentEditPhoneNumberBinding mBind;

    public FragmentEditPhoneNumber() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_phone_number, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.mBtnNeverMind.setOnClickListener(this);
        mBind.mBtnUpdatePhone.setOnClickListener(this);

        String phoneNumber = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PHONE, "");
        mBind.mTvPhoneNumber.setText(phoneNumber);
    }

        @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnNeverMind:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnUpdatePhone:
                ((MainActivity) getActivity()).fnLoadFragAdd("ENTER EDIT NUMBER", true, null);
                break;

        }
    }
}