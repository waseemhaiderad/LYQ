package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;

public class EventBusAddContactGroupMembers {
    int pos;
    String groupId;
    List<UserConnectionsData> existingMembersList;

    public EventBusAddContactGroupMembers(int pos, List<UserConnectionsData> existingMembersList, String groupId) {
        this.pos = pos;
        this.groupId = groupId;
        this.existingMembersList = existingMembersList;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public List<UserConnectionsData> getExistingMembersList() {
        return existingMembersList;
    }

    public void setExistingMembersList(List<UserConnectionsData> existingMembersList) {
        this.existingMembersList = existingMembersList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
