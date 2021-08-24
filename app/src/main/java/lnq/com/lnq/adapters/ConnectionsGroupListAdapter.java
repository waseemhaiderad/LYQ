package lnq.com.lnq.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.fast_scroller.RecyclerViewFastScroller;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusAddContactGroupMembers;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusConnectionClick;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusDistanceClicked;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.CreateUserGroup;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ConnectionsGroupListAdapter extends RecyclerView.Adapter<ConnectionsGroupListAdapter.UserContactsViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<CreateUserGroup> userContactsDataList = new ArrayList<>();

    public ConnectionsGroupListAdapter(Context context, List<CreateUserGroup> userContactsDataList) {
        this.context = context;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
            this.userContactsDataList = userContactsDataList;
            fontUtils = FontUtils.getFontUtils(context);
            createTransferUtility();
            cachePath = context.getCacheDir().getAbsolutePath();
        }
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
    public UserContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_group_members_header, parent, false);
        return new UserContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserContactsViewHolder holder, int position) {
        CreateUserGroup userContactsData = userContactsDataList.get(position);
        String groupName = userContactsData.getGroup_name();
        holder.textViewGroupName.setText(groupName);
        holder.connectionsGroupItemsListAdapter.setGroupData(userContactsData.getMembers(), userContactsData.getId());
    }

    @Override
    public int getItemCount() {
        return userContactsDataList.size();
    }

//    void download(String objectKey, ImageView imageView) {
//
//        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
//            Glide.with(context).
//                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
//                    apply(new RequestOptions().circleCrop()).
//                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imageView);
//        } else {
//            final File fileDownload = new File(cachePath, objectKey);
//
//            TransferObserver transferObserver = transferUtility.download(
//                    Constants.BUCKET_NAME,
//                    objectKey,
//                    fileDownload
//            );
//            transferObserver.setTransferListener(new TransferListener() {
//
//                @Override
//                public void onStateChanged(int id, TransferState state) {
//                    Log.d(TAG, "onStateChanged: " + state);
//                    if (TransferState.COMPLETED.equals(state)) {
//                        LnqApplication.getInstance().listImagePaths.add(fileDownload.getAbsolutePath());
//                        Glide.with(context).
//                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
//                                apply(new RequestOptions().circleCrop()).
//                                apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .into(imageView);
//                    }
//                }
//
//                @Override
//                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                }
//
//                @Override
//                public void onError(int id, Exception ex) {
//                    Log.e(TAG, "onError: ", ex);
//                }
//            });
//        }
//    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= userContactsDataList.size())
            return null;

        String name = userContactsDataList.get(pos).getMembers().get(pos).getContact_name();
        if (name == null || name.length() < 1)
            return null;

        return userContactsDataList.get(pos).getMembers().get(pos).getContact_name().substring(0, 1);
    }

    class UserContactsViewHolder extends RecyclerView.ViewHolder {
        ConnectionsGroupItemsListAdapter connectionsGroupItemsListAdapter;
        TextView textViewGroupName, textViewAddMember;
        RecyclerView recyclerViewGroupTitle;
        AppCompatImageView imageViewChatImage;

        UserContactsViewHolder(View itemView) {
            super(itemView);

            textViewGroupName = itemView.findViewById(R.id.textViewGroupName);
            textViewAddMember = itemView.findViewById(R.id.textViewAddMember);
            imageViewChatImage = itemView.findViewById(R.id.imageViewChatImage);
            recyclerViewGroupTitle = itemView.findViewById(R.id.recyclerViewGroupTitle);
            connectionsGroupItemsListAdapter = new ConnectionsGroupItemsListAdapter(context);
            recyclerViewGroupTitle.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewGroupTitle.setAdapter(connectionsGroupItemsListAdapter);

            textViewAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusAddContactGroupMembers(getAdapterPosition(), userContactsDataList.get(getAdapterPosition()).getMembers(), userContactsDataList.get(getAdapterPosition()).getId()));
                }
            });

            imageViewChatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("groupName", userContactsDataList.get(getAdapterPosition()).getGroup_name());
                    bundle.putSerializable("groupData", (Serializable) userContactsDataList.get(getAdapterPosition()).getMembers());
                    ((MainActivity) context).fnLoadFragAdd("NEW_MESSAGE", true, bundle);
                    EventBus.getDefault().post(new EventBusUserSession("new_msg_clicked"));
                }
            });

        }
    }
}