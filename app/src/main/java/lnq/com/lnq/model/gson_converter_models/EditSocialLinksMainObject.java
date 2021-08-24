package lnq.com.lnq.model.gson_converter_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditSocialLinksMainObject {

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("social_links")
    @Expose
    private String social_links;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSocial_links() {
        return social_links;
    }

    public void setSocial_links(String social_links) {
        this.social_links = social_links;
    }
}
