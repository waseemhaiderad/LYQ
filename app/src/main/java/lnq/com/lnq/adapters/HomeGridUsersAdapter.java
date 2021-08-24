package lnq.com.lnq.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.util.Base64;
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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;
import lnq.com.lnq.model.event_bus_models.EventBusGridUserClick;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class HomeGridUsersAdapter extends RecyclerView.Adapter<HomeGridUsersAdapter.GridUsersHolder> {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<UpdateLocationData> updateLocationDataList;

    public HomeGridUsersAdapter(Context context, List<UpdateLocationData> updateLocationDataList) {
        this.context = context;
        fontUtils = FontUtils.getFontUtils(context);
        layoutInflater = LayoutInflater.from(context);
        this.updateLocationDataList = updateLocationDataList;
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
    public GridUsersHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_home_grid, parent, false);
        return new GridUsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridUsersHolder holder, int position) {
        UpdateLocationData updateLocationData = updateLocationDataList.get(position);
        if (updateLocationData != null) {

            String userName = updateLocationData.getUser_name();
            String userImage = updateLocationData.getUser_image();
            String homeLocationName = updateLocationData.getUser_home_address();
            String currentLocation = updateLocationData.getLocation_name();
            String statusMessage = updateLocationData.getUser_status_msg();
            String userPosition = updateLocationData.getUser_current_position();
            String company = updateLocationData.getUser_company();
            String distance = updateLocationData.getUser_distance();
            String connectionDate = updateLocationData.getUser_connection_date();

            if (statusMessage.equals(""))
                statusMessage = "No status yet.";

            if (distance.length() > 4) {
                distance = distance.substring(0, 4);
            }
//            byte[] userImageGrid = Base64.decode(userImage, Base64.DEFAULT);
//            BitmapFactory.decodeByteArray(userImageGrid, 0, userImageGrid.length);
//            Glide.with(context)
//                    .load(EndpointUrls.IMAGES_BASE_URL + userImage)
//                    .apply(new RequestOptions().circleCrop())
//                    .apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                    .apply(new RequestOptions().error(R.drawable.ic_action_avatar))
//                    .into(holder.imageViewProfile);
            if (userImage != null && !userImage.isEmpty()) {
                download(userImage, holder.imageViewProfile);
            }
            holder.textViewName.setText(userName);
            holder.textViewCompany.setText(company);
            holder.textViewJobTitle.setText(userPosition);
            holder.textViewStatusMessage.setText(statusMessage);
            holder.textViewHomeLocation.setText(updateLocationData.getUser_home_address());

            if (currentLocation.isEmpty() && distance.isEmpty()) {
                holder.textViewCurrentLocation.setVisibility(View.GONE);
                holder.textViewCurrentLocation.setText("");
                holder.imageViewCurrentLocation.setVisibility(View.GONE);
            } else {
                holder.imageViewCurrentLocation.setVisibility(View.VISIBLE);
                holder.textViewCurrentLocation.setVisibility(View.VISIBLE);
                if (currentLocation.isEmpty()) {
                    holder.textViewCurrentLocation.setText(distance + " mi");
                } else {
                    holder.textViewCurrentLocation.setText(currentLocation + " . " + distance + " mi");
                }
            }
            holder.textViewHomeLocation.setText(homeLocationName);

            if (updateLocationData.getIs_favorite().equals(Constants.FAVORITE)) {
                holder.imageViewStarSolid.setVisibility(View.VISIBLE);
                holder.imageViewStartStroke.setVisibility(View.GONE);
                holder.imageViewLinked.setVisibility(View.VISIBLE);
                holder.textViewSince.setVisibility(View.VISIBLE);
                holder.textViewConnectionDate.setVisibility(View.VISIBLE);
                holder.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                holder.textViewIn.setVisibility(View.VISIBLE);
                holder.textViewConnectionLocation.setVisibility(View.VISIBLE);
                holder.imageViewProfileLinked.setVisibility(View.GONE);
                holder.textViewConnectionDate.setText(connectionDate);
                switch (updateLocationData.getIs_connection()) {
                    case Constants.CONTACTED:
                        holder.imageViewFavoriteBorder.setImageResource(R.drawable.ic_fav_teen_border);
                        if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                            holder.textViewConnectionLocation.setText(updateLocationData.getSender_location());
                            holder.textViewSince.setText(context.getResources().getString(R.string.request_sent_on));
                        } else {
                            holder.textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                            holder.textViewSince.setText(context.getResources().getString(R.string.request_received_on));
                        }
                        break;
                    case Constants.CONNECTED:
                        holder.textViewSince.setText(context.getResources().getString(R.string.since));
                        if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                            holder.textViewConnectionLocation.setText(updateLocationData.getSender_location());
                        } else {
                            holder.textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                        }
                        break;
                    default:
                        holder.imageViewFavoriteBorder.setImageResource(R.drawable.ic_fav_lnq_border);
                        holder.textViewSince.setVisibility(View.GONE);
                        holder.imageViewLinked.setVisibility(View.GONE);
                        holder.textViewConnectionDate.setVisibility(View.GONE);
                        holder.textViewConnectionLocation.setVisibility(View.GONE);
                        holder.textViewIn.setVisibility(View.GONE);
                        break;
                }
            } else {
                holder.imageViewStarSolid.setVisibility(View.GONE);
                holder.imageViewStartStroke.setVisibility(View.VISIBLE);
                holder.imageViewLinked.setVisibility(View.VISIBLE);
                holder.textViewConnectionDate.setVisibility(View.VISIBLE);
                holder.textViewSince.setVisibility(View.VISIBLE);
                holder.textViewIn.setVisibility(View.VISIBLE);
                holder.imageViewFavoriteBorder.setVisibility(View.GONE);
                holder.textViewConnectionLocation.setVisibility(View.VISIBLE);
                switch (updateLocationData.getIs_connection()) {
                    case Constants.CONTACTED:
                        holder.textViewConnectionDate.setText(connectionDate);
                        holder.imageViewProfileLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen));
                        holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                        if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                            holder.textViewConnectionLocation.setText(updateLocationData.getSender_location());
                            holder.textViewSince.setText(context.getResources().getString(R.string.request_sent_on));
                        } else {
                            holder.textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                            holder.textViewSince.setText(context.getResources().getString(R.string.request_received_on));
                        }
                        break;
                    case Constants.CONNECTED:
                        holder.textViewSince.setText(context.getResources().getString(R.string.since));
                        holder.textViewConnectionDate.setText(connectionDate);
                        if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                            holder.textViewConnectionLocation.setText(updateLocationData.getSender_location());
                        } else {
                            holder.textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                        }
                        holder.imageViewProfileLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
                        holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                        break;
                    default:
                        holder.textViewConnectionDate.setVisibility(View.GONE);
                        holder.imageViewProfileLinked.setVisibility(View.GONE);
                        holder.imageViewLinked.setVisibility(View.GONE);
                        holder.textViewIn.setVisibility(View.GONE);
                        holder.textViewConnectionLocation.setVisibility(View.GONE);
                        holder.imageViewFavoriteBorder.setVisibility(View.GONE);
                        holder.textViewSince.setVisibility(View.GONE);
                        holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return updateLocationDataList.size();
    }

//    void download(String objectKey, ImageView imageView) {
//        final File fileDownload = new File(context.getCacheDir(), objectKey);
//
//        TransferObserver transferObserver = transferUtility.download(
//                Constants.BUCKET_NAME,
//                objectKey,
//                fileDownload
//        );
//        transferObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                Log.d(TAG, "onStateChanged: " + state);
//                if (TransferState.COMPLETED.equals(state)) {
//                    Glide.with(context).
//                            load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
//                            apply(new RequestOptions().circleCrop()).
//                            apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar)).
//                            apply(new RequestOptions().error(R.drawable.ic_action_avatar)).
//                            into(imageView);
////                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
//
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                Log.e(TAG, "onError: ", ex);
//            }
//        });
//    }

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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class GridUsersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatImageView imageViewProfile, imageViewStarSolid, imageViewChat, imageViewLinked, imageViewStartStroke, imageViewProfileLinked, imageViewFavoriteBorder, imageViewCurrentLocation;
        TextView textViewName, textViewJobTitle, textViewCompany, textViewStatusMessage, textViewCurrentLocation, textViewHomeLocation;
        TextView textViewSince, textViewConnectionDate, textViewIn, textViewConnectionLocation;

        GridUsersHolder(View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            imageViewStarSolid = itemView.findViewById(R.id.imageViewStarSolid);
            imageViewStartStroke = itemView.findViewById(R.id.imageViewStarStroke);
            imageViewChat = itemView.findViewById(R.id.imageViewChat);
            imageViewLinked = itemView.findViewById(R.id.imageViewLinkedConnection);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewCompany = itemView.findViewById(R.id.textViewCompany);
            textViewStatusMessage = itemView.findViewById(R.id.textViewStatusMessage);
            textViewCurrentLocation = itemView.findViewById(R.id.textViewCurrentLocation);
            textViewHomeLocation = itemView.findViewById(R.id.textViewHomeLocation);
            imageViewProfileLinked = itemView.findViewById(R.id.imageViewProfileLinkedConnection);
            imageViewFavoriteBorder = itemView.findViewById(R.id.imageViewFavoriteBorder);
            imageViewCurrentLocation = itemView.findViewById(R.id.imageViewCurrentLocation);
            textViewSince = itemView.findViewById(R.id.textViewConnectionSince);
            textViewConnectionDate = itemView.findViewById(R.id.textViewConnectionDate);
            textViewIn = itemView.findViewById(R.id.textViewConnectionIn);
            textViewConnectionLocation = itemView.findViewById(R.id.textViewConnectionLocation);


//            Setting custom font....
            fontUtils.setTextViewRegularFont(textViewName);
            fontUtils.setTextViewRegularFont(textViewJobTitle);
            fontUtils.setTextViewSemiBold(textViewCompany);
            fontUtils.setTextViewSemiBold(textViewStatusMessage);
            fontUtils.setTextViewRegularFont(textViewHomeLocation);
            fontUtils.setTextViewRegularFont(textViewCurrentLocation);
            fontUtils.setTextViewRegularFont(textViewSince);
            fontUtils.setTextViewSemiBold(textViewConnectionDate);
            fontUtils.setTextViewRegularFont(textViewIn);
            fontUtils.setTextViewSemiBold(textViewConnectionLocation);

            imageViewProfile.setOnClickListener(this);
            imageViewStartStroke.setOnClickListener(this);
            imageViewStarSolid.setOnClickListener(this);
            imageViewChat.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusGridUserClick(getAdapterPosition(), Constants.PROFILE));

                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageViewProfile:
                    EventBus.getDefault().post(new EventBusGridUserClick(getAdapterPosition(), Constants.PROFILE));
                    break;
                case R.id.imageViewStarStroke:
                    EventBus.getDefault().post(new EventBusGridUserClick(getAdapterPosition(), Constants.FAVORITE));
                    break;
                case R.id.imageViewStarSolid:
                    EventBus.getDefault().post(new EventBusGridUserClick(getAdapterPosition(), Constants.UN_FAVORITE));
                    break;
                case R.id.imageViewChat:
                    EventBus.getDefault().post(new EventBusGridUserClick(getAdapterPosition(), Constants.CHAT));
                    break;
            }
        }

    }

}
