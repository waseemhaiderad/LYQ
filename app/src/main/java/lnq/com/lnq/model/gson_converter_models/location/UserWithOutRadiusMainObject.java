package lnq.com.lnq.model.gson_converter_models.location;

import java.util.ArrayList;
import java.util.List;

public class UserWithOutRadiusMainObject {
    private Integer status;
    private String message;
    private List<UserWithOutRadiusData> usersWithOutRadius;

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

    public List<UserWithOutRadiusData> getUsersWithOutRadius() {
        return usersWithOutRadius;
    }

    public void setUsersWithOutRadius(List<UserWithOutRadiusData> usersWithOutRadius) {
        this.usersWithOutRadius = usersWithOutRadius;
    }
}
