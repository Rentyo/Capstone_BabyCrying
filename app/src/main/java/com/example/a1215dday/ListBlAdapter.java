package com.example.a1215dday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListBlAdapter extends BaseAdapter {
    private ArrayList<ListBl> list = new ArrayList<ListBl>();
    private Context context;

    private LayoutInflater mLayoutInflater;
    // ListBl과 dataArrayList가 null일 가능성도 나중에 추가
    public ListBlAdapter(Context context, ArrayList<ListBl> dataArrayList){
        this.context = context;
        this.list = dataArrayList;
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

        iv.setImageResource(list.get(position).getImage());
        txt.setText(list.get(position).getName());

        return view;
    }

}
