package lnq.com.lnq.model.event_bus_models;

public class EventBusConversationProfileClick {
    private int mPos;

    public EventBusConversationProfileClick(int mPos) {
        this.mPos = mPos;
    }

    public int getmPos() {
        return mPos;
    }

    public void setmPos(int mPos) {
        this.mPos = mPos;
    }
}
