package lnq.com.lnq.model.event_bus_models;

public class EventBusGetChatImagePath {

    private String imagePath;

    public EventBusGetChatImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
