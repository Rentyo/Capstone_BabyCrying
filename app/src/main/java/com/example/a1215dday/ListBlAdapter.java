package com.example.a1215dday;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListBlAdapter extends BaseAdapter {
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
//                         BluetoothActivity의 메서드 호출
//                        ((BluetoothActivity) context).disconnectpreDevice();
                        //((BluetoothActivity) context).connectSelectedDevice(list.get(position).getName());
                        blmanager.connectSelectedDevice(list.get(position).getName());
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
