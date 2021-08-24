
package lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts;

import java.util.List;

public class Contacts {
    private List<ContactList> contacts = null;

    public Contacts(List<ContactList> contact_list) {
        this.contacts = contact_list;
    }

    public List<ContactList> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactList> contacts) {
        this.contacts = contacts;
    }

}
