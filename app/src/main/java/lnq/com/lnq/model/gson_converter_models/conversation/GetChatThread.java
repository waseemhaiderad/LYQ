
package lnq.com.lnq.model.gson_converter_models.conversation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetChatThread {

    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("last_message")
    @Expose
    private String lastMessage;
    @SerializedName("last_message_time")
    @Expose
    private String lastMessageTime;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_names")
    @Expose
    private String userNames;
    @SerializedName("user_avatar")
    @Expose
    private String userAvatar;
    @SerializedName("is_connected")
    @Expose
    private String isConnected;
    @SerializedName("is_favorite")
    @Expose
    private String isFavorite;
    @SerializedName("is_blocked")
    @Expose
    private String isBlocked;
    @SerializedName("thread_id")
    @Expose
    private String threadId;
    @SerializedName(("count"))
    @Expose
    private String count;
    @SerializedName(("is_muted"))
    @Expose
    private String is_muted;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("participant_ids")
    @Expose
    private String participant_ids;

    @SerializedName("participant_profile_ids")
    @Expose
    private String participant_profile_ids;

    @SerializedName("group_name")
    @Expose
    private String group_name;

    @SerializedName("groupchat_thread_id")
    @Expose
    private String groupchat_thread_id;

    @SerializedName("sender_profile_id")
    @Expose
    private String sender_profile_id;

    @SerializedName("receiver_profile_id")
    @Expose
    private String receiver_profile_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParticipant_ids() {
        return participant_ids;
    }

    public void setParticipant_ids(String participant_ids) {
        this.participant_ids = participant_ids;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroupchat_thread_id() {
        return groupchat_thread_id;
    }

    public void setGroupchat_thread_id(String groupchat_thread_id) {
        this.groupchat_thread_id = groupchat_thread_id;
    }

    public String getIs_muted() {
        return is_muted;
    }

    public void setIs_muted(String is_muted) {
        this.is_muted = is_muted;
    }

    public String getCount(){
        return count;
    }

    public void setCount(String count){
        this.count = count;
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

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(String isConnected) {
        this.isConnected = isConnected;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
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

    public String getParticipant_profile_ids() {
        return participant_profile_ids;
    }

    public void setParticipant_profile_ids(String participant_profile_ids) {
        this.participant_profile_ids = participant_profile_ids;
    }
}
