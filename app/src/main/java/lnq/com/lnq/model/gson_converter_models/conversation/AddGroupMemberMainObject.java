package lnq.com.lnq.model.gson_converter_models.conversation;

public class AddGroupMemberMainObject {
    private Integer status;
    private String message;
    private String thread_id;
    private AddMemberToGroupChat addMemberToGroupChat;

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

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public AddMemberToGroupChat getAddMemberToGroupChat() {
        return addMemberToGroupChat;
    }

    public void setAddMemberToGroupChat(AddMemberToGroupChat addMemberToGroupChat) {
        this.addMemberToGroupChat = addMemberToGroupChat;
    }
}
