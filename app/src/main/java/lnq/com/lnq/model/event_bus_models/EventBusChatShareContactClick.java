package lnq.com.lnq.model.event_bus_models;

public class EventBusChatShareContactClick {
    String userId;
    String userProfileId;

    public EventBusChatShareContactClick(String userId, String userProfileId) {
        this.userId = userId;
        this.userProfileId = userProfileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }
}
