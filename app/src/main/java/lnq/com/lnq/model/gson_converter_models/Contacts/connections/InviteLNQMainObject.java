package lnq.com.lnq.model.gson_converter_models.Contacts.connections;

import com.google.gson.annotations.SerializedName;

public class InviteLNQMainObject {

    private String user_id;
    private String referral_fname;
    private String referral_lname;
    private String referral_email;
    private String referral_phone;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReferral_fname() {
        return referral_fname;
    }

    public void setReferral_fname(String referral_fname) {
        this.referral_fname = referral_fname;
    }

    public String getReferral_lname() {
        return referral_lname;
    }

    public void setReferral_lname(String referral_lname) {
        this.referral_lname = referral_lname;
    }

    public String getReferral_email() {
        return referral_email;
    }

    public void setReferral_email(String referral_email) {
        this.referral_email = referral_email;
    }

    public String getReferral_phone() {
        return referral_phone;
    }

    public void setReferral_phone(String referral_phone) {
        this.referral_phone = referral_phone;
    }
}
