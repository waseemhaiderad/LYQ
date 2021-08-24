package lnq.com.lnq.fragments.chat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ConversationAdapter;
import lnq.com.lnq.adapters.MentionChatAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentConversationBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.event_bus_models.EventBusConversationProfileClick;
import lnq.com.lnq.model.event_bus_models.EventBusEditGroupName;
import lnq.com.lnq.model.event_bus_models.EventBusGroupChatClicked;
import lnq.com.lnq.model.event_bus_models.EventBusMuteChat;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChat;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatCount;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatNew;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusChatClick;
import lnq.com.lnq.model.gson_converter_models.chat.MuteChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.MuteGroupChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.UnMuteChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.UnMuteGroupChatMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.ChatThread;
import lnq.com.lnq.model.gson_converter_models.conversation.GetChatThread;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentConversation extends Fragment implements TextWatcher, TextView.OnEditorActionListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    Constant fields....
    private static final String TAG = "FragmentConversation";

    //    Android fields....
    private FragmentConversationBinding conversationBinding;
    private ConversationClickHandler clickHandler;
    private LayoutInflater layoutInflater;
    ConversationAdapter conversationAdapter;
    private String searchSuggestion;
    private String profileId;

    //    Font fields....
    private FontUtils fontUtils;

    //    Retrofit Call....
    private Call<ChatThread> chatThreadCall;
    private Call<MuteChatMainObject> muteChatMainObjectCall;
    private Call<MuteGroupChatMainObject> muteGroupChatMainObjectCall;
    private Call<UnMuteChatMainObject> unMuteChatMainObjectCall;
    private Call<UnMuteGroupChatMainObject> unMuteGroupChatMainObjectCall;
    private Call<GroupChatMainObject> groupChatMainObjectCall;

    //    Instance fields...
    List<GetChatThread> chatThreadList = new ArrayList<>();

    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;
    private SlideUp slideUp;

    public FragmentConversation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        conversationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation, container, false);
        return conversationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        OverScrollDecoratorHelper.setUpOverScroll(conversationBinding.recyclerViewChat, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        CardView topBarLayout = conversationBinding.tobBar.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.messages);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);
        imageViewContactGridTopBar.setVisibility(View.GONE);
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(conversationBinding.slideView)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    conversationBinding.checkBoxFavourites.setChecked(false);
                                    conversationBinding.checkBoxOutstandingTasks.setChecked(false);
                                    conversationBinding.checkBoxPendingLNQs.setChecked(false);
                                    conversationBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(conversationBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(conversationBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(conversationBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(conversationBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                        }
                                    }
                                    conversationBinding.editTextSearch.requestFocus();
                                    conversationBinding.tobBar.topBarContactCardView.setVisibility(View.VISIBLE);
                                    conversationBinding.viewHideTopBar.setVisibility(View.GONE);
                                    conversationBinding.searchBarLayout.setVisibility(View.GONE);
                                } else {
                                    conversationBinding.checkBoxFavourites.setChecked(false);
                                    conversationBinding.checkBoxOutstandingTasks.setChecked(false);
                                    conversationBinding.checkBoxPendingLNQs.setChecked(false);
                                    conversationBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(conversationBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(conversationBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(conversationBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(conversationBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                        }
                                    }
                                    conversationBinding.editTextSearch.requestFocus();
                                    conversationBinding.tobBar.topBarContactCardView.setVisibility(View.INVISIBLE);
                                    conversationBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                                    conversationBinding.searchBarLayout.setVisibility(View.VISIBLE);
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
                ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });

    }

    private void init() {
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getActivity() != null) {

            profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

//            Registering event bus....
            EventBus.getDefault().register(this);
//            Setting default setting....
            ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
//            ((MainActivity) getActivity()).mBind.mViewBgBottomBar.setVisibility(View.INVISIBLE);

//        Setting custom font....
            setCustomFont();

            toggleFilterButtonBackground();
            conversationBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));

            //        Checking id edit text search field is empty or not to toggle visibility of close icon....
            if (conversationBinding.editTextSearch.getText().toString().isEmpty()) {
                conversationBinding.imageViewClose.setVisibility(View.GONE);
            } else {
                conversationBinding.imageViewClose.setVisibility(View.VISIBLE);
            }

            conversationBinding.editTextSearch.addTextChangedListener(this);
            conversationBinding.editTextSearch.setOnEditorActionListener(this);

//            Setting layout manager and item animator....
            conversationBinding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(getActivity()));
            conversationBinding.recyclerViewChat.setItemAnimator(new DefaultItemAnimator());

//            Getting chat conversation from api....
            getChatThread();

//        Setting click handler for data binding....
            clickHandler = new ConversationClickHandler();
            conversationBinding.setClickHandler(clickHandler);
        }

        conversationBinding.recyclerViewChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValidUtils.hideKeyboardFromFragment(getContext(), conversationBinding.getRoot());
                return false;
            }
        });

        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
            String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
            if (userFilters != null) {
                if (userFilters.contains(Constants.FAVORITES)) {
                    changeSelection(conversationBinding.checkBoxFavourites);
                    userFilter.add(Constants.FAVORITES);
                }
                if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                    changeSelection(conversationBinding.checkBoxVerifiedProfile);
                    userFilter.add(Constants.VERIFIED_PROFILE);
                }
                if (userFilters.contains(Constants.PENDING_LNQS)) {
                    changeSelection(conversationBinding.checkBoxPendingLNQs);
                    userFilter.add(Constants.PENDING_LNQS);
                }
                if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                    changeSelection(conversationBinding.checkBoxOutstandingTasks);
                    userFilter.add(Constants.OUTSTANDING_TASKS);
                }
            }
        }

        conversationBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
        conversationBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
        conversationBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
        conversationBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

        conversationBinding.imageViewBack.setOnClickListener(this);
        conversationBinding.clearTextViewApply.setOnClickListener(this);
        conversationBinding.textViewClearAll.setOnClickListener(this);
        conversationBinding.textViewFavorites.setOnClickListener(this);
        conversationBinding.textViewVerifiedProfiles.setOnClickListener(this);
        conversationBinding.textViewPendingLnq.setOnClickListener(this);
        conversationBinding.textViewOutstandingTasks.setOnClickListener(this);
        conversationBinding.imageViewBack.setOnClickListener(this);
        conversationBinding.mTvAccountHeading1.setOnClickListener(this);

        slideUp = new SlideUpBuilder(conversationBinding.slideView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            conversationBinding.checkBoxFavourites.setChecked(false);
                            conversationBinding.checkBoxOutstandingTasks.setChecked(false);
                            conversationBinding.checkBoxPendingLNQs.setChecked(false);
                            conversationBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(conversationBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(conversationBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(conversationBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(conversationBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                }
                            }
                            conversationBinding.tobBar.topBarContactCardView.setVisibility(View.VISIBLE);
                            conversationBinding.viewHideTopBar.setVisibility(View.GONE);
                            conversationBinding.searchBarLayout.setVisibility(View.GONE);
                        } else {
                            conversationBinding.checkBoxFavourites.setChecked(false);
                            conversationBinding.checkBoxOutstandingTasks.setChecked(false);
                            conversationBinding.checkBoxPendingLNQs.setChecked(false);
                            conversationBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(conversationBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(conversationBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(conversationBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(conversationBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                }
                            }
                            conversationBinding.tobBar.topBarContactCardView.setVisibility(View.INVISIBLE);
                            conversationBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                            conversationBinding.searchBarLayout.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(conversationBinding.viewScroll)
                .build();

        conversationBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
                    conversationBinding.searchBarLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    //    Method to set custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setEditTextSemiBold(conversationBinding.editTextSearch);
        fontUtils.setTextViewRegularFont(conversationBinding.textViewShowOnly);
        fontUtils.setTextViewRegularFont(conversationBinding.textViewFavorites);
        fontUtils.setTextViewRegularFont(conversationBinding.textViewVerifiedProfiles);
        fontUtils.setTextViewRegularFont(conversationBinding.textViewPendingLnq);
        fontUtils.setTextViewRegularFont(conversationBinding.textViewOutstandingTasks);
        fontUtils.setTextViewRegularFont(conversationBinding.clearTextViewApply);
    }

    private void changeSelection(AppCompatCheckBox compatCheckBox) {
        compatCheckBox.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                conversationBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                conversationBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                conversationBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                conversationBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
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
                EventBus.getDefault().post(new EventBusUserSession("conversation_filter"));
                slideUp.hide();
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                conversationBinding.checkBoxFavourites.setChecked(false);
                conversationBinding.checkBoxOutstandingTasks.setChecked(false);
                conversationBinding.checkBoxPendingLNQs.setChecked(false);
                conversationBinding.checkBoxVerifiedProfile.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
        }
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
        if (buttonView == conversationBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, conversationBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == conversationBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, conversationBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == conversationBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, conversationBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == conversationBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, conversationBinding.checkBoxVerifiedProfile.isChecked());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusActivityClick(EventBusChatClick eventBusChatClick) {
        Bundle bundle = new Bundle();
        bundle.putString("mFlag", "conversation");
        bundle.putString(EndpointKeys.USER_ID, eventBusChatClick.getUser_ID());
        bundle.putString(EndpointKeys.PROFILE_ID, eventBusChatClick.getRecevierProfileId());
        bundle.putString(EndpointKeys.USER_NAME, chatThreadList.get(eventBusChatClick.getPosition()).getUserName());
        bundle.putString(EndpointKeys.USER_AVATAR, chatThreadList.get(eventBusChatClick.getPosition()).getUserAvatar());
        bundle.putString(EndpointKeys.USER_CONNECTION_STATUS, chatThreadList.get(eventBusChatClick.getPosition()).getIsConnected());
        bundle.putString(EndpointKeys.IS_FAVORITE, chatThreadList.get(eventBusChatClick.getPosition()).getIsFavorite());
        bundle.putString(EndpointKeys.IS_BLOCK, chatThreadList.get(eventBusChatClick.getPosition()).getIsBlocked());
        bundle.putString(EndpointKeys.THREAD_ID, chatThreadList.get(eventBusChatClick.getPosition()).getThreadId());
        bundle.putString(EndpointKeys.CHAT_UNREAD_COUNT, chatThreadList.get(eventBusChatClick.getPosition()).getCount());
        ((MainActivity) getActivity()).fnLoadFragAdd("CHAT", true, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGroupChatClicked(EventBusGroupChatClicked eventBusGroupChatClicked) {
        Bundle bundle = new Bundle();
        bundle.putString(EndpointKeys.GROUP_USERNAMES, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getUserNames());
        bundle.putString(EndpointKeys.GROUP_NAME, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getGroup_name());
        bundle.putString(EndpointKeys.GROUP_CHAT_THREAD_ID, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getGroupchat_thread_id());
        bundle.putString(EndpointKeys.USER_AVATAR, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getUserAvatar());
        bundle.putString(EndpointKeys.GROUP_USERS_IDS, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getParticipant_ids());
        bundle.putString(EndpointKeys.GROUP_USERS_PROFILE_IDS, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getParticipant_profile_ids());
        bundle.putString(EndpointKeys.CHAT_UNREAD_COUNT, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getCount());
        bundle.putString(EndpointKeys.GROUP_USER_NAMES, chatThreadList.get(eventBusGroupChatClicked.getPosition()).getUserNames());
        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.GROUP_CHAT_FRAGMENT, true, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusConversationProfileClick(EventBusConversationProfileClick eventBusConversationProfileClick) {
        String userID = null;
        String profileId = null;
        if (chatThreadList.get(eventBusConversationProfileClick.getmPos()).getSenderId().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            userID = chatThreadList.get(eventBusConversationProfileClick.getmPos()).getReceiverId();
        } else {
            userID = chatThreadList.get(eventBusConversationProfileClick.getmPos()).getSenderId();
        }
        if (chatThreadList.get(eventBusConversationProfileClick.getmPos()).getSender_profile_id().equals(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
            profileId = chatThreadList.get(eventBusConversationProfileClick.getmPos()).getReceiver_profile_id();
        } else {
            profileId = chatThreadList.get(eventBusConversationProfileClick.getmPos()).getSender_profile_id();
        }
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userID);
        bundle.putString(EndpointKeys.PROFILE_ID, profileId);
        bundle.putString("topBar", "conversation");
        ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)

    public void eventBusUpdateConversation(EventBusUpdateChat eventBusUpdateChat) {
        getChatThread();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventBusUpdateChatNew(EventBusUpdateChatNew mObj) {
        getChatThreadWithOutShimmer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateCount(EventBusUpdateChatCount eventBusUpdateChat) {
        getChatThread();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusEditGroupName(EventBusEditGroupName eventBusEditGroupName) {
        getChatThread();
    }

    public void getChatThread() {
        chatThreadList.clear();
        if (conversationAdapter != null) {
            conversationAdapter.notifyDataSetChanged();
        }
        conversationBinding.shimmerLayout.setVisibility(View.VISIBLE);
        conversationBinding.shimmerLayout.startShimmerAnimation();
        chatThreadCall = Api.WEB_SERVICE.getUserGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""), profileId);
        chatThreadCall.enqueue(new Callback<ChatThread>() {
            @Override
            public void onResponse(Call<ChatThread> call, Response<ChatThread> response) {
                conversationBinding.constraintLayoutNotChatFound.setVisibility(View.GONE);
                conversationBinding.shimmerLayout.setVisibility(View.GONE);
                conversationBinding.shimmerLayout.stopShimmerAnimation();
                if (response.isSuccessful() && response != null) {
                    Log.d("datata", response.body().getGetChatThreads().toString());
                    switch (response.body().getStatus()) {
                        case 1:
                            chatThreadList.clear();
                            chatThreadList.addAll(response.body().getGetChatThreads());
                            for (int i = 0; i<chatThreadList.size(); i++){
                                if (chatThreadList.get(i).getIs_muted() == null){
                                    chatThreadList.remove(i);
                                }
                            }
                            try {
                                SortingUtils.sortChatByDate(chatThreadList);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            chatThreadList.add(0, new GetChatThread());
                            conversationAdapter = new ConversationAdapter(getActivity(), chatThreadList);
                            conversationBinding.recyclerViewChat.setAdapter(conversationAdapter);
                            ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
                            for (int i = 1; i < chatThreadList.size(); i++) {
                                if (chatThreadList.get(i).getReceiverId() != null) {
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(chatThreadList.get(i).getReceiverId())) {
                                        mentionModelArrayList.add(new MentionModel(chatThreadList.get(i).getReceiverId(), chatThreadList.get(i).getUserName(), chatThreadList.get(i).getUserAvatar(), chatThreadList.get(i).getReceiver_profile_id()));
                                    }
                                }
                            }
                            MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
                            conversationBinding.editTextSearch.setAdapter(mentionChatAdapter);
                            conversationBinding.editTextSearch.setThreshold(1);
                            conversationBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
                                    conversationBinding.editTextSearch.setText(conversationBinding.editTextSearch.getText());
                                    conversationBinding.constraintLayoutNotChatFound.setVisibility(View.GONE);
                                    getChatThread();
                                }
                            });
                            break;
                        case 0:
                            conversationBinding.constraintLayoutNotChatFound.setVisibility(View.VISIBLE);
                            conversationBinding.shimmerLayout.setVisibility(View.GONE);
                            conversationBinding.shimmerLayout.stopShimmerAnimation();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatThread> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                conversationBinding.shimmerLayout.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    public void getChatThreadWithOutShimmer() {
        chatThreadList.clear();
        if (conversationAdapter != null) {
            conversationAdapter.notifyDataSetChanged();
        }
        chatThreadCall = Api.WEB_SERVICE.getUserGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""), profileId);
        chatThreadCall.enqueue(new Callback<ChatThread>() {
            @Override
            public void onResponse(Call<ChatThread> call, Response<ChatThread> response) {
                conversationBinding.constraintLayoutNotChatFound.setVisibility(View.GONE);
                if (response.isSuccessful() && response != null) {
                    Log.d("datata", response.body().getGetChatThreads().toString());
                    switch (response.body().getStatus()) {
                        case 1:
                            chatThreadList.clear();
                            chatThreadList.addAll(response.body().getGetChatThreads());
                            for (int i = 0; i<chatThreadList.size(); i++){
                                if (chatThreadList.get(i).getIs_muted() == null){
                                    chatThreadList.remove(i);
                                }
                            }
                            try {
                                SortingUtils.sortChatByDate(chatThreadList);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            chatThreadList.add(0, new GetChatThread());
                            conversationAdapter = new ConversationAdapter(getActivity(), chatThreadList);
                            conversationBinding.recyclerViewChat.setAdapter(conversationAdapter);
                            ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
                            for (int i = 1; i < chatThreadList.size(); i++) {
                                if (chatThreadList.get(i).getReceiverId() != null) {
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(chatThreadList.get(i).getReceiverId())) {
                                        mentionModelArrayList.add(new MentionModel(chatThreadList.get(i).getReceiverId(), chatThreadList.get(i).getUserName(), chatThreadList.get(i).getUserAvatar(), chatThreadList.get(i).getReceiver_profile_id()));
                                    }
                                }
                            }
                            MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
                            conversationBinding.editTextSearch.setAdapter(mentionChatAdapter);
                            conversationBinding.editTextSearch.setThreshold(1);
                            conversationBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
                                    conversationBinding.editTextSearch.setText(conversationBinding.editTextSearch.getText());
                                    conversationBinding.constraintLayoutNotChatFound.setVisibility(View.GONE);
                                    getChatThread();
                                }
                            });
                            break;
                        case 0:
                            conversationBinding.constraintLayoutNotChatFound.setVisibility(View.VISIBLE);
                            conversationBinding.shimmerLayout.setVisibility(View.GONE);
                            conversationBinding.shimmerLayout.stopShimmerAnimation();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatThread> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                conversationBinding.shimmerLayout.setVisibility(View.GONE);
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }
            }
        });
    }

    private void reqMuteChat(String fromUserId, String forUserId, int muteType, String muteTime, int position, String frormProfileid, String forFrofileid) {
        muteChatMainObjectCall = Api.WEB_SERVICE.muteChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), fromUserId, forUserId, muteType, muteTime, frormProfileid, forFrofileid);
        muteChatMainObjectCall.enqueue(new Callback<MuteChatMainObject>() {
            @Override
            public void onResponse(Call<MuteChatMainObject> call, Response<MuteChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            chatThreadList.get(position).setIs_muted("muted");
                            conversationAdapter.notifyItemChanged(position);
                            ((MainActivity) getActivity()).showMessageDialog("success", "User Muted Successfully");
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", "Error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<MuteChatMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }

            }
        });
    }

    private void reqUnMuteChat(String fromUserId, String forUserId, int position, String fromProfileId, String forProfileId) {
        unMuteChatMainObjectCall = Api.WEB_SERVICE.unMuteChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), fromUserId, forUserId, fromProfileId, forProfileId);
        unMuteChatMainObjectCall.enqueue(new Callback<UnMuteChatMainObject>() {
            @Override
            public void onResponse(Call<UnMuteChatMainObject> call, Response<UnMuteChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            chatThreadList.get(position).setIs_muted("");
                            conversationAdapter.notifyItemChanged(position);
                            ((MainActivity) getActivity()).showMessageDialog("success", "User UnMuted Successfully");
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", "Error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UnMuteChatMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }

            }
        });
    }

    private void reqMuteGroupChat(String groupThreadId, String userIdMuteBy, int muteType, String muteTime, int position, String userProfileIdMuteBy) {
        muteGroupChatMainObjectCall = Api.WEB_SERVICE.muteGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), groupThreadId, userIdMuteBy, muteType, muteTime, userProfileIdMuteBy);
        muteGroupChatMainObjectCall.enqueue(new Callback<MuteGroupChatMainObject>() {
            @Override
            public void onResponse(Call<MuteGroupChatMainObject> call, Response<MuteGroupChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            chatThreadList.get(position).setIs_muted("muted");
                            conversationAdapter.notifyItemChanged(position);
                            ((MainActivity) getActivity()).showMessageDialog("success", "Group Muted Successfully");
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", "Error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<MuteGroupChatMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }

            }
        });
    }

    private void reqUnMuteGroupChat(String groupThreadId, String userIdMuteBy, int position, String userProfileIdMuteBy) {
        unMuteGroupChatMainObjectCall = Api.WEB_SERVICE.unmuteGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), groupThreadId, userIdMuteBy, userProfileIdMuteBy);
        unMuteGroupChatMainObjectCall.enqueue(new Callback<UnMuteGroupChatMainObject>() {
            @Override
            public void onResponse(Call<UnMuteGroupChatMainObject> call, Response<UnMuteGroupChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            chatThreadList.get(position).setIs_muted("");
                            conversationAdapter.notifyItemChanged(position);
                            ((MainActivity) getActivity()).showMessageDialog("success", "User UnMuted Successfully");
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", "Error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UnMuteGroupChatMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Network connection was lost");
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusMuteChat(EventBusMuteChat eventBusMuteChat) {
        int muteType = eventBusMuteChat.getMuteType();
        if (chatThreadList.get(eventBusMuteChat.getmPos()).getGroupchat_thread_id() != null) {
            String UserIdMuteBy = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
            String muted = chatThreadList.get(eventBusMuteChat.getmPos()).getIs_muted();
            if (muted != null && muted.equals("muted")) {
                reqUnMuteGroupChat(chatThreadList.get(eventBusMuteChat.getmPos()).getGroupchat_thread_id(), UserIdMuteBy, eventBusMuteChat.getmPos(), profileId);
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

                try {
                    Date date1 = simpleDateFormat.parse("23/10/2020 23:59:59");
                    Date date = new Date();
                    String time = simpleDateFormat.format(date);
                    Date date2 = simpleDateFormat.parse(time);

                    String hms = printDifference(date2, date1);
                    reqMuteGroupChat(chatThreadList.get(eventBusMuteChat.getmPos()).getGroupchat_thread_id(), UserIdMuteBy, muteType, hms, eventBusMuteChat.getmPos(), profileId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            String fromUserID = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
            String forUserID = null;
            String forProfielId = null;
            if (chatThreadList.get(eventBusMuteChat.getmPos()).getSenderId().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                forUserID = chatThreadList.get(eventBusMuteChat.getmPos()).getReceiverId();
                forProfielId = chatThreadList.get(eventBusMuteChat.getmPos()).getReceiver_profile_id();
            } else {
                forUserID = chatThreadList.get(eventBusMuteChat.getmPos()).getSenderId();
                forProfielId = chatThreadList.get(eventBusMuteChat.getmPos()).getSender_profile_id();
            }
            if (chatThreadList.get(eventBusMuteChat.getmPos()).getIs_muted().equals("muted")) {
                reqUnMuteChat(fromUserID, forUserID, eventBusMuteChat.getmPos(), profileId, forProfielId);
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

                try {
                    Date date1 = simpleDateFormat.parse("23/10/2020 23:59:59");
                    Date date = new Date();
                    String time = simpleDateFormat.format(date);
                    Date date2 = simpleDateFormat.parse(time);

                    String hms = printDifference(date2, date1);
                    reqMuteChat(fromUserID, forUserID, muteType, hms, eventBusMuteChat.getmPos(), profileId, forProfielId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String printDifference(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds;
    }

    //    Event bus method triggers when user filters are updated....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserFilters(EventBusUpdateFilters eventBusUpdateFilters) {
        if (getActivity() != null) {
            toggleFilterButtonBackground();
            getChatThread();
        }
    }

    //method to set the background of filter button
    private void toggleFilterButtonBackground() {
        chatThreadList.clear();
        String filters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
        if (filters != null)
            if (filters.isEmpty()) {
                conversationBinding.buttonFilter.setVisibility(View.VISIBLE);
//                conversationBinding.horizontalScrollViewChatFilters.setVisibility(View.GONE);
            } else {
                if (layoutInflater == null) {
                    layoutInflater = LayoutInflater.from(getContext());
                }
                List<String> filterList = new ArrayList<>(Arrays.asList(filters.split(",")));
                if (filterList.size() > 0) {
                    for (int i = 0; i < filterList.size(); i++) {
                        View filterView = layoutInflater.inflate(R.layout.row_filter, null);
                        TextView textViewFilter = filterView.findViewById(R.id.textViewFilterRow);
                        textViewFilter.setText(filterList.get(i));
                        ImageView imageViewCloseFilter = filterView.findViewById(R.id.imageViewCloseFilter);
                        int finalI = i;
                        imageViewCloseFilter.setOnClickListener(view -> {
                            filterList.remove(textViewFilter.getText().toString());
                            String filter = filterList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "");
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, filter).apply();
                            EventBus.getDefault().post(new EventBusUpdateFilters());
                            EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                            if (filterList.size() == 0) {
                                conversationBinding.buttonFilter.setVisibility(View.VISIBLE);
//                                conversationBinding.horizontalScrollViewChatFilters.setVisibility(View.GONE);
                            }
                        });
                    }
                }
//                conversationBinding.horizontalScrollViewChatFilters.setVisibility(View.VISIBLE);
                conversationBinding.buttonFilter.setVisibility(View.INVISIBLE);
            }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (chatThreadCall != null && chatThreadCall.isExecuted()) {
            chatThreadCall.cancel();
        }
        if (muteChatMainObjectCall != null && muteChatMainObjectCall.isExecuted()) {
            muteChatMainObjectCall.cancel();
        }
        if (unMuteChatMainObjectCall != null && unMuteChatMainObjectCall.isExecuted()) {
            unMuteChatMainObjectCall.cancel();
        }
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
            conversationBinding.imageViewClose.setVisibility(View.GONE);
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
            toggleFilterButtonBackground();
            EventBus.getDefault().post(new EventBusUserSession("msg_search"));
            getChatThread();
        } else {
            conversationBinding.imageViewClose.setVisibility(View.VISIBLE);
        }
        LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, s.toString()).apply();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
            slideUp.hide();
            getChatThread();
            return true;
        }
        return false;
    }

    public class ConversationClickHandler {

//        public void onFilterClick(View view) {
//            ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
//            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.CONVERSATION_FILTER, true, null);
//        }

        public void onSearchClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
            getChatThread();
        }

        public void onCloseClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
            LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "");
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "");
            LnqApplication.getInstance().editor.apply();
            conversationBinding.editTextSearch.setText("");
            toggleFilterButtonBackground();
            conversationBinding.constraintLayoutNotChatFound.setVisibility(View.GONE);
            ValidUtils.hideKeyboardFromFragment(getActivity(), conversationBinding.getRoot());
        }

        public void onNewChatClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd("NEW_MESSAGE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("new_msg_clicked"));
        }

        public void onSearchAreaClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragReplace("HOME", true, null);

        }

        public void onYourConnectionClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragReplace("CONTACTS", true, null);
        }

        public void onQrCodeClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
        }

        public void onVisibilityClick(View view) {
            ((MainActivity) getActivity()).mFScreenName = Constants.VISIBLE;
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VISIBLE, true, null);
            EventBus.getDefault().post(new EventBusUserSession("visibility_view"));
        }

        public void onSettingClick(View view) {
            ((MainActivity) getActivity()).mFScreenName = Constants.SETTING;
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SETTING, true, null);
            EventBus.getDefault().post(new EventBusUserSession("setting_view"));
        }

        public void onStatusMessageClick(View view) {
            ((MainActivity) getActivity()).mFScreenName = Constants.LOOKING_FOR;
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOOKING_FOR, true, null);
            EventBus.getDefault().post(new EventBusUserSession("status_view"));
        }
    }
}