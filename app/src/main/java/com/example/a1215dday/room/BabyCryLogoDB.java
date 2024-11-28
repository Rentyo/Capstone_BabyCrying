package com.example.a1215dday.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BabyCryLogoData.class}, version=1 ,exportSchema = false)
public abstract class BabyCryLogoDB extends RoomDatabase {

    private static BabyCryLogoDB database;

    private static String DATABASE_NAME = "database";


    public synchronized static BabyCryLogoDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                    BabyCryLogoDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return database;
    }

    public abstract BabyCryLogoDao BabyCryDao();
    public static void destroyInstance() {
        database = null;
    }
}
