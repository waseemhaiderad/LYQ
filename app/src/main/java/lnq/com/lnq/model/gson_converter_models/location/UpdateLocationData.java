package lnq.com.lnq.model.gson_converter_models.location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class UpdateLocationData implements ClusterItem {

    private String sender_id;
    private String receiver_id;
    private String user_lat;
    private String user_long;
    private String user_distance;
    private String is_connection;
    private String is_favorite;
    private String user_name;
    private String user_image;
    private String user_current_position;
    private String user_company;
    private String user_status_msg;
    private String user_home_address;
    private String location_name;
    private String user_connection_date;
    private String sender_location;
    private String receiver_location;
    private String location;
    private int index;
    private String thread_id;
    private String profile_id;

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSender_location() {
        return sender_location;
    }

    public void setSender_location(String sender_location) {
        this.sender_location = sender_location;
    }

    public String getReceiver_location() {
        return receiver_location;
    }

    public void setReceiver_location(String receiver_location) {
        this.receiver_location = receiver_location;
    }

    public String getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
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

    public String getUser_connection_date() {
        return user_connection_date;
    }

    public void setUser_connection_date(String user_connection_date) {
        this.user_connection_date = user_connection_date;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
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

    public String getUser_status_msg() {
        return user_status_msg;
    }

    public void setUser_status_msg(String user_status_msg) {
        this.user_status_msg = user_status_msg;
    }

    public String getUser_home_address() {
        return user_home_address;
    }

    public void setUser_home_address(String user_home_address) {
        this.user_home_address = user_home_address;
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

    public String getUser_distance() {
        return user_distance;
    }

    public void setUser_distance(String user_distance) {
        this.user_distance = user_distance;
    }

    public String getIs_connection() {
        return is_connection;
    }

    public void setIs_connection(String is_connection) {
        this.is_connection = is_connection;
    }

    @Override
    public LatLng getPosition() {
        if (user_lat != null && !user_lat.isEmpty() && user_long != null && !user_long.isEmpty())
            return new LatLng(Double.parseDouble(user_lat), Double.parseDouble(user_long));
        else
            return new LatLng(0.0, 0.0);
    }
}
