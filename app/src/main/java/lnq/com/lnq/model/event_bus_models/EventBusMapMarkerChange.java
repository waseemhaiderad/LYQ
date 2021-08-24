package lnq.com.lnq.model.event_bus_models;

public class EventBusMapMarkerChange {

    private int position;
    private String latitude;
    private String longitude;

    public EventBusMapMarkerChange(int position, String latitude, String longitude) {
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
