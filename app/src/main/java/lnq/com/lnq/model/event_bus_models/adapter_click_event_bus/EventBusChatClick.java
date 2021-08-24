package lnq.com.lnq.model.event_bus_models.adapter_click_event_bus;

public class EventBusChatClick {

    private int position;
    private String user_ID;
    private String recevierProfileId;

    public EventBusChatClick(int position, String user_ID, String recevierProfileId) {
        this.position = position;
        this.user_ID = user_ID;
        this.recevierProfileId = recevierProfileId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getRecevierProfileId() {
        return recevierProfileId;
    }

    public void setRecevierProfileId(String recevierProfileId) {
        this.recevierProfileId = recevierProfileId;
    }
}
