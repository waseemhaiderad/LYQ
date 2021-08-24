package lnq.com.lnq.fragments.registeration.profileverification;


import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentProfileVerifiedBinding;
import lnq.com.lnq.utils.ValidUtils;

public class FragmentProfileVerified extends Fragment implements View.OnClickListener {

    private FragmentProfileVerifiedBinding mBind;
    AppCompatImageView imageViewBackTopBar;

    public FragmentProfileVerified() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_verified_profile,container,false);
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_verified, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        CardView topBarLayout = mBind.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.verified_profile);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void init() {
        mBind.mBtnOk.setOnClickListener(this);

        ValidUtils.textViewGradientColor(mBind.textView6);
        ValidUtils.buttonGradientColor(mBind.verifyprofile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnOk:
                getActivity().onBackPressed();
                break;
        }
    }
}
