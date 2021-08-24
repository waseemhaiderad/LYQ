package lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts;

import java.util.List;

public class SelectedExportContact {

    private String contactId, name, note, address, company, birthday, job, image;
    private List<String> number, email;

    public SelectedExportContact(String contactId, String name, List<String> number, List<String> email, String note, String address, String company, String birthday, String job, String image) {
        this.contactId = contactId;
        this.name = name;
        this.number = number;
        this.email = email;
        this.note = note;
        this.address = address;
        this.company = company;
        this.birthday = birthday;
        this.job = job;
        this.image = image;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNumber() {
        return number;
    }

    public void setNumber(List<String> number) {
        this.number = number;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
