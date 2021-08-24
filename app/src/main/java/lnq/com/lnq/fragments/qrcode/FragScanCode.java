package lnq.com.lnq.fragments.qrcode;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusQrCodeScanSuccess;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContactsMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts.ContactList;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.utils.ValidUtils;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragScanCode extends Fragment implements ZBarScannerView.ResultHandler {

    //    Android fields.....
    public ZBarScannerView mScannerView;

    //    Retrofit fields....
    private Call<UpdateLocationMainObject> callRequest;
    private Call<ExportContactsMainObject> callExportContacts;

    //    Instance fields....
    List<ContactList> contactsList = new ArrayList<>();
    String profileId;

    public FragScanCode() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String getContent = rawResult.getContents();
        String[] values = getContent.split("\n");
        String id = "";
        if (values.length > 7) {
            id = values[8];
//            id = id.replace("NOTE:","");
        }

        if (id.contains("LNQ")) {
            profileId = LnqApplication.getInstance().sharedPreferences.getString("selectedProfileId", "");
            if (profileId != null){
                String[] userId = id.split(",");
                reqContactRequest(userId[1], profileId, userId[2]);
            }else {
                profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");
                String[] userId = id.split(",");
                reqContactRequest(userId[1], profileId, userId[2]);
            }
        } else {
//            List<String> phones = new ArrayList<>();
//            phones.add(values[4]);
//            List<String> emails = new ArrayList<>();
//            emails.add(values[5]);
//            contactsList.add(new ContactList(values[2], phones, emails));
//            Contacts contacts = new Contacts(contactsList);
//            Gson gson = new Gson();
//            String jSon = gson.toJson(contacts);
//            reqExportContacts(jSon);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(FragScanCode.this);
            }
        }, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callRequest != null && callRequest.isExecuted()) {
            callRequest.cancel();
        }
    }

    //    Method to save imported phone numbers to server....
    private void reqExportContacts(String json) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callExportContacts = Api.WEB_SERVICE.exportContacts(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), json);
        callExportContacts = Api.WEB_SERVICE.exportContacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), json);
        callExportContacts.enqueue(new Callback<ExportContactsMainObject>() {
            public void onResponse(Call<ExportContactsMainObject> call, Response<ExportContactsMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                switch (response.body().getStatus()) {
                    case 1:
                        ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                        break;
                    case 0:
                        ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                        break;
                }
            }

            @Override
            public void onFailure(Call<ExportContactsMainObject> call, Throwable error) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
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

    private void reqContactRequest(final String userId, String senderProfileId, String reciverProfileId) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId, "qr");
        callRequest = Api.WEB_SERVICE.contactRequest(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), userId,"qr", senderProfileId, reciverProfileId);
        callRequest.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            EventBus.getDefault().post(new EventBusUserSession("lnq_request_sent"));
                            ((MainActivity) getActivity()).showMessageDialog("success", response.body().getMessage());
                            EventBus.getDefault().post(new EventBusQrCodeScanSuccess());
                            break;
                        case 0:
                            if (response.body().getMessage().equalsIgnoreCase("you are already a connection")) {
                                Bundle bundle = new Bundle();
                                bundle.putString(EndpointKeys.USER_ID, userId);
                                bundle.putString(EndpointKeys.PROFILE_ID, reciverProfileId);
                                bundle.putString(Constants.REQUEST_FROM, "qr");
                                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, true, bundle);
                            }
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
}