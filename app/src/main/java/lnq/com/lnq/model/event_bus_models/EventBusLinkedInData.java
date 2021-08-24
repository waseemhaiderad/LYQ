package lnq.com.lnq.model.event_bus_models;

import com.AbdAllahAbdElFattah13.linkedinsdk.ui.LinkedInUser;

public class EventBusLinkedInData {
    private LinkedInUser linkedInUser;

    public EventBusLinkedInData(LinkedInUser linkedInUser) {
        this.linkedInUser = linkedInUser;
    }

    public LinkedInUser getLinkedInUser() {
        return linkedInUser;
    }

    public void setLinkedInUser(LinkedInUser linkedInUser) {
        this.linkedInUser = linkedInUser;
    }
}
