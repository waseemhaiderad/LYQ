package lnq.com.lnq.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.fast_scroller.RecyclerViewFastScroller;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusConnectionClick;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ConnectionsGridAdapter extends RecyclerView.Adapter<ConnectionsGridAdapter.UserContactsViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<UserConnectionsData> userContactsDataList = new ArrayList<>();

    public ConnectionsGridAdapter(Context context, List<UserConnectionsData> userContactsDataList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.userContactsDataList = userContactsDataList;
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
    public UserContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_connection_grid, parent, false);
        return new UserContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserContactsViewHolder holder, int position) {
        UserConnectionsData userContactsData = userContactsDataList.get(position);

        if (userContactsData != null && !userContactsData.getUser_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {

            String userName = userContactsData.getUser_fname() + " " + userContactsData.getUser_lname();
            String jobTitle = userContactsData.getUser_current_position();
            String isUserFavorite = userContactsData.getIs_favorite();
            String connectionStatus = userContactsData.getIs_connection();

            if (!jobTitle.isEmpty()) {
                if (jobTitle.length() > 20) {
                    jobTitle = jobTitle.substring(0, 19);
                }
            }
//            byte[] fullImageAsBytes = Base64.decode(EndpointUrls.IMAGES_BASE_URL + userContactsData.getUser_avatar(), Base64.DEFAULT);
//            BitmapFactory.decodeByteArray(fullImageAsBytes, 0, fullImageAsBytes.length);
//            Glide.with(context)
//                    .load(EndpointUrls.IMAGES_BASE_URL + userContactsData.getUser_avatar())
//                    .apply(new RequestOptions().circleCrop().placeholder(R.drawable.ic_action_avatar))
//                    .apply(new RequestOptions().error(R.drawable.ic_action_avatar))
//                    .into(holder.imageViewProfile);
            if (userContactsData.getUser_avatar() != null && !userContactsData.getUser_avatar().isEmpty()) {
                download(userContactsData.getUser_avatar(), holder.imageViewProfile);
            }
            holder.textViewName.setText(userName);
            if (userContactsData.getContact_status().equals(Constants.NOT_LNQED)) {
                holder.imageViewFavBorder.setVisibility(View.GONE);
                holder.imageViewLinked.setVisibility(View.GONE);
                holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
            } else {
                holder.textViewJobTitle.setText(jobTitle);
                holder.textViewCompany.setVisibility(View.VISIBLE);
                holder.textViewCompany.setText(userContactsDataList.get(position).getUser_company());
                holder.textViewDistance.setVisibility(View.VISIBLE);
                holder.textViewDistance.setText(userContactsDataList.get(position).getUser_distance() + " miles");
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
            }
        }

    }

    @Override
    public int getItemCount() {
        return userContactsDataList.size();
    }

    void download1(String objectKey, ImageView imageView) {
        final File fileDownload = new File(context.getCacheDir(), objectKey);

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
                    Glide.with(context).
                            load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                            apply(new RequestOptions().circleCrop().placeholder(R.drawable.ic_action_avatar)).
                            apply(new RequestOptions().error(R.drawable.ic_action_avatar)).
                            into(imageView);
//                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));

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

    class UserContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewName, textViewDistance;
        TextView textViewJobTitle;
        TextView textViewCompany;
        Button buttonInvite;
        ImageView imageViewProfile, imageViewFavBorder, imageViewLinked;
//        ConstraintLayout constraintLayoutDistance;

        UserContactsViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
            textViewCompany = itemView.findViewById(R.id.textViewCompany);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            imageViewFavBorder = itemView.findViewById(R.id.imageViewProfileFavoriteBorder);
            imageViewLinked = itemView.findViewById(R.id.imageViewConnectionLinked);

            fontUtils.setTextViewSemiBold(textViewName);
            fontUtils.setTextViewRegularFont(textViewJobTitle);
            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusConnectionClick(getAdapterPosition(), userContactsDataList.get(getAdapterPosition()).getProfile_id()));
                }
            });

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new EventBusConnectionClick(getAdapterPosition(), userContactsDataList.get(getAdapterPosition()).getProfile_id()));
        }
    }
}