package lnq.com.lnq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.fragments.gallery.GalleryFragmentNew;
import lnq.com.lnq.model.event_bus_models.EventBusCloseChatImageLayout;
import lnq.com.lnq.utils.FontUtils;

public class ChooseImageAdapter extends RecyclerView.Adapter<ChooseImageAdapter.ChooseImagesFromGelleryHolder> {

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<File> filesList = new ArrayList<>();

    public ChooseImageAdapter(Context context, List<File> filesList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.filesList = filesList;
        fontUtils = FontUtils.getFontUtils(context);
    }

    @NonNull
    @Override
    public ChooseImagesFromGelleryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_row_chat_images, viewGroup, false);
        return new ChooseImagesFromGelleryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseImagesFromGelleryHolder holder, int position) {
        String imagePath = filesList.get(position).getAbsolutePath();
        Glide.with(context)
                .load(imagePath)
                .into(holder.imageChoose);
//        GalleryFragmentNew.loadImage(holder.imageChoose, imagePath);

        holder.imageViewCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filesList.remove(position);
                holder.imageChoose.setImageResource(0);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, filesList.size());

                EventBus.getDefault().post(new EventBusCloseChatImageLayout());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }


    class ChooseImagesFromGelleryHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageChoose, imageViewCross;

        public ChooseImagesFromGelleryHolder(@NonNull View itemView) {
            super(itemView);
            imageChoose = itemView.findViewById(R.id.imageChoose);
            imageViewCross = itemView.findViewById(R.id.imageViewCross);

//            imageViewCross.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    imageChoose.setImageResource(0);
//                    itemView.setVisibility(View.GONE);
//                }
//            });
        }
    }
}
