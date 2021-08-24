package lnq.com.lnq.fragments.registeration;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentWelcomeLnqBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;

public class FragmentWelcomeLnq extends Fragment {

    //    Android fields....
    private FragmentWelcomeLnqBinding welcomeLnqBinding;
    private WelcomeLnqClickHandler clickHandler;

    //    Font Fields
    FontUtils fontUtils;

    public FragmentWelcomeLnq() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        welcomeLnqBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome_lnq, container, false);
        return welcomeLnqBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewMedium(welcomeLnqBinding.textViewWelcomeLnqHeading);
        fontUtils.setTextViewRegularFont(welcomeLnqBinding.textViewWelomeLnqDes);
        fontUtils.setTextViewMedium(welcomeLnqBinding.clearTextViewVerifyProfile);
        fontUtils.setButtonMedium(welcomeLnqBinding.buttonSkip);
    }

    private void init() {
        clickHandler = new WelcomeLnqClickHandler();
        welcomeLnqBinding.setClickHandler(clickHandler);

        ValidUtils.textViewGradientColor(welcomeLnqBinding.textViewWelcomeLnqHeading);
        ValidUtils.textViewGradientColor(welcomeLnqBinding.textViewWelomeLnqDes);
        ValidUtils.buttonGradientColor(welcomeLnqBinding.buttonSkip);
    }

    public class WelcomeLnqClickHandler {

        public void onSkipClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(EndpointKeys.ON_BOARD_TUTORIAL_TYPE, EndpointKeys.GUID_LINE);
            ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ((MainActivity) getActivity()).fnLoadFragReplaceCommitAllowing(Constants.TUTORIAL_BASE, false, bundle);
        }

        public void onVerifyProfileClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_VERIFY_OPTIONS, true, null);
        }

    }
}
