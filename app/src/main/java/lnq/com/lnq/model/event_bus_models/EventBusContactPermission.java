package lnq.com.lnq.model.event_bus_models;

public class EventBusContactPermission {
    private String permissionType;
    private int requestCode;

    public EventBusContactPermission(String permissionType, int requestCode) {
        this.permissionType = permissionType;
        this.requestCode = requestCode;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
