package com.example.a1215dday;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class AccountFragment extends Fragment {

    private ArrayList<ListAccountLog> list;
    private ListAccountLogAdapter adapter;
    private int count = -1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        RecyclerView mRecyclerView = view.findViewById(R.id.Q_ALIST);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        list = new ArrayList<>();

        adapter = new ListAccountLogAdapter(list);

        initLogData();
        mRecyclerView.setAdapter(adapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    void initLogData() {
        ListAccountLog data = new ListAccountLog(1,"2024\n10-24", "Baby Crying. \n내용 : discomfort");
        ListAccountLog data2 = new ListAccountLog(2,"2024\n10-24", "Baby Crying. \n내용 : belly_comfort");
        ListAccountLog data3 = new ListAccountLog(3,"2024\n10-24", "Baby Crying. \n내용 : tired");

        //mArrayList.add(0, dict); //RecyclerView의 첫 줄에 삽입
        list.add(data); // RecyclerView의 마지막 줄에 삽입
        list.add(data2);
        list.add(data3);
        adapter.notifyDataSetChanged();
    }
}