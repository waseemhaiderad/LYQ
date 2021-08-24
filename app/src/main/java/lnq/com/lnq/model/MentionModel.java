package lnq.com.lnq.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.linkedin.android.spyglass.mentions.Mentionable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import lnq.com.lnq.R;

public class MentionModel implements Mentionable {

    private String userId;
    private String userName;
    private String userImage;
    private String userProfileId;

    public MentionModel(String userId, String userName, String userImage, String userProfileId) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.userProfileId = userProfileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    @Override
    public String toString() {
        return getUserName();
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(Mentionable.MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return userName;
            case PARTIAL:
            case NONE:
            default:
                return "";
        }
    }

    @NonNull
    @Override
    public Mentionable.MentionDeleteStyle getDeleteStyle() {
        // Note: Cities do not support partial deletion
        // i.e. "San Francisco" -> DEL -> ""
        return Mentionable.MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return userName.hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
    }

    public MentionModel(Parcel in) {
        userName = in.readString();
    }

    public static final Parcelable.Creator<MentionModel> CREATOR
            = new Parcelable.Creator<MentionModel>() {
        public MentionModel createFromParcel(Parcel in) {
            return new MentionModel(in);
        }

        public MentionModel[] newArray(int size) {
            return new MentionModel[size];
        }
    };

    // --------------------------------------------------
    // CityLoader Class (loads cities from JSON file)
    // --------------------------------------------------

    public static class CityLoader extends MentionsLoader<MentionModel> {
        private static final String TAG = CityLoader.class.getSimpleName();
        public CityLoader(JSONArray array) {
            super(array);
        }

        @Override
        public MentionModel[] loadData(JSONArray arr) {
            MentionModel[] data = new MentionModel[arr.length()];
            try {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    data[i] = new MentionModel(jsonObject.getString("userId"), jsonObject.getString("userName"), jsonObject.getString("userImage"), jsonObject.getString("userProfileId"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while parsing city JSONArray", e);
            }
            return data;
        }
    }

}
