package lnq.com.lnq.fragments.connections.import_contacts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.bumptech.glide.Glide;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.ImportContactsAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.custom.views.LinearLayoutManagerWithSmoothScroller;
import lnq.com.lnq.custom.views.fast_scroller.models.AlphabetItem;
import lnq.com.lnq.databinding.FragmentImportContactsBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.PhoneContactsModel;
import lnq.com.lnq.model.event_bus_models.EventBusContactPermission;
import lnq.com.lnq.model.event_bus_models.EventBusGetImportedContacts;
import lnq.com.lnq.model.event_bus_models.EventBusGetLnqedContactList;
import lnq.com.lnq.model.event_bus_models.EventBusImportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusAlphabetClick;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContactsMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts.ContactList;
import lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts.Contacts;
import lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts.ImportContactsModel;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.Contact;
import lnq.com.lnq.model.gson_converter_models.multipleemailcontants.PhoneContact;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.ValidUtils;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentImportContacts extends Fragment implements ImportContactsAdapter.OnCheckedListener {
    //    Android fields....
    private FragmentImportContactsBinding importContactsBinding;
    private ImportContactsClickHandler clickHandler;

    //    Instance fields....
    private ArrayList<String> alphabetsList = new ArrayList<>();
    private List<PhoneContactsModel> phoneContactsModelList = new ArrayList<>();
    private List<ImportContactsModel> contactsModelList = new ArrayList<>();
    List<ContactList> contactsList = new ArrayList<>();
    private List<AlphabetItem> mAlphabetItems;
    private String importType;
    private String searchSuggestion;

    //    Contact related instance fields....
    private String countryRegion;
    private PhoneNumberUtil phoneNumberUtil;

    //    Adapter fields....
    private ImportContactsAdapter importContactsAdapter;

    //    Retrofit fields....
    private Call<ExportContactsMainObject> callExportContacts;

    //    Font fields....
    private FontUtils fontUtils;

    private SlideUp slideUp;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewBackTopBar;
    CardView topBarLayout;

    public FragmentImportContacts() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        importContactsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_import_contacts, container, false);
        return importContactsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        OverScrollDecoratorHelper.setUpOverScroll(importContactsBinding.recyclerViewImportContacts, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        topBarLayout = importContactsBinding.topBarImportContact.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.import_connections);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);

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

        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), importContactsBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(importContactsBinding.slideViewContacts)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    importContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.VISIBLE);
                                    importContactsBinding.viewHideTopBar.setVisibility(View.GONE);

                                } else {
                                    importContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                                    importContactsBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .withStartGravity(Gravity.TOP)
                        .withLoggingEnabled(true)
                        .withGesturesEnabled(true)
                        .withStartState(SlideUp.State.SHOWED)
                        .build();
            }
        });
    }

    private void init() {
        if (getArguments() != null) {
            importType = getArguments().getString("import_type", "local");

//        Registering event bus for trigger of events....
            EventBus.getDefault().register(this);

//        Getting country region....
            countryRegion = ValidUtils.getCountryCode(getActivity());
            phoneNumberUtil = PhoneNumberUtil.getInstance();

//        Setting custom font....
            setCustomFont();

//        Setting layout managers of recycler view...
            importContactsBinding.recyclerViewImportContacts.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));

//        Setting item animators of recycler view....
            importContactsBinding.recyclerViewImportContacts.setItemAnimator(new DefaultItemAnimator());

//        Setting click handler for data binding....
            clickHandler = new ImportContactsClickHandler();
            importContactsBinding.setClickHandler(clickHandler);

            searchSuggestion = LnqApplication.getInstance().sharedPreferences.getString("search_suggestion", "");
            if (!searchSuggestion.isEmpty()) {
                List<String> suggestionArray = Arrays.asList(searchSuggestion.split(","));
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, suggestionArray);
                importContactsBinding.editTextSearch.setAdapter(arrayAdapter);
                importContactsBinding.editTextSearch.setThreshold(1);
            }

//        All event listeners....
            importContactsBinding.editTextSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    importContactsAdapter.getFilter().filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            importContactsBinding.recyclerViewImportContacts.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ValidUtils.hideKeyboardFromFragment(getContext(), importContactsBinding.getRoot());
                    return false;
                }
            });
        }

        importContactsBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
                }
            }
        });

        slideUp = new SlideUpBuilder(importContactsBinding.slideViewContacts)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            importContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.VISIBLE);
                            importContactsBinding.viewHideTopBar.setVisibility(View.GONE);
                        } else {
                            importContactsBinding.topBarImportContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                            importContactsBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(importContactsBinding.viewScroll)
                .build();
    }

    //    Method use to set custom font to android views....
    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setTextViewMedium(importContactsBinding.clearTextViewImport);
        fontUtils.setEditTextSemiBold(importContactsBinding.editTextSearch);
    }

    public static View makeMeBlink(View view, int duration, int offset) {

        Animation anim = new AlphaAnimation(0.3f, 1.0f);
        anim.setDuration(duration);
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callExportContacts != null && callExportContacts.isExecuted()) {
            callExportContacts.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    //    Event bus method triggers when user allow runtime permission to read contacts from mobile phone....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusContactPermission(EventBusContactPermission mObj) {
        new GetAllContactsTask().execute();
    }

    //    Event bus method triggers to get imported contact list from server from FragHomeContacts....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUserContactsDataList(EventBusGetLnqedContactList eventBusGetLnqedContactList) {
        for (int i = 0; i < eventBusGetLnqedContactList.getGetLnqContactList().size(); i++) {
            UserConnectionsData userContactsData = eventBusGetLnqedContactList.getGetLnqContactList().get(i);
            contactsModelList.add(new ImportContactsModel(userContactsData.getUser_id(), userContactsData.getUser_fname() + userContactsData.getUser_lname(), userContactsData.getUser_avatar(), userContactsData.getUser_phone(), userContactsData.getContact_status(), userContactsData.getUser_email()));
        }
        importContactsAdapter = new ImportContactsAdapter(getActivity(), phoneContactsModelList);
        importContactsBinding.recyclerViewImportContacts.setAdapter(importContactsAdapter);
        importContactsAdapter.setOnCheckedListener(this);

//        Checking phone contact permission....
        if (((MainActivity) getActivity()).fnCheckContactsPermission()) {
            if (importType.equalsIgnoreCase("local")) {
                new GetAllContactsTask().execute();
            } else {
                ((MainActivity) getActivity()).isFromImport = true;
                ((MainActivity) getActivity()).googleSignIn();
            }
        } else {
            ((MainActivity) getActivity()).fnRequestContactsPermission(5);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGoogleSignIn(EventBusGoogleSignInAccount eventBusGoogleSignInAccount) {
        new PeoplesAsync().execute(eventBusGoogleSignInAccount.getSignInAccount().getServerAuthCode());
    }

    //    Event bus method triggers when imprt users button is clicked from FragImportUsers to convert selected phone numbers
    //    to json and call the method to save these numbers to server....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusImportUsers(EventBusImportUsers eventBusImportUsers) {
        Contacts contacts = new Contacts(contactsList);
        Gson gson = new Gson();
        String jSon = gson.toJson(contacts);
        reqExportContacts(jSon);
    }

    //    Event bus method triggers when any alphabet is clicked....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fnAlphaEvent(EventBusAlphabetClick eventBusAlphaId) {
        String alpha = alphabetsList.get(eventBusAlphaId.getmPoss());
        for (int i = 0; i < phoneContactsModelList.size(); i++) {
            if (!phoneContactsModelList.get(i).getName().isEmpty()) {
                if (phoneContactsModelList.get(i).getName().length() > 1) {
                    if (alpha.equalsIgnoreCase(phoneContactsModelList.get(i).getName().substring(0, 1))) {
                        importContactsBinding.recyclerViewImportContacts.smoothScrollToPosition(i);
                        break;
                    }
                }
            }
        }
    }

    //    Method to get all contacts from mobile phone if permission is allowed....
    private void getAllContacts(List<Contact> contactList) {
        phoneContactsModelList.clear();
        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            HashMap<Integer, String> hashMapPhoneNumbers = contact.getPhones();
            HashMap<Integer, String> hashMapEmails = contact.getEmails();
            List<String> phoneList = new ArrayList<>();
            List<String> emailList = new ArrayList<>();
            if (hashMapPhoneNumbers != null) {
                int foundNumberCounter = 0, foundEmailCounter = 0;
                for (Map.Entry<Integer, String> phoneEntry : hashMapPhoneNumbers.entrySet()) {
                    int key = phoneEntry.getKey();
                    String value = phoneEntry.getValue();
                    try {
                        if (!phoneNumberUtil.isValidNumber(phoneNumberUtil.parseAndKeepRawInput(value, Constants.DEFAULT_REGION))) {
                            value = phoneNumberUtil.format(phoneNumberUtil.parseAndKeepRawInput(value, countryRegion.toUpperCase()), PhoneNumberUtil.PhoneNumberFormat.E164);
                            hashMapPhoneNumbers.put(key, value);
                        }
                        for (int j = 0; j < contactsModelList.size(); j++) {
                            String contactNumber = contactsModelList.get(j).getPhone();
                            if (!contactNumber.equalsIgnoreCase(value)) {
                                foundNumberCounter++;
                            } else {
                                foundNumberCounter = 0;
                                break;
                            }
                        }
                        if (foundNumberCounter > 0) {
                            phoneList.add(value);
                        }
                    } catch (NumberParseException e) {

                    }
                }
                if (hashMapEmails != null) {
                    for (Map.Entry<Integer, String> emailEntry : hashMapEmails.entrySet()) {
                        String value = emailEntry.getValue();
                        for (int j = 0; j < contactsModelList.size(); j++) {
                            String email = contactsModelList.get(j).getEmail();
                            if (!email.equalsIgnoreCase(value)) {
                                foundEmailCounter++;
                            } else {
                                foundEmailCounter = 0;
                                break;
                            }
                        }
                        if (foundEmailCounter > 0) {
                            emailList.add(value);
                        }
                    }
                }
                if (contactsModelList.size() == 0) {
                    for (Map.Entry<Integer, String> phoneEntry : hashMapPhoneNumbers.entrySet()) {
                        phoneList.add(phoneEntry.getValue());
                    }
                    if (hashMapEmails != null) {
                        for (Map.Entry<Integer, String> emailEntry : hashMapEmails.entrySet()) {
                            emailList.add(emailEntry.getValue());
                        }
                    }
                    phoneContactsModelList.add(new PhoneContactsModel(contact.getId(), contact.getDisplayName(), phoneList, emailList, contact.getImage(), false));
                    importContactsAdapter.notifyItemInserted(phoneContactsModelList.size() - 1);
                } else if (phoneList.size() > 0 || emailList.size() > 0) {
                    phoneContactsModelList.add(new PhoneContactsModel(contact.getId(), contact.getDisplayName(), phoneList, emailList, contact.getImage(), false));
                    importContactsAdapter.notifyItemInserted(phoneContactsModelList.size() - 1);
                }
            }
        }
        mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int j = 0; j < phoneContactsModelList.size(); j++) {
            String word = phoneContactsModelList.get(j).getName().substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(j, word, false));
            }
        }
        importContactsBinding.recyclerViewAlphabets.setRecyclerView((importContactsBinding.recyclerViewImportContacts));
        importContactsBinding.recyclerViewAlphabets.setUpAlphabet(mAlphabetItems);
    }

    class PeoplesAsync extends AsyncTask<String, Void, List<Contact>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Contact> doInBackground(String... params) {

            List<Contact> contactList = new ArrayList<>();

            try {
                People peopleService = PeopleHelper.setUp(getActivity(), params[0]);

                ListConnectionsResponse response = peopleService.people().connections()
                        .list("people/me")
                        // http://stackoverflow.com/questions/35604406/retrieving-information-about-a-contact-with-google-people-api-java
                        .setRequestMaskIncludeField("person.names,person.emailAddresses,person.phoneNumbers")
                        .execute();
                List<Person> connections = response.getConnections();
                if (connections != null && connections.size() > 0) {

                    for (Person person : connections) {
                        if (!person.isEmpty()) {

                            List<Name> names = person.getNames();
                            List<EmailAddress> emailAddresses = person.getEmailAddresses();
                            List<PhoneNumber> phoneNumbers = person.getPhoneNumbers();
                            HashMap<Integer, String> hashMapEmail = new HashMap<>();
                            HashMap<Integer, String> hashMapPhone = new HashMap<>();
                            String name = "";

                            if (phoneNumbers != null)
                                for (int i = 0; i < phoneNumbers.size(); i++)
                                    hashMapPhone.put(i, phoneNumbers.get(i).getValue());

                            if (emailAddresses != null)
                                for (int i = 0; i < emailAddresses.size(); i++)
                                    hashMapEmail.put(i, emailAddresses.get(i).getValue());


                            if (names != null && names.size() > 0)
                                name = names.get(0).getDisplayName();
                            Contact contact = new Contact();
                            contact.setDisplayName(name);
                            contact.setEmails(hashMapEmail);
                            contact.setPhones(hashMapPhone);
                            contact.setImage(null);
                            contact.setNote("");
                            contact.setId("");
                            contactList.add(contact);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return contactList;
        }


        @Override
        protected void onPostExecute(List<Contact> contactList) {
            super.onPostExecute(contactList);
            if (contactList != null && contactList.size() > 0) {
                importContactsBinding.shimmerLayout.setVisibility(View.GONE);
                importContactsBinding.shimmerLayout.stopShimmerAnimation();
                if (contactList.size() > 0) {
                    try {
                        Collections.sort(contactList, new Comparator<Contact>() {
                            @Override
                            public int compare(Contact o1, Contact o2) {
                                return o1.getDisplayName().compareTo(o2.getDisplayName());
                            }
                        });
                    } catch (Exception e) {

                    }
                    getAllContacts(contactList);
                }
            } else {
                Toast.makeText(getActivity(), "No contacts found in your gmail contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class GetAllContactsTask extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            importContactsBinding.shimmerLayout.setVisibility(View.VISIBLE);
            importContactsBinding.shimmerLayout.startShimmerAnimation();
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            if (getActivity() != null) {
                PhoneContact phoneContact = new PhoneContact(getActivity());
                return phoneContact.getAllContacts();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);
            if (contacts != null) {
                importContactsBinding.shimmerLayout.setVisibility(View.GONE);
                importContactsBinding.shimmerLayout.stopShimmerAnimation();
                if (contacts.size() > 0) {
                    try {
                        Collections.sort(contacts, new Comparator<Contact>() {
                            @Override
                            public int compare(Contact o1, Contact o2) {
                                return o1.getDisplayName().compareTo(o2.getDisplayName());
                            }
                        });
                    } catch (Exception e) {

                    }
                    getAllContacts(contacts);
                }
            }
        }
    }

    //    Override method trigger when selection button is clicked from phone contacts adapter....
    @Override
    public void onChecked(View view, int position, boolean isChecked, PhoneContactsModel phoneContactsModel) {
        if (isChecked) {
            for (int i = 0; i < contactsList.size(); i++) {
                ContactList contactList = contactsList.get(i);
                outer:
                for (int j = 0; j < phoneContactsModel.getPhoneNumber().size(); j++) {
                    for (int k = 0; k < contactList.getPhone_numbers().size(); k++) {
                        if (phoneContactsModel.getPhoneNumber().get(j).equalsIgnoreCase(contactList.getPhone_numbers().get(k))) {
                            contactsList.remove(i);
                            break outer;
                        }
                    }
                }
            }
            contactsList.add(new ContactList(phoneContactsModel.getName(), phoneContactsModel.getPhoneNumber(), phoneContactsModel.getEmail()));
        } else {
            for (int i = 0; i < contactsList.size(); i++) {
                ContactList contactList = contactsList.get(i);
                outer:
                for (int j = 0; j < phoneContactsModel.getPhoneNumber().size(); j++) {
                    for (int k = 0; k < contactList.getPhone_numbers().size(); k++) {
                        if (phoneContactsModel.getPhoneNumber().get(j).equalsIgnoreCase(contactList.getPhone_numbers().get(k))) {
                            contactsList.remove(i);
                            break outer;
                        }
                    }
                }
            }
        }
        phoneContactsModel.setSelected(isChecked);
        importContactsAdapter.notifyItemChanged(position);
    }

    //    Method to save imported phone numbers to server....
    private void reqExportContacts(String json) {
        ((MainActivity) getActivity()).progressDialog.show();
        callExportContacts = Api.WEB_SERVICE.exportContacts(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), json);
        callExportContacts.enqueue(new Callback<ExportContactsMainObject>() {
            public void onResponse(Call<ExportContactsMainObject> call, Response<ExportContactsMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                switch (response.body().getStatus()) {
                    case 1:
                        contactsList.clear();
                        ((MainActivity) getActivity()).showMessageDialog("success", getResources().getString(R.string.contacts_imported_successfully));
                        getActivity().onBackPressed();
                        EventBus.getDefault().post(new EventBusGetImportedContacts());
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

    public class ImportContactsClickHandler {

        public void onSearchClick(View view) {
            importContactsAdapter.getFilter().filter(
                    importContactsBinding.editTextSearch.getText().toString());
            List<String> searchIndex = Arrays.asList(searchSuggestion.split(","));
            if (!searchIndex.contains(importContactsBinding.editTextSearch.getText().toString())) {
                searchSuggestion = searchSuggestion + "," + importContactsBinding.editTextSearch.getText().toString();
                List<String> suggestionArray = Arrays.asList(searchSuggestion.split(","));
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, suggestionArray);
                importContactsBinding.editTextSearch.setAdapter(arrayAdapter);
            }
            LnqApplication.getInstance().editor.putString("search_suggestion", searchSuggestion).apply();
        }

        public void onCloseClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), importContactsBinding.getRoot());
            importContactsBinding.editTextSearch.setText("");
        }

        public void onSelectAllClick(View view) {
            importContactsBinding.imageViewDeSelectAllContacts.setVisibility(View.VISIBLE);
            importContactsBinding.imageViewSelectAllContacts.setVisibility(View.INVISIBLE);
            if (phoneContactsModelList.size() > 0) {
                contactsList.clear();
                for (int i = 0; i < phoneContactsModelList.size(); i++) {
                    contactsList.add(new ContactList(phoneContactsModelList.get(i).getName(), phoneContactsModelList.get(i).getPhoneNumber(), phoneContactsModelList.get(i).getEmail()));
                    phoneContactsModelList.get(i).setSelected(true);
                    importContactsAdapter.notifyItemChanged(i);
                }
            }
        }

        public void onDeSelectAllClick(View view) {
            importContactsBinding.imageViewSelectAllContacts.setVisibility(View.VISIBLE);
            importContactsBinding.imageViewDeSelectAllContacts.setVisibility(View.INVISIBLE);
            if (phoneContactsModelList.size() > 0) {
                contactsList.clear();
                for (int i = 0; i < phoneContactsModelList.size(); i++) {
                    phoneContactsModelList.get(i).setSelected(false);
                    importContactsAdapter.notifyItemChanged(i);
                }
            }
        }

        public void onStartClick(View view) {

        }

        public void onImportClick(View view) {
            if (contactsList.size() > 0) {
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_IMPORT_USERS, true, null);
            }
        }

        public void onQrCodeClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), importContactsBinding.getRoot());
//            if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
//            } else {
//                ((MainActivity) getActivity()).fnRequestCameraPermission(8);
//            }
        }

        public void onBackClick(View view) {
            getActivity().onBackPressed();
        }

    }

}