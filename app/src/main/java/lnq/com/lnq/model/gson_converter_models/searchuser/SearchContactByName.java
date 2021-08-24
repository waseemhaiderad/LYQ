
package lnq.com.lnq.model.gson_converter_models.searchuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchContactByName {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_fname")
    @Expose
    private String userFname;
    @SerializedName("user_lname")
    @Expose
    private String userLname;
    @SerializedName("user_avatar")
    @Expose
    private String userAvatar;
    @SerializedName("user_phone")
    @Expose
    private String userPhone;
    @SerializedName("thread_id")
    @Expose
    private String threadId;
    @SerializedName("is_favorite")
    @Expose
    private String isFavorite;
    @SerializedName("is_blocked")
    @Expose
    private String isBlocked;
    @SerializedName("profile_id")
    @Expose
    private String profile_id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }
}
