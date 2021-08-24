package lnq.com.lnq.model.event_bus_models;

public class EventBusDisableQRButtons {
    String type;

    public EventBusDisableQRButtons(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
