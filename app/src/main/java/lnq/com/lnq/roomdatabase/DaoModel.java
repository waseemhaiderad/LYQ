package lnq.com.lnq.roomdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoModel {

    @Insert
    void insertProfiles(MultiProfileRoomModel multiProfileRoomModel);

    @Query("SELECT * FROM profiledata_table ORDER BY created_at desc")
    LiveData<List<MultiProfileRoomModel>> fetchAllProfiles();

    @Query("SELECT * FROM profiledata_table WHERE id =:profileId")
    LiveData<MultiProfileRoomModel> getProfileByIDs(String profileId);

    @Update
    void updateProfile(MultiProfileRoomModel multiProfileRoomModel);

    @Update
    void updateProfileList(List<MultiProfileRoomModel> multiProfileRoomModel);

    @Query("DELETE FROM profiledata_table")
    public void deleteProfiles();

}
