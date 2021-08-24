package lnq.com.lnq.model.event_bus_models;

public class EventBusOpenMentionedUser {
    String name;

    public EventBusOpenMentionedUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
