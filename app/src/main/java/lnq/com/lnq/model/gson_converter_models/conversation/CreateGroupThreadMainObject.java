package lnq.com.lnq.model.gson_converter_models.conversation;

public class CreateGroupThreadMainObject {
    private Integer status;
    private String message;
    private String participant_ids;
    private String group_name;
    private String thread_id;

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParticipant_ids() {
        return participant_ids;
    }

    public void setParticipant_ids(String participant_ids) {
        this.participant_ids = participant_ids;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
}
