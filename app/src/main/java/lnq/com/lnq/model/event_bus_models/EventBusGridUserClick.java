package lnq.com.lnq.model.event_bus_models;

public class EventBusGridUserClick {

    private int position;
    private String clickType;

    public EventBusGridUserClick(int position,String clickType) {
        this.position = position;
        this.clickType = clickType;
    }

    public String getClickType() {
        return clickType;
    }

    public void setClickType(String clickType) {
        this.clickType = clickType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
