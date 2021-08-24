
package lnq.com.lnq.model.gson_converter_models.activity;

import java.util.List;

public class ActivityMainObject {

    private Integer status;
    private String message;
    private List<ActivityData> userActivity = null;

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

    public List<ActivityData> getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(List<ActivityData> userActivity) {
        this.userActivity = userActivity;
    }

}
