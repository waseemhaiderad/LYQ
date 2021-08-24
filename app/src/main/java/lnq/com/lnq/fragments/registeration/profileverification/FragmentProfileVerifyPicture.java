package lnq.com.lnq.fragments.registeration.profileverification;


import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.databinding.FragmentProfileVerifyPictureBinding;
import lnq.com.lnq.utils.ValidUtils;

public class FragmentProfileVerifyPicture extends Fragment implements View.OnClickListener {

    private FragmentProfileVerifyPictureBinding mBind;
    AppCompatImageView imageViewBackTopBar;

    public FragmentProfileVerifyPicture() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_verify_picture, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        CardView topBarLayout = mBind.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.verify_profile_picture);
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBind.mPb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (LnqApplication.getInstance().sharedPreferences.getString("avatar_from", "").equals("gallery")) {
            mBind.mBtnUseThisPhoto.setVisibility(View.INVISIBLE);
        }
        Glide.with(getActivity())
                .load(LnqApplication.getInstance().sharedPreferences.getString("user_avatar", ""))
                .apply(new RequestOptions().centerCrop())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().placeholder(R.drawable.avatar))
                .into(mBind.mImgVerifyProfile);
        mBind.mBtnTakeNewPhoto.setOnClickListener(this);
        mBind.mBtnUseThisPhoto.setOnClickListener(this);

        ValidUtils.textViewGradientColor(mBind.mTvVerifyProfileDes);
        ValidUtils.buttonGradientColor(mBind.mBtnTakeNewPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnTakeNewPhoto:
                ((MainActivity) getActivity()).fnLoadFragAdd("PROFILE VERIFY TAKE PHOTO", true, null);
                break;
            case R.id.mBtnUseThisPhoto:
                Bundle bundle = new Bundle();
                bundle.putString("type", "verify_profile");
                ((MainActivity) getActivity()).fnLoadFragAdd("PROFILE LOOKS GOOD", true, bundle);
                break;
        }
    }

}