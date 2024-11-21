package com.example.a1215dday;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.a1215dday.databinding.ActivityFragmentMainBinding;
import com.example.a1215dday.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private BarChart chart;
    private FragmentStatisticsBinding binding;

    private Button selectbtn;

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
        Log.d("statics",String.valueOf(blmanager.getBluetoothSocket().isConnected()));
        binding = FragmentStatisticsBinding.inflate(getLayoutInflater());
        selectbtn = binding.chartChanger;

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
                            setData(chart, selectedItem);
                        }
                    });  // 확인 버튼 추가
                    dialog.show(); // 다이얼로그 표시
                } else {
                    Log.d("ERR1", "없는 버튼인데?");
                }
            }
        };
        selectbtn.setOnClickListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        chart = binding.chart;
        Log.d("init", "init");
        initBarChart(chart);
        Log.d("data", "data");
        setData(chart , "default");
        return binding.getRoot();
    }
    private void setData(BarChart barChart, String mode) {

        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false);

        ArrayList<BarEntry> valueList = new ArrayList<BarEntry>();
        String title = "울음 수치";
        int num = mode == "Week" ? 7 : mode == "Month" ? 31 : 24;
        // 임의 데이터
        for (int i = 0 ; i < num ; i++) {
            valueList.add(new BarEntry((float) i, i + (float)(Math.random() * 10)));
        }

        BarDataSet barDataSet = new BarDataSet(valueList, title);
        // 바 색상 설정 (ColorTemplate.LIBERTY_COLORS)
        barDataSet.setColors(
                Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
                Color.rgb(118, 174, 175), Color.rgb(42, 109, 130));

        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.invalidate();
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
        xAxis.setGranularity(1f);
        // x축 텍스트 컬러 설정
        xAxis.setTextColor(Color.RED);
        // x축 선 설정 (default = true)
        xAxis.setDrawAxisLine(false);
        // 격자선 설정 (default = true)
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false);
        // 좌측 텍스트 컬러 설정
        leftAxis.setTextColor(Color.BLUE);

        YAxis rightAxis = barChart.getAxisRight();
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false);
        // 우측 텍스트 컬러 설정
        rightAxis.setTextColor(Color.GREEN);

        // 바차트의 타이틀
        Legend legend = barChart.getLegend();
        // 범례 모양 설정 (default = 정사각형)
        legend.setForm(Legend.LegendForm.LINE);
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