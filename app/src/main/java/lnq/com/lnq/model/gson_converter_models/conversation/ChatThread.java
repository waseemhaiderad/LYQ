
package lnq.com.lnq.model.gson_converter_models.conversation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatThread {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("getChatThreads")
    @Expose
    private List<GetChatThread> getChatThreads = null;

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

    public List<GetChatThread> getGetChatThreads() {
        return getChatThreads;
    }

    public void setGetChatThreads(List<GetChatThread> getChatThreads) {
        this.getChatThreads = getChatThreads;
    }

}
