package lnq.com.lnq.fragments.profile.createmultipleprofiles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.date_picker_dialog.SingleDateAndTimePicker;
import lnq.com.lnq.custom.views.date_picker_dialog.dialog.SingleDateAndTimePickerDialog;
import lnq.com.lnq.databinding.FragmentCreateProfileInfoBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.profile.editprofile.FragmentEditProfile;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentCreateProfile;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserData;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserSecondaryProfileData;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateMultipleProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserSecondaryProfile;
import lnq.com.lnq.model.gson_converter_models.search_city_zip.SearchCityZipData;
import lnq.com.lnq.model.gson_converter_models.search_city_zip.SearchCityZipObject;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentCreateProfileInfo extends Fragment implements View.OnClickListener {

    private FragmentCreateProfileInfoBinding fragmentCreateProfileInfoBinding;
    private SingleDateAndTimePickerDialog.Builder singleBuilder;

    //    Retrofit fields....
    private Call<SearchCityZipObject> mCallSearchCity;
    private Call<CreateMultipleProfileMainObject> mCallCreateProfile;

    //    Instance fields....
    long delay = 1500;
    long last_text_edit = 0;
    Handler handler = new Handler();
    private List<SearchCityZipData> mLstSearchData;
    private List<SearchCityZipData> mLstSearchDataTemp;

    private int previousLength;
    private boolean backSpace;
    Animation animShake;
    String userBirthday, firstName, lastName, userAddress, userGender;
    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentUserProfileData;
    private AppCompatImageView imageViewBackTopBar;
    String[] cityNames;

    public FragmentCreateProfileInfo() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCreateProfileInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_profile_info, container, false);
        return fragmentCreateProfileInfoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        CardView topBarLayout = fragmentCreateProfileInfoBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.profile_info);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        textViewHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void init() {

        multiProfileRepositry = new MultiProfileRepositry(getContext());

        fragmentCreateProfileInfoBinding.mBtnSaveChange.setOnClickListener(this);

        fragmentCreateProfileInfoBinding.mEtCeo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fragmentCreateProfileInfoBinding.mEtCeo.getText().toString().isEmpty()) {
//                    fragmentCreateProfileInfoBinding.mTvCeoTitle.setVisibility(View.VISIBLE);
                } else {
//                    fragmentCreateProfileInfoBinding.mTvCeoTitle.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fragmentCreateProfileInfoBinding.mEtComapny.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fragmentCreateProfileInfoBinding.mEtComapny.getText().toString().isEmpty()) {
//                    fragmentCreateProfileInfoBinding.mTvCompany.setVisibility(View.VISIBLE);
                } else {
//                    fragmentCreateProfileInfoBinding.mTvCompany.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fragmentCreateProfileInfoBinding.mEtHomeBase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (fragmentCreateProfileInfoBinding.mEtHomeBase.getText().toString().isEmpty()) {
//                    fragmentCreateProfileInfoBinding.mTvHomeBase.setVisibility(View.VISIBLE);
//                } else {
//                    fragmentCreateProfileInfoBinding.mTvHomeBase.setVisibility(View.GONE);
//                }
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fnIsUserTyping(s.toString());
            }
        });

        fragmentCreateProfileInfoBinding.mEtHomeBase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                fragmentCreateProfileInfoBinding.mEtHomeBase.setText(mLstSearchData.get(position).getCity() + ", " + mLstSearchData.get(position).getState() + ", " + "US");
                fragmentCreateProfileInfoBinding.mEtHomeBase.setText(cityNames[position] + ", " + "US");
            }
        });

        fragmentCreateProfileInfoBinding.mEtNicKName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (fragmentCreateProfileInfoBinding.mEtNicKName.getText().toString().isEmpty()) {
//                    fragmentCreateProfileInfoBinding.mTvNicKName.setVisibility(View.VISIBLE);
                } else {
//                    fragmentCreateProfileInfoBinding.mTvNicKName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                backSpace = previousLength > s.length();
            }
        });
        firstName = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FNAME, "");
        lastName = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_LNAME, "");
        userBirthday = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_BIRTHDAY, "");

        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentUserProfileData = data;
                        fragmentCreateProfileInfoBinding.mEtHomeBase.setText(data.getUser_address());

                        String genderText = data.getUser_gender();
                        ArrayAdapter adapter = (ArrayAdapter) fragmentCreateProfileInfoBinding.mSpinnerGender.getAdapter();
                        int spinnerPosition = 0;
                        if (genderText.equalsIgnoreCase("male") || genderText.equalsIgnoreCase("female")) {
                            spinnerPosition = adapter.getPosition(genderText);
                        } else {
                            spinnerPosition = 2;
                            fragmentCreateProfileInfoBinding.mETGender.setText(genderText);
                        }
                        fragmentCreateProfileInfoBinding.mSpinnerGender.setSelection(spinnerPosition);
                    }
                }
            }
        });

        fragmentCreateProfileInfoBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), fragmentCreateProfileInfoBinding.mRoot);
                return false;
            }
        });

        fragmentCreateProfileInfoBinding.mSpinnerGender.setOnItemSelectedListener(new SpinnerSelectedListenerEdit(fragmentCreateProfileInfoBinding.mSpinnerGender));
    }

    class SpinnerSelectedListenerEdit implements AdapterView.OnItemSelectedListener {

        Spinner spinner;

        SpinnerSelectedListenerEdit(Spinner spinner) {
            this.spinner = spinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spinner.setSelection(i);
            if (i == 2) {
                fragmentCreateProfileInfoBinding.mETGender.setVisibility(View.VISIBLE);
            } else {
                fragmentCreateProfileInfoBinding.mETGender.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                String city = fragmentCreateProfileInfoBinding.mEtHomeBase.getText().toString();
//                if (city.contains(",")) {
//                    city = city.substring(0, city.indexOf(","));
//                }
                reqSearchState(city);
            }
        }
    };

    private void fnIsUserTyping(String text) {
        if (text.isEmpty()) {
            return;
        }
        if (text.length() > 0) {
            if (mCallSearchCity != null && mCallSearchCity.isExecuted()) {
                mCallSearchCity.cancel();
            }
            last_text_edit = System.currentTimeMillis();
            handler.postDelayed(input_finish_checker, delay);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCallSearchCity != null && mCallSearchCity.isExecuted()) {
            mCallSearchCity.cancel();
        }
        if (singleBuilder != null)
            singleBuilder.dismiss();
        if (mCallCreateProfile != null && mCallCreateProfile.isExecuted()) {
            mCallCreateProfile.cancel();
        }
    }

    Date currentDate = new Date(System.currentTimeMillis());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnSaveChange:
                if (!((MainActivity) getActivity()).fnIsisOnline()) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
                    return;
                }
                String gender = fragmentCreateProfileInfoBinding.mSpinnerGender.getSelectedItemPosition() == 2 ? fragmentCreateProfileInfoBinding.mETGender.getText().toString()
                        : fragmentCreateProfileInfoBinding.mSpinnerGender.getSelectedItem().toString();
                if (fragmentCreateProfileInfoBinding.mEtHomeBase.getText().toString().trim().isEmpty()) {
                    fragmentCreateProfileInfoBinding.mEtHomeBase.startAnimation(animShake);
                } else if (gender.trim().isEmpty()) {
                    fragmentCreateProfileInfoBinding.mETGender.startAnimation(animShake);
                } else {
                    reqCreateProfile(firstName,
                            lastName,
                            fragmentCreateProfileInfoBinding.mEtNicKName.getText().toString(),
                            fragmentCreateProfileInfoBinding.mEtHomeBase.getText().toString(),
                            fragmentCreateProfileInfoBinding.mEtCeo.getText().toString(),
                            fragmentCreateProfileInfoBinding.mEtComapny.getText().toString(),
                            userBirthday,
                            gender);
                }
                break;
        }
    }

    private void reqSearchState(String text) {
        mCallSearchCity = Api.WEB_SERVICE.searchState(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), text, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        mCallSearchCity.enqueue(new Callback<SearchCityZipObject>() {
            @Override
            public void onResponse(Call<SearchCityZipObject> call, Response<SearchCityZipObject> response) {
                if (response.body() != null){
                switch (response.body().getStatus()) {
                    case 1:
                        if (getActivity() != null) {
                            mLstSearchData = response.body().getSearchState();
                            mLstSearchDataTemp = new ArrayList<>();
                            for (SearchCityZipData searchCityZipData : mLstSearchData) {
                                boolean isFound = false;
                                for (SearchCityZipData searchCityZipDataTemp : mLstSearchDataTemp) {
                                    if (searchCityZipData.getCity().equalsIgnoreCase(searchCityZipDataTemp.getCity())) {
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (!isFound) {
                                    mLstSearchDataTemp.add(searchCityZipData);
                                }
                            }
                            cityNames = new String[mLstSearchDataTemp.size()];
                            for (int i = 0; i < mLstSearchDataTemp.size(); i++) {
                                cityNames[i] = mLstSearchDataTemp.get(i).getCity() + ", " + mLstSearchDataTemp.get(i).getState();
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cityNames);
                            fragmentCreateProfileInfoBinding.mEtHomeBase.setAdapter(arrayAdapter);
                            fragmentCreateProfileInfoBinding.mEtHomeBase.showDropDown();
                            fragmentCreateProfileInfoBinding.mEtHomeBase.setThreshold(1);
                        }
                        break;
                    case 0:
                        break;
                }
            }
            }

            @Override
            public void onFailure(Call<SearchCityZipObject> call, Throwable error) {
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

    //     Method to upload profile image....
    private void reqCreateProfile(String firstName, String lastName, String nickName, String address, String currentPosition, String company, String birthday, String gender) {
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(fragmentCreateProfileInfoBinding.mRoot);
        ((MainActivity) getActivity()).progressDialog.show();
        mCallCreateProfile = Api.WEB_SERVICE.createUserSecondaryProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), firstName, lastName, nickName, currentPosition, company, address, birthday, gender);
        mCallCreateProfile.enqueue(new Callback<CreateMultipleProfileMainObject>() {
            @Override
            public void onResponse(Call<CreateMultipleProfileMainObject> call, Response<CreateMultipleProfileMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
//                            ((MainActivity) getActivity()).showMessageDialog("success", "Secondary profile created successfully.");
                            MultiProfileRoomModel multiProfileRoomModel = multiProfileRepositry.insertProfilesData(
                                    response.body().getCreateUserSecondaryProfile().getId(),
                                    response.body().getCreateUserSecondaryProfile().getUser_id(),
                                    response.body().getCreateUserSecondaryProfile().getUser_fname(),
                                    response.body().getCreateUserSecondaryProfile().getUser_lname(),
                                    response.body().getCreateUserSecondaryProfile().getUser_nickname(),
                                    response.body().getCreateUserSecondaryProfile().getUser_avatar(),
                                    response.body().getCreateUserSecondaryProfile().getAvatar_from(),
                                    response.body().getCreateUserSecondaryProfile().getUser_cnic(),
                                    response.body().getCreateUserSecondaryProfile().getUser_address(),
                                    response.body().getCreateUserSecondaryProfile().getUser_phone(),
                                    response.body().getCreateUserSecondaryProfile().getSecondary_phones(),
                                    response.body().getCreateUserSecondaryProfile().getSecondary_emails(),
                                    response.body().getCreateUserSecondaryProfile().getUser_current_position(),
                                    response.body().getCreateUserSecondaryProfile().getUser_company(),
                                    response.body().getCreateUserSecondaryProfile().getUser_birthday(),
                                    response.body().getCreateUserSecondaryProfile().getUser_bio(),
                                    response.body().getCreateUserSecondaryProfile().getUser_status_msg(),
                                    response.body().getCreateUserSecondaryProfile().getUser_tags(),
                                    response.body().getCreateUserSecondaryProfile().getUser_interests(),
                                    response.body().getCreateUserSecondaryProfile().getUser_gender(),
                                    response.body().getCreateUserSecondaryProfile().getHome_default_view(),
                                    response.body().getCreateUserSecondaryProfile().getContact_default_view(),
                                    response.body().getCreateUserSecondaryProfile().getSocial_links(),
                                    response.body().getCreateUserSecondaryProfile().getProfile_status(),
                                    response.body().getCreateUserSecondaryProfile().getCreated_at(),
                                    response.body().getCreateUserSecondaryProfile().getUpdated_at(),
                                    response.body().getCreateUserSecondaryProfile().getVisibleTo(),
                                    response.body().getCreateUserSecondaryProfile().getVisibleAt()
                            );
                            EventBus.getDefault().post(new EventBusRefreshUserSecondaryProfileData(multiProfileRoomModel));
                            Bundle bundle = new Bundle();
                            bundle.putString(EndpointKeys.TYPE, "secondary_profile_image");
                            bundle.putString("secondaryProfileId", response.body().getCreateUserSecondaryProfile().getId());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_PICTURE, true, bundle);
                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateMultipleProfileMainObject> call, Throwable error) {
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