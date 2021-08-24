
package lnq.com.lnq.model.gson_converter_models.activity;


public class ActivityData {

    private String activity_type;
    private String activity_date;
    private String user_id;
    private String sender_id;
    private String receiver_id;
    private String user_avatar;
    private String user_name;
    private String is_blocked;
    private String is_connection;
    private String is_favorite;
    private String activity_status;
    private String description;
    private String activity_time;
    private String sender_profile_id;
    private String receiver_profile_id;
    private boolean showDate = false;

    public ActivityData() {
    }

    public String getActivity_time() {
        return activity_time;
    }

    public void setActivity_time(String activity_time) {
        this.activity_time = activity_time;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(String is_blocked) {
        this.is_blocked = is_blocked;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getActivity_date() {
        return activity_date;
    }

    public void setActivity_date(String activity_date) {
        this.activity_date = activity_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getActivity_status() {
        return activity_status;
    }

    public void setActivity_status(String activity_status) {
        this.activity_status = activity_status;
    }

    public String getSender_profile_id() {
        return sender_profile_id;
    }

    public void setSender_profile_id(String sender_profile_id) {
        this.sender_profile_id = sender_profile_id;
    }

    public String getReceiver_profile_id() {
        return receiver_profile_id;
    }

    public void setReceiver_profile_id(String receiver_profile_id) {
        this.receiver_profile_id = receiver_profile_id;
    }
}
