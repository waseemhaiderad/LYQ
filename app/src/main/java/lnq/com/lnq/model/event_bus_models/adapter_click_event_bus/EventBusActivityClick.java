package lnq.com.lnq.model.event_bus_models.adapter_click_event_bus;

public class EventBusActivityClick {

    private String clickType;
    private int position;

    public EventBusActivityClick(int position, String clickType) {
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
