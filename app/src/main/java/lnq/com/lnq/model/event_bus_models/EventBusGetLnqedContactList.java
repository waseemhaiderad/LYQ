package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;

public class EventBusGetLnqedContactList {
    private List<UserConnectionsData> userContactsDataList;

    public EventBusGetLnqedContactList(List<UserConnectionsData> userContactsDataList) {
        this.userContactsDataList = userContactsDataList;
    }

    public List<UserConnectionsData> getGetLnqContactList() {
        return userContactsDataList;
    }

    public void setGetLnqContactList(List<UserConnectionsData> userContactsDataList) {
        this.userContactsDataList = userContactsDataList;
    }
}
