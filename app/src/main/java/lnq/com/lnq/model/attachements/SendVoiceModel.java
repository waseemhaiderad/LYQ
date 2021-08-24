package lnq.com.lnq.model.attachements;

public class SendVoiceModel {
    int status;
    String message;
    SendVoiceAttachment sendVoiceAttachement;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SendVoiceAttachment getSendVoiceAttachement() {
        return sendVoiceAttachement;
    }

    public void setSendVoiceAttachement(SendVoiceAttachment sendVoiceAttachement) {
        this.sendVoiceAttachement = sendVoiceAttachement;
    }
}
