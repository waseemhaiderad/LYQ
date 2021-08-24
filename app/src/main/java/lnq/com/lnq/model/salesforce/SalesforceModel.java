package lnq.com.lnq.model.salesforce;

public class SalesforceModel {

    private String firstname;
    private String lastname;
    private String mobilephone;
    private String email;
    private SalesforceAttributes attributes;

    public SalesforceModel(String firstname, String lastname, String mobilephone, String email, SalesforceAttributes attributes) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobilephone = mobilephone;
        this.email = email;
        this.attributes = attributes;
    }

    public String getfirstname() {
        return firstname;
    }

    public void setfirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getmobilephone() {
        return mobilephone;
    }

    public void setmobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public SalesforceAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(SalesforceAttributes attributes) {
        this.attributes = attributes;
    }
}
