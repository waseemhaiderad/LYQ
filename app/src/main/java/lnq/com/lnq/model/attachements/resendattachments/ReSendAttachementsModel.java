
package lnq.com.lnq.model.attachements.resendattachments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReSendAttachementsModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("thread_id")
    @Expose
    private String threadId;
    @SerializedName("resendAttachment")
    @Expose
    private ResendAttachment resendAttachment;

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

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public ResendAttachment getResendAttachment() {
        return resendAttachment;
    }

    public void setResendAttachment(ResendAttachment resendAttachment) {
        this.resendAttachment = resendAttachment;
    }

}
