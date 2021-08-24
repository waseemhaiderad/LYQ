package lnq.com.lnq.model;

import android.graphics.Bitmap;

import java.util.List;

public class PhoneContactsModel {

    private String id;
    private String name;
    private List<String> phoneNumber;
    private List<String> email;
    private Bitmap image;
    private boolean isSelected;

    public PhoneContactsModel(String id, String name, List<String> phoneNumber, List<String> email, Bitmap image, boolean isSelected) {
        setId(id);
        setName(name);
        setImage(image);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setSelected(isSelected);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
