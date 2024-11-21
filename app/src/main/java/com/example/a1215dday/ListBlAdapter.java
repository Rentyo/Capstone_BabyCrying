package com.example.a1215dday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListBlAdapter extends BaseAdapter {

    private static final int SINGLE_PERMISSION =1004;
    private ArrayList<ListBl> list = new ArrayList<ListBl>();
    private Context context;

    private SparseBooleanArray selectedBl;
    private LayoutInflater mLayoutInflater;

    private BluetoothManager blmanager;
    // ListBl과 dataArrayList가 null일 가능성도 나중에 추가
    public ListBlAdapter(Context context, ArrayList<ListBl> dataArrayList, BluetoothManager bluetoothManager){
        this.context = context;
        this.list = dataArrayList;

        this.blmanager = bluetoothManager;
        mLayoutInflater = LayoutInflater.from(context);

        selectedBl = new SparseBooleanArray(list.size());
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
        switchBl.setChecked(selectedBl.get(position, false));
        switchBl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("switch check", "checked");
                    if (context instanceof BluetoothActivity) {
                        for (int i = 0; i < selectedBl.size(); i++) {
                            selectedBl.put(i, i == position);  // 현재 스위치만 켜고 나머지는 끄기
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
                            context.startService(intent);
                        }
                        //이거 뷰에서 컨텍스트 얻어올수있는거 맞지?
                        // 그럼  if문에서 막혀서 연결이 안되야 하는데 연결은 가능...
//                        blmanager.connectSelectedDevice(list.get(position).getName());
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
