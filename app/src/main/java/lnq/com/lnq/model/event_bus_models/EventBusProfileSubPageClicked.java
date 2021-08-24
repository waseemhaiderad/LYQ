package lnq.com.lnq.model.event_bus_models;

public class EventBusProfileSubPageClicked {
    private int position;

    public EventBusProfileSubPageClicked(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
