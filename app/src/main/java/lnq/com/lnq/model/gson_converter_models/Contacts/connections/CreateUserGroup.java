package lnq.com.lnq.model.gson_converter_models.Contacts.connections;

import java.io.Serializable;
import java.util.List;

public class CreateUserGroup implements Serializable {
    private String id;
    private String group_name;
    private String visible_to;
    private String visible_at;
    private String user_id;
    private String profile_id;
    private String created_at;
    private String updated_at;
    private List<UserConnectionsData> members;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<UserConnectionsData> getMembers() {
        return members;
    }

    public void setMembers(List<UserConnectionsData> members) {
        this.members = members;
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
