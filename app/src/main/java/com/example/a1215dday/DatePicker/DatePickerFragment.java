package com.example.a1215dday.DatePicker;

import android.accounts.Account;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.a1215dday.AccountFragment;
import com.example.a1215dday.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    AccountFragment accountFragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), R.style.MySpinnerDatePickerStyle ,this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String selectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);  // 월은 0부터 시작하므로 +1
        Bundle bundle = new Bundle();
        bundle.putString("selectedDate", selectedDate);
        // Fragment에 날짜 전달 (FragmentResultListener 사용)
        getParentFragmentManager().setFragmentResult("dateKey",bundle);
    }
}
