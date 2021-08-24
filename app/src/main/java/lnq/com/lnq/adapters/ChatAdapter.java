package lnq.com.lnq.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
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

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.listeners.activit_listeners.SwipeDetector;
import lnq.com.lnq.model.event_bus_models.EventBusChatGestures;
import lnq.com.lnq.model.event_bus_models.EventBusChatShareContactClick;
import lnq.com.lnq.model.event_bus_models.EventBusChatThreadClick;
import lnq.com.lnq.model.event_bus_models.EventBusSaveChatImagesToGallery;
import lnq.com.lnq.model.event_bus_models.EventBussFailedMsg;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatData;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String cachePath = "";
    private TransferUtility transferUtility;
    String voiceUrl = "";
    String durationText;
    MediaPlayer myPlayer;
    LottieAnimationView previousRecording;
    AppCompatImageView previousPlay, previousStop;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<GetChatData> chatDataList = new ArrayList<>();
    private String userImage, isConnection, isFavorite;

    //    Font fields....
    private FontUtils fontUtils;

    public ChatAdapter(Context context, List<GetChatData> chatDataList, String userImage, String isConnected, String isFavorite) {
        this.context = context;
        this.chatDataList = chatDataList;
        this.userImage = userImage;
        this.isConnection = isConnected;
        this.isFavorite = isFavorite;
        layoutInflater = LayoutInflater.from(context);
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        if (i == 0) {
            view = layoutInflater.inflate(R.layout.row_chat_sender, viewGroup, false);
            viewHolder = new ChatSenderHolder(view);
        } else {
            view = layoutInflater.inflate(R.layout.row_chat_receiver, viewGroup, false);
            viewHolder = new ChatReceiverHolder(view);
        }

        return viewHolder;
    }

    @SuppressLint({"ResourceAsColor", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder chatHolder, final int i) {
        GetChatData chatData = chatDataList.get(i);

        if (chatData != null) {
            try {
                String message = chatData.getMessage();
                String messageTime = chatData.getmessage_time();
                Log.d("UserChat", (chatData.getSharecontact() != null) + "");


                if (chatHolder.getItemViewType() == 0) {
                    ChatSenderHolder senderHolder = (ChatSenderHolder) chatHolder;
                    if (message.isEmpty() && chatData.getSharecontact() == null && chatData.getVoice_attachment().isEmpty()) {
                        List<String> items = Arrays.asList(chatData.getAttachment().split("\\s*,\\s*"));
                        if (items.size() == 2) {
                            senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                            senderHolder.imageViewMessage.setVisibility(View.GONE);
                            senderHolder.img1.setVisibility(View.VISIBLE);
                            senderHolder.img2.setVisibility(View.VISIBLE);
                            senderHolder.img3.setVisibility(View.GONE);
                            senderHolder.img4.setVisibility(View.GONE);
                            senderHolder.img1.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            senderHolder.img2.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
//                            String[] arr = items.toArray(new String[0]);
//                            senderHolder.smartImageViewMessage.putImages(arr);
                            senderHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), senderHolder.img1);
                            download(items.get(1), senderHolder.img2);
                        } else if (items.size() == 3) {
                            senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                            senderHolder.imageViewMessage.setVisibility(View.GONE);
                            senderHolder.img1.setVisibility(View.VISIBLE);
                            senderHolder.img2.setVisibility(View.VISIBLE);
                            senderHolder.img3.setVisibility(View.VISIBLE);
                            senderHolder.img4.setVisibility(View.GONE);
                            senderHolder.img2.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            senderHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), senderHolder.img1);
                            download(items.get(1), senderHolder.img2);
                            download(items.get(2), senderHolder.img3);

                        } else if (items.size() > 3) {
                            senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                            senderHolder.imageViewMessage.setVisibility(View.GONE);
                            senderHolder.img1.setVisibility(View.VISIBLE);
                            senderHolder.img2.setVisibility(View.VISIBLE);
                            senderHolder.img3.setVisibility(View.VISIBLE);
                            senderHolder.img4.setVisibility(View.VISIBLE);
                            senderHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), senderHolder.img1);
                            download(items.get(1), senderHolder.img2);
                            download(items.get(2), senderHolder.img3);
                            download(items.get(3), senderHolder.img4);
                        } else {
                            senderHolder.imageViewMessage.setVisibility(View.VISIBLE);
                            senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                            senderHolder.multiImagesLayout.setVisibility(View.GONE);
                            download(items.get(0), senderHolder.imageViewMessage);
                        }
                        senderHolder.textViewMessage.setVisibility(View.GONE);
                        senderHolder.textViewTextMessage.setVisibility(View.GONE);
                        senderHolder.textViewMutliTextMessage.setVisibility(View.GONE);
                        senderHolder.imageViewTextMessage.setVisibility(View.GONE);
                        senderHolder.cardViewSharePrifile.setVisibility(View.GONE);
                        senderHolder.voiceLayout.setVisibility(View.GONE);
                    } else if (!message.isEmpty() && !chatData.getAttachment().isEmpty() && chatData.getVoice_attachment().isEmpty()) {
                        senderHolder.textViewMessage.setVisibility(View.INVISIBLE);
                        senderHolder.imageViewMessage.setVisibility(View.GONE);
                        senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                        senderHolder.multiImagesLayout.setVisibility(View.GONE);
                        senderHolder.cardViewSharePrifile.setVisibility(View.GONE);
                        senderHolder.voiceLayout.setVisibility(View.GONE);
                        List<String> items = Arrays.asList(chatData.getAttachment().split("\\s*,\\s*"));
//                        String[] arr = items.toArray(new String[0]);
//                        if (arr.length > 1) {
//                            senderHolder.smartImageViewTextMessage.setVisibility(View.VISIBLE);
//                            senderHolder.smartImageViewTextMessage.putImages(arr);
//                            senderHolder.textViewTextMessage.setVisibility(View.GONE);
//                            senderHolder.textViewMutliTextMessage.setVisibility(View.VISIBLE);
//                            senderHolder.textViewMutliTextMessage.setAutoLinkText(message);
                        if (items.size() == 2) {
                            senderHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                            senderHolder.textViewTextMessage.setVisibility(View.GONE);
                            senderHolder.textViewMutliTextMessage.setVisibility(View.VISIBLE);
                            senderHolder.textViewMutliTextMessage.setAutoLinkText(message);
                            senderHolder.img1Text.setVisibility(View.VISIBLE);
                            senderHolder.img2Text.setVisibility(View.VISIBLE);
                            senderHolder.img3Text.setVisibility(View.GONE);
                            senderHolder.img4Text.setVisibility(View.GONE);
                            senderHolder.img1Text.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            senderHolder.img2Text.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            senderHolder.multiImagesLayoutText.setVisibility(View.VISIBLE);
                            download(items.get(0), senderHolder.img1Text);
                            download(items.get(1), senderHolder.img2Text);
                        } else if (items.size() == 3) {
                            senderHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                            senderHolder.textViewTextMessage.setVisibility(View.GONE);
                            senderHolder.textViewMutliTextMessage.setVisibility(View.VISIBLE);
                            senderHolder.textViewMutliTextMessage.setAutoLinkText(message);
                            senderHolder.img1Text.setVisibility(View.VISIBLE);
                            senderHolder.img2Text.setVisibility(View.VISIBLE);
                            senderHolder.img3Text.setVisibility(View.VISIBLE);
                            senderHolder.img4Text.setVisibility(View.GONE);
                            senderHolder.img2Text.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            senderHolder.multiImagesLayoutText.setVisibility(View.VISIBLE);
                            download(items.get(0), senderHolder.img1Text);
                            download(items.get(1), senderHolder.img2Text);
                            download(items.get(2), senderHolder.img3Text);

                        } else if (items.size() > 3) {
                            senderHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                            senderHolder.textViewTextMessage.setVisibility(View.GONE);
                            senderHolder.textViewMutliTextMessage.setVisibility(View.VISIBLE);
                            senderHolder.textViewMutliTextMessage.setAutoLinkText(message);
                            senderHolder.img1Text.setVisibility(View.VISIBLE);
                            senderHolder.img2Text.setVisibility(View.VISIBLE);
                            senderHolder.img3Text.setVisibility(View.VISIBLE);
                            senderHolder.img4Text.setVisibility(View.VISIBLE);
                            senderHolder.multiImagesLayoutText.setVisibility(View.VISIBLE);
                            download(items.get(0), senderHolder.img1Text);
                            download(items.get(1), senderHolder.img2Text);
                            download(items.get(2), senderHolder.img3Text);
                            download(items.get(3), senderHolder.img4Text);
                        } else {
                            download(items.get(0), senderHolder.imageViewTextMessage);
                            senderHolder.imageViewTextMessage.setVisibility(View.VISIBLE);
                            senderHolder.textViewMutliTextMessage.setVisibility(View.VISIBLE);
                            senderHolder.textViewMutliTextMessage.setAutoLinkText(message);
                            senderHolder.multiImagesLayoutText.setVisibility(View.GONE);
                        }

                    } else if (chatData.getSharecontact() != null) {
                        senderHolder.textViewTextMessage.setVisibility(View.GONE);
                        senderHolder.textViewMutliTextMessage.setVisibility(View.GONE);
                        senderHolder.imageViewTextMessage.setVisibility(View.GONE);
                        senderHolder.textViewMessage.setVisibility(View.INVISIBLE);
                        senderHolder.imageViewMessage.setVisibility(View.GONE);
                        senderHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                        senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                        senderHolder.multiImagesLayout.setVisibility(View.GONE);
                        senderHolder.multiImagesLayoutText.setVisibility(View.GONE);
                        senderHolder.voiceLayout.setVisibility(View.GONE);
                        senderHolder.cardViewSharePrifile.setVisibility(View.VISIBLE);
                        downloadUserImage(chatData.getSharecontact().getUser_profile(), senderHolder.imageViewShareProfile);
                        senderHolder.textViewShareName.setText(chatData.getSharecontact().getFirst_name() + " " + chatData.getSharecontact().getLast_name());

                    } else if (!chatData.getVoice_attachment().isEmpty()) {
                        senderHolder.imageViewPlayRecording.setBackgroundResource(R.drawable.play_iocn_w);
                        downloadVoiceMsg(chatData.getVoice_attachment());
                        senderHolder.progressBarVoiceNote.setAnimation(R.raw.androidwave);
                        senderHolder.textViewTextMessage.setVisibility(View.GONE);
                        senderHolder.textViewMutliTextMessage.setVisibility(View.GONE);
                        senderHolder.imageViewTextMessage.setVisibility(View.GONE);
                        senderHolder.textViewMessage.setVisibility(View.INVISIBLE);
                        senderHolder.imageViewMessage.setVisibility(View.GONE);
                        senderHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                        senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                        senderHolder.multiImagesLayout.setVisibility(View.GONE);
                        senderHolder.multiImagesLayoutText.setVisibility(View.GONE);
                        senderHolder.voiceLayout.setVisibility(View.VISIBLE);
                        senderHolder.cardViewSharePrifile.setVisibility(View.GONE);
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(voiceUrl);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    int length = mediaPlayer.getDuration();
                                    durationText = android.text.format.DateUtils.formatElapsedTime(length / 1000);
                                    senderHolder.textViewVoiceNoteTime.setText(durationText);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        senderHolder.textViewTextMessage.setVisibility(View.GONE);
                        senderHolder.textViewMutliTextMessage.setVisibility(View.GONE);
                        senderHolder.imageViewTextMessage.setVisibility(View.GONE);
                        senderHolder.textViewMessage.setAutoLinkText(message);
                        senderHolder.textViewMessage.setVisibility(View.VISIBLE);
                        senderHolder.voiceLayout.setVisibility(View.GONE);
                        senderHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                        senderHolder.smartImageViewMessage.setVisibility(View.GONE);
                        senderHolder.multiImagesLayout.setVisibility(View.GONE);
                        senderHolder.multiImagesLayoutText.setVisibility(View.GONE);
                        senderHolder.imageViewMessage.setVisibility(View.GONE);
                        senderHolder.cardViewSharePrifile.setVisibility(View.GONE);
                    }

                    if (chatDataList.get(i).isSwipeText()) {
                        try {
                            Calendar timeNeeded = Calendar.getInstance();
                            timeNeeded.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(chatData.getmessage_time()).getTime());
                            String time = "" + DateFormat.format("HH:mm a", timeNeeded);
                            time = DateUtils.getLocalConvertedTime(time);
                            senderHolder.textViewDate.setVisibility(View.VISIBLE);
                            senderHolder.textViewDate.setText(SortingUtils.formateDate(time));
                            senderHolder.textViewMessage.setMaxWidth(450);
                        } catch (ParseException e) {

                        }
                    } else {
                        senderHolder.textViewMessage.setMaxWidth(450);
                        senderHolder.textViewDate.setVisibility(View.GONE);
                    }

                    if (chatData.isSent().equals("0")) {
                        senderHolder.imageViewMsgFailed.setVisibility(View.VISIBLE);
                        senderHolder.textViewDelivered.setVisibility(View.VISIBLE);
                        senderHolder.textViewSent.setVisibility(View.GONE);
                        senderHolder.textViewSent1.setVisibility(View.GONE);
                        senderHolder.textViewSent2.setVisibility(View.GONE);
                        senderHolder.imageViewDelivered.setVisibility(View.GONE);
                        senderHolder.imageViewDelivered1.setVisibility(View.GONE);
                        senderHolder.imageViewDelivered2.setVisibility(View.GONE);
                    } else {
                        senderHolder.imageViewMsgFailed.setVisibility(View.GONE);
                        senderHolder.textViewDelivered.setVisibility(View.GONE);
                        if (message.isEmpty() && chatData.getSharecontact() == null && chatData.getVoice_attachment().isEmpty()) {
                            senderHolder.textViewSent.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered.setVisibility(View.GONE);
                            senderHolder.textViewSent2.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered2.setVisibility(View.GONE);
                            if (chatDataList.get(i).getPosition()) {
                                senderHolder.textViewSent1.setVisibility(View.VISIBLE);
                                senderHolder.imageViewDelivered1.setVisibility(View.VISIBLE);
                            } else {
                                senderHolder.textViewSent1.setVisibility(View.GONE);
                                senderHolder.imageViewDelivered1.setVisibility(View.GONE);
                            }
                        } else if (!message.isEmpty() && !chatData.getAttachment().isEmpty() && chatData.getVoice_attachment().isEmpty()) {
                            senderHolder.textViewSent1.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered1.setVisibility(View.GONE);
                            senderHolder.textViewSent.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered.setVisibility(View.GONE);
                            if (chatDataList.get(i).getPosition()) {
                                senderHolder.textViewSent2.setVisibility(View.VISIBLE);
                                senderHolder.imageViewDelivered2.setVisibility(View.VISIBLE);
                            } else {
                                senderHolder.textViewSent2.setVisibility(View.GONE);
                                senderHolder.imageViewDelivered2.setVisibility(View.GONE);
                            }
                        } else if (chatData.getSharecontact() != null) {
                            senderHolder.textViewSent1.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered1.setVisibility(View.GONE);
                            senderHolder.textViewSent2.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered2.setVisibility(View.GONE);
                            if (chatDataList.get(i).getPosition()) {
                                senderHolder.textViewSent.setVisibility(View.VISIBLE);
                                senderHolder.imageViewDelivered.setVisibility(View.VISIBLE);
                            } else {
                                senderHolder.textViewSent.setVisibility(View.GONE);
                                senderHolder.imageViewDelivered.setVisibility(View.GONE);
                            }
                        } else if (!chatData.getVoice_attachment().isEmpty()) {
                            senderHolder.textViewSent1.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered1.setVisibility(View.GONE);
                            senderHolder.textViewSent2.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered2.setVisibility(View.GONE);
                            if (chatDataList.get(i).getPosition()) {
                                senderHolder.textViewSent.setVisibility(View.VISIBLE);
                                senderHolder.imageViewDelivered.setVisibility(View.VISIBLE);
                            } else {
                                senderHolder.textViewSent.setVisibility(View.GONE);
                                senderHolder.imageViewDelivered.setVisibility(View.GONE);
                            }

                        } else {
                            senderHolder.textViewSent1.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered1.setVisibility(View.GONE);
                            senderHolder.textViewSent2.setVisibility(View.GONE);
                            senderHolder.imageViewDelivered2.setVisibility(View.GONE);
                            if (chatDataList.get(i).getPosition()) {
                                senderHolder.textViewSent.setVisibility(View.VISIBLE);
                                senderHolder.imageViewDelivered.setVisibility(View.VISIBLE);
                            } else {
                                senderHolder.textViewSent.setVisibility(View.GONE);
                                senderHolder.imageViewDelivered.setVisibility(View.GONE);
                            }
                        }
                    }

                    String formateDate = "";
                    String lastFormateDate = "";
                    String messageDateTime = "";
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(messageTime);
                        formateDate = DateUtils.getMyPrettyDate(date.getTime());
                    } catch (ParseException e) {

                    }
                    if (i == 0) {
                        try {
                            Calendar neededTime = Calendar.getInstance();
                            neededTime.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(messageTime).getTime());
                            messageDateTime = formateDate + ", " + DateFormat.format("HH:mm a", neededTime);
                        } catch (Exception e) {

                        }
                        senderHolder.textViewTime.setVisibility(View.VISIBLE);

                    } else {
                        try {
                            Date lastDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(chatDataList.get(i - 1).getmessage_time());
                            lastFormateDate = DateUtils.getMyPrettyDate(lastDate.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (formateDate.equals(lastFormateDate)) {
                            senderHolder.textViewTime.setVisibility(View.GONE);
                        } else {
                            try {
                                Calendar neededTime = Calendar.getInstance();
                                neededTime.setTimeInMillis(new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a").parse(messageTime).getTime());
                                messageDateTime = formateDate + ", " + DateFormat.format("HH:mm a", neededTime);
                            } catch (Exception e) {

                            }
                            senderHolder.textViewTime.setVisibility(View.VISIBLE);
                        }
                    }
                    senderHolder.textViewTime.setText(DateUtils.getDate(messageTime));

                    if (i > 0) {
                        if (chatData.getsender_id().equals(chatDataList.get(i - 1).getsender_id())) {
                            senderHolder.imageViewArrow.setVisibility(View.INVISIBLE);
                        } else {
                            senderHolder.imageViewArrow.setVisibility(View.VISIBLE);
                        }
                    } else {
                        senderHolder.imageViewArrow.setVisibility(View.VISIBLE);
                    }


                } else {
                    ChatReceiverHolder receiverHolder = (ChatReceiverHolder) chatHolder;

                    String formateDate = "";
                    String lastFormateDate = "";
                    String messageDateTime = "";
                    String datetextView = "";

                    if (chatDataList.get(i).isSwipeText()) {
                        try {
                            Calendar timeNeeded = Calendar.getInstance();
                            timeNeeded.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(chatData.getmessage_time()).getTime());
                            String time = "" + DateFormat.format("HH:mm a", timeNeeded);
                            time = DateUtils.getLocalConvertedTime(time);
                            receiverHolder.textViewDate.setVisibility(View.VISIBLE);
                            receiverHolder.textViewDate.setText(SortingUtils.formateDate(time));
                            receiverHolder.textViewMessage.setMaxWidth(450);
                        } catch (ParseException e) {

                        }
                    } else {
                        receiverHolder.textViewDate.setVisibility(View.GONE);
                        receiverHolder.textViewMessage.setMaxWidth(450);
                    }

                    if (chatDataList.get(i).isMessageUnread()) {
                        receiverHolder.viewUnRead.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (context != null) {
                                    chatDataList.get(i).setMessageUnread(false);
                                    notifyItemChanged(i);
                                }
                            }
                        }, 3000);
                    } else {
                        receiverHolder.viewUnRead.setVisibility(View.GONE);
                    }

                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(messageTime);
                        formateDate = DateUtils.getMyPrettyDate(date.getTime());
                    } catch (ParseException e) {

                    }
                    if (i == 0) {
                        receiverHolder.textViewTime.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            Date lastDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(chatDataList.get(i - 1).getmessage_time());
                            lastFormateDate = DateUtils.getMyPrettyDate(lastDate.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (formateDate.equals(lastFormateDate)) {
                            receiverHolder.textViewTime.setVisibility(View.GONE);
                        } else {
                            receiverHolder.textViewTime.setVisibility(View.VISIBLE);
                        }
                    }
                    if (i > 0) {
                        if (chatData.getreceiver_id().equals(chatDataList.get(i - 1).getreceiver_id())) {
                            receiverHolder.imageViewProfile.setVisibility(View.INVISIBLE);
//                            receiverHolder.textViewUserName.setVisibility(View.INVISIBLE);
                            receiverHolder.imageViewFavoriteBorder.setVisibility(View.INVISIBLE);
                            receiverHolder.imageViewArrow.setVisibility(View.INVISIBLE);
                        } else {
                            receiverHolder.imageViewProfile.setVisibility(View.VISIBLE);
//                            receiverHolder.textViewUserName.setVisibility(View.VISIBLE);
                            setUserConnectionStatus(receiverHolder);
                            setUserImage(receiverHolder.imageViewProfile);
                            receiverHolder.imageViewArrow.setVisibility(View.VISIBLE);
                        }
                    } else {
                        receiverHolder.imageViewArrow.setVisibility(View.VISIBLE);
                        receiverHolder.imageViewProfile.setVisibility(View.VISIBLE);
//                        receiverHolder.textViewUserName.setVisibility(View.VISIBLE);
                        setUserConnectionStatus(receiverHolder);

                    }
                    String time = chatData.getmessage_time();
                    receiverHolder.textViewTime.setText(DateUtils.getDate(time));
                    if (message.isEmpty() && chatData.getSharecontact() == null && chatData.getVoice_attachment().isEmpty()) {
                        List<String> items = Arrays.asList(chatData.getAttachment().split("\\s*,\\s*"));
//                        String[] arr = items.toArray(new String[0]);
//                        if (arr.length > 1) {
//                            receiverHolder.smartImageViewMessage.putImages(arr);
//                            receiverHolder.smartImageViewMessage.setVisibility(View.VISIBLE);
//                            receiverHolder.imageViewMessage.setVisibility(View.GONE);
                        if (items.size() == 2) {
                            receiverHolder.smartImageViewMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewMessage.setVisibility(View.GONE);
                            receiverHolder.img1.setVisibility(View.VISIBLE);
                            receiverHolder.img2.setVisibility(View.VISIBLE);
                            receiverHolder.img3.setVisibility(View.GONE);
                            receiverHolder.img4.setVisibility(View.GONE);
                            receiverHolder.img1.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            receiverHolder.img2.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            receiverHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), receiverHolder.img1);
                            download(items.get(1), receiverHolder.img2);
                        } else if (items.size() == 3) {
                            receiverHolder.smartImageViewMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewMessage.setVisibility(View.GONE);
                            receiverHolder.img1.setVisibility(View.VISIBLE);
                            receiverHolder.img2.setVisibility(View.VISIBLE);
                            receiverHolder.img3.setVisibility(View.VISIBLE);
                            receiverHolder.img4.setVisibility(View.GONE);
                            receiverHolder.img2.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            receiverHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), receiverHolder.img1);
                            download(items.get(1), receiverHolder.img2);
                            download(items.get(2), receiverHolder.img3);

                        } else if (items.size() > 3) {
                            receiverHolder.smartImageViewMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewMessage.setVisibility(View.GONE);
                            receiverHolder.img1.setVisibility(View.VISIBLE);
                            receiverHolder.img2.setVisibility(View.VISIBLE);
                            receiverHolder.img3.setVisibility(View.VISIBLE);
                            receiverHolder.img4.setVisibility(View.VISIBLE);
                            receiverHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), receiverHolder.img1);
                            download(items.get(1), receiverHolder.img2);
                            download(items.get(2), receiverHolder.img3);
                            download(items.get(3), receiverHolder.img4);
                        } else {
                            receiverHolder.smartImageViewMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewMessage.setVisibility(View.VISIBLE);
                            receiverHolder.multiImagesLayout.setVisibility(View.GONE);
                            download(items.get(0), receiverHolder.imageViewMessage);
                        }
                        receiverHolder.textViewMessage.setVisibility(View.INVISIBLE);
                        receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewMultiTextMessage.setVisibility(View.GONE);
                        receiverHolder.cardViewSharePrifile.setVisibility(View.GONE);
                        receiverHolder.voiceLayout.setVisibility(View.GONE);
                    } else if (!message.isEmpty() && !chatData.getAttachment().isEmpty() && chatData.getVoice_attachment().isEmpty()) {
                        receiverHolder.textViewMessage.setVisibility(View.INVISIBLE);
                        receiverHolder.imageViewMessage.setVisibility(View.GONE);
                        receiverHolder.cardViewSharePrifile.setVisibility(View.GONE);
                        receiverHolder.voiceLayout.setVisibility(View.GONE);
                        List<String> items = Arrays.asList(chatData.getAttachment().split("\\s*,\\s*"));
//                        String[] arr = items.toArray(new String[0]);
//                        if (arr.length > 1) {
//                            receiverHolder.smartImageViewTextMessage.putImages(arr);
//                            receiverHolder.smartImageViewTextMessage.setVisibility(View.VISIBLE);
//                            receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
//                            receiverHolder.textViewMultiTextMessage.setVisibility(View.VISIBLE);
//                            receiverHolder.textViewMultiTextMessage.setAutoLinkText(chatData.getMessage());
//                            receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                        if (items.size() == 2) {
                            receiverHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.textViewMultiTextMessage.setVisibility(View.VISIBLE);
                            receiverHolder.textViewMultiTextMessage.setAutoLinkText(chatData.getMessage());
                            receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.img1Text.setVisibility(View.VISIBLE);
                            receiverHolder.img2Text.setVisibility(View.VISIBLE);
                            receiverHolder.img3Text.setVisibility(View.GONE);
                            receiverHolder.img4Text.setVisibility(View.GONE);
                            receiverHolder.img1Text.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            receiverHolder.img2Text.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            receiverHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), receiverHolder.img1Text);
                            download(items.get(1), receiverHolder.img2Text);
                        } else if (items.size() == 3) {
                            receiverHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.textViewMultiTextMessage.setVisibility(View.VISIBLE);
                            receiverHolder.textViewMultiTextMessage.setAutoLinkText(chatData.getMessage());
                            receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.img1Text.setVisibility(View.VISIBLE);
                            receiverHolder.img2Text.setVisibility(View.VISIBLE);
                            receiverHolder.img3Text.setVisibility(View.VISIBLE);
                            receiverHolder.img4Text.setVisibility(View.GONE);
                            receiverHolder.img2Text.getLayoutParams().height = ((int) context.getResources().getDimension(R.dimen._200sdp));
                            receiverHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), receiverHolder.img1Text);
                            download(items.get(1), receiverHolder.img2Text);
                            download(items.get(2), receiverHolder.img3Text);
                        } else if (items.size() > 3) {
                            receiverHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.textViewMultiTextMessage.setVisibility(View.VISIBLE);
                            receiverHolder.textViewMultiTextMessage.setAutoLinkText(chatData.getMessage());
                            receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.img1Text.setVisibility(View.VISIBLE);
                            receiverHolder.img2Text.setVisibility(View.VISIBLE);
                            receiverHolder.img3Text.setVisibility(View.VISIBLE);
                            receiverHolder.img4Text.setVisibility(View.VISIBLE);
                            receiverHolder.multiImagesLayout.setVisibility(View.VISIBLE);
                            download(items.get(0), receiverHolder.img1Text);
                            download(items.get(1), receiverHolder.img2Text);
                            download(items.get(2), receiverHolder.img3Text);
                            download(items.get(3), receiverHolder.img4Text);
                        } else {
                            receiverHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                            receiverHolder.imageViewTextMessage.setVisibility(View.VISIBLE);
                            download(items.get(0), receiverHolder.imageViewTextMessage);
                            receiverHolder.textViewTextMessage.setVisibility(View.VISIBLE);
                            receiverHolder.multiImagesLayoutText.setVisibility(View.GONE);
                            receiverHolder.textViewTextMessage.setAutoLinkText(chatData.getMessage());
                            receiverHolder.textViewMultiTextMessage.setVisibility(View.GONE);
                        }
                    } else if (chatData.getSharecontact() != null) {
                        receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewMultiTextMessage.setVisibility(View.GONE);
                        receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewMessage.setVisibility(View.INVISIBLE);
                        receiverHolder.imageViewMessage.setVisibility(View.GONE);
                        receiverHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.smartImageViewMessage.setVisibility(View.GONE);
                        receiverHolder.voiceLayout.setVisibility(View.GONE);
                        receiverHolder.cardViewSharePrifile.setVisibility(View.VISIBLE);
                        downloadUserImage(chatData.getSharecontact().getUser_profile(), receiverHolder.imageViewShareProfile);
                        receiverHolder.textViewShareName.setText(chatData.getSharecontact().getFirst_name() + " " + chatData.getSharecontact().getLast_name());
                    } else if (!chatData.getVoice_attachment().isEmpty()) {
                        receiverHolder.imageViewPlayRecording.setBackgroundResource(R.drawable.play_iocn_b);
                        downloadVoiceMsg(chatData.getVoice_attachment());
                        receiverHolder.progressBarVoiceNote.setAnimation(R.raw.androidwave);
                        receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewMultiTextMessage.setVisibility(View.GONE);
                        receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewMessage.setVisibility(View.INVISIBLE);
                        receiverHolder.imageViewMessage.setVisibility(View.GONE);
                        receiverHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.smartImageViewMessage.setVisibility(View.GONE);
                        receiverHolder.voiceLayout.setVisibility(View.GONE);
                        receiverHolder.cardViewSharePrifile.setVisibility(View.GONE);
                        receiverHolder.voiceLayout.setVisibility(View.VISIBLE);
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(voiceUrl);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    int length = mediaPlayer.getDuration(); // duration in time in millis
                                    durationText = android.text.format.DateUtils.formatElapsedTime(length / 1000); // converting time in millis to minutes:second format eg 14:15 min
                                    receiverHolder.textViewVoiceNoteTime.setText(durationText);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        receiverHolder.imageViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewMultiTextMessage.setVisibility(View.GONE);
                        receiverHolder.textViewMessage.setAutoLinkText(chatData.getMessage());
                        receiverHolder.textViewMessage.setVisibility(View.VISIBLE);
                        receiverHolder.smartImageViewTextMessage.setVisibility(View.GONE);
                        receiverHolder.smartImageViewMessage.setVisibility(View.GONE);
                        receiverHolder.imageViewMessage.setVisibility(View.GONE);
                        receiverHolder.cardViewSharePrifile.setVisibility(View.GONE);
                    }
                    setUserImage(receiverHolder.imageViewProfile);
                }
            } catch (Exception e) {

            }
        }
    }

    private void setUserImage(ImageView imageViewProfile) {
        downloadUserImage(userImage, imageViewProfile);
    }

    private void setUserConnectionStatus(ChatReceiverHolder receiverHolder) {
        if (isFavorite.equals(Constants.FAVORITE)) {
            switch (isConnection) {
                case Constants.CONTACTED:
                    receiverHolder.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                    receiverHolder.imageViewFavoriteBorder.setBackground(context.getResources().getDrawable(R.drawable.ic_border_request_favorite));
                    break;
                case Constants.CONNECTED:
                    receiverHolder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                    receiverHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                    break;
                default:
                    receiverHolder.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                    receiverHolder.imageViewFavoriteBorder.setBackground(context.getResources().getDrawable(R.drawable.ic_fav_lnq_border));
                    break;
            }
        } else {
            switch (isConnection) {
                case Constants.CONTACTED:
                    receiverHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                    receiverHolder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                    break;
                case Constants.CONNECTED:
                    receiverHolder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                    receiverHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                    break;
                default:
                    receiverHolder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                    receiverHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatDataList.get(position).getsender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return chatDataList.size();
    }

    void downloadVoiceMsg(String objectKey) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            voiceUrl = cachePath + "/" + objectKey;
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
                        voiceUrl = fileDownload.getAbsolutePath();
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

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
            Glide.with(context).
                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
                    error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .transform(new CenterCrop(), new RoundedCorners(25))
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
                                error(R.drawable.placeholder)
                                .placeholder(R.drawable.placeholder)
                                .transform(new CenterCrop(), new RoundedCorners(25))
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

    void downloadUserImage(String objectKey, ImageView imageView) {

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

    class ChatSenderHolder extends RecyclerView.ViewHolder {

        TextView textViewTime, textViewDelivered, textViewSent, textViewDate, textViewShareName, textViewShareProfile;
        AutoLinkTextView textViewMessage;
        TextView textViewSent1, textViewSent2, textViewVoiceNoteTime;
        ImageView imageViewArrow, imageViewMsgFailed, imageViewDelivered;
        ImageView imageViewDelivered1, imageViewDelivered2;
        ImageView imageViewTextMessage, imageViewMessage;
        SmartImageViewLayout smartImageViewTextMessage, smartImageViewMessage;
        AppCompatImageView img1, img2, img3, img4, img1Text, img2Text, img3Text, img4Text;
        AppCompatImageView imageViewShareProfile, imageViewPlayRecording;
        AutoLinkTextView textViewTextMessage, textViewMutliTextMessage;
        ConstraintLayout cardViewSharePrifile, multiImagesLayout, multiImagesLayoutText, voiceLayout;
        LottieAnimationView progressBarVoiceNote;

        ChatSenderHolder(@NonNull View itemView) {
            super(itemView);

            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageViewArrow = itemView.findViewById(R.id.imageViewArrowSender);
            imageViewMsgFailed = itemView.findViewById(R.id.imageViewMsgFailed);
            textViewDelivered = itemView.findViewById(R.id.textViewDelivered);
            textViewSent = itemView.findViewById(R.id.textViewSent);
            imageViewDelivered = itemView.findViewById(R.id.imageViewDelivered);
            imageViewMessage = itemView.findViewById(R.id.imageViewMessage);
            smartImageViewTextMessage = itemView.findViewById(R.id.smartImageViewTextMessage);
            smartImageViewMessage = itemView.findViewById(R.id.smartImageViewMessage);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewSent1 = itemView.findViewById(R.id.textViewSent1);
            imageViewDelivered1 = itemView.findViewById(R.id.imageViewDelivered1);
            fontUtils.setTextViewRegularFont(textViewMessage);
            fontUtils.setTextViewRegularFont(textViewTime);
            textViewSent2 = itemView.findViewById(R.id.textViewSent2);
            imageViewDelivered2 = itemView.findViewById(R.id.imageViewDelivered2);
            imageViewTextMessage = itemView.findViewById(R.id.imageViewTextMessage);
            textViewTextMessage = itemView.findViewById(R.id.textViewTextMessage);
            textViewMutliTextMessage = itemView.findViewById(R.id.textViewMultiTextMessage);
            cardViewSharePrifile = itemView.findViewById(R.id.constraintLayoutChatSender);
            multiImagesLayout = itemView.findViewById(R.id.multiImagesLayout);
            multiImagesLayoutText = itemView.findViewById(R.id.multiImagesLayoutText);
            textViewShareProfile = itemView.findViewById(R.id.textViewShareProfile);
            textViewShareName = itemView.findViewById(R.id.textViewShareName);
            imageViewShareProfile = itemView.findViewById(R.id.imageViewShareProfile);
            imageViewPlayRecording = itemView.findViewById(R.id.imageViewPlayRecording);
            progressBarVoiceNote = itemView.findViewById(R.id.progressBarVoiceNote);
            textViewVoiceNoteTime = itemView.findViewById(R.id.textViewVoiceNoteTime);
            voiceLayout = itemView.findViewById(R.id.voiceLayout);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            img4 = itemView.findViewById(R.id.img4);
            img1Text = itemView.findViewById(R.id.img1Text);
            img2Text = itemView.findViewById(R.id.img2Text);
            img3Text = itemView.findViewById(R.id.img3Text);
            img4Text = itemView.findViewById(R.id.img4Text);
            textViewMessage.addAutoLinkMode(AutoLinkMode.MODE_PHONE, AutoLinkMode.MODE_EMAIL, AutoLinkMode.MODE_URL);
            textViewTextMessage.addAutoLinkMode(AutoLinkMode.MODE_PHONE, AutoLinkMode.MODE_EMAIL, AutoLinkMode.MODE_URL);
            textViewMutliTextMessage.addAutoLinkMode(AutoLinkMode.MODE_PHONE, AutoLinkMode.MODE_EMAIL, AutoLinkMode.MODE_URL);
            textViewMessage.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMessage.setUrlModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMessage.setEmailModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewTextMessage.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewTextMessage.setUrlModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewTextMessage.setEmailModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMutliTextMessage.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMutliTextMessage.setUrlModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMutliTextMessage.setEmailModeColor(ContextCompat.getColor(context, R.color.colorAccent));

            textViewMessage.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    handleIntent(autoLinkMode, matchedText);
                }
            });
            textViewTextMessage.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    handleIntent(autoLinkMode, matchedText);
                }
            });
            textViewMutliTextMessage.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    handleIntent(autoLinkMode, matchedText);
                }
            });
            imageViewMsgFailed.setOnClickListener(v -> EventBus.getDefault().post(new EventBussFailedMsg(getAdapterPosition())));
            imageViewMessage.setOnClickListener(v -> {
                Intent intent = new Intent(context,
                        FullProfilePictureActivity.class);
//                intent.putExtra("profileImage","http://lnq.demo.leadconcept.net/uploads/chat_images/" + chatDataList.get(getAdapterPosition()).getAttachment());
                intent.putExtra("profileImage", chatDataList.get(getAdapterPosition()).getAttachment());
                context.startActivity(intent);
            });
            imageViewTextMessage.setOnClickListener(v -> {
                Intent intent = new Intent(context,
                        FullProfilePictureActivity.class);
                intent.putExtra("profileImage", chatDataList.get(getAdapterPosition()).getAttachment());
                context.startActivity(intent);
            });
            imageViewMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    imageViewMessage.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(imageViewMessage.getDrawingCache()));
                    return false;
                }
            });
            imageViewTextMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    imageViewTextMessage.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(imageViewTextMessage.getDrawingCache()));
                    return false;
                }
            });
            img1.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img2.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img3.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img4.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img1Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img2Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img3Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img4Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img1.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img1.getDrawingCache()));
                    return false;
                }
            });
            img2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img2.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img2.getDrawingCache()));
                    return false;
                }
            });
            img3.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img3.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img3.getDrawingCache()));
                    return false;
                }
            });
            img4.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img4.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img4.getDrawingCache()));
                    return false;
                }
            });
            img1Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img1Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img1Text.getDrawingCache()));
                    return false;
                }
            });
            img2Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img2Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img2Text.getDrawingCache()));
                    return false;
                }
            });
            img3Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img3Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img3Text.getDrawingCache()));
                    return false;
                }
            });
            img4Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img4Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img4Text.getDrawingCache()));
                    return false;
                }
            });
            textViewShareProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusChatShareContactClick(chatDataList.get(getAdapterPosition()).getSharecontact().getUser_id(), chatDataList.get(getAdapterPosition()).getSharecontact().getProfile_id()));
                }
            });
            new SwipeDetector(itemView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
                @Override
                public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum SwipeType) {
                    if (SwipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                        EventBus.getDefault().post(new EventBusChatGestures(SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT));
                    } else if (SwipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                        EventBus.getDefault().post(new EventBusChatGestures(SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT));
                    }
                }
            });

            imageViewPlayRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (previousRecording != null) {
                        previousRecording.pauseAnimation();
                        stopPlay();
                    }
                    if (previousPlay != null) {
                        if (imageViewPlayRecording.getTag().equals("play")) {
                            previousPlay.setBackgroundResource(R.drawable.play_iocn_w);
                            imageViewPlayRecording.setBackgroundResource(R.drawable.pause_icon_w);
                            imageViewPlayRecording.setTag("stop");
                            play(voiceUrl);
                        } else {
                            previousPlay.setBackgroundResource(R.drawable.pause_icon_w);
                            imageViewPlayRecording.setBackgroundResource(R.drawable.play_iocn_w);
                            imageViewPlayRecording.setTag("play");
                            stopPlay();
                        }
                    }
                    play(voiceUrl);
                    progressBarVoiceNote.playAnimation();
                    progressBarVoiceNote.loop(true);
                    previousRecording = progressBarVoiceNote;
                    previousPlay = imageViewPlayRecording;
                }
            });
        }
    }

    public void play(String path) {
        try {
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(path);
            myPlayer.prepare();
            myPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ChatReceiverHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProfile, imageViewArrow, imageViewFavoriteBorder;
        TextView textViewTime, textViewDate, textViewShareName, textViewShareProfile,
                textViewVoiceNoteTime, textViewUserName;
        AutoLinkTextView textViewMessage;
        ImageView imageViewTextMessage, imageViewMessage;
        SmartImageViewLayout smartImageViewTextMessage, smartImageViewMessage;
        AutoLinkTextView textViewTextMessage, textViewMultiTextMessage;
        View viewUnRead;
        AppCompatImageView img1, img2, img3, img4, img1Text, img2Text, img3Text, img4Text, imgReaction;
        ConstraintLayout cardViewSharePrifile, multiImagesLayout, multiImagesLayoutText, voiceLayout;
        AppCompatImageView imageViewShareProfile, imageViewPlayRecording;
        LottieAnimationView progressBarVoiceNote;

        @SuppressLint("ClickableViewAccessibility")
        ChatReceiverHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageViewArrow = itemView.findViewById(R.id.imageViewReceiver);
            imageViewFavoriteBorder = itemView.findViewById(R.id.imageViewProfileFavoriteBorder);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewMessage = itemView.findViewById(R.id.imageViewMessage);
            smartImageViewTextMessage = itemView.findViewById(R.id.smartImageViewTextMessage);
            smartImageViewMessage = itemView.findViewById(R.id.smartImageViewMessage);
            imageViewTextMessage = itemView.findViewById(R.id.imageViewTextMessage);
            textViewTextMessage = itemView.findViewById(R.id.textViewTextMessage);
            textViewMultiTextMessage = itemView.findViewById(R.id.textViewMultiTextMessage);
            viewUnRead = itemView.findViewById(R.id.viewUnRead);
            cardViewSharePrifile = itemView.findViewById(R.id.constraintLayoutChatReciver);
            textViewShareProfile = itemView.findViewById(R.id.textViewShareProfile);
            textViewShareName = itemView.findViewById(R.id.textViewShareName);
            multiImagesLayout = itemView.findViewById(R.id.multiImagesLayout);
            multiImagesLayoutText = itemView.findViewById(R.id.multiImagesLayoutText);
            imageViewPlayRecording = itemView.findViewById(R.id.imageViewPlayRecording);
            progressBarVoiceNote = itemView.findViewById(R.id.progressBarVoiceNote);
            textViewVoiceNoteTime = itemView.findViewById(R.id.textViewVoiceNoteTime);
            voiceLayout = itemView.findViewById(R.id.voiceLayout);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            img4 = itemView.findViewById(R.id.img4);
            img1Text = itemView.findViewById(R.id.img1Text);
            img2Text = itemView.findViewById(R.id.img2Text);
            img3Text = itemView.findViewById(R.id.img3Text);
            img4Text = itemView.findViewById(R.id.img4Text);
            imgReaction = itemView.findViewById(R.id.imgReaction);
            imageViewShareProfile = itemView.findViewById(R.id.imageViewShareProfile);
            textViewMessage.addAutoLinkMode(AutoLinkMode.MODE_PHONE, AutoLinkMode.MODE_EMAIL, AutoLinkMode.MODE_URL);
            textViewTextMessage.addAutoLinkMode(AutoLinkMode.MODE_PHONE, AutoLinkMode.MODE_EMAIL, AutoLinkMode.MODE_URL);
            textViewTextMessage.addAutoLinkMode(AutoLinkMode.MODE_PHONE, AutoLinkMode.MODE_EMAIL, AutoLinkMode.MODE_URL);
            textViewMessage.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMessage.setUrlModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMessage.setEmailModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewTextMessage.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewTextMessage.setUrlModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewTextMessage.setEmailModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMultiTextMessage.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMultiTextMessage.setUrlModeColor(ContextCompat.getColor(context, R.color.colorAccent));
            textViewMultiTextMessage.setEmailModeColor(ContextCompat.getColor(context, R.color.colorAccent));

            ReactionsConfig config = new ReactionsConfigBuilder(context)
                    .withReactions(new int[]{
                            R.drawable.ic_fb_like,
                            R.drawable.ic_fb_love,
                            R.drawable.ic_fb_laugh,
                            R.drawable.ic_fb_wow,
                            R.drawable.ic_fb_sad,
                            R.drawable.ic_fb_angry
                    })
                    .build();

            ReactionPopup popup = new ReactionPopup(context, config, (position) -> {
                return true; // true is closing popup, false is requesting a new selection
            });

            imgReaction.setOnTouchListener(popup);

            popup.setReactionSelectedListener((position) -> {
                if (position == 1){
                    imgReaction.setImageResource(R.drawable.ic_fb_love);
                }else {
                    imgReaction.setImageResource(R.drawable.ic_fb_angry);
                }
                return position != 3;
            });

            textViewMessage.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    handleIntent(autoLinkMode, matchedText);
                }
            });
            textViewTextMessage.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    handleIntent(autoLinkMode, matchedText);
                }
            });
            textViewMultiTextMessage.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    handleIntent(autoLinkMode, matchedText);
                }
            });
            fontUtils.setTextViewRegularFont(textViewMessage);
            fontUtils.setTextViewRegularFont(textViewTime);

            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusChatThreadClick(getAdapterPosition()));
                }
            });

            imageViewMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            FullProfilePictureActivity.class);
                    intent.putExtra("profileImage", chatDataList.get(getAdapterPosition()).getAttachment());
                    context.startActivity(intent);
                }
            });

            imageViewTextMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            FullProfilePictureActivity.class);
                    intent.putExtra("profileImage", chatDataList.get(getAdapterPosition()).getAttachment());
                    context.startActivity(intent);
                }
            });
            imageViewMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    imageViewMessage.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(imageViewMessage.getDrawingCache()));
                    return false;
                }
            });
            imageViewTextMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    imageViewTextMessage.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(imageViewTextMessage.getDrawingCache()));
                    return false;
                }
            });

            img1.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img2.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img3.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img4.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img1Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img2Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img3Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img4Text.setOnClickListener(v -> {
                List<String> imageList = Arrays.asList(chatDataList.get(getAdapterPosition()).getAttachment().split("\\s*,\\s*"));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.cus_dialog_fullchatimages, null);

                RecyclerView recyclerViewShareProfile = dialogView.findViewById(R.id.recyclerViewShareContactList);

                OpenChatImagesAdapter sharedUserProfileAdapter = new OpenChatImagesAdapter(context, imageList);
                recyclerViewShareProfile.setAdapter(sharedUserProfileAdapter);
                recyclerViewShareProfile.setLayoutManager(new LinearLayoutManager(context));

                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();

                try {
                    dialog.getWindow().getDecorView().setBackgroundResource(R.color.colorTransparaent);

                } catch (Exception e) {

                }
            });
            img1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img1.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img1.getDrawingCache()));
                    return false;
                }
            });
            img2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img2.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img2.getDrawingCache()));
                    return false;
                }
            });
            img3.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img3.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img3.getDrawingCache()));
                    return false;
                }
            });
            img4.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img4.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img4.getDrawingCache()));
                    return false;
                }
            });
            img1Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img1Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img1Text.getDrawingCache()));
                    return false;
                }
            });
            img2Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img2Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img2Text.getDrawingCache()));
                    return false;
                }
            });
            img3Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img3Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img3Text.getDrawingCache()));
                    return false;
                }
            });
            img4Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    img4Text.setDrawingCacheEnabled(true);
                    EventBus.getDefault().post(new EventBusSaveChatImagesToGallery(img4Text.getDrawingCache()));
                    return false;
                }
            });

            textViewShareProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusChatShareContactClick(chatDataList.get(getAdapterPosition()).getSharecontact().getUser_id(), chatDataList.get(getAdapterPosition()).getSharecontact().getProfile_id()));
                }
            });

            new SwipeDetector(itemView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
                @Override
                public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum SwipeType) {
                    if (SwipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                        EventBus.getDefault().post(new EventBusChatGestures(SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT));
                    } else if (SwipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                        EventBus.getDefault().post(new EventBusChatGestures(SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT));
                    }
                }
            });

            imageViewPlayRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (previousRecording != null) {
                        previousRecording.pauseAnimation();
                        stopPlay();
                    }
                    if (previousPlay != null) {
                        if (imageViewPlayRecording.getTag().equals("play")) {
                            previousPlay.setBackgroundResource(R.drawable.play_iocn_b);
                            imageViewPlayRecording.setBackgroundResource(R.drawable.pause_icon_b);
                            imageViewPlayRecording.setTag("stop");
                            play(voiceUrl);
                        } else {
                            previousPlay.setBackgroundResource(R.drawable.pause_icon_b);
                            imageViewPlayRecording.setBackgroundResource(R.drawable.play_iocn_b);
                            imageViewPlayRecording.setTag("play");
                            stopPlay();
                        }
                    }
                    play(voiceUrl);
                    progressBarVoiceNote.playAnimation();
                    progressBarVoiceNote.loop(true);
                    previousRecording = progressBarVoiceNote;
                    previousPlay = imageViewPlayRecording;
                }
            });
        }
    }

    private void handleIntent(AutoLinkMode autoLinkMode, String value) {
        if (autoLinkMode == AutoLinkMode.MODE_EMAIL) {
            openEmailIntent(value);
        } else if (autoLinkMode == AutoLinkMode.MODE_PHONE) {
            openPhoneIntent(value);
        } else if (autoLinkMode == AutoLinkMode.MODE_URL) {
            openBrowser(value);
        }
    }

    private void openPhoneIntent(String number) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", number);
        context.startActivity(smsIntent);
    }

    private void openEmailIntent(String emailId) {

        String uriText =
                "mailto:" + emailId;
        Uri uri = Uri.parse(uriText);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(uri);
        context.startActivity(Intent.createChooser(emailIntent, "Send email using..."));
    }

    private void openBrowser(String url) {
        Uri webpage = Uri.parse(url.trim());
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webpage = Uri.parse("http://" + webpage);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}