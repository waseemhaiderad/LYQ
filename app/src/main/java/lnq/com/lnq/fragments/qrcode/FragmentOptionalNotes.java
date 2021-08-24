package lnq.com.lnq.fragments.qrcode;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.databinding.FragmentOptionalNotesBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusSendReceipetInfo;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentOptionalNotes extends Fragment implements View.OnClickListener {

    private String firstName, lastName, phone, email;
    private FragmentOptionalNotesBinding optionalNotesBinding;

    private Call<RegisterLoginMainObject> callReferAccount;
    private AppCompatImageView imageViewBackTopBar, imageViewSearchTopBar, imageViewDropdownContacts;
    CardView topBarLayout;
    TextView textViewHeading;

    public FragmentOptionalNotes() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        optionalNotesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_optional_notes, container, false);
        return optionalNotesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
        topBarLayout = optionalNotesBinding.topBarContact.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewSearchTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.add_private_notes);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewSearchTopBar.setVisibility(View.GONE);
        imageViewDropdownContacts.setVisibility(View.GONE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        EventBus.getDefault().register(this);
        optionalNotesBinding.buttonSendMyInfo.setOnClickListener(this);

        optionalNotesBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), optionalNotesBinding.mRoot);
                return false;
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetReciptInfo(EventBusSendReceipetInfo eventBusSendReceipetInfo) {
        firstName = eventBusSendReceipetInfo.getFirstName();
        lastName = eventBusSendReceipetInfo.getLastName();
        phone = eventBusSendReceipetInfo.getPhone();
        email = eventBusSendReceipetInfo.getEmail();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSendMyInfo:
                optionalNotesBinding.editTextTasks.getText().toString();
                referLNQ("", optionalNotesBinding.editTextNotes.getText().toString(), optionalNotesBinding.editTextTasks.getText().toString());
                break;
        }
    }


    private void referLNQ(String referral_context, String referral_notes, String referral_tasks) {
        ((MainActivity) getActivity()).progressDialog.show();
        callReferAccount = Api.WEB_SERVICE.referLNQ(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""),
                LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")),
                LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""),
                firstName, lastName, email, phone, referral_context, referral_notes, referral_tasks);
        callReferAccount.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("refer_lnq"));
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
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
}
