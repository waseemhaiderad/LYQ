package lnq.com.lnq.model.defaultsetting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetDefaultSetting {

    @SerializedName("home_default_view")
    @Expose
    private String homeDefaultView;
    @SerializedName("contact_default_view")
    @Expose
    private String contactDefaultView;

    public String getHomeDefaultView() {
        return homeDefaultView;
    }

    public void setHomeDefaultView(String homeDefaultView) {
        this.homeDefaultView = homeDefaultView;
    }

    public String getContactDefaultView() {
        return contactDefaultView;
    }

    public void setContactDefaultView(String contactDefaultView) {
        this.contactDefaultView = contactDefaultView;
    }
}
