package com.example.a1215dday.room;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "baby_cry_logs")
public class BabyCryLogoData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "CryType")
    private int cryType;

    @ColumnInfo(name = "date")
    public String dateYMD; // yyyy-MM-dd

    @ColumnInfo(name = "time")
    public String timeHMS; // HH:mm:ss

    @ColumnInfo(name = "userId")
    private String userId;    // 유저 ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCryType() {
        return cryType;
    }

    public void setCryType(int cryType) {
        this.cryType = cryType;
    }

    public String getDateYMD() {
        return dateYMD;
    }
    public void setDateYMD (String dateYMD){
        this.dateYMD = dateYMD;
    }

    public String getTimeHMS() {
        return timeHMS;
    }
    public void setTimeHMS (String timeHMS){
        this.timeHMS = timeHMS;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
