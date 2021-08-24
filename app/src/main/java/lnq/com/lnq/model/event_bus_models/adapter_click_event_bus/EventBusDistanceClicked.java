package lnq.com.lnq.model.event_bus_models.adapter_click_event_bus;

public class EventBusDistanceClicked {
    private int position;
    private String profileId;

    public EventBusDistanceClicked(int position, String profileId) {
        this.position = position;
        this.profileId = profileId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
