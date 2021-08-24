package lnq.com.lnq.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.tokenautocomplete.ViewSpan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.fast_scroller.RecyclerViewFastScroller;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.model.PhoneContactsModel;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContacts;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ExportContactsAdapter extends RecyclerView.Adapter<ExportContactsAdapter.UserExportContactsViewHolder>
        implements RecyclerViewFastScroller.BubbleTextGetter, Filterable {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private OnCheckedListener onCheckedListener;
    private List<ExportContacts> exportContactsModelList = new ArrayList<>();
    private List<ExportContacts> exportContactsFilteredList = new ArrayList<>();

    public ExportContactsAdapter(Context context, List<ExportContacts> exportContactsModelList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        createTransferUtility();
        this.exportContactsModelList = exportContactsModelList;
        this.exportContactsFilteredList = exportContactsModelList;
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

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    @NonNull
    @Override
    public UserExportContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_phone_contacts_dialog, parent, false);
        return new UserExportContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserExportContactsViewHolder holder, int position) {
        ExportContacts exportContactsModel = exportContactsFilteredList.get(position);

        List<String> number = exportContactsModel.getPhone();
        String name = exportContactsModel.getName();
        String isUserFavorite = exportContactsModel.getIsFavorite();
        String connectionStatus = exportContactsModel.getConnectionStatus();
        boolean isSelected = exportContactsModel.isSelected();

        if (exportContactsModel.getImage() != null && !exportContactsModel.getImage().isEmpty()) {
            download(exportContactsModel.getImage(), holder.imageViewProfile);
        }

        if (exportContactsModel.getName().isEmpty()) {
            if (number.size() > 0)
                holder.textViewName.setText(number.get(0));
        } else {
            holder.textViewName.setText(name);
        }

        if (isUserFavorite.equals(Constants.FAVORITE)) {
            holder.imageViewLinked.setVisibility(View.GONE);
            holder.imageViewFavBorder.setVisibility(View.VISIBLE);
            holder.imageViewProfile.setBackground(null);
            if (connectionStatus.equals("")) {
                holder.imageViewFavBorder.setImageResource(R.drawable.ic_fav_lnq_border);
            } else if (connectionStatus.equals(Constants.CONTACTED)) {
                holder.imageViewFavBorder.setImageResource(R.drawable.ic_fav_teen_border);
            }
        } else {
            switch (connectionStatus) {
                case Constants.CONTACTED:
                    holder.imageViewFavBorder.setVisibility(View.GONE);
                    holder.imageViewLinked.setVisibility(View.VISIBLE);
                    holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                    holder.imageViewLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen));
                    break;
                case Constants.CONNECTED:
                    holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                    holder.imageViewLinked.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
                    holder.imageViewLinked.setVisibility(View.VISIBLE);
                    holder.imageViewFavBorder.setVisibility(View.GONE);
                    break;
                default:
                    holder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                    holder.imageViewLinked.setVisibility(View.GONE);
                    holder.imageViewFavBorder.setVisibility(View.GONE);
                    break;
            }
        }
        if (isSelected) {
            holder.imageViewSelect.setVisibility(View.INVISIBLE);
            holder.imageViewDeselect.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewSelect.setVisibility(View.VISIBLE);
            holder.imageViewDeselect.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return exportContactsFilteredList.size();
    }

//    void download(String objectKey,ImageView imageView) {
//        final File fileDownload = new File(context.getCacheDir(), objectKey);
//
//        TransferObserver transferObserver = transferUtility.download(
//                Constants.BUCKET_NAME,
//                objectKey,
//                fileDownload
//        );
//        transferObserver.setTransferListener(new TransferListener(){
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
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}
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
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= exportContactsFilteredList.size())
            return null;

        String name = exportContactsFilteredList.get(pos).getName();
        if (name == null || name.length() < 1)
            return null;

        return exportContactsFilteredList.get(pos).getName().substring(0, 1);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    exportContactsFilteredList = exportContactsModelList;
                } else {
                    List<ExportContacts> filteredList = new ArrayList<>();
                    for (ExportContacts row : exportContactsModelList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    exportContactsFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = exportContactsFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                exportContactsFilteredList = (ArrayList<ExportContacts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class UserExportContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewName;
        ImageView imageViewProfile;
        AppCompatImageView imageViewSelect, imageViewDeselect, imageViewFavBorder, imageViewLinked;

        UserExportContactsViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewProfile = itemView.findViewById(R.id.imageViewContact);
            imageViewSelect = itemView.findViewById(R.id.imageViewSelectContact);
            imageViewDeselect = itemView.findViewById(R.id.imageViewDeSelectContact);
            imageViewFavBorder = itemView.findViewById(R.id.imageViewFavoriteBorder);
            imageViewLinked = itemView.findViewById(R.id.imageViewProfileLinkedConnection);

            FontUtils.getFontUtils(context).setTextViewSemiBold(textViewName);

//            imageViewSelect.setOnClickListener(this);
//            imageViewDeselect.setOnClickListener(this);
//            viewData.setOnClickListener(this);
//            textViewName.setOnClickListener(this);
//            imageViewProfile.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCheckedListener != null) {
                        onCheckedListener.onChecked(itemView, getAdapterPosition(), !exportContactsFilteredList.get(getAdapterPosition()).isSelected(), exportContactsFilteredList.get(getAdapterPosition()));
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.textViewName:
//                case R.id.imageViewContact:
//                case R.id.imageViewSelectContact:
//                    if (onCheckedListener != null) {
//                        onCheckedListener.onChecked(view, getAdapterPosition(), true);
//                    }
//                    break;
//                case R.id.viewData:
//                case R.id.imageViewDeSelectContact:
//                    if (onCheckedListener != null) {
//                        onCheckedListener.onChecked(view, getAdapterPosition(), false);
//                    }
//                    break;
//            }
        }
    }

    public interface OnCheckedListener {
        void onChecked(View view, int position, boolean isChecked, ExportContacts exportContacts);
    }

}
