package com.manan.dev.shineymca.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manan.dev.shineymca.R;

/**
 * Created by nisha on 6/15/2018.
 */

public class NotificationFragment extends android.support.v4.app.Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}
