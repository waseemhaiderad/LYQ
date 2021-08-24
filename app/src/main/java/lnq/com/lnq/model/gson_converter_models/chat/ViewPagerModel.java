package lnq.com.lnq.model.gson_converter_models.chat;

import java.util.List;

public class ViewPagerModel {
    List<GetChatData> chatDataList;
    String userImage;
    String isConnected;
    String isFavorite;

    public List<GetChatData> getChatDataList() {
        return chatDataList;
    }

    public void setChatDataList(List<GetChatData> chatDataList) {
        this.chatDataList = chatDataList;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
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
}
