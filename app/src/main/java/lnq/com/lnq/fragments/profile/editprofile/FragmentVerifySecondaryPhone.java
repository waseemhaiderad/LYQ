package lnq.com.lnq.fragments.profile.editprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
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
import lnq.com.lnq.databinding.FragmentUpdateNumberVerifyCodeBinding;
import lnq.com.lnq.databinding.FragmentVerifySecondaryPhoneBinding;
import lnq.com.lnq.fragments.setting.account.accountsetting.FragmentUpdateNumberVerifyCode;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.utils.ValidUtils;

public class FragmentVerifySecondaryPhone extends Fragment {

    //    Android fields....
    private FragmentVerifySecondaryPhoneBinding binding;
    private VerifyCodeClick clickHandler;

    public FragmentVerifySecondaryPhone() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_secondary_phone, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        binding.editTextCode1.requestFocus();
        ((MainActivity) getActivity()).fnShowKeyboardFrom(binding.editTextCode1);

        clickHandler = new VerifyCodeClick();
        binding.setClickListener(clickHandler);

        //        All event listeners....
        EventBus.getDefault().register(this);
        setListenersForTextWatcher();
    }

    //    Event bus trigger when verifying code....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void verificationCode(EventBusPhoneVerification mObj) {
        switch (mObj.getmFlag()) {
            case "mFprogress":
                if (((MainActivity) getActivity()).progressDialog.isShowing()) {
                    ((MainActivity) getActivity()).progressDialog.dismiss();
                }
                break;
        }
    }

    private void setListenersForTextWatcher() {
        binding.editTextCode1.addTextChangedListener(new EditTextVerificationCodeListener(binding.editTextCode2, binding.editTextCode1));
        binding.editTextCode2.addTextChangedListener(new EditTextVerificationCodeListener(binding.editTextCode3, binding.editTextCode2));
        binding.editTextCode3.addTextChangedListener(new EditTextVerificationCodeListener(binding.editTextCode4, binding.editTextCode3));
        binding.editTextCode4.addTextChangedListener(new EditTextVerificationCodeListener(binding.editTextCode5, binding.editTextCode4));
        binding.editTextCode5.addTextChangedListener(new EditTextVerificationCodeListener(binding.editTextCode6, binding.editTextCode5));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ValidUtils.hideKeyboardFromFragment(getActivity(), binding.editTextCode1);
    }

    public class VerifyCodeClick {

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void onVerifyClick(View view) {
            if (binding.editTextCode1.getText().toString().isEmpty() && binding.editTextCode2.getText().toString().isEmpty() &&
                    binding.editTextCode3.getText().toString().isEmpty() && binding.editTextCode4.getText().toString().isEmpty() &&
                    binding.editTextCode5.getText().toString().isEmpty() && binding.editTextCode6.getText().toString().isEmpty()) {
                ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.enter_verification_code));
                return;
            }
            EventBus.getDefault().post(new EventBusPhoneVerification(getArguments().getString("verification_id", ""), binding.editTextCode1.getText().toString() + binding.editTextCode2.getText().toString() + binding.editTextCode3.getText().toString() + binding.editTextCode4.getText().toString() + binding.editTextCode5.getText().toString() + binding.editTextCode6.getText().toString(), "mFverify"));
        }

        public void onResendCodeClick(View view) {
            binding.textViewVerifyPhoneNo.setText(getResources().getString(R.string.verification_code_resent));
            ((MainActivity) getActivity()).progressDialog.show();
            EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFresend"));
        }

    }
}