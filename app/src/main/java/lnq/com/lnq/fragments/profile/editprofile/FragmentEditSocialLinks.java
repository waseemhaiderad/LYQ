package lnq.com.lnq.fragments.profile.editprofile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.AbdAllahAbdElFattah13.linkedinsdk.ui.LinkedInUser;
import com.AbdAllahAbdElFattah13.linkedinsdk.ui.linkedin_builder.LinkedInFromActivityBuilder;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ProfileEditContactInfoEmailAdapter;
import lnq.com.lnq.adapters.ProfleSocialLinksAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.databinding.FragmentFragmentEditSocialLinksBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusFacebookData;
import lnq.com.lnq.model.event_bus_models.EventBusLinkedInData;
import lnq.com.lnq.model.event_bus_models.EventBusPhoneVerification;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSocial;
import lnq.com.lnq.model.event_bus_models.EventBusSocialMedia;
import lnq.com.lnq.model.gson_converter_models.EditSocialLinksMainObject;
import lnq.com.lnq.model.userprofile.SocialMediaLinksModel;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEditSocialLinks extends Fragment implements View.OnClickListener {

    private FragmentFragmentEditSocialLinksBinding socialLinksBinding;

    ProfleSocialLinksAdapter adapter;

    private MultiProfileRoomModel currentUserProfileData;

    private MultiProfileRepositry multiProfileRepositry;

    private List<SocialMediaLinksModel> socialList = new ArrayList<>();
    private List<String> linksList = new ArrayList<>();
    private String socialLinks;
    private Call<EditSocialLinksMainObject> callSocialLinks;

    public FragmentEditSocialLinks() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        socialLinksBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fragment_edit_social_links, container, false);
        return socialLinksBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init() {
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        EventBus.getDefault().register(this);
        socialLinksBinding.mImgBack.setOnClickListener(this);
        socialLinksBinding.mTvSocialMediaHeading.setOnClickListener(this);
        socialLinksBinding.mBtnSaveChangeSocialMedia.setOnClickListener(this);
        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentUserProfileData = data;
                        socialLinks = data.getSocial_links();
                        if (!socialLinks.isEmpty()) {
                            linksList.addAll(Arrays.asList(socialLinks.split("\\s*,\\s*")));
                            for (String link : linksList) {
                                socialList.add(new SocialMediaLinksModel(link));
                            }
                        }
                        adapter = new ProfleSocialLinksAdapter(getContext(), socialList, "edit");
                        socialLinksBinding.recyclerViewEditSocialMediaLinks.setLayoutManager(new LinearLayoutManager(getActivity()));
                        socialLinksBinding.recyclerViewEditSocialMediaLinks.setAdapter(adapter);
                    }
                }
            }
        });
//        socialLinks = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_SOCIAL_LINK, "");
//        if (!socialLinks.isEmpty()) {
//            linksList.addAll(Arrays.asList(socialLinks.split("\\s*,\\s*")));
//            for (String link : linksList) {
//                socialList.add(new SocialMediaLinksModel(link));
//            }
//        }

        socialLinksBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), socialLinksBinding.mRoot);
                return false;
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvSocialMediaHeading:
            case R.id.mImgBack:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnSaveChangeSocialMedia:
                String socialLinkName = socialLinksBinding.mEtLinksName.getText().toString();
                if (socialLinkName.equalsIgnoreCase("linkedin")) {
                    LinkedInFromActivityBuilder.getInstance(getActivity())
                            .setClientID("81l1exng0zwf6y")
                            .setClientSecret("9N0wuqRVHdBOCyEB")
                            .setRedirectURI("http://www.leadconcept.net/")
                            .authenticate(123);
                } else if (socialLinkName.equalsIgnoreCase("facebook")) {
                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                } else if (socialLinkName.equalsIgnoreCase("twitter")) {
                    reqSocialLinks("Twitter", currentUserProfileData.getId());
                } else if (socialLinkName.equalsIgnoreCase("instagram")) {
                    reqSocialLinks("Instagram", currentUserProfileData.getId());
                } else {
                    Toast.makeText(getActivity(), "Please enter valid social media name!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void reqSocialLinks(String link, String profileId) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), socialLinksBinding.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callSocialLinks = Api.WEB_SERVICE.socialLinks(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), link, profileId);
        callSocialLinks.enqueue(new Callback<EditSocialLinksMainObject>() {
            @Override
            public void onResponse(Call<EditSocialLinksMainObject> call, Response<EditSocialLinksMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            ((MainActivity) getActivity()).showMessageDialog("success", "SocialLinks added successfully.");
                            socialList.add(new SocialMediaLinksModel(link));
                            linksList.add(link);
                            socialLinks = TextUtils.join(",", linksList);
//                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SOCIAL_LINK, socialLinks).apply();
                            currentUserProfileData.setSocial_links(socialLinks);
                            multiProfileRepositry.updateTask(currentUserProfileData);
                            adapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new EventBusSocialMedia(currentUserProfileData.getSocial_links()));
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("success", "SocialLinks already exist.");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<EditSocialLinksMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
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

    private void reqSocialLinksRemove(String link, int position, String profileId) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), socialLinksBinding.getRoot());
        ((MainActivity) getActivity()).progressDialog.show();
        callSocialLinks = Api.WEB_SERVICE.socialLinksRemove(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), link, profileId);
        callSocialLinks.enqueue(new Callback<EditSocialLinksMainObject>() {
            @Override
            public void onResponse(Call<EditSocialLinksMainObject> call, Response<EditSocialLinksMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    socialList.remove(position);
                    adapter.notifyDataSetChanged();
                    linksList.remove(link);
                    socialLinks = TextUtils.join(",", linksList);
//                    LnqApplication.getInstance().editor.putString(EndpointKeys.USER_SOCIAL_LINK, socialLinks).apply();
                    currentUserProfileData.setSocial_links(socialLinks);
                    multiProfileRepositry.updateTask(currentUserProfileData);
                    ((MainActivity) getActivity()).showMessageDialog("success", "SocialLinks removed successfully.");
                }
            }

            @Override
            public void onFailure(Call<EditSocialLinksMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                EventBus.getDefault().post(new EventBusPhoneVerification("", "", "mFprogress"));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusLinkedInData(EventBusLinkedInData eventBusLinkedInData) {
        String firstName = eventBusLinkedInData.getLinkedInUser().getFirstName();
        String lastName = eventBusLinkedInData.getLinkedInUser().getLastName();
        String userId = eventBusLinkedInData.getLinkedInUser().getId();
        String linkedInFullName = firstName + lastName;
        String lnqName = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FNAME, "") + LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_LNAME, "");
        if (linkedInFullName.replaceAll(" ", "").equalsIgnoreCase(lnqName.replaceAll(" ", ""))) {
            String userName = firstName + "-" + lastName + "-" + userId;
            reqSocialLinks("http://www.linkedin.com/" + userName, currentUserProfileData.getId());
        } else {
            Toast.makeText(getActivity(), "LNQ profile name did not match with Linkedin profile name.", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusFacebookData(EventBusFacebookData eventBusFacebookData) {
        reqSocialLinks(eventBusFacebookData.getLink(), currentUserProfileData.getId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRemoveSocial(EventBusRemoveSocial eventBusRemoveSocial) {
        reqSocialLinksRemove(socialList.get(eventBusRemoveSocial.getPosition()).getSocialMediasLinks(), eventBusRemoveSocial.getPosition(), currentUserProfileData.getId());
    }

}
