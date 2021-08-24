
package lnq.com.lnq.model.gson_converter_models.send_message;

import java.util.HashMap;
import java.util.Map;

public class SendMessageMainObject {

    private Integer status;
    private String message;
    private String thread_id;
    private SendMessageData sendMessage;
    private SendMessageData resendMessage;
    private SendMessageData sendGroupMessage;

    public SendMessageData getSendGroupMessage() {
        return sendGroupMessage;
    }

    public void setSendGroupMessage(SendMessageData sendGroupMessage) {
        this.sendGroupMessage = sendGroupMessage;
    }

    public SendMessageData getResendMessage() {
        return resendMessage;
    }

    public void setResendMessage(SendMessageData resendMessage) {
        this.resendMessage = resendMessage;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
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

    public SendMessageData getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(SendMessageData sendMessage) {
        this.sendMessage = sendMessage;
    }


}
