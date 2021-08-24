package lnq.com.lnq.custom.views.gallery;


public class GalleryModel {

    private String mName;
    private String mPath;
    private String mAlbum;
    private boolean mSelected;

    public GalleryModel(String mName, String mPath, String mAlbum, boolean mSelected) {
        this.mName = mName;
        this.mPath = mPath;
        this.mAlbum = mAlbum;
        this.mSelected = mSelected;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public boolean ismSelected() {
        return mSelected;
    }

    public void setmSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
