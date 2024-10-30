package com.example.a1215dday;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAccountLogAdapter extends RecyclerView.Adapter<ListAccountLogAdapter.ViewHolder> {
    private ArrayList<ListAccountLog> list;
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView id;
        protected TextView date;
        protected TextView log;
        protected SwitchCompat switch_1;

        public ViewHolder(View view) {
            super(view);
            this.id = (TextView) view.findViewById(R.id.account_log_num);
            this.date = (TextView) view.findViewById(R.id.account_log_datetime);
            this.log = (TextView) view.findViewById(R.id.account_log_logdata);
            this.switch_1 = (SwitchCompat) view.findViewById(R.id.switch1);
        }
    }

    @NonNull
    @Override
    public ListAccountLogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }
    public ListAccountLogAdapter(ArrayList<ListAccountLog> list){
        this.list = list;
    }

    @Override
    public void onBindViewHolder(@NonNull ListAccountLogAdapter.ViewHolder holder, int position) {
        holder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        holder.date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        holder.log.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        holder.id.setGravity(Gravity.CENTER);
        holder.date.setGravity(Gravity.CENTER);
        holder.log.setGravity(Gravity.CENTER);



        holder.id.setText(Integer.toString(list.get(position).getNum()));
        holder.date.setText(list.get(position).getDateTime());
        holder.log.setText(list.get(position).getLog());

    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }
}
