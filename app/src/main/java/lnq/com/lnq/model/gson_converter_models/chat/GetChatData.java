package lnq.com.lnq.model.gson_converter_models.chat;

import java.util.List;

public class GetChatData {

    private String sender_id;
    private String receiver_id;
    private String message;
    private String message_time;
    private boolean swipeText = false;
    private String msg_id;
    private String sent;
    private String pending;
    private boolean position;
    private String attachment;
    private String sender_name;
    private String voice_attachment;
    private boolean isMessageUnread;
    private String share_profile_id;
    private String sender_profile_id;
    private String receiver_profile_id;
    private ShareProfileObject sharecontact;

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public boolean isMessageUnread() {
        return isMessageUnread;
    }

    public void setMessageUnread(boolean messageUnread) {
        isMessageUnread = messageUnread;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public boolean getPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String isSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public boolean isSwipeText() {
        return swipeText;
    }

    public void setSwipeText(boolean swipeText) {
        this.swipeText = swipeText;
    }

    public String getVoice_attachment() {
        return voice_attachment;
    }

    public void setVoice_attachment(String voice_attachment) {
        this.voice_attachment = voice_attachment;
    }

    public GetChatData(){

    }

    public GetChatData(String sender_id, String receiver_id, String message, String message_time, String msg_id, String sent, String pending, boolean position,String attachment, String voiceAttachment) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
        this.message_time = message_time;
        this.msg_id = msg_id;
        this.sent = sent;
        this.pending = pending;
        this.position = position;
        this.attachment = attachment;
        this.voice_attachment = voiceAttachment;
    }

    public String getsender_id() {
        return sender_id;
    }

    public void setsender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getreceiver_id() {
        return receiver_id;
    }

    public void setreceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getmessage_time() {
        return message_time;
    }

    public void setmessage_time(String message_time) {
        this.message_time = message_time;
    }

    public ShareProfileObject getSharecontact() {
        return sharecontact;
    }

    public void setSharecontact(ShareProfileObject sharecontact) {
        this.sharecontact = sharecontact;
    }

    public String getShare_profile_id() {
        return share_profile_id;
    }

    public void setShare_profile_id(String share_profile_id) {
        this.share_profile_id = share_profile_id;
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
}
