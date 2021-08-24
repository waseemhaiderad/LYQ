package lnq.com.lnq.fragments.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordPermissionHandler;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.GPHSettings;
import com.giphy.sdk.ui.themes.GPHTheme;
import com.giphy.sdk.ui.themes.GridType;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ChatAdapter;
import lnq.com.lnq.adapters.ChooseImageAdapter;
import lnq.com.lnq.adapters.SharedUserProfileAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.image_compressor.Luban;
import lnq.com.lnq.custom.image_compressor.OnCompressListener;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.databinding.FragmentChatBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.gallery.GalleryFragmentNew;
import lnq.com.lnq.listeners.activit_listeners.SwipeDetector;
import lnq.com.lnq.model.attachements.SendAttachementsModel;
import lnq.com.lnq.model.attachements.SendMultipleAttachementsModel;
import lnq.com.lnq.model.attachements.resendattachments.ReSendAttachementsModel;
import lnq.com.lnq.model.event_bus_models.EventBusBlockedUnBlocked;
import lnq.com.lnq.model.event_bus_models.EventBusChatGestures;
import lnq.com.lnq.model.event_bus_models.EventBusChatShareContactClick;
import lnq.com.lnq.model.event_bus_models.EventBusChatThreadClick;
import lnq.com.lnq.model.event_bus_models.EventBusCheckPermission;
import lnq.com.lnq.model.event_bus_models.EventBusCloseChatImageLayout;
import lnq.com.lnq.model.event_bus_models.EventBusGetChatImagePath;
import lnq.com.lnq.model.event_bus_models.EventBusSaveChatImagesToGallery;
import lnq.com.lnq.model.event_bus_models.EventBusSharedProfileItemClick;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChat;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatCount;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateMessages;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.EventBussFailedMsg;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnections;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatData;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.MuteChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.UnMuteChatMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.ChatThread;
import lnq.com.lnq.model.gson_converter_models.conversation.GetChatThread;
import lnq.com.lnq.model.gson_converter_models.conversation.ShareProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.gson_converter_models.send_message.SendMessageMainObject;
import lnq.com.lnq.model.userprofile.GetUserProfileData;
import lnq.com.lnq.model.userprofile.GetUserProfileMainObject;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class FragmentChat extends Fragment implements KeyboardVisibilityEventListener, CompoundButton.OnCheckedChangeListener {

    //    Android fields....
    private FragmentChatBinding chatBinding;
    private ChatClickHandler clickHandler;
    private Unregistrar unregistrar;
    Handler handler;
    Runnable runnable;
    Dialog dialog;
    private SharedUserProfileAdapter sharedUserProfileAdapter;
    BottomSheetDialogFragment myBottomSheet;
    private AudioRecorder audioRecorder;
    private File recordFile;
    private MediaPlayer myPlayer;
    private TransferUtility transferUtility;
    private String cachePath = "";

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private String userId, usermessage, userName, userImage, isFavorite, isConnected, threadId = "", isBlocked = "";
    private String unreadMessageCount = "";
    private List<GetChatData> chatDataList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListImported = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListLNQ = new ArrayList<>();
    private List<UserConnections> userContactList = new ArrayList<>();
    private String profileId;
    private String recevierProfileId;

    //    Retrofit fields....
    private Call<SendMessageMainObject> callSendMessage;
    private Call<SendMessageMainObject> callReSendMessage;
    private Call<GetChatMainObject> callGetChat;
    private Call<RegisterLoginMainObject> callBlockWhiteStatus;
    private Call<GetUserProfileMainObject> callGetUserProfile;
    private Call<MuteChatMainObject> muteChatMainObjectCall;
    private Call<UnMuteChatMainObject> unMuteChatMainObjectCall;


    //    Retrofit Call....
    private Call<ChatThread> chatThreadCall;
    private Call<SendAttachementsModel> sendAttachementsModelCall;
    private Call<SendAttachementsModel> sendVoiceAttachementsModelCall;
    private Call<SendMultipleAttachementsModel> sendMultipleAttachementsModelCall;
    private Call<ReSendAttachementsModel> reSendAttachementsModelCall;
    private Call<UserConnectionsMainObject> callUserConnections;
    private Call<ShareProfileMainObject> shareProfileMainObjectCall;


    //    Instance fields...
    List<GetChatThread> chatThreadList = new ArrayList<>();
    private AppCompatImageView imageViewBackTopBar, imageViewDropdownContacts;
    TextView textViewHeading;
    AppCompatTextView imageViewSearchTopBar;

    //    Adapter fields....
    private ChatAdapter chatAdapter;
    ChooseImageAdapter chooseImageAdapter;
    File imagePath;
    ArrayList<File> files = new ArrayList<>();
    boolean imageisChecked = false;
    private SlideUp slideUp;
    private BottomSheetDialog bottomSheet;


    public FragmentChat() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        return chatBinding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                List<String> result = (List<String>) bundle.getSerializable("bundleKey");
                System.out.println("kuch");
                Luban.with(getActivity())
                        .load(result)
                        .ignoreBy(100)
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                imagePath = file;
                                files.add(file);
                                chooseImageAdapter = new ChooseImageAdapter(getActivity(), files);
                                chatBinding.recyclerViewChatImages.setAdapter(chooseImageAdapter);
                                imageisChecked = true;
                                if (imageisChecked) {
                                    chatBinding.imageButtonSendMessage.setClickable(true);
                                    chatBinding.imglayout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }).launch();

                myBottomSheet.dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.VISIBLE);
        CardView topBarLayout = chatBinding.tobBar.topBarCardViewChat;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewSearchTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), chatBinding.getRoot());
                EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                getActivity().onBackPressed();
            }
        });
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), chatBinding.getRoot());
                EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                getActivity().onBackPressed();
            }
        });
        init();
        createTransferUtility();
        cachePath = getContext().getCacheDir().getAbsolutePath();
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity().getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, getActivity().getApplicationContext());
    }

    private void init() {
//        Registering event bus....
        EventBus.getDefault().register(this);

        profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
//        Setting custom font....
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setEditTextRegularFont(chatBinding.editTextMessage);

//        Setting layout manager for recycler view....
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        chatBinding.recyclerViewChat.setLayoutManager(linearLayoutManager);
        chatBinding.recyclerViewChat.setItemAnimator(new DefaultItemAnimator());

        chatBinding.recyclerViewChatImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        chatBinding.recyclerViewChatImages.setItemAnimator(new DefaultItemAnimator());

        reqContacts("", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));

        //        Getting userId from bundle....
        if (getArguments() != null) {
            userId = getArguments().getString(EndpointKeys.USER_ID, "");
            LnqApplication.getInstance().editor.putString(EndpointKeys.SENDER_ID, userId).apply();
            if (getArguments().getString("mFlag", "").equals("chat")) {
                recevierProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
                reqGetUserProfile();
            } else {
                userName = getArguments().getString(EndpointKeys.USER_NAME, "");
                userImage = getArguments().getString(EndpointKeys.USER_AVATAR, "");
                isConnected = getArguments().getString(EndpointKeys.USER_CONNECTION_STATUS, "");
                isFavorite = getArguments().getString(EndpointKeys.IS_FAVORITE, "");
                threadId = getArguments().getString(EndpointKeys.THREAD_ID, "");
                isBlocked = getArguments().getString(EndpointKeys.IS_BLOCK, "");
                unreadMessageCount = getArguments().getString(EndpointKeys.CHAT_UNREAD_COUNT);
                profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
                recevierProfileId = getArguments().getString(EndpointKeys.PROFILE_ID);
                textViewHeading.setText(userName);
                chatBinding.textViewUserName.setText(userName);
                showMessages();
            }

            String draftProfileId = "";
            if (LnqApplication.getInstance().draftHasMap.containsKey(threadId)) {
                draftProfileId = LnqApplication.getInstance().draftHasMap.get(threadId);
            }
            chatBinding.editTextMessage.setText(draftProfileId);

            chatBinding.editTextMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!chatBinding.editTextMessage.getText().toString().isEmpty()) {
//                        chatBinding.imageButtonSendMessage.setBackgroundResource(R.drawable.bg_circle_blue);
                        chatBinding.imageButtonSendMessage.setClickable(true);
                    } else {
//                        chatBinding.imageButtonSendMessage.setBackgroundResource(R.drawable.circle_bg_grey);
                        chatBinding.imageButtonSendMessage.setClickable(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
//        Setting click handler fot data binding....
        clickHandler = new ChatClickHandler();
        chatBinding.setClickHandler(clickHandler);

//        All event listeners....
        unregistrar = KeyboardVisibilityEvent.registerEventListener(getActivity(), this);
        chatBinding.editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (!ValidUtils.isNetworkAvailable(getActivity())) {
                        return true;
                    }
                    if (!chatBinding.editTextMessage.getText().toString().isEmpty()) {
                        reqSendMessage(chatBinding.editTextMessage.getText().toString(), profileId, recevierProfileId);
                    }
                    return true;
                }
                return false;
            }
        });

        chatBinding.recyclerViewChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValidUtils.hideKeyboardFromFragment(getContext(), chatBinding.getRoot());
                return false;
            }
        });
        usermessage = chatBinding.editTextMessage.getText().toString();
        chatBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
//                    chatBinding.searchBarLayout.setVisibility(View.GONE);
                }
            }
        });

        slideUp = new SlideUpBuilder(chatBinding.slideView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            chatBinding.tobBar.topBarCardViewChat.setVisibility(View.VISIBLE);
                            chatBinding.viewHideTopBar.setVisibility(View.GONE);
//                            chatBinding.searchBarLayout.setVisibility(View.GONE);
                        } else {
                            chatBinding.tobBar.topBarCardViewChat.setVisibility(View.GONE);
                            chatBinding.viewHideTopBar.setVisibility(View.VISIBLE);
//                            chatBinding.searchBarLayout.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(chatBinding.viewScroll)
                .build();

        audioRecorder = new AudioRecorder();

        //IMPORTANT
        chatBinding.recordButton.setRecordView(chatBinding.recordView);
        chatBinding.recordButton.setListenForRecord(true);

        //ListenForRecord must be false ,otherwise onClick will not be called
        chatBinding.recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
            }
        });

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        chatBinding.recordView.setCancelBounds(8);
        chatBinding.recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        chatBinding.recordView.setLessThanSecondAllowed(false);
        chatBinding.recordView.setSlideToCancelText("Slide To Cancel");
        chatBinding.recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);

        chatBinding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if (((MainActivity) getActivity()).fnCheckStoragePermission()) {
                    chatBinding.view3.setVisibility(View.VISIBLE);
//                recordFile = new File(getActivity().getFilesDir(), UUID.randomUUID().toString() + ".3gp");
                    String filepath = Environment.getExternalStorageDirectory().getPath();
                    recordFile = new File(filepath, "LNQ");
                    if (!recordFile.exists()) {
                        recordFile.mkdirs();
                    }
                    recordFile = new File(recordFile.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3");
                    try {
                        audioRecorder.start(recordFile.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ((MainActivity) getActivity()).fnRequestStoragePermission(2);
                }
//                Toast.makeText(getContext(), "OnStartRecord", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                stopRecording(true);
                chatBinding.view3.setVisibility(View.GONE);
                Toast.makeText(getContext(), "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                stopRecording(false);
                String time = getHumanTimeText(recordTime);
                chatBinding.view3.setVisibility(View.GONE);
//                Toast.makeText(getContext(), "onFinishRecord - Recorded Time is: " + time + " File saved at " + recordFile.getPath(), Toast.LENGTH_SHORT).show();
                sendVoiceAttachment(threadId, profileId, recevierProfileId, recordFile);
            }

            @Override
            public void onLessThanSecond() {
                stopRecording(true);
                chatBinding.view3.setVisibility(View.GONE);
                Toast.makeText(getContext(), "OnLessThanSecond", Toast.LENGTH_SHORT).show();
            }
        });

        chatBinding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {

            }
        });

        chatBinding.recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }
                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }
                ActivityCompat.
                        requestPermissions(getActivity(),
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                0);
                return false;
            }
        });

    }

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }


    private void showMessages() {
        if (!userId.isEmpty()) {
            //        Setting empty adapter....
            chatDataList.clear();
            if (chatAdapter != null) {
                chatAdapter.notifyDataSetChanged();
            }
            reqGetChat();
            chatAdapter = new ChatAdapter(getActivity(), chatDataList, userImage, isConnected, isFavorite);
            chatBinding.recyclerViewChat.setAdapter(chatAdapter);
        }
    }

    @Override
    public void onVisibilityChanged(boolean isOpen) {
        if (isOpen) {
            ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (((MainActivity) getActivity()).mBind != null)
                        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.VISIBLE);
                }
            }, 50);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusClickEventOfChat(EventBusChatThreadClick eventBusChatThreadClick) {
        String userID = null;
        if (chatDataList.get(eventBusChatThreadClick.getmPos()).getsender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            userID = chatDataList.get(eventBusChatThreadClick.getmPos()).getreceiver_id();
        } else {
            userID = chatDataList.get(eventBusChatThreadClick.getmPos()).getsender_id();
        }
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userID);
        bundle.putString("topBar", "messages");
        bundle.putString(EndpointKeys.PROFILE_ID, recevierProfileId);
        ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusClickShareContactClick(EventBusChatShareContactClick eventBusChatShareContactClick) {
        String userID = eventBusChatShareContactClick.getUserId();
        String userProfileID = eventBusChatShareContactClick.getUserProfileId();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userID);
        bundle.putString("topBar", "messages");
        bundle.putString(EndpointKeys.PROFILE_ID, userProfileID);
        ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateChatEvent(GetChatData getChatData) {
        if (getChatData.getsender_id().equals(userId)) {
//            threadId = getChatData.getThread_id();
            chatDataList.add(getChatData);
            chatAdapter.notifyItemInserted(chatDataList.size() - 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBlockUnBlock(EventBusBlockedUnBlocked eventBusBlockedUnBlocked) {
        if (eventBusBlockedUnBlocked.getIsBlocked().equals("blocked"))
            isBlocked = "blocked";
        else
            isBlocked = "";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getFailedMsg(EventBussFailedMsg eventBussFailedMsg) {
        if (isBlocked.equals("blocked"))
            showDialogBoxUnBlock();
        else
            showDialogBox(eventBussFailedMsg.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMessages(EventBusUpdateMessages eventBusUpdateMessages) {
        showMessages();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusCloseChatImageLayout(EventBusCloseChatImageLayout eventBusCloseChatImageLayout) {
        if (files.size() == 0) {
            chatBinding.imglayout.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSaveChatImagesToGallery(EventBusSaveChatImagesToGallery mObj) {
        bottomSheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cus_bottomsheet_saveimages, null);
        AppCompatTextView saveImage = view.findViewById(R.id.saveImage);
        AppCompatTextView cancel = view.findViewById(R.id.cancel);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToGallery(mObj.getImagePath());
                bottomSheet.hide();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.hide();
            }
        });
        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    private void saveImageToGallery(Bitmap finalBitmap) {
        if (checkStoragePermission()) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/LNQ Chat");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fName = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FNAME, "");
            String fname = fName + n + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                Toast.makeText(getActivity(), "Your chat image has been saved to your camera roll.", Toast.LENGTH_SHORT).show();
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(getContext(), new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
    }

    public boolean checkStoragePermission() {
        return ((MainActivity) getActivity()).fnCheckPermission();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String fromUserID = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
        if (buttonView == chatBinding.switchMuteUnMute) {
            if (isChecked) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                try {
                    Date date1 = simpleDateFormat.parse("23/10/2020 23:59:59");
                    Date date = new Date();
                    String time = simpleDateFormat.format(date);
                    Date date2 = simpleDateFormat.parse(time);

                    String hms = printDifference(date2, date1);
                    reqMuteChat(fromUserID, userId, 3, hms, profileId, recevierProfileId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                reqUnMuteChat(fromUserID, userId, profileId, recevierProfileId);
            }
        } else if (buttonView == chatBinding.switchBlockUnBlock) {
            if (isChecked) {
                reqBlockUnblock(Constants.BLOCK, profileId, recevierProfileId);
            } else {
                reqBlockUnblock(Constants.UNBLOCK, profileId, recevierProfileId);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.VISIBLE);
        if (unregistrar != null) {
            unregistrar.unregister();
        }
        if (callSendMessage != null && callSendMessage.isExecuted()) {
            callSendMessage.cancel();
        }
        if (callGetChat != null && callGetChat.isExecuted()) {
            callGetChat.cancel();
        }
        if (callReSendMessage != null && callReSendMessage.isExecuted()) {
            callReSendMessage.cancel();
        }
        if (chatThreadCall != null && chatThreadCall.isExecuted()) {
            chatThreadCall.cancel();
        }
        LnqApplication.getInstance().draftHasMap.put(threadId, chatBinding.editTextMessage.getText().toString());
    }

    public void showDialogBox(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Resend Message");
        builder.setMessage("Do you want to send message again?");
        builder.setPositiveButton("Resend Message", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                reSendMessage(threadId, chatDataList.get(position).getMsg_id(), position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogBoxUnBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You cannot message with a blocked user. Do you want to unblock " + userName);
        builder.setPositiveButton("Yes, unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                reqBlockUnblock(Constants.UNBLOCK, profileId, recevierProfileId);
            }
        });

        builder.setNegativeButton("No, keep them blocked", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reqGetChat() {
        chatBinding.shimmerLayoutChatThread.setVisibility(View.VISIBLE);
        chatBinding.shimmerLayoutChatThread.startShimmerAnimation();
        callGetChat = Api.WEB_SERVICE.getChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, profileId, recevierProfileId);
        callGetChat.enqueue(new Callback<GetChatMainObject>() {
            @Override
            public void onResponse(Call<GetChatMainObject> call, Response<GetChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    chatBinding.shimmerLayoutChatThread.setVisibility(View.GONE);
                    chatBinding.shimmerLayoutChatThread.stopShimmerAnimation();
                    switch (response.body().getStatus()) {
                        case 1:
                            threadId = response.body().getThread_id();
                            int lastSuccessfullMessagePosition = 0;
                            chatDataList.addAll(response.body().getGetChat());
                            SortingUtils.sortChatDataByDate(chatDataList);
                            if (unreadMessageCount != null && !unreadMessageCount.isEmpty()) {
                                for (int i = 0; i < Integer.parseInt(unreadMessageCount); i++) {
                                    chatDataList.get(chatDataList.size() - 1 - i).setMessageUnread(true);
                                }
                            }
                            for (int i = 0; i < chatDataList.size(); i++) {
                                chatAdapter.notifyItemInserted(i);
                                if (chatDataList.get(i).isSent().equals("1")) {
                                    lastSuccessfullMessagePosition = i;
                                }
                            }

                            String isChatMuted = LnqApplication.getInstance().sharedPreferences.getString(Constants.CHAT_MUTED, "");
                            if (isChatMuted != null && !isChatMuted.isEmpty()) {
                                if (isChatMuted.equalsIgnoreCase("muted")) {
                                    chatBinding.switchMuteUnMute.setChecked(true);
                                } else {
                                    chatBinding.switchMuteUnMute.setChecked(false);
                                }
                            }
                            String isBlocked = LnqApplication.getInstance().sharedPreferences.getString("blockUser", "");
                            if (isBlocked != null && !isBlocked.isEmpty()) {
                                if (isBlocked.equalsIgnoreCase("blocked")) {
                                    chatBinding.switchBlockUnBlock.setChecked(true);
                                } else {
                                    chatBinding.switchBlockUnBlock.setChecked(false);
                                }
                            }

                            chatBinding.switchMuteUnMute.setOnCheckedChangeListener(FragmentChat.this);
                            chatBinding.switchBlockUnBlock.setOnCheckedChangeListener(FragmentChat.this);

                            chatDataList.get(lastSuccessfullMessagePosition).setPosition(true);
                            chatAdapter.notifyItemChanged(lastSuccessfullMessagePosition);
                            chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    super.onItemRangeInserted(positionStart, itemCount);
                                    chatBinding.recyclerViewChat.scrollToPosition(chatDataList.size() - 1);
                                }
                            });
//                            getChatThread();
                            EventBus.getDefault().post(new EventBusUpdateChatCount(0));
//                            EventBus.getDefault().post(new EventBusUpdateChat());
                            break;
                        case 0:
                            chatBinding.shimmerLayoutChatThread.setVisibility(View.GONE);
                            chatBinding.shimmerLayoutChatThread.stopShimmerAnimation();
//                            getChatThread();
                            EventBus.getDefault().post(new EventBusUpdateChatCount(0));
//                            EventBus.getDefault().post(new EventBusUpdateChat());
                            threadId = response.body().getThread_id();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetChatMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                chatBinding.shimmerLayoutChatThread.setVisibility(View.GONE);
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
    public void eventBusSwipeGesture(EventBusChatGestures eventBusActivityGestures) {
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

    public void onLeftSwipe() {
        for (int i = 0; i < chatDataList.size(); i++) {
            chatDataList.get(i).setSwipeText(false);
            chatAdapter.notifyItemChanged(i);
        }
    }

    public void onRightSwipe() {
        for (int i = 0; i < chatDataList.size(); i++) {
            chatDataList.get(i).setSwipeText(true);
            chatAdapter.notifyItemChanged(i);
        }
    }

    private void reqSendMessage(final String message, String senderProfielid, String recevierProfileid) {
        chatBinding.editTextMessage.setText("");
        callSendMessage = Api.WEB_SERVICE.sendMessage(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, message, senderProfielid, recevierProfileid);
        callSendMessage.enqueue(new Callback<SendMessageMainObject>() {
            @Override
            public void onResponse(Call<SendMessageMainObject> call, Response<SendMessageMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body().getSendMessage() != null) {
                                threadId = response.body().getThread_id();
                                if (response.body().getSendMessage().getIs_sent() == 0) {
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, message, response.body().getSendMessage().getMessage_time(), String.valueOf(response.body().getSendMessage().getMsg_id()), String.valueOf(response.body().getSendMessage().getIs_sent()), response.body().getSendMessage().getIs_pending(), false, "", ""));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                } else {
                                    for (int i = 0; i < chatDataList.size(); i++) {
                                        chatDataList.get(i).setPosition(false);
                                    }
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, message, response.body().getSendMessage().getMessage_time(), String.valueOf(response.body().getSendMessage().getMsg_id()), String.valueOf(response.body().getSendMessage().getIs_sent()), response.body().getSendMessage().getIs_pending(), true, "", ""));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                }
                                chatAdapter.notifyDataSetChanged();
                                EventBus.getDefault().post(new EventBusUpdateChat());
                                EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
//                                EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<SendMessageMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                chatBinding.shimmerLayoutChatThread.setVisibility(View.GONE);
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

    public class ChatClickHandler {

        public void onNameClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(EndpointKeys.USER_ID, userId);
            bundle.putString(EndpointKeys.PROFILE_ID, recevierProfileId);
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, true, bundle);
        }

        public void onBackClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), chatBinding.getRoot());
            EventBus.getDefault().post(new EventBusUpdateChatCount(0));
            getActivity().onBackPressed();
        }

        public void onSettingClick(View view) {
            ((MainActivity) getActivity()).mFScreenName = Constants.SETTING;
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.SETTING, true, null);
            EventBus.getDefault().post(new EventBusUserSession("setting_view"));
        }

        public void onVisibilityClick(View view) {
            ((MainActivity) getActivity()).mFScreenName = Constants.VISIBLE;
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.VISIBLE, true, null);
            EventBus.getDefault().post(new EventBusUserSession("visibility_view"));
        }

        public void onStatusMessageClick(View view) {
            ((MainActivity) getActivity()).mFScreenName = Constants.LOOKING_FOR;
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LOOKING_FOR, true, null);
            EventBus.getDefault().post(new EventBusUserSession("status_view"));
        }

        public void onSendMessageClick(View view) {
//            if (chatBinding.progressBar.getVisibility() == View.VISIBLE) {
//                return;
//            }
            if (!ValidUtils.isNetworkAvailable(getActivity())) {
                return;
            }
            if (imageisChecked) {
                sendMutipleAttachment(files, profileId, recevierProfileId);
                chatBinding.imglayout.setVisibility(View.GONE);
            } else {
                reqSendMessage(chatBinding.editTextMessage.getText().toString(), profileId, recevierProfileId);
            }
        }

        public void onAttachmentClick(View view) {
            if (((MainActivity) getActivity()).fnCheckReadStoragePermission()) {
//                TedBottomPicker.Builder builder = TedBottomPicker.with(getActivity())
//                        .setPeekHeight(1000)
//                        .setSelectMaxCount(4)
//                        .showGalleryTile(true);
//                builder.showCamera = false;
//                builder.showMultiImage(uriList -> Luban.with(getActivity())
//                        .load(uriList)
//                        .ignoreBy(100)
//                        .setCompressListener(new OnCompressListener() {
//                            @Override
//                            public void onStart() {
//                            }
//
//                            @Override
//                            public void onSuccess(File file) {
//                                imagePath = file;
//                                files.add(file);
//                                chooseImageAdapter = new ChooseImageAdapter(getActivity(), files);
//                                chatBinding.recyclerViewChatImages.setAdapter(chooseImageAdapter);
//                                imageisChecked = true;
//                                if (imageisChecked) {
//                                    chatBinding.imageButtonSendMessage.setBackgroundResource(R.drawable.bg_circle_blue);
//                                    chatBinding.imageButtonSendMessage.setClickable(true);
//                                    chatBinding.imglayout.setVisibility(View.VISIBLE);
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                            }
//                        }).launch());

                myBottomSheet = GalleryFragmentNew.newInstance("multiple");
                myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());


            } else {
                ((MainActivity) getActivity()).fnRequestStoragePermission(10);
            }
        }

        public void onSendProfileClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.cus_dialog_shareprofilechat, null);
            RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

            sharedUserProfileAdapter = new SharedUserProfileAdapter(getActivity(), userContactsDataListLNQ);
            recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
            recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(getActivity()));

            builder.setView(dialogView);
            dialog = builder.create();
            dialog.show();

            try {
                dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

            } catch (Exception e) {

            }
        }

        public void onOpenGiphyViewClick(View view) {
            GiphyDialogFragment dialog = GiphyDialogFragment.Companion.newInstance(new GPHSettings(), "xsCX6Jx7W1qQRIlU54D0YMKc80BiclZ5", true);
//            GiphyDialogFragment dialog = new GiphyDialogFragment.newInstance(settings.copy(selectedContentType = contentType))
            dialog.setGifSelectionListener(new GiphyDialogFragment.GifSelectionListener() {
                @Override
                public void onGifSelected(@NotNull Media media, @org.jetbrains.annotations.Nullable String s, @NotNull GPHContentType gphContentType) {

                }

                @Override
                public void onDismissed(@NotNull GPHContentType gphContentType) {

                }

                @Override
                public void didSearchTerm(@NotNull String s) {

                }
            });
            dialog.show(getActivity().getSupportFragmentManager(), "gifs_dialog");

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetChatImagePath(EventBusGetChatImagePath eventBusGetChatImagePath) {
        Luban.with(getActivity())
                .load(eventBusGetChatImagePath.getImagePath())
                .ignoreBy(100)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        imagePath = new File(eventBusGetChatImagePath.getImagePath());
                        imageisChecked = true;
                        if (imageisChecked) {
                            chatBinding.imageButtonSendMessage.setClickable(true);
                            chatBinding.imglayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSharedProfile(EventBusSharedProfileItemClick eventBusSharedProfileItemClick) {
        reqShareContact(userId, eventBusSharedProfileItemClick.getUserId(), profileId, recevierProfileId, eventBusSharedProfileItemClick.getUserProfileId());
    }

    private void reqBlockUnblock(final String status, String senderProfileid, String recevierProfileid) {
        ((MainActivity) getActivity()).progressDialog.show();
        callBlockWhiteStatus = Api.WEB_SERVICE.blockUnblockLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, status, senderProfileid, recevierProfileid);
        callBlockWhiteStatus.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (status.equals("block")) {
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, Constants.BLOCK, false));
                                EventBus.getDefault().post(new EventBusBlockedUnBlocked("blocked"));
                                EventBus.getDefault().post(new EventBusUserSession("blocked_user"));
                                LnqApplication.getInstance().editor.putString("blockUser", "blocked").apply();
                                ((MainActivity) getActivity()).showMessageDialog("success", userName + " is now in your Black list.");
                            } else {
                                EventBus.getDefault().post(new EventBusBlockedUnBlocked(""));
                                EventBus.getDefault().post(new EventBusUserSession("unblocked_user"));
                                EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "unblock", false));
                                LnqApplication.getInstance().editor.putString("blockUser", "unblocked").apply();
                                ((MainActivity) getActivity()).showMessageDialog("success", userName + " is removed from your Black list.");
                            }
                            isBlocked = "";
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    public void reSendMessage(String thread_id, String msg_id, final int position) {
        callReSendMessage = Api.WEB_SERVICE.resendMessage(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), thread_id, msg_id, DateUtils.getCurrentTime());
        callReSendMessage.enqueue(new Callback<SendMessageMainObject>() {
            @Override
            public void onResponse(Call<SendMessageMainObject> call, Response<SendMessageMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    for (int i = 0; i < chatDataList.size(); i++) {
                        chatDataList.get(i).setPosition(false);
                    }
                    chatDataList.get(position).setSent("1");
                    chatDataList.get(position).setmessage_time(response.body().getResendMessage().getMessage_time());
                    chatDataList.get(position).setPosition(true);
                    SortingUtils.sortChatDataByDate(chatDataList);
                    chatAdapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new EventBusUpdateChat());
                    EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
                }
            }

            @Override
            public void onFailure(Call<SendMessageMainObject> call, Throwable t) {
                Log.i("FragmentChat", "onFailure: " + t.getMessage());
                ValidUtils.showCustomToast(getContext(), "Network connection was lost");
            }
        });
    }

//    public void getChatThread() {
//        chatThreadList.clear();
//        chatThreadCall = Api.WEB_SERVICE.getUserChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""));
//        chatThreadCall.enqueue(new Callback<ChatThread>() {
//            @Override
//            public void onResponse(Call<ChatThread> call, Response<ChatThread> response) {
//                if (response.isSuccessful() && response != null) {
//                    switch (response.body().getStatus()) {
//                        case 1:
//                            chatThreadList.addAll(response.body().getGetChatThreads());
//                            LnqApplication.getInstance().editor.putString(EndpointKeys.RECEIVER_ID, chatThreadList.get(0).getSenderId());
//                            LnqApplication.getInstance().editor.apply();
//                            int total = 0;
//                            for (int i = 0; i < chatThreadList.size(); i++) {
//                                total = total + Integer.parseInt(chatThreadList.get(i).getCount());
//                            }
//                            EventBus.getDefault().post(new EventBusUpdateChatCount(total));
//
//                            break;
//                        case 0:
//
//                            break;
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ChatThread> call, Throwable error) {
//                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
//                    return;
//                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
////                if (error != null) {
////                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
////                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
////                    } else {
////                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
////                    }
////                } else {
////                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
////                }
//            }
//        });
//    }

    private void reqGetUserProfile() {
//        callGetUserProfile = Api.WEB_SERVICE.getUserProfile(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId);
        callGetUserProfile = Api.WEB_SERVICE.getUserActiveProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, profileId, recevierProfileId);
        callGetUserProfile.enqueue(new Callback<GetUserProfileMainObject>() {
            public void onResponse(Call<GetUserProfileMainObject> call, Response<GetUserProfileMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            GetUserProfileData getUserProfileData = response.body().getGetUserProfile();
                            userName = getUserProfileData.getUser_fname() + " " + getUserProfileData.getUser_lname();
                            userImage = getUserProfileData.getUser_avatar();
                            isConnected = getUserProfileData.getIs_connection();
                            isFavorite = getUserProfileData.getIs_favorite();
                            threadId = getArguments().getString(EndpointKeys.THREAD_ID, "");
                            isBlocked = getUserProfileData.getIs_blocked();
                            textViewHeading.setText(userName);
                            showMessages();
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                chatBinding.shimmerLayoutChatThread.setVisibility(View.GONE);
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

    private MultipartBody.Part prepareFilePart(String partName, String fileUri) {
        File file = new File(fileUri);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }

    public void sendMutipleAttachment(ArrayList<File> file, String senderProfileid, String recevierProfileid) {
        imageisChecked = false;
        usermessage = chatBinding.editTextMessage.getText().toString();
        chatBinding.editTextMessage.setText("");
        List<MultipartBody.Part> filePart = new ArrayList<>();
        final RequestBody sender_id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        final RequestBody receiver_id = RequestBody.create(MediaType.parse("text/plain"), userId);
        final RequestBody sender_profile_id = RequestBody.create(MediaType.parse("text/plain"), senderProfileid);
        final RequestBody receiver_profile_id = RequestBody.create(MediaType.parse("text/plain"), recevierProfileid);
        final RequestBody addMessage = RequestBody.create(MediaType.parse("text/plain"), chatBinding.editTextMessage.getText().toString());
        for (int i = 0; i < files.size(); i++) {
            filePart.add(prepareFilePart("attachment[" + i + "]", file.get(i).getAbsolutePath()));
        }
        sendAttachementsModelCall = Api.WEB_SERVICE.sendMultipleAttachment(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                filePart,
                sender_id,
                addMessage,
                receiver_id,
                sender_profile_id,
                receiver_profile_id);
        sendAttachementsModelCall.enqueue(new Callback<SendAttachementsModel>() {
            @Override
            public void onResponse(Call<SendAttachementsModel> call, Response<SendAttachementsModel> response) {
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (chooseImageAdapter != null) {
                                files.clear();
                                chooseImageAdapter.notifyDataSetChanged();
                            }
                            if (response.body().getSendAttachment() != null) {
                                threadId = response.body().getThreadId();
                                if (response.body().getSendAttachment().getIsSent() == 0) {
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, usermessage, response.body().getSendAttachment().getMessageTime(), String.valueOf(response.body().getSendAttachment().getMsgId()), String.valueOf(response.body().getSendAttachment().getIsSent()), String.valueOf(response.body().getSendAttachment().getIsPending()), false, response.body().getSendAttachment().getAttachment(), ""));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                } else {
                                    for (int i = 0; i < chatDataList.size(); i++) {
                                        chatDataList.get(i).setPosition(false);
                                    }
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, usermessage, response.body().getSendAttachment().getMessageTime(), String.valueOf(response.body().getSendAttachment().getMsgId()), String.valueOf(response.body().getSendAttachment().getIsSent()), String.valueOf(response.body().getSendAttachment().getIsPending()), true, response.body().getSendAttachment().getAttachment(), ""));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                }
                                chatAdapter.notifyDataSetChanged();
                                EventBus.getDefault().post(new EventBusUpdateChat());
                                EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<SendAttachementsModel> call, Throwable error) {
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
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    public void sendVoiceAttachment(String threadId, String senderProfileid, String recevierProfileid, File file) {
        imageisChecked = false;
        usermessage = chatBinding.editTextMessage.getText().toString();
        chatBinding.editTextMessage.setText("");
//        List<MultipartBody.Part> filePart = new ArrayList<>();
        final RequestBody sender_id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        final RequestBody receiver_id = RequestBody.create(MediaType.parse("text/plain"), userId);
        final RequestBody chatThreadId = RequestBody.create(MediaType.parse("text/plain"), threadId);
        final RequestBody sender_profile_id = RequestBody.create(MediaType.parse("text/plain"), senderProfileid);
        final RequestBody receiver_profile_id = RequestBody.create(MediaType.parse("text/plain"), recevierProfileid);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("voice_attachment", file.getName(), RequestBody.create(MediaType.parse("/mp3"), file));
        sendVoiceAttachementsModelCall = Api.WEB_SERVICE.sendVoiceAttachement(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                chatThreadId,
                sender_id,
                receiver_id,
                sender_profile_id,
                receiver_profile_id,
                filePart);
        sendVoiceAttachementsModelCall.enqueue(new Callback<SendAttachementsModel>() {
            @Override
            public void onResponse(Call<SendAttachementsModel> call, Response<SendAttachementsModel> response) {
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body().getSendVoiceAttachement().getVoice_attachment() != null) {
                                if (response.body().getSendVoiceAttachement().getIsSent() == 0) {
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, "", "", "", String.valueOf(response.body().getSendVoiceAttachement().getIsSent()), String.valueOf(response.body().getSendVoiceAttachement().getIsPending()), false, "", response.body().getSendVoiceAttachement().getVoice_attachment()));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                } else {
                                    for (int i = 0; i < chatDataList.size(); i++) {
                                        chatDataList.get(i).setPosition(false);
                                    }
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, "", "", "", String.valueOf(response.body().getSendVoiceAttachement().getIsSent()), String.valueOf(response.body().getSendVoiceAttachement().getIsPending()), false, "", response.body().getSendVoiceAttachement().getVoice_attachment()));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                }
                                chatAdapter.notifyDataSetChanged();
                                EventBus.getDefault().post(new EventBusUpdateChat());
                                EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
//                                EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<SendAttachementsModel> call, Throwable error) {
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
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }


    private void reqContacts(String searchKey, String searchFilter) {
        userContactsDataList.clear();
        userContactsDataListLNQ.clear();
        userContactsDataListImported.clear();
        callUserConnections = Api.WEB_SERVICE.contacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
                sharedPreferences.getString(EndpointKeys.ID, ""), searchKey, searchFilter, profileId);
        callUserConnections.enqueue(new Callback<UserConnectionsMainObject>() {
            public void onResponse(Call<UserConnectionsMainObject> call, Response<UserConnectionsMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            userContactList = response.body().getUserContacts();
                            for (int i = 0; i < userContactList.size(); i++) {
                                UserConnections userContact = userContactList.get(i);
                                if (!userContact.getUser_data().getUser_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")))
                                    if (userContact.getUser_data().getContact_status().equals("not_lnqed")) {
                                        userContactsDataListImported.add(userContact.getUser_data());
                                    } else {
                                        userContactsDataListLNQ.add(userContact.getUser_data());
                                    }
                            }
                            userContactsDataList.addAll(userContactsDataListLNQ);
                            userContactsDataList.addAll(userContactsDataListImported);
                            for (int i = 0; i < userContactsDataListImported.size(); i++) {
                                UserConnectionsData userContactsData = userContactsDataListImported.get(i);
                                if (userContactsData.getUser_fname() == null && userContactsData.getUser_lname() == null) {
                                    userContactsData.setUser_fname("");
                                    userContactsData.setUser_lname("");
                                }
                                if (userContactsData.getUser_current_position() == null) {
                                    userContactsData.setUser_current_position("");
                                }
                                if ((userContactsData.getUser_fname().trim() + userContactsData.getUser_lname().trim()).isEmpty()) {
                                    if (!userContactsData.getContact_name().isEmpty()) {
                                        userContactsData.setUser_fname(userContactsData.getContact_name());
                                    } else if (!userContactsData.getUser_phone().isEmpty()) {
                                        userContactsData.setUser_fname(userContactsData.getUser_phone());
                                    } else {
                                        userContactsData.setUser_fname(userContactsData.getUser_email());
                                    }
                                }
                            }
                            break;
                        case 0:
                            if (response.body().getMessage() != null) {
                                if (response.body().getMessage().equalsIgnoreCase("No contacts found")) {
                                }
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserConnectionsMainObject> call, Throwable error) {
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
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    public void reqShareContact(String reciverId, String userId, String senderProfileid, String recevierProfileid, String userProfileid) {
        shareProfileMainObjectCall = Api.WEB_SERVICE.sendShareContact(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), reciverId, userId, senderProfileid, recevierProfileid, userProfileid);
        shareProfileMainObjectCall.enqueue(new Callback<ShareProfileMainObject>() {
            @Override
            public void onResponse(Call<ShareProfileMainObject> call, Response<ShareProfileMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            dialog.dismiss();
                            showMessages();
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ShareProfileMainObject> call, Throwable t) {

            }
        });
    }

    private void reqMuteChat(String fromUserId, String forUserId, int muteType, String muteTime, String frormProfileid, String forFrofileid) {
        muteChatMainObjectCall = Api.WEB_SERVICE.muteChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), fromUserId, forUserId, muteType, muteTime, frormProfileid, forFrofileid);
        muteChatMainObjectCall.enqueue(new Callback<MuteChatMainObject>() {
            @Override
            public void onResponse(Call<MuteChatMainObject> call, Response<MuteChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(Constants.CHAT_MUTED, "muted").apply();
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

    private void reqUnMuteChat(String fromUserId, String forUserId, String fromProfileId, String forProfileId) {
        unMuteChatMainObjectCall = Api.WEB_SERVICE.unMuteChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), fromUserId, forUserId, fromProfileId, forProfileId);
        unMuteChatMainObjectCall.enqueue(new Callback<UnMuteChatMainObject>() {
            @Override
            public void onResponse(Call<UnMuteChatMainObject> call, Response<UnMuteChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(Constants.CHAT_MUTED, "unmuted").apply();
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

//    public void resendAttachements() {
//
//        reSendAttachementsModelCall = Api.WEB_SERVICE.reSendAttachements(EndpointKeys.X_API_KEY,
//                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, "")
//                        , LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), );
//
//        reSendAttachementsModelCall.enqueue(new Callback<ReSendAttachementsModel>() {
//            @Override
//            public void onResponse(Call<ReSendAttachementsModel> call, Response<ReSendAttachementsModel> response) {
//                if (response.body() != null) {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ReSendAttachementsModel> call, Throwable t) {
//
//            }
//        });
//    }
}