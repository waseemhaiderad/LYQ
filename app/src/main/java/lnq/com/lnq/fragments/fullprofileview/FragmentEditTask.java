package lnq.com.lnq.fragments.fullprofileview;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.date_picker_dialog.SingleDateAndTimePicker;
import lnq.com.lnq.custom.views.date_picker_dialog.dialog.SingleDateAndTimePickerDialog;
import lnq.com.lnq.databinding.FragmentEditTaskBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.tasknote.CreateTaskModel;
import lnq.com.lnq.model.gson_converter_models.tasknote.TaskNoteMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshTaskNotes;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEditTask extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentEditTaskBinding editTaskBinding;
    private SingleDateAndTimePickerDialog.Builder singleBuilder;
    private String taskId, taskDescription, userId, userFirstName, taskDueDate, profileIdBy, profileIdFor;
    private String dueDateForServer = "";

    //    Api fields...
    private Call<TaskNoteMainObject> callEditTask;
    //    private Call<TaskNoteMainObject> callCreateTask;
    private Call<CreateTaskModel> callCreateTaskV1;

    //    Font fields....
    private FontUtils fontUtils;
//    private boolean backSpace;
//    private int previousLength;

    public FragmentEditTask() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        editTaskBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_task, container, false);
        return editTaskBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        editTaskBinding.mEtTaskDay.setFocusable(false);
        editTaskBinding.mEtTaskDay.setClickable(true);
        editTaskBinding.mEtTaskDay.setOnClickListener(this);

        editTaskBinding.editTextTask.requestFocus();
        ((MainActivity) getActivity()).fnShowKeyboardFrom(editTaskBinding.mRoot);
        if (getArguments() != null) {
            taskId = getArguments().getString(Constants.TASK_ID);
            taskDescription = getArguments().getString(Constants.TASK_DES);
            userId = getArguments().getString(Constants.USER_ID);
            userFirstName = getArguments().getString(EndpointKeys.FIRST_NAME);
            taskDueDate = getArguments().getString(Constants.TASK_DUEDATE);
            profileIdFor = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            profileIdBy = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

            if (taskDescription != null) {
                editTaskBinding.editTextTask.setText(taskDescription);
                editTaskBinding.editTextTask.setSelection(editTaskBinding.editTextTask.getText().length());
                editTaskBinding.textViewEditTaskHeading.setText("Edit your tasks - " + userFirstName);
                editTaskBinding.clearTextViewSave.setText("Save");
            } else {
                editTaskBinding.textViewEditTaskHeading.setText("Create your tasks - " + userFirstName);
            }
            if (taskDueDate != null) {
                if (taskDueDate.contains("0000-00-00")) {
                    editTaskBinding.mEtTaskDay.setText("");
                } else {
                    editTaskBinding.mEtTaskDay.setText(taskDueDate);
                }
            }
        }

//        Setting custom font....
        setCustomFont();

//        All event listeners....
        editTaskBinding.clearTextViewSave.setOnClickListener(this);
        editTaskBinding.buttonCancel.setOnClickListener(this);
        editTaskBinding.imageViewBack.setOnClickListener(this);

        editTaskBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), editTaskBinding.mRoot);
                return false;
            }
        });

        editTaskBinding.mEtTaskDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editTaskBinding.mEtTaskDay.getText().toString().isEmpty()) {
                    editTaskBinding.mTvTaskDay.setVisibility(View.VISIBLE);
                } else {
                    editTaskBinding.mTvTaskDay.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                backSpace = previousLength > s.length();
//                if (backSpace) {
//                    editProfileBinding.mEtBirthDay.setText(new SimpleDateFormat("MM dd, yyyy", Locale.getDefault()).format(date));
//                }

            }
        });
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(editTaskBinding.textViewEditTaskHeading);
        fontUtils.setTextViewRegularFont(editTaskBinding.clearTextViewSave);
        fontUtils.setButtonRegularFont(editTaskBinding.buttonCancel);
        fontUtils.setEditTextRegularFont(editTaskBinding.editTextTask);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearTextViewSave:
                ValidUtils.hideKeyboardFromFragment(getActivity(), editTaskBinding.mRoot);
                String taskDescription = editTaskBinding.editTextTask.getText().toString();
                if (!TextUtils.isEmpty(taskDescription)) {
                    if (taskId != null) {
                        reqEditTask(taskId, taskDescription, dueDateForServer);
                    } else {
                        reqCreateTaskV1(userId, taskDescription, dueDateForServer, "Pending", profileIdBy, profileIdFor);
                    }
                } else {
                    ((MainActivity) getActivity()).showMessageDialog("error", "Task cannot be empty.");
                }
                break;
            case R.id.buttonCancel:
            case R.id.imageViewBack:
                ValidUtils.hideKeyboardFromFragment(getActivity(), editTaskBinding.mRoot);
                getActivity().onBackPressed();
                break;
            case R.id.mEtTaskDay:
                ((MainActivity) getActivity()).fnHideKeyboardForcefully(editTaskBinding.getRoot());
                try {
                    singleBuilder = new SingleDateAndTimePickerDialog.Builder(getActivity())
                            .bottomSheet()
                            .curved()
                            .displayYears(true)
                            .displayMonth(true)
                            .displayDaysOfMonth(true)
                            .mustBeOnFuture()
                            .defaultDate(Calendar.getInstance().getTime())
                            .todayText("Today")
                            .mainColor(getResources().getColor(R.color.colorPrimaryBlue))
                            .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                                @Override
                                public void onDisplayed(SingleDateAndTimePicker picker) {

                                }
                            })
                            .listener(new SingleDateAndTimePickerDialog.Listener() {
                                @Override
                                public void onDateSelected(Date date) {
                                    dueDateForServer = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
                                    editTaskBinding.mEtTaskDay.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
                                }
                            });
                    singleBuilder.display();
                } catch (Exception e) {

                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callCreateTaskV1 != null && callCreateTaskV1.isExecuted()) {
            callCreateTaskV1.cancel();
        }
//        if (callCreateTask != null && callCreateTask.isExecuted()) {
//            callCreateTask.cancel();
//        }
        if (callEditTask != null && callEditTask.isExecuted()) {
            callEditTask.cancel();
        }
    }

    private void reqEditTask(String task_id, String task_des, String dueDate) {
        ValidUtils.hideKeyboardFromFragment(getContext(), editTaskBinding.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(editTaskBinding.mRoot);
//        callEditTask = Api.WEB_SERVICE.editTask(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), task_id, task_des);
        callEditTask = Api.WEB_SERVICE.editTask(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), task_id, task_des, dueDate);
        callEditTask.enqueue(new Callback<TaskNoteMainObject>() {
            @Override
            public void onResponse(Call<TaskNoteMainObject> call, Response<TaskNoteMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusRefreshTaskNotes());
                            EventBus.getDefault().post(new EventBusUserSession("edit_task"));
                            ((MainActivity) getActivity()).showMessageDialog("success", "Task edited.");
                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<TaskNoteMainObject> call, Throwable error) {
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

    /*private void reqCreateTask(String user_id_for, String task_des) {
        ((MainActivity) getActivity()).progressDialog.show();
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(editTaskBinding.mRoot);
//        callCreateTask = Api.WEB_SERVICE.createTask(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), user_id_for, task_des);
        callCreateTask = Api.WEB_SERVICE.createTask(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), user_id_for, task_des);
        callCreateTask.enqueue(new Callback<TaskNoteMainObject>() {
            @Override
            public void onResponse(Call<TaskNoteMainObject> call, Response<TaskNoteMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                        case 1:
                            EventBus.getDefault().post(new EventBusRefreshTaskNotes());
                            EventBus.getDefault().post(new EventBusUserSession("create_task"));
                            ((MainActivity) getActivity()).showMessageDialog("success", "Task created.");
                            getActivity().onBackPressed();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<TaskNoteMainObject> call, Throwable error) {
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
    }*/

    private void reqCreateTaskV1(String user_id_for, String task_des, String task_duedate, String task_status, String profileIdby, String profileIdfor) {
        ((MainActivity) getActivity()).progressDialog.show();
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(editTaskBinding.mRoot);
        callCreateTaskV1 = Api.WEB_SERVICE.createTaskV1(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), user_id_for, task_des, task_duedate, task_status, profileIdby, profileIdfor);
        callCreateTaskV1.enqueue(new Callback<CreateTaskModel>() {
            @Override
            public void onResponse(Call<CreateTaskModel> call, Response<CreateTaskModel> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 0:
                            EventBus.getDefault().post(new EventBusRefreshTaskNotes());
                            EventBus.getDefault().post(new EventBusUserSession("create_task"));
                            ((MainActivity) getActivity()).showMessageDialog("success", "Task created.");
                            getActivity().onBackPressed();
                            break;
                        case 1:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateTaskModel> call, Throwable error) {
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

}