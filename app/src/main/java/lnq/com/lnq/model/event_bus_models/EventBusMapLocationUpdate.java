package lnq.com.lnq.model.event_bus_models;

import android.location.Location;

public class EventBusMapLocationUpdate {
    private Location location ;

    public EventBusMapLocationUpdate(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
