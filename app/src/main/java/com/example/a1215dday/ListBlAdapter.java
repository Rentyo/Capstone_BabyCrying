package com.example.a1215dday;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;


import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListBlAdapter extends BaseAdapter {

    private static final int SINGLE_PERMISSION =1004;
    private ArrayList<ListBl> list = new ArrayList<ListBl>();
    private Context context;

    private LayoutInflater mLayoutInflater;

    private BluetoothManager blmanager;




    public ListBlAdapter(Context context, ArrayList<ListBl> dataArrayList, BluetoothManager bluetoothManager){
        this.context = context;
        this.list = dataArrayList;

        this.blmanager = bluetoothManager;
        mLayoutInflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return (null != list ? list.size() : 0);
    }

    @Override
    public ListBl getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.bluetooth_list_item, null);
        com.google.android.material.imageview.ShapeableImageView iv = view.findViewById(R.id.listImage);
        TextView txt = view.findViewById(R.id.listName);


        SwitchCompat switchBl = view.findViewById(R.id.switch1);
        switchBl.setChecked(list.get(position).getChecked());

        switchBl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("switch check", "checked");
                    if (context instanceof BluetoothActivity) {
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setChecked(i == position);  // 현재 스위치만 켜고 나머지는 끄기
                        }


                        //서비스 호출
                        Intent intent = new Intent(context, BluetoothService.class);
                        intent.putExtra("BluetoothDevice",list.get(position).getName());

                        if (ActivityCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // 권한 요청
                            ActivityCompat.requestPermissions(
                                    (BluetoothActivity)context,
                                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}
                                    , SINGLE_PERMISSION
                            );
                        } else {
                            // 이미 권한이 있으면 서비스 시작
                           context.startForegroundService(intent);
//                            context.startService(intent);
                        }

                        notifyDataSetChanged();
                    }
                }
            }
        });
        iv.setImageResource(list.get(position).getImage());
        txt.setText(list.get(position).getName());


        return view;
    }


}
