package com.example.a1215dday;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class IntegerValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value); // Float 값을 정수로 변환
    }
}
