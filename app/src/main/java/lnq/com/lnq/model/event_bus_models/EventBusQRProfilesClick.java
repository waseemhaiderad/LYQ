package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;

public class EventBusQRProfilesClick {
   int position;
   List<MultiProfileRoomModel> multiProfileRoomModels;

    public EventBusQRProfilesClick(int position, List<MultiProfileRoomModel> multiProfileRoomModels) {
        this.position = position;
        this.multiProfileRoomModels = multiProfileRoomModels;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<MultiProfileRoomModel> getMultiProfileRoomModels() {
        return multiProfileRoomModels;
    }

    public void setMultiProfileRoomModels(List<MultiProfileRoomModel> multiProfileRoomModels) {
        this.multiProfileRoomModels = multiProfileRoomModels;
    }
}
