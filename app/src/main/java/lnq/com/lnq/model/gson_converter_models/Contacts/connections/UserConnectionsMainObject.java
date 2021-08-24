package lnq.com.lnq.model.gson_converter_models.Contacts.connections;

import java.util.List;

public class UserConnectionsMainObject {

    private Integer status;
    private String message;
    private List<UserConnections> userContacts = null;

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

    public List<UserConnections> getUserContacts() {
        return userContacts;
    }

    public void setUserContacts(List<UserConnections> userContacts) {
        this.userContacts = userContacts;
    }

}