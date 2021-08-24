package lnq.com.lnq.model.gson_converter_models.qr_code;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InviteUserMainObject {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("doUserExist")
    @Expose
    private GetUserProfile getUserProfile;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GetUserProfile getGetUserProfile() {
        return getUserProfile;
    }

    public void setGetUserProfile(GetUserProfile getUserProfile) {
        this.getUserProfile = getUserProfile;
    }

}