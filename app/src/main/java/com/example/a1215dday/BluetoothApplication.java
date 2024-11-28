package com.example.a1215dday;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import androidx.compose.ui.layout.RootMeasurePolicy;
import androidx.room.Room;

import com.example.a1215dday.room.BabyCryLogoDB;

public class BluetoothApplication extends Application {
    private BluetoothManager bluetoothManager;
    private static BabyCryLogoDB database;
    @Override
    public void onCreate() {
        super.onCreate();
        database = BabyCryLogoDB.getInstance(this);
        // 앱 초기화 코드 작성 (예: 블루투스 설정)
        bluetoothManager = BluetoothManager.getInstance();
        Log.d("database", "db 정의");


    }

    public static BabyCryLogoDB getDatabase() {
        Log.d("database", database.toString());
        return database;
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }
}
