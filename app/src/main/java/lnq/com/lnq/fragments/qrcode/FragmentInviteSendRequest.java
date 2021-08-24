package lnq.com.lnq.fragments.qrcode;


import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mancj.slideup.SlideUp;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentInviteSendRequestBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.model.gson_converter_models.qr_code.GetUserProfile;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class FragmentInviteSendRequest extends Fragment implements View.OnClickListener {

    FragmentInviteSendRequestBinding sendRequestBinding;

    private TransferUtility transferUtility;

    GetUserProfile getUserProfile;
    String senderProfileId, receiverProfileId;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callRequest;
    private AppCompatImageView imageViewBackTopBar, imageViewSearchTopBar, imageViewDropdownContacts;
    private SlideUp slideUp;
    CardView topBarLayout;
    TextView textViewHeading;

    public FragmentInviteSendRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sendRequestBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite_send_request, container, false);
        return sendRequestBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransferUtility();
        init();
        topBarLayout = sendRequestBinding.topBarContact.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewSearchTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.add_connection);
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

    private void init() {
        senderProfileId = LnqApplication.getInstance().sharedPreferences.getString("selectedProfileId", "");
        Bundle bundle = getArguments();
        if (bundle != null) {

            getUserProfile = bundle.getParcelable("UserData");
            sendRequestBinding.textViewFullName.setText(getUserProfile.getUserFname() + " " + getUserProfile.getUserLname());
//            sendRequestBinding.textViewFirstName.setText("Would you like to send LNQ request to " + getUserProfile.getUserFname() + " now?");
            sendRequestBinding.textViewCompanyName.setText(getUserProfile.getUserCompany());
            sendRequestBinding.textViewStatusMessage.setText(getUserProfile.getUserStatusMsg());
            sendRequestBinding.textViewJobTitle.setText(getUserProfile.getUserCurrentPosition() + " -");
            sendRequestBinding.textViewCurrentLocation.setText(getUserProfile.getLocationName());
            sendRequestBinding.textViewHomeLocation.setText(getUserProfile.getUserAddress());
            receiverProfileId = getArguments().getString(EndpointKeys.PROFILE_ID, "");
            if (getUserProfile.getUserAvatar() != null && !getUserProfile.getUserAvatar().isEmpty()) {
                download(getUserProfile.getUserAvatar(), sendRequestBinding.imageViewUser);
            }
        }

        sendRequestBinding.imageViewBack.setOnClickListener(this);
        sendRequestBinding.buttonDontSend.setOnClickListener(this);
        sendRequestBinding.buttonsendLNQ.setOnClickListener(this);
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity().getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, getActivity().getApplicationContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
            case R.id.buttonDontSend:
                getActivity().onBackPressed();
                break;
            case R.id.buttonsendLNQ:
                if (!((MainActivity) getActivity()).fnIsisOnline()) {
                    LnqApplication.getInstance().snakeBar(sendRequestBinding.getRoot(), getResources().getString(R.string.wifi_internet_not_connected), getResources().getColor(R.color.colorError));
                    return;
                }
                if (getUserProfile != null) {
                    if (getUserProfile.getId() == null || getUserProfile.getId().equals(""))
                        return;
                    reqContactRequest(getUserProfile.getId(), senderProfileId, receiverProfileId);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callRequest != null && callRequest.isExecuted()) {
            callRequest.cancel();
        }
    }

    private void reqContactRequest(final String userId, String senderProfileid, String recevierProfileId) {
        ((MainActivity) getActivity()).progressDialog.show();
        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, "map", senderProfileid, recevierProfileId);
        callRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", "Request sent to " + userName);
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_sent"));
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(userId, "contacted", false));
                            getActivity().onBackPressed();
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
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

    void download(String objectKey, ImageView imageView) {
        final File fileDownload = new File(getActivity().getCacheDir(), objectKey);

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
                    Glide.with(getActivity()).
                            load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
                            circleCrop().
                            into(imageView);
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