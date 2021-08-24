package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.userprofile.UserTasks;

public class EventBusEditExistingTask {
    int pos;

    public EventBusEditExistingTask(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
