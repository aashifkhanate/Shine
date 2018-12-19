package com.manan.dev.shineymca.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.manan.dev.shineymca.Cordinator;
import com.manan.dev.shineymca.Developer;
import com.manan.dev.shineymca.R;

/**
 * Created by nisha on 6/15/2018.
 */

public class AboutFragment extends Fragment{

    private Button cordinator , developer;
    private View mView;

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       mView = inflater.inflate(R.layout.fragment_about, container, false);

       cordinator = (Button)mView.findViewById(R.id.button1);
       developer = (Button)mView.findViewById(R.id.button2);
       cordinator.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               loadFragment(new Cordinator());
           }
       });
       developer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               loadFragment(new Developer());
           }
       });
        return mView;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
