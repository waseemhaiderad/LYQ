package lnq.com.lnq.model.gson_converter_models.conversation;

public class CreateGroupNameMainObject {
    private Integer status;
    private String message;
    private String thread_id;
    private String group_name;

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

    public String getGroup_thread_id() {
        return thread_id;
    }

    public void setGroup_thread_id(String group_thread_id) {
        this.thread_id = group_thread_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
}
