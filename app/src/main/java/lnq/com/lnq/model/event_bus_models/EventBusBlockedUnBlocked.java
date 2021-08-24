package lnq.com.lnq.model.event_bus_models;

public class EventBusBlockedUnBlocked {

    private String isBlocked;

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public EventBusBlockedUnBlocked(String isBlocked){
        this.isBlocked = isBlocked;
    }
}
