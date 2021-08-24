package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;

public class EventBusAddToGroup {
    int pos;
    String groupId;
    List<UserConnectionsData> existingMembersList;
    String groupName;

    public EventBusAddToGroup(int pos, String groupId, List<UserConnectionsData> existingMembersList, String groupName) {
        this.pos = pos;
        this.groupId = groupId;
        this.existingMembersList = existingMembersList;
        this.groupName = groupName;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<UserConnectionsData> getExistingMembersList() {
        return existingMembersList;
    }

    public void setExistingMembersList(List<UserConnectionsData> existingMembersList) {
        this.existingMembersList = existingMembersList;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
