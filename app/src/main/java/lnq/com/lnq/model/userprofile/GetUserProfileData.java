
package lnq.com.lnq.model.userprofile;


import java.util.List;

public class GetUserProfileData {

    private String user_email;
    private String secondary_emails;
    private String verification_status;
    private String user_fname;
    private String user_lname;
    private String user_avatar;
    private String avatar_from;
    private String user_cnic;
    private String user_address;
    private String user_phone;
    private String secondary_phones;
    private String user_current_position;
    private String user_company;
    private String user_birthday;
    private String user_bio;
    private String user_status_msg;
    private String location_name;
    private String distance;
    private String user_interests;
    private String is_connection;
    private String is_favorite;
    private String is_blocked;
    private String connection_date;
    private List<UserTasks> task_on_user;
    private UserNotes note_on_user;
    private String location;
    private String visible_at;
    private String visible_to;
    private String user_lat;
    private String user_long;
    private String receiver_location;
    private String sender_location;
    private String sender_id;
    private String receiver_id;
    private List<History> user_history;
    private String thread_id;
    private String common_connections;
    private String social_links;
    private String profile_id;

    private String id;

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getSecondary_emails() {
        return secondary_emails;
    }

    public void setSecondary_emails(String secondary_emails) {
        this.secondary_emails = secondary_emails;
    }

    public String getSecondary_phones() {
        return secondary_phones;
    }

    public void setSecondary_phones(String secondary_phones) {
        this.secondary_phones = secondary_phones;
    }

    public String getSocial_links() {
        return social_links;
    }

    public void setSocial_links(String social_links) {
        this.social_links = social_links;
    }

    public String getCommon_connections() {
        return common_connections;
    }

    public void setCommon_connections(String common_connections) {
        this.common_connections = common_connections;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }


    public String getReceiver_location() {
        return receiver_location;
    }

    public void setReceiver_location(String receiver_location) {
        this.receiver_location = receiver_location;
    }

    public String getSender_location() {
        return sender_location;
    }

    public void setSender_location(String sender_location) {
        this.sender_location = sender_location;
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

    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public String getUser_long() {
        return user_long;
    }

    public void setUser_long(String user_long) {
        this.user_long = user_long;
    }

    public String getVisible_at() {
        return visible_at;
    }

    public void setVisible_at(String visible_at) {
        this.visible_at = visible_at;
    }

    public String getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(String visible_to) {
        this.visible_to = visible_to;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<History> getUser_history() {
        return user_history;
    }

    public void setUser_history(List<History> user_history) {
        this.user_history = user_history;
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

    public List<UserTasks> getTask_on_user() {
        return task_on_user;
    }

    public void setTask_on_user(List<UserTasks> task_on_user) {
        this.task_on_user = task_on_user;
    }

    public UserNotes getNote_on_user() {
        return note_on_user;
    }

    public void setNote_on_user(UserNotes note_on_user) {
        this.note_on_user = note_on_user;
    }

    public String getUser_interests() {
        return user_interests;
    }

    public void setUser_interests(String user_interests) {
        this.user_interests = user_interests;
    }

    public String getIs_connection() {
        return is_connection;
    }

    public void setIs_connection(String is_connection) {
        this.is_connection = is_connection;
    }

    public String getConnection_date() {
        return connection_date;
    }

    public void setConnection_date(String connection_date) {
        this.connection_date = connection_date;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getVerification_status() {
        return verification_status;
    }

    public void setVerification_status(String verification_status) {
        this.verification_status = verification_status;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getAvatar_from() {
        return avatar_from;
    }

    public void setAvatar_from(String avatar_from) {
        this.avatar_from = avatar_from;
    }

    public String getUser_cnic() {
        return user_cnic;
    }

    public void setUser_cnic(String user_cnic) {
        this.user_cnic = user_cnic;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_current_position() {
        return user_current_position;
    }

    public void setUser_current_position(String user_current_position) {
        this.user_current_position = user_current_position;
    }

    public String getUser_company() {
        return user_company;
    }

    public void setUser_company(String user_company) {
        this.user_company = user_company;
    }

    public String getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }

    public String getUser_bio() {
        return user_bio;
    }

    public void setUser_bio(String user_bio) {
        this.user_bio = user_bio;
    }

    public String getUser_status_msg() {
        return user_status_msg;
    }

    public void setUser_status_msg(String user_status_msg) {
        this.user_status_msg = user_status_msg;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }
}
