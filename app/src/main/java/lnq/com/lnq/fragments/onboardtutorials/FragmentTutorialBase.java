package lnq.com.lnq.fragments.onboardtutorials;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.TutorialPagerAdapter;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentTutorialBaseBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.onboardtutorials.guidlines.EventBusGuidlineTutorial2;
import lnq.com.lnq.fragments.onboardtutorials.guidlines.FragmentGuidlineTutorialOne;
import lnq.com.lnq.fragments.onboardtutorials.guidlines.FragmentGuidlineTutorialThree;
import lnq.com.lnq.fragments.onboardtutorials.guidlines.FragmentGuidlineTutorialTwo;
import lnq.com.lnq.fragments.onboardtutorials.introduction.FragmentTutorialOne;
import lnq.com.lnq.fragments.onboardtutorials.introduction.FragmentTutorialThree;
import lnq.com.lnq.fragments.onboardtutorials.introduction.FragmentTutorialTwo;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatCount;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.application.LnqApplication;

public class FragmentTutorialBase extends Fragment implements ViewPager.OnPageChangeListener {

    //    Android fields....
    private FragmentTutorialBaseBinding tutorialBaseBinding;
    private BaseTutorialClickListener baseTutorialClickListener;

    //    Adapter fields.....
    private TutorialPagerAdapter tutorialPagerAdapter;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private boolean isLastPageSwiped;
    private int counterPageScroll;
    private String pagerType;

    public FragmentTutorialBase() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tutorialBaseBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial_base, container, false);
        return tutorialBaseBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (getActivity() != null) {
            if (getArguments() != null) {
                tutorialPagerAdapter = new TutorialPagerAdapter(getChildFragmentManager());

//                Checking where did user come from 1 - Splash, 2 - Guid lines....
                pagerType = getArguments().getString(EndpointKeys.ON_BOARD_TUTORIAL_TYPE, "");

                if (pagerType.equals(EndpointKeys.GUID_LINE)) {
                    tutorialBaseBinding.textViewSkip.setVisibility(View.GONE);
                    tutorialPagerAdapter.addFragment(new FragmentGuidlineTutorialOne());
                    tutorialPagerAdapter.addFragment(new FragmentGuidlineTutorialTwo());
                    tutorialPagerAdapter.addFragment(new FragmentGuidlineTutorialThree());
                } else {
                    tutorialPagerAdapter.addFragment(new FragmentTutorialOne());
                    tutorialPagerAdapter.addFragment(new FragmentTutorialTwo());
                    tutorialPagerAdapter.addFragment(new FragmentTutorialThree());
                }
            }
            tutorialBaseBinding.viewPagerTutorialBase.setAdapter(tutorialPagerAdapter);
            tutorialBaseBinding.viewPagerTutorialBase.setOffscreenPageLimit(2);

//            Setting custom font....
            setCustomFont();

//            All event listeners....
            tutorialBaseBinding.viewPagerTutorialBase.addOnPageChangeListener(this);
            baseTutorialClickListener = new BaseTutorialClickListener(getActivity());
            tutorialBaseBinding.setClickListener(baseTutorialClickListener);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fragmentGuidlines2(EventBusGuidlineTutorial2 guidlineTutorial2){
            if (guidlineTutorial2.getPosition() < 2) {
                tutorialBaseBinding.viewPagerTutorialBase.setCurrentItem(guidlineTutorial2.getPosition() + 1);
            } else {
                checkUserVerificationStatus();
            }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int i1) {
        if (pagerType.equals(EndpointKeys.GUID_LINE)) {
            if (position == 2 && positionOffset == 0 && !isLastPageSwiped) {
                if (counterPageScroll != 0) {
                    isLastPageSwiped = true;
                    Log.d("BaseTutorial",positionOffset + " " + position + " ");
                    checkUserVerificationStatus();
                }
                counterPageScroll++;
            } else {
                counterPageScroll = 0;
            }
        } else if (pagerType.equals(EndpointKeys.INTRODUCTION)) {
            if (position == 2 && positionOffset == 0 && !isLastPageSwiped) {
                if (counterPageScroll != 0) {
                    isLastPageSwiped = true;
                    Log.d("BaseTutorial",positionOffset + " " + position + " ");
                    checkUserVerificationStatus();
                }
                counterPageScroll++;
            } else {
                counterPageScroll = 0;
            }
        }
    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                tutorialBaseBinding.imageViewCircleDot1.setBackground(getResources().getDrawable(R.drawable.bg_newcircle_blue));
                tutorialBaseBinding.imageViewCircleDot2.setBackground(getResources().getDrawable(R.drawable.bg_newcircle_blue_border));
                tutorialBaseBinding.imageViewCircleDot3.setBackground(getResources().getDrawable(R.drawable.bg_newcircle_blue_border));
                break;
            case 1:
                tutorialBaseBinding.imageViewCircleDot2.setBackground(getResources().getDrawable(R.drawable.bg_newcircle_blue));
                tutorialBaseBinding.imageViewCircleDot3.setBackground(getResources().getDrawable(R.drawable.bg_newcircle_blue_border));
                break;
            case 2:
                tutorialBaseBinding.imageViewCircleDot3.setBackground(getResources().getDrawable(R.drawable.bg_newcircle_blue));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    //    Setting custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(tutorialBaseBinding.textViewSkip);
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

    public class BaseTutorialClickListener {

        private Context context;

        BaseTutorialClickListener(Context context) {
            this.context = context;
        }

        public void onNextClick(View view) {
            if (getActivity() != null) {
                if (tutorialBaseBinding.viewPagerTutorialBase.getCurrentItem() < 2) {
                    tutorialBaseBinding.viewPagerTutorialBase.setCurrentItem(tutorialBaseBinding.viewPagerTutorialBase.getCurrentItem() + 1);
                } else {
                    checkUserVerificationStatus();
                }
            }
        }

        public void onSkipClick(View view) {
            if (getActivity() != null) {
                checkUserVerificationStatus();
            }
        }

    }
}
