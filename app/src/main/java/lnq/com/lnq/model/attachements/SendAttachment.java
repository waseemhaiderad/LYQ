
package lnq.com.lnq.model.attachements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendAttachment {

    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("is_pending")
    @Expose
    private Integer isPending;
    @SerializedName("is_sent")
    @Expose
    private Integer isSent;
    @SerializedName("msg_id")
    @Expose
    private Integer msgId;
    @SerializedName("message_time")
    @Expose
    private String messageTime;
    @SerializedName("group_thread_id")
    @Expose
    private String groupThreadId;

    public String getGroupThreadId() {
        return groupThreadId;
    }

    public void setGroupThreadId(String groupThreadId) {
        this.groupThreadId = groupThreadId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Integer getIsPending() {
        return isPending;
    }

    public void setIsPending(Integer isPending) {
        this.isPending = isPending;
    }

    public Integer getIsSent() {
        return isSent;
    }

    public void setIsSent(Integer isSent) {
        this.isSent = isSent;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

}
