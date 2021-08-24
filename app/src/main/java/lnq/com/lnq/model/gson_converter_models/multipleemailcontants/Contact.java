package lnq.com.lnq.model.gson_converter_models.multipleemailcontants;

import android.graphics.Bitmap;

import java.util.HashMap;

public class Contact {
    String id = "";
    String displayName = "";
//    String dateOfBirth = "";
//    String dateOfAnniversary = "";
//    String nickName = "";
    String note = "";
    Bitmap image = null;

    HashMap<Integer, String> emails;
    HashMap<Integer, String> phones;
//    HashMap<Integer, Address> addresses;
//    HashMap<Integer, Organization> organizations;
//    HashMap<Integer, String> im;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getDisplayName() {
        return displayName;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


//    public String getDateOfBirth() {
//        return dateOfBirth;
//    }
//
//
//    public void setDateOfBirth(String dateOfBirth) {
//        this.dateOfBirth = dateOfBirth;
//    }
//
//
//    public String getDateOfAnniversary() {
//        return dateOfAnniversary;
//    }
//
//
//    public void setDateOfAnniversary(String dateOfAnniversary) {
//        this.dateOfAnniversary = dateOfAnniversary;
//    }
//
//
//    public String getNickName() {
//        return nickName;
//    }
//
//
//    public void setNickName(String nickName) {
//        this.nickName = nickName;
//    }


    public String getNote() {
        return note;
    }


    public void setNote(String note) {
        this.note = note;
    }


    public Bitmap getImage() {
        return image;
    }


    public void setImage(Bitmap image) {
        this.image = image;
    }


    public HashMap<Integer, String> getEmails() {
        return emails;
    }


    public void setEmails(HashMap<Integer, String> emails) {
        this.emails = emails;
    }


    public HashMap<Integer, String> getPhones() {
        return phones;
    }


    public void setPhones(HashMap<Integer, String> phones) {
        this.phones = phones;
    }


//    public HashMap<Integer, Address> getAddresses() {
//        return addresses;
//    }
//
//
//    public void setAddresses(HashMap<Integer, Address> addresses) {
//        this.addresses = addresses;
//    }
//
//
//    public HashMap<Integer, Organization> getOrganizations() {
//        return organizations;
//    }
//
//
//    public void setOrganizations(HashMap<Integer, Organization> organizations) {
//        this.organizations = organizations;
//    }


//    public HashMap<Integer, String> getIm() {
//        return im;
//    }
//
//
//    public void setIm(HashMap<Integer, String> im) {
//        this.im = im;
//    }


    /******************************************************************************************/
//    static class Address {
//
//        private String postBox = "";
//        private String street = "";
//        private String city = "";
//        private String state = "";
//        private String postalCode = "";
//        private String country = "";
//        private String neighborhood = "";
//
//        public String getPostBox() {
//            return postBox;
//        }
//
//        public void setPostBox(String postBox) {
//            this.postBox = postBox;
//        }
//
//        public String getStreet() {
//            return street;
//        }
//
//        public void setStreet(String street) {
//            this.street = street;
//        }
//
//        public String getCity() {
//            return city;
//        }
//
//        public void setCity(String city) {
//            this.city = city;
//        }
//
//        public String getState() {
//            return state;
//        }
//
//        public void setState(String state) {
//            this.state = state;
//        }
//
//        public String getPostalCode() {
//            return postalCode;
//        }
//
//        public void setPostalCode(String postalCode) {
//            this.postalCode = postalCode;
//        }
//
//        public String getCountry() {
//            return country;
//        }
//
//        public void setCountry(String country) {
//            this.country = country;
//        }
//
//        public String getNeighborhood() {
//            return neighborhood;
//        }
//
//        public void setNeighborhood(String neighborhood) {
//            this.neighborhood = neighborhood;
//        }
//
//        @Override
//        public String toString() {
//            return "Address [postBox=" + postBox + "\n street=" + street
//                    + "\n city=" + city + "\n state=" + state + "\n postalCode="
//                    + postalCode + "\n country=" + country + "\n neighborhood="
//                    + neighborhood + "]";
//        }
//    }
//
//    /**********************************/
//    static class Organization {
//        private String company = "";
//        private String jobTitle = "";
//
//        public String getCompany() {
//            return company;
//        }
//
//        public void setCompany(String company) {
//            this.company = company;
//        }
//
//        public String getJobTitle() {
//            return jobTitle;
//        }
//
//        public void setJobTitle(String jobTitle) {
//            this.jobTitle = jobTitle;
//        }
//
//
//        @Override
//        public String toString() {
//            return "Organization [company=" + company + "\n jobTitle="
//                    + jobTitle + "]";
//        }
//    }

    /**********************************/
    public static class Email_TYPE {
        // Email Type
        public static final int HOME = 1;
        public static final int WORK = 2;
        public static final int OTHER = 3;
        public static final int MOBILE = 4;
    }

    /**********************************/
    public static class PHONE_TYPE {
        // / Phone Type
        public static final int HOME = 1;
        public static final int MOBILE = 2;
        public static final int WORK = 3;
        public static final int FAX_WORK = 4;
        public static final int FAX_HOME = 5;
        public static final int PAGER = 6;
        public static final int OTHER = 7;
    }

    /**********************************/
    public static class ADDRESS_TYPE {
        // / Address Type
        public static final int HOME = 1;
        public static final int WORK = 2;
        public static final int OTHER = 3;
    }

    /**********************************/
    public static class ORGANIZATION_TYPE {
        // / Organization Type
        public static final int WORK = 2;
        public static final int OTHER = 3;
    }


    /**********************************/
    public static class IM_TYPE {
        public static final int CUSTOM = -1;
        public static final int AIM = 0;
        public static final int MSN = 1;
        public static final int YAHOO = 2;
        public static final int SKYPE = 3;
        public static final int QQ = 4;
        public static final int GOOGLE_TALK = 5;
        public static final int ICQ = 6;
        public static final int JABBER = 7;
        public static final int NETMEETING = 8;
    }


//    @Override
//    public String toString() {
//        return "Contact [id=" + id + "\n displayName=" + displayName
//                + "\n dateOfBirth=" + dateOfBirth + "\n dateOfAnniversary="
//                + dateOfAnniversary + "\n nickName=" + nickName + "\n note="
//                + note + "\n image=" + image + "\n emails=" + emails
//                + "\n phones=" + phones + "\n addresses=" + addresses
//                + "\n organizations=" + organizations + "\n im=" + im + "]";
//    }

}
