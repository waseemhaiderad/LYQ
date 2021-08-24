package lnq.com.lnq.model.event_bus_models;

public class EventBusGroupProfileClick {
    private int mPos;
    private String profileId;
    private String userId;

    public EventBusGroupProfileClick(int mPos, String profileId, String userId) {
        this.mPos = mPos;
        this.profileId = profileId;
        this.userId = userId;
    }

    public int getmPos() {
        return mPos;
    }

    public void setmPos(int mPos) {
        this.mPos = mPos;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
