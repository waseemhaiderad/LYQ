package lnq.com.lnq.fragments.setting.account.blockusers;


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
import lnq.com.lnq.databinding.FragmentBlockedUsersSettingBinding;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBlockedUsersSetting extends Fragment implements View.OnClickListener {


    private FragmentBlockedUsersSettingBinding mBind;

    public FragmentBlockedUsersSetting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_blocked_users_setting, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.imageViewBack.setOnClickListener(this);
        mBind.mTvSeeBlockedUsers.setOnClickListener(this);
        mBind.mTvAccountHeading1.setOnClickListener(this);
        OverScrollDecoratorHelper.setUpOverScroll(mBind.nestedScrollView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvSeeBlockedUsers:
            case R.id.mImgLeftSign:
                ((MainActivity) getActivity()).fnLoadFragAdd("BLOCKED USERS", true, null);
                break;
            case R.id.mTvAccountHeading1:
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
        }
    }
}

