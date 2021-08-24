package lnq.com.lnq.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lnq.com.lnq.R;
import lnq.com.lnq.model.event_bus_models.EventBusCompleteTask;
import lnq.com.lnq.model.event_bus_models.EventBusEditExistingTask;
import lnq.com.lnq.model.gson_converter_models.tasknote.CreateTaskModel;
import lnq.com.lnq.model.userprofile.SocialMediaLinksModel;
import lnq.com.lnq.model.userprofile.UserTasks;

public class TaskAddedAdapter extends RecyclerView.Adapter<TaskAddedAdapter.TaskAddedHolder> {

    private List<UserTasks> taskAddedListModels = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private OnTaskCheckedChangeListener onTaskCheckedChangeListener;

    public TaskAddedAdapter(Context context, List<UserTasks> taskAddedListModels, OnTaskCheckedChangeListener onTaskCheckedChangeListener) {
        this.taskAddedListModels = taskAddedListModels;
        layoutInflater = LayoutInflater.from(context);
        this.onTaskCheckedChangeListener = onTaskCheckedChangeListener;
    }

    @NonNull
    @Override
    public TaskAddedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_task_added, viewGroup, false);
        return new TaskAddedAdapter.TaskAddedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAddedHolder holder, int position) {
        UserTasks userTasks = taskAddedListModels.get(position);

        String task = userTasks.getTask_description();
        String date = userTasks.getTask_duedate();

        if (date != null) {
            if (date.equals("0000-00-00")) {
                holder.textViewTaskDate.setVisibility(View.GONE);
                holder.textViewTaskDate.setText(date);
            } else {
                holder.textViewTaskDate.setText(date);
            }
        }

        holder.textViewTask.setText(task);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date());

        try {
            if (date.equals("0000-00-00")) {
                holder.textViewTask.setTextColor(Color.BLACK);
            } else {

                Date date2 = sdf.parse(date);
                Date date1 = sdf.parse(currentDateandTime);
                if (date2.equals(date1) || date2.before(date1)) {
                    holder.textViewTask.setTextColor(Color.RED);
                    holder.textViewTaskDate.setTextColor(Color.RED);

                } else {
                    holder.textViewTask.setTextColor(Color.BLACK);
                    holder.textViewTaskDate.setTextColor(Color.BLACK);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.checkBoxTaskAdd.setOnCheckedChangeListener(null);
        holder.checkBoxTaskAdd.setChecked(userTasks.getTask_status().equals("complete"));
        holder.checkBoxTaskAdd.setEnabled(!userTasks.getTask_status().equals("complete"));
        holder.checkBoxTaskAdd.setClickable(!userTasks.getTask_status().equals("complete"));
        holder.textViewTask.setEnabled(!userTasks.getTask_status().equals("complete"));
        holder.checkBoxTaskAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onTaskCheckedChangeListener != null) {
                        onTaskCheckedChangeListener.onCheckChange(position);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskAddedListModels.size();
    }

    public class TaskAddedHolder extends RecyclerView.ViewHolder {

        CheckBox checkBoxTaskAdd;
        TextView textViewTask, textViewTaskDate;

        public TaskAddedHolder(@NonNull View itemView) {
            super(itemView);

            checkBoxTaskAdd = itemView.findViewById(R.id.checkBoxTaskAdd);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewTaskDate = itemView.findViewById(R.id.textViewTaskDate);


            textViewTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusEditExistingTask(getAdapterPosition()));
                }
            });

        }
    }

    public interface OnTaskCheckedChangeListener {
        void onCheckChange(int position);
    }
}
