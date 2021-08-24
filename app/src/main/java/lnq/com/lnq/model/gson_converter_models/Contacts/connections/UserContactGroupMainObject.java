package lnq.com.lnq.model.gson_converter_models.Contacts.connections;

public class UserContactGroupMainObject {
    private Integer status;
    private String message;
    private String visible_to;
    private String visible_at;
    private String receiver_ids;
    private String receiver_profile_ids;
    private CreateUserGroup createUserGroup;

    public UserContactGroupMainObject(String receiver_ids, String receiver_profile_ids) {
        this.receiver_ids = receiver_ids;
        this.receiver_profile_ids = receiver_profile_ids;
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

    public String getReceiver_ids() {
        return receiver_ids;
    }

    public void setReceiver_ids(String receiver_ids) {
        this.receiver_ids = receiver_ids;
    }

    public String getReceiver_profile_ids() {
        return receiver_profile_ids;
    }

    public void setReceiver_profile_ids(String receiver_profile_ids) {
        this.receiver_profile_ids = receiver_profile_ids;
    }

    public CreateUserGroup getCreateUserGroup() {
        return createUserGroup;
    }

    public void setCreateUserGroup(CreateUserGroup createUserGroup) {
        this.createUserGroup = createUserGroup;
    }

    public String getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(String visible_to) {
        this.visible_to = visible_to;
    }

    public String getVisible_at() {
        return visible_at;
    }

    public void setVisible_at(String visible_at) {
        this.visible_at = visible_at;
    }
}
