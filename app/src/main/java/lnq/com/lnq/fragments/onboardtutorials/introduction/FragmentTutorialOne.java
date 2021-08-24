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
import lnq.com.lnq.databinding.FragmentTutorialOneBinding;
import lnq.com.lnq.utils.FontUtils;

public class FragmentTutorialOne extends Fragment {

    //    Android fields....
    private FragmentTutorialOneBinding tutorialOneBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentTutorialOne() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tutorialOneBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial_one, container, false);
        return tutorialOneBinding.getRoot();
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
        fontUtils.setTextViewRegularFont(tutorialOneBinding.textViewTutorialOneHeading);
        fontUtils.setTextViewRegularFont(tutorialOneBinding.textViewTutorialOneDescription);
    }

}