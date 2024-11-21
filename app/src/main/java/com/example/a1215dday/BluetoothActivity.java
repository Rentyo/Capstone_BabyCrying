package com.example.a1215dday;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.compose.ui.node.IntermediateLayoutModifierNode;
import androidx.core.app.ActivityCompat;
import androidx.core.os.HandlerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends ComponentActivity {

    // 리스트뷰 데이터 3인방
    private ListView listView;
    private ArrayList<ListBl> arrayList;
    private ListBlAdapter adapter;
    //설정 완료 버튼
    private Button btn_Com;
    //새로고침 버튼
    private Button btn_Rf;
    //블루투스 모드
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;

    //블루투스 어댑터
    BluetoothAdapter mBluetoothAdapter;

    //블루투스 관리
    BluetoothManager blManager;


    //블루투스 소켓
    BluetoothSocket mBluetoothSocket;
    //입출력 스트림
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btn_Com = findViewById(R.id.settingCButton);
        btn_Rf = findViewById(R.id.refreshBL);
        Log.d("onCreate", "BluetoothActivity OnCreate");

        BluetoothApplication app = (BluetoothApplication) getApplicationContext();
        blManager = app.getBluetoothManager();
        mBluetoothAdapter = blManager.getBluetoothAdapter();

        if(mBluetoothAdapter == null){
            Log.d("error3", "디바이스가 블루투스를 지원하지 않는다.");
            Toast.makeText(getApplicationContext(), "디바이스가 블루투스를 지원하지 않네요",Toast.LENGTH_SHORT).show();
        }else if(mBluetoothAdapter.isEnabled()){
            Toast.makeText(getApplicationContext(), "이미 블루투스가 켜져있습니다.", Toast.LENGTH_SHORT).show();
            Log.d("error4", "이미 블루투스가 켜져 있다.");
        }else{
            //Ask user
            Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityResultLauncher.launch(enableBtIntent);
            Log.d("ASK", "ASKUSER");

            //Intercept Status changed (by.broadcast)
            IntentFilter BTIntent = new IntentFilter(mBluetoothAdapter.ACTION_STATE_CHANGED);
            Log.d("Broadcast", "Broadcast Status Changed");
            registerReceiver(mBroadCastReceiver, BTIntent);
        }

        bluetoothDiscovery();
        bluetoothDiscover();

        //리스트 데이터
        this.InitializeListData();
        listView = findViewById(R.id.listviewBl);
        adapter = new ListBlAdapter(this, arrayList, blManager);
        listView.setAdapter(adapter);

        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.settingCButton){
                    //1. 블루투스 세팅
                    Log.d("시작", "블루투스 소켓 닫히는 시점 파악 시작 1");
                    //4. Fragment Activity로 이동
                    Intent intent = new Intent(BluetoothActivity.this,FragmentActivity.class); //LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if(v.getId() == R.id.refreshBL) {
                    blManager.getDeviceSet().clear();
                    Log.d("refresh", "refresh");
                    Toast.makeText(getApplicationContext(), "다시 검색합니다.", Toast.LENGTH_SHORT).show();
                    bluetoothDiscover();
                }else {
                    Log.d("ERR1", "없는 버튼인데?");
                }
            }
        };
        btn_Com.setOnClickListener(listener);
        btn_Rf.setOnClickListener(listener);

    }
    private ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Log.d("activityResultLauncher", result.toString());
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_SHORT).show();
                    Log.d("activityResultLauncher" , "블루투스 활성화");
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                    Log.d("activityResultLauncher" , "취소");

                }
            });

    public void InitializeListData()
    {
        arrayList = new ArrayList<ListBl>();

        arrayList.add(new ListBl("블루투스1","시간1",1,2,R.drawable.splash));
        arrayList.add(new ListBl("블루투스2","시간2",2,2,R.drawable.splash));
        arrayList.add(new ListBl("블루투스3","시간3",3,2,R.drawable.splash));
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            if(permissionCheck!=0){
                Log.d("checkPermission", "Permission!");
                String[] permission_list;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    permission_list = new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.BLUETOOTH_SCAN,
                            android.Manifest.permission.BLUETOOTH_ADMIN,
                            android.Manifest.permission.BLUETOOTH_ADVERTISE,
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                    };
                } else {
                    permission_list = new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    };
                }
                this.requestPermissions(permission_list ,1001);
            }
            else{
                Log.d("checkPermission", "No need to check permissions. SDK version < LoLLIPOP");
            }
        }
    }

    // Create a BroadcastReceiver
    @Override
    protected void onDestroy() {
        //Toast.makeText(getApplicationContext(), "onDestroy called", Toast.LENGTH_SHORT).show();
        Log.d("onDestroy", String.valueOf(blManager.getBluetoothSocket().isConnected()));
        try{
            Log.d("onDestroy", String.valueOf(blManager.getBluetoothSocket().getInputStream()));
            Log.d("onDestroy", String.valueOf(blManager.getBluetoothSocket().getOutputStream()));
        }
        catch (IOException e){
            Log.d("onDestroy" , e.toString());
        }
        super.onDestroy();
        unregisterReceiver(mBroadCastReceiver);
        //mThreadConnectedBluetooth.cancel();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 블루투스 연결 끊기 방지
        Log.d("onPause", "BluetoothActivity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 연결 상태 점검 및 필요 시 재연결
        Log.d("onResume", "BluetoothActivity onResume");
    }
    public void bluetoothDiscovery(){
        Log.d("bluetoothDiscovery", "bluetoothDiscovery()");
        try {
            Toast.makeText(getApplicationContext(), "Making device discoverable for 120 seconds.", Toast.LENGTH_SHORT).show();

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            startActivity(discoverableIntent);

            //get scanmode change
            IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mBroadCastReceiver, intentFilter);
        } catch (SecurityException e){
            Log.d("error", e.toString());
        }
    }
    public void bluetoothDiscover(){
        try {
            if (mBluetoothAdapter.isDiscovering()) { //already discovering > cancel
                Log.d("bluetoothDiscover", "already discovering");
                mBluetoothAdapter.cancelDiscovery();
                Toast.makeText(getApplicationContext(), "Canceling discovery", Toast.LENGTH_SHORT).show();

                checkBTPermissions(); //check for permissions

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadCastReceiver, discoverDevicesIntent);

                IntentFilter discoveryFinishedIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(mBroadCastReceiver, discoveryFinishedIntent);
            } else if (!mBluetoothAdapter.isDiscovering()) {
                Log.d("bluetoothDiscover", "first discovering");
                checkBTPermissions();
                Toast.makeText(getApplicationContext(), "Starting discovery", Toast.LENGTH_SHORT).show();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadCastReceiver, discoverDevicesIntent);

                IntentFilter discoveryFinishedIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(mBroadCastReceiver, discoveryFinishedIntent);
            }
        } catch(SecurityException e){
            Log.d("error1", e.toString());
        }
    }
    private final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadcastReciver onReceive", "onReceive");
            String action = intent.getAction();
            try {
                if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                    Log.d("Action", "ACTION_STATE_CHANGED");
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                    switch (state) {
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(getApplicationContext(), "Bluetooth On", Toast.LENGTH_SHORT).show();
//                        tvBluetoothStatus.setText("Active");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(getApplicationContext(), "Bluetooth Off", Toast.LENGTH_SHORT).show();
//                        tvBluetoothStatus.setText("NonActive");
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Toast.makeText(getApplicationContext(), "Bluetooth turning On", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Toast.makeText(getApplicationContext(), "Bluetooth turning Off", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }//end if

                else if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                    Log.d("Action", "ACTION_SCAN_MODE_CHANGED");

                    final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                    switch (mode) {
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            Toast.makeText(getApplicationContext(), "Discoverability enabled", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            Toast.makeText(getApplicationContext(), "Discoverability Disabled. Able to receive connections", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            Toast.makeText(getApplicationContext(), "Discoverability Disabled. Not able to receive connections", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }//end else if
                else if(action.equals(mBluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                    arrayList.clear();
                    blManager.getDeviceSet().forEach(obj -> {
                        try{
                            arrayList.add(new ListBl(obj.getName(), obj.getAddress(),0,0,R.drawable.splash));
                        } catch (SecurityException e){
                            Log.d("error ArrayList", e.toString());
                        }
                    });
                    adapter.notifyDataSetChanged();
//                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Discovery finished, list cleared.", Toast.LENGTH_SHORT).show();
                    Log.d("BluetoothDiscovery", "Discovery finished, list cleared.");
                }

                else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    Log.d("Action", "ACTION_FOUND");
                    try {
                        //get devices
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(device.getName() != null) {
                            Log.d("devices", device.getName());
                            blManager.addDevice(device);
                        }
                        else{
                            Log.d("null devices" , "null");
                        }

                        Toast.makeText(getApplicationContext(), device.getName() + " : " + device.getAddress(), Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        Log.d("error2", e.toString());
                    }
                }
                    //end else if

            }catch (Exception e){
                Log.d("ERROR", e.toString());
            }
        }

    };

}

