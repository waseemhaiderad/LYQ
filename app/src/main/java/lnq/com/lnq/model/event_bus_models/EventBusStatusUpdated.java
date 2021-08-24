package lnq.com.lnq.model.event_bus_models;

public class EventBusStatusUpdated {

    private String statusMessage;

    public EventBusStatusUpdated(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
