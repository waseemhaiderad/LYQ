package lnq.com.lnq.fragments.fullprofileview;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.FullProfileViewPagerAdapter;
import lnq.com.lnq.databinding.FragmentUnLnqUserProfileViewBinding;
import lnq.com.lnq.model.event_bus_models.EventBusProfileSubPageClicked;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;

public class FragmentUnLnqUserProfileView extends Fragment implements View.OnClickListener {

    private FragmentUnLnqUserProfileViewBinding mBind;

    public FragmentUnLnqUserProfileView() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_un_lnq_user_profile_view, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            mBind.mTvContactsNameHeading.setText(getArguments().getString("user_name", ""));
        }
        EventBus.getDefault().register(this);
        final FullProfileViewPagerAdapter pagerAdapter = new FullProfileViewPagerAdapter(getChildFragmentManager(), false, "");
        mBind.viewPagify.setAdapter(pagerAdapter);
        mBind.viewPagify.setCurrentItemPosition(1);
//        mBind.viewPagify.setOnItemClickListener(this);
//        mBind.viewPagify.addOnPageChangeListener(this);
        mBind.mImgBack.setOnClickListener(this);
        mBind.mImgLookingFor.setOnClickListener(this);
        mBind.mImgSetting.setOnClickListener(this);
        mBind.mImgVisible.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSubPageClicked(EventBusProfileSubPageClicked mObj) {
        mBind.viewPagify.setCurrentItemPosition(mObj.getPosition());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImgBack:
                getActivity().onBackPressed();
                break;
            case R.id.mImgLookingFor:
                ((MainActivity) getActivity()).mFScreenName = "LOOKING FOR";
                ((MainActivity) getActivity()).fnLoadFragAdd("LOOKING FOR", true, null);
                EventBus.getDefault().post(new EventBusUserSession("status_view"));
                break;
            case R.id.mImgSetting:
                ((MainActivity) getActivity()).mFScreenName = "SETTING";
                ((MainActivity) getActivity()).fnLoadFragAdd("SETTING", true, null);
                EventBus.getDefault().post(new EventBusUserSession("setting_view"));
                break;
            case R.id.mImgVisible:
                ((MainActivity) getActivity()).mFScreenName = "VISIBLE";
                ((MainActivity) getActivity()).fnLoadFragAdd("VISIBLE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("visibility_view"));
                break;
        }
    }
}
