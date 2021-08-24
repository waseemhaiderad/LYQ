
package lnq.com.lnq.model.attachements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendVoiceAttachment {

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
    @SerializedName("is_read")
    @Expose
    private Integer is_read;
    @SerializedName("sender_profile_id")
    @Expose
    private String sender_profile_id;
    @SerializedName("receiver_profile_id")
    @Expose
    private String receiver_profile_id;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("voice_attachment")
    @Expose
    private String voice_attachment;
    @SerializedName("user_profile_id")
    @Expose
    private String user_profile_id;
    @SerializedName("share_profile_id")
    @Expose
    private String share_profile_id;

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

    public Integer getIs_read() {
        return is_read;
    }

    public void setIs_read(Integer is_read) {
        this.is_read = is_read;
    }

    public String getSender_profile_id() {
        return sender_profile_id;
    }

    public void setSender_profile_id(String sender_profile_id) {
        this.sender_profile_id = sender_profile_id;
    }

    public String getReceiver_profile_id() {
        return receiver_profile_id;
    }

    public void setReceiver_profile_id(String receiver_profile_id) {
        this.receiver_profile_id = receiver_profile_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVoice_attachment() {
        return voice_attachment;
    }

    public void setVoice_attachment(String voice_attachment) {
        this.voice_attachment = voice_attachment;
    }

    public String getUser_profile_id() {
        return user_profile_id;
    }

    public void setUser_profile_id(String user_profile_id) {
        this.user_profile_id = user_profile_id;
    }

    public String getShare_profile_id() {
        return share_profile_id;
    }

    public void setShare_profile_id(String share_profile_id) {
        this.share_profile_id = share_profile_id;
    }
}
