package lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts;

import java.util.List;

public class ExportContacts {
    private String id;
    private String name;
    private String image;
    private List<String> phone;
    private List<String> email;
    private String note;
    private String company;
    private String address;
    private String birthday;
    private String job;
    private String contactStatus;
    private String connectionStatus;
    private String isFavorite;
    private String isBlocked;
    private boolean isSelected;
    private String contactId;

    public ExportContacts(String id, String name, String image, List<String> phone, String contactStatus, String connectionStatus, boolean isSelected, String isFavorite, String isBlocked, List<String> email, String note, String address, String company, String birthday, String job, String contactId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.contactStatus = contactStatus;
        this.connectionStatus = connectionStatus;
        this.isSelected = isSelected;
        this.isFavorite = isFavorite;
        this.isBlocked = isBlocked;
        this.email = email;
        this.note = note;
        this.address = address;
        this.company = company;
        this.birthday = birthday;
        this.job = job;
        this.contactId = contactId;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public String getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(String contactStatus) {
        this.contactStatus = contactStatus;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
