package lnq.com.lnq.fragments.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordPermissionHandler;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.AddGroupMemberAdapter;
import lnq.com.lnq.adapters.ChooseImageAdapter;
import lnq.com.lnq.adapters.GroupChatAdapter;
import lnq.com.lnq.adapters.GroupMembersAdapter;
import lnq.com.lnq.adapters.MentionUsersAdapter;
import lnq.com.lnq.adapters.SharedUserProfileAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.image_compressor.Luban;
import lnq.com.lnq.custom.image_compressor.OnCompressListener;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.databinding.FragmentGroupChatBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.gallery.GalleryFragmentNew;
import lnq.com.lnq.listeners.activit_listeners.SwipeDetector;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.attachements.SendAttachementsModel;
import lnq.com.lnq.model.attachements.SendMultipleAttachementsModel;
import lnq.com.lnq.model.event_bus_models.EventBusAddGroupMember;
import lnq.com.lnq.model.event_bus_models.EventBusBlockedUnBlocked;
import lnq.com.lnq.model.event_bus_models.EventBusChatGestures;
import lnq.com.lnq.model.event_bus_models.EventBusChatShareContactClick;
import lnq.com.lnq.model.event_bus_models.EventBusChatThreadClick;
import lnq.com.lnq.model.event_bus_models.EventBusCloseChatImageLayout;
import lnq.com.lnq.model.event_bus_models.EventBusEditGroupName;
import lnq.com.lnq.model.event_bus_models.EventBusGetChatImagePath;
import lnq.com.lnq.model.event_bus_models.EventBusGroupMemberFullProfile;
import lnq.com.lnq.model.event_bus_models.EventBusMentionUsersClick;
import lnq.com.lnq.model.event_bus_models.EventBusOpenMentionedUser;
import lnq.com.lnq.model.event_bus_models.EventBusSaveGroupChatImagesToGallery;
import lnq.com.lnq.model.event_bus_models.EventBusSharedProfileItemClick;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChat;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatCount;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateMessages;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.EventBussFailedMsg;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnections;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatData;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.MuteGroupChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.UnMuteGroupChatMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.AddGroupMemberMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.AddMemberToGroupChat;
import lnq.com.lnq.model.gson_converter_models.conversation.CreateGroupNameMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.ShareGroupProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.send_message.SendMessageMainObject;
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
import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class FragmentGroupChat extends Fragment implements KeyboardVisibilityEventListener, CompoundButton.OnCheckedChangeListener, QueryTokenReceiver {
    private String cachePath = "";
    private TransferUtility transferUtility;




    //    Android fields....
    private FragmentGroupChatBinding binding;
    private ChatClickHandler clickHandler;
    private Unregistrar unregistrar;
    private Handler handler;
    private Runnable runnable;
    View dialogView;
    Dialog dialog;
    ChooseImageAdapter chooseImageAdapter;
    boolean isMentioned;
    private String lastMentiondText;
    private String profileId, receverProfileId;
    private SlideUp slideUp;
    private GroupMembersAdapter groupMembersAdapter;
    //    Instance fields....
    private String groupUserNames, groupChatThreadId, userAvatars, groupUserIds, unreadCount, userNames, groupName, groupUserProfileIds;
    private List<GetChatData> chatDataList = new ArrayList<>();
    private List<String> namesList = new ArrayList<>();
    File imagePath;
    BottomSheetDialogFragment myBottomSheet;
    ArrayList<File> files = new ArrayList<>();
    boolean imageisChecked = false;
    private HashMap<String, String> images = new HashMap<>();
    private List<String> userIds = new ArrayList<>();
    private List<String> groupUserProfileIdsList = new ArrayList<>();
    private List<String> userImages = new ArrayList<>();
    ArrayList<MentionModel> mentionModelArrayList;
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListImported = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListLNQ = new ArrayList<>();
    private List<UserConnectionsData> addUsersToGroupChat = new ArrayList<>();
    private List<UserConnections> userContactList = new ArrayList<>();
    private List<MentionModel> groupMembersList = new ArrayList<>();

    //    Font fields....
    private FontUtils fontUtils;
    private MentionModel.CityLoader cities;
    private static final String BUCKET = "cities-memory";
    private MediaPlayer myPlayer;

    private Call<GetChatMainObject> groupChatMainObjectCall;
    private Call<SendMessageMainObject> callSendGruopMessage;
    private Call<SendAttachementsModel> sendAttachementsGroupModelCall;
    private Call<SendAttachementsModel> sendVoiceGroupModelCall;
    private Call<SendMultipleAttachementsModel> sendMultipleAttachementsModelCall;
    private Call<CreateGroupNameMainObject> nameMainObjectCall;
    private Call<ShareGroupProfileMainObject> shareGroupProfileMainObjectCall;
    private Call<AddGroupMemberMainObject> addGroupMemberMainObjectCall;
    private Call<UserConnectionsMainObject> callUserConnections;
    private Call<MuteGroupChatMainObject> muteGroupChatMainObjectCall;
    private Call<UnMuteGroupChatMainObject> unMuteGroupChatMainObjectCall;

    //    Adapter fields....
    private GroupChatAdapter chatAdapter;
    private SharedUserProfileAdapter sharedUserProfileAdapter;
    private AudioRecorder audioRecorder;
    private File recordFile;
    String playOrStop = "";

    public FragmentGroupChat() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_chat, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                List<String> result = (List<String>) bundle.getSerializable("bundleKey");

                Luban.with(getActivity())
                        .load(result)
                        .ignoreBy(100)
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                files.add(file);
                                chooseImageAdapter = new ChooseImageAdapter(getActivity(), files);
                                binding.recyclerViewChatImages.setAdapter(chooseImageAdapter);
                                imageisChecked = true;
                                if (imageisChecked) {
                                    binding.imageButtonSendMessage.setClickable(true);
                                    binding.imglayout.setVisibility(View.VISIBLE);
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
        createTransferUtility();
        cachePath = getContext().getCacheDir().getAbsolutePath();
        init();
        ValidUtils.textViewGradientColor(binding.textViewUserName);
        ValidUtils.textViewGradientColor(binding.textViewUserName1);
        ValidUtils.textViewGradientColor(binding.textViewGroupMemberNames);
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
//        fontUtils.setEditTextRegularFont(binding.editTextMessage);
        fontUtils.setTextViewSemiBold(binding.textViewUserName);

//        Setting layout manager for recycler view....
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewChat.setLayoutManager(linearLayoutManager);
        binding.recyclerViewChat.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager linearLayoutManagerGroup = new LinearLayoutManager(getActivity());
        binding.recyclerViewGroupMembersDetails.setLayoutManager(linearLayoutManagerGroup);

        binding.recyclerViewChatImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewChatImages.setItemAnimator(new DefaultItemAnimator());

        binding.recyclerViewMentionData.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewMentionData.setItemAnimator(new DefaultItemAnimator());

        reqContacts("", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));

        //        Getting userId from bundle....
        if (getArguments() != null) {
            groupUserNames = getArguments().getString(EndpointKeys.GROUP_USERNAMES, "");
            groupName = getArguments().getString(EndpointKeys.GROUP_NAME, "");
            userAvatars = getArguments().getString(EndpointKeys.USER_AVATAR, "");
            groupChatThreadId = getArguments().getString(EndpointKeys.GROUP_CHAT_THREAD_ID, "");
            groupUserIds = getArguments().getString(EndpointKeys.GROUP_USERS_IDS, "");
            groupUserProfileIds = getArguments().getString(EndpointKeys.GROUP_USERS_PROFILE_IDS, "");
            unreadCount = getArguments().getString(EndpointKeys.CHAT_UNREAD_COUNT);
            userNames = getArguments().getString(EndpointKeys.GROUP_USER_NAMES);
            receverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID);
            if (groupName != null && !groupName.isEmpty()) {
                binding.textViewUserName.setText(groupName);
                binding.textViewGroupMemberNames.setVisibility(View.VISIBLE);
                binding.textViewGroupMemberNames.setText(groupUserNames);
            } else {
                binding.textViewUserName.setText(groupUserNames);
                binding.textViewGroupMemberNames.setVisibility(View.GONE);

            }

            String draftMessage = "";
            if (LnqApplication.getInstance().draftHasMap.containsKey(groupChatThreadId)) {
                draftMessage = LnqApplication.getInstance().draftHasMap.get(groupChatThreadId);
            }
            binding.editTextMessage.setText(draftMessage);

            userIds = Arrays.asList(groupUserIds.split(","));
            groupUserProfileIdsList = Arrays.asList(groupUserProfileIds.split(","));
            userImages = Arrays.asList(userAvatars.split(","));
            namesList = Arrays.asList(groupUserNames.split(","));
            for (int i = 0; i < userIds.size(); i++) {
                if (i > userImages.size() - 1) {
                    images.put(userIds.get(i), "");
                } else {
                    images.put(userIds.get(i), userImages.get(i));
                }
            }
            ArrayList<String> imageFromMap = new ArrayList<>();
            Iterator it = images.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                imageFromMap.add((String) pair.getValue());
            }
            mentionModelArrayList = new ArrayList<>();
            for (int i = 0; i < userIds.size(); i++) {
                if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(userIds.get(i))) {
                    mentionModelArrayList.add(new MentionModel(userIds.get(i), "@" + namesList.get(i), images.get(userIds.get(i)), groupUserProfileIdsList.get(i)));
                }
            }
            MentionUsersAdapter mentionChatAdapter = new MentionUsersAdapter(getActivity(), mentionModelArrayList);
            binding.recyclerViewMentionData.setAdapter(mentionChatAdapter);
            binding.editTextMessage.setQueryTokenReceiver(this);
            binding.editTextMessage.setHint(getResources().getString(R.string.say_something));
            String jsonDataString = new Gson().toJson(mentionModelArrayList, new TypeToken<ArrayList<MentionModel>>() {
            }.getType());
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(jsonDataString);
                cities = new MentionModel.CityLoader(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (userImages.size() == 1) {
                if (userImages != null && !userImages.isEmpty()) {
                    download(userImages.get(0), binding.imageViewGroupUserImage1);
                    binding.imageViewGroupUserImage2.setVisibility(View.GONE);
                }
            } else {
                if (userImages != null && !userImages.isEmpty()) {
                    download(userImages.get(1), binding.imageViewGroupUserImage1);
                    download(userImages.get(2), binding.imageViewGroupUserImage2);
                }
            }

            for (int i = 0; i < userIds.size(); i++) {
                groupMembersList.add(new MentionModel(userIds.get(i), namesList.get(i), images.get(userIds.get(i)), groupUserProfileIdsList.get(i)));
            }
            groupMembersAdapter = new GroupMembersAdapter(getActivity(), groupMembersList);
            binding.recyclerViewGroupMembersDetails.setAdapter(groupMembersAdapter);

            int count = 0;
            if (binding.recyclerViewGroupMembersDetails.getAdapter() != null) {
                count = binding.recyclerViewGroupMembersDetails.getAdapter().getItemCount();
            }
            if (count <= 3) {
                binding.textViewShowMore.setVisibility(View.GONE);
                binding.recyclerViewGroupMembersDetails.getLayoutParams().height = (int) getResources().getDimension(R.dimen._200sdp);
            } else if (count > 4) {
                int remaninigUsers = count - 4;
                binding.textViewShowMore.setVisibility(View.VISIBLE);
                binding.textViewShowMore.setText("Show More (" + remaninigUsers + ")");
                binding.recyclerViewGroupMembersDetails.getLayoutParams().height = (int) getResources().getDimension(R.dimen._240sdp);
            }

            binding.textViewShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.recyclerViewGroupMembersDetails.post(new Runnable() {
                        @Override
                        public void run() {
//                            binding.recyclerViewGroupMembersDetails.smoothScrollToPosition(adapter.getItemCount() - 4);
                            linearLayoutManagerGroup.setStackFromEnd(true);
                        }
                    });
                }
            });

            binding.textViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddMemberList();
                }
            });

            showMessages();

            binding.editTextMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!binding.editTextMessage.getText().toString().isEmpty()) {
//                        binding.imageButtonSendMessage.setBackgroundResource(R.drawable.bg_circle_blue);
                        binding.imageButtonSendMessage.setClickable(true);
                    } else {
//                        binding.imageButtonSendMessage.setBackgroundResource(R.drawable.circle_bg_grey);
                        binding.imageButtonSendMessage.setClickable(false);
                        binding.mentiolayout.setVisibility(View.GONE);
                    }
                    if (s.toString().length() > 0) {
                        if (s.toString().endsWith("@")) {
                            lastMentiondText = s.toString();
                            isMentioned = true;
                        } else {
                            binding.mentiolayout.setVisibility(View.GONE);
                        }
                        if (s.toString().endsWith(" ")) {
                            isMentioned = false;
                        }
                        if (isMentioned) {
                            binding.mentiolayout.setVisibility(View.VISIBLE);
                        }
                        if (isMentioned) {
                            if (lastMentiondText != null) {
                                String newMentionedText = s.toString().substring(lastMentiondText.length());
                                mentionChatAdapter.getFilter().filter(newMentionedText);
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

//        Setting click handler fot data binding....
        clickHandler = new ChatClickHandler();
        binding.setClickHandler(clickHandler);

        //        All event listeners....
        unregistrar = KeyboardVisibilityEvent.registerEventListener(getActivity(), this);
//        binding.editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
////                    if (binding.progressBar.getVisibility() == View.VISIBLE) {
////                        return true;
////                    }
//                    if (!ValidUtils.isNetworkAvailable(getActivity())) {
//                        return true;
//                    }
//                    if (!binding.editTextMessage.getText().toString().isEmpty()) {
//                        reqSendGroupMessage(groupChatThreadId, profileId);
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

        binding.recyclerViewChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
                return false;
            }
        });

        slideUp = new SlideUpBuilder(binding.slideView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            binding.userNamecontainer.setElevation(10);
                            binding.imageViewDropdownContacts.setVisibility(View.VISIBLE);
                        } else {
                            binding.userNamecontainer.setElevation(0);
                            binding.imageViewDropdownContacts.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(binding.userNamecontainer)
                .build();

        audioRecorder = new AudioRecorder();

        //IMPORTANT
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordButton.setListenForRecord(true);

        //ListenForRecord must be false ,otherwise onClick will not be called
        binding.recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
            }
        });


        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        binding.recordView.setCancelBounds(8);
        binding.recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        binding.recordView.setLessThanSecondAllowed(false);
        binding.recordView.setSlideToCancelText("Slide To Cancel");
        binding.recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                binding.view3.setVisibility(View.VISIBLE);
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
            }

            @Override
            public void onCancel() {
                stopRecording(true);
                binding.view3.setVisibility(View.GONE);
                Toast.makeText(getContext(), "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                stopRecording(false);
                binding.view3.setVisibility(View.GONE);
                String time = getHumanTimeText(recordTime);
                sendVoiceAttachment(groupChatThreadId, profileId, recordFile);
//                Toast.makeText(getContext(), "onFinishRecord - Recorded Time is: " + time + " File saved at " + recordFile.getPath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLessThanSecond() {
                stopRecording(true);
                Toast.makeText(getContext(), "OnLessThanSecond", Toast.LENGTH_SHORT).show();
            }
        });


        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {

            }
        });

        binding.recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
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

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            Glide.with(getActivity()).
                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
                    apply(new RequestOptions().circleCrop()).
                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } else {
            final File fileDownload = new File(cachePath, objectKey);

            TransferObserver transferObserver = transferUtility.download(
                    Constants.BUCKET_NAME,
                    objectKey,
                    fileDownload
            );
            transferObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d(TAG, "onStateChanged: " + state);
                    if (TransferState.COMPLETED.equals(state)) {
                        LnqApplication.getInstance().listImagePaths.add(fileDownload.getAbsolutePath());
                        if (getActivity() != null) {
                            Glide.with(getActivity()).
                                    load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                                    apply(new RequestOptions().circleCrop()).
                                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e(TAG, "onError: ", ex);
                }
            });
        }
    }

    private void showMessages() {
        if (!groupChatThreadId.isEmpty()) {
            //        Setting empty adapter....
            chatDataList.clear();
            if (chatAdapter != null) {
                chatAdapter.notifyDataSetChanged();
            }
            chatAdapter = new GroupChatAdapter(getActivity(), chatDataList, images, groupMembersList);
            binding.recyclerViewChat.setAdapter(chatAdapter);
            reqGroupChat();
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
        bundle.putString(EndpointKeys.PROFILE_ID, receverProfileId);
        ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGroupMemberFullProfile(EventBusGroupMemberFullProfile mObj) {
        String userID = mObj.getUserId();
        if (!userID.equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            String pId = mObj.getProfileId();
            Bundle bundle = new Bundle();
            bundle.putString("topBar", "messages");
            bundle.putString("user_id", userID);
            bundle.putString(EndpointKeys.PROFILE_ID, pId);
            ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
        }
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
//        if (getChatData.getsender_id().equals(userId)) {
//            threadId = getChatData.getThread_id();
        chatDataList.add(getChatData);
        chatAdapter.notifyItemInserted(chatDataList.size() - 1);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBlockUnBlock(EventBusBlockedUnBlocked eventBusBlockedUnBlocked) {
        /*if (eventBusBlockedUnBlocked.getIsBlocked().equals("blocked"))
            isBlocked = "blocked";
        else
            isBlocked = "";*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getFailedMsg(EventBussFailedMsg eventBussFailedMsg) {
        /*if (isBlocked.equals("blocked"))
            showDialogBoxUnBlock();
        else
            showDialogBox(eventBussFailedMsg.getPosition());*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMessages(EventBusUpdateMessages eventBusUpdateMessages) {
        showMessages();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSharedProfile(EventBusSharedProfileItemClick eventBusSharedProfileItemClick) {
        reqShareGroupContact(groupChatThreadId, eventBusSharedProfileItemClick.getUserId(), profileId, eventBusSharedProfileItemClick.getUserProfileId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusMentionUsersClick(EventBusMentionUsersClick mObj) {
        Mentionable userName = mObj.getUserName();
        String userId = mObj.getUserId();
        String profileId = mObj.getProfileId();
        String lastSavedData = LnqApplication.getInstance().sharedPreferences.getString("mentioned_ids", "");
        if (lastSavedData.isEmpty()) {
            lastSavedData = userId + "/" + profileId + ":" + userName;
        } else {
            lastSavedData = lastSavedData + ',' + userId + "/" + profileId + ":" + userName;
        }
        LnqApplication.getInstance().editor.putString("mentioned_ids", lastSavedData).apply();
        binding.editTextMessage.addMentionToken(userName);
        binding.mentiolayout.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusSaveGroupChatImagesToGallery(EventBusSaveGroupChatImagesToGallery mObj) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
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
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (unregistrar != null) {
            unregistrar.unregister();
        }
        if (callSendGruopMessage != null && callSendGruopMessage.isExecuted()) {
            callSendGruopMessage.cancel();
        }
        if (groupChatMainObjectCall != null && groupChatMainObjectCall.isExecuted()) {
            groupChatMainObjectCall.cancel();
        }
        if (nameMainObjectCall != null && nameMainObjectCall.isExecuted()) {
            nameMainObjectCall.cancel();
        }
        if (addGroupMemberMainObjectCall != null && addGroupMemberMainObjectCall.isExecuted()) {
            addGroupMemberMainObjectCall.cancel();
        }
        if (muteGroupChatMainObjectCall != null && muteGroupChatMainObjectCall.isExecuted()) {
            muteGroupChatMainObjectCall.cancel();
        }
        if (unMuteGroupChatMainObjectCall != null && unMuteGroupChatMainObjectCall.isExecuted()) {
            unMuteGroupChatMainObjectCall.cancel();
        }
        /*if (callReSendMessage != null && callReSendMessage.isExecuted()) {
            callReSendMessage.cancel();
        }*/
        LnqApplication.getInstance().draftHasMap.put(groupChatThreadId, binding.editTextMessage.getText().toString().trim());
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
            handler.postDelayed(runnable, 1500);
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
//                        binding.imageChoose.setImageURI(Uri.fromFile(imagePath));
                        imageisChecked = true;
                        if (imageisChecked) {
                            binding.imageButtonSendMessage.setClickable(true);
                            binding.imglayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusCloseChatImageLayout(EventBusCloseChatImageLayout eventBusCloseChatImageLayout) {
        if (files.size() == 0) {
            binding.imglayout.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusAddGroupMember(EventBusAddGroupMember eventBusAddGroupMember) {
        reqAddGroupMember(groupChatThreadId, eventBusAddGroupMember.getUser_id(), eventBusAddGroupMember.getProfile_id());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusOpenGroupMember(EventBusOpenMentionedUser mObj) {
        String name = mObj.getName();
        String userid = "";
        String profiled = "";
        for (int i = 0; i < mentionModelArrayList.size(); i++) {
            if (mentionModelArrayList.get(i).getUserName().contains(mObj.getName().replaceAll(" ", ""))) {
                userid = mentionModelArrayList.get(i).getUserId();
                profiled = mentionModelArrayList.get(i).getUserProfileId();
                break;
            }
        }
        if (!userid.equalsIgnoreCase("")) {
            Bundle bundle = new Bundle();
            bundle.putString("topBar", "messages");
            bundle.putString("user_id", userid);
            bundle.putString(EndpointKeys.PROFILE_ID, profiled);
            ((MainActivity) getActivity()).fnLoadFragAdd("LNQ CONTACT PROFILE VIEW", true, bundle);
        } else {
            ValidUtils.showCustomToast(getContext(), "Its your profile");
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

    private void reqGroupChat() {
        binding.shimmerLayoutChatThread.setVisibility(View.VISIBLE);
        binding.shimmerLayoutChatThread.startShimmerAnimation();
        groupChatMainObjectCall = Api.WEB_SERVICE.groupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), groupChatThreadId, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), profileId);
        groupChatMainObjectCall.enqueue(new Callback<GetChatMainObject>() {
            @Override
            public void onResponse(Call<GetChatMainObject> call, Response<GetChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    binding.shimmerLayoutChatThread.setVisibility(View.GONE);
                    binding.shimmerLayoutChatThread.stopShimmerAnimation();
                    switch (response.body().getStatus()) {
                        case 1:
                            int lastSuccessfullMessagePosition = 0;
                            chatDataList.addAll(response.body().getGetGroupChat());
                            SortingUtils.sortChatDataByDate(chatDataList);
                            if (unreadCount != null && !unreadCount.isEmpty()) {
                                for (int i = 0; i < Integer.parseInt(unreadCount); i++) {
                                    chatDataList.get(chatDataList.size() - 1 - i).setMessageUnread(true);
                                }
                            }
                            for (int i = 0; i < chatDataList.size(); i++) {
                                chatAdapter.notifyItemInserted(i);
                                if (chatDataList.get(i).isSent().equals("1")) {
                                    lastSuccessfullMessagePosition = i;
                                }
                            }

                            String isChatMuted = LnqApplication.getInstance().sharedPreferences.getString(Constants.GROUP_MUTED, "");
                            if (isChatMuted != null && !isChatMuted.isEmpty()) {
                                if (isChatMuted.equalsIgnoreCase("muted")) {
                                    binding.switchMuteUnMute.setChecked(true);
                                } else {
                                    binding.switchMuteUnMute.setChecked(false);
                                }
                            }

                            binding.switchMuteUnMute.setOnCheckedChangeListener(FragmentGroupChat.this);

                            chatDataList.get(lastSuccessfullMessagePosition).setPosition(true);
                            chatAdapter.notifyItemChanged(lastSuccessfullMessagePosition);
                            chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    super.onItemRangeInserted(positionStart, itemCount);
                                    binding.recyclerViewChat.scrollToPosition(chatDataList.size() - 1);
                                }
                            });
                            EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                            break;
                        case 0:
                            binding.shimmerLayoutChatThread.setVisibility(View.GONE);
                            binding.shimmerLayoutChatThread.stopShimmerAnimation();
                            EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetChatMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                binding.shimmerLayoutChatThread.setVisibility(View.GONE);
                binding.shimmerLayoutChatThread.stopShimmerAnimation();
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

//    public void sendAttachment(File file) {
//        imageisChecked = false;
//        String groupMessage = binding.editTextMessage.getText().toString();
//        binding.editTextMessage.setText("");
//        final RequestBody threadId = RequestBody.create(MediaType.parse("text/plain"), groupChatThreadId);
//        final RequestBody senderId = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
//        final RequestBody message = RequestBody.create(MediaType.parse("text/plain"), groupMessage);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("attachment", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
//        sendAttachementsGroupModelCall = Api.WEB_SERVICE.sendGroupAttachment(EndpointKeys.X_API_KEY,
//                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
//                filePart,
//                threadId,
//                senderId,
//                message);
//        sendAttachementsGroupModelCall.enqueue(new Callback<SendAttachementsModel>() {
//            @Override
//            public void onResponse(Call<SendAttachementsModel> call, Response<SendAttachementsModel> response) {
//                if (response.body() != null) {
//                    switch (response.body().getStatus()) {
//                        case 1:
//                            if (response.body().getSendAttachment() != null) {
//                                if (response.body().getSendAttachment().getIsSent() == 0) {
//                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendGroupAttachment().getSenderId(), groupMessage, response.body().getSendGroupAttachment().getMessageTime(), String.valueOf(response.body().getSendGroupAttachment().getMsgId()), String.valueOf(response.body().getSendGroupAttachment().getIsSent()), String.valueOf(response.body().getSendGroupAttachment().getIsPending()), false, response.body().getSendGroupAttachment().getAttachment()));
//                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
//                                } else {
//                                    for (int i = 0; i < chatDataList.size(); i++) {
//                                        chatDataList.get(i).setPosition(false);
//                                    }
//                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendGroupAttachment().getSenderId(), groupMessage, response.body().getSendGroupAttachment().getMessageTime(), String.valueOf(response.body().getSendGroupAttachment().getMsgId()), String.valueOf(response.body().getSendGroupAttachment().getIsSent()), String.valueOf(response.body().getSendGroupAttachment().getIsPending()), true, response.body().getSendGroupAttachment().getAttachment()));
//                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
//                                }
//                                chatAdapter.notifyDataSetChanged();
//                                EventBus.getDefault().post(new EventBusUpdateChat());
//                                EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
//                            }
//                            break;
//                        case 0:
//                            break;
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SendAttachementsModel> call, Throwable error) {
//                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
//                    return;
//                }
//                if (error != null) {
//                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
//                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
//                    } else {
//                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
//                    }
//                } else {
//                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
//                }
//            }
//        });
//    }

    private MultipartBody.Part prepareFilePart(String partName, String fileUri) {
        File file = new File(fileUri);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }

    public void sendMultiAttachment(ArrayList<File> file, String groupMessage) {
        imageisChecked = false;
//        String groupMessage = binding.editTextMessage.getText().toString().trim();
        binding.editTextMessage.setText("");
        List<MultipartBody.Part> filePart = new ArrayList<>();
        final RequestBody threadId = RequestBody.create(MediaType.parse("text/plain"), groupChatThreadId);
        final RequestBody senderId = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        final RequestBody message = RequestBody.create(MediaType.parse("text/plain"), groupMessage);
        final RequestBody senderProfileId = RequestBody.create(MediaType.parse("text/plain"), profileId);
        for (int i = 0; i < files.size(); i++) {
            filePart.add(prepareFilePart("attachment[" + i + "]", file.get(i).getAbsolutePath()));
        }
        sendAttachementsGroupModelCall = Api.WEB_SERVICE.sendGroupMultipleAttachment(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                filePart,
                threadId,
                senderId,
                message,
                senderProfileId);
        sendAttachementsGroupModelCall.enqueue(new Callback<SendAttachementsModel>() {
            @Override
            public void onResponse(Call<SendAttachementsModel> call, Response<SendAttachementsModel> response) {
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (chooseImageAdapter != null) {
                                files.clear();
                                chooseImageAdapter.notifyDataSetChanged();
                                binding.imglayout.setVisibility(View.GONE);
                            }
                            if (response.body().getSendGroupAttachment() != null) {
                                if (response.body().getSendGroupAttachment().getIsSent() == 0) {
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendGroupAttachment().getSenderId(), groupMessage.trim(), response.body().getSendGroupAttachment().getMessageTime(), String.valueOf(response.body().getSendGroupAttachment().getMsgId()), String.valueOf(response.body().getSendGroupAttachment().getIsSent()), String.valueOf(response.body().getSendGroupAttachment().getIsPending()), false, response.body().getSendGroupAttachment().getAttachment(), ""));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                } else {
                                    for (int i = 0; i < chatDataList.size(); i++) {
                                        chatDataList.get(i).setPosition(false);
                                    }
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendGroupAttachment().getSenderId(), groupMessage.trim(), response.body().getSendGroupAttachment().getMessageTime(), String.valueOf(response.body().getSendGroupAttachment().getMsgId()), String.valueOf(response.body().getSendGroupAttachment().getIsSent()), String.valueOf(response.body().getSendGroupAttachment().getIsPending()), true, response.body().getSendGroupAttachment().getAttachment(), ""));
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

    public void sendVoiceAttachment(String threadId, String senderProfileid, File file) {
        imageisChecked = false;
        binding.editTextMessage.setText("");
//        List<MultipartBody.Part> filePart = new ArrayList<>();
        final RequestBody sender_id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        final RequestBody chatThreadId = RequestBody.create(MediaType.parse("text/plain"), threadId);
        final RequestBody sender_profile_id = RequestBody.create(MediaType.parse("text/plain"), senderProfileid);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("voice_attachment", file.getName(), RequestBody.create(MediaType.parse("/mp3"), file));
        sendVoiceGroupModelCall = Api.WEB_SERVICE.sendVoiceAttachementInGroup(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                chatThreadId,
                sender_id,
                sender_profile_id,
                filePart);
        sendVoiceGroupModelCall.enqueue(new Callback<SendAttachementsModel>() {
            @Override
            public void onResponse(Call<SendAttachementsModel> call, Response<SendAttachementsModel> response) {
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body().getSendVoiceAttachementInGroup().getVoice_attachment() != null) {
                                if (response.body().getSendVoiceAttachementInGroup().getIsSent() == 0) {
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendVoiceAttachementInGroup().getSenderId(), "", "", "", String.valueOf(response.body().getSendVoiceAttachementInGroup().getIsSent()), String.valueOf(response.body().getSendVoiceAttachementInGroup().getIsPending()), false, "", response.body().getSendVoiceAttachementInGroup().getVoice_attachment()));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                } else {
                                    for (int i = 0; i < chatDataList.size(); i++) {
                                        chatDataList.get(i).setPosition(false);
                                    }
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendVoiceAttachementInGroup().getSenderId(), "", "", "", String.valueOf(response.body().getSendVoiceAttachementInGroup().getIsSent()), String.valueOf(response.body().getSendVoiceAttachementInGroup().getIsPending()), false, "", response.body().getSendVoiceAttachementInGroup().getVoice_attachment()));
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


    public void reqSendGroupMessage(String threadId, String profileid) {
        ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
        String groupMessage = binding.editTextMessage.getText().toString().trim();
        String lastSavedMentionIds = LnqApplication.getInstance().sharedPreferences.getString("mentioned_ids", "");
        List<String> foundNames = new ArrayList();
        if (!lastSavedMentionIds.isEmpty()) {
            List<String> data = Arrays.asList(lastSavedMentionIds.split(","));
            for (String d : data) {
                String mentionedText = d.substring(d.indexOf(":") + 1);
                if (foundNames.size() == 0 || !foundNames.contains(mentionedText)) {
                    groupMessage = groupMessage.replaceAll(mentionedText, d);
                    foundNames.add(mentionedText);
                }
            }
        }
        groupMessage = groupMessage.replaceAll(":", "").replaceAll("@", "");

        LnqApplication.getInstance().editor.putString("mentioned_ids", "").apply();
        binding.editTextMessage.setText("");
        callSendGruopMessage = Api.WEB_SERVICE.sendGroupMessage(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), threadId, groupMessage, profileid);
        String finalGroupMessage = groupMessage;
        callSendGruopMessage.enqueue(new Callback<SendMessageMainObject>() {
            @Override
            public void onResponse(Call<SendMessageMainObject> call, Response<SendMessageMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body().getMessage() != null) {
                                if (response.body().getSendGroupMessage().getIs_sent() == 0) {
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendGroupMessage().getSender_id(), finalGroupMessage, response.body().getSendGroupMessage().getMessage_time(), String.valueOf(response.body().getSendGroupMessage().getMsg_id()), String.valueOf(response.body().getSendGroupMessage().getIs_sent()), response.body().getSendGroupMessage().getIs_pending(), false, "", ""));
                                    chatAdapter.notifyItemInserted(chatDataList.size() - 1);
                                } else {
                                    for (int i = 0; i < chatDataList.size(); i++) {
                                        chatDataList.get(i).setPosition(false);
                                    }
                                    chatDataList.add(new GetChatData(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), response.body().getSendGroupMessage().getSender_id(), finalGroupMessage, response.body().getSendGroupMessage().getMessage_time(), String.valueOf(response.body().getSendGroupMessage().getMsg_id()), String.valueOf(response.body().getSendGroupMessage().getIs_sent()), response.body().getSendGroupMessage().getIs_pending(), true, "", ""));
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
            public void onFailure(Call<SendMessageMainObject> call, Throwable error) {
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

    public void showAddMemberList() {
        addUsersToGroupChat.clear();
        for (UserConnectionsData userConnectionsData : userContactsDataListLNQ) {
            boolean isFound = false;
            for (MentionModel usersProfileId : mentionModelArrayList) {
                if (userConnectionsData.getProfile_id().equalsIgnoreCase(usersProfileId.getUserProfileId())) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                addUsersToGroupChat.add(userConnectionsData);
            }
        }
        if (addUsersToGroupChat.size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.cus_dialog_addmemberlist, null);

            RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

            AddGroupMemberAdapter addGroupMemberAdapter = new AddGroupMemberAdapter(getActivity(), addUsersToGroupChat);
            recyclerViewShareProfile.setAdapter(addGroupMemberAdapter);
            recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(getActivity()));

            builder.setView(dialogView);
            dialog = builder.create();
            dialog.show();

            try {
                dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

            } catch (Exception e) {

            }
        } else {
            ValidUtils.showCustomToast(getContext(), "All your contacts are already members of this group");
        }
    }

    @NonNull
    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        List<String> buckets = Collections.singletonList(BUCKET);
        List<MentionModel> suggestions = cities.getSuggestions(queryToken);
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
        binding.editTextMessage.onReceiveSuggestionsResult(result, BUCKET);
        return buckets;
    }

    public class ChatClickHandler {

        public void onBackClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), binding.getRoot());
            EventBus.getDefault().post(new EventBusUpdateChatCount(0));
            getActivity().onBackPressed();
        }

        public void onSendMessageClick(View view) {
            if (!ValidUtils.isNetworkAvailable(getActivity())) {
                return;
            }

            if (imageisChecked) {
                sendMultiAttachment(files, binding.editTextMessage.getText().toString().trim());
                binding.imglayout.setVisibility(View.GONE);
            } else {
                reqSendGroupMessage(groupChatThreadId, profileId);
            }
        }

        public void onAttachmentClick(View view) {
            if (((MainActivity) getActivity()).fnCheckReadStoragePermission()) {
                myBottomSheet = GalleryFragmentNew.newInstance("multiple");
                myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
            } else {
                ((MainActivity) getActivity()).fnRequestStoragePermission(10);
            }
        }

        public void onEditStatusClick(View view) {
            showCreateGroupDialog();
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

        public void onLeaveChatClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), binding.getRoot());
            reqLeaveGroup(groupChatThreadId, profileId);
        }
    }

    private void showCreateGroupDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        dialogView = inflater.inflate(R.layout.cus_dialog_creategroup, null);
        TextView textCancel = dialogView.findViewById(R.id.textViewCancel);
        TextView textViewSaveGroup = dialogView.findViewById(R.id.textViewSaveGroup);
        AppCompatEditText editTextGroupName = dialogView.findViewById(R.id.editTextGroupName);

        textViewSaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqCreateGroupName(groupChatThreadId, editTextGroupName.getText().toString());
                dialog.dismiss();
            }
        });

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        try {
            dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);
        } catch (Exception e) {

        }
    }

    public void reqCreateGroupName(String groupThreadId, String groupNewName) {
        ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
        nameMainObjectCall = Api.WEB_SERVICE.createGroupName(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), groupThreadId, groupNewName);
        nameMainObjectCall.enqueue(new Callback<CreateGroupNameMainObject>() {
            @Override
            public void onResponse(Call<CreateGroupNameMainObject> call, Response<CreateGroupNameMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusEditGroupName());
                            if (groupNewName != null && !groupNewName.isEmpty()) {
                                binding.textViewUserName.setText(groupNewName);
                                binding.textViewGroupMemberNames.setVisibility(View.VISIBLE);
                                binding.textViewGroupMemberNames.setText(groupUserNames);
                            } else {
                                binding.textViewUserName.setText(groupUserNames);
                                binding.textViewGroupMemberNames.setVisibility(View.GONE);
                            }
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateGroupNameMainObject> call, Throwable error) {
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

    public void reqShareGroupContact(String threadId, String userId, String senderProfileid, String userProfileid) {
        shareGroupProfileMainObjectCall = Api.WEB_SERVICE.sendGroupShareContact(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), threadId, userId, senderProfileid, userProfileid);
        shareGroupProfileMainObjectCall.enqueue(new Callback<ShareGroupProfileMainObject>() {
            @Override
            public void onResponse(Call<ShareGroupProfileMainObject> call, Response<ShareGroupProfileMainObject> response) {
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
            public void onFailure(Call<ShareGroupProfileMainObject> call, Throwable error) {
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

    public void reqAddGroupMember(String threadId, String userId, String profileid) {
        addGroupMemberMainObjectCall = Api.WEB_SERVICE.addMemberToGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                threadId, userId, profileid, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        addGroupMemberMainObjectCall.enqueue(new Callback<AddGroupMemberMainObject>() {
            @Override
            public void onResponse(Call<AddGroupMemberMainObject> call, Response<AddGroupMemberMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            AddMemberToGroupChat groupMember = response.body().getAddMemberToGroupChat();
                            EventBus.getDefault().post(new EventBusEditGroupName());
                            groupMembersList.add(new MentionModel(groupMember.getUser_id(), groupMember.getUser_fname() + " " + groupMember.getUser_lname(), groupMember.getUser_avatar(), groupMember.getProfile_id()));
                            groupMembersAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<AddGroupMemberMainObject> call, Throwable error) {
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

    public void reqLeaveGroup(String threadId, String profileid) {
        addGroupMemberMainObjectCall = Api.WEB_SERVICE.leaveFromGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                threadId, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), profileid);
        addGroupMemberMainObjectCall.enqueue(new Callback<AddGroupMemberMainObject>() {
            @Override
            public void onResponse(Call<AddGroupMemberMainObject> call, Response<AddGroupMemberMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusEditGroupName());
                            ((MainActivity) getActivity()).showMessageDialog("success", "You are successfully leave the group");
                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<AddGroupMemberMainObject> call, Throwable error) {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == binding.switchMuteUnMute) {
            if (isChecked) {
                reqMuteGroupChat(groupChatThreadId, 3, "", profileId);
            } else {
                reqUnMuteGroupChat(groupChatThreadId, profileId);
            }
        }
    }

    private void reqMuteGroupChat(String groupThreadId, int muteType, String muteTime, String userProfileIdMuteBy) {
        muteGroupChatMainObjectCall = Api.WEB_SERVICE.muteGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), groupThreadId, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), muteType, muteTime, userProfileIdMuteBy);
        muteGroupChatMainObjectCall.enqueue(new Callback<MuteGroupChatMainObject>() {
            @Override
            public void onResponse(Call<MuteGroupChatMainObject> call, Response<MuteGroupChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(Constants.GROUP_MUTED, "muted");
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

    private void reqUnMuteGroupChat(String groupThreadId, String userProfileIdMuteBy) {
        unMuteGroupChatMainObjectCall = Api.WEB_SERVICE.unmuteGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), groupThreadId, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userProfileIdMuteBy);
        unMuteGroupChatMainObjectCall.enqueue(new Callback<UnMuteGroupChatMainObject>() {
            @Override
            public void onResponse(Call<UnMuteGroupChatMainObject> call, Response<UnMuteGroupChatMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(Constants.GROUP_MUTED, "unMuted");
                            ((MainActivity) getActivity()).showMessageDialog("success", "Group UnMuted Successfully");
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
}