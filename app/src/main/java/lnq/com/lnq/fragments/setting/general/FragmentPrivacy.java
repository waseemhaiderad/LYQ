package lnq.com.lnq.fragments.setting.general;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentPrivacyBinding;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class FragmentPrivacy extends Fragment  implements View.OnClickListener {


    private FragmentPrivacyBinding mBind;

    public FragmentPrivacy() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mBind.imageViewBack.setOnClickListener(this);
        OverScrollDecoratorHelper.setUpOverScroll(mBind.nestedScrollView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
        }

    }
}