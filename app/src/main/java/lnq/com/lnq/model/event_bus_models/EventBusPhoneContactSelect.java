package lnq.com.lnq.model.event_bus_models;

public class EventBusPhoneContactSelect {

    private boolean isSelected;
    private int id;
    private int position;

    public EventBusPhoneContactSelect(int position, int id, boolean isSelected) {
        this.position = position;
        this.id = id;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
