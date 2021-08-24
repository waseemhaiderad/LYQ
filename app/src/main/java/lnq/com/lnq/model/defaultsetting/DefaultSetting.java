package lnq.com.lnq.model.defaultsetting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultSetting {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("setDefaultSetting")
    @Expose
    private SetDefaultSetting setDefaultSetting;

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

    public SetDefaultSetting getSetDefaultSetting() {
        return setDefaultSetting;
    }

    public void setSetDefaultSetting(SetDefaultSetting setDefaultSetting) {
        this.setDefaultSetting = setDefaultSetting;
    }
}
