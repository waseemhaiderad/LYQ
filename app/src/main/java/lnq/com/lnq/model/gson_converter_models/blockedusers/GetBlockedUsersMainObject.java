
package lnq.com.lnq.model.gson_converter_models.blockedusers;

import java.util.List;

public class GetBlockedUsersMainObject {

    private Integer status;
    private String message;
    private List<GetBlockedUserList> listBlockedUsers = null;

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

    public List<GetBlockedUserList> getListBlockedUsers() {
        return listBlockedUsers;
    }

    public void setListBlockedUsers(List<GetBlockedUserList> listBlockedUsers) {
        this.listBlockedUsers = listBlockedUsers;
    }
}

