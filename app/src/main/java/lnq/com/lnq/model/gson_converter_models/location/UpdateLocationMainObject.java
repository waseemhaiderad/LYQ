
package lnq.com.lnq.model.gson_converter_models.location;

import java.util.ArrayList;

public class UpdateLocationMainObject {

    private Integer status;
    private String message;
    private String connections_count;
    private ArrayList<UpdateLocationData> updateLocation = null;
    private ArrayList<UpdateLocationData> usersInRadius = null;

    public String getConnections_count() {
        return connections_count;
    }

    public void setConnections_count(String connections_count) {
        this.connections_count = connections_count;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<UpdateLocationData> getUpdateLocation() {
        return updateLocation;
    }

    public void setUpdateLocation(ArrayList<UpdateLocationData> updateLocation) {
        this.updateLocation = updateLocation;
    }

    public ArrayList<UpdateLocationData> getUsersInRadius() {
        return usersInRadius;
    }

    public void setUsersInRadius(ArrayList<UpdateLocationData> usersInRadius) {
        this.usersInRadius = usersInRadius;
    }
}
