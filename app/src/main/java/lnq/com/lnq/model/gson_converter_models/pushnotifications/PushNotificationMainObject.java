
package lnq.com.lnq.model.gson_converter_models.pushnotifications;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushNotificationMainObject {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("setPushNotifications")
    @Expose
    private PushNotificationData setPushNotifications;
    @SerializedName("logout_from_old")
    @Expose
    private Integer logoutFromOld;
    @Expose
    @SerializedName("frozen_date")
    private String frozenDate;

    public String getFrozenDate() {
        return frozenDate;
    }

    public void setFrozenDate(String frozenDate) {
        this.frozenDate = frozenDate;
    }

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

    public PushNotificationData getSetPushNotifications() {
        return setPushNotifications;
    }

    public void setSetPushNotifications(PushNotificationData setPushNotifications) {
        this.setPushNotifications = setPushNotifications;
    }

    public Integer getLogoutFromOld() {
        return logoutFromOld;
    }

    public void setLogoutFromOld(Integer logoutFromOld) {
        this.logoutFromOld = logoutFromOld;
    }

}
