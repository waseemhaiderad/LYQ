package lnq.com.lnq.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lnq.com.lnq.R;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.model.event_bus_models.EventBusProfileSubPageClicked;
import lnq.com.lnq.model.userprofile.History;
import lnq.com.lnq.utils.DateUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.SortingUtils;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private Context context;
    private FontUtils fontUtils;
    private List<History> historyList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private String name;

    public HistoryAdapter(Context context, List<History> historyList, String name) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.historyList = historyList;
        this.name = name;
        fontUtils = FontUtils.getFontUtils(context);
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.row_history, viewGroup, false);
        return new HistoryHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull HistoryHolder historyHolder, int i) {
        History history = historyList.get(i);

        String historyType = history.getHistory_type();
        String historyDate = history.getHistory_date();
        String historyTime = history.getHistory_time();
        String historyDescription = history.getDescription();
        String historyTaskCompleted = history.getHistory_taskCompleted();

        if (historyTime != null)
            try {
                Calendar timeNeeded = Calendar.getInstance();
                timeNeeded.setTimeInMillis(new SimpleDateFormat("hh:mm:ss a").parse(history.getHistory_time()).getTime());
                historyTime = "" + DateFormat.format("HH:mm a", timeNeeded);
                historyTime = DateUtils.getLocalConvertedTime(historyTime);
                historyTime = SortingUtils.formateDate(historyTime);
            } catch (ParseException e) {

            }

        String formateDate = "";
        String lastFormateDate = "";
        try {
            Date date = new SimpleDateFormat("MMMM dd,yyyy", Locale.US).parse(historyDate);
            formateDate = DateUtils.getMyPrettyDate(date.getTime());
        } catch (ParseException e) {

        }
        if (i == 0) {
            historyHolder.textViewDate.setVisibility(View.VISIBLE);
        } else {
            try {
                Date lastDate = new SimpleDateFormat("MMMM dd,yyyy", Locale.US).parse(historyList.get(i - 1).getHistory_date());
                lastFormateDate = DateUtils.getMyPrettyDate(lastDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (formateDate.equals(lastFormateDate)) {
                historyHolder.textViewDate.setVisibility(View.GONE);
            } else {
                historyHolder.textViewDate.setVisibility(View.VISIBLE);
            }
        }
        if (historyDescription.length() > 19) {
            historyDescription = historyDescription.substring(0, 19);
        }
        historyHolder.textViewDate.setText(formateDate);
        historyHolder.textViewTime.setText(historyTime);
        if (historyType.equals("request_accepted")) {
            historyHolder.textViewDescription.setText("You accepted LNQ request from " + name);

        } else {
            historyHolder.textViewDescription.setText(historyDescription);
        }
        String historyTypeText = "";
        int resourceId = 0;
        switch (historyType) {
            case Constants.FAVORITED:
                historyTypeText = context.getResources().getString(R.string.favorited);
                resourceId = R.drawable.icon_star_fill;
                break;
            case Constants.UN_FAVORITED:
                historyTypeText = context.getResources().getString(R.string.un_favorited);
                resourceId = R.drawable.icon_star_stroke;
                break;
            case Constants.REQUEST_ACCEPTED:
                historyTypeText = context.getResources().getString(R.string.lnq_accepted);
                resourceId = R.drawable.icon_link_green;
                break;
            case Constants.LNQ_REQUEST:
                historyTypeText = context.getResources().getString(R.string.lnq_request);
                resourceId = R.drawable.pin_blue;
                break;
            case Constants.UN_LNQ:
                historyTypeText = context.getResources().getString(R.string.un_lnq);
                resourceId = -1;
                break;
            case Constants.NOTE_ADDED:
                historyTypeText = context.getResources().getString(R.string.note_added);
                historyHolder.textViewDescription.setVisibility(View.VISIBLE);
                resourceId = R.drawable.ic_action_task_note_dot;
                break;
            case Constants.TASK_ADDED:
                historyTypeText = context.getResources().getString(R.string.task_added);
                historyHolder.textViewDescription.setVisibility(View.GONE);
                resourceId = R.drawable.ic_action_task_note_dot;
                break;
            case Constants.NOTE_EDITED:
                historyTypeText = context.getResources().getString(R.string.note_edited);
                historyHolder.textViewDescription.setVisibility(View.VISIBLE);
                resourceId = R.drawable.ic_action_task_note_dot;
                break;
            case Constants.TASK_EDITED:
                historyTypeText = context.getResources().getString(R.string.task_edited);
                historyHolder.textViewDescription.setVisibility(View.GONE);
                resourceId = R.drawable.ic_action_task_note_dot;
                break;
            case Constants.BLOCKED_USER:
                historyTypeText = context.getResources().getString(R.string.blocked);
                resourceId = -1;
                break;
            case Constants.UN_BLOCKED_USER:
                historyTypeText = context.getResources().getString(R.string.un_blocked);
                resourceId = -1;
                break;
            case Constants.LOCATION_HIDDEN:
                historyTypeText = "Hide Location";
                resourceId = R.drawable.ic_action_hide_location;
                break;
            case Constants.LOCATION_SHOWN:
                historyTypeText = "Show Location";
                resourceId = R.drawable.show_location;
                break;
            case Constants.TASK_COMPLETED:
                historyTypeText = context.getResources().getString(R.string.task_completed);
                historyHolder.textViewDescription.setVisibility(View.GONE);
                resourceId = R.drawable.ic_action_task_note_dot;
                break;

        }
        historyHolder.textViewHistoryType.setText(historyTypeText);
        if (resourceId != -1) {
            historyHolder.imageViewHistoryType.setVisibility(View.VISIBLE);
            historyHolder.imageViewHistoryType.setImageResource(resourceId);
        } else {
            historyHolder.imageViewHistoryType.setVisibility(View.INVISIBLE);
//            historyHolder.imageViewHistoryType.setImageResource(-1);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {

        TextView textViewDate, textViewTime, textViewHistoryType, textViewDescription;
        ImageView imageViewHistoryType;

        HistoryHolder(@NonNull View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.textViewDateHistoryRow);
            textViewTime = itemView.findViewById(R.id.textViewTimeHistoryRow);
            textViewHistoryType = itemView.findViewById(R.id.textViewHistoryTypeHistoryRow);
            imageViewHistoryType = itemView.findViewById(R.id.imageViewHistoryTypeHistoryRow);
            textViewDescription = itemView.findViewById(R.id.textViewTaskDescription);

            fontUtils.setTextViewRegularFont(textViewDate);
            fontUtils.setTextViewRegularFont(textViewTime);
            fontUtils.setTextViewRegularFont(textViewDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusProfileSubPageClicked(0));
                }
            });
        }
    }
}