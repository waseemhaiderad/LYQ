package lnq.com.lnq.model.event_bus_models;

public class EventBusRemoveSocial {

    private int position;

    public EventBusRemoveSocial(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
