package lnq.com.lnq.fragments.chat;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tokenautocomplete.TokenCompleteTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ChooseImageAdapter;
import lnq.com.lnq.adapters.ConnectionsListAdapter;
import lnq.com.lnq.adapters.MentionChatAdapter;
import lnq.com.lnq.adapters.SearchContactByNameCustomAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.custom.image_compressor.Luban;
import lnq.com.lnq.custom.image_compressor.OnCompressListener;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.databinding.FragmentNewMessageBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.gallery.GalleryFragmentNew;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.attachements.SendAttachementsModel;
import lnq.com.lnq.model.event_bus_models.EventBusCloseChatImageLayout;
import lnq.com.lnq.model.event_bus_models.EventBusConnectionView;
import lnq.com.lnq.model.event_bus_models.EventBusGetChatImagePath;
import lnq.com.lnq.model.event_bus_models.EventBusGetLnqedContactList;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChat;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnections;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.CreateGroupThreadMainObject;
import lnq.com.lnq.model.gson_converter_models.searchuser.SearchContactByName;
import lnq.com.lnq.model.gson_converter_models.searchuser.SearchUser;
import lnq.com.lnq.model.gson_converter_models.send_message.SendMessageMainObject;
import lnq.com.lnq.utils.ValidUtils;
import mabbas007.tagsedittext.TagsEditText;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMessageFragment extends Fragment implements KeyboardVisibilityEventListener, TagsEditText.TagsEditListener, TokenCompleteTextView.TokenListener<SearchContactByName> {

    //    Android fields....
    private FragmentNewMessageBinding binding;

    //Retrofit Call
    Call<SearchUser> searchUserCall;
    private Call<SendMessageMainObject> callReSendMessage;
    private Call<CreateGroupThreadMainObject> callCreateGruop;
    private Call<SendMessageMainObject> callSendGruopMessage;
    private Call<SendAttachementsModel> sendAttachementsModelCall;

    //    Instance fields....
    List<SearchContactByName> contactByNameList;
    List<SearchContactByName> selectedContacts = new ArrayList<>();
    private List<UserConnections> userContactList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListImported = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataTempListImported = new ArrayList<>();
    private List<UserConnectionsData> userContactsDataListLNQ = new ArrayList<>();
    ArrayList<File> files = new ArrayList<>();
    File imagePath;
    boolean imageisChecked = false;
    long delay = 1000;
    long last_text_edit = 0;
    Handler handler = new Handler();
    View dialogView;
    Dialog dialog;
    String groupChatThreadId;
    private String activeProfileId;
    private String recevierProfileId;
    String contactGroupName;
    BottomSheetDialogFragment myBottomSheet;

    private ChooseImageAdapter chooseImageAdapter;

    //    private ChatAdapter chatAdapter;
    private Unregistrar unregistrar;

    public NewMessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_message, container, false);
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
        init();
    }

    public void init() {
        EventBus.getDefault().register(this);
        activeProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        binding.tagsEditTextNewMessage.requestFocus();
        ((MainActivity) getActivity()).fnShowKeyboardFrom(binding.tagsEditTextNewMessage);

        if (getArguments() != null) {
            contactGroupName = getArguments().getString("groupName");
            List<UserConnectionsData> list = (List<UserConnectionsData>) getArguments().getSerializable("groupData");
            List<SearchContactByName> groupData = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SearchContactByName searchContactByName = new SearchContactByName();
                searchContactByName.setProfile_id(list.get(i).getProfile_id());
                searchContactByName.setUserId(list.get(i).getUser_id());
                searchContactByName.setUserFname(list.get(i).getUser_fname());
                searchContactByName.setUserLname(list.get(i).getUser_lname());
                searchContactByName.setUserAvatar(list.get(i).getUser_avatar());
                searchContactByName.setUserPhone(list.get(i).getUser_phone());
                groupData.add(searchContactByName);
                binding.tagsEditTextNewMessage.addObjectSync(searchContactByName);
                selectedContacts = groupData;
            }
//            StringBuilder text = new StringBuilder();
//            for (int i = 0; i < selectedContacts.size(); i++){
//                text.append(" ").append(selectedContacts.get(i).getUserFname());
//            }
////            binding.tagsEditTextNewMessage.setText(text);
        }

        binding.recyclerViewChatImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewChatImages.setItemAnimator(new DefaultItemAnimator());

        binding.tagsEditTextNewMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fnIsUserTyping(s.toString());
            }
        });
        binding.mainLayout.setOnTouchListener((v, event) -> {
            ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
            return false;
        });

        binding.imageAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
                if (((MainActivity) getActivity()).fnCheckReadStoragePermission()) {
                    myBottomSheet = GalleryFragmentNew.newInstance("multiple");
                    myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
                } else {
                    ((MainActivity) getActivity()).fnRequestStoragePermission(10);
                }
            }
        });

        binding.imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ValidUtils.isNetworkAvailable(getActivity())) {
                    return;
                }

                if (selectedContacts.size() == 1) {
                    ArrayList<String> idsList = new ArrayList<>();
                    for (SearchContactByName contactByName : selectedContacts) {
                        idsList.add(contactByName.getUserId());
                        recevierProfileId = contactByName.getProfile_id();
                    }
                    String ids = idsList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "");
                    if (imageisChecked) {
                        sendMutipleAttachment(files, ids, binding.editTextMessage.getText().toString(), activeProfileId, recevierProfileId);
                        binding.imglayout.setVisibility(View.GONE);
                    } else {
                        reqSendMessage(ids, activeProfileId, recevierProfileId);
                    }
                } else if (selectedContacts.size() > 1) {
                    String senderId = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
                    ArrayList<String> idsList = new ArrayList<>();
                    ArrayList<String> profileIdsList = new ArrayList<>();
                    idsList.add(senderId);
                    profileIdsList.add(activeProfileId);
                    for (SearchContactByName contactByName : selectedContacts) {
                        idsList.add(contactByName.getUserId());
                        profileIdsList.add(contactByName.getProfile_id());
                    }
                    String ids = idsList.toString().replaceAll(", ", ",").replaceAll("[\\[.\\]]", "");
                    String profileIds = profileIdsList.toString().replaceAll(", ", ",").replaceAll("[\\[.\\]]", "");

                    if (contactGroupName != null && !contactGroupName.isEmpty()) {
                        reqCreateGroup(ids, contactGroupName, profileIds);
                    } else {
                        reqCreateGroup(ids, "", profileIds);
                    }
                }
            }
        });
        binding.editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.editTextMessage.getText().toString().isEmpty()) {
//                    binding.imageButtonSendMessage.setBackgroundResource(R.drawable.bg_circle_blue);
                    binding.imageButtonSendMessage.setClickable(true);
                } else {
//                    binding.imageButtonSendMessage.setBackgroundResource(R.drawable.circle_bg_grey);
                    binding.imageButtonSendMessage.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());
        unregistrar = KeyboardVisibilityEvent.registerEventListener(getActivity(), this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusCloseChatImageLayout(EventBusCloseChatImageLayout eventBusCloseChatImageLayout) {
        if (files.size() == 0) {
            binding.imglayout.setVisibility(View.GONE);
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
                            binding.imageButtonSendMessage.setBackgroundResource(R.drawable.bg_circle_blue);
                            binding.imageButtonSendMessage.setClickable(true);
                            binding.imglayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

    private void fnIsUserTyping(String text) {
        if (text.isEmpty()) {
            return;
        }
        if (searchUserCall != null && searchUserCall.isExecuted()) {
            searchUserCall.cancel();
        }
        if (callReSendMessage != null && callReSendMessage.isExecuted()) {
            callReSendMessage.cancel();
        }
        if (sendAttachementsModelCall != null && sendAttachementsModelCall.isExecuted()) {
            sendAttachementsModelCall.cancel();
        }
        if (callCreateGruop != null && callCreateGruop.isExecuted()) {
            callCreateGruop.cancel();
        }
        if (callSendGruopMessage != null && callSendGruopMessage.isExecuted()) {
            callSendGruopMessage.cancel();
        }
        last_text_edit = System.currentTimeMillis();
        handler.postDelayed(input_finish_checker, delay);
    }

    public void showDialogBox(List<SearchContactByName> list) {
        SearchContactByNameCustomAdapter customAdapter = new SearchContactByNameCustomAdapter(getActivity(), R.layout.search_contact_row, list);
        binding.tagsEditTextNewMessage.setAdapter(customAdapter);
        binding.tagsEditTextNewMessage.showDropDown();
        binding.tagsEditTextNewMessage.setThreshold(1);
        binding.tagsEditTextNewMessage.setTokenListener(this);
        binding.tagsEditTextNewMessage.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
    }

    public void searchContactByName(String name, String profileId) {
        contactByNameList = new ArrayList<>();
        searchUserCall = Api.WEB_SERVICE.searchByName(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), name, profileId);
        searchUserCall.enqueue(new Callback<SearchUser>() {
            @Override
            public void onResponse(Call<SearchUser> call, Response<SearchUser> response) {
                if (response.isSuccessful() && response != null) {
                    contactByNameList.addAll(response.body().getSearchContactByName());
                    if (contactByNameList.size() > 0) {
                        showDialogBox(contactByNameList);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchUser> call, Throwable error) {
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

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                String query = binding.tagsEditTextNewMessage.getText().toString().trim();
                query = query.replace(" ", "");
                List<String> nameList = new ArrayList<>(Arrays.asList(query.split(",")));
                if (nameList.size() > 1) {
                    if (nameList.get(nameList.size() - 1).trim().length() > 0)
                        searchContactByName(nameList.get(nameList.size() - 1), activeProfileId);
                } else {
                    if (!query.isEmpty() && query.trim().length() > 0)
                        searchContactByName(query, activeProfileId);
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
        EventBus.getDefault().unregister(this);
        if (searchUserCall != null && searchUserCall.isExecuted()) {
            searchUserCall.cancel();
        }
        if (unregistrar != null) {
            unregistrar.unregister();
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
                    ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.VISIBLE);
                }
            }, 50);
        }
    }

    public void reqSendMessage(String msg_ids, String senderProfileId, String recevierProfileid) {
        ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
        String message = binding.editTextMessage.getText().toString();
        binding.editTextMessage.setText("");
        callReSendMessage = Api.WEB_SERVICE.sendMultiMessage(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), msg_ids, message, senderProfileId, recevierProfileid);
        callReSendMessage.enqueue(new Callback<SendMessageMainObject>() {
            @Override
            public void onResponse(Call<SendMessageMainObject> call, Response<SendMessageMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    if (selectedContacts.size() > 1) {
                        getActivity().onBackPressed();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("mFlag", "conversation");
                        bundle.putString(EndpointKeys.USER_ID, selectedContacts.get(0).getUserId());
                        bundle.putString("receiverProfileId", selectedContacts.get(0).getProfile_id());
                        bundle.putString(EndpointKeys.USER_NAME, selectedContacts.get(0).getUserFname() + " " + selectedContacts.get(0).getUserLname());
                        bundle.putString(EndpointKeys.USER_AVATAR, selectedContacts.get(0).getUserAvatar());
                        bundle.putString(EndpointKeys.IS_FAVORITE, selectedContacts.get(0).getIsFavorite());
                        bundle.putString(EndpointKeys.IS_BLOCK, selectedContacts.get(0).getIsBlocked());
                        bundle.putString(EndpointKeys.THREAD_ID, selectedContacts.get(0).getThreadId());
                        ((MainActivity) getActivity()).fnLoadFragAdd("CHAT", true, bundle);
                    }
                    EventBus.getDefault().post(new EventBusUpdateChat());
                    EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
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

    public void sendMutipleAttachment(ArrayList<File> file, String userIds, String message, String senderProfileid, String recevierProfileid) {
        ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
        binding.editTextMessage.setText("");
        imageisChecked = false;
        List<MultipartBody.Part> filePart = new ArrayList<>();
        final RequestBody sender_id = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        final RequestBody receiver_id = RequestBody.create(MediaType.parse("text/plain"), userIds);
        final RequestBody sender_profile_id = RequestBody.create(MediaType.parse("text/plain"), senderProfileid);
        final RequestBody receiver_profile_id = RequestBody.create(MediaType.parse("text/plain"), recevierProfileid);
        final RequestBody addMessage = RequestBody.create(MediaType.parse("text/plain"), message);
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
                            if (response.body().getSendAttachment() != null) {
                                if (selectedContacts.size() > 1) {
                                    getActivity().onBackPressed();
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("mFlag", "conversation");
                                    bundle.putString(EndpointKeys.USER_ID, selectedContacts.get(0).getUserId());
                                    bundle.putString("receiverProfileId", selectedContacts.get(0).getProfile_id());
                                    bundle.putString(EndpointKeys.USER_NAME, selectedContacts.get(0).getUserFname() + " " + selectedContacts.get(0).getUserLname());
                                    bundle.putString(EndpointKeys.USER_AVATAR, selectedContacts.get(0).getUserAvatar());
                                    bundle.putString(EndpointKeys.IS_FAVORITE, selectedContacts.get(0).getIsFavorite());
                                    bundle.putString(EndpointKeys.IS_BLOCK, selectedContacts.get(0).getIsBlocked());
                                    bundle.putString(EndpointKeys.THREAD_ID, selectedContacts.get(0).getThreadId());
                                    ((MainActivity) getActivity()).fnLoadFragAdd("CHAT", true, bundle);
                                }
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


    @Override
    public void onTagsChanged(Collection<String> tags) {
//        for (String tag : tags) {
//            for (SearchContactByName contact : selectedContacts) {
//                if (tag.equalsIgnoreCase(contact.getUserFname())) {
//                    selectedContacts.remove(contact);
//                }
//            }
//        }
    }

    @Override
    public void onEditingFinished() {

    }

    @Override
    public void onTokenAdded(SearchContactByName token) {
        selectedContacts.add(token);
    }

    @Override
    public void onTokenRemoved(SearchContactByName token) {
        selectedContacts.remove(token);
    }

    @Override
    public void onTokenIgnored(SearchContactByName token) {
        Toast.makeText(getActivity(), token.getUserId() + "", Toast.LENGTH_SHORT).show();
    }

    public void reqCreateGroup(String participant_ids, String group_name, String participantProfileids) {
        ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
        callCreateGruop = Api.WEB_SERVICE.createGroupChat(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), participant_ids, group_name, participantProfileids);
        callCreateGruop.enqueue(new Callback<CreateGroupThreadMainObject>() {
            @Override
            public void onResponse(Call<CreateGroupThreadMainObject> call, Response<CreateGroupThreadMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            groupChatThreadId = response.body().getThread_id();
                            if (imageisChecked) {
                                sendMultiGroupAttachment(files);
                                binding.imglayout.setVisibility(View.GONE);
                                EventBus.getDefault().post(new EventBusUpdateChat());
                                EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
                                ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                                getActivity().onBackPressed();
                            } else {
                                reqSendGroupMessage(groupChatThreadId, activeProfileId);
                            }
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateGroupThreadMainObject> call, Throwable error) {
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

    public void reqSendGroupMessage(String threadId, String profileid) {
        ValidUtils.hideKeyboardFromFragment(getContext(), binding.getRoot());
        String groupMessage = binding.editTextMessage.getText().toString();
        binding.editTextMessage.setText("");
        callSendGruopMessage = Api.WEB_SERVICE.sendGroupMessage(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), threadId, groupMessage, profileid);
        callSendGruopMessage.enqueue(new Callback<SendMessageMainObject>() {
            @Override
            public void onResponse(Call<SendMessageMainObject> call, Response<SendMessageMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUpdateChat());
                            EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
                            ((MainActivity) getActivity()).showMessageDialog("success", "Group chat thread created" + "\n" + response.body().getMessage());
                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
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

    private MultipartBody.Part prepareFilePart(String partName, String fileUri) {
        File file = new File(fileUri);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }

    public void sendMultiGroupAttachment(ArrayList<File> file) {
        imageisChecked = false;
        String groupMessage = binding.editTextMessage.getText().toString();
        binding.editTextMessage.setText("");
        List<MultipartBody.Part> filePart = new ArrayList<>();
        final RequestBody threadId = RequestBody.create(MediaType.parse("text/plain"), groupChatThreadId);
        final RequestBody senderId = RequestBody.create(MediaType.parse("text/plain"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        final RequestBody message = RequestBody.create(MediaType.parse("text/plain"), groupMessage);
        final RequestBody senderProfileId = RequestBody.create(MediaType.parse("text/plain"), activeProfileId);
        for (int i = 0; i < files.size(); i++) {
            filePart.add(prepareFilePart("attachment[" + i + "]", file.get(i).getAbsolutePath()));
        }
        sendAttachementsModelCall = Api.WEB_SERVICE.sendGroupMultipleAttachment(EndpointKeys.X_API_KEY,
                Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                filePart,
                threadId,
                senderId,
                message,
                senderProfileId);
        sendAttachementsModelCall.enqueue(new Callback<SendAttachementsModel>() {
            @Override
            public void onResponse(Call<SendAttachementsModel> call, Response<SendAttachementsModel> response) {
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUpdateChat());
                            EventBus.getDefault().post(new EventBusUserSession("msg_sent"));
                            ((MainActivity) getActivity()).showMessageDialog("success", "Group chat thread created" + "\n" + response.body().getMessage());
                            getActivity().onBackPressed();
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

    //    Method to get all imported contacts or connections from server....
//    private void reqContacts(String searchKey, String searchFilter) {
//        userContactsDataList.clear();
//        userContactsDataListLNQ.clear();
//        userContactsDataListImported.clear();
//        if (searchKey.isEmpty()) {
//            userContactsDataTempListImported.clear();
//        }
//        callUserConnections = Api.WEB_SERVICE.contacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().
//                sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().
//                sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().
//                sharedPreferences.getString(EndpointKeys.ID, ""), searchKey, searchFilter, activeProfileId);
//        callUserConnections.enqueue(new Callback<UserConnectionsMainObject>() {
//            public void onResponse(Call<UserConnectionsMainObject> call, Response<UserConnectionsMainObject> response) {
//                if (response != null && response.isSuccessful()) {
//                    switch (response.body().getStatus()) {
//                        case 1:
//                            userContactList = response.body().getUserContacts();
//                            for (int i = 0; i < userContactList.size(); i++) {
//                                UserConnections userContact = userContactList.get(i);
//                                if (!userContact.getUser_data().getUser_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")))
//                                    if (userContact.getUser_data().getContact_status().equals("not_lnqed")) {
//                                        userContactsDataListImported.add(userContact.getUser_data());
//                                    } else {
//                                        if (userContact.getNote_on_user() != null) {
//                                            userContact.getUser_data().setUserNote(userContact.getNote_on_user().getNote_description());
//                                        }
//                                        userContactsDataListLNQ.add(userContact.getUser_data());
//                                    }
//                            }
//                            userContactsDataList.addAll(userContactsDataListLNQ);
//                            userContactsDataList.addAll(userContactsDataListImported);
//                            for (int i = 0; i < userContactsDataListImported.size(); i++) {
//                                UserConnectionsData userContactsData = userContactsDataListImported.get(i);
//                                if (userContactsData.getUser_fname() == null && userContactsData.getUser_lname() == null) {
//                                    userContactsData.setUser_fname("");
//                                    userContactsData.setUser_lname("");
//                                }
//                                if (userContactsData.getUser_current_position() == null) {
//                                    userContactsData.setUser_current_position("");
//                                }
//                                if ((userContactsData.getUser_fname().trim() + userContactsData.getUser_lname().trim()).isEmpty()) {
//                                    if (!userContactsData.getContact_name().isEmpty()) {
//                                        userContactsData.setUser_fname(userContactsData.getContact_name());
//                                    } else if (!userContactsData.getUser_phone().isEmpty()) {
//                                        userContactsData.setUser_fname(userContactsData.getUser_phone());
//                                    } else {
//                                        userContactsData.setUser_fname(userContactsData.getUser_email());
//                                    }
//                                }
//                            }
//                            if (searchKey.isEmpty()) {
//                                userContactsDataTempListImported.addAll(userContactsDataListImported);
//                            }
//                            EventBus.getDefault().post(new EventBusConnectionView());
//                            ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
//                            for (int i = 1; i < userContactsDataListLNQ.size(); i++) {
//                                if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(userContactsDataListLNQ.get(i).getUser_id())) {
//                                    mentionModelArrayList.add(new MentionModel(userContactsDataListLNQ.get(i).getUser_id(), userContactsDataListLNQ.get(i).getUser_fname() + " " + userContactsDataListLNQ.get(i).getUser_lname().toLowerCase(), userContactsDataListLNQ.get(i).getUser_avatar(), userContactsDataListLNQ.get(i).getProfile_id()));
//                                }
//                            }
//                            MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
//                            binding.tagsEditTextNewMessage.setAdapter(mentionChatAdapter);
//                            binding.tagsEditTextNewMessage.setThreshold(1);
//                            binding.tagsEditTextNewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    ValidUtils.hideKeyboardFromFragment(getActivity(), binding.getRoot());
//                                    binding.tagsEditTextNewMessage.setText(binding.tagsEditTextNewMessage.getText());
//                                    reqContacts(binding.tagsEditTextNewMessage.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""));
//                                }
//                            });
//                            break;
//                        case 0:
//                            break;
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserConnectionsMainObject> call, Throwable error) {
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
}