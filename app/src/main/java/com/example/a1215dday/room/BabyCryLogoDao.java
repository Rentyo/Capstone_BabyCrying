package com.example.a1215dday.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BabyCryLogoDao {
    @Insert
    void insertBabyCryLogo(BabyCryLogoData log);

    @Query("SELECT * FROM baby_cry_logs")
    List<BabyCryLogoData> getAll();

    @Delete
    void reset(List<BabyCryLogoData> mainData);


}
