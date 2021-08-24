package lnq.com.lnq.fragments.connections;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentLnqCountsBinding;
import lnq.com.lnq.utils.FontUtils;

public class FragmentLnqCounts extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentLnqCountsBinding lnqCountsBinding;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentLnqCounts() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lnqCountsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lnq_counts, container, false);
        return lnqCountsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(lnqCountsBinding.textViewLnqCountsHeading);
        fontUtils.setTextViewRegularFont(lnqCountsBinding.textViewLnqCountDescription);
        fontUtils.setTextViewMedium(lnqCountsBinding.clearTextViewOk);

        lnqCountsBinding.imageViewBack.setOnClickListener(this);
        lnqCountsBinding.clearTextViewOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnOk:
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;

            case R.id.clearTextViewOk:
                getActivity().onBackPressed();
                break;

        }
    }

}