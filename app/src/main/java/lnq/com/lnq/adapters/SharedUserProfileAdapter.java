package lnq.com.lnq.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import lnq.com.lnq.model.event_bus_models.EventBusSharedProfileItemClick;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.SelectedExportContact;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class SharedUserProfileAdapter extends RecyclerView.Adapter<SharedUserProfileAdapter.UserExportContactsViewHolder> {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<UserConnectionsData> selectedExportContacts = new ArrayList<>();
    private List<UserConnectionsData> exportContactsFilteredList = new ArrayList<>();

    public SharedUserProfileAdapter(Context context, List<UserConnectionsData> userConnectionsData) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        createTransferUtility();
        this.selectedExportContacts = userConnectionsData;
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
        View view = layoutInflater.inflate(R.layout.cus_phone_exportlist_dialog, parent, false);
        return new UserExportContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserExportContactsViewHolder holder, int position) {
        UserConnectionsData userConnectionsData = exportContactsFilteredList.get(position);

        String number = userConnectionsData.getContact_name();
        String name = userConnectionsData.getUser_fname() + " " + userConnectionsData.getUser_lname();
        if (userConnectionsData.getUser_avatar() != null && !userConnectionsData.getUser_avatar().isEmpty()) {
            download(userConnectionsData.getUser_avatar(), holder.imageViewProfile);
        }
        holder.textViewName.setText(name);

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
//                            apply(new RequestOptions().circleCrop().placeholder(R.drawable.ic_action_avatar)).
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

    class UserExportContactsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewProfile;
        RecyclerView recyclerView;

        UserExportContactsViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewNameExportList);
            imageViewProfile = itemView.findViewById(R.id.imageViewExportList);
            recyclerView = itemView.findViewById(R.id.recyclerViewExportList);

            FontUtils.getFontUtils(context).setTextViewSemiBold(textViewName);

            textViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusSharedProfileItemClick(getAdapterPosition(), exportContactsFilteredList.get(getAdapterPosition()).getUser_id(), exportContactsFilteredList.get(getAdapterPosition()).getProfile_id()));
                }
            });

            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusSharedProfileItemClick(getAdapterPosition(), exportContactsFilteredList.get(getAdapterPosition()).getUser_id(), exportContactsFilteredList.get(getAdapterPosition()).getProfile_id()));
                }
            });

        }
    }

}
