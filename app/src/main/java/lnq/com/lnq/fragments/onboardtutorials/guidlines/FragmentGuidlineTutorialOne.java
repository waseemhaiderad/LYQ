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
import lnq.com.lnq.databinding.FragmentGuidlineTutorialOneBinding;

public class FragmentGuidlineTutorialOne extends Fragment implements View.OnClickListener {

    private FragmentGuidlineTutorialOneBinding mBind1;

    public FragmentGuidlineTutorialOne() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBind1 = DataBindingUtil.inflate(inflater, R.layout.fragment_guidline_tutorial_one, container, false);
        return mBind1.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind1.mBtnOk1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnOk1:
                ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((MainActivity) getActivity()).fnLoadFragReplace("", false, null);
                EventBus.getDefault().post(new EventBusGuidlineTutorial2(1));
                break;
        }
    }
}
