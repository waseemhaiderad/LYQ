package lnq.com.lnq.fragments.registeration;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentSignUpTermsConditionsBinding;
import lnq.com.lnq.utils.FontUtils;

public class FragmentSignUpTermsConditions extends Fragment {

    //    Android fields....
    private FragmentSignUpTermsConditionsBinding termsConditionBinding;
    private TermsConditionClickHandler clickHandler;

    //    Font fields....
    private FontUtils fontUtils;
    String type;

    public FragmentSignUpTermsConditions() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        termsConditionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_terms_conditions, container, false);
        return termsConditionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Setting custom font....
        setCustomFont();

        if (getArguments() != null){
            type = getArguments().getString("privacy");
        }
        if (type != null && !type.isEmpty()){
            if (type.equalsIgnoreCase("privacyText")){
                fnChangeButtonDrawable(termsConditionBinding.buttonPrivacy, termsConditionBinding.buttonTerms);
                termsConditionBinding.textViewTermsHeading.setText(getResources().getString(R.string.privacy));
            }
            else {
                fnChangeButtonDrawable(termsConditionBinding.buttonTerms, termsConditionBinding.buttonPrivacy);
            }
        }else {
            fnChangeButtonDrawable(termsConditionBinding.buttonTerms, termsConditionBinding.buttonPrivacy);
        }



//        Setting click handler for data binding....
        clickHandler = new TermsConditionClickHandler();
        termsConditionBinding.setClickHandler(clickHandler);
    }

    //    Method to set custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewMedium(termsConditionBinding.textViewTermsConditionHeading);
        fontUtils.setTextViewRegularFont(termsConditionBinding.textViewTermsHeading);
        fontUtils.setButtonMedium(termsConditionBinding.buttonPrivacy);
        fontUtils.setButtonMedium(termsConditionBinding.buttonTerms);
    }

    private void fnChangeButtonDrawable(Button buttonSelected, Button buttonDeselected) {
        buttonSelected.setSelected(true);
        buttonDeselected.setSelected(false);
        buttonDeselected.setBackgroundResource(0);
        buttonSelected.setBackgroundResource(R.mipmap.btn_blue_newtheme);
        buttonSelected.setTextColor(getResources().getColor(R.color.colorWhite));
        buttonDeselected.setTextColor(getResources().getColor(R.color.colorBlueNewTheme));
    }

    public class TermsConditionClickHandler {

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void onTermsClick(View view) {
            fnChangeButtonDrawable(termsConditionBinding.buttonTerms, termsConditionBinding.buttonPrivacy);
            termsConditionBinding.textViewTermsHeading.setText(getResources().getString(R.string.terms_condition));
        }

        public void onPrivacyClick(View view) {
            fnChangeButtonDrawable(termsConditionBinding.buttonPrivacy, termsConditionBinding.buttonTerms);
            termsConditionBinding.textViewTermsHeading.setText(getResources().getString(R.string.privacy));
        }

    }

}