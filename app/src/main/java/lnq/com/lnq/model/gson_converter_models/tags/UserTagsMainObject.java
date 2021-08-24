
package lnq.com.lnq.model.gson_converter_models.tags;

public class UserTagsMainObject {

    private Integer status;
    private String message;
    private userInterests userInterests;

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

    public userInterests getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(userInterests userInterests) {
        this.userInterests = userInterests;
    }


}
