package lnq.com.lnq.model.event_bus_models;

public class EventBusCameraPermission {
    private boolean isAllowed;

    public EventBusCameraPermission(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public void setAllowed(boolean allowed) {
        isAllowed = allowed;
    }
}
