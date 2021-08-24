package lnq.com.lnq.model.gson_converter_models.chat;

public class MuteChatMainObject {

    private Integer status;
    private String message;
    private String user_id_mute_by;
    private String user_id_mute_for;
    private int mute_type;
    private String mute_time;

    public String getMute_time() {
        return mute_time;
    }

    public void setMute_time(String mute_time) {
        this.mute_time = mute_time;
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

    public int getMute_type() {
        return mute_type;
    }

    public void setMute_type(int mute_type) {
        this.mute_type = mute_type;
    }
}
