
package lnq.com.lnq.model.gson_converter_models.Contacts.connections;

import java.io.Serializable;

import lnq.com.lnq.model.gson_converter_models.Contacts.NoteOnUser;
import lnq.com.lnq.model.gson_converter_models.Contacts.TaskOnUser;

public class UserConnectionsData implements Serializable {

    private String user_id;
    private String user_email;
    private String user_avatar;
    private String user_phone;
    private String user_fname;
    private String user_lname;
    private String user_current_position;
    private String user_company;
    private String user_distance;
    private String is_favorite;
    private String is_blocked;
    private String is_connection;
    private String contact_status;
    private String contact_id;
    private String contact_name;
    private String user_address;
    private String user_birthday;
    private String visible_to;
    private String visible_at;
    private String user_lat;
    private String user_long;
    private String connection_date;
    private String recent_viewed;
    private String userNote;
    private String profile_id;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public String getConnection_date() {
        return connection_date;
    }

    public void setConnection_date(String connection_date) {
        this.connection_date = connection_date;
    }

    public String getRecent_viewed() {
        return recent_viewed;
    }

    public void setRecent_viewed(String recent_viewed) {
        this.recent_viewed = recent_viewed;
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

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
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

    public String getUser_distance() {
        return user_distance;
    }

    public void setUser_distance(String user_distance) {
        this.user_distance = user_distance;
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

    public String getIs_connection() {
        return is_connection;
    }

    public void setIs_connection(String is_connection) {
        this.is_connection = is_connection;
    }

    public String getContact_status() {
        return contact_status;
    }

    public void setContact_status(String contact_status) {
        this.contact_status = contact_status;
    }

}
