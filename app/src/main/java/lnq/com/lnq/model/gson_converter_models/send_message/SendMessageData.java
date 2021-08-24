
package lnq.com.lnq.model.gson_converter_models.send_message;

import java.util.HashMap;
import java.util.Map;

public class SendMessageData {

    private String sender_id;
    private String receiver_id;
    private String message;
    private int is_sent;
    private String message_time;
    private String is_pending;
    private int msg_id;
    private String group_thread_id;

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIs_sent() {
        return is_sent;
    }

    public void setIs_sent(int is_sent) {
        this.is_sent = is_sent;
    }

    public String getMessage_time() {
        return message_time;
    }

    public void setMessage_time(String message_time) {
        this.message_time = message_time;
    }

    public String getIs_pending() {
        return is_pending;
    }

    public void setIs_pending(String is_pending) {
        this.is_pending = is_pending;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public String getGroup_thread_id() {
        return group_thread_id;
    }

    public void setGroup_thread_id(String group_thread_id) {
        this.group_thread_id = group_thread_id;
    }
}
