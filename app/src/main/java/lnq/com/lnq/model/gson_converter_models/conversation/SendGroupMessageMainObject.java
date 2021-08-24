package lnq.com.lnq.model.gson_converter_models.conversation;

public class SendGroupMessageMainObject {
    private Integer status;
    private String message;
    private String sender_id;
    private String thread_id;
    private String group_message;

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

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getGroup_message() {
        return group_message;
    }

    public void setGroup_message(String group_message) {
        this.group_message = group_message;
    }
}
