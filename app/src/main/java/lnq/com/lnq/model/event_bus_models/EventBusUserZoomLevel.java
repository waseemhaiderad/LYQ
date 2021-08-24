package lnq.com.lnq.model.event_bus_models;

public class EventBusUserZoomLevel {

    private String id;
    private double latitude, longitude;
    private String zoomLevel;

    public EventBusUserZoomLevel(String id, double latitude, double longitude, String zoomLevel) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoomLevel = zoomLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(String zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
}
