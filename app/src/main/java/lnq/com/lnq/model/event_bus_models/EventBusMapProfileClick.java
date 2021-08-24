package lnq.com.lnq.model.event_bus_models;

public class EventBusMapProfileClick {
    private int clickType;

    public EventBusMapProfileClick(int clickType) {
        this.clickType = clickType;
    }

    public int getClickType() {
        return clickType;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }
}
