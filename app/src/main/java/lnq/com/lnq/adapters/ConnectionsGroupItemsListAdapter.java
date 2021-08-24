package lnq.com.lnq.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.chauthai.swipereveallayout.SwipeRevealLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.fast_scroller.RecyclerViewFastScroller;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusGroupProfileClick;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveMemberFromGroup;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusConnectionClick;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusDistanceClicked;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.CreateUserGroup;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ConnectionsGroupItemsListAdapter extends RecyclerView.Adapter<ConnectionsGroupItemsListAdapter.UserContactsViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;
    private String groupId;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();

    public ConnectionsGroupItemsListAdapter(Context context) {
        this.context = context;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
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
        View view = layoutInflater.inflate(R.layout.cus_group_members, parent, false);
        return new UserContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserContactsViewHolder holder, int position) {
        UserConnectionsData userContactsData = userContactsDataList.get(position);

        String userName = userContactsData.getUser_fname() + " " + userContactsData.getUser_lname();
        String isUserFavorite = userContactsData.getIs_favorite();
        String connectionStatus = userContactsData.getIs_connection();

        if (userContactsData.getUser_avatar() != null && !userContactsData.getUser_avatar().isEmpty()) {
            download(userContactsData.getUser_avatar(), holder.imageViewProfile);
        }
        holder.textViewName.setText(userName);
        if (userContactsData.getContact_status().equals(Constants.NOT_LNQED)) {
            holder.imageViewFavBorder.setVisibility(View.GONE);
            holder.imageViewLinked.setVisibility(View.GONE);
            holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
        } else {
            if (isUserFavorite != null && !isUserFavorite.isEmpty()) {
                if (isUserFavorite.equals(Constants.FAVORITE)) {
                    holder.imageViewLinked.setVisibility(View.GONE);
                    holder.imageViewFavBorder.setVisibility(View.VISIBLE);
                    holder.imageViewProfile.setBackground(null);
                    if (connectionStatus.equals("")) {
                        holder.imageViewFavBorder.setImageResource(R.drawable.ic_fav_lnq_border);
                    } else if (connectionStatus.equals(Constants.CONTACTED)) {
                        holder.imageViewFavBorder.setImageResource(R.drawable.ic_fav_teen_border);
                    } else if (connectionStatus.equals(Constants.CONNECTED)) {
                        holder.imageViewFavBorder.setImageResource(R.drawable.icon_fav_border);
                    }
                } else {
                    holder.imageViewFavBorder.setVisibility(View.GONE);
                    switch (connectionStatus) {
                        case Constants.CONTACTED:
                            holder.imageViewLinked.setVisibility(View.VISIBLE);
                            holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                            holder.imageViewLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen));
                            break;
                        case Constants.CONNECTED:
                            holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                            holder.imageViewLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
                            holder.imageViewLinked.setVisibility(View.VISIBLE);
                            break;
                        default:
                            holder.imageViewLinked.setVisibility(View.GONE);
                            holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                            break;
                    }
                }
            } else {
                holder.imageViewFavBorder.setVisibility(View.GONE);
                switch (connectionStatus) {
                    case Constants.CONTACTED:
                        holder.imageViewLinked.setVisibility(View.VISIBLE);
                        holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                        holder.imageViewLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen));
                        break;
                    case Constants.CONNECTED:
                        holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                        holder.imageViewLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
                        holder.imageViewLinked.setVisibility(View.VISIBLE);
                        break;
                    default:
                        holder.imageViewLinked.setVisibility(View.GONE);
                        holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                        break;
                }
            }
        }
    }

    public void setGroupData(List<UserConnectionsData> createUserGroups, String mGroupId) {
        userContactsDataList = createUserGroups;
        groupId = mGroupId;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userContactsDataList.size();
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

    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= userContactsDataList.size())
            return null;

        String name = userContactsDataList.get(pos).getContact_name();
        if (name == null || name.length() < 1)
            return null;

        return userContactsDataList.get(pos).getContact_name().substring(0, 1);
    }

    class UserContactsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewProfile, imageViewFavBorder, imageViewLinked;
        View delete_layout, front_layout;
        SwipeRevealLayout layout;

        UserContactsViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            imageViewFavBorder = itemView.findViewById(R.id.imageViewProfileFavoriteBorder);
            imageViewLinked = itemView.findViewById(R.id.imageViewConnectionLinked);
            delete_layout = itemView.findViewById(R.id.delete_layout);
            front_layout = itemView.findViewById(R.id.front_layout);
            layout = itemView.findViewById(R.id.swipe_layout);


            fontUtils.setTextViewSemiBold(textViewName);
            front_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusGroupProfileClick(getAdapterPosition(), userContactsDataList.get(getAdapterPosition()).getProfile_id(), userContactsDataList.get(getAdapterPosition()).getUser_id()));
                }
            });

            delete_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusRemoveMemberFromGroup(getAdapterPosition(), userContactsDataList.get(getAdapterPosition()).getUser_id(), userContactsDataList.get(getAdapterPosition()).getProfile_id(), groupId));
                    layout.close(true);
                }
            });

        }
    }
}