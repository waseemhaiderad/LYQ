package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.SearchUserInMapModel;

public class EventBusUserMapSearch {
    int position;
//    String matchType;
//    String userName;
//    String userDistance;

    public EventBusUserMapSearch(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
