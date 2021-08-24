
package lnq.com.lnq.model.gson_converter_models.pushnotifications;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushNotificationData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("device_tocken")
    @Expose
    private String deviceTocken;
    @SerializedName("push_notification")
    @Expose
    private String pushNotification;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTocken() {
        return deviceTocken;
    }

    public void setDeviceTocken(String deviceTocken) {
        this.deviceTocken = deviceTocken;
    }

    public String getPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(String pushNotification) {
        this.pushNotification = pushNotification;
    }

}
