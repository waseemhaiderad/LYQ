package lnq.com.lnq.model.gson_converter_models.chat;

public class UnMuteGroupChatMainObject {
    private Integer status;
    private String message;
    private String group_thread_id;
    private String user_id_mute_by;

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
        return group_thread_id;
    }

    public void setGroup_thread_id(String group_thread_id) {
        this.group_thread_id = group_thread_id;
    }

    public String getUser_id_mute_by() {
        return user_id_mute_by;
    }

    public void setUser_id_mute_by(String user_id_mute_by) {
        this.user_id_mute_by = user_id_mute_by;
    }
}
