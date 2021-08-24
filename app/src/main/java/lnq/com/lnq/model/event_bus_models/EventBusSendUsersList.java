package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;

public class EventBusSendUsersList {
    private List<UpdateLocationData> updateLocationDataList;
    private int index;

    public EventBusSendUsersList(List<UpdateLocationData> updateLocationDataList, int index) {
        this.updateLocationDataList = updateLocationDataList;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<UpdateLocationData> getUpdateLocationDataList() {
        return updateLocationDataList;
    }

    public void setUpdateLocationDataList(List<UpdateLocationData> updateLocationDataList) {
        this.updateLocationDataList = updateLocationDataList;
    }
}
