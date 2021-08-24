package lnq.com.lnq.model.gson_converter_models.chat;

public class UnMuteChatMainObject {
    private Integer status;
    private String message;
    private String user_id_mute_by;
    private String user_id_mute_for;

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

    public String getUser_id_mute_by() {
        return user_id_mute_by;
    }

    public void setUser_id_mute_by(String user_id_mute_by) {
        this.user_id_mute_by = user_id_mute_by;
    }

    public String getUser_id_mute_for() {
        return user_id_mute_for;
    }

    public void setUser_id_mute_for(String user_id_mute_for) {
        this.user_id_mute_for = user_id_mute_for;
    }
}
