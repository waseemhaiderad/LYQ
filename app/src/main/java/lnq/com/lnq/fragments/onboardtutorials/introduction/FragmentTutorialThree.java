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
import lnq.com.lnq.databinding.FragmentTutorialThreeBinding;
import lnq.com.lnq.utils.FontUtils;

public class FragmentTutorialThree extends Fragment {

    //    Android fields....
    private FragmentTutorialThreeBinding tutorialThreeBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentTutorialThree() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tutorialThreeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial_three, container, false);
        return tutorialThreeBinding.getRoot();
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
        fontUtils.setTextViewRegularFont(tutorialThreeBinding.textViewTutorialThreeHeading);
        fontUtils.setTextViewRegularFont(tutorialThreeBinding.textViewTutorialTwoDescription);
    }

}