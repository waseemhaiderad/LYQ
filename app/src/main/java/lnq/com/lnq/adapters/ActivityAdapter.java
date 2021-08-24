package lnq.com.lnq.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.fragments.activity.FullProfilePictureActivity;
import lnq.com.lnq.fragments.qrcode.EventBusActivityGestures;
import lnq.com.lnq.listeners.activit_listeners.SwipeDetector;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusActivityClick;
import lnq.com.lnq.model.gson_converter_models.activity.ActivityData;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;

import static lnq.com.lnq.common.Constants.BUCKET_NAME;
import static lnq.com.lnq.common.Constants.COGNITO_POOL_ID;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.UserActivityHolder> {
    private String cachePath  = "";
    private TransferUtility transferUtility;

    //    Constant fields....
    private static final String TAG = "ActivityAdapter";

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Font fields....
    private FontUtils fontUtils;

    //    Instance fields....
    private List<ActivityData> activityDataList = new ArrayList<>();

    public ActivityAdapter(Context context, List<ActivityData> activityDataList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.activityDataList = activityDataList;
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
    public UserActivityHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.cus_user_activity, viewGroup, false);
        return new UserActivityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserActivityHolder userActivityHolder, int i) {
        ActivityData activityData = activityDataList.get(i);
        if (i == 0) {
            userActivityHolder.textViewDate.setVisibility(View.GONE);
            userActivityHolder.imageViewProfile.setVisibility(View.VISIBLE);
            userActivityHolder.textViewName.setText("");
            userActivityHolder.textViewActivityType.setText("");
            userActivityHolder.textViewTime.setText("");
            userActivityHolder.imageViewActivityType.setImageResource(0);
            userActivityHolder.imageViewFavoriteBorder.setImageResource(0);
            userActivityHolder.imageViewProfile.setBackground(null);
            userActivityHolder.textViewTaskDescription.setText("");
            Glide.with(context)
                    .load(0)
                    .into(userActivityHolder.imageViewProfile);
            userActivityHolder.imageViewFavoriteBorder.setVisibility(View.INVISIBLE);
        } else {
            try {
                if (!activityData.getUser_name().isEmpty()) {
                    String userName = activityData.getUser_name();
                    String userImage = activityData.getUser_avatar();
                    String activityDate = activityData.getActivity_date();
                    String activityType = activityData.getActivity_type();
                    String isFavorite = activityData.getIs_favorite();
                    String isConnection = activityData.getIs_connection();
                    String description = activityData.getDescription();
                    String time = activityData.getActivity_time();
                    try {
                        Calendar timeNeeded = Calendar.getInstance();
                        timeNeeded.setTimeInMillis(new SimpleDateFormat("HH:mm:ss").
                                parse(activityData.getActivity_time()).getTime());
                        time = "" + DateFormat.format("HH:mm a", timeNeeded);
                        time = DateUtils.getLocalConvertedTime(time);
                        time = SortingUtils.formateDate(time);
                    } catch (ParseException e) {

                    }
                    boolean isShowTime = activityData.isShowDate();

                    if (!isShowTime) {
                        userActivityHolder.textViewTime.setVisibility(View.GONE);
                        userActivityHolder.imageViewProfile.setVisibility(View.VISIBLE);
                        userActivityHolder.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                        userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) userActivityHolder.textViewActivityType.getLayoutParams();
                        layoutParams.leftMargin = (int) context.getResources().getDimension(R.dimen._50sdp);
                        userActivityHolder.textViewActivityType.setLayoutParams(layoutParams);
                        if (isFavorite.equals(Constants.FAVORITE)) {
                            userActivityHolder.imageViewProfile.setBackground(null);
                            switch (isConnection) {
                                case Constants.CONTACTED:
                                    userActivityHolder.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                                    userActivityHolder.imageViewFavoriteBorder.setBackground(context.getResources().getDrawable(R.drawable.ic_border_request_favorite));
                                    break;
                                case Constants.CONNECTED:
                                    userActivityHolder.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                                    userActivityHolder.imageViewFavoriteBorder.setBackground(context.getResources().getDrawable(R.drawable.ic_action_fav_connected));
                                    break;
                                default:
                                    userActivityHolder.imageViewFavoriteBorder.setVisibility(View.VISIBLE);
                                    userActivityHolder.imageViewFavoriteBorder.setBackground(context.getResources().getDrawable(R.drawable.ic_fav_lnq_border));
                                    break;
                            }
                        } else {
                            switch (isConnection) {
                                case Constants.CONTACTED:
                                    userActivityHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userActivityHolder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_teen_border));
                                    break;
                                case Constants.CONNECTED:
                                    userActivityHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userActivityHolder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green_border));
                                    break;
                                default:
                                    userActivityHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                                    userActivityHolder.imageViewProfile.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border));
                                    break;
                            }
                        }
                    } else {
                        userActivityHolder.textViewTime.setVisibility(View.VISIBLE);
                        userActivityHolder.textViewTime.setText(time);
                        userActivityHolder.imageViewProfile.setVisibility(View.GONE);
                        userActivityHolder.imageViewFavoriteBorder.setVisibility(View.GONE);
                        userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) userActivityHolder.textViewActivityType.getLayoutParams();
                        layoutParams.leftMargin = (int) context.getResources().getDimension(R.dimen._5sdp);
                        userActivityHolder.textViewActivityType.setLayoutParams(layoutParams);
                    }

                    try {
                        String formateDate = "";
                        String lastFormateDate = "";
                        try {
                            Date date = new SimpleDateFormat("MMMM dd,yyyy", Locale.US).parse(activityDate);
                            formateDate = DateUtils.getMyPrettyDate(date.getTime());
                        } catch (ParseException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        if (i == 1) {
                            if (!isShowTime) {
                                userActivityHolder.textViewDate.setVisibility(View.VISIBLE);
                                userActivityHolder.textViewDate2.setVisibility(View.GONE);
                            }
//                        else {
//                            userActivityHolder.textViewDate2.setVisibility(View.GONE);
//                            userActivityHolder.textViewDate.setVisibility(View.VISIBLE);
//                        }
                        } else {
                            try {
                                Date lastDate = new SimpleDateFormat("MMMM dd,yyyy", Locale.US).parse(activityDataList.get(i - 1).getActivity_date());
                                lastFormateDate = DateUtils.getMyPrettyDate(lastDate.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (formateDate.equals(lastFormateDate)) {

                                userActivityHolder.textViewDate.setVisibility(View.GONE);
                                userActivityHolder.textViewDate2.setVisibility(View.GONE);

                            } else {
                                if (!isShowTime) {
                                    userActivityHolder.textViewDate.setVisibility(View.VISIBLE);
                                    userActivityHolder.textViewDate2.setVisibility(View.GONE);
                                }
//                            else {
//                                userActivityHolder.textViewDate.setVisibility(View.VISIBLE);
//                                userActivityHolder.textViewDate2.setVisibility(View.GONE);
//                            }
                            }
                        }
//                    if (isShowTime) {
//                        userActivityHolder.textViewDate2.setText(formateDate);
//                    }
//                    else {
                        userActivityHolder.textViewDate.setText(formateDate);
//                    }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    download(userImage, userActivityHolder.imageViewProfile);
                    userActivityHolder.textViewName.setText(userName);

                    String activityTypeText = "";
                    switch (activityType) {
                        case Constants.FAVORITED:
                            activityTypeText = context.getResources().getString(R.string.favorited);
                            if (!isShowTime) {
                                userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                                userActivityHolder.imageViewActivityType.setImageResource(R.drawable.ic_action_circle_star);
                            } else {
                                userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            }
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            break;
                        case Constants.UN_FAVORITED:
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            activityTypeText = context.getResources().getString(R.string.un_favorited);
                            break;
                        case Constants.REQUEST_ACCEPTED:
                            activityTypeText = context.getResources().getString(R.string.lnq_accepted);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            if (!isShowTime) {
                                userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                                userActivityHolder.imageViewActivityType.setImageResource(R.drawable.ic_action_circle_link);
                            } else {
                                userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.LNQ_REQUEST:
                            activityTypeText = context.getResources().getString(R.string.lnq_request_sent_to);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.LNQ_REQUESTED:
                            activityTypeText = context.getResources().getString(R.string.lnq_request);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.UN_LNQ:
                            activityTypeText = context.getResources().getString(R.string.un_lnq);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.NOTE_ADDED:
                            userActivityHolder.textViewTaskDescription.setVisibility(View.VISIBLE);
                            userActivityHolder.textViewTaskDescription.setText(description);
                            activityTypeText = context.getResources().getString(R.string.note_added);
                            if (!isShowTime) {
                                userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                                userActivityHolder.imageViewActivityType.setImageResource(R.drawable.ic_action_task_note_dot);
                            } else {
                                userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.TASK_ADDED:
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.textViewTaskDescription.setText(description);
                            activityTypeText = context.getResources().getString(R.string.task_added);
                            if (!isShowTime) {
                                userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                                userActivityHolder.imageViewActivityType.setImageResource(R.drawable.ic_action_task_note_dot);
                            } else {
                                userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.TASK_COMPLETED:
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.textViewTaskDescription.setText(description);
                            activityTypeText = context.getResources().getString(R.string.task_completed);
                            if (!isShowTime) {
                                userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                                userActivityHolder.imageViewActivityType.setImageResource(R.drawable.ic_action_task_note_dot);
                            } else {
                                userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.NOTE_EDITED:
                            userActivityHolder.textViewTaskDescription.setVisibility(View.VISIBLE);
                            userActivityHolder.textViewTaskDescription.setText(description);
                            activityTypeText = context.getResources().getString(R.string.note_edited);
                            if (!isShowTime) {
                                userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                                userActivityHolder.imageViewActivityType.setImageResource(R.drawable.ic_action_task_note_dot);
                            } else {
                                userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.TASK_EDITED:
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.textViewTaskDescription.setText(description);
                            activityTypeText = context.getResources().getString(R.string.task_edited);
                            if (!isShowTime) {
                                userActivityHolder.imageViewActivityType.setVisibility(View.VISIBLE);
                                userActivityHolder.imageViewActivityType.setImageResource(R.drawable.ic_action_task_note_dot);
                            } else {
                                userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.BLOCKED_USER:
                            activityTypeText = context.getResources().getString(R.string.blocked);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.UN_BLOCKED_USER:
                            activityTypeText = context.getResources().getString(R.string.un_blocked);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.TAGS_EDITED:
                            activityTypeText = context.getResources().getString(R.string.tags_edited);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.INTERESTS_EDITED:
                            activityTypeText = context.getResources().getString(R.string.tags_edited);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.PROFILE_VIEWED:
                            activityTypeText = context.getResources().getString(R.string.profile_viewed);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.LOCATION_HIDDEN:
                            activityTypeText = context.getResources().getString(R.string.you_hid_your_location);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.LOCATION_SHOWN:
                            activityTypeText = context.getResources().getString(R.string.you_show_your_location);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        case Constants.REQUEST_CANCELLED:
                            activityTypeText = context.getResources().getString(R.string.request_cancelled);
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                        default:
                            userActivityHolder.textViewTaskDescription.setVisibility(View.GONE);
                            userActivityHolder.imageViewActivityType.setVisibility(View.GONE);
                            break;
                    }
                    if (activityTypeText.contains("Viewed profile of")) {
                        userActivityHolder.textViewActivityType.setText(activityTypeText + " ");
                    } else
                        userActivityHolder.textViewActivityType.setText(activityTypeText + " - ");
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return activityDataList.size();
    }

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath+"/"+objectKey)){
            Glide.with(context).
                    load(BitmapFactory.decodeFile(cachePath+"/"+objectKey)).
                    apply(new RequestOptions().circleCrop()).
                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }else{
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

    class UserActivityHolder extends RecyclerView.ViewHolder {

        TextView textViewDate, textViewDate2, textViewActivityType, textViewName, textViewTaskDescription, textViewTime;
        ImageView imageViewProfile, imageViewActivityType, imageViewFavoriteBorder;

        UserActivityHolder(@NonNull View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDate2 = itemView.findViewById(R.id.textViewDate2);
            textViewActivityType = itemView.findViewById(R.id.textViewActivityType);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            imageViewFavoriteBorder = itemView.findViewById(R.id.imageViewProfileFavoriteBorder);
            imageViewActivityType = itemView.findViewById(R.id.imageViewActivityType);
            textViewTaskDescription = itemView.findViewById(R.id.textViewTaskDescription);
            textViewTime = itemView.findViewById(R.id.textViewTime);

            fontUtils.setTextViewBoldFont(textViewDate);
            fontUtils.setTextViewBoldFont(textViewDate2);
            fontUtils.setTextViewRegularFont(textViewName);
            fontUtils.setTextViewRegularFont(textViewActivityType);
            fontUtils.setTextViewRegularFont(textViewTime);
            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusActivityClick(getAdapterPosition(), activityDataList.get(getAdapterPosition()).getActivity_type()));
                }
            });
            textViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusActivityClick(getAdapterPosition(), activityDataList.get(getAdapterPosition()).getActivity_type()));

                }
            });

            new SwipeDetector(itemView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
                @Override
                public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum SwipeType) {
                    if (SwipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                        EventBus.getDefault().post(new EventBusActivityGestures(SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT));
                    } else if (SwipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                        EventBus.getDefault().post(new EventBusActivityGestures(SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT));
                    }
                }
            });

        }
    }

}