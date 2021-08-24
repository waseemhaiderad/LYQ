package lnq.com.lnq.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.linkedin.android.spyglass.mentions.Mentionable;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.event_bus_models.EventBusCloseChatImageLayout;
import lnq.com.lnq.model.event_bus_models.EventBusMentionUsersClick;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContacts;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class MentionUsersAdapter extends RecyclerView.Adapter<MentionUsersAdapter.ChooseImagesFromGelleryHolder>
        implements Filterable {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<MentionModel> mentionlist = new ArrayList<>();
    private List<MentionModel> filterMentionlist = new ArrayList<>();

    public MentionUsersAdapter(Context context, List<MentionModel> mentionlist) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.mentionlist = mentionlist;
        this.filterMentionlist = mentionlist;
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
    public ChooseImagesFromGelleryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.search_mention_row, viewGroup, false);
        return new ChooseImagesFromGelleryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseImagesFromGelleryHolder holder, int position) {
        String imagePath = filterMentionlist.get(position).getUserImage();
        String name = filterMentionlist.get(position).getUserName();
        name = name.substring(1);
        holder.names.setText(name);
        if (imagePath != null && !imagePath.isEmpty()) {
            download(imagePath, holder.userImage);
        }

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
    public int getItemCount() {
        return filterMentionlist.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterMentionlist = mentionlist;
                } else {
                    List<MentionModel> filteredList = new ArrayList<>();
                    for (MentionModel row : mentionlist) {
                        if (row.getUserName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    filterMentionlist = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterMentionlist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterMentionlist = (ArrayList<MentionModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ChooseImagesFromGelleryHolder extends RecyclerView.ViewHolder {
        TextView names;
        ImageView userImage;

        public ChooseImagesFromGelleryHolder(@NonNull View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.imageViewProfile);
            names = (TextView) itemView.findViewById(R.id.textViewUserName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    LnqApplication.getInstance().editor.putString("mUserId", filterMentionlist.get(getBindingAdapterPosition()).getUserId()).apply();
//                    LnqApplication.getInstance().editor.putString("mProfileId", filterMentionlist.get(getBindingAdapterPosition()).getUserProfileId()).apply();
                    Mentionable mention = (Mentionable) filterMentionlist.get(getBindingAdapterPosition());
                    EventBus.getDefault().post(new EventBusMentionUsersClick(getAdapterPosition(), mention, filterMentionlist.get(getBindingAdapterPosition()).getUserId(), filterMentionlist.get(getBindingAdapterPosition()).getUserProfileId()));
                }
            });

        }
    }
}
