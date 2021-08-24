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
import lnq.com.lnq.model.event_bus_models.EventBusUpdateGroupOffGridVisibilty;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateGroupVisibilty;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.CreateUserGroup;
import lnq.com.lnq.utils.FontUtils;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class GroupVisibiltyAdapter extends RecyclerView.Adapter<GroupVisibiltyAdapter.UserExportContactsViewHolder> {

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<CreateUserGroup> selectedExportContacts = new ArrayList<>();
    private List<CreateUserGroup> exportContactsFilteredList = new ArrayList<>();
    String selectionType = "";
    String selectionType2 = "";

    public GroupVisibiltyAdapter(Context context, List<CreateUserGroup> userConnectionsData) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.selectedExportContacts = userConnectionsData;
        this.exportContactsFilteredList = userConnectionsData;
    }


    @NonNull
    @Override
    public UserExportContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_show_group_visbility, parent, false);
        return new UserExportContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserExportContactsViewHolder holder, int position) {
        CreateUserGroup groupName = exportContactsFilteredList.get(position);
        holder.textViewVisibilityStatusHeading.setText(groupName.getGroup_name());

        if (groupName.getVisible_at() != null && groupName.getVisible_to() != null) {
            selectionType = groupName.getVisible_to();
            selectionType2 = groupName.getVisible_at();
        }
        if (groupName.getVisible_at().isEmpty() && groupName.getVisible_to().isEmpty()){
            EventBus.getDefault().post(new EventBusUpdateGroupOffGridVisibilty(0, LnqApplication.getInstance().sharedPreferences.getString("visible_at", ""), groupName.getId()));
            EventBus.getDefault().post(new EventBusUpdateGroupVisibilty(0, LnqApplication.getInstance().sharedPreferences.getString("visible_to", ""), groupName.getId()));
        }

        switch (selectionType) {
            case Constants.NONE:
                holder.viewDividerNoboby.setVisibility(View.VISIBLE);
                holder.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                holder.viewDividerPeopleNearMe.setVisibility(View.INVISIBLE);

                holder.imageViewNobody.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_nobody_white));
                holder.imageViewEveryOne.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                holder.imageViewPeopleNearMe.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_people_near_me_transparent));

                holder.textViewNobody.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewPeopleNearMe.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewEveryOne.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.NEAR_BY:
                holder.viewDividerNoboby.setVisibility(View.INVISIBLE);
                holder.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                holder.viewDividerPeopleNearMe.setVisibility(View.VISIBLE);

                holder.imageViewNobody.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                holder.imageViewEveryOne.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                holder.imageViewPeopleNearMe.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_people_near_me_white));

                holder.textViewNobody.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewPeopleNearMe.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewEveryOne.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.EVERY_ONE:
                holder.viewDividerNoboby.setVisibility(View.INVISIBLE);
                holder.viewDividerEveryOne.setVisibility(View.VISIBLE);
                holder.viewDividerPeopleNearMe.setVisibility(View.INVISIBLE);

                holder.imageViewNobody.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                holder.imageViewEveryOne.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_everyone_white));
                holder.imageViewPeopleNearMe.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_people_near_me_transparent));

                holder.textViewNobody.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewPeopleNearMe.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewEveryOne.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            default:
                holder.viewDividerNoboby.setVisibility(View.INVISIBLE);
                holder.viewDividerEveryOne.setVisibility(View.INVISIBLE);
                holder.viewDividerPeopleNearMe.setVisibility(View.VISIBLE);

                holder.imageViewNobody.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                holder.imageViewEveryOne.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                holder.imageViewPeopleNearMe.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_people_near_me_white));

                holder.textViewNobody.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewPeopleNearMe.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewEveryOne.setTextColor(context.getResources().getColor(R.color.colorWhiteTrans));
                break;
        }

        switch (selectionType2) {
            case Constants.OFF_GRID:
                holder.viewDividerCity.setVisibility(View.INVISIBLE);
                holder.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                holder.viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                holder.viewDividerOffGrid.setVisibility(View.VISIBLE);

                holder.imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_white));
                holder.imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                holder.imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                holder.imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_transparent));

                holder.textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.GLOBAL:
                holder.viewDividerCity.setVisibility(View.INVISIBLE);
                holder.viewDividerGlobalRegion.setVisibility(View.VISIBLE);
                holder.viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                holder.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                holder.imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                holder.imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_white));
                holder.imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                holder.imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_transparent));

                holder.textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.LOCAL:
                holder.viewDividerCity.setVisibility(View.INVISIBLE);
                holder.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                holder.viewDividerLocalRegion.setVisibility(View.VISIBLE);
                holder.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                holder.imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                holder.imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                holder.imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_white));
                holder.imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_transparent));

                holder.textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            case Constants.CITY:
                holder.viewDividerCity.setVisibility(View.VISIBLE);
                holder.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                holder.viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                holder.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                holder.imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                holder.imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                holder.imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                holder.imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_white));

                holder.textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
            default:
                holder.viewDividerCity.setVisibility(View.INVISIBLE);
                holder.viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                holder.viewDividerLocalRegion.setVisibility(View.VISIBLE);
                holder.viewDividerOffGrid.setVisibility(View.INVISIBLE);

                holder.imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                holder.imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                holder.imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_white));
                holder.imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_transparent));

                holder.textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                holder.textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return exportContactsFilteredList.size();
    }

    class UserExportContactsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewVisibilityStatusHeading, textViewNobody, textViewPeopleNearMe, textViewEveryOne;
        TextView textViewOffGrid, textViewGlobalRegion, textViewLocalRegion, textViewCity;
        AppCompatImageView imageViewNobody, imageViewPeopleNearMe, imageViewEveryOne;
        AppCompatImageView imageViewOffGrid, imageViewGlobalRegion, imageViewLocalRegion, imageViewCity;
        View viewDividerNoboby, viewDividerPeopleNearMe, viewDividerEveryOne;
        View viewDividerOffGrid, viewDividerGlobalRegion, viewDividerLocalRegion, viewDividerCity;

        UserExportContactsViewHolder(View itemView) {
            super(itemView);

            textViewVisibilityStatusHeading = itemView.findViewById(R.id.textViewVisibilityStatusHeading);
            textViewNobody = itemView.findViewById(R.id.textViewNobody);
            textViewPeopleNearMe = itemView.findViewById(R.id.textViewPeopleNearMe);
            textViewEveryOne = itemView.findViewById(R.id.textViewEveryOne);
            textViewOffGrid = itemView.findViewById(R.id.textViewOffGrid);
            textViewGlobalRegion = itemView.findViewById(R.id.textViewGlobalRegion);
            textViewLocalRegion = itemView.findViewById(R.id.textViewLocalRegion);
            textViewCity = itemView.findViewById(R.id.textViewCity);
            imageViewNobody = itemView.findViewById(R.id.imageViewNobody);
            imageViewPeopleNearMe = itemView.findViewById(R.id.imageViewPeopleNearMe);
            imageViewEveryOne = itemView.findViewById(R.id.imageViewEveryOne);
            imageViewOffGrid = itemView.findViewById(R.id.imageViewOffGrid);
            imageViewGlobalRegion = itemView.findViewById(R.id.imageViewGlobalRegion);
            imageViewLocalRegion = itemView.findViewById(R.id.imageViewLocalRegion);
            imageViewCity = itemView.findViewById(R.id.imageViewCity);
            viewDividerNoboby = itemView.findViewById(R.id.viewDividerNoboby);
            viewDividerPeopleNearMe = itemView.findViewById(R.id.viewDividerPeopleNearMe);
            viewDividerEveryOne = itemView.findViewById(R.id.viewDividerEveryOne);
            viewDividerOffGrid = itemView.findViewById(R.id.viewDividerOffGrid);
            viewDividerGlobalRegion = itemView.findViewById(R.id.viewDividerGlobalRegion);
            viewDividerLocalRegion = itemView.findViewById(R.id.viewDividerLocalRegion);
            viewDividerCity = itemView.findViewById(R.id.viewDividerCity);

            imageViewNobody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUpdateGroupVisibilty(getAdapterPosition(), Constants.NONE, exportContactsFilteredList.get(getAdapterPosition()).getId()));
                    viewDividerNoboby.setVisibility(View.VISIBLE);
                    viewDividerEveryOne.setVisibility(View.INVISIBLE);
                    viewDividerPeopleNearMe.setVisibility(View.INVISIBLE);

                    imageViewNobody.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_nobody_white));
                    imageViewEveryOne.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                    imageViewPeopleNearMe.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_people_near_me_transparent));

                    textViewNobody.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewPeopleNearMe.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewEveryOne.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                }
            });

            imageViewPeopleNearMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUpdateGroupVisibilty(getAdapterPosition(), Constants.NEAR_BY, exportContactsFilteredList.get(getAdapterPosition()).getId()));
                    viewDividerNoboby.setVisibility(View.INVISIBLE);
                    viewDividerEveryOne.setVisibility(View.INVISIBLE);
                    viewDividerPeopleNearMe.setVisibility(View.VISIBLE);

                    imageViewNobody.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                    imageViewEveryOne.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_everyone_transparent));
                    imageViewPeopleNearMe.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_people_near_me_white));

                    textViewNobody.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewPeopleNearMe.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewEveryOne.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                }
            });

            imageViewEveryOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUpdateGroupVisibilty(getAdapterPosition(), Constants.EVERY_ONE, exportContactsFilteredList.get(getAdapterPosition()).getId()));
                    viewDividerNoboby.setVisibility(View.INVISIBLE);
                    viewDividerEveryOne.setVisibility(View.VISIBLE);
                    viewDividerPeopleNearMe.setVisibility(View.INVISIBLE);

                    imageViewNobody.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_nobody_transparent));
                    imageViewEveryOne.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_everyone_white));
                    imageViewPeopleNearMe.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_people_near_me_transparent));

                    textViewNobody.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewPeopleNearMe.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewEveryOne.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                }
            });

            imageViewOffGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUpdateGroupOffGridVisibilty(getAdapterPosition(), Constants.OFF_GRID, exportContactsFilteredList.get(getAdapterPosition()).getId()));
                    viewDividerCity.setVisibility(View.INVISIBLE);
                    viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                    viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                    viewDividerOffGrid.setVisibility(View.VISIBLE);

                    imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_white));
                    imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                    imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                    imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_transparent));

                    textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                }
            });

            imageViewGlobalRegion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUpdateGroupOffGridVisibilty(getAdapterPosition(), Constants.GLOBAL, exportContactsFilteredList.get(getAdapterPosition()).getId()));
                    viewDividerCity.setVisibility(View.INVISIBLE);
                    viewDividerGlobalRegion.setVisibility(View.VISIBLE);
                    viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                    viewDividerOffGrid.setVisibility(View.INVISIBLE);

                    imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                    imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_white));
                    imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                    imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_transparent));

                    textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                }
            });

            imageViewLocalRegion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUpdateGroupOffGridVisibilty(getAdapterPosition(), Constants.LOCAL, exportContactsFilteredList.get(getAdapterPosition()).getId()));
                    viewDividerCity.setVisibility(View.INVISIBLE);
                    viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                    viewDividerLocalRegion.setVisibility(View.VISIBLE);
                    viewDividerOffGrid.setVisibility(View.INVISIBLE);

                    imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                    imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                    imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_white));
                    imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_transparent));

                    textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                }
            });

            imageViewCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusUpdateGroupOffGridVisibilty(getAdapterPosition(), Constants.CITY, exportContactsFilteredList.get(getAdapterPosition()).getId()));
                    viewDividerCity.setVisibility(View.VISIBLE);
                    viewDividerGlobalRegion.setVisibility(View.INVISIBLE);
                    viewDividerLocalRegion.setVisibility(View.INVISIBLE);
                    viewDividerOffGrid.setVisibility(View.INVISIBLE);

                    imageViewOffGrid.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_off_grid_transparent));
                    imageViewGlobalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_global_region_transparent));
                    imageViewLocalRegion.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_local_region_transparent));
                    imageViewCity.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_city_white));

                    textViewCity.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewGlobalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewLocalRegion.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                    textViewOffGrid.setTextColor(context.getResources().getColor(R.color.colorBlackHintNewTheme));
                }
            });


        }
    }
}
