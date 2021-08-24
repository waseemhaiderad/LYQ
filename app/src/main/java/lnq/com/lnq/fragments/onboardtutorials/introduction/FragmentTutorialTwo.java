package lnq.com.lnq.fragments.onboardtutorials.introduction;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentTutorialTwoBinding;
import lnq.com.lnq.utils.FontUtils;


public class FragmentTutorialTwo extends Fragment {

    //    Android fields....
    private FragmentTutorialTwoBinding tutorialTwoBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentTutorialTwo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tutorialTwoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial_two, container, false);
        return tutorialTwoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Setting custom font....
        setCustomFont();
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(tutorialTwoBinding.textViewTutorialTwoHeading);
        fontUtils.setTextViewRegularFont(tutorialTwoBinding.textViewTutorialTwoDescription);
    }

}
