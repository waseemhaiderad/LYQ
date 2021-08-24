package lnq.com.lnq.model.event_bus_models;

public class EventBusBlockedUsersClicked {

    private  int position;

    public EventBusBlockedUsersClicked(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
