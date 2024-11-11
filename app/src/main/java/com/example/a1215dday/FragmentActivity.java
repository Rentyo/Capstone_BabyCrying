package com.example.a1215dday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.a1215dday.databinding.ActivityFragmentMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentActivity extends AppCompatActivity{
    ActivityFragmentMainBinding binding;
    ViewPageAdapter pagerAdapter;
    ViewPager2 viewPager2;
    Fragment statisticsFr;
    Fragment accountFr;

    BluetoothManager blmanager;
    // 메인 페이지 AppCompatActivity를 중심으로
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("시작", "블루투스 소켓 닫히는 시점 파악 시작 2");
        blmanager = BluetoothManager.getInstance();
        binding = ActivityFragmentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewPager2 = binding.mainPageFrame;
        pagerAdapter = new ViewPageAdapter(this);

        viewPager2.registerOnPageChangeCallback
                (new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // ViewPager의 페이지가 선택될 때 BottomNavigationView의 해당 메뉴 항목을 체크 상태로 설정
                binding.bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });

        pagerAdapter.createFragment(0);
        pagerAdapter.createFragment(1);
        viewPager2.setAdapter(pagerAdapter);
        viewPager2.setUserInputEnabled(false);
        // 리스너 연결
        binding.bottomNav.setOnItemSelectedListener(
                item -> {
                    if(item.getItemId() == R.id.statistics){
                        binding.mainPageFrame.setCurrentItem(0);
                        return true;
                    } else if (item.getItemId() == R.id.account) {
                        binding.mainPageFrame.setCurrentItem(1);
                        return true;
                    }else{
                        return false;
                    }
                }
        );
    }


}
