package lnq.com.lnq.model.event_bus_models;

public class EventBussFailedMsg {
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public EventBussFailedMsg(int position) {
        this.position = position;
    }


}
