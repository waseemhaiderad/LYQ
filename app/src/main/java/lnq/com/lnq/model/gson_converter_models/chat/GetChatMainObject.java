package lnq.com.lnq.model.gson_converter_models.chat;

import java.util.List;

public class GetChatMainObject {

    private Integer status;
    private String message;
    private String thread_id;
    private Integer is_muted;
    private Integer is_blocked;
    private List<GetChatData> getChat = null;
    private List<GetChatData> getGroupChat = null;

    public List<GetChatData> getGetGroupChat() {
        return getGroupChat;
    }

    public void setGetGroupChat(List<GetChatData> getGroupChat) {
        this.getGroupChat = getGroupChat;
    }

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

    public List<GetChatData> getGetChat() {
        return getChat;
    }

    public void setGetChat(List<GetChatData> getChat) {
        this.getChat = getChat;
    }

    public Integer getIs_muted() {
        return is_muted;
    }

    public void setIs_muted(Integer is_muted) {
        this.is_muted = is_muted;
    }

    public Integer getIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(Integer is_blocked) {
        this.is_blocked = is_blocked;
    }
}
