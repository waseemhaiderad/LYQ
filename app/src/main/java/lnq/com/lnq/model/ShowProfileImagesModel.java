package lnq.com.lnq.model;

public class ShowProfileImagesModel {
    String imagePath;
    boolean isSelected;

    public ShowProfileImagesModel(String imagePath, boolean isSelected) {
        this.imagePath = imagePath;
        this.isSelected = isSelected;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
