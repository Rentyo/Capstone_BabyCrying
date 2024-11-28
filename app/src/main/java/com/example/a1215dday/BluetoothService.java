package com.example.a1215dday;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class BluetoothService extends Service {

    private BluetoothManager blmanager;

    private String CHANNEL_ID2 = "background_service";
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

        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
//            blmanager.disconnectDevice();

            // 포그라운드 서비스 종료
            stopForeground(true); // 알림 제거
            stopSelf(); // 서비스 종료
            return START_NOT_STICKY; // 재시작하지 않음
        }
        String selectedDeviceName = intent.getStringExtra("BluetoothDevice");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, //context
                0, //request code
                intent, //flag
                PendingIntent.FLAG_IMMUTABLE //flag
        );

        // 종료 동작을 처리할 PendingIntent 생성
        Intent stopSelfIntent = new Intent(this, BluetoothService.class);
        stopSelfIntent.setAction("STOP_SERVICE");

        PendingIntent stopSelfPendingIntent = PendingIntent.getService(
                this,
                0,
                stopSelfIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID2);
        builder.setSmallIcon(R.drawable.splash);
        builder.setContentTitle("Foreground Service");
        builder.setContentText("포그라운드 서비스");
        builder.setColor(Color.RED);
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.ic_launcher_stop, "종료",stopSelfPendingIntent); // 종료 버튼 추가

        createNotificationChannel(this);
        startForeground(1, builder.build());


        // Bluetooth 연결을 별도의 스레드에서 실행
        new Thread(new Runnable() {
            @Override
            public void run() {
                blmanager.createNotificationChannel(BluetoothService.this);
                blmanager.connectSelectedDevice(selectedDeviceName, BluetoothService.this);
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "채널이름";
            String description = "채널설명";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID2, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    //서비스가 종료될 때 할 작업

    public void onDestroy() {
//        if (blmanager.getBluetoothSocket().isConnected()) {
//            blmanager.disconnect();
//            Log.d("연결", "끊어졌습니다");
//        }
        Log.d("연결","연결이 끊어졌습니다.");
    }
}