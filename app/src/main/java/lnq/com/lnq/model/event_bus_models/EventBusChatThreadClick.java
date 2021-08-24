package lnq.com.lnq.model.event_bus_models;

public class EventBusChatThreadClick {
    private int mPos;
    private String profileId;

    public EventBusChatThreadClick(int mPos) {
        this.mPos = mPos;
//        this.profileId = profileId;
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
}
