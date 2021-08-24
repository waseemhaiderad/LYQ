package lnq.com.lnq.custom.views.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import lnq.com.lnq.R;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<GalleryModel> galleryModelList;
    private Context context;

    public GalleryAdapter(Context context, List<GalleryModel> data) {
        layoutInflater = LayoutInflater.from(context);
        this.galleryModelList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.cus_gallery_own, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GalleryModel mObjCur = galleryModelList.get(position);
        Glide.with(context).load(new File(mObjCur.getmPath()))
                .apply(new RequestOptions().centerCrop())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_wait))
                .apply(new RequestOptions().error(R.drawable.palceholer_error))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.imageViewBorder.getLayoutParams().width = holder.mImg.getWidth();
                        holder.imageViewBorder.getLayoutParams().height = holder.mImg.getHeight();
                        holder.imageViewBorder.requestLayout();
                        return false;
                    }
                })
                .into(holder.mImg);
        if (mObjCur.ismSelected()) {
            holder.imageViewBorder.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewBorder.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return galleryModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatImageView mImg;
        ImageView imageViewBorder;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imageViewBorder = itemLayoutView.findViewById(R.id.mImgBorderGallery);
            mImg = itemLayoutView.findViewById(R.id.mImg);
            imageViewBorder.setVisibility(View.GONE);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(new EventGalleryModel(getAdapterPosition()));
        }
    }

}