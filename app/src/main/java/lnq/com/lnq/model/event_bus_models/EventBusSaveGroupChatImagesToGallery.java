package lnq.com.lnq.model.event_bus_models;

import android.graphics.Bitmap;

public class EventBusSaveGroupChatImagesToGallery {
    Bitmap imagePath;

    public EventBusSaveGroupChatImagesToGallery(Bitmap imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getImagePath() {
        return imagePath;
    }

    public void setImagePath(Bitmap imagePath) {
        this.imagePath = imagePath;
    }
}
