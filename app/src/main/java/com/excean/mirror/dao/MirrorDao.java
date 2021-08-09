package com.excean.mirror.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.excean.mirror.model.Mirror;

import java.util.List;

@Dao
public interface MirrorDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertMirror(Mirror info);

    @Delete
    void deleteMirror(Mirror info);

    @Query("select * from mirrors;")
    LiveData<List<Mirror>> getLiveMirrors();


    @Query("select * from mirrors;")
    List<Mirror> getMirrors();
}

