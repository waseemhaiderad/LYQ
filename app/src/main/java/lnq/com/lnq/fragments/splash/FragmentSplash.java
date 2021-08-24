package lnq.com.lnq.fragments.splash;

import android.animation.ObjectAnimator;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentSplashBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatCount;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;

public class FragmentSplash extends Fragment {

    //    Android fields....
    private FragmentSplashBinding splashBinding;

    //    Instance fields....
    private Handler handler;

    public FragmentSplash() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        splashBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);
        return splashBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
//        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void init() {
        if (getActivity() != null) {

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(splashBinding.imageViewSplashLogo, "translationY", 800, 0);
            objectAnimator.setDuration(2000);
            objectAnimator.start();

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean isFirstTime = LnqApplication.getInstance().sharedPreferences.getBoolean("is_first_time", false);
                    if (isFirstTime) {
                        checkUserVerificationStatus();
                    } else {
                        LnqApplication.getInstance().sharedPreferences.edit().putBoolean("is_first_time", true).apply();
                        Bundle bundle = new Bundle();
                        bundle.putString(EndpointKeys.ON_BOARD_TUTORIAL_TYPE, EndpointKeys.INTRODUCTION);
                        if (getArguments() != null) {
                            bundle.putBoolean(EndpointKeys.OPEN_LOGIN, getArguments().getBoolean(EndpointKeys.OPEN_LOGIN, false));
                        }
                        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VERSION_DETAILS, false, bundle);
                    }

                }
            }, 3000);
        }
    }

    private void checkUserVerificationStatus() {
        if (LnqApplication.getInstance().sharedPreferences.getBoolean(EndpointKeys.IS_USER_LOGGED_IN, false)) {
            ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            switch (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VERIFICATION_STATUS, "")) {
                case EndpointKeys.SIGN_UP:
                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.VERIFICATION_ONE, false, null);
                    break;
                case EndpointKeys.FIRSTNAME_LASTNAME:
                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.VERIFICATION_ONE, false, null);
                    break;
                case EndpointKeys.PHONE:
                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.PROFILE_CREATE, false, null);
                    break;
                case EndpointKeys.PROFILE:
                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.PROFILE_PICTURE, false, null);
                    break;
                case EndpointKeys.PROFILE_IMAGE:
                    EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.HOME, false, null);
                    EventBus.getDefault().post(new EventBusUserSession("app_launch"));
                    EventBus.getDefault().post(new EventBusUserSession("app_active"));
                    break;
            }
        } else {
            if (getArguments() != null) {
                if (getArguments().getBoolean(EndpointKeys.OPEN_LOGIN, false)) {
                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.LOGIN, false, null);
                } else {
                    ((MainActivity) getActivity()).fnLoadFragReplace(Constants.LOGIN, false, null);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

}