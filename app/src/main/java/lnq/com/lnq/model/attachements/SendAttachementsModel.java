
package lnq.com.lnq.model.attachements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendAttachementsModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("thread_id")
    @Expose
    private String threadId;
    @SerializedName("sendAttachment")
    @Expose
    private SendAttachment sendAttachment;
    @SerializedName("sendGroupAttachment")
    @Expose
    private SendAttachment sendGroupAttachment;
    @SerializedName("sendVoiceAttachement")
    @Expose
    private SendVoiceAttachment sendVoiceAttachement;
    @SerializedName("sendVoiceAttachementInGroup")
    @Expose
    private SendVoiceAttachment sendVoiceAttachementInGroup;

    public SendAttachment getSendGroupAttachment() {
        return sendGroupAttachment;
    }

    public void setSendGroupAttachment(SendAttachment sendGroupAttachment) {
        this.sendGroupAttachment = sendGroupAttachment;
    }

    public SendVoiceAttachment getSendVoiceAttachement() {
        return sendVoiceAttachement;
    }

    public void setSendVoiceAttachement(SendVoiceAttachment sendVoiceAttachement) {
        this.sendVoiceAttachement = sendVoiceAttachement;
    }

    public SendVoiceAttachment getSendVoiceAttachementInGroup() {
        return sendVoiceAttachementInGroup;
    }

    public void setSendVoiceAttachementInGroup(SendVoiceAttachment sendVoiceAttachementInGroup) {
        this.sendVoiceAttachementInGroup = sendVoiceAttachementInGroup;
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

    public SendAttachment getSendAttachment() {
        return sendAttachment;
    }

    public void setSendAttachment(SendAttachment sendAttachment) {
        this.sendAttachment = sendAttachment;
    }

}
