package lnq.com.lnq.fragments.registeration.createprofile;


import android.content.Context;

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

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.R;
import lnq.com.lnq.databinding.FragmentProfileRequiredBinding;
import lnq.com.lnq.model.event_bus_models.EventBusTakeNewPhoto;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;


public class FragmentProfileRequired extends Fragment {

    //    Android fields....
    private FragmentProfileRequiredBinding profileRequiredBinding;
    private ProfileRequiredClickHandler clickHandler;

    //    Font fields....
    private FontUtils fontUtils;
    private AppCompatImageView imageViewBackTopBar;

    public FragmentProfileRequired() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileRequiredBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_required, container, false);
        return profileRequiredBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setCustomFonts();
        CardView topBarLayout = profileRequiredBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.profile_required);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.INVISIBLE);
    }

    public void setCustomFonts() {
        fontUtils = FontUtils.getFontUtils(getActivity());

        fontUtils.setTextViewRegularFont(profileRequiredBinding.textViewProfileRequiredDes);
        fontUtils.setTextViewRegularFont(profileRequiredBinding.textViewProfileRequiredDes2);
        fontUtils.setTextViewMedium(profileRequiredBinding.clearTextViewTakeSelfie);
        fontUtils.setButtonMedium(profileRequiredBinding.buttonUploadPhoto);
    }

    private void init() {
//        Setting click handler for data binding....
        clickHandler = new ProfileRequiredClickHandler(getActivity());
        profileRequiredBinding.setClickHandler(clickHandler);

        ValidUtils.buttonGradientColor(profileRequiredBinding.buttonUploadPhoto);
    }

    public class ProfileRequiredClickHandler {
        private Context context;

        ProfileRequiredClickHandler(Context context) {
            this.context = context;
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

        public void onTakeSelfieClick(View view) {
            EventBus.getDefault().post(new EventBusTakeNewPhoto());
            getActivity().onBackPressed();
        }

        public void onUploadPhotoClick(View view) {
            EventBus.getDefault().post(new EventBusTakeNewPhoto());
            getActivity().onBackPressed();
        }

    }
}
