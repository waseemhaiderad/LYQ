package lnq.com.lnq.adapters;

import android.content.Context;

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
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.model.gson_converter_models.blockedusers.GetBlockedUserList;
import lnq.com.lnq.model.event_bus_models.EventBusBlockedUsersClicked;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.BlockedUsersViewHolder> {
    private String cachePath = "";
    private TransferUtility transferUtility;

    private Context context;
    private LayoutInflater layoutInflater;
    private List<GetBlockedUserList> getBlockedUserLists = new ArrayList<>();

    public BlockedUsersAdapter(Context context, List<GetBlockedUserList> contactsModelList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.getBlockedUserLists = contactsModelList;
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
    public BlockedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_blocked_users, parent, false);
        return new BlockedUsersViewHolder(view, new BlockedUsersViewHolder.OnBlockedUsers() {
            @Override
            public void rowClick(int position, int id) {
                EventBus.getDefault().post(new EventBusBlockedUsersClicked(position));
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedUsersViewHolder holder, int position) {
        GetBlockedUserList getLnqContact = getBlockedUserLists.get(position);
        download(getLnqContact.getUser_avatar(), holder.mImProfile);
        String name = getLnqContact.getUser_name();
        holder.mTvNumber.setText(name);
    }

    @Override
    public int getItemCount() {
        return getBlockedUserLists.size();
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class BlockedUsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTvNumber;
        Button mBtnUnBlock;
        ImageView mImProfile;
        private OnBlockedUsers onContactClickListener;

        public BlockedUsersViewHolder(View itemView, OnBlockedUsers onContactClickListener) {
            super(itemView);

            this.onContactClickListener = onContactClickListener;

            mTvNumber = itemView.findViewById(R.id.mTvName);
            mTvNumber.setOnClickListener(this);
            mImProfile = itemView.findViewById(R.id.mImContactPic);
            mBtnUnBlock = itemView.findViewById(R.id.mBtnUnBlock);


            mBtnUnBlock.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onContactClickListener.rowClick(getAdapterPosition(), view.getId());
        }

        interface OnBlockedUsers {
            void rowClick(int position, int id);
        }

    }
}
