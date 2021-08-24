
package lnq.com.lnq.model.attachements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMultipleAttachementsModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("thread_id")
    @Expose
    private String threadId;
    @SerializedName("sendMultipleAttachmentV2")
    @Expose
    private SendAttachment sendMultipleAttachmentV2;
    @SerializedName("sendGroupMultipleAttachment")
    @Expose
    private SendAttachment sendGroupMultipleAttachment;

    public SendAttachment getSendGroupMultipleAttachment() {
        return sendGroupMultipleAttachment;
    }

    public void setSendGroupMultipleAttachment(SendAttachment sendGroupMultipleAttachment) {
        this.sendGroupMultipleAttachment = sendGroupMultipleAttachment;
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

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }


    public SendAttachment getSendMultipleAttachmentV2() {
        return sendMultipleAttachmentV2;
    }

    public void setSendMultipleAttachmentV2(SendAttachment sendMultipleAttachmentV2) {
        this.sendMultipleAttachmentV2 = sendMultipleAttachmentV2;
    }
}
