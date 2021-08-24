package lnq.com.lnq.fragments.idverification;


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
import lnq.com.lnq.databinding.FragIdentificationVerifyBinding;

public class FragIdentificationVerify extends Fragment implements View.OnClickListener {
    public FragIdentificationVerify() {
    }

    private FragIdentificationVerifyBinding mBind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.frag_identification_verify, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.mImgBack.setOnClickListener(this);
        mBind.mBtnFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImgBack:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnFinish:
                ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((MainActivity) getActivity()).fnLoadFragReplace("IDENTITY VERIFY SUCCESS", false, null);
                break;
        }
    }
}