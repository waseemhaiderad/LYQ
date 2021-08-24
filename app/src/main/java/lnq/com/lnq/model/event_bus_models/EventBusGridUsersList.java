package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;

public class EventBusGridUsersList {
    private List<UpdateLocationData> updateLocationDataList;

    public EventBusGridUsersList(List<UpdateLocationData> updateLocationDataList) {
        this.updateLocationDataList = updateLocationDataList;
    }

    public List<UpdateLocationData> getUpdateLocationDataList() {
        return updateLocationDataList;
    }

    public void setUpdateLocationDataList(List<UpdateLocationData> updateLocationDataList) {
        this.updateLocationDataList = updateLocationDataList;
    }
}
