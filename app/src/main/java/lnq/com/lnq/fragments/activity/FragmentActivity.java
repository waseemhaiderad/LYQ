package lnq.com.lnq.fragments.activity;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ActivityAdapter;
import lnq.com.lnq.adapters.MentionChatAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.android_tag_view.TagView;
import lnq.com.lnq.databinding.FragmentActivityBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.lnqrequest.EventBusUpdateAll;
import lnq.com.lnq.fragments.qrcode.EventBusActivityGestures;
import lnq.com.lnq.listeners.activit_listeners.OnSwipeTouchListener;
import lnq.com.lnq.listeners.activit_listeners.SwipeDetector;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.activity.ActivityData;
import lnq.com.lnq.model.gson_converter_models.activity.ActivityMainObject;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusActivityClick;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentActivity extends Fragment implements TextWatcher, TextView.OnEditorActionListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentActivityBinding activityBinding;
    private ActivityClickHandler clickHandler;
    private LayoutInflater layoutInflater;

    //    Retrofit fields....
    private Call<ActivityMainObject> callActivity;

    //    Adapter fields....
    private ActivityAdapter userActivityAdapter;

    //    Instance fields....
    private List<ActivityData> activityDataList = new ArrayList<>();
    private List<String> tagsList = new ArrayList<>();
    private String searchSuggestion;
    private SlideUp slideUp;

    Handler handler;
    Runnable runnable;
    AppCompatImageView imageSimmer;
    TextView textShimmer;
    String senderProfileId;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;

    //    Font fields....
    private FontUtils fontUtils;
    private List<String> userFilter = new ArrayList<>();

    public FragmentActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activity, container, false);
        return activityBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        OverScrollDecoratorHelper.setUpOverScroll(activityBinding.recyclerViewActivity, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
//        imageSimmer = view.findViewById(R.id.imgShimmer);
//        textShimmer = view.findViewById(R.id.textName);
//
//        makeMeBlink(imageSimmer, 1000, 50);
//        makeMeBlink(textShimmer, 1000, 50);

        CardView topBarLayout = activityBinding.tobBar.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.activity);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);
        imageViewContactGridTopBar.setVisibility(View.GONE);
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(activityBinding.slideView)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    activityBinding.checkBoxBlockedUsers.setChecked(false);
                                    activityBinding.checkBoxVerifiedProfile.setChecked(false);
                                    activityBinding.checkBoxPendingLNQs.setChecked(false);
                                    activityBinding.checkBoxOutstandingTasks.setChecked(false);
                                    activityBinding.checkBoxFavourites.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(activityBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(activityBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(activityBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(activityBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                            if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                                changeSelection(activityBinding.checkBoxBlockedUsers);
                                                userFilter.add(Constants.BLOCKED_USERS);
                                            }
                                        }
                                    }
                                    activityBinding.editTextSearch.requestFocus();
                                    activityBinding.tobBar.topBarContactCardView.setVisibility(View.VISIBLE);
                                    activityBinding.viewHideTopBar.setVisibility(View.GONE);
                                    activityBinding.searchBarLayout.setVisibility(View.GONE);

                                } else {
                                    activityBinding.checkBoxBlockedUsers.setChecked(false);
                                    activityBinding.checkBoxVerifiedProfile.setChecked(false);
                                    activityBinding.checkBoxPendingLNQs.setChecked(false);
                                    activityBinding.checkBoxOutstandingTasks.setChecked(false);
                                    activityBinding.checkBoxFavourites.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(activityBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(activityBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(activityBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(activityBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                            if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                                changeSelection(activityBinding.checkBoxBlockedUsers);
                                                userFilter.add(Constants.BLOCKED_USERS);
                                            }
                                        }
                                    }
                                    activityBinding.editTextSearch.requestFocus();
                                    activityBinding.tobBar.topBarContactCardView.setVisibility(View.INVISIBLE);
                                    activityBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                                    activityBinding.searchBarLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .withStartGravity(Gravity.TOP)
                        .withLoggingEnabled(true)
                        .withGesturesEnabled(true)
                        .withStartState(SlideUp.State.SHOWED)
                        .build();
            }
        });
        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), activityBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
    }

    private void init() {
        if (getActivity() != null) {
//        Registering event bus for different triggers....
            EventBus.getDefault().register(this);

//        Setting custom font....
            setCustomFont();

            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                if (userFilters != null) {
                    if (userFilters.contains(Constants.FAVORITES)) {
                        changeSelection(activityBinding.checkBoxFavourites);
                        userFilter.add(Constants.FAVORITES);
                    }
                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                        changeSelection(activityBinding.checkBoxVerifiedProfile);
                        userFilter.add(Constants.VERIFIED_PROFILE);
                    }
                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                        changeSelection(activityBinding.checkBoxPendingLNQs);
                        userFilter.add(Constants.PENDING_LNQS);
                    }
                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                        changeSelection(activityBinding.checkBoxOutstandingTasks);
                        userFilter.add(Constants.OUTSTANDING_TASKS);
                    }
                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                        changeSelection(activityBinding.checkBoxBlockedUsers);
                        userFilter.add(Constants.BLOCKED_USERS);
                    }
                }
            }

            activityBinding.textViewClearAll.setOnClickListener(this);
            activityBinding.clearTextViewApply.setOnClickListener(this);
            activityBinding.textViewFavorites.setOnClickListener(this);
            activityBinding.textViewVerifiedProfiles.setOnClickListener(this);
            activityBinding.textViewPendingLnq.setOnClickListener(this);
            activityBinding.textViewOutstandingTasks.setOnClickListener(this);
            activityBinding.textViewBlockedUsers.setOnClickListener(this);
            activityBinding.mTvAccountHeading1.setOnClickListener(this);
            activityBinding.imageViewBack.setOnClickListener(this);

            activityBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
            activityBinding.checkBoxBlockedUsers.setOnCheckedChangeListener(this);
            activityBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
            activityBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
            activityBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

            senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

//        Setting default values for android views....
            ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.INVISIBLE);
            ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);

            toggleFilterButtonBackground();
            activityBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));

//        Setting layout manager for recycler view....
            activityBinding.recyclerViewActivity.setLayoutManager(new LinearLayoutManager(getActivity()));

//        Checking id edit text search field is empty or not to toggle visibility of close icon....
            if (activityBinding.editTextSearch.getText().toString().isEmpty()) {
                activityBinding.imageViewClose.setVisibility(View.GONE);
            } else {
                activityBinding.imageViewClose.setVisibility(View.VISIBLE);
            }

//        Getting user activities from api....
            getUserActivity();

//        Setting click handler for data binding....
            clickHandler = new ActivityClickHandler();
            activityBinding.setClickHandler(clickHandler);

//        All event listeners....
            activityBinding.editTextSearch.addTextChangedListener(this);
            activityBinding.editTextSearch.setOnEditorActionListener(this);
//            activityBinding.recyclerViewActivity.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    EventBus.getDefault().post(new EventBusActivityClick(position, activityDataList.get(position).getActivity_type()));
//                }
//            }));
        }

        activityBinding.recyclerViewActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValidUtils.hideKeyboardFromFragment(getContext(), activityBinding.getRoot());
                return false;
            }
        });

        activityBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
                    activityBinding.searchBarLayout.setVisibility(View.GONE);
                }
            }
        });

        slideUp = new SlideUpBuilder(activityBinding.slideView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            activityBinding.checkBoxBlockedUsers.setChecked(false);
                            activityBinding.checkBoxVerifiedProfile.setChecked(false);
                            activityBinding.checkBoxPendingLNQs.setChecked(false);
                            activityBinding.checkBoxOutstandingTasks.setChecked(false);
                            activityBinding.checkBoxFavourites.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(activityBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(activityBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(activityBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(activityBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                        changeSelection(activityBinding.checkBoxBlockedUsers);
                                        userFilter.add(Constants.BLOCKED_USERS);
                                    }
                                }
                            }
                            activityBinding.tobBar.topBarContactCardView.setVisibility(View.VISIBLE);
                            activityBinding.viewHideTopBar.setVisibility(View.GONE);
                            activityBinding.searchBarLayout.setVisibility(View.GONE);
                        } else {
                            activityBinding.checkBoxBlockedUsers.setChecked(false);
                            activityBinding.checkBoxVerifiedProfile.setChecked(false);
                            activityBinding.checkBoxPendingLNQs.setChecked(false);
                            activityBinding.checkBoxOutstandingTasks.setChecked(false);
                            activityBinding.checkBoxFavourites.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(activityBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(activityBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(activityBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(activityBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                    if (userFilters.contains(Constants.BLOCKED_USERS)) {
                                        changeSelection(activityBinding.checkBoxBlockedUsers);
                                        userFilter.add(Constants.BLOCKED_USERS);
                                    }
                                }
                            }
                            activityBinding.tobBar.topBarContactCardView.setVisibility(View.INVISIBLE);
                            activityBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                            activityBinding.searchBarLayout.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(activityBinding.viewScroll)
                .build();

    }

    //    Method to set custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
//        fontUtils.setTextViewRegularFont(activityBinding.textViewActivityHeading);
        fontUtils.setEditTextSemiBold(activityBinding.editTextSearch);
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(activityBinding.textViewSortBy);
        fontUtils.setTextViewRegularFont(activityBinding.clearTextViewMostRecent);
        fontUtils.setTextViewRegularFont(activityBinding.textViewShowOnly);
        fontUtils.setTextViewRegularFont(activityBinding.textViewFavorites);
        fontUtils.setTextViewRegularFont(activityBinding.textViewVerifiedProfiles);
        fontUtils.setTextViewRegularFont(activityBinding.textViewPendingLnq);
        fontUtils.setTextViewRegularFont(activityBinding.textViewOutstandingTasks);
        fontUtils.setTextViewRegularFont(activityBinding.textViewBlockedUsers);
        fontUtils.setTextViewRegularFont(activityBinding.clearTextViewApply);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        EventBus.getDefault().unregister(this);
        if (callActivity != null && callActivity.isExecuted()) {
            callActivity.cancel();
        }
    }

    private void getUserActivity() {
        reqUserActivity(activityBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), senderProfileId);
    }

    private void toggleFilterButtonBackground() {
        String filters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
        if (filters != null)
            if (filters.isEmpty()) {
                activityBinding.buttonFilter.setVisibility(View.VISIBLE);
//                activityBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
                activityBinding.horizontalScrollViewActivityFilters.setVisibility(View.GONE);
            } else {
                if (layoutInflater == null) {
                    layoutInflater = LayoutInflater.from(getContext());
                }
//                activityBinding.linearLayoutActivityFilter.removeAllViews();
                List<String> filterList = new ArrayList<>(Arrays.asList(filters.split(",")));
                if (filterList.size() > 0) {
                    for (int i = 0; i < filterList.size(); i++) {
                        View filterView = layoutInflater.inflate(R.layout.row_filter, null);
                        TextView textViewFilter = filterView.findViewById(R.id.textViewFilterRow);
                        textViewFilter.setText(filterList.get(i));
                        ImageView imageViewCloseFilter = filterView.findViewById(R.id.imageViewCloseFilter);
                        imageViewCloseFilter.setOnClickListener(view -> {
//                            activityBinding.linearLayoutActivityFilter.removeView(filterView);
                            filterList.remove(textViewFilter.getText().toString());
                            String filter = filterList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "");
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, filter).apply();
                            EventBus.getDefault().post(new EventBusUpdateFilters());
                            EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                            if (filterList.size() == 0) {
                                activityBinding.buttonFilter.setVisibility(View.VISIBLE);
//                                activityBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
                                activityBinding.horizontalScrollViewActivityFilters.setVisibility(View.GONE);
                            }
                        });
//                        activityBinding.linearLayoutActivityFilter.addView(filterView);
                    }
                }
                activityBinding.horizontalScrollViewActivityFilters.setVisibility(View.VISIBLE);
                activityBinding.buttonFilter.setVisibility(View.INVISIBLE);
//                activityBinding.clearTextViewFilter.setVisibility(View.VISIBLE);
            }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusActivityClick(EventBusActivityClick eventBusActivityClick) {
        String userId = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equals(activityDataList.get(eventBusActivityClick.getPosition()).getSender_id()) ? activityDataList.get(eventBusActivityClick.getPosition()).getReceiver_id() : activityDataList.get(eventBusActivityClick.getPosition()).getSender_id();
        if (!userId.equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), activityBinding.getRoot());
            Bundle bundle = new Bundle();
            bundle.putString(EndpointKeys.USER_ID, userId);
            bundle.putString(EndpointKeys.PROFILE_ID, activityDataList.get(eventBusActivityClick.getPosition()).getReceiver_profile_id());
            bundle.putString(EndpointKeys.ACTIVITY_TYPE, activityDataList.get(eventBusActivityClick.getPosition()).getActivity_type());
            if (eventBusActivityClick.getClickType().equals(Constants.LNQ_REQUESTED) && activityDataList.get(eventBusActivityClick.getPosition()).getIs_connection().equals(Constants.CONTACTED)) {
                bundle.putString(EndpointKeys.USER_NAME, activityDataList.get(eventBusActivityClick.getPosition()).getUser_name());
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.ACCEPT_REQUEST_POPUP, true, bundle);
            } else {
                bundle.putString("topBar", "activity");
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, true, bundle);
            }
        }
    }

    //    Event bus method triggers when user filters are updated....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserFilters(EventBusUpdateFilters eventBusUpdateFilters) {
        if (getActivity() != null) {
            toggleFilterButtonBackground();
            getUserActivity();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSwipeGesture(EventBusActivityGestures eventBusActivityGestures) {
        if (eventBusActivityGestures.getSwipeTypeEnum() == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {

            onRightSwipe();
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    onLeftSwipe();

                }
            };

            handler.postDelayed(runnable, 1000);

        } else if (eventBusActivityGestures.getSwipeTypeEnum() == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
            onLeftSwipe();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateAll(EventBusUpdateAll mObj){
        getUserActivity();
    }

    public void onLeftSwipe() {
        for (int i = 0; i < activityDataList.size(); i++) {
            activityDataList.get(i).setShowDate(false);
            userActivityAdapter.notifyItemChanged(i);
        }
    }

    public void onRightSwipe() {
        for (int i = 0; i < activityDataList.size(); i++) {
            activityDataList.get(i).setShowDate(true);
            userActivityAdapter.notifyItemChanged(i);
        }
    }

//    public static View makeMeBlink(View view, int duration, int offset) {
//
//        Animation anim = new AlphaAnimation(0.3f, 1.0f);
//        anim.setDuration(duration);
//        anim.setStartOffset(offset);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        view.startAnimation(anim);
//        return view;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                activityBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                activityBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                activityBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                activityBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.textViewBlockedUsers:
                activityBinding.checkBoxBlockedUsers.setChecked(!userFilter.contains(Constants.BLOCKED_USERS));
                break;
            case R.id.mTvAccountHeading1:
            case R.id.imageViewBack:
                slideUp.hide();
                break;
            case R.id.viewCloseFilter:
                getActivity().onBackPressed();
                break;
            case R.id.clearTextViewApply:
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, userFilter.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "")).apply();
                EventBus.getDefault().post(new EventBusUpdateFilters());
                EventBus.getDefault().post(new EventBusUserSession("activity_filter"));
                slideUp.hide();
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                activityBinding.checkBoxBlockedUsers.setChecked(false);
                activityBinding.checkBoxVerifiedProfile.setChecked(false);
                activityBinding.checkBoxPendingLNQs.setChecked(false);
                activityBinding.checkBoxOutstandingTasks.setChecked(false);
                activityBinding.checkBoxFavourites.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
        }
    }

    private void changeSelection(AppCompatCheckBox checkBox) {
        checkBox.setChecked(true);
    }

    private void isChecked(String key, boolean isSelected) {
        if (isSelected) {
            userFilter.add(key);
        } else {
            userFilter.remove(key);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == activityBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, activityBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == activityBinding.checkBoxBlockedUsers) {
            isChecked(Constants.BLOCKED_USERS, activityBinding.checkBoxBlockedUsers.isChecked());
        } else if (buttonView == activityBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, activityBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == activityBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, activityBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == activityBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, activityBinding.checkBoxVerifiedProfile.isChecked());
        }
    }

    //    Method to hit api to get user activity....
    private void reqUserActivity(String searchKey, String searchFilter, String profileid) {
        activityDataList.clear();
        activityBinding.shimmerLayout.setVisibility(View.VISIBLE);
        activityBinding.shimmerLayout.startShimmerAnimation();
        activityBinding.textViewNoActivityFound.setVisibility(View.GONE);
        callActivity = Api.WEB_SERVICE.userActivity(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "")
                        , LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                LnqApplication.getInstance().sharedPreferences.getString("id", ""), searchKey, searchFilter, profileid);
        callActivity.enqueue(new Callback<ActivityMainObject>() {
            @Override
            public void onResponse(Call<ActivityMainObject> call, Response<ActivityMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            activityBinding.shimmerLayout.setVisibility(View.GONE);
                            activityBinding.shimmerLayout.stopShimmerAnimation();
                            activityBinding.textViewNoActivityFound.setVisibility(View.GONE);
                            activityBinding.recyclerViewActivity.setVisibility(View.VISIBLE);
                            activityDataList = response.body().getUserActivity();
                            activityDataList.add(0, new ActivityData());
                            userActivityAdapter = new ActivityAdapter(getActivity(), activityDataList);
                            activityBinding.recyclerViewActivity.setAdapter(userActivityAdapter);
                            ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
                            ArrayList<MentionModel> tempMentionModelArrayList = new ArrayList<>();
                            tempMentionModelArrayList.clear();
                            mentionModelArrayList.clear();
                            for (int i = 1; i < activityDataList.size(); i++) {
                                if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(activityDataList.get(i).getReceiver_id())) {
                                    tempMentionModelArrayList.add(new MentionModel(activityDataList.get(i).getReceiver_id(), activityDataList.get(i).getUser_name(), activityDataList.get(i).getUser_avatar(), activityDataList.get(i).getReceiver_profile_id()));
                                }
                            }
                            HashMap<String, MentionModel> mentionModelHashMap = new HashMap<>();
                            for (int i = 0; i < tempMentionModelArrayList.size(); i++) {
                                mentionModelHashMap.put(tempMentionModelArrayList.get(i).getUserId(), tempMentionModelArrayList.get(i));
                            }
                            for (MentionModel mentionModel : mentionModelHashMap.values()) {
                                mentionModelArrayList.add(mentionModel);
                            }
                            MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
                            activityBinding.editTextSearch.setAdapter(mentionChatAdapter);
                            activityBinding.editTextSearch.setThreshold(1);
                            activityBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ValidUtils.hideKeyboardFromFragment(getActivity(), activityBinding.getRoot());
                                    activityBinding.editTextSearch.setText(activityBinding.editTextSearch.getText());
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "");
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "");
                                    LnqApplication.getInstance().editor.apply();
                                    toggleFilterButtonBackground();
                                    getUserActivity();
                                }
                            });
                            break;
                        case 0:
                            if (response.body().getMessage() != null && response.body().getMessage().equals("activities not found")) {
                                activityBinding.recyclerViewActivity.setVisibility(View.GONE);
                                activityBinding.textViewNoActivityFound.setVisibility(View.VISIBLE);
                                activityBinding.shimmerLayout.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ActivityMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                activityBinding.shimmerLayout.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().isEmpty()) {
            activityBinding.imageViewClose.setVisibility(View.GONE);
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
            toggleFilterButtonBackground();
            getUserActivity();
        } else {
            activityBinding.imageViewClose.setVisibility(View.VISIBLE);
        }
        LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, s.toString()).apply();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), activityBinding.mRoot);
            slideUp.hide();
            EventBus.getDefault().post(new EventBusUserSession("activity_search"));
            getUserActivity();
            return true;
        }
        return false;
    }

    public class ActivityClickHandler {
        public void onFilterClick(View view) {
//            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.ACTIVITY_FILTER, true, null);
        }

        public void onCloseClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), activityBinding.getRoot());
            activityBinding.editTextSearch.setText("");
            LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "");
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "");
            LnqApplication.getInstance().editor.apply();
            toggleFilterButtonBackground();
            getUserActivity();
        }

        public void onSearchClick(View view) {
            getUserActivity();
        }

        public void onQrCodeClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), activityBinding.getRoot());
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
        }
    }
}
