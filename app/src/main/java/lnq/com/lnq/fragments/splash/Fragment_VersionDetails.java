package lnq.com.lnq.fragments.splash;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentVersionDetailsBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.utils.ValidUtils;


public class Fragment_VersionDetails extends Fragment implements View.OnClickListener {

    FragmentVersionDetailsBinding fragmentVersionDetailsBinding;

    public Fragment_VersionDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentVersionDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment__version_details, container, false);
        return fragmentVersionDetailsBinding.getRoot();

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentVersionDetailsBinding.clearTextOk.setOnClickListener(this);

        ValidUtils.textViewGradientColor(fragmentVersionDetailsBinding.textViewLabel);
        ValidUtils.textViewGradientColor(fragmentVersionDetailsBinding.textViewPoint1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clearTextOk:
                Bundle bundle = new Bundle();
                bundle.putString(EndpointKeys.ON_BOARD_TUTORIAL_TYPE, EndpointKeys.INTRODUCTION);
                if (getArguments() != null) {
                    bundle.putBoolean(EndpointKeys.OPEN_LOGIN, getArguments().getBoolean(EndpointKeys.OPEN_LOGIN, false));
                }
                ((MainActivity) getActivity()).fnLoadFragReplaceCommitAllowing(Constants.TUTORIAL_BASE, false, bundle);
                break;
        }
    }
}