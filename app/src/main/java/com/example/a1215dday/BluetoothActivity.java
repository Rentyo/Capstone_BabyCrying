package com.example.a1215dday;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.RequiresApi;
import androidx.compose.ui.node.IntermediateLayoutModifierNode;
import androidx.core.app.ActivityCompat;
import androidx.core.os.HandlerCompat;

import java.util.ArrayList;
import java.util.Set;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);

        btn_Com = findViewById(R.id.settingCButton);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("mBluetoothAdapter" , mBluetoothAdapter.getAddress());
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        }

        bluetoothDiscovery();
        bluetoothDiscover();

        //리스트 데이터
        this.InitializeListData();
        listView = findViewById(R.id.listviewBl);
        adapter = new ListBlAdapter(this, arrayList);
        listView.setAdapter(adapter);



        View.OnClickListener listener = new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.settingCButton){
                    //1. 블루투스 세팅

                    //4. BlueTooth Activity로 이동
                    Intent intent = new Intent(BluetoothActivity.this,FragmentActivity.class); //LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("ERR1", "없는 버튼인데?");
                }
            }
        };
        btn_Com.setOnClickListener(listener);

    }

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
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }
            else{
                Log.d("checkPermission", "No need to check permissions. SDK version < LoLLIPOP");
            }
        }
    }

    // Create a BroadcastReceiver
    private final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
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
                        Toast.makeText(getApplicationContext(), "Connected.",Toast.LENGTH_SHORT).show();
                        break;

                }
            }//end else if

            else if(action.equals(BluetoothDevice.ACTION_FOUND)){
                try {
                    //get devices
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                mBTdevices.add(device);
                    Toast.makeText(getApplicationContext(), device.getName() + " : " + device.getAddress(), Toast.LENGTH_SHORT).show();
                    //attach device to adapter & set list
//                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTdevices);
//                    newDevicesList.setAdapter(mDeviceListAdapter);
                }catch (SecurityException e){
                    Log.d("error2", e.toString());
                }
            }//end else if

        }//end onReceive
    };
    public void bluetoothDiscovery(){
        try {
            Toast.makeText(getApplicationContext(), "Making device discoverable for 300 seconds.", Toast.LENGTH_SHORT).show();

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
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
                mBluetoothAdapter.cancelDiscovery();
                Toast.makeText(getApplicationContext(), "Canceling discovery", Toast.LENGTH_SHORT).show();

                checkBTPermissions(); //check for permissions

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadCastReceiver, discoverDevicesIntent);
            } else if (!mBluetoothAdapter.isDiscovering()) {
                checkBTPermissions();
                Toast.makeText(getApplicationContext(), "Starting discovery", Toast.LENGTH_SHORT).show();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadCastReceiver, discoverDevicesIntent);
            }
        } catch(SecurityException e){
            Log.d("error1", e.toString());
        }
    }


}
