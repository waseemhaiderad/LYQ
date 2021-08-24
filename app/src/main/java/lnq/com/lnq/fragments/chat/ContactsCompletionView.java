package lnq.com.lnq.fragments.chat;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;

import com.tokenautocomplete.TokenCompleteTextView;

import lnq.com.lnq.R;
import lnq.com.lnq.model.gson_converter_models.searchuser.SearchContactByName;

public class ContactsCompletionView extends TokenCompleteTextView<SearchContactByName> {

    InputConnection testAccessibleInputConnection;
    SearchContactByName personToIgnore;

    public ContactsCompletionView(Context context) {
        super(context);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(SearchContactByName person) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        token.setText(person.getUserFname() + "  ");
        return token;
    }

    @Override
    protected SearchContactByName defaultObject(String completionText) {
        SearchContactByName searchContactByName = new SearchContactByName();
        searchContactByName.setUserFname(completionText);
        return searchContactByName;
    }

    //Methods for testing
    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        testAccessibleInputConnection = super.onCreateInputConnection(outAttrs);
        return testAccessibleInputConnection;
    }

    void setPersonToIgnore(SearchContactByName person) {
        this.personToIgnore = person;
    }

    @Override
    public boolean shouldIgnoreToken(SearchContactByName token) {
        return personToIgnore != null && personToIgnore.getUserFname().equals(token.getUserFname());
    }

    public void simulateSelectingPersonFromList(SearchContactByName person) {
        convertSelectionToString(person);
        replaceText(currentCompletionText());
    }
}