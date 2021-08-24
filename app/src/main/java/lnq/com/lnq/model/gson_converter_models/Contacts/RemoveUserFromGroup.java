package lnq.com.lnq.model.gson_converter_models.Contacts;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;

public class RemoveUserFromGroup {
    private Integer status;
    private String message;
    private List<UserConnectionsData> members;

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

    public List<UserConnectionsData> getMembers() {
        return members;
    }

    public void setMembers(List<UserConnectionsData> members) {
        this.members = members;
    }
}
