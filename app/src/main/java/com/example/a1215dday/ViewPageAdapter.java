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
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                fragment1 = new StatisticsFragment();
                addFragment(fragment1);
                break;
            case 1:
                fragment2 = new AccountFragment();
                addFragment(fragment2);
                break;
            default:
                return null;
        }

        return mFragmentList.get(position);

    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}
