package com.example.a1215dday;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.a1215dday.DatePicker.DatePickerFragment;
import com.example.a1215dday.room.BabyCryLogoDB;
import com.example.a1215dday.room.BabyCryLogoDao;
import com.example.a1215dday.room.BabyCryLogoData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private ArrayList<ListAccountLog> list;
    private ListAccountLogAdapter adapter;
    private int count = -1;

    private Observer<List<BabyCryLogoData>> babyCryLogoDataObserver;

    LiveData<List<BabyCryLogoData>> babyCryLogoDataLive;

    String Today;
    String Yesterday;
    RecyclerView mRecyclerView;

    BabyCryLogoDB db =  BluetoothApplication.getDatabase();
    BabyCryLogoDao dataDao = db.BabyCryDao();;


    Button logdateBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("dateKey", this, (requestKey, result) -> {
            String selectedDate = result.getString("selectedDate");
            // 날짜 처리
            Log.d("Selected Date", selectedDate);
            // 날짜에 맞는 데이터 로드 로직 작성
            loadDataForDate(selectedDate);
        });
    }
    private void loadDataForDate(String selectedDate) {
        // 선택된 날짜에 맞는 데이터 로드 로직
        Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

        Log.d("Today", Today);
        Log.d("Yesterday", Yesterday);

        if (babyCryLogoDataObserver != null && babyCryLogoDataLive != null) {
            babyCryLogoDataLive.removeObserver(babyCryLogoDataObserver);
        }

        if(selectedDate.equals(Today)  || selectedDate.equals(Yesterday) ){
            Log.d("날짜 선택 ", "어제 또는 오늘입니다");
            initLogData();
        }else{
            Log.d("날짜 선택 ", "어제와 오늘이 아닙니다");
            List<BabyCryLogoData> babyCryLogoData = dataDao.getLogsDates(selectedDate);
            if(babyCryLogoData.size() == 0){
                list.clear();
                ListAccountLog data = new ListAccountLog(1,selectedDate, "Baby Crying. \n내용 : discomfort");
                ListAccountLog data2 = new ListAccountLog(2,selectedDate, "Baby Crying. \n내용 : belly_comfort");
                ListAccountLog data3 = new ListAccountLog(3,selectedDate, "Baby Crying. \n내용 : tired");

                //mArrayList.add(0, dict); //RecyclerView의 첫 줄에 삽입
                list.add(data); // RecyclerView의 마지막 줄에 삽입
                list.add(data2);
                list.add(data3);
            }else{
                list.clear();
                for (int i = 0; i < babyCryLogoData.size(); i++
                ) {
                    String crying_type;
                    switch(babyCryLogoData.get(i).getCryType()){
                        case 0 :
                            crying_type = "bellypain";
                            break;
                        case 1 :
                            crying_type = "discomfort";
                            break;
                        case 2 :
                            crying_type = "hungry";
                            break;
                        case 3 :
                            crying_type = "tired";
                            break;
                        default:
                            crying_type = "오류";
                    }
                    String[] parts = babyCryLogoData.get(i).getDateYMD().split("-");
                    ListAccountLog listdata = new ListAccountLog(i, babyCryLogoData.get(i).getDateYMD() +"  /  "+babyCryLogoData.get(i).getTimeHMS(), "Baby Crying. \n" + "내용 : " + crying_type);
                    list.add(listdata);

                }
            }
            adapter.notifyDataSetChanged();
            mRecyclerView.startLayoutAnimation();
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        logdateBtn = view.findViewById(R.id.logDate);

        logdateBtn.setOnClickListener(this::onClick);
        mRecyclerView = view.findViewById(R.id.Q_ALIST);
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.logDate){
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(((AppCompatActivity) requireActivity()).getSupportFragmentManager(),"datePicker");
        } else {
            Log.d("ERR1", "없는 버튼인데?");
        }
    }


    void initLogData() {
        //변화 감지

        Date date = new Date();
        // 원하는 형식으로 포맷
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Today = formatter.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1); // 하루 전날로 이동
        Yesterday = formatter.format(calendar.getTime());

        babyCryLogoDataObserver = new Observer<List<BabyCryLogoData>>() {
            @Override
            public void onChanged(List<BabyCryLogoData> babyCryLogoData) {
                Log.d("onChanged", "onchanee");
                if(babyCryLogoData.size() == 0){
                    list.clear();
                    ListAccountLog data = new ListAccountLog(1,"2024-10-24", "Baby Crying. \n내용 : discomfort");
                    ListAccountLog data2 = new ListAccountLog(2,"2024-10-24", "Baby Crying. \n내용 : belly_comfort");
                    ListAccountLog data3 = new ListAccountLog(3,"2024-10-24", "Baby Crying. \n내용 : tired");

                    //mArrayList.add(0, dict); //RecyclerView의 첫 줄에 삽입
                    list.add(data); // RecyclerView의 마지막 줄에 삽입
                    list.add(data2);
                    list.add(data3);
                }else{
                    list.clear();
                    for (int i = 0 ; i < babyCryLogoData.size(); i++)
                    {
                        String crying_type;
                        switch(babyCryLogoData.get(i).getCryType()){
                            case 0 :
                                crying_type = "bellypain";
                                break;
                            case 1 :
                                crying_type = "discomfort";
                                break;
                            case 2 :
                                crying_type = "hungry";
                                break;
                            case 3 :
                                crying_type = "tired";
                                break;
                            default:
                                crying_type = "오류";
                        }
                        String[] parts = babyCryLogoData.get(i).getDateYMD().split("-");
                        ListAccountLog listdata = new ListAccountLog(i, babyCryLogoData.get(i).getDateYMD() +"  /  "+babyCryLogoData.get(i).getTimeHMS(), "Baby Crying. \n" + "내용 : " + crying_type);
                        list.add(listdata);

                    }
                }
                adapter.notifyDataSetChanged();
                mRecyclerView.startLayoutAnimation();
            }
        };
        babyCryLogoDataLive = dataDao.getLogsBetweenDates(Yesterday,Today);
        babyCryLogoDataLive.observe(getViewLifecycleOwner(), babyCryLogoDataObserver);


    }
}