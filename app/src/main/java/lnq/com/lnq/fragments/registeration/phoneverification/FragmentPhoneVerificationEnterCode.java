package lnq.com.lnq.fragments.registeration.phoneverification;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentVerificationEnterCodeBinding;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;

public class FragmentPhoneVerificationEnterCode extends Fragment {

    //    Android fields....
    private FragmentVerificationEnterCodeBinding enterCodeBinding;
    private EnterCodeClickListener enterCodeClickListener;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentPhoneVerificationEnterCode() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        enterCodeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_verification_enter_code, container, false);
        return enterCodeBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setCustomFont();
    }

    private void init() {
        enterCodeBinding.editTextCode1.requestFocus();
        ((MainActivity) getActivity()).fnShowKeyboardFrom(enterCodeBinding.editTextCode1);

//        Setting event listener using binding....
        enterCodeClickListener = new EnterCodeClickListener(getActivity());
        enterCodeBinding.setClickListener(enterCodeClickListener);

//        All event listeners....
        EventBus.getDefault().register(this);
        setListenersForTextWatcher();
        ((MainActivity) getActivity()).fnShowKeyboardFrom(enterCodeBinding.editTextCode1);

        if (!enterCodeBinding.editTextCode6.getText().toString().isEmpty()) {

            if (enterCodeBinding.editTextCode1.getText().toString().isEmpty() && enterCodeBinding.editTextCode2.getText().toString().isEmpty() &&
                    enterCodeBinding.editTextCode3.getText().toString().isEmpty() && enterCodeBinding.editTextCode4.getText().toString().isEmpty() &&
                    enterCodeBinding.editTextCode5.getText().toString().isEmpty() && enterCodeBinding.editTextCode6.getText().toString().isEmpty()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.enter_verification_code));
                return;
            }
            ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
            EventBus.getDefault().post(new EventBusPhoneVerification(getArguments().getString("verification_id", ""), enterCodeBinding.editTextCode1.getText().toString() + enterCodeBinding.editTextCode2.getText().toString() + enterCodeBinding.editTextCode3.getText().toString() + enterCodeBinding.editTextCode4.getText().toString() + enterCodeBinding.editTextCode5.getText().toString() + enterCodeBinding.editTextCode6.getText().toString(), "mFverify"));

        }

        ValidUtils.textViewGradientColor(enterCodeBinding.textViewVerifyPhoneNo);
        ValidUtils.textViewGradientColor(enterCodeBinding.textViewEnterDigitCode);
        ValidUtils.buttonGradientColor(enterCodeBinding.buttonResendCode);
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());

        fontUtils.setTextViewBoldFont(enterCodeBinding.textViewVerifyPhoneNo);
        fontUtils.setEditTextRegularFont(enterCodeBinding.editTextCode1);
        fontUtils.setEditTextRegularFont(enterCodeBinding.editTextCode2);
        fontUtils.setEditTextRegularFont(enterCodeBinding.editTextCode3);
        fontUtils.setEditTextRegularFont(enterCodeBinding.editTextCode4);
        fontUtils.setEditTextRegularFont(enterCodeBinding.editTextCode5);
        fontUtils.setEditTextRegularFont(enterCodeBinding.editTextCode6);
        fontUtils.setTextViewRegularFont(enterCodeBinding.textViewEnterDigitCode);
        fontUtils.setTextViewMedium(enterCodeBinding.clearTextViewVerifyCode);
        fontUtils.setTextViewMedium(enterCodeBinding.buttonResendCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ValidUtils.hideKeyboardFromFragment(getActivity(), enterCodeBinding.editTextCode1);
    }

    //    Event bus trigger when verifying code....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void verificationCode(EventBusPhoneVerification mObj) {
        switch (mObj.getmFlag()) {
            case "mFprogress":
//                if (((MainActivity) getActivity()).progressDialog.isShowing()) {
//                    ((MainActivity) getActivity()).progressDialog.dismiss();
//                }
//                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                enterCodeBinding.clearTextViewVerifyCode2.stopAnimation();
                enterCodeBinding.clearTextViewVerifyCode.setVisibility(View.VISIBLE);
                enterCodeBinding.clearTextViewVerifyCode2.setVisibility(View.GONE);
                enterCodeBinding.clearTextViewVerifyCode2.revertAnimation();
                break;
        }
    }

    private void setListenersForTextWatcher() {
        enterCodeBinding.editTextCode1.addTextChangedListener(new EditTextVerificationCodeListener(enterCodeBinding.editTextCode2, enterCodeBinding.editTextCode1));
        enterCodeBinding.editTextCode2.addTextChangedListener(new EditTextVerificationCodeListener(enterCodeBinding.editTextCode3, enterCodeBinding.editTextCode2));
        enterCodeBinding.editTextCode3.addTextChangedListener(new EditTextVerificationCodeListener(enterCodeBinding.editTextCode4, enterCodeBinding.editTextCode3));
        enterCodeBinding.editTextCode4.addTextChangedListener(new EditTextVerificationCodeListener(enterCodeBinding.editTextCode5, enterCodeBinding.editTextCode4));
        enterCodeBinding.editTextCode5.addTextChangedListener(new EditTextVerificationCodeListener(enterCodeBinding.editTextCode6, enterCodeBinding.editTextCode5));
    }


    public class EnterCodeClickListener {
        private Context context;

        EnterCodeClickListener(Context context) {
            this.context = context;
        }


        public void onVerifyClick(View view) {
            if (enterCodeBinding.editTextCode1.getText().toString().isEmpty() && enterCodeBinding.editTextCode2.getText().toString().isEmpty() &&
                    enterCodeBinding.editTextCode3.getText().toString().isEmpty() && enterCodeBinding.editTextCode4.getText().toString().isEmpty() &&
                    enterCodeBinding.editTextCode5.getText().toString().isEmpty() && enterCodeBinding.editTextCode6.getText().toString().isEmpty()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.enter_verification_code));
                return;
            }
//            ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
            enterCodeBinding.clearTextViewVerifyCode.setVisibility(View.INVISIBLE);
            enterCodeBinding.clearTextViewVerifyCode2.setVisibility(View.VISIBLE);
            enterCodeBinding.clearTextViewVerifyCode2.startAnimation();
            EventBus.getDefault().post(new EventBusPhoneVerification(getArguments().getString("verification_id", ""), enterCodeBinding.editTextCode1.getText().toString() + enterCodeBinding.editTextCode2.getText().toString() + enterCodeBinding.editTextCode3.getText().toString() + enterCodeBinding.editTextCode4.getText().toString() + enterCodeBinding.editTextCode5.getText().toString() + enterCodeBinding.editTextCode6.getText().toString(), "mFverify"));
        }

        public void onResendCodeClick(View view) {
            enterCodeBinding.textViewVerifyPhoneNo.setText(getResources().getString(R.string.verification_code_resent));
            ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
            EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFresend"));
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

    }

    class EditTextVerificationCodeListener implements TextWatcher {

        private EditText editTextCodeTo, editTextFrom;

        EditTextVerificationCodeListener(EditText editTextCodeTo, EditText editTextFrom) {
            this.editTextCodeTo = editTextCodeTo;
            this.editTextFrom = editTextFrom;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextFrom.getText().toString().length() == 1 && editTextCodeTo.getText().toString().length() == 0) {
                editTextCodeTo.requestFocus();
            }
        }
    }

}