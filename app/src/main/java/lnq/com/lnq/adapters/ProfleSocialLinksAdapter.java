package lnq.com.lnq.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSocial;
import lnq.com.lnq.model.gson_converter_models.blockedusers.GetBlockedUserList;
import lnq.com.lnq.model.userprofile.SocialMediaLinksModel;
import lnq.com.lnq.utils.FontUtils;

public class ProfleSocialLinksAdapter extends RecyclerView.Adapter<ProfleSocialLinksAdapter.SocialMediaHolder> {

    private Context context;
    private List<SocialMediaLinksModel> socialList = new ArrayList<>();
    private String type;

    public ProfleSocialLinksAdapter(Context context, List<SocialMediaLinksModel> socialList, String type) {
        this.context = context;
        this.socialList = socialList;
        this.type = type;
    }

    @NonNull
    @Override
    public SocialMediaHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_social_links, viewGroup, false);
        return new SocialMediaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialMediaHolder historyHolder, int i) {
        SocialMediaLinksModel mediaLinksModel = socialList.get(i);
        String link = mediaLinksModel.getSocialMediasLinks();
        historyHolder.socialMediaLink.setText(link);
        if (mediaLinksModel.getSocialMediasLinks().contains("www.facebook.com") || mediaLinksModel.getSocialMediasLinks().contains("www.fb.com")) {
            Glide.with(context).load(R.drawable.facebook).into(historyHolder.imageViewMedia);
        }
        if (mediaLinksModel.getSocialMediasLinks().contains("twitter") || mediaLinksModel.getSocialMediasLinks().contains("www.Twitter.com")) {
            Glide.with(context).load(R.drawable.twitter).into(historyHolder.imageViewMedia);
        }
        if (mediaLinksModel.getSocialMediasLinks().contains("instagram") || mediaLinksModel.getSocialMediasLinks().contains("www.Instagram.com")) {
            Glide.with(context).load(R.drawable.instagram).into(historyHolder.imageViewMedia);
        }
        if (mediaLinksModel.getSocialMediasLinks().contains("www.linkedin.com") || mediaLinksModel.getSocialMediasLinks().contains("www.LinkedIn.com")) {
            Glide.with(context).load(R.drawable.linkedin).into(historyHolder.imageViewMedia);
        }
        historyHolder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusRemoveSocial(i));
            }
        });
        if (type.equalsIgnoreCase("edit")) {
            historyHolder.buttonRemove.setVisibility(View.VISIBLE);
            historyHolder.socialMediaLink.setTextColor(ContextCompat.getColor(context, R.color.colorBlueNewTheme));
        } else {
            historyHolder.buttonRemove.setVisibility(View.VISIBLE);
            historyHolder.socialMediaLink.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        }
    }

    @Override
    public int getItemCount() {
        return socialList.size();
    }

    class SocialMediaHolder extends RecyclerView.ViewHolder {

        TextView socialMediaLink;
        ImageView imageViewMedia;
        Button buttonRemove;

        SocialMediaHolder(@NonNull View itemView) {
            super(itemView);
            socialMediaLink = itemView.findViewById(R.id.textViewLink);
            imageViewMedia = itemView.findViewById(R.id.imageView);
            buttonRemove = itemView.findViewById(R.id.buttonRemoveSocialLinks);

        }
    }
}