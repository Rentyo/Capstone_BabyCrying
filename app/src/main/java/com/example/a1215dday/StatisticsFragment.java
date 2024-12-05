package com.example.a1215dday;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1215dday.DatePicker.DatePickerFragment;
import com.example.a1215dday.DatePicker.DatePickerFragment2;
import com.example.a1215dday.databinding.ActivityFragmentMainBinding;
import com.example.a1215dday.databinding.FragmentStatisticsBinding;
import com.example.a1215dday.room.BabyCryLogoDB;
import com.example.a1215dday.room.BabyCryLogoDao;
import com.example.a1215dday.room.BabyCryLogoData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StatisticsFragment extends Fragment {

    private BarChart chart;
    private FragmentStatisticsBinding binding;

    String Today;
    String Yesterday;

    String selectedDate;

    BabyCryLogoDB db =  BluetoothApplication.getDatabase();
    BabyCryLogoDao dataDao = db.BabyCryDao();;

    private Button selectbtn;
    private Button selectDate;
    private TextView tableTitle;
    private TextView difference;
    private TextView[] tableItems;
    private String selectedItem;
    public StatisticsFragment() {
        // Required empty public constructor
    }
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothManager blmanager = BluetoothManager.getInstance();
        binding = FragmentStatisticsBinding.inflate(getLayoutInflater());
        selectbtn = binding.chartChanger;
        selectDate = binding.statisticsDate;
        tableTitle = binding.selectedDateTotal;
        difference = binding.difference;
        tableItems = new TextView[]{binding.tableBellypain,binding.tableDiscomfort,binding.tableHungry,binding.tableTired};
        selectedItem = "default";
        Date date = new Date();
        // 원하는 형식으로 포맷
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //오늘 날짜 어제 날짜, 디폴트 날짜(오늘) 설정
        Today = formatter.format(date);
        selectedDate = formatter.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1); // 하루 전날로 이동
        Yesterday = formatter.format(calendar.getTime());


        //캘린더 리턴 설정
        getParentFragmentManager().setFragmentResultListener("statistics", this, (requestKey, result) -> {
            String selectedDate = result.getString("selectedDate");
            // 날짜 처리
            Log.d("Selected Date", selectedDate);
            // 날짜에 맞는 데이터 로드 로직 작성
            try {
                loadDataForDate(selectedDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });


        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.chartChanger){
                    final String items [] = new String[] {"Day","Week", "Month"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("차트 선택").setSingleChoiceItems(
                            items,
                            -1,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), items[which], Toast.LENGTH_SHORT).show();
                                    selectedItem = items[which];
                                }
                            }
                    );
                    dialog.setPositiveButton("확인",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                setData(chart, selectedItem);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });  // 확인 버튼 추가
                    dialog.show(); // 다이얼로그 표시
                } else if (v.getId() == R.id.statisticsDate) {
                    DialogFragment newFragment = new DatePickerFragment2();
                    newFragment.show(((AppCompatActivity) requireActivity()).getSupportFragmentManager(),"datePicker");
                } else {
                    Log.d("ERR1", "없는 버튼인데?");
                }
            }
        };
        selectbtn.setOnClickListener(listener);
        selectDate.setOnClickListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        chart = binding.chart;
        //초기 설정 - 이제 오늘로 바꿀거야
        initBarChart(chart);
        Log.d("data", "data");
        try {
            setData(chart , selectedItem);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return binding.getRoot();
    }

    private void loadDataForDate(String selectedDate) throws ParseException {
        // 선택된 날짜에 맞는 데이터 로드 로직
        this.selectedDate = selectedDate;
        setData(chart, selectedItem);

    }




    private void setData(BarChart barChart, String mode) throws ParseException {

        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false);

        ArrayList<BarEntry> valueList = new ArrayList<BarEntry>();
        String title = "";
        int num = mode == "Week" ? 7 : mode == "Month" ? 31 : 24;

        List<BabyCryLogoData> babyCryLogoData;

        String pattern = "yyyy-MM-dd";
        Date date1=new SimpleDateFormat(pattern).parse(selectedDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();calendar.setTime(date1);


        //데이터를 가지고 올거야
        if(mode.equals("Week")){
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            String startDate = simpleDateFormat.format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
            String finishDate = simpleDateFormat.format(calendar.getTime());
            Log.d("날짜", startDate + "/" + finishDate );
            babyCryLogoData = dataDao.getDataBetweenDates(startDate, finishDate);

            Map<String, Map<Integer, Long>> analysis = analyzeDataByDay(babyCryLogoData,startDate,finishDate);

            analysis.forEach((date, cryTypeCounts) -> {
                try {
                    float[] data = new float[4];
                    Arrays.fill(data, 0.0f);
                    Date start = simpleDateFormat.parse(startDate);
                    // date를 Date 객체로 변환
                    Date currentDate = simpleDateFormat.parse(date);

                    // 날짜 차이 계산
                    long diffInMillis = currentDate.getTime() - start.getTime();
                    long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);  // 날짜 차이를 일 단위로 변환

                    Log.d("날짜", "Date: " + date + " --> 순서: " + diffInDays);
                    cryTypeCounts.forEach((cryType, count) ->
                            data[(int)cryType] = (float)count);
                    valueList.add(new BarEntry((float)diffInDays, data));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });

            List<BabyCryLogoData> todaydata = dataDao.getLogsDates(selectedDate);

            if (todaydata.size() > (babyCryLogoData.size()/7)) {
                difference.setText("일주일 평균보다 빈도수가 많습니다.\n일주일 평균 : " + String.valueOf((babyCryLogoData.size()/7)) + " 오늘 : " + String.valueOf(todaydata.size()));
            } else {
                if (todaydata.size() < (babyCryLogoData.size()/7)) {
                    difference.setText("일주일 평균보다 빈도수가 적습니다.\n일주일 평균 : " + String.valueOf((babyCryLogoData.size()/7)) + " 오늘 : " + String.valueOf(todaydata.size()));
                } else {
                    difference.setText("평균치입니다.\n" + "데이터 수 : " + String.valueOf(babyCryLogoData.size()/7));
                }
            }

        } else if(mode.equals("Month")){
            calendar.set(Calendar.DAY_OF_MONTH,1);
            String startDate = simpleDateFormat.format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String finishDate = simpleDateFormat.format(calendar.getTime());
            Log.d("날짜", startDate + "/" + finishDate );
            babyCryLogoData = dataDao.getDataBetweenDates(startDate, finishDate);

            Map<String, Map<Integer, Long>> analysis = analyzeDataByDay(babyCryLogoData, startDate, finishDate);

            // 결과 출력
            analysis.forEach((date, cryTypeCounts) -> {
                try {
                    float[] data = new float[4];
                    Arrays.fill(data, 0.0f);
                    Date start = simpleDateFormat.parse(startDate);
                    // date를 Date 객체로 변환
                    Date currentDate = simpleDateFormat.parse(date);

                    // 날짜 차이 계산
                    long diffInMillis = currentDate.getTime() - start.getTime();
                    long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);  // 날짜 차이를 일 단위로 변환

                    Log.d("날짜", "Date: " + date + " --> 순서: " + diffInDays);
                    cryTypeCounts.forEach((cryType, count) ->
                            data[(int)cryType] = (float)count);
                    valueList.add(new BarEntry((float)diffInDays, data));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;

            try {
                date = formatter.parse(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);

            int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Log.d("마지막날", String.valueOf(lastDayOfMonth));

            List<BabyCryLogoData> todaydata = dataDao.getLogsDates(selectedDate);
            if (todaydata.size() > (babyCryLogoData.size()/lastDayOfMonth)) {
                difference.setText("한달 평균보다 빈도수가 많습니다.\n한달 평균 : " + String.valueOf((babyCryLogoData.size()/lastDayOfMonth)) + " 오늘 : " + String.valueOf(todaydata.size()));
            } else {
                if (todaydata.size() < (babyCryLogoData.size()/lastDayOfMonth)) {
                    difference.setText("한달 평균보다 빈도수가 적습니다.\n한달 평균 : " + String.valueOf((babyCryLogoData.size()/lastDayOfMonth)) + " 오늘 : " + String.valueOf(todaydata.size()));
                } else {
                    difference.setText("평균치입니다.\n" + "데이터 수 : " + String.valueOf(babyCryLogoData.size()/lastDayOfMonth));
                }
            }



        } else{
            Log.d("selecteDate", selectedDate);
            babyCryLogoData = dataDao.getLogsDates(selectedDate);
            Log.d("babyCryLogoData", String.valueOf(babyCryLogoData.size()));
            Map<Integer, Map<Integer, Long>> analysis = analyzeData(babyCryLogoData);
            analysis.forEach((hour, cryTypeCounts) -> {
                float[] data = new float[4];
                Arrays.fill(data, 0.0f);
                cryTypeCounts.forEach((cryType, count) ->{
                    Log.d("cryType", String.valueOf(cryType) + String.valueOf(count));
                        data[(int)cryType] = (float)count;
                }
                );
                valueList.add(new BarEntry((float)hour, data));
            });

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dateToday = null;

            try {
                dateToday = formatter.parse(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            calendar.setTime(dateToday);
            calendar.add(Calendar.DAY_OF_MONTH, -1); // 하루 빼기

            String Yesterday = formatter.format(calendar.getTime());
            List<BabyCryLogoData> yesterdata = dataDao.getLogsDates(Yesterday);

            if (yesterdata.size() > babyCryLogoData.size()) {
                difference.setText("울음 소리 빈도가 줄었습니다.\n어제 : " + String.valueOf(yesterdata.size()) + " 오늘 : " + String.valueOf(babyCryLogoData.size()));
            } else {
                if (yesterdata.size() < babyCryLogoData.size()) {
                    difference.setText("울음 소리 빈도가 늘어났습니다.\n어제 : " + String.valueOf(yesterdata.size()) + " 오늘 : " + String.valueOf(babyCryLogoData.size()));
                } else {
                    difference.setText("울음 소리 빈도가 그대로입니다.\n" + "데이터 수 : " + String.valueOf(babyCryLogoData.size()));
                }
            }
        }

        babyCryLogoData.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.dateYMD,  // 날짜를 구분하는 키
                        Collectors.groupingBy(
                                entry -> entry.getCryType(),  // cryType을 구분하는 키
                                Collectors.counting()  // 개수 세기
                        )
                ))
                .forEach((date, cryTypeCountMap) -> {
                    cryTypeCountMap.forEach((cryType, count) -> {
                        tableItems[cryType].setText(String.valueOf(count));
                        tableItems[cryType].setTextColor(Color.BLACK);
                    });
                });

        tableTitle.setText("선택된 날짜 : " + selectedDate + "\n"+mode +" Total_Data : " +String.valueOf(babyCryLogoData.size()));
        tableTitle.setTextColor(Color.BLACK);
        for (BarEntry entry : valueList) {
            Log.d("BarEntry", "Hour: " + entry.getX() + ", Data: " + Arrays.toString(entry.getYVals()));
        }
        BarDataSet barDataSet = new BarDataSet(valueList, title);
        // 바 색상 설정 (ColorTemplate.LIBERTY_COLORS)
        // 초과된 개수는 색상 처음부터 반복
        barDataSet.setColors(
                Color.rgb(240, 117, 170),  // 데이터 1 색상
                Color.rgb(180, 90, 148),  // 데이터 2 색상
                Color.rgb(173, 216, 153),  // 데이터 3 색상
                Color.rgb(33, 156, 144)   // 데이터 4 색상
        );
        barDataSet.setStackLabels(new String[]{"복통", "불편함", "배고픔", "피곤함"});

        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.invalidate();

    }

    //day에 따른 데이터

    public static Map<String, Map<Integer, Long>> analyzeDataByDay(List<BabyCryLogoData> data, String start, String end) {
        // 날짜 범위 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        // 결과 저장소: Map<날짜, Map<cryType, 개수>>
        Map<String, Map<Integer, Long>> result = new HashMap<>();

        // 데이터 처리
        data.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.dateYMD,  // 날짜를 구분하는 키
                        Collectors.groupingBy(
                                entry -> entry.getCryType(),  // cryType을 구분하는 키
                                Collectors.counting()  // 개수 세기
                        )
                ))
                .forEach((date, cryTypeCountMap) -> {
                    result.put(date, cryTypeCountMap);  // 결과 저장
                });

        // 날짜 범위 내 모든 날짜를 확인하고 빈값을 채우기
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(formatter);
            result.putIfAbsent(dateStr, new HashMap<>());

            // cryType 0, 1, 2, 3에 대해 빈값을 0으로 채우기
            Map<Integer, Long> cryTypeCountMap = result.get(dateStr);
            for (int cryType = 0; cryType <= 3; cryType++) {
                cryTypeCountMap.putIfAbsent(cryType, 0L);
            }
        }

        return result;
    }

    //시간에 따른 데이터
    public static Map<Integer, Map<Integer, Long>> analyzeData(List<BabyCryLogoData> data) {
        // 결과 저장소 초기화: Map<시간대, Map<cryType, 개수>>
        Map<Integer, Map<Integer, Long>> result = new HashMap<>();

        // 시간대(0~23)와 cryType(0~3)을 초기화
        for (int hour = 0; hour < 24; hour++) {
            Map<Integer, Long> cryTypeMap = new HashMap<>();
            for (int cryType = 0; cryType < 4; cryType++) {
                cryTypeMap.put(cryType, 0L); // 기본값 0L
            }
            result.put(hour, cryTypeMap);
        }

        // 데이터 처리
        data.stream()
                .collect(Collectors.groupingBy(
                        // 시간대를 구분하는 키
                        entry -> LocalTime.parse(entry.timeHMS).getHour(),
                        Collectors.groupingBy(
                                // cryType을 구분하는 키
                                BabyCryLogoData::getCryType,
                                Collectors.counting() // 개수 세기
                        )
                ))
                .forEach((hour, cryTypeCounts) ->
                        cryTypeCounts.forEach((cryType, count) ->
                                result.get(hour).put(cryType, count) // 해당 시간대와 cryType에 카운트 설정
                        )
                );

        return result;
    }

    private void initBarChart(BarChart barChart) {
        // 차트 회색 배경 설정 (default = false)
        barChart.setDrawGridBackground(false);
        // 막대 그림자 설정 (default = false)
        barChart.setDrawBarShadow(false);
        // 차트 테두리 설정 (default = false)
        barChart.setDrawBorders(false);

        Description description = new Description();
        // 오른쪽 하단 모서리 설명 레이블 텍스트 표시 (default = false)
        description.setEnabled(false);
        barChart.setDescription(description);

        // X, Y 바의 애니메이션 효과
        barChart.animateY(1000);
        barChart.animateX(1000);



        // 바텀 좌표 값
        XAxis xAxis = barChart.getXAxis();
        // x축 위치 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 그리드 선 수평 거리 설정
        xAxis.setGranularity(0.5f);
        // x축 텍스트 컬러 설정
        xAxis.setTextColor(Color.BLACK);
        // x축 선 설정 (default = true)
        xAxis.setDrawAxisLine(false);
        // 격자선 설정 (default = true)
        xAxis.setDrawGridLines(false);



        YAxis leftAxis = barChart.getAxisLeft();

        leftAxis.setValueFormatter(new IntegerValueFormatter());
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false);
        // 좌측 텍스트 컬러 설정
        leftAxis.setTextColor(Color.BLACK);

        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f); // 간격을 1로 설정

        YAxis rightAxis = barChart.getAxisRight();
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false);
        rightAxis.setGranularity(1f);
        rightAxis.setValueFormatter(new IntegerValueFormatter());
        // 우측 텍스트 컬러 설정
        rightAxis.setTextColor(Color.BLACK);

        rightAxis.setAxisMinimum(0.0f);
        // 바차트의 타이틀
        Legend legend = barChart.getLegend();
        // 범례 모양 설정 (default = 정사각형)
        legend.setForm(Legend.LegendForm.CIRCLE);

        // 타이틀 텍스트 사이즈 설정
        legend.setTextSize(20f);
        // 타이틀 텍스트 컬러 설정
        legend.setTextColor(Color.BLACK);
        // 범례 위치 설정
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        // 범례 방향 설정
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        // 차트 내부 범례 위치하게 함 (default = false)
        legend.setDrawInside(false);


    }
}