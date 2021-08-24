package lnq.com.lnq.fragments.onboardtutorials.guidlines;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.TutorialPagerAdapter;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentGuidlineTutorialTwoBinding;
import lnq.com.lnq.utils.ValidUtils;

public class FragmentGuidlineTutorialTwo extends Fragment implements View.OnClickListener {

    private FragmentGuidlineTutorialTwoBinding mBind2;

    public FragmentGuidlineTutorialTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBind2 = DataBindingUtil.inflate(inflater, R.layout.fragment_guidline_tutorial_two, container, false);
        return mBind2.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind2.mBtnOk2.setOnClickListener(this);
        ValidUtils.buttonGradientColor(mBind2.mBtnOk2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnOk2:
                EventBus.getDefault().post(new EventBusGuidlineTutorial2(2));
        }
    }
}