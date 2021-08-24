
package lnq.com.lnq.model.gson_converter_models.blockedusers;

import java.util.HashMap;
import java.util.Map;

public class GetBlockedUserList {

    private String user_id;
    private String profile_id;
    private String user_name;
    private String user_avatar;
    private String is_connection;
    private String is_favorite;
    private String is_blocked;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getIs_connection() {
        return is_connection;
    }

    public void setIs_connection(String is_connection) {
        this.is_connection = is_connection;
    }

    public String getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(String is_blocked) {
        this.is_blocked = is_blocked;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }
}
