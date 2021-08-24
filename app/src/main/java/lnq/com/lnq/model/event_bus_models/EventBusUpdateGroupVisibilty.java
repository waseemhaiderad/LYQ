package lnq.com.lnq.model.event_bus_models;

public class EventBusUpdateGroupVisibilty {
    int mPos;
    String type;
    String groupId;

    public EventBusUpdateGroupVisibilty(int mPos, String type, String groupId) {
        this.mPos = mPos;
        this.type = type;
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getmPos() {
        return mPos;
    }

    public void setmPos(int mPos) {
        this.mPos = mPos;
    }
}
