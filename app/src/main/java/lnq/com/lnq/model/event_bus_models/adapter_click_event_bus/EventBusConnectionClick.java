package lnq.com.lnq.model.event_bus_models.adapter_click_event_bus;

public class EventBusConnectionClick {
    private int mPos;
    private String profileId;

    public EventBusConnectionClick(int mPos, String profileId) {
        this.mPos = mPos;
        this.profileId = profileId;
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
