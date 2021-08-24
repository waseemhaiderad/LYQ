package lnq.com.lnq.fragments.fullprofileview;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentEditNoteBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.tasknote.TaskNoteMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshTaskNotes;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEditNote extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentEditNoteBinding editNoteBinding;
    private String noteId, noteDescription, userId, userFirstName, profileIdBy, profileIdFor;

    //    Api fields...
    private Call<TaskNoteMainObject> callEditNote;
    private Call<TaskNoteMainObject> callCreateNote;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentEditNote() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        editNoteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_note, container, false);
        return editNoteBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        editNoteBinding.editTextNote.requestFocus();
        ((MainActivity) getActivity()).fnShowKeyboardFrom(editNoteBinding.mRoot);
        if (getArguments() != null) {
            noteId = getArguments().getString(Constants.NOTE_ID);
            noteDescription = getArguments().getString(Constants.NOTE_DESCRIPTION);
            userId = getArguments().getString(Constants.USER_ID);
            userFirstName = getArguments().getString(EndpointKeys.FIRST_NAME);
            profileIdFor = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            profileIdBy = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
            if (noteDescription != null) {
                editNoteBinding.editTextNote.setText(noteDescription);
                editNoteBinding.editTextNote.setSelection(editNoteBinding.editTextNote.getText().length());
            }
            if (userFirstName != null) {
                editNoteBinding.textViewEditNoteHeading.setText("Edit your notes - " + userFirstName);
            }
        }

//        Setting custom font....
        setCustomFont();

//        All event listeners....
        editNoteBinding.clearTextViewSave.setOnClickListener(this);
        editNoteBinding.buttonCancel.setOnClickListener(this);
        editNoteBinding.imageViewBack.setOnClickListener(this);

        editNoteBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), editNoteBinding.mRoot);
                return false;
            }
        });

    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewRegularFont(editNoteBinding.textViewEditNoteHeading);
        fontUtils.setTextViewRegularFont(editNoteBinding.clearTextViewSave);
        fontUtils.setButtonRegularFont(editNoteBinding.buttonCancel);
        fontUtils.setEditTextRegularFont(editNoteBinding.editTextNote);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callCreateNote != null && callCreateNote.isExecuted()) {
            callCreateNote.cancel();
        }
        if (callEditNote != null && callEditNote.isExecuted()) {
            callEditNote.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearTextViewSave:
                if (noteId != null) {
                    reqEditNote(noteId, editNoteBinding.editTextNote.getText().toString());
                } else {
                    reqCreateNote(userId, editNoteBinding.editTextNote.getText().toString(), profileIdBy, profileIdFor);
                }
                break;
            case R.id.buttonCancel:
            case R.id.imageViewBack:
                ValidUtils.hideKeyboardFromFragment(getActivity(), editNoteBinding.mRoot);
                getActivity().onBackPressed();
                break;
        }
    }

    private void reqEditNote(final String note_id, String note_des) {
        ((MainActivity) getActivity()).progressDialog.show();
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(editNoteBinding.mRoot);
//        callEditNote = Api.WEB_SERVICE.editNote(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), note_id, note_des);
        callEditNote = Api.WEB_SERVICE.editNote(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), note_id, note_des);
        callEditNote.enqueue(new Callback<TaskNoteMainObject>() {
            @Override
            public void onResponse(Call<TaskNoteMainObject> call, Response<TaskNoteMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusRefreshTaskNotes());
                            EventBus.getDefault().post(new EventBusUserSession("edit_note"));
                            ((MainActivity) getActivity()).showMessageDialog("success", "Notes edited.");
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

    private void reqCreateNote(final String user_id_for, String note_des, String profileIdby, String profileIdfor) {
        ((MainActivity) getActivity()).progressDialog.show();
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(editNoteBinding.mRoot);
//        callCreateNote = Api.WEB_SERVICE.createNote(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), user_id_for, note_des);
        callCreateNote = Api.WEB_SERVICE.createNote(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), user_id_for, note_des, profileIdby, profileIdfor);
        callCreateNote.enqueue(new Callback<TaskNoteMainObject>() {
            @Override
            public void onResponse(Call<TaskNoteMainObject> call, Response<TaskNoteMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusRefreshTaskNotes());
                            EventBus.getDefault().post(new EventBusUserSession("create_note"));
                            ((MainActivity) getActivity()).showMessageDialog("success", "Notes created.");
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

}
