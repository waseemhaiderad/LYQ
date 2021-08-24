package lnq.com.lnq.model.event_bus_models;

public class EventBusSharedProfileItemClick {
    int position;
    String userId;
    String userProfileId;

    public EventBusSharedProfileItemClick(int position, String userId, String userProfileId) {
        this.position = position;
        this.userId = userId;
        this.userProfileId = userProfileId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
