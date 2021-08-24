package lnq.com.lnq.fragments.setting.account.accountsetting;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentSuccessPasswordBinding;

public class FragmentSuccessPassword extends Fragment {

    private FragmentSuccessPasswordBinding mBind;

    private Handler mWaitHandler = new Handler();

    public FragmentSuccessPassword() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_success_password, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            String type = getArguments().getString("type", "");
            switch (type) {
                case "password":
                    mBind.mTvPhoneNumberShould.setText("Your password has been updated.");
                    break;
                case "email":
                    mBind.mTvPhoneNumberShould.setText("You email has been updated.");
                    break;
            }
        }
        mWaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ((MainActivity) getActivity()).popBackHomeFragment("ACCOUNT SETTING");
                } catch (Exception ignored) {

                }
            }
        }, 3000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWaitHandler != null) {
            mWaitHandler.removeCallbacks(null);
        }
    }
}