package lnq.com.lnq.roomdatabase;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class MultiProfileRepositry {

    private String DB_NAME = "multiple_profiles";
    private MultiProfileRoomDataBase dataBase;

    public MultiProfileRepositry(Context context) {
        dataBase = Room.databaseBuilder(context, MultiProfileRoomDataBase.class, DB_NAME).build();
    }

    public MultiProfileRoomModel insertProfilesData(String id,
                                   String user_id,
                                   String user_fname,
                                   String user_lname,
                                   String user_nickname,
                                   String user_avatar,
                                   String avatar_from,
                                   String user_cnic,
                                   String user_address,
                                   String user_phone,
                                   String secondary_phones,
                                   String secondary_emails,
                                   String user_current_position,
                                   String user_company,
                                   String user_birthday,
                                   String user_bio,
                                   String user_status_msg,
                                   String user_tags,
                                   String user_interests,
                                   String user_gender,
                                   String home_default_view,
                                   String contact_default_view,
                                   String social_links,
                                   String profile_status,
                                   String created_at,
                                   String updated_at,
                                   String visibleTo,
                                   String visibleAt) {

        MultiProfileRoomModel multiProfileRoomModel = new MultiProfileRoomModel();
        multiProfileRoomModel.setId(id);
        multiProfileRoomModel.setUser_id(user_id);
        multiProfileRoomModel.setUser_fname(user_fname);
        multiProfileRoomModel.setUser_lname(user_lname);
        multiProfileRoomModel.setUser_nickname(user_nickname);
        multiProfileRoomModel.setUser_avatar(user_avatar);
        multiProfileRoomModel.setAvatar_from(avatar_from);
        multiProfileRoomModel.setUser_cnic(user_cnic);
        multiProfileRoomModel.setUser_address(user_address);
        multiProfileRoomModel.setUser_phone(user_phone);
        multiProfileRoomModel.setSecondary_phones(secondary_phones);
        multiProfileRoomModel.setSecondary_emails(secondary_emails);
        multiProfileRoomModel.setUser_current_position(user_current_position);
        multiProfileRoomModel.setUser_company(user_company);
        multiProfileRoomModel.setUser_birthday(user_birthday);
        multiProfileRoomModel.setUser_bio(user_bio);
        multiProfileRoomModel.setUser_status_msg(user_status_msg);
        multiProfileRoomModel.setUser_tags(user_tags);
        multiProfileRoomModel.setUser_interests(user_interests);
        multiProfileRoomModel.setUser_gender(user_gender);
        multiProfileRoomModel.setHome_default_view(home_default_view);
        multiProfileRoomModel.setContact_default_view(contact_default_view);
        multiProfileRoomModel.setSocial_links(social_links);
        multiProfileRoomModel.setProfile_status(profile_status);
        multiProfileRoomModel.setCreated_at(created_at);
        multiProfileRoomModel.setUpdated_at(updated_at);
        multiProfileRoomModel.setVisible_to(visibleTo);
        multiProfileRoomModel.setVisible_at(visibleAt);

        insertUserSecondaryProfileData(multiProfileRoomModel);
        return multiProfileRoomModel;
    }

    public void insertUserSecondaryProfileData(final MultiProfileRoomModel multiProfileRoomModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dataBase.daoModel().insertProfiles(multiProfileRoomModel);
                return null;
            }
        }.execute();
    }

    public void updateTask(final MultiProfileRoomModel multiProfileRoomModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dataBase.daoModel().updateProfile(multiProfileRoomModel);
                return null;
            }
        }.execute();
    }

    public void updateProfileList(final List<MultiProfileRoomModel> multiProfileRoomModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dataBase.daoModel().updateProfileList(multiProfileRoomModel);
                return null;
            }
        }.execute();
    }

    public void deleteAllProfiles() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dataBase.daoModel().deleteProfiles();
                return null;
            }
        }.execute();
    }

    public LiveData<List<MultiProfileRoomModel>> getProfileData() {
        return dataBase.daoModel().fetchAllProfiles();
    }

    public LiveData<MultiProfileRoomModel> getProfileByID(String id) {
        return dataBase.daoModel().getProfileByIDs(id);
    }

}
