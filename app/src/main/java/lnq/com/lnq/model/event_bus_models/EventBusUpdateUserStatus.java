package lnq.com.lnq.model.event_bus_models;

public class EventBusUpdateUserStatus {

    private String userId, userStatus;
    private boolean isFromFcm;

    public EventBusUpdateUserStatus(String userId, String userStatus, boolean isFromFcm) {
        this.userId = userId;
        this.userStatus = userStatus;
        this.isFromFcm = isFromFcm;
    }

    public boolean isFromFcm() {
        return isFromFcm;
    }

    public void setFromFcm(boolean fromFcm) {
        isFromFcm = fromFcm;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
