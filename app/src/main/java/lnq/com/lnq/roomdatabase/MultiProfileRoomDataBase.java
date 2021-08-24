package lnq.com.lnq.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MultiProfileRoomModel.class}, version = 1, exportSchema = false)
public abstract class MultiProfileRoomDataBase extends RoomDatabase {

    public abstract DaoModel daoModel();

}
