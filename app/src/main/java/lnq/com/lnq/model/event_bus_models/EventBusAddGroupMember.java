package lnq.com.lnq.model.event_bus_models;

public class EventBusAddGroupMember {

    int position;
    String user_id;
    String profile_id;

    public EventBusAddGroupMember(int position, String user_id, String profile_id) {
        this.position = position;
        this.user_id = user_id;
        this.profile_id = profile_id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }
}
