package com.excean.mirror.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mirrors")
public class Mirror {
    @ColumnInfo(name = "user_id")
    public int userId;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "pkg")
    public String packageName;
    @PrimaryKey
    @ColumnInfo(name = "mirror_pkg")
    @NonNull
    public String mirrorPackageName="";
    @ColumnInfo(name = "mirror_name")
    public String mirrorName;
}
