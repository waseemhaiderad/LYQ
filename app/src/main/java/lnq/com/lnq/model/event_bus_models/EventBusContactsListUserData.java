package lnq.com.lnq.model.event_bus_models;

public class EventBusContactsListUserData {
    private String phone;
    private String fName;
    public EventBusContactsListUserData(String phone, String secondryPhone, String email, String secondryEmail, String socialMedia, String fName) {
        this.phone = phone;
        this.secondryPhone = secondryPhone;
        this.email = email;
        this.secondryEmail = secondryEmail;
        this.socialMedia = socialMedia;
        this.fName = fName;
    }

    private String secondryPhone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getSecondryPhone() {
        return secondryPhone;
    }

    public void setSecondryPhone(String secondryPhone) {
        this.secondryPhone = secondryPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecondryEmail() {
        return secondryEmail;
    }

    public void setSecondryEmail(String secondryEmail) {
        this.secondryEmail = secondryEmail;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    private String email;
    private String secondryEmail;
    private String socialMedia;

}
