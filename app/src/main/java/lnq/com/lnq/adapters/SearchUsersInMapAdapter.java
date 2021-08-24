package lnq.com.lnq.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
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
import java.util.Arrays;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.model.event_bus_models.EventBusQRProfilesClick;
import lnq.com.lnq.model.event_bus_models.EventBusUserMapSearch;
import lnq.com.lnq.model.gson_converter_models.SearchUserInMapModel;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.SortingUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class SearchUsersInMapAdapter extends RecyclerView.Adapter<SearchUsersInMapAdapter.UserSearchInMapViewHolder> {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<SearchUserInMapModel> searchUserInMapModels;
    private List<String> tagsList = new ArrayList<>();
    private String searchKey;

    public SearchUsersInMapAdapter(Context context, List<SearchUserInMapModel> searchUserInMapModels, String searchKey) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        createTransferUtility();
        this.searchUserInMapModels = searchUserInMapModels;
        this.searchKey = searchKey;
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
    public UserSearchInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_searchresults_row, parent, false);
        return new UserSearchInMapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSearchInMapViewHolder holder, int position) {
        String imagePath = searchUserInMapModels.get(position).getUserImage();
        String matchType = searchUserInMapModels.get(position).getMatchType();
        if (imagePath != null && !imagePath.isEmpty()) {
            download(imagePath, holder.imageViewProfile);
        }

        holder.textViewName.setText(searchUserInMapModels.get(position).getUserName());
        holder.textViewCompanyName.setText(searchUserInMapModels.get(position).getUserCompany());
        holder.textViewJob.setText(searchUserInMapModels.get(position).getUserJobTitle());
        holder.textViewDistance.setText(searchUserInMapModels.get(position).getUserDistance());
        if (matchType.equalsIgnoreCase("name")) {
            holder.textViewJobTitle.setText("");
        } else if (matchType.equalsIgnoreCase("position")) {
            holder.textViewJobTitle.setText(searchUserInMapModels.get(position).getUserJobTitle());
        } else if (matchType.equalsIgnoreCase("company")) {
            holder.textViewJobTitle.setText(searchUserInMapModels.get(position).getUserCompany());
        } else if (matchType.equalsIgnoreCase("interests")) {
            String tags = searchUserInMapModels.get(position).getUserInterests();
            StringBuilder text = new StringBuilder();
            if (!tags.isEmpty()) {
                List<String> tagsList = new ArrayList<>(Arrays.asList(tags.split(",")));
                SortingUtils.sortTagsList(tagsList);
                for (int i = 0; i < tagsList.size(); i++) {
                    text.append(" ").append(tagsList.get(i));
                }
            }
            holder.textViewJobTitle.setText(text.toString());
        } else if (matchType.equalsIgnoreCase("address")) {
            holder.textViewJobTitle.setText(searchUserInMapModels.get(position).getUserAddress());
        } else if (matchType.equalsIgnoreCase("status")) {
            holder.textViewJobTitle.setText(searchUserInMapModels.get(position).getUserStatus());
        } else if (matchType.equalsIgnoreCase("bio")) {
            holder.textViewJobTitle.setText(searchUserInMapModels.get(position).getUserBio());
        } else if (matchType.equalsIgnoreCase("task")) {
            holder.textViewJobTitle.setText(searchUserInMapModels.get(position).getUserTasks());
        } else if (matchType.equalsIgnoreCase("note")) {
            holder.textViewJobTitle.setText(searchUserInMapModels.get(position).getUserNotes());
        } else {
            holder.textViewJobTitle.setText("");
        }

        String first = holder.textViewName.getText().toString();
        String second = searchKey.toLowerCase();
        first = first.toLowerCase().replaceAll(second, "<font color='#743fff'>" + second + "</font>");
        first = first.substring(0, 1).toUpperCase() + first.substring(1);
        holder.textViewName.setText(Html.fromHtml(first));

        String first1 = holder.textViewJobTitle.getText().toString();
        first1 = first1.toLowerCase().replaceAll(second, "<font color='#743fff'>" + second + "</font>");
        holder.textViewJobTitle.setText(Html.fromHtml(first1));

        String first2 = holder.textViewCompanyName.getText().toString();
        first2 = first2.toLowerCase().replaceAll(second, "<font color='#743fff'>" + second + "</font>");
        holder.textViewCompanyName.setText(Html.fromHtml(first2));

        String first3 = holder.textViewJob.getText().toString();
        first3 = first3.toLowerCase().replaceAll(second, "<font color='#743fff'>" + second + "</font>");
        holder.textViewJob.setText(Html.fromHtml(first3));

//        //split the second string into words
//        List<String> wordsOfSecond1 = Arrays.asList(second1.split(" "));

        //split and compare each word of the first string
//        for (String word1 : first1.split(" ")) {
//            if(wordsOfSecond1.contains(word1)){
//                Spannable spannable = new SpannableString(first1);
//                spannable.setSpan(new ForegroundColorSpan(Color.BLUE), first1.indexOf(second1), first1.indexOf(second1) + second1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                holder.textViewJobTitle.setText(spannable, TextView.BufferType.SPANNABLE);
//            }
////                System.out.println(word);
//        }

    }

    @Override
    public int getItemCount() {
        return searchUserInMapModels.size();
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

    class UserSearchInMapViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageViewProfile;
        AppCompatTextView textViewName, textViewJobTitle, textViewCompanyName, textViewDistance, textViewSearchType, textViewJob;

        UserSearchInMapViewHolder(View itemView) {
            super(itemView);

            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewCompanyName = itemView.findViewById(R.id.textViewCompanyName);
            textViewJob = itemView.findViewById(R.id.textViewJob);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
            textViewSearchType = itemView.findViewById(R.id.textViewSearchType);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUserMapSearch(getAdapterPosition()));
                }
            });

        }
    }

}
