package lnq.com.lnq.model.event_bus_models;

public class EventBusGroupChatClicked {
    private int position;
    private String thread_id;
    private String participant_profile_ids;

    public EventBusGroupChatClicked(int position, String thread_id, String participant_profile_ids) {
        this.position = position;
        this.thread_id = thread_id;
        this.participant_profile_ids = participant_profile_ids;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getParticipant_profile_ids() {
        return participant_profile_ids;
    }

    public void setParticipant_profile_ids(String participant_profile_ids) {
        this.participant_profile_ids = participant_profile_ids;
    }
}
