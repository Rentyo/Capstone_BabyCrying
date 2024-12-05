package com.example.a1215dday.room;

import androidx.lifecycle.LiveData;
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
    List<BabyCryLogoData> getAllLogData();

    @Query("SELECT * FROM baby_cry_logs WHERE date BETWEEN :yesterday AND :today")
    LiveData<List<BabyCryLogoData>> getLogsBetweenDates(String yesterday, String today);

    @Query("SELECT * FROM baby_cry_logs WHERE date BETWEEN :first AND :second")
    List<BabyCryLogoData> getDataBetweenDates(String first, String second);

    @Query("SELECT * FROM baby_cry_logs WHERE date = :date")
    List<BabyCryLogoData> getLogsDates(String date);

    @Delete
    void reset(List<BabyCryLogoData> mainData);


}
