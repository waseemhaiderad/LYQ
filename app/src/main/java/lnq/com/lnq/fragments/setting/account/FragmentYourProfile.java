package lnq.com.lnq.fragments.setting.account;


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
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentYourProfileBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.application.LnqApplication;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class FragmentYourProfile extends Fragment implements View.OnClickListener {


    private FragmentYourProfileBinding mBind;

    public FragmentYourProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_your_profile, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.imageViewBack.setOnClickListener(this);
        mBind.mTvViewOrEdit.setOnClickListener(this);
        mBind.mImgLeftSign.setOnClickListener(this);
        mBind.mTvAccountHeading1.setOnClickListener(this);
        mBind.mTvProfileCreated.setText(String.format("Profile Created %s", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.PROFILE_CREATED_DATE, "")));
        mBind.mTvCountsNumber.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.CONNECTION_COUNT, "0"));

        OverScrollDecoratorHelper.setUpOverScroll(mBind.nestedScrollView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mTvAccountHeading1:
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.mTvViewOrEdit:
            case R.id.mImgLeftSign:
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOOKING_FOR,true,null);
        }

    }
}
