package lnq.com.lnq.fragments.fullprofileview;


import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.TaskAddedAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentNotesBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusCompleteTask;
import lnq.com.lnq.model.event_bus_models.EventBusEditExistingTask;
import lnq.com.lnq.model.event_bus_models.EventBusNotesUserData;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshTaskNotes;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateTaskHistory;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.tasknote.CreateTaskModel;
import lnq.com.lnq.model.gson_converter_models.tasknote.TaskData;
import lnq.com.lnq.model.userprofile.UserNotes;
import lnq.com.lnq.model.userprofile.UserTasks;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentNotes extends Fragment implements View.OnClickListener, TaskAddedAdapter.OnTaskCheckedChangeListener {

    //    Android fields....
    private FragmentNotesBinding notesBinding;

    private Call<UserTasks> callCompleteV1;

    //    Instance fields....
    private String userId, profileId;
    private List<UserTasks> userTasks = new ArrayList<>();
    private List<UserTasks> userTasksHide = new ArrayList<>();
    private UserNotes userNotes;
    private String userFirstName;
    private TaskAddedAdapter addedAdapter;
    private boolean isShowCompleteTask;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentNotes() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        notesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false);
        return notesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Registering event bus for different triggers....
        EventBus.getDefault().register(this);

        if (getArguments() != null) {
            boolean isLnqUser = getArguments().getBoolean(Constants.IS_LNQ_USER, false);
            profileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            if (!isLnqUser) {
                notesBinding.textViewTask.setVisibility(View.INVISIBLE);
                notesBinding.textViewNotes.setVisibility(View.INVISIBLE);
            }
        }

        changeButtonDrawable(notesBinding.textViewHide, notesBinding.textViewShow);

//        Setting custom font....
        setCustomFont();

//        All event listeners....
        notesBinding.imageViewTaskEdit.setOnClickListener(this);
        notesBinding.imageViewNoteEdit.setOnClickListener(this);
        notesBinding.textViewHide.setOnClickListener(this);
        notesBinding.textViewShow.setOnClickListener(this);

    }

    private void changeButtonDrawable(AppCompatTextView buttonSelected, AppCompatTextView buttonDeselected) {
        buttonSelected.setSelected(true);
        buttonDeselected.setSelected(false);
        buttonSelected.setTextColor(getResources().getColor(R.color.colorWhite));
        buttonDeselected.setTextColor(getResources().getColor(R.color.colorAccentTeenTransparent));
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(notesBinding.textViewTask);
        fontUtils.setTextViewRegularFont(notesBinding.textViewNotes);
        fontUtils.setTextViewRegularFont(notesBinding.textViewNotesDetail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callCompleteV1 != null && callCompleteV1.isExecuted()) {
            callCompleteV1.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewTaskEdit:
                Bundle bundleTask = new Bundle();
                bundleTask.putString(Constants.USER_ID, userId);
                bundleTask.putString(EndpointKeys.PROFILE_ID, profileId);
                bundleTask.putString(EndpointKeys.FIRST_NAME, userFirstName);
                ((MainActivity) getActivity()).fnLoadFragAdd("EDIT TASK", true, bundleTask);
                break;
            case R.id.imageViewNoteEdit:
                if (userNotes.getId() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.NOTE_ID, userNotes.getId());
                    bundle.putString(Constants.NOTE_DESCRIPTION, userNotes.getNote_description());
                    bundle.putString(EndpointKeys.FIRST_NAME, userFirstName);
                    ((MainActivity) getActivity()).fnLoadFragAdd("EDIT NOTE", true, bundle);
                } else {
                    Bundle bundleUserId = new Bundle();
                    bundleUserId.putString(Constants.USER_ID, userId);
                    bundleUserId.putString(EndpointKeys.PROFILE_ID, profileId);
                    bundleUserId.putString(EndpointKeys.FIRST_NAME, userFirstName);
                    ((MainActivity) getActivity()).fnLoadFragAdd("EDIT NOTE", true, bundleUserId);
                }
                break;
            case R.id.textViewHide:
                isShowCompleteTask = false;
                changeButtonDrawable(notesBinding.textViewHide, notesBinding.textViewShow);
                userTasks.clear();
                for (int i = 0; i < userTasksHide.size(); i++) {
                    if (!userTasksHide.get(i).getTask_status().equals("complete")) {
                        userTasks.add(userTasksHide.get(i));
                    }
                }
                addedAdapter.notifyDataSetChanged();
                break;
            case R.id.textViewShow:
                isShowCompleteTask = true;
                changeButtonDrawable(notesBinding.textViewShow, notesBinding.textViewHide);
                userTasks.clear();
                userTasks.addAll(userTasksHide);
                addedAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusNotesOfUser(EventBusNotesUserData eventBusNotesUserData) {
        userTasks.clear();
        userTasksHide.clear();
        userId = eventBusNotesUserData.getUserId();
        userFirstName = eventBusNotesUserData.getUserFirstName();
        notesBinding.textViewBottomSection.setText("This section is not viewable by " + userFirstName);
        userNotes = eventBusNotesUserData.getNotes();
        if (eventBusNotesUserData.getNotes().getNote_description() != null) {
            if (!eventBusNotesUserData.getNotes().getNote_description().isEmpty()) {
                notesBinding.textViewNotesDetail.setText(eventBusNotesUserData.getNotes().getNote_description());
            } else {
                notesBinding.textViewNotesDetail.setText("You have not added any note");
            }
        } else {
            notesBinding.textViewNotesDetail.setText("You have not added any note");
        }
        userTasks.addAll(eventBusNotesUserData.getTasks());
        userTasksHide.addAll(eventBusNotesUserData.getTasks());
        if (eventBusNotesUserData.getTasks() != null && eventBusNotesUserData.getTasks().size() > 0) {
            if (eventBusNotesUserData.getTasks().get(0) != null) {
                if (userTasksHide.size() > 0) {
                    addedAdapter = new TaskAddedAdapter(getContext(), userTasks, this);
                    notesBinding.recyclerViewTaskAdded.setLayoutManager(new LinearLayoutManager(getActivity()));
                    notesBinding.recyclerViewTaskAdded.setAdapter(addedAdapter);
                    userTasks.clear();
                    for (int i = 0; i < userTasksHide.size(); i++) {
                        if (!userTasksHide.get(i).getTask_status().equals("complete")) {
                            userTasks.add(userTasksHide.get(i));
                        }
                    }
                    notesBinding.textViewNoTask.setVisibility(View.GONE);
                    notesBinding.textViewCompletedTasksDes.setVisibility(View.VISIBLE);
                    notesBinding.linearLayoutGridMapButtons.setVisibility(View.VISIBLE);
                } else {
                    notesBinding.textViewNoTask.setVisibility(View.VISIBLE);
                    notesBinding.textViewNoTask.setText("You have not added any task");
                    notesBinding.textViewCompletedTasksDes.setVisibility(View.GONE);
                    notesBinding.linearLayoutGridMapButtons.setVisibility(View.GONE);
                }
            } else {
                notesBinding.textViewNoTask.setVisibility(View.VISIBLE);
                notesBinding.textViewNoTask.setText("You have not added any task");
                notesBinding.textViewCompletedTasksDes.setVisibility(View.GONE);
                notesBinding.linearLayoutGridMapButtons.setVisibility(View.GONE);
            }
        } else {
            notesBinding.textViewNoTask.setVisibility(View.VISIBLE);
            notesBinding.textViewNoTask.setText("You have not added any task");
            notesBinding.textViewCompletedTasksDes.setVisibility(View.GONE);
            notesBinding.linearLayoutGridMapButtons.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusEditExistingTask(EventBusEditExistingTask eventBusEditExistingTask) {
        Bundle bundleTask = new Bundle();
        bundleTask.putString(Constants.USER_ID, userId);
        bundleTask.putString(Constants.TASK_ID, userTasks.get(eventBusEditExistingTask.getPos()).getId());
        bundleTask.putString(EndpointKeys.FIRST_NAME, userFirstName);
        bundleTask.putString(Constants.TASK_DES, userTasks.get(eventBusEditExistingTask.getPos()).getTask_description());
        bundleTask.putString(Constants.TASK_DUEDATE, userTasks.get(eventBusEditExistingTask.getPos()).getTask_duedate());
        ((MainActivity) getActivity()).fnLoadFragAdd("EDIT TASK", true, bundleTask);
    }

    private void reqCompleteTaskV1(String task_id, int index) {
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(notesBinding.getRoot());
        callCompleteV1 = Api.WEB_SERVICE.completeTaskV1(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), task_id);
        callCompleteV1.enqueue(new Callback<UserTasks>() {
            @Override
            public void onResponse(Call<UserTasks> call, Response<UserTasks> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", "Error");
                            break;
                        case 1:
                            if (userTasks.size() > 0) {
                                for (int i = 0; i < userTasksHide.size(); i++) {
                                    if (userTasksHide.get(i).getId().equalsIgnoreCase(userTasks.get(index).getId())) {
                                        userTasksHide.get(i).setTask_status("complete");
                                        break;
                                    }
                                }
                                if (isShowCompleteTask) {
                                    userTasks.get(index).setTask_status("complete");
                                    addedAdapter.notifyItemChanged(index);
                                } else {
                                    userTasks.remove(index);
                                    addedAdapter.notifyItemRemoved(index);
                                }

                                if (userTasks.size() == 0) {
                                    notesBinding.textViewNoTask.setVisibility(View.VISIBLE);
//                                    notesBinding.textViewNoTask.setText("You have not added any task");
                                }
                            } else {
                                notesBinding.textViewNoTask.setVisibility(View.VISIBLE);
                                notesBinding.textViewNoTask.setText("You do not have any uncompleted task");
                            }
                            EventBus.getDefault().post(new EventBusUpdateTaskHistory());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserTasks> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    @Override
    public void onCheckChange(int position) {
        String taskID = userTasks.get(position).getId();
        reqCompleteTaskV1(taskID, position);
    }
}