package lnq.com.lnq.model.event_bus_models;

public class EventBusMapLnqClick {
    private int position;
    private String clickedType;

    public EventBusMapLnqClick(String clickedType, int position) {
        setClickedType(clickedType);
        this.position = position;
    }

    public String getClickedType() {
        return clickedType;
    }

    public void setClickedType(String clickedType) {
        this.clickedType = clickedType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
