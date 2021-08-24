package lnq.com.lnq.model.event_bus_models;

public class EventBusContacts {

    public boolean isContacts() {
        return contacts;
    }

    public void setContacts(boolean contacts) {
        this.contacts = contacts;
    }

    boolean contacts;
    public EventBusContacts(boolean contacts) {
        this.contacts = contacts;
    }


}
