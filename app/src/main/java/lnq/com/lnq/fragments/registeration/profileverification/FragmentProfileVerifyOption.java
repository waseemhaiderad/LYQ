package lnq.com.lnq.fragments.registeration.profileverification;


import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentProfileVerifyOptionBinding;
import lnq.com.lnq.utils.ValidUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfileVerifyOption extends Fragment implements View.OnClickListener {
    private FragmentProfileVerifyOptionBinding mBind;
    AppCompatImageView imageViewBackTopBar;
    String type = "";

    public FragmentProfileVerifyOption() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_verify_option, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        CardView topBarLayout = mBind.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.verify_your_profile);
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
        if (getArguments() != null){
            type = getArguments().getString("type");
        }
        if (type.equalsIgnoreCase("verify")){
            mBind.mBtnVerifyId.setVisibility(View.GONE);
        }else {
            mBind.mBtnVerifyId.setVisibility(View.VISIBLE);
        }
        mBind.mBtnVerifyId.setOnClickListener(this);
        mBind.mBtnVerifyProfilePicture.setOnClickListener(this);

        ValidUtils.textViewGradientColor(mBind.mTvProfileVerifyOptDes);
        ValidUtils.buttonGradientColor(mBind.mBtnVerifyId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBtnVerifyId:
                ((MainActivity) getActivity()).fnLoadFragAdd("ID VERIFY", true, null);
                break;
            case R.id.mBtnVerifyProfilePicture:
                ((MainActivity) getActivity()).fnLoadFragAdd("PROFILE VERIFY TAKE PHOTO", true, null);
                break;
        }
    }
}
