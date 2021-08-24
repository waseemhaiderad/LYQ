package lnq.com.lnq.fragments.setting.account.blockusers;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.BlockedUsersAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.databinding.FragmentBlockedUsersBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.setting.FragmentSetting;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.blockedusers.GetBlockedUserList;
import lnq.com.lnq.model.gson_converter_models.blockedusers.GetBlockedUsersMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.event_bus_models.EventBusBlockedUsersClicked;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentBlockedUsers extends Fragment {

    //    Android fields....
    private FragmentBlockedUsersBinding mBind;
    private BlockUsersClickHandler blockUsersClickHandler;

    //    Retrofit fields....
    private Call<GetBlockedUsersMainObject> mCallBlockedUsers;
    private Call<RegisterLoginMainObject> mCallBlackWhiteStatus;

    //    Adapter fields....
    private BlockedUsersAdapter blockedUsersAdapter;

    //    Instance fields....
    private List<GetBlockedUserList> getBlockedUserLists = new ArrayList<>();
    String senderProfileId;


    public FragmentBlockedUsers() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_blocked_users, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        OverScrollDecoratorHelper.setUpOverScroll(mBind.nestedScrollView);
    }


    private void init() {
        blockUsersClickHandler = new FragmentBlockedUsers.BlockUsersClickHandler(getActivity());
        mBind.setClickHandler(blockUsersClickHandler);
        EventBus.getDefault().register(this);

//        Setting layout managers of recycler view....
        mBind.mRvBlockedUsers.setLayoutManager(new LinearLayoutManager(getContext()));
//        Setting default item animator of recycler view....
        mBind.mRvBlockedUsers.setItemAnimator(new DefaultItemAnimator());

        senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
        reqContactRequest();
        Glide.with(getContext()).load(R.drawable.qloading).into(mBind.mPb);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mCallBlackWhiteStatus != null && mCallBlackWhiteStatus.isExecuted()) {
            mCallBlackWhiteStatus.cancel();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusBlockedUserClick(EventBusBlockedUsersClicked eventBusBlockedUsersClicked) {
        String blockedUserId = getBlockedUserLists.get(eventBusBlockedUsersClicked.getPosition()).getUser_id();
        String blockedProfileId = getBlockedUserLists.get(eventBusBlockedUsersClicked.getPosition()).getProfile_id();
        reqBlockUnblock(blockedUserId, "unblock", eventBusBlockedUsersClicked.getPosition(), senderProfileId, blockedProfileId);
    }

    private void reqContactRequest() {
        mBind.mPb.setVisibility(View.VISIBLE);
//        mCallBlockedUsers = Api.WEB_SERVICE.getBlockedUsers(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""));
        mCallBlockedUsers = Api.WEB_SERVICE.getBlockedUsers(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), senderProfileId);
        mCallBlockedUsers.enqueue(new Callback<GetBlockedUsersMainObject>() {
            @Override
            public void onResponse(Call<GetBlockedUsersMainObject> call, Response<GetBlockedUsersMainObject> response) {
                mBind.mPb.setVisibility(View.INVISIBLE);
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            getBlockedUserLists = response.body().getListBlockedUsers();
                            blockedUsersAdapter = new BlockedUsersAdapter(getContext(), getBlockedUserLists);
                            mBind.mRvBlockedUsers.setAdapter(blockedUsersAdapter);
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetBlockedUsersMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                mBind.mPb.setVisibility(View.INVISIBLE);
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

    private void reqBlockUnblock(String id, final String status, final int position, String senderProfileid, String recevierProfileid) {
        ((MainActivity) getActivity()).progressDialog.show();
//        mCallBlackWhiteStatus = Api.WEB_SERVICE.blockUnblockLNQ(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), id, status);
        mCallBlackWhiteStatus = Api.WEB_SERVICE.blockUnblockLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), id, status, senderProfileid, recevierProfileid);
        mCallBlackWhiteStatus.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("unblocked_user"));
                            ((MainActivity) getActivity()).showMessageDialog("success", getBlockedUserLists.get(position).getUser_name() + " is removed from your Black list.");
                            getBlockedUserLists.remove(position);
                            blockedUsersAdapter.notifyItemRemoved(position);
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
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

    public class BlockUsersClickHandler {

        private Context context;

        public BlockUsersClickHandler(Context context) {
            this.context = context;
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }
    }


}

