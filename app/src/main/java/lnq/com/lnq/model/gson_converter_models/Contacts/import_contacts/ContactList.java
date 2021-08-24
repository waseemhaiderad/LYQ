
package lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts;


import java.util.List;

public class ContactList {

    private String name;
    private List<String> phone_numbers;
    private List<String> emails;

    public ContactList(String name, List<String> phone_numbers, List<String> emails) {
        this.name = name;
        this.phone_numbers = phone_numbers;
        this.emails = emails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhone_numbers() {
        return phone_numbers;
    }

    public void setPhone_numbers(List<String> phone_numbers) {
        this.phone_numbers = phone_numbers;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

}
