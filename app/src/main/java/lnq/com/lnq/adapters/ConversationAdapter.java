package lnq.com.lnq.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.teleclinic.bulent.smartimageview.SmartImageViewLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.model.event_bus_models.EventBusConversationProfileClick;
import lnq.com.lnq.model.event_bus_models.EventBusGroupChatClicked;
import lnq.com.lnq.model.event_bus_models.EventBusMuteChat;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusChatClick;
import lnq.com.lnq.model.gson_converter_models.conversation.GetChatThread;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.UserConversationViewHolder> {

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<GetChatThread> userChatThread = new ArrayList<>();

    public ConversationAdapter(Context context, List<GetChatThread> userChatThread) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.userChatThread = userChatThread;
        fontUtils = FontUtils.getFontUtils(context);
        createTransferUtility();
        cachePath = context.getCacheDir().getAbsolutePath();
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, context.getApplicationContext());
    }

    @NonNull
    @Override
    public UserConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.row_conversation, viewGroup, false);
        return new UserConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserConversationViewHolder userConversationViewHolder, int i) {
        if (i == 0) {
            userConversationViewHolder.itemView.setVisibility(View.INVISIBLE);
        } else {
            GetChatThread chatThread = userChatThread.get(i);
            userConversationViewHolder.itemView.setVisibility(View.VISIBLE);
            try {
                if (chatThread != null) {

                    String lastMessage = chatThread.getLastMessage();
                    String isConnection = chatThread.getIsConnected();
                    String isFavorite = chatThread.getIsFavorite();
                    String name = chatThread.getUserName();
                    String image = chatThread.getUserAvatar();
                    String count = chatThread.getCount();
                    String isMuted = chatThread.getIs_muted();
                    String groupName = chatThread.getGroup_name();
                    String groupchatThreadId = chatThread.getGroupchat_thread_id();
                    String groupUserNames = chatThread.getUserNames();

                    List<String> data = Arrays.asList(lastMessage.split(" "));
                    for(int j = 0; j < data.size(); j++){
                        if(data.get(j).contains("/")){
                            String newWord = data.get(j).replaceAll("/", "").replaceAll("[0-9]","");
                            data.set(j,"@"+newWord);
                        }
                    }
                    lastMessage = "";
                    for(String word : data){
                        lastMessage = lastMessage + " " + word;
                    }

                    if (count.equalsIgnoreCase("0") || count.equalsIgnoreCase("") ||
                            isMuted.equals("muted")) {
                        userConversationViewHolder.textViewCount.setVisibility(View.GONE);
                    } else {
                        userConversationViewHolder.textViewCount.setVisibility(View.VISIBLE);
                        userConversationViewHolder.textViewCount.setText(count);
                    }
                    if (isMuted.equals("muted")) {
                        userConversationViewHolder.imageViewMute.setImageResource(R.drawable.mute_icon);
                        userConversationViewHolder.imageViewMute.setColorFilter(ContextCompat.getColor(context, R.color.colorBlackHintNewTheme));
                    } else {
                        userConversationViewHolder.imageViewMute.setImageResource(0);
                    }
                    String draftMessage = "";
                    if (LnqApplication.getInstance().draftHasMap.containsKey(chatThread.getThreadId())) {
                        draftMessage = LnqApplication.getInstance().draftHasMap.get(chatThread.getThreadId());
                    }
                    if (!draftMessage.isEmpty()) {
                        userConversationViewHolder.userMessageTextView.setText("[draft] " + draftMessage);
                    } else {
                        userConversationViewHolder.userMessageTextView.setText(lastMessage);
                    }

                    try {
                        String time = DateUtils.getDateForConversation(chatThread.getLastMessageTime());
                        if (time.startsWith("0")) {
                            time = time.substring(1);
                        }
                        userConversationViewHolder.userTimeTextView.setText(time);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (groupchatThreadId != null) {
                        userConversationViewHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                        userConversationViewHolder.imageViewFavoriteBorder1.setVisibility(View.VISIBLE);
//                        userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.VISIBLE);
//                        userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.VISIBLE);
//                        userConversationViewHolder.userImageView.setBackground(null);
                        if (groupName != null && !groupName.isEmpty()) {
                            userConversationViewHolder.userNametextView.setText(groupName);
                        } else {
                            userConversationViewHolder.userNametextView.setText(groupUserNames);
                        }
                        String groupDraftMessage = "";
                        if (LnqApplication.getInstance().draftHasMap.containsKey(chatThread.getGroupchat_thread_id())) {
                            groupDraftMessage = LnqApplication.getInstance().draftHasMap.get(chatThread.getGroupchat_thread_id());
                        }
                        if (!groupDraftMessage.isEmpty()) {
                            userConversationViewHolder.userMessageTextView.setText("[draft] " + groupDraftMessage);
                        } else {
                            userConversationViewHolder.userMessageTextView.setText(lastMessage);
                        }
                        List<String> items = Arrays.asList(image.split("\\s*,\\s*"));
//                        String[] arr = items.toArray(new String[0]);

                        if (items.size() != -1) {
                            download(items.get(0), userConversationViewHolder.img1);
                            download(items.get(1), userConversationViewHolder.img2);
                            download(items.get(2), userConversationViewHolder.img3);
                            userConversationViewHolder.img2.setVisibility(View.VISIBLE);
                            userConversationViewHolder.img3.setVisibility(View.VISIBLE);
                        }else {
                            userConversationViewHolder.img2.setVisibility(View.INVISIBLE);
                            userConversationViewHolder.img3.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        userConversationViewHolder.userNametextView.setText(name);
//                        userConversationViewHolder.userImageView.putImages(image);
                        download(image, userConversationViewHolder.img1);
                        userConversationViewHolder.img2.setVisibility(View.INVISIBLE);
                        userConversationViewHolder.img3.setVisibility(View.INVISIBLE);
                        userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.GONE);
                        userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.GONE);
                        if (isFavorite.equals(Constants.FAVORITE)) {
//                            userConversationViewHolder.userImageView.setBackground(null);
                            switch (isConnection) {
                                case Constants.CONTACTED:
                                    userConversationViewHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setVisibility(View.VISIBLE);
                                    userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setBackground(context.getResources().getDrawable(R.drawable.ic_border_request_favorite));
//                                    userConversationViewHolder.imageViewFavoriteBorder2.setBackground(context.getResources().getDrawable(R.drawable.ic_border_request_favorite));
//                                    userConversationViewHolder.imageViewFavoriteBorder3.setBackground(context.getResources().getDrawable(R.drawable.ic_border_request_favorite));
                                    break;
                                case Constants.CONNECTED:
                                    userConversationViewHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setVisibility(View.VISIBLE);
                                    userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setBackground(context.getResources().getDrawable(R.drawable.ic_action_fav_connected));
//                                    userConversationViewHolder.imageViewFavoriteBorder2.setBackground(context.getResources().getDrawable(R.drawable.ic_action_fav_connected));
//                                    userConversationViewHolder.imageViewFavoriteBorder3.setBackground(context.getResources().getDrawable(R.drawable.ic_action_fav_connected));
                                    break;
                                default:
                                    userConversationViewHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setVisibility(View.VISIBLE);
                                    userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setBackground(context.getResources().getDrawable(R.drawable.ic_fav_lnq_border));
//                                    userConversationViewHolder.imageViewFavoriteBorder2.setBackground(context.getResources().getDrawable(R.drawable.ic_fav_lnq_border));
//                                    userConversationViewHolder.imageViewFavoriteBorder3.setBackground(context.getResources().getDrawable(R.drawable.ic_fav_lnq_border));
                                    break;
                            }
                        } else {
                            switch (isConnection) {
                                case Constants.CONTACTED:
                                    userConversationViewHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.GONE);
//                                    userConversationViewHolder.userImageView.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                                    userConversationViewHolder.img1.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
//                                    userConversationViewHolder.img2.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
//                                    userConversationViewHolder.img3.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                                    break;
                                case Constants.CONNECTED:
                                    userConversationViewHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.GONE);
//                                    userConversationViewHolder.userImageView.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                                    userConversationViewHolder.img1.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
//                                    userConversationViewHolder.img2.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
//                                    userConversationViewHolder.img3.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                                    break;
                                default:
                                    userConversationViewHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder1.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder2.setVisibility(View.GONE);
                                    userConversationViewHolder.imageViewFavoriteBorder3.setVisibility(View.GONE);
//                                    userConversationViewHolder.userImageView.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                                    userConversationViewHolder.img1.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
//                                    userConversationViewHolder.img2.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
//                                    userConversationViewHolder.img3.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public int getItemCount() {
        return userChatThread.size();
    }

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            Glide.with(context).
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
                        Glide.with(context).
                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                                apply(new RequestOptions().circleCrop()).
                                apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
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

    public class UserConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userNametextView, userTimeTextView, textViewCount;
        TextView userMessageTextView;
        ImageView imageViewMute;
        AppCompatImageView img1, img2, img3;

        ImageView imageViewFavoriteBorder, imageViewFavoriteBorder1, imageViewFavoriteBorder2, imageViewFavoriteBorder3;
        Dialog dialog;
        View dialogView;

        public UserConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            userNametextView = itemView.findViewById(R.id.textViewUserName);
            userMessageTextView = itemView.findViewById(R.id.textViewLastMessage);
            userTimeTextView = itemView.findViewById(R.id.textViewLastMessageTime);
            textViewCount = itemView.findViewById(R.id.textViewCount);
//            userImageView = itemView.findViewById(R.id.imageViewProfile);
            imageViewMute = itemView.findViewById(R.id.imageViewMute);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            imageViewFavoriteBorder = itemView.findViewById(R.id.imageViewProfileFavoriteBorder);
            imageViewFavoriteBorder1 = itemView.findViewById(R.id.imageViewProfileFavoriteBorder1);
            imageViewFavoriteBorder2 = itemView.findViewById(R.id.imageViewProfileFavoriteBorder2);
            imageViewFavoriteBorder3 = itemView.findViewById(R.id.imageViewProfileFavoriteBorder3);

            fontUtils.setTextViewSemiBold(userNametextView);
            fontUtils.setTextViewRegularFont(userMessageTextView);
            fontUtils.setTextViewRegularFont(userTimeTextView);
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != 0) {
                        EventBus.getDefault().post(new EventBusConversationProfileClick(getAdapterPosition()));
                    }
                }
            });

            itemView.setOnClickListener(this);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (userChatThread.get(getAdapterPosition()).getIs_muted().equals("muted")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        dialogView = inflater.inflate(R.layout.cus_dialog_unmutechat, null);
                        TextView textCancel = dialogView.findViewById(R.id.textViewCancel);
                        TextView textViewUnMute = dialogView.findViewById(R.id.textViewUnMute);

                        textViewUnMute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getAdapterPosition() != 0) {
                                    EventBus.getDefault().post(new EventBusMuteChat(getAdapterPosition(), 0));
                                }
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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        dialogView = inflater.inflate(R.layout.cus_dialog_mutechat, null);
                        TextView textOneHour = dialogView.findViewById(R.id.textViewOneHour);
                        TextView textMidNight = dialogView.findViewById(R.id.textViewMidNight);
                        TextView textUnTill = dialogView.findViewById(R.id.textViewUnTill);
                        TextView textCancel = dialogView.findViewById(R.id.textViewCancel);

                        textOneHour.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getAdapterPosition() != 0) {
                                    EventBus.getDefault().post(new EventBusMuteChat(getAdapterPosition(), 1));
                                }
                                dialog.dismiss();
                            }
                        });

                        textMidNight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new EventBusMuteChat(getAdapterPosition(), 2));
                                dialog.dismiss();
                            }
                        });

                        textUnTill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new EventBusMuteChat(getAdapterPosition(), 3));
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
                    return false;
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != 0)
                if (userChatThread.get(getAdapterPosition()).getGroupchat_thread_id() == null) {
//                    userChatThread.get(getAdapterPosition()).getSenderId().equals(userChatThread.get(getAdapterPosition()).getSender_profile_id()) ? userChatThread.get(getAdapterPosition()).getReceiver_profile_id() : userChatThread.get(getAdapterPosition()).getSender_profile_id()
                    EventBus.getDefault().post(new EventBusChatClick(getAdapterPosition(), userChatThread.get(getAdapterPosition()).getSenderId().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? userChatThread.get(getAdapterPosition()).getReceiverId() : userChatThread.get(getAdapterPosition()).getSenderId(), userChatThread.get(getAdapterPosition()).getReceiver_profile_id().equals(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "")) ? userChatThread.get(getAdapterPosition()).getSender_profile_id() : userChatThread.get(getAdapterPosition()).getReceiver_profile_id()));
                } else {
                    EventBus.getDefault().post(new EventBusGroupChatClicked(getAdapterPosition(), userChatThread.get(getAdapterPosition()).getGroupchat_thread_id(), userChatThread.get(getAdapterPosition()).getParticipant_profile_ids()));
                }
        }
    }
}