package lnq.com.lnq.model.gson_converter_models;

public class GridDemoModel {
    private int image;
    private String name,position,company,status,homeLocation,location;
    private boolean isLnq,isFavorite,isLnqRequested;

    public GridDemoModel(int image, String name, String position, String company, String status, String homeLocation, String location, boolean isLnq, boolean isFavorite, boolean isLnqRequested) {
        this.image = image;
        this.name = name;
        this.position = position;
        this.company = company;
        this.status = status;
        this.homeLocation = homeLocation;
        this.location = location;
        this.isLnq = isLnq;
        this.isFavorite = isFavorite;
        this.isLnqRequested = isLnqRequested;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isLnq() {
        return isLnq;
    }

    public void setLnq(boolean lnq) {
        isLnq = lnq;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isLnqRequested() {
        return isLnqRequested;
    }

    public void setLnqRequested(boolean lnqRequested) {
        isLnqRequested = lnqRequested;
    }
}
