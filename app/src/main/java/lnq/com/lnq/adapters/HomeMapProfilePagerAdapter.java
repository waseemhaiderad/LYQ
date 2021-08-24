package lnq.com.lnq.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.model.event_bus_models.EventBusHomeMapProfileClick;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;
import lnq.com.lnq.model.event_bus_models.EventBusMapLnqClick;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class HomeMapProfilePagerAdapter extends PagerAdapter {
    private String cachePath = "";
    private TransferUtility transferUtility;

    //    Constant fields....
    private static final String TAG = "HomeMapProfilePagerAdap";

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private List<UpdateLocationData> updateLocationDataArrayList = new ArrayList<>();

    //    Font fields....
    private FontUtils fontUtils;

    public HomeMapProfilePagerAdapter(Context context, List<UpdateLocationData> updateLocationDataArrayList) {
        this.context = context;
        this.updateLocationDataArrayList = updateLocationDataArrayList;
        layoutInflater = LayoutInflater.from(context);
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
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = layoutInflater.inflate(R.layout.cus_home_map_profile, container, false);
        if (updateLocationDataArrayList.size() > 0) {

            final UpdateLocationData updateLocationData = updateLocationDataArrayList.get(position);

            if (updateLocationData != null) {

//                Android fields initialization....
                ImageView imageViewProfile = view.findViewById(R.id.imageViewProfile);
                ImageView imageViewProfileVerified = view.findViewById(R.id.imageViewVerifiedProfileTick);
                ImageView imageViewMenu = view.findViewById(R.id.imageViewPopupMenu);
                ImageView imageViewProfileLinkedConnection = view.findViewById(R.id.imageViewRoundedLinkedProfile);
                ImageView imageViewCurrentLocation = view.findViewById(R.id.imageViewCurrentLocation);
                TextView textViewName = view.findViewById(R.id.textViewName);
                TextView textViewJobTitle = view.findViewById(R.id.textViewJobTitle);
                TextView textViewCompany = view.findViewById(R.id.textViewCompany);
                TextView textViewStatus = view.findViewById(R.id.textViewStatusMessage);
                TextView textViewHomeLocation = view.findViewById(R.id.textViewHomeLocation);
                TextView textViewCurrentLocation = view.findViewById(R.id.textViewCurrentLocation);
                ImageView imageViewLnqConnection = view.findViewById(R.id.imageViewLnqConnection);
                TextView textViewSince = view.findViewById(R.id.textViewConnectionSince);
                TextView textViewConnectionDate = view.findViewById(R.id.textViewConnectionDate);
                TextView textViewIn = view.findViewById(R.id.textViewConnectionIn);
                TextView textViewConnectionLocation = view.findViewById(R.id.textViewConnectionLocation);
                ConstraintLayout constraintLayout = view.findViewById(R.id.mRoot);
                Button buttonRequestLnq = view.findViewById(R.id.buttonLnq);
                ImageView imageViewFavoriteBorder = view.findViewById(R.id.imageViewFavoriteBorder);
                ScrollView scroll = view.findViewById(R.id.scroll);
//                scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//                    float y = 0;
//
//                    @Override
//                    public void onScrollChanged() {
//                        if (scroll.getScrollY() > y) {
//                            Log.v("Message", "Scrolls Down");
//                        } else {
//                            Log.v("Message", "Scrolls Up");
//                        }
//                        y = scroll.getScrollY();
//                    }
//                });


//                Setting custom font....
                fontUtils.setTextViewRegularFont(textViewName);
                fontUtils.setTextViewRegularFont(textViewJobTitle);
                fontUtils.setTextViewRegularFont(textViewStatus);
                fontUtils.setTextViewRegularFont(textViewHomeLocation);
                fontUtils.setTextViewRegularFont(textViewCurrentLocation);
                fontUtils.setTextViewRegularFont(textViewSince);
                fontUtils.setTextViewRegularFont(textViewIn);
                fontUtils.setTextViewSemiBold(textViewConnectionDate);
                fontUtils.setTextViewSemiBold(textViewConnectionLocation);
                fontUtils.setTextViewSemiBold(textViewCompany);

                String name = updateLocationData.getUser_name();
                String jobTitle = updateLocationData.getUser_current_position();
                String company = updateLocationData.getUser_company();
                String status = updateLocationData.getUser_status_msg();
                String distance = updateLocationData.getUser_distance();
                String locationName = updateLocationData.getLocation_name();
                String connectionDate = updateLocationData.getUser_connection_date();
                final String isConnected = updateLocationData.getIs_connection();
                String isFavorite = updateLocationData.getIs_favorite();
                String homeLocation = updateLocationData.getUser_home_address();

                if (status.isEmpty()) {
                    status = "No status yet.";
                }
                if (name.length() > 17) {
                    name = name.substring(0, 17);
                }
                if (jobTitle.isEmpty()) {
                    textViewJobTitle.setText("");
                } else {
                    textViewJobTitle.setText(jobTitle + " . ");
                }
                textViewCompany.setText(company);
                textViewStatus.setText(status);
                textViewName.setText(name);
                textViewHomeLocation.setText(homeLocation);

                if (updateLocationData.getUser_image() != null && !updateLocationData.getUser_image().isEmpty()) {
                    download(updateLocationData.getUser_image(), imageViewProfile);
                }

                try {
                    if (locationName.isEmpty() && distance.isEmpty()) {
                        textViewCurrentLocation.setVisibility(View.GONE);
                        imageViewCurrentLocation.setVisibility(View.GONE);
                    } else {
                        imageViewCurrentLocation.setVisibility(View.VISIBLE);
                        textViewCurrentLocation.setVisibility(View.VISIBLE);
                        if (locationName.isEmpty()) {
                            if (Double.parseDouble(distance) > 5.0 && distance.contains(".")) {
                                distance = distance.substring(0, distance.indexOf("."));
                                textViewCurrentLocation.setText(distance + " mi");
                            } else {
                                textViewCurrentLocation.setText(distance + " mi");
                            }
                        } else {
                            if (distance.length() > 0) {
                                if (Double.parseDouble(distance) > 5.0 && distance.contains(".")) {
                                    distance = distance.substring(0, distance.indexOf("."));
                                    textViewCurrentLocation.setText(locationName + " . " + distance + " mi");
                                } else {
                                    textViewCurrentLocation.setText(locationName + " . " + distance + " mi");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                if (isFavorite.equals(Constants.FAVORITE)) {
                    textViewConnectionLocation.setVisibility(View.VISIBLE);
                    textViewConnectionDate.setText(connectionDate);
                    textViewSince.setVisibility(View.VISIBLE);
                    textViewIn.setVisibility(View.VISIBLE);
                    imageViewProfileLinkedConnection.setVisibility(View.GONE);
                    imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                    imageViewLnqConnection.setVisibility(View.VISIBLE);
                    switch (isConnected) {
                        case Constants.CONTACTED:
                            buttonRequestLnq.setTextColor(context.getResources().getColor(R.color.colorWhite));
                            imageViewFavoriteBorder.setImageResource(R.drawable.ic_fav_teen_border);
                            if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString("id", ""))) {
                                buttonRequestLnq.setBackground(context.getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                                buttonRequestLnq.setText(context.getResources().getString(R.string.sent));
                                textViewConnectionLocation.setText(updateLocationData.getSender_location());
                                textViewSince.setText(context.getResources().getString(R.string.request_sent_on));
                            } else {
                                buttonRequestLnq.setBackground(context.getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                                buttonRequestLnq.setText(context.getResources().getString(R.string.recv));
                                textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                                textViewSince.setText(context.getResources().getString(R.string.request_received_on));
                            }
                            break;
                        case Constants.CONNECTED:
                            buttonRequestLnq.setText("LNQ'd");
                            textViewSince.setText(context.getResources().getString(R.string.since));
                            if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString("id", ""))) {
                                textViewConnectionLocation.setText(updateLocationData.getSender_location());
                            } else {
                                textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                            }
                            break;
                        default:
                            imageViewFavoriteBorder.setImageResource(R.drawable.ic_fav_lnq_border);
                            textViewSince.setVisibility(View.GONE);
                            imageViewLnqConnection.setVisibility(View.GONE);
                            textViewConnectionDate.setVisibility(View.GONE);
                            textViewConnectionLocation.setVisibility(View.GONE);
                            textViewIn.setVisibility(View.GONE);
//                            buttonRequestLnq.setBackground(context.getResources().getDrawable(R.drawable.bg_rectangle_grey_new));
//                            buttonRequestLnq.setTextColor(context.getResources().getColor(R.color.colorWhite));
                            buttonRequestLnq.setText(context.getResources().getString(R.string.lnq));
                            break;
                    }
                } else {
                    textViewConnectionLocation.setVisibility(View.VISIBLE);
                    textViewConnectionDate.setVisibility(View.VISIBLE);
                    textViewIn.setVisibility(View.VISIBLE);
                    imageViewLnqConnection.setVisibility(View.VISIBLE);
                    imageViewFavoriteBorder.setVisibility(View.GONE);
                    imageViewProfileLinkedConnection.setVisibility(View.VISIBLE);
                    switch (isConnected) {
                        case Constants.CONTACTED:
                            buttonRequestLnq.setTextColor(context.getResources().getColor(R.color.colorWhite));
                            textViewConnectionDate.setText(connectionDate);
                            imageViewProfileLinkedConnection.setBackground(context.getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                            imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                            if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                                buttonRequestLnq.setBackground(context.getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                                buttonRequestLnq.setText(context.getResources().getString(R.string.sent));
                                textViewConnectionLocation.setText(updateLocationData.getSender_location());
                                textViewSince.setText(context.getResources().getString(R.string.request_sent_on));
                            } else {
                                buttonRequestLnq.setBackground(context.getResources().getDrawable(R.drawable.bg_rectangle_teen_new));
                                buttonRequestLnq.setText(context.getResources().getString(R.string.recv));
                                textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                                textViewSince.setText(context.getResources().getString(R.string.request_received_on));
                            }
                            break;
                        case Constants.CONNECTED:
                            buttonRequestLnq.setText("LNQ'd");
                            textViewSince.setText(context.getResources().getString(R.string.since));
                            textViewConnectionDate.setText(connectionDate);
                            if (updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString("id", ""))) {
                                textViewConnectionLocation.setText(updateLocationData.getSender_location());
                            } else {
                                textViewConnectionLocation.setText(updateLocationData.getReceiver_location());
                            }
                            imageViewProfileLinkedConnection.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
                            imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                            break;
                        default:
//                            buttonRequestLnq.setBackground(context.getResources().getDrawable(R.drawable.bg_rectangle_grey_new));
//                            buttonRequestLnq.setTextColor(context.getResources().getColor(R.color.colorWhite));
                            buttonRequestLnq.setText(context.getResources().getString(R.string.lnq));
                            textViewConnectionDate.setVisibility(View.GONE);
                            imageViewProfileLinkedConnection.setVisibility(View.GONE);
                            imageViewLnqConnection.setVisibility(View.GONE);
                            textViewIn.setVisibility(View.GONE);
                            textViewConnectionLocation.setVisibility(View.GONE);
                            imageViewFavoriteBorder.setVisibility(View.GONE);
                            textViewSince.setVisibility(View.GONE);
                            imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                            break;
                    }
                }
                OverScrollDecoratorHelper.setUpOverScroll(scroll);
                buttonRequestLnq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isConnected.isEmpty()) {
                            EventBus.getDefault().post(new EventBusMapLnqClick(Constants.LNQ, position));
                        } else if (isConnected.equals(Constants.CONTACTED)) {
                            EventBus.getDefault().post(new EventBusMapLnqClick(Constants.CONTACTED, position));
                        } else if (isConnected.equals(Constants.CONNECTED)) {
                            EventBus.getDefault().post(new EventBusMapLnqClick(Constants.CONNECTED, position));
                        }
                    }
                });
//                imageViewProfileVerified.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE_VERIFIED, position));
//                    }
//                });
                imageViewMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.MORE, position));
                    }
                });
                constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.FINISH, -1));
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE, position));
                    }
                });
                imageViewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE, position));
                    }
                });
                textViewName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE, position));
                    }
                });
                textViewJobTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE, position));
                    }
                });
                textViewCompany.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE, position));
                    }
                });
                textViewHomeLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE, position));
                    }
                });
                textViewCurrentLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMapLnqClick(Constants.PROFILE, position));
                    }
                });
                container.addView(view);
            }
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public int getCount() {
        return updateLocationDataArrayList.size();
    }

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
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

}