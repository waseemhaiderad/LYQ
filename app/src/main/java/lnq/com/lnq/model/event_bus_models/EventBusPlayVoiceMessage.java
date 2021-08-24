package lnq.com.lnq.model.event_bus_models;

public class EventBusPlayVoiceMessage {
    String type;
    String play;

    public EventBusPlayVoiceMessage(String type, String play) {
        this.type = type;
        this.play = play;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }
}
