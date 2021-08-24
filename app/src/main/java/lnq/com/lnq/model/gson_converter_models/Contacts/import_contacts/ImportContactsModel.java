package lnq.com.lnq.model.gson_converter_models.Contacts.import_contacts;

public class ImportContactsModel {

    private String id;
    private String name;
    private String image;
    private String phone;
    private String contactStatus;
    private String email;

    public ImportContactsModel(String id, String name, String image, String phone, String contactStatus, String email) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.contactStatus = contactStatus;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(String contactStatus) {
        this.contactStatus = contactStatus;
    }
}
