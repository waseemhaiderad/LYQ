package lnq.com.lnq.model.event_bus_models;

import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;

public class EventBusRefreshUserSecondaryProfileData {
    private MultiProfileRoomModel multiProfileRoomModel;

    public EventBusRefreshUserSecondaryProfileData(MultiProfileRoomModel multiProfileRoomModel) {
        this.multiProfileRoomModel = multiProfileRoomModel;
    }

    public MultiProfileRoomModel getMultiProfileRoomModel() {
        return multiProfileRoomModel;
    }

    public void setMultiProfileRoomModel(MultiProfileRoomModel multiProfileRoomModel) {
        this.multiProfileRoomModel = multiProfileRoomModel;
    }
}
