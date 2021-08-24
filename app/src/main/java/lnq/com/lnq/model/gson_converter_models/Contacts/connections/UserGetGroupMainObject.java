package lnq.com.lnq.model.gson_converter_models.Contacts.connections;

import java.util.List;

public class UserGetGroupMainObject {
    private Integer status;
    private String message;
    private List<CreateUserGroup> getUserGroup;

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

    public List<CreateUserGroup> getGetUserGroup() {
        return getUserGroup;
    }

    public void setGetUserGroup(List<CreateUserGroup> getUserGroup) {
        this.getUserGroup = getUserGroup;
    }
}
