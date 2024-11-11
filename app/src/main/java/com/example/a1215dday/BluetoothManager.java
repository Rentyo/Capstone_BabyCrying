package com.example.a1215dday;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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

    //블루투스 핸들러
    Handler mBluetoothHandler;
    //블루투스 연결 스레드
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    //블루투스 디바이스 목록
    Set<BluetoothDevice> deviceSet = new HashSet<>();


    private BluetoothManager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothHandler = new Handler(Looper.getMainLooper()){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    readMessage = new String((byte[]) msg.obj,0 ,msg.arg1, StandardCharsets.UTF_8);
                    Log.d("readmessage", readMessage);

                    // 알림
                    switch(readMessage) {
                        case "bellypain" :
                            break;
                        case "discomfort" :
                            break;
                        case "hungry":
                            break;
                        case "tired":
                            break;

                    }
                }
            }
        };

    }

    // 싱글턴 인스턴스 가져오기
    public static synchronized BluetoothManager getInstance() {
        if (instance == null) {
            instance = new BluetoothManager();
        }
        return instance;
    }
    public BluetoothAdapter getBluetoothAdapter() { return bluetoothAdapter; }

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
        try {
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                bluetoothSocket.close();
                bluetoothSocket = null;
                connectedDevice = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void connectSelectedDevice(String selectedDeviceName) {
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
                    connectSelectedDevice(mBluetoothDevice.getName());
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
                mmSocket.connect();
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
