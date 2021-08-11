package com.excean.mirror.dao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.excean.mirror.model.Mirror;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.Singleton;

@Database(entities = {Mirror.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final Singleton<AppDatabase> singleton = new Singleton<AppDatabase>() {
        @Override
        protected AppDatabase create() {
            return Room.databaseBuilder(AppGlobal.getApplication(), AppDatabase.class, "app_database").build();
        }
    };

    public static AppDatabase getDefault() {
        return singleton.get();
    }

    public abstract MirrorDao mirrorDao();

}
