package com.example.a1215dday;

import android.accounts.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private StatisticsFragment fragment1;
    private AccountFragment fragment2;
    public ViewPageAdapter(AppCompatActivity activity) {
        super(activity);
        mFragmentList.add(new StatisticsFragment());
        mFragmentList.add(new AccountFragment());
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}
