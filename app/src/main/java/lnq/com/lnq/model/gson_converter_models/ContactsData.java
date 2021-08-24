package lnq.com.lnq.model.gson_converter_models;

public class ContactsData {
    String ContactName;
    String Des;
    String min;
    String mImage;
    String mImageMulti;

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getDes() {
        return Des;
    }

    public void setDes(String des) {
        Des = des;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmImageMulti() {
        return mImageMulti;
    }

    public void setmImageMulti(String mImageMulti) {
        this.mImageMulti = mImageMulti;
    }


    public ContactsData(String contactName, String des, String min, String mImage, String mImageMulti) {
        ContactName = contactName;
        Des = des;
        this.min = min;
        this.mImage = mImage;
        this.mImageMulti = mImageMulti;
    }
}

