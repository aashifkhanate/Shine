package com.manan.dev.shineymca.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.RegisterSecondActivity;
import com.manan.dev.shineymca.SingleClubActivity;

/**
 * Created by nisha on 6/15/2018.
 */

public class CalenderFragment extends android.support.v4.app.Fragment{

    private TextView textView, textView2;
    private View mView;
    private CalendarView mCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_calender, container, false);
        textView = (TextView)mView.findViewById(R.id.calendar_club);
        mCalendar = (CalendarView)mView.findViewById(R.id.calender_calendar);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SingleClubActivity.class));
            }
        });

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String s = String.valueOf(mCalendar.getDate());
                Log.d("dekh", s);
                Log.d("Hi", String.valueOf(i) + " " + String.valueOf(i1) + " " + String.valueOf(i2));
                Toast.makeText(getContext(), "Akki: " + String.valueOf(mCalendar.getMaxDate()), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Date: " + s, Toast.LENGTH_SHORT).show();
            }
        });

        return mView;
    }
}
