package com.manan.dev.shineymca.AdminZone;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.shineymca.AdminZone.AdminFragments.AddDescriptionFragment;
import com.manan.dev.shineymca.BottomNavigator;
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.Utility.Methods;

public class AdminHomeActivity extends AppCompatActivity {

    TextView mAddDescription, mRounds, mViewAttendees, mResult, mCoordinators,mAddAttendees;
    private FirebaseAuth mAuth;
    private String clubName;
    EditText input1;
    private DatabaseReference mDescriptionReference;
    private ChildEventListener mDescriptionEventListener;
    String mClubDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        mAuth = FirebaseAuth.getInstance();
        clubName = mAuth.getCurrentUser().getDisplayName();
        initializeVariables();
        Toast.makeText(this, mAuth.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
        setListeners();
        mClubDescription = "Add Description Here";
        Toast.makeText(this, clubName, Toast.LENGTH_SHORT).show();
        mDescriptionReference = FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName);
    }

    //giving reference to all the variables
    private void initializeVariables() {
        mAddDescription = (TextView) findViewById(R.id.tv_add_description);
        mRounds = (TextView) findViewById(R.id.admin_home_rounds_all);
        mViewAttendees = (TextView) findViewById(R.id.tv_view_registered);
        mResult = (TextView) findViewById(R.id.tv_result);
        mCoordinators = (TextView) findViewById(R.id.tv_add_coordinators);
        mAddAttendees = (TextView)findViewById(R.id.tv_add_attendees);

    }

    //adding listeners to all the TextViews
    private void setListeners() {
        //open a dial

        // og box to add/ update the description of a club
        mAddDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDescriptionFragment fragment = new AddDescriptionFragment();
                FragmentManager fm = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("description", mClubDescription);

                fragment.setArguments(bundle);

                fragment.show(fm, "Add Description");
            }
        });

        //open activity to add new event
        mRounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, AdminAllRoundsActivity.class)
                        .putExtra("clubName",clubName));
//                startActivity(new Intent(AdminHomeActivity.this, AddRoundActivity.class));
            }
        });

        //open activity to add view coordinators
        //in new activity add coordinators using dialog fragment
        mCoordinators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, AddCoordinator.class));
            }
        });

        //open activity to view th list of attendees
        mViewAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //open activity to perform all activities related to results
        mResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mAddAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this,AddAttendeesActivity.class));
            }
        });
    }

    private boolean checkDetails() {

        if (!isNetworkAvailable()) {
            Toast.makeText(AdminHomeActivity.this, "No Internet Access", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (input1.getText().toString().equals("")) {
            input1.setError("Enter a Name");
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            Methods.callSharedPreference(getApplicationContext(), "default");
            startActivity(new Intent(AdminHomeActivity.this, BottomNavigator.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    private void addListener() {
        if(mDescriptionEventListener == null){
            mDescriptionEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.getKey().equals("clubDescription")){
                        mClubDescription = dataSnapshot.getValue().toString();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.getKey().equals("clubDescription")){
                        mClubDescription = dataSnapshot.getValue().toString();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getKey().equals("clubDescription")){
                        mClubDescription = dataSnapshot.getValue().toString();
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDescriptionReference.addChildEventListener(mDescriptionEventListener);
        }
    }

    private void removeListener() {
        if(mDescriptionEventListener != null){
            mDescriptionReference.removeEventListener(mDescriptionEventListener);
            mDescriptionEventListener = null;
        }
    }
}
