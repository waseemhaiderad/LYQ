package lnq.com.lnq.model.event_bus_models;

public class EventBusRemoveMemberFromGroup {
    int pos;
    String recevierId;
    String recevierProfileId;
    String groupId;

    public EventBusRemoveMemberFromGroup(int pos, String recevierId, String recevierProfileId, String mGroupId) {
        this.pos = pos;
        this.recevierId = recevierId;
        this.groupId = mGroupId;
        this.recevierProfileId = recevierProfileId;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getRecevierId() {
        return recevierId;
    }

    public void setRecevierId(String recevierId) {
        this.recevierId = recevierId;
    }

    public String getRecevierProfileId() {
        return recevierProfileId;
    }

    public void setRecevierProfileId(String recevierProfileId) {
        this.recevierProfileId = recevierProfileId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
