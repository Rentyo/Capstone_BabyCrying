package com.example.a1215dday;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BluetoothService extends Service {

    private BluetoothManager blmanager;
    public BluetoothService() {
        this.blmanager = BluetoothManager.getInstance();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String selectedDeviceName = intent.getStringExtra("BluetoothDevice");
        // Bluetooth 연결을 별도의 스레드에서 실행
        new Thread(new Runnable() {
            @Override
            public void run() {
                //여기서 blmanager에 스레드 생성 요청을 보내잖아
                blmanager.createNotificationChannel(BluetoothService.this);
                blmanager.connectSelectedDevice(selectedDeviceName, BluetoothService.this);
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        if (blmanager.getBluetoothSocket().isConnected()) {
            blmanager.disconnect();
            Log.d("연결", "끊어졌습니다");
        }
    }
}