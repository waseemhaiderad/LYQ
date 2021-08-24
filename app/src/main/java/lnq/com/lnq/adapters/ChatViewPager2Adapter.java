package lnq.com.lnq.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
import lnq.com.lnq.model.event_bus_models.EventBussFailedMsg;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatData;
import lnq.com.lnq.model.gson_converter_models.chat.ViewPagerModel;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ChatViewPager2Adapter extends RecyclerView.Adapter<ChatViewPager2Adapter.ChatSenderHolder> {

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<ViewPagerModel> chatDataList = new ArrayList<>();
    private String userImage, isConnection, isFavorite;

    //    Font fields....
    private FontUtils fontUtils;

    public ChatViewPager2Adapter(Context context, List<ViewPagerModel> chatDataList) {
        this.context = context;
        this.chatDataList = chatDataList;
        layoutInflater = LayoutInflater.from(context);
        fontUtils = FontUtils.getFontUtils(context);
    }

    @NonNull
    @Override
    public ChatSenderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_chat_viewpager, parent, false);
        return new ChatSenderHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatSenderHolder chatHolder, final int i) {
        ViewPagerModel chatData = chatDataList.get(i);
//        chatHolder.chatAdapter.setChatData(chatData.getChatDataList(), chatData.getUserImage(), chatData.getIsConnected(), chatData.getIsFavorite());

    }


    @Override
    public int getItemCount() {
        return chatDataList.size();
    }

    class ChatSenderHolder extends RecyclerView.ViewHolder {
        ChatAdapter chatAdapter;
        RecyclerView recyclerViewChat;

        ChatSenderHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewChat = itemView.findViewById(R.id.recyclerViewViewPagerChat);
//            chatAdapter = new ChatAdapter(context);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//            linearLayoutManager.setStackFromEnd(true);
//            recyclerViewChat.setLayoutManager(linearLayoutManager);
//            recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
//            recyclerViewChat.setAdapter(chatAdapter);

        }

    }
}