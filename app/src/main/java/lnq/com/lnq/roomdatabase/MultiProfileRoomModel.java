package lnq.com.lnq.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "profiledata_table")
public class MultiProfileRoomModel {

    @NonNull
    @PrimaryKey
    private String id;
    @ColumnInfo(name = "user_id")
    private String user_id;
    @ColumnInfo(name = "user_fname")
    private String user_fname;
    @ColumnInfo(name = "user_lname")
    private String user_lname;
    @ColumnInfo(name = "user_nickname")
    private String user_nickname;
    @ColumnInfo(name = "user_avatar")
    private String user_avatar;
    @ColumnInfo(name = "avatar_from")
    private String avatar_from;
    @ColumnInfo(name = "user_cnic")
    private String user_cnic;
    @ColumnInfo(name = "user_address")
    private String user_address;
    @ColumnInfo(name = "user_phone")
    private String user_phone;
    @ColumnInfo(name = "secondary_phones")
    private String secondary_phones;
    @ColumnInfo(name = "secondary_emails")
    private String secondary_emails;
    @ColumnInfo(name = "user_current_position")
    private String user_current_position;
    @ColumnInfo(name = "user_company")
    private String user_company;
    @ColumnInfo(name = "user_birthday")
    private String user_birthday;
    @ColumnInfo(name = "user_bio")
    private String user_bio;
    @ColumnInfo(name = "user_status_msg")
    private String user_status_msg;
    @ColumnInfo(name = "user_tags")
    private String user_tags;
    @ColumnInfo(name = "user_interests")
    private String user_interests;
    @ColumnInfo(name = "user_gender")
    private String user_gender;
    @ColumnInfo(name = "home_default_view")
    private String home_default_view;
    @ColumnInfo(name = "contact_default_view")
    private String contact_default_view;
    @ColumnInfo(name = "social_links")
    private String social_links;
    @ColumnInfo(name = "profile_status")
    private String profile_status;
    @ColumnInfo(name = "created_at")
    private String created_at;
    @ColumnInfo(name = "updated_at")
    private String updated_at;
    @ColumnInfo(name = "visible_to")
    private String visible_to;
    @ColumnInfo(name = "visible_at")
    private String visible_at;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
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

    public String getSecondary_phones() {
        return secondary_phones;
    }

    public void setSecondary_phones(String secondary_phones) {
        this.secondary_phones = secondary_phones;
    }

    public String getSecondary_emails() {
        return secondary_emails;
    }

    public void setSecondary_emails(String secondary_emails) {
        this.secondary_emails = secondary_emails;
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

    public String getUser_tags() {
        return user_tags;
    }

    public void setUser_tags(String user_tags) {
        this.user_tags = user_tags;
    }

    public String getUser_interests() {
        return user_interests;
    }

    public void setUser_interests(String user_interests) {
        this.user_interests = user_interests;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getHome_default_view() {
        return home_default_view;
    }

    public void setHome_default_view(String home_default_view) {
        this.home_default_view = home_default_view;
    }

    public String getContact_default_view() {
        return contact_default_view;
    }

    public void setContact_default_view(String contact_default_view) {
        this.contact_default_view = contact_default_view;
    }

    public String getSocial_links() {
        return social_links;
    }

    public void setSocial_links(String social_links) {
        this.social_links = social_links;
    }

    public String getProfile_status() {
        return profile_status;
    }

    public void setProfile_status(String profile_status) {
        this.profile_status = profile_status;
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
