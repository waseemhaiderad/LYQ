package lnq.com.lnq.fragments.profile.editprofile;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

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

import com.google.api.client.util.DateTime;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.databinding.FragmentEditProfileBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.registeration.createprofile.FragmentCreateProfile;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.search_city_zip.SearchCityZipData;
import lnq.com.lnq.model.gson_converter_models.search_city_zip.SearchCityZipObject;
import lnq.com.lnq.model.event_bus_models.EventBusRefreshUserData;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.custom.views.date_picker_dialog.SingleDateAndTimePicker;
import lnq.com.lnq.custom.views.date_picker_dialog.dialog.SingleDateAndTimePickerDialog;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEditProfile extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentEditProfileBinding editProfileBinding;
    private SingleDateAndTimePickerDialog.Builder singleBuilder;

    //    Retrofit fields....
    private Call<SearchCityZipObject> mCallSearchCity;
    private Call<CreateUserProfileMainObject> mCallUpdateProfile;

    //    Instance fields....
    long delay = 1500;
    long last_text_edit = 0;
    Handler handler = new Handler();
    private List<SearchCityZipData> mLstSearchData;
    private List<SearchCityZipData> mLstSearchDataTemp;

    private int previousLength;
    private boolean backSpace;
    Animation animShake;
    private AppCompatImageView imageViewBackTopBar;
    private MultiProfileRoomModel currentUserProfileData;

    private MultiProfileRepositry multiProfileRepositry;
    String[] cityNames;


    public FragmentEditProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        editProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        return editProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        CardView topBarLayout = editProfileBinding.tobBar.topBarCardView;
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
        editProfileBinding.mEtBirthDay.setFocusable(false);
        editProfileBinding.mEtBirthDay.setClickable(true);
        editProfileBinding.mBtnSaveChange.setOnClickListener(this);
        editProfileBinding.mEtBirthDay.setOnClickListener(this);

        editProfileBinding.mEtUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editProfileBinding.mEtUser.getText().toString().isEmpty()) {
//                    editProfileBinding.mTvUser.setVisibility(View.VISIBLE);
                } else {
//                    editProfileBinding.mTvUser.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                backSpace = previousLength > s.length();
//                if (backSpace) {
//                    try {
//                        if (s.length() > 0) {
//                            editProfileBinding.mTvUser.getText().subSequence(0, s.length() - 1);
//                        }
//                    }catch (Exception e){
//                        throw e;
//                    }
//                }

            }
        });

        editProfileBinding.mEtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editProfileBinding.mEtLastName.getText().toString().isEmpty()) {
//                    editProfileBinding.mTvLastName.setVisibility(View.VISIBLE);
                } else {
//                    editProfileBinding.mTvLastName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                backSpace = previousLength > s.length();
//                if (backSpace) {
//                    if (s.length() > 0) {
//                        editProfileBinding.mTvLastName.getText().subSequence(0, s.length() - 1);
//                    }
//                }
            }
        });

        editProfileBinding.mEtCeo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editProfileBinding.mEtCeo.getText().toString().isEmpty()) {
//                    editProfileBinding.mTvCeoTitle.setVisibility(View.VISIBLE);
                } else {
//                    editProfileBinding.mTvCeoTitle.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                backSpace = previousLength > s.length();
//                if (backSpace) {
//                    if (s.length() > 0) {
//                        editProfileBinding.mEtCeo.getText().subSequence(0, s.length() - 1);
//
//
//                    }
//            }
            }
        });

        editProfileBinding.mEtComapny.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editProfileBinding.mEtComapny.getText().toString().isEmpty()) {
//                    editProfileBinding.mTvCompany.setVisibility(View.VISIBLE);
                } else {
//                    editProfileBinding.mTvCompany.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                backSpace = previousLength > s.length();
//                if (backSpace) {
//                    if (s.length() > 0) {
//                        editProfileBinding.mEtComapny.getText().subSequence(0, s.length() - 1);
//
//
//                    }
//                }
            }
        });

        editProfileBinding.mEtHomeBase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //        previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (editProfileBinding.mEtHomeBase.getText().toString().isEmpty()) {
//                    editProfileBinding.mTvHomeBase.setVisibility(View.VISIBLE);
//                } else {
//                    editProfileBinding.mTvHomeBase.setVisibility(View.GONE);
//                }
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fnIsUserTyping(s.toString());
//                backSpace = previousLength > s.length();
//                if (backSpace) {
//                    if (s.length() > 0) {
//
//                   editProfileBinding.mEtHomeBase.getText().subSequence(0, s.length() - 1);
//                    }
//                }
//
//
//                 }
            }
        });
        editProfileBinding.mEtBirthDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                if (editProfileBinding.mEtBirthDay.getText().toString().isEmpty()) {
//                    editProfileBinding.mTvBirthDay.setVisibility(View.VISIBLE);
//                } else {
//                    editProfileBinding.mTvBirthDay.setVisibility(View.GONE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                backSpace = previousLength > s.length();
//                if (backSpace) {
//                    editProfileBinding.mEtBirthDay.setText(new SimpleDateFormat("MM dd, yyyy", Locale.getDefault()).format(date));
//                }

            }
        });

        editProfileBinding.mEtHomeBase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                editProfileBinding.mEtHomeBase.setText(mLstSearchDataTemp.get(position).getCity() + ", " + mLstSearchDataTemp.get(position).getState() + ", " + "US");
                editProfileBinding.mEtHomeBase.setText(cityNames[position] + ", " + "US");
            }
        });

        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentUserProfileData = data;
                        editProfileBinding.mEtUser.setText(data.getUser_fname());
                        editProfileBinding.mEtLastName.setText(data.getUser_lname());
                        editProfileBinding.mEtComapny.setText(data.getUser_company());
                        editProfileBinding.mEtBirthDay.setText(data.getUser_birthday());
                        editProfileBinding.mEtCeo.setText(data.getUser_current_position());
                        editProfileBinding.mEtHomeBase.setText(data.getUser_address());
                        editProfileBinding.mEtNicKName.setText(data.getUser_nickname());

                        if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                            editProfileBinding.mTiUser.setVisibility(View.VISIBLE);
                            editProfileBinding.mTiLastName.setVisibility(View.VISIBLE);
                        }
                        else {
                            editProfileBinding.mTiUser.setVisibility(View.GONE);
                            editProfileBinding.mTiLastName.setVisibility(View.GONE);
                        }

                        String genderText = data.getUser_gender();
                        ArrayAdapter adapter = (ArrayAdapter) editProfileBinding.mSpinnerGender.getAdapter();
                        int spinnerPosition = 0;
                        if (genderText.equalsIgnoreCase("male") || genderText.equalsIgnoreCase("female")) {
                            spinnerPosition = adapter.getPosition(genderText);
                        } else {
                            spinnerPosition = 2;
                            editProfileBinding.mETGender.setText(genderText);
                        }
                        editProfileBinding.mSpinnerGender.setSelection(spinnerPosition);
                    }
                }
            }
        });

        editProfileBinding.mSpinnerGender.setOnItemSelectedListener(new FragmentEditProfile.SpinnerSelectedListenerEdit(editProfileBinding.mSpinnerGender));

        editProfileBinding.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), editProfileBinding.mRoot);
                return false;
            }
        });

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
                editProfileBinding.mETGender.setVisibility(View.VISIBLE);
            } else {
                editProfileBinding.mETGender.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                String city = editProfileBinding.mEtHomeBase.getText().toString();
                if (city.contains(",")) {
                    city = city.replaceAll(",", "").replaceAll(" ", "").trim();
                }
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
        if (singleBuilder != null)
            singleBuilder.dismiss();
        if (mCallSearchCity != null && mCallSearchCity.isExecuted()) {
            mCallSearchCity.cancel();
        }
        if (mCallUpdateProfile != null && mCallUpdateProfile.isExecuted()) {
            mCallUpdateProfile.cancel();
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

                if (editProfileBinding.mEtUser.getText().toString().equals(currentUserProfileData.getUser_fname()) &&
                        editProfileBinding.mEtLastName.getText().toString().equals(currentUserProfileData.getUser_lname()) &&
                        editProfileBinding.mEtComapny.getText().toString().equals(currentUserProfileData.getUser_company()) &&
                        editProfileBinding.mEtBirthDay.getText().toString().equals(currentUserProfileData.getUser_birthday()) &&
                        editProfileBinding.mEtCeo.getText().toString().equals(currentUserProfileData.getUser_current_position()) &&
                        editProfileBinding.mEtNicKName.getText().toString().equals(currentUserProfileData.getUser_nickname()) &&
                        editProfileBinding.mSpinnerGender.getSelectedItem().toString().equals(currentUserProfileData.getUser_gender()) &&
                        editProfileBinding.mEtHomeBase.getText().toString().equals(currentUserProfileData.getUser_address())) {
                    ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.nothing_to_update));
                    return;
                }
                String gender = editProfileBinding.mSpinnerGender.getSelectedItemPosition() == 2 ? editProfileBinding.mETGender.getText().toString()
                        : editProfileBinding.mSpinnerGender.getSelectedItem().toString();
                if (editProfileBinding.mEtUser.getText().toString().trim().isEmpty()) {
                    editProfileBinding.mEtUser.startAnimation(animShake);
                } else if (editProfileBinding.mEtLastName.getText().toString().trim().isEmpty()) {
                    editProfileBinding.mEtLastName.startAnimation(animShake);
                } else if (editProfileBinding.mEtHomeBase.getText().toString().trim().isEmpty()) {
                    editProfileBinding.mEtHomeBase.startAnimation(animShake);
                } else if (editProfileBinding.mEtBirthDay.getText().toString().trim().isEmpty()) {
                    editProfileBinding.mEtBirthDay.startAnimation(animShake);
                } else if (gender.trim().isEmpty()) {
                    editProfileBinding.mETGender.startAnimation(animShake);
                } else {
                    reqUpdateProfile(editProfileBinding.mEtUser.getText().toString(), editProfileBinding.mEtLastName.getText().toString(), editProfileBinding.mEtCeo.getText().toString(), editProfileBinding.mEtComapny.getText().toString(), editProfileBinding.mEtHomeBase.getText().toString(), editProfileBinding.mEtBirthDay.getText().toString(), gender, currentUserProfileData.getId(), editProfileBinding.mEtNicKName.getText().toString());
                }
                break;
            case R.id.mEtBirthDay:
                ((MainActivity) getActivity()).fnHideKeyboardForcefully(editProfileBinding.getRoot());
                try {
                    singleBuilder = new SingleDateAndTimePickerDialog.Builder(getActivity())
                            .bottomSheet()
                            .curved()
                            .displayHours(false)
                            .displayMinutes(false)
                            .displayDays(false)
                            .displayMonth(true)
                            .displayDaysOfMonth(true)
                            .displayYears(true)
                            .displayMonthNumbers(false)
                            .todayText("Today")
                            .maxDateRange(currentDate)
                            .defaultDate(new SimpleDateFormat("MMMM dd, yyyy").parse(LnqApplication.getInstance().sharedPreferences.getString("user_birthday", "")))
                            .mainColor(getResources().getColor(R.color.colorPrimaryBlue))
                            .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                                @Override
                                public void onDisplayed(SingleDateAndTimePicker picker) {

                                }
                            })
                            .listener(new SingleDateAndTimePickerDialog.Listener() {
                                @Override
                                public void onDateSelected(Date date) {
                                    editProfileBinding.mEtBirthDay.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
                                }
                            });
                    singleBuilder.display();
                } catch (Exception e) {

                }
                break;
        }
    }

    private void reqSearchState(String text) {
//        mCallSearchCity = Api.WEB_SERVICE.searchState(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), text);
        mCallSearchCity = Api.WEB_SERVICE.searchState(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), text, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        mCallSearchCity.enqueue(new Callback<SearchCityZipObject>() {
            @Override
            public void onResponse(Call<SearchCityZipObject> call, Response<SearchCityZipObject> response) {
                if (response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (getActivity() != null) {
                                mLstSearchData = response.body().getSearchState();
                                mLstSearchDataTemp = new ArrayList<>();
                                for (SearchCityZipData searchCityZipData : mLstSearchData) {
                                    boolean isFound = false;
                                    for (SearchCityZipData searchCityZipDataTemp : mLstSearchDataTemp) {
                                        if (searchCityZipData.getCity().equalsIgnoreCase(searchCityZipDataTemp.getCity()) && searchCityZipData.getState().equalsIgnoreCase(searchCityZipDataTemp.getState())) {
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
//                                    cityNames[i] = mLstSearchData.get(i).getCity() + "(" + mLstSearchData.get(i).getZip_code() + ") " + mLstSearchData.get(i).getState();
                                    cityNames[i] = mLstSearchDataTemp.get(i).getCity() + ", " + mLstSearchDataTemp.get(i).getState();
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cityNames);
                                editProfileBinding.mEtHomeBase.setAdapter(arrayAdapter);
                                editProfileBinding.mEtHomeBase.showDropDown();
                                editProfileBinding.mEtHomeBase.setThreshold(1);
                            }
                            break;
                        case 0:
//                        ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
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
    private void reqUpdateProfile(String firstName, String lastName, String currentPosition, String company, String address, String birthday, String gender, String profileId, String nickName) {
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(editProfileBinding.mRoot);
        ((MainActivity) getActivity()).progressDialog.show();
        mCallUpdateProfile = Api.WEB_SERVICE.updateUserProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), firstName, lastName, currentPosition, company, address, birthday, gender, profileId, nickName);
//        mCallUpdateProfile = Api.WEB_SERVICE.updateUserProfile(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), firstName, lastName, currentPosition, company, address, birthday);
        mCallUpdateProfile.enqueue(new Callback<CreateUserProfileMainObject>() {
            @Override
            public void onResponse(Call<CreateUserProfileMainObject> call, Response<CreateUserProfileMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
                                        @Override
                                        public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                                            for (MultiProfileRoomModel data : multiProfileRoomModels) {
                                                data.setUser_fname(response.body().getUpdateUserProfile().getUser_fname());
                                                data.setUser_lname(response.body().getUpdateUserProfile().getUser_lname());
                                                data.setUser_birthday(response.body().getUpdateUserProfile().getUser_birthday());
                                            }
                                            multiProfileRepositry.updateProfileList(multiProfileRoomModels);
                                        }
                                    });
                                    EventBus.getDefault().post(new EventBusRefreshUserData());
                                    ((MainActivity) getActivity()).showMessageDialog("success", "Profile updated successfully.");
                                    EventBus.getDefault().post(new EventBusUserSession("basic_info_updated"));
                                    getActivity().onBackPressed();
                                }
                            }, 1000);
                            currentUserProfileData.setUser_fname(response.body().getUpdateUserProfile().getUser_fname());
                            currentUserProfileData.setUser_lname(response.body().getUpdateUserProfile().getUser_lname());
                            currentUserProfileData.setUser_birthday(response.body().getUpdateUserProfile().getUser_birthday());
                            currentUserProfileData.setUser_bio(response.body().getUpdateUserProfile().getUser_bio());
                            currentUserProfileData.setUser_address(response.body().getUpdateUserProfile().getUser_address());
                            currentUserProfileData.setUser_current_position(response.body().getUpdateUserProfile().getUser_current_position());
                            currentUserProfileData.setUser_company(response.body().getUpdateUserProfile().getUser_company());
                            currentUserProfileData.setUser_gender(response.body().getUpdateUserProfile().getUser_gender());
                            currentUserProfileData.setUser_nickname(response.body().getUpdateUserProfile().getUser_nickname());
                            multiProfileRepositry.updateTask(currentUserProfileData);
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FNAME, response.body().getUpdateUserProfile().getUser_fname()).apply();
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_LNAME, response.body().getUpdateUserProfile().getUser_lname()).apply();
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIRTHDAY, response.body().getUpdateUserProfile().getUser_birthday()).apply();

                            break;
                        case 0:
                            ((MainActivity) getActivity()).showMessageDialog("error", response.body().getMessage());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateUserProfileMainObject> call, Throwable error) {
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