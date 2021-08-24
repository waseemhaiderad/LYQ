package lnq.com.lnq.fragments.onboardtutorials.guidlines;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentGuidlineTutorialThreeBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGuidlineTutorialThree extends Fragment implements View.OnClickListener {

    private FragmentGuidlineTutorialThreeBinding mBind;

    public FragmentGuidlineTutorialThree() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_guidline_tutorial_three, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.mBtnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnOk:
                ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((MainActivity) getActivity()).fnLoadFragReplace("HOME", false, null);
                break;
        }
    }
}
