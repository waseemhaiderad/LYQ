package lnq.com.lnq.fragments.profile.editprofile;

import android.content.DialogInterface;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentEditTagsBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusEditTagsPopUp;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.tags.UserTagsMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserData;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.SortingUtils;
import lnq.com.lnq.custom.views.android_tag_view.TagView;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEditTags extends Fragment implements View.OnClickListener {

    private FragmentEditTagsBinding mBind;
    private List<String> tagsList = new ArrayList<>();

    private MultiProfileRepositry multiProfileRepositry;

    private Call<UserTagsMainObject> mCallUserTags;

    private MultiProfileRoomModel currentProfile;

    private boolean isDataLoadedFirstTime;

    public FragmentEditTags() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_tags, container, false);

        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        mBind.mImgBack.setOnClickListener(this);
        mBind.mTvEditTagsHeading.setOnClickListener(this);
        mBind.mBtnSaveChange.setOnClickListener(this);
//        final String tags = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_INTRESTS, "");
        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                if (!isDataLoadedFirstTime) {
                    for (MultiProfileRoomModel data : multiProfileRoomModels) {
                        if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                            currentProfile = data;
                        }
                    }
                    if (!currentProfile.getUser_interests().isEmpty()) {
                        List<String> tagsListData = new ArrayList<>(Arrays.asList(currentProfile.getUser_interests().split(",")));
                        tagsList.addAll(tagsListData);
                        SortingUtils.sortTagsList(tagsListData);
                        if (tagsList.size() > 0) {
                            for (int i = 0; i < tagsList.size(); i++) {
                                mBind.mTagContainer.addTag(tagsList.get(i));
                            }
                        }
                    }
                    isDataLoadedFirstTime = true;
                }
            }
        });

        mBind.mEtEditTags.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    addTagToList();
                    if (tagsList.size() > 0) {
                        reqUserTags(tagsList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", ""), false, "0", currentProfile.getId());
                    }
                    return true;
                }
                return false;
            }
        });
//
//        mBind.mEtEditTags.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 0) {
//                    char last = s.charAt(s.length() - 1);
//                    if (last == ' ') {
//                        addTagToList();
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        mBind.mTagContainer.setOnTagClickListener(new TagView.OnTagClickListener() {

            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(final int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                mBind.mTagContainer.removeTag(position);
                tagsList.remove(position);
            }
        });

        mBind.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), mBind.mRoot);
                return false;
            }
        });
    }

    public void addTagToList() {
        if (getActivity() != null && mBind.mEtEditTags.getText() != null) {
            String tag = mBind.mEtEditTags.getText().toString().trim();
            if (!tag.isEmpty()) {
                if (tag.length() == 1 && tag.equals("#"))
                    return;
                if (!tag.startsWith("#"))
                    tag = "#" + tag;
                if (tagsList.toString().contains(tag)) {
                    Toast.makeText(getActivity(), tag + " tag already exist", Toast.LENGTH_SHORT).show();
                    mBind.mEtEditTags.setText("");
                    return;
                } else {
                    tagsList.add(tag);
                    mBind.mTagContainer.addTag(tag);
                    mBind.mEtEditTags.setText("");
                }
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCallUserTags != null && mCallUserTags.isExecuted()) {
            mCallUserTags.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvEditTagsHeading:
            case R.id.mImgBack:
                ValidUtils.hideKeyboardFromFragment(getContext(), mBind.getRoot());
                if (!mBind.mEtEditTags.getText().toString().isEmpty()) {
                    ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_EDIT_TAGS, true, null);
                } else {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.mBtnSaveChange:
                addTagToList();
                if (tagsList.size() > 0) {
                    reqUserTags(tagsList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", ""), true, "1", currentProfile.getId());
                }
                break;
        }

    }

    private void reqUserTags(String interests, final boolean isBackPressed, String logActivity, String profileId) {
        if (isBackPressed) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(mBind.mRoot);
//            ((MainActivity) getActivity()).progressDialog.show();
        }
//        mCallUserTags = Api.WEB_SERVICE.userTags(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), tags, logActivity);
        mCallUserTags = Api.WEB_SERVICE.userTags(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), interests, logActivity, profileId);
        mCallUserTags.enqueue(new Callback<UserTagsMainObject>() {
            @Override
            public void onResponse(Call<UserTagsMainObject> call, Response<UserTagsMainObject> response) {
                if (getActivity() != null) {
//                    ((MainActivity) getActivity()).progressDialog.dismiss();
                }
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body().getUserInterests() != null) {
                                currentProfile.setUser_interests(response.body().getUserInterests().getUser_interests());
                                multiProfileRepositry.updateTask(currentProfile);
                                EventBus.getDefault().post(new EventBusRefreshUserData());
                                EventBus.getDefault().post(new EventBusUserSession("tags_updated"));
                            }
                            if (isBackPressed) {
                            ((MainActivity) getActivity()).showMessageDialog("success", "interests updated successfully.");
                            getActivity().onBackPressed();
                            }
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", "Could not update tags.");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserTagsMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateTags(EventBusEditTagsPopUp mObj) {
        if (mObj.getType().equalsIgnoreCase("okay")) {
            addTagToList();
            if (tagsList.size() > 0) {
                reqUserTags(tagsList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", ""), true, "1", currentProfile.getId());
            }
            getActivity().onBackPressed();
        } else {
            getActivity().onBackPressed();
        }
    }

}