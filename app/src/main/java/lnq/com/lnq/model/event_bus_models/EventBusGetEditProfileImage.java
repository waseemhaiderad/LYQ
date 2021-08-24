package lnq.com.lnq.model.event_bus_models;

import android.graphics.Bitmap;

public class EventBusGetEditProfileImage {
    private String profileImagePath;
    private String secondaryProfileId;
    private Bitmap bitmapImagePath;

    public EventBusGetEditProfileImage(String profileImagePath, String secondaryProfileId, Bitmap bitmapImagePath) {
        this.profileImagePath = profileImagePath;
        this.secondaryProfileId = secondaryProfileId;
        this.bitmapImagePath = bitmapImagePath;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String getSecondaryProfileId() {
        return secondaryProfileId;
    }

    public void setSecondaryProfileId(String secondaryProfileId) {
        this.secondaryProfileId = secondaryProfileId;
    }

    public Bitmap getBitmapImagePath() {
        return bitmapImagePath;
    }

    public void setBitmapImagePath(Bitmap bitmapImagePath) {
        this.bitmapImagePath = bitmapImagePath;
    }
}
