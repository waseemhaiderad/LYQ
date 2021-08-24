package lnq.com.lnq.fragments.registeration.createprofile;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentCreateProfileBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.search_city_zip.SearchCityZipData;
import lnq.com.lnq.model.gson_converter_models.search_city_zip.SearchCityZipObject;
import lnq.com.lnq.application.LnqApplication;

import lnq.com.lnq.custom.views.date_picker_dialog.SingleDateAndTimePicker;
import lnq.com.lnq.custom.views.date_picker_dialog.dialog.SingleDateAndTimePickerDialog;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEvent;
import lnq.com.lnq.custom.keyboard_event_listener.KeyboardVisibilityEventListener;
import lnq.com.lnq.custom.keyboard_event_listener.Unregistrar;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentCreateProfile extends Fragment {

    //    Android fields....
    private FragmentCreateProfileBinding createProfileBinding;
    private SingleDateAndTimePickerDialog.Builder singleBuilder;
    private Animation mSlideUpAnimation, mSlideDownAnimation;
    private Unregistrar unregister;
    private int previousLength;
    private boolean backSpace;
    private AppCompatImageView imageViewBackTopBar;
    private AppCompatImageView imageViewBackTopBar1;

    //    Api fields....
    private Call<CreateUserProfileMainObject> callCreateProfile;
    private Call<SearchCityZipObject> callSearchCity;

    //    Instance fields....
    long delay = 1500;
    long last_text_edit = 0;
    Handler handler = new Handler();
    private List<SearchCityZipData> searchCityList;
    private List<SearchCityZipData> mLstSearchDataTemp;
    private FragmentCreateProfileClickListener createProfileClickListener;
    String[] cityNames;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentCreateProfile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_profile, container, false);
        return createProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setCustomFont();
        //        All event listeners....
        createProfileClickListener = new FragmentCreateProfileClickListener(getActivity());
        createProfileBinding.setCreateProfileClick(createProfileClickListener);

        CardView topBarLayout = createProfileBinding.tobBar.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.create_profile);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.INVISIBLE);

        CardView topBarLayout1 = createProfileBinding.tobBar1.topBarCardView;
        imageViewBackTopBar1 = topBarLayout1.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading1 = topBarLayout1.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading1.setText(R.string.create_profile);
        ValidUtils.textViewGradientColor(textViewHeading1);
        imageViewBackTopBar1.setVisibility(View.INVISIBLE);

    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());

        fontUtils.setEditTextRegularFont(createProfileBinding.editTextCurrentPosition);
        fontUtils.setEditTextRegularFont(createProfileBinding.editTextCompany);
        fontUtils.setEditTextRegularFont(createProfileBinding.editTextLivingIn);
        fontUtils.setEditTextRegularFont(createProfileBinding.editTextBirthday);
        fontUtils.setTextViewMedium(createProfileBinding.clearTextViewAddProfilePicture);
        fontUtils.setEditTextRegularFont(createProfileBinding.editTextCurrentPosition1);
        fontUtils.setEditTextRegularFont(createProfileBinding.editTextCompany1);
        fontUtils.setEditTextRegularFont(createProfileBinding.editTextLiving1);
        fontUtils.setEditTextRegularFont(createProfileBinding.editTextBirthday1);
        fontUtils.setTextViewMedium(createProfileBinding.clearTextViewAddProfilePicture1);
    }

    private void init() {
//        Loading animations....
        mSlideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        mSlideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

//        All event listeners....
        createProfileBinding.editTextBirthday.setFocusable(false);
        createProfileBinding.editTextBirthday.setClickable(true);
        createProfileBinding.editTextBirthday1.setFocusable(false);
        createProfileBinding.editTextBirthday1.setClickable(true);

        createProfileBinding.editTextCurrentPosition1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fnIsUserTyping(s.toString());
                backSpace = previousLength > s.length();
                if (backSpace) {
                    createProfileBinding.editTextCurrentPosition1.setText("");
                }
            }
        });

        createProfileBinding.editTextCompany1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fnIsUserTyping(s.toString());
                backSpace = previousLength > s.length();
                if (backSpace) {
                    createProfileBinding.editTextCompany1.setText("");
                }
            }
        });

        createProfileBinding.editTextLiving1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fnIsUserTyping(s.toString());
                backSpace = previousLength > s.length();
                if (backSpace) {
                    createProfileBinding.editTextLiving1.setText("");
                }
            }
        });
        createProfileBinding.editTextBirthday1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fnIsUserTyping(s.toString());
                backSpace = previousLength > s.length();
                if (backSpace) {
                    createProfileBinding.editTextBirthday1.setText("");
                }
            }
        });
        createProfileBinding.editTextLiving1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                createProfileBinding.textViewLivingFormat1.setVisibility(View.INVISIBLE);
//                createProfileBinding.textViewLivingFormat.setVisibility(View.INVISIBLE);
//                createProfileBinding.editTextLiving1.setText(searchCityList.get(position).getCity() + ", " + searchCityList.get(position).getState() + ", " + "US");
                createProfileBinding.editTextLiving1.setText(cityNames[position] + ", " + "US");
            }
        });
        unregister = KeyboardVisibilityEvent.registerEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            if (createProfileBinding.editTextCompany.hasFocus()) {
                                fnChangeFocus(createProfileBinding.editTextCompany1);
                            } else if (createProfileBinding.editTextCurrentPosition.hasFocus()) {
                                fnChangeFocus(createProfileBinding.editTextCurrentPosition1);
                            } else if (createProfileBinding.mETGender.hasFocus()) {
                                fnChangeFocus(createProfileBinding.mETGender1);
                            } else {
                                fnChangeFocus(createProfileBinding.editTextLiving1);
                            }
                            createProfileBinding.mRoot1.startAnimation(mSlideUpAnimation);
                            fnToggleVisibilityOfRoot(createProfileBinding.mRoot1, createProfileBinding.mRoot);
                            CardView topBarLayout1 = createProfileBinding.tobBar1.topBarCardView;
                            imageViewBackTopBar1 = topBarLayout1.findViewById(R.id.imageViewBackTopBar);
                            TextView textViewHeading1 = topBarLayout1.findViewById(R.id.textViewUserNameTopBar);
                            textViewHeading1.setText(R.string.create_profile);
                            ValidUtils.textViewGradientColor(textViewHeading1);

                            imageViewBackTopBar1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().onBackPressed();
                                }
                            });

                        } else {
                            createProfileBinding.editTextCompany.setText(createProfileBinding.editTextCompany1.getText().toString());
                            createProfileBinding.editTextCurrentPosition.setText(createProfileBinding.editTextCurrentPosition1.getText().toString());
                            createProfileBinding.editTextLivingIn.setText(createProfileBinding.editTextLiving1.getText().toString());
                            createProfileBinding.mETGender.setText(createProfileBinding.mETGender1.getText().toString());
                            if (createProfileBinding.editTextCompany1.hasFocus()) {
                                fnChangeFocus(createProfileBinding.editTextCompany);
                            } else if (createProfileBinding.editTextCurrentPosition1.hasFocus()) {
                                fnChangeFocus(createProfileBinding.editTextCurrentPosition);
                            } else if (createProfileBinding.mETGender1.hasFocus()) {
                                fnChangeFocus(createProfileBinding.mETGender);
                            } else {
                                fnChangeFocus(createProfileBinding.editTextLivingIn);
                            }
                            fnToggleVisibilityOfRoot(createProfileBinding.mRoot, createProfileBinding.mRoot1);
                            createProfileBinding.mRoot.startAnimation(mSlideDownAnimation);
                        }
                    }
                });
        createProfileBinding.mSpinnerGenderCreate.setOnItemSelectedListener(new SpinnerSelectedListener(createProfileBinding.mSpinnerGenderCreate1));
        createProfileBinding.mSpinnerGenderCreate1.setOnItemSelectedListener(new SpinnerSelectedListener(createProfileBinding.mSpinnerGenderCreate));

        createProfileBinding.mRootlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), createProfileBinding.mRootlayout);
                return false;
            }
        });

    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        Spinner spinner;

        SpinnerSelectedListener(Spinner spinner) {
            this.spinner = spinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spinner.setSelection(i);
            if (i == 2) {
                createProfileBinding.mETGender.setVisibility(View.VISIBLE);
                createProfileBinding.mETGender1.setVisibility(View.VISIBLE);
            } else {
                createProfileBinding.mETGender.setVisibility(View.INVISIBLE);
                createProfileBinding.mETGender1.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void fnIsUserTyping(String text) {
        if (text.isEmpty()) {
            return;
        }
        if (text.length() > 0) {
            if (callSearchCity != null && callSearchCity.isExecuted()) {
                callSearchCity.cancel();
            }
            last_text_edit = System.currentTimeMillis();
            handler.postDelayed(input_finish_checker, delay);
        }
    }

    //    Method to change focus of edit texts....
    private void fnChangeFocus(EditText editText) {
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
    }

    //    Method to toggle visibility of view groups....
    public void fnToggleVisibilityOfRoot(ViewGroup viewGroupShow, ViewGroup viewGroupHide) {
        viewGroupShow.setVisibility(View.VISIBLE);
        viewGroupHide.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callCreateProfile != null && callCreateProfile.isExecuted()) {
            callCreateProfile.cancel();
        }
        if (callSearchCity != null && callSearchCity.isExecuted()) {
            callSearchCity.cancel();
        }
        if (unregister != null) {
            unregister.unregister();
        }
        if (singleBuilder != null) {
            singleBuilder.dismiss();
        }
    }

    //    Method to validate user data....
    private void fnValidateData(String currentPosition, String company, String livingIn, String birthday, String gender) {
        if (!((MainActivity) getActivity()).fnIsisOnline()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.wifi_internet_not_connected));
            return;
        }
//        if (currentPosition.trim().isEmpty()) {
//            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_current_position));
//        } else if (company.trim().isEmpty()) {
//            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_company));
//        }
        if (livingIn.trim().isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_living_in));
        } else if (birthday.trim().isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_birthday));
        } else if (gender.trim().isEmpty()) {
            ((MainActivity) getActivity()).showMessageDialog("error", getResources().getString(R.string.empty_gender));
        } else {
            reqProfile(currentPosition, company, livingIn, birthday, gender);
        }
    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                reqSearchState(createProfileBinding.editTextLiving1.getText().toString());
            }
        }
    };

    //    Method to request api to create profile of user....
    private void reqProfile(String currentPosition, String company, String livingIn, String birthday, String gender) {
        ((MainActivity) getActivity()).fnHideKeyboardForcefully(createProfileBinding.getRoot());
//        ((MainActivity) getActivity()).progressBarQNewTheme(View.VISIBLE);
        createProfileBinding.clearTextViewAddProfilePicture.setVisibility(View.INVISIBLE);
        createProfileBinding.clearTextViewLogin2.setVisibility(View.VISIBLE);
        createProfileBinding.clearTextViewLogin2.startAnimation();
        callCreateProfile = Api.WEB_SERVICE.createProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), livingIn, currentPosition, company, birthday, "", gender);
//        callCreateProfile = Api.WEB_SERVICE.createProfile(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), livingIn, currentPosition, company, birthday, "");
        callCreateProfile.enqueue(new Callback<CreateUserProfileMainObject>() {
            @Override
            public void onResponse(Call<CreateUserProfileMainObject> call, Response<CreateUserProfileMainObject> response) {
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                createProfileBinding.clearTextViewLogin2.stopAnimation();
                createProfileBinding.clearTextViewAddProfilePicture.setVisibility(View.VISIBLE);
                createProfileBinding.clearTextViewLogin2.setVisibility(View.GONE);
                createProfileBinding.clearTextViewLogin2.revertAnimation();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().editor.putString(EndpointKeys.VERIFICATION_STATUS, response.body().getCreateUserProfile().getVerification_status());
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_CURRENT_POSITION, response.body().getCreateUserProfile().getUser_current_position());
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_COMPANY, response.body().getCreateUserProfile().getUser_company());
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIRTHDAY, response.body().getCreateUserProfile().getUser_birthday());
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_BIO, response.body().getCreateUserProfile().getUser_bio());
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_ADDRESS, response.body().getCreateUserProfile().getUser_address());
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_Gender, response.body().getCreateUserProfile().getUser_gender());
                            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.PROFILE_PICTURE, true, null);
                            LnqApplication.getInstance().editor.apply();
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
                ((MainActivity) getActivity()).progressBarQNewTheme(View.GONE);
                createProfileBinding.clearTextViewAddProfilePicture.setVisibility(View.VISIBLE);
                createProfileBinding.clearTextViewLogin2.setVisibility(View.GONE);
                createProfileBinding.clearTextViewLogin2.revertAnimation();
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

    //    Method to request api to search states for city suggestion....
    private void reqSearchState(String text) {
//        callSearchCity = Api.WEB_SERVICE.searchState(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), text);
        callSearchCity = Api.WEB_SERVICE.searchState(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), text, LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""));
        callSearchCity.enqueue(new Callback<SearchCityZipObject>() {
            @Override
            public void onResponse(Call<SearchCityZipObject> call, Response<SearchCityZipObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            searchCityList = response.body().getSearchState();
                            mLstSearchDataTemp = new ArrayList<>();
                            for (SearchCityZipData searchCityZipData : searchCityList) {
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
                            createProfileBinding.editTextLiving1.setAdapter(arrayAdapter);
                            createProfileBinding.editTextLiving1.showDropDown();
                            createProfileBinding.editTextLivingIn.setThreshold(1);
                            createProfileBinding.editTextLiving1.setThreshold(1);
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

    public class FragmentCreateProfileClickListener {

        Context context;

        public FragmentCreateProfileClickListener(Context context) {
            this.context = context;
        }

        public void onCreateProfileClick(View view) {
            String gender = createProfileBinding.mSpinnerGenderCreate.getSelectedItemPosition() == 2 ? createProfileBinding.mETGender.getText().toString()
                    : createProfileBinding.mSpinnerGenderCreate.getSelectedItem().toString();
            fnValidateData(createProfileBinding.editTextCurrentPosition.getText().toString(), createProfileBinding.editTextCompany.getText().toString(), createProfileBinding.editTextLivingIn.getText().toString(), createProfileBinding.editTextBirthday.getText().toString(), gender);
        }

        public void onCreateProfileClick1(View view) {
            String gender = createProfileBinding.mSpinnerGenderCreate1.getSelectedItemPosition() == 2 ? createProfileBinding.mETGender1.getText().toString()
                    : createProfileBinding.mSpinnerGenderCreate1.getSelectedItem().toString();
            fnValidateData(createProfileBinding.editTextCurrentPosition1.getText().toString(), createProfileBinding.editTextCompany1.getText().toString(), createProfileBinding.editTextLiving1.getText().toString(), createProfileBinding.editTextBirthday1.getText().toString(), gender);
        }

        Date currentDate = new Date(System.currentTimeMillis());

        public void onBirthdayClicked(View view) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(createProfileBinding.getRoot());
            singleBuilder = new SingleDateAndTimePickerDialog.Builder(getActivity())
                    .bottomSheet()
                    .curved()
                    .displayYears(true)
                    .displayMonth(true)
                    .displayDaysOfMonth(true)
                    .todayText("Today")
                    .maxDateRange(currentDate)
                    .defaultDate(currentDate)
                    .mainColor(getResources().getColor(R.color.colorPrimaryBlue))
                    .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                        @Override
                        public void onDisplayed(SingleDateAndTimePicker picker) {

                        }
                    })
                    .listener(new SingleDateAndTimePickerDialog.Listener() {
                        @Override
                        public void onDateSelected(Date date) {
//                            createProfileBinding.textViewBirthdayFormat.setVisibility(View.INVISIBLE);
                            createProfileBinding.editTextBirthday.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
//                            createProfileBinding.textViewBirthdayFormat1.setVisibility(View.INVISIBLE);
                            createProfileBinding.editTextBirthday1.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
                        }
                    });
            singleBuilder.display();
        }

        public void backPressed(View view) {
            getActivity().onBackPressed();
        }

        public void onRootClicked(View view) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(createProfileBinding.mRoot);
        }
    }

}