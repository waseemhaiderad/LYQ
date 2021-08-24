package lnq.com.lnq.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
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
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.model.event_bus_models.EventBusProfilesClick;
import lnq.com.lnq.model.event_bus_models.EventBusQRProfilesClick;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class SelectProfilesAdapter extends RecyclerView.Adapter<SelectProfilesAdapter.UserExportContactsViewHolder> {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<MultiProfileRoomModel> profileList;
    String selectedprofileId;

    public SelectProfilesAdapter(Context context, List<MultiProfileRoomModel> multiProfileRoomModel) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        createTransferUtility();
        this.profileList = multiProfileRoomModel;
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
        View view = layoutInflater.inflate(R.layout.cus_row_selectprofile, parent, false);
        return new UserExportContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserExportContactsViewHolder holder, int position) {
        String imagePath = profileList.get(position).getUser_avatar();
        if (imagePath != null && !imagePath.isEmpty()) {
            download(imagePath, holder.imageViewSelectProfileImage);
        }

        if (selectedprofileId == null) {
            if (profileList.get(position).getProfile_status().equalsIgnoreCase("active")) {
                holder.imageViewSelectedProfile.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewSelectedProfile.setVisibility(View.GONE);
            }
        } else {
            if (profileList.get(position).getId().equals(selectedprofileId)) {
                holder.imageViewSelectedProfile.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewSelectedProfile.setVisibility(View.GONE);
            }
        }

        holder.textViewUserName.setText(profileList.get(position).getUser_fname() + " " + profileList.get(position).getUser_lname());
        holder.textViewUserTitle.setText(profileList.get(position).getUser_current_position());
        holder.textViewUserCompany.setText(profileList.get(position).getUser_company());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
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
//                            apply(new RequestOptions().centerCrop()).
//                            apply(new RequestOptions().circleCrop()).
//                            apply(new RequestOptions().placeholder(R.drawable.avatar)).
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

        AppCompatImageView imageViewSelectProfileImage, imageViewSelectedProfile;
        AppCompatTextView textViewUserName, textViewUserTitle, textViewUserCompany;

        UserExportContactsViewHolder(View itemView) {
            super(itemView);

            imageViewSelectProfileImage = itemView.findViewById(R.id.imageViewSelectProfileImage);
            imageViewSelectedProfile = itemView.findViewById(R.id.imageViewSelectedProfile);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewUserTitle = itemView.findViewById(R.id.textViewUserTitle);
            textViewUserCompany = itemView.findViewById(R.id.textViewUserCompany);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedprofileId = profileList.get(getAdapterPosition()).getId();
                    EventBus.getDefault().post(new EventBusQRProfilesClick(getAdapterPosition(), profileList));
                }
            });

        }
    }

}
