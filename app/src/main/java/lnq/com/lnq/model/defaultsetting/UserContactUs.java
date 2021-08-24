package lnq.com.lnq.model.defaultsetting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserContactUs {

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @SerializedName("status")
    @Expose
    private Integer status;

}
