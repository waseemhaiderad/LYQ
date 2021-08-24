package lnq.com.lnq.model.event_bus_models;

public class EventBusGroupMemberFullProfile {
  int position;
  String profileId;
  String userId;

    public EventBusGroupMemberFullProfile(int position, String profileId, String userId) {
        this.position = position;
        this.profileId = profileId;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
