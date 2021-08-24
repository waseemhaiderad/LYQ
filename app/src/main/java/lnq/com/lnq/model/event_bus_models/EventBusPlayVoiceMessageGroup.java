package lnq.com.lnq.model.event_bus_models;

public class EventBusPlayVoiceMessageGroup {
    String voice_attachment;
    String play;

    public EventBusPlayVoiceMessageGroup(String voice_attachment, String play) {
        this.voice_attachment = voice_attachment;
        this.play = play;
    }

    public String getVoice_attachment() {
        return voice_attachment;
    }

    public void setVoice_attachment(String voice_attachment) {
        this.voice_attachment = voice_attachment;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }
}
