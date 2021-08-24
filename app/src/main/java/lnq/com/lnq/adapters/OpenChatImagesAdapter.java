package lnq.com.lnq.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
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
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.model.event_bus_models.EventBusSharedProfileItemClick;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class OpenChatImagesAdapter extends RecyclerView.Adapter<OpenChatImagesAdapter.UserExportContactsViewHolder> {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<String> exportContactsFilteredList = new ArrayList<>();

    public OpenChatImagesAdapter(Context context, List<String> userConnectionsData) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        createTransferUtility();
        this.exportContactsFilteredList = userConnectionsData;
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
    public UserExportContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_full_chat_images, parent, false);
        return new UserExportContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserExportContactsViewHolder holder, int position) {
        String imagePath = exportContactsFilteredList.get(position);
        download(imagePath, holder.fullChatImageView);

    }

    @Override
    public int getItemCount() {
        return exportContactsFilteredList.size();
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
//                            apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar)).
//                            into(imageView);
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
//                    apply(new RequestOptions().circleCrop()).
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
//                                apply(new RequestOptions().circleCrop()).
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

    class UserExportContactsViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView fullChatImageView;
        RecyclerView recyclerView;

        UserExportContactsViewHolder(View itemView) {
            super(itemView);

            fullChatImageView = itemView.findViewById(R.id.fullChatImageView);
            recyclerView = itemView.findViewById(R.id.recyclerViewExportList);

            fullChatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    EventBus.getDefault().post(new EventBusSharedProfileItemClick(getAdapterPosition(), exportContactsFilteredList.get(getAdapterPosition()).getUser_id()));
                    Intent intent = new Intent(context,
                            FullProfilePictureActivity.class);
                    intent.putExtra("profileImage", exportContactsFilteredList.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

        }
    }

}
