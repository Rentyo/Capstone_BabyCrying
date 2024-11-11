package com.example.a1215dday;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;

public class BluetoothApplication extends Application {
    private BluetoothManager bluetoothManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // 앱 초기화 코드 작성 (예: 블루투스 설정)
        bluetoothManager = BluetoothManager.getInstance();
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }
}
