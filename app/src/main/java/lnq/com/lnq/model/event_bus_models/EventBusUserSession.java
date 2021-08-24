package lnq.com.lnq.model.event_bus_models;

public class EventBusUserSession {

    private String action_type;

    public EventBusUserSession(String action_type){
        this.action_type = action_type;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }
}
