package lnq.com.lnq.fragments.setting;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentSettingBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.LogOut;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSetting extends Fragment {

    //    Android fields....
    private FragmentSettingBinding settingBinding;
    private SettingClickHandler clickHandler;

    //    Retrofit fields....
    private AppCompatImageView imageViewBackTopBar;

    public FragmentSetting() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        CardView topBarLayout = settingBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.settings);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.GONE);
    }

    private void init() {
//        Setting click handler for data binding....
        clickHandler = new SettingClickHandler(getActivity());
        settingBinding.setClickHandler(clickHandler);
        OverScrollDecoratorHelper.setUpOverScroll(settingBinding.nestedScrollView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public class SettingClickHandler {

        private Context context;

        public SettingClickHandler(Context context) {
            this.context = context;
        }

        public void onYourProfileClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.YOUR_PROFILE, true, null);
            EventBus.getDefault().post(new EventBusUserSession("profile_view"));
        }

        public void onVisibilitySettingClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VISIBILITY_SETTING, true, null);

        }

        public void onDefaultSettingClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.DEFAULT_SETTING, true, null);

        }

        public void onSyncContactsClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SYNC_CONTACTS, true, null);
        }

        public void onAccountSettingClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.ACCOUNT_SETTING, true, null);
        }

        public void onBlockedUserClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.BLOCKED_USERS_SETTING, true, null);
        }

        public void onSignOutClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_SIGNOUT, true, null);

        }

        public void onFaqClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.FAQS, true, null);
        }

        public void onCommunityGuidlineClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.COMMUNITY_GUIDLINES, true, null);
        }

        public void onVisibilityPolicyClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VISIBILITY_POLICY, true, null);
        }

        public void onPrivacyPolicyClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PRIVACY_POLICY, true, null);
        }

        public void onTermsAndConditionClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.TERMS_AND_CONDITIONS, true, null);
        }

        public void onContactUsClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.CONTACT_US, true, null);
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

    }

}