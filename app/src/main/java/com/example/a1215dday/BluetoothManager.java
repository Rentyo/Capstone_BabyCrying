package com.example.a1215dday;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.a1215dday.room.BabyCryLogoDB;
import com.example.a1215dday.room.BabyCryLogoDao;
import com.example.a1215dday.room.BabyCryLogoData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    private static BluetoothManager instance;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice connectedDevice;
    private BluetoothSocket bluetoothSocket;

    private Service service;
    //블루투스 핸들러
    Handler mBluetoothHandler;
    //블루투스 연결 스레드
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    //블루투스 디바이스 목록
    Set<BluetoothDevice> deviceSet = new HashSet<>();

    // 알림
    private static String CHANNEL_ID = "baby_crying_detect";


    final static String notiTitle = "Hear4You 감지";
    final static String notiText = "아이 울음 소리가 감지되었습니다.";
    final static String notiTextbel = "아이 울음 소리 종류는 복통입니다.";
    final static String notiTextdis = "아이 울음 소리 종류는 불편함입니다.";
    final static String notiTexthun = "아이 울음 소리 종류는 배고픔입니다.";
    final static String notiTexttir = "아이 울음 소리 종류는 피곤함입니다.";
    NotificationCompat.Builder builder;

    BabyCryLogoDB db =  BluetoothApplication.getDatabase();
    BabyCryLogoDao dataDao = db.BabyCryDao();;

    String tempUserId = "tempUser";

    private BluetoothManager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        mBluetoothHandler = new Handler(Looper.getMainLooper()){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    readMessage = new String((byte[]) msg.obj,0 ,msg.arg1, StandardCharsets.UTF_8);
                    Log.d("readmessage", readMessage);
                    Date date = new Date();
                    // 원하는 형식으로 포맷
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = formatter.format(date);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(service);
                    builder = new NotificationCompat.Builder(service, CHANNEL_ID)
                            .setSmallIcon(R.drawable.splash)
                            .setContentTitle(notiTitle)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    // 알림
                    switch(readMessage) {
                        case "아이 울음 소리 감지" :
                            builder.setContentText( notiText);
                            builder.setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(formattedDateTime + "\n" + notiText));
                            try{
                                //NOTIFIYCATION ID 101
                                notificationManagerCompat.notify(101, builder.build());
                            }catch (SecurityException e){
                                Log.d("알림 오류", e.toString());
                            }
                            break;
                        case "bellypain" :
                            builder.setContentText(notiTextbel);
                            builder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(formattedDateTime + "\n" + notiTextbel));
                            try{
                                //NOTIFIYCATION ID 101
                                notificationManagerCompat.notify(102, builder.build());
                                saveData(0, formattedDateTime, tempUserId); // type 1
                            }catch (SecurityException e){
                                Log.d("알림 오류", e.toString());
                            }
                            break;
                        case "discomfort" :
                            builder.setContentText(notiTextdis);
                            builder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(formattedDateTime + "\n" + notiTextdis));
                            try{
                                //NOTIFIYCATION ID 101
                                notificationManagerCompat.notify(103, builder.build());
                                saveData(1, formattedDateTime, tempUserId); // type 1
                            }catch (SecurityException e){
                                Log.d("알림 오류", e.toString());
                            }
                            break;
                        case "hungry":
                            builder.setContentText(notiTexthun);
                            builder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(formattedDateTime + "\n" + notiTexthun));
                            try{
                                //NOTIFIYCATION ID 101
                                notificationManagerCompat.notify(104, builder.build());
                                saveData(2, formattedDateTime, tempUserId); // type 1
                            }catch (SecurityException e){
                                Log.d("알림 오류", e.toString());
                            }
                            break;
                        case "tired":
                            builder.setContentText(notiTexttir);
                            builder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(formattedDateTime + "\n" + notiTexttir));
                            try{
                                //NOTIFIYCATION ID 101
                                notificationManagerCompat.notify(105, builder.build());
                                saveData(3, formattedDateTime, tempUserId); // type 1
                            }catch (SecurityException e){
                                Log.d("알림 오류", e.toString());
                            }
                            break;

                    }
                }
            }
        };
    }

    private void saveData(int type, String dateTime, String userId) {
        String[] parts = dateTime.split(" ");
        String date = parts[0];
        String time = parts[1];

        BabyCryLogoData data = new BabyCryLogoData();
        data.setCryType(type);
        data.setUserId(userId);
        data.setDateYMD(date);
        data.setTimeHMS(time);
        dataDao.insertBabyCryLogo(data);
    }
    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "채널이름";
            String description = "채널설명";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    // 싱글턴 인스턴스 가져오기
    public static synchronized BluetoothManager getInstance() {
        if (instance == null) {
            instance = new BluetoothManager();
        }
        return instance;
    }
    public BluetoothAdapter getBluetoothAdapter() { return bluetoothAdapter; }
    public Handler getBluetoothHandler() {return mBluetoothHandler;}
    public void addDevice(BluetoothDevice device){
        deviceSet.add(device);
    }
    public void refreshDevice(){
        deviceSet.clear();
    }
    public Set<BluetoothDevice> getDeviceSet(){
        return deviceSet;
    }

    // 연결된 BluetoothSocket 반환
    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    // 연결 해제 메서드
    public void disconnect() {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                bluetoothSocket.close();
                Log.d("BluetoothManager", "Device disconnected");
            } catch (IOException e) {
                Log.e("BluetoothManager", "Failed to disconnect", e);
            }
        } else {
            Log.d("BluetoothManager", "No connection to disconnect");
        }
    }
    void connectSelectedDevice(String selectedDeviceName, Service service) {
        this.service = service;
        BluetoothDevice mBluetoothDevice = null;
        for(BluetoothDevice tempDevice : deviceSet) {
            try {

                if (selectedDeviceName.equals(tempDevice.getName())) {
                    mBluetoothDevice = tempDevice;
                    Log.d("blDevice", mBluetoothDevice.toString());
                    break;
                }
            }
            catch (SecurityException e){
                Log.d("error9", e.toString());
            }
        }
        boolean pairing = false;
        try {
            pairing = mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED;
        } catch ( SecurityException e){
            Log.d("페어링 오류", e.toString());
        }
        Log.d("pairing", String.valueOf(pairing));
        if(mBluetoothDevice != null && pairing) {
            UUID BT_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            try {
                bluetoothSocket = mBluetoothDevice.
                        createRfcommSocketToServiceRecord(BT_UUID);
                Log.d("socket", bluetoothSocket.toString());
                if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                    Log.d("Bluetooth", "Already connected");
                } else {
                    //그럼 여기서
                    mThreadConnectedBluetooth = new ConnectedBluetoothThread(bluetoothSocket);
                    mThreadConnectedBluetooth.start();
                    mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
                }
            } catch (IOException e) {
                Log.e("Error Reason2", e.toString());
            } catch (SecurityException e){
                Log.e("Error Reason2", e.toString());
            }
        }
        else {
            try {
                boolean isBonded = mBluetoothDevice.createBond();
                if (isBonded) {
                    Log.d("페어링", "페어링 시작");
                    connectSelectedDevice(mBluetoothDevice.getName(), service);
                } else {
                    Log.d("페어링", "페어링 실패");
                }

            }catch (SecurityException e){
                Log.d("pairing중", e.toString());
            }

        }
    }


//    void disconnectpreDevice(){
//        try {
//            if(mBluetoothSocket != null) {
//                mBluetoothSocket.close();
//                mBluetoothSocket = null;
//            }
//            if (mThreadConnectedBluetooth != null) {
//                mThreadConnectedBluetooth.interrupt();
//                mThreadConnectedBluetooth = null;
//            }
//        }
//        catch (IOException e) {
//            Log.e("Error Reason", e.toString());
//        }
//
//    }
    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        // 스레드 생성자
        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                if (!bluetoothSocket.isConnected()) {
                    mmSocket.connect();
                }
                // 연결 성공 후 추가 작업
            } catch (IOException e) {
                Log.e("Bluetooth", "Connection failed", e);
            } catch (SecurityException e){
                Log.e("Bluetooth", "Connection failed", e);
            }

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d("오류", "소켓 연결 중 오류");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        // run() 메서드
        public void run() {
            int bytes;
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        Log.d("bytes", String.valueOf(bytes));
                        Log.d("data", new String(buffer,0 ,bytes,"UTF-8"));
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.d("오류", "전송 오류");
            }
        }
        public void cancel() {
            try {
                if (mmSocket != null) {
                    mmSocket.close();
                    Log.d("Bluetooth", "Socket closed");
                } else {
                    Log.d("Bluetooth", "Socket is already null");
                }
            } catch (IOException e) {
                Log.d("Bluetooth", "Socket close error", e);
            }
        }
    }


}
