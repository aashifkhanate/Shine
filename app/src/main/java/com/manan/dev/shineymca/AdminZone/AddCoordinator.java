package com.manan.dev.shineymca.AdminZone;

import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.shineymca.Adapters.CoordinatorAdapter;
import com.manan.dev.shineymca.AdminZone.AdminFragments.AddCoordinatorFragment;
import com.manan.dev.shineymca.Models.Coordinator;
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.Utility.Methods;

import java.util.ArrayList;

public class AddCoordinator extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;
    ArrayList<Coordinator> mCoordinators;
    RecyclerView mCoordinatorView;
    private CoordinatorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coordinator);

        mCoordinators = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Coordinators");

        mCoordinatorView = (RecyclerView) findViewById(R.id.rv_coordinators);
        mCoordinatorView.setLayoutManager(new LinearLayoutManager(AddCoordinator.this));

        adapter = new CoordinatorAdapter(AddCoordinator.this, mCoordinators);
        mCoordinatorView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_coordinator_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            FragmentManager fm = getFragmentManager();
            AddCoordinatorFragment fragment = new AddCoordinatorFragment();
            fragment.show(fm, "Add Coordinator");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCoordinators.clear();
        removeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    private void addListener() {
        if(mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Log.d("yatin", dataSnapshot.toString());
                    try {
                        if (dataSnapshot.getKey().equals(Methods.getEmailSharedPref(getApplicationContext()))) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d("yatin", snapshot.getValue().toString());
                                Coordinator coordinator = snapshot.getValue(Coordinator.class);
                                coordinator.setCoordId(snapshot.getKey());
                                mCoordinators.add(coordinator);
                            }
                        }
                        Log.d("yd", String.valueOf(mCoordinators.size()));
                        if(mCoordinators.size() == 0){
                            Log.d("yd", "yippie");
                            mCoordinatorView.setVisibility(View.GONE);
                        } else {
                            mCoordinatorView.setVisibility(View.VISIBLE);
                            adapter = new CoordinatorAdapter(AddCoordinator.this, mCoordinators);
                            mCoordinatorView.setAdapter(adapter);
                        }
                    } catch (Exception e){
                        Log.d("yd", e.getMessage());
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        mCoordinators.clear();
                        if (dataSnapshot.getKey().equals(Methods.getEmailSharedPref(getApplicationContext()))) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d("changed", snapshot.getValue().toString());
                                Coordinator coordinator = snapshot.getValue(Coordinator.class);
                                coordinator.setCoordId(snapshot.getKey());
                                mCoordinators.add(coordinator);
                            }
                        }
                        Log.d("yd", String.valueOf(mCoordinators.size()));
                        if(mCoordinators.size() == 0){
                            Log.d("yd", "yippie");
                            mCoordinatorView.setVisibility(View.GONE);
                        } else {
                            mCoordinatorView.setVisibility(View.VISIBLE);
                            adapter = new CoordinatorAdapter(AddCoordinator.this, mCoordinators);
                            mCoordinatorView.setAdapter(adapter);
                        }
                    } catch (Exception e){
                        Log.d("yd", e.getMessage());
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        mCoordinators.clear();
                        if (dataSnapshot.getKey().equals(Methods.getEmailSharedPref(getApplicationContext()))) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d("removed", snapshot.getValue().toString());
                                Coordinator coordinator = snapshot.getValue(Coordinator.class);
                                coordinator.setCoordId(snapshot.getKey());
                                mCoordinators.remove(coordinator);
                            }
                        }
                        Log.d("yd", String.valueOf(mCoordinators.size()));
                        if(mCoordinators.size() == 0){
                            Log.d("yd", "yippie");
                            mCoordinatorView.setVisibility(View.GONE);
                        } else {
                            mCoordinatorView.setVisibility(View.VISIBLE);
                            adapter = new CoordinatorAdapter(AddCoordinator.this, mCoordinators);
                            mCoordinatorView.setAdapter(adapter);
                        }
                    } catch (Exception e){
                        Log.d("yd", e.getMessage());
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void removeListener() {
        if(mChildEventListener != null){
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
