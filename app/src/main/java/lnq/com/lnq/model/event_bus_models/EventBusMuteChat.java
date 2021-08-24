package lnq.com.lnq.model.event_bus_models;

public class EventBusMuteChat {
    private int mPos;
    private int muteType;

    public int getMuteType() {
        return muteType;
    }

    public void setMuteType(int muteType) {
        this.muteType = muteType;
    }

    public EventBusMuteChat(int mPos, int muteType) {
        this.mPos = mPos;
        this.muteType = muteType;
    }

    public int getmPos() {
        return mPos;
    }

    public void setmPos(int mPos) {
        this.mPos = mPos;
    }
}
