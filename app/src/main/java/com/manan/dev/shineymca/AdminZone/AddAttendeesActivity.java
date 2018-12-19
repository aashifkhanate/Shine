package com.manan.dev.shineymca.AdminZone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.manan.dev.shineymca.Models.User;
import com.manan.dev.shineymca.R;

public class AddAttendeesActivity extends AppCompatActivity {
    Button scanQRCode , addUserName;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String clubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendees);
        mAuth = FirebaseAuth.getInstance();
        clubName = mAuth.getCurrentUser().getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        scanQRCode = (Button)findViewById(R.id.qrcode_scan);
        addUserName = (Button)findViewById(R.id.add_username);
        addUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddAttendeesActivity.this , AddUserNameActivity.class));
            }
        });
        scanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(AddAttendeesActivity.this);
                integrator.initiateScan();
            }
        });

    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            final String userKey = scanResult.getContents();
            if(userKey != null ) {
                Log.d("prerna", userKey);
                databaseReference.child("Users").child(userKey).child("Clubs").child(clubName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("attendance")) {
                            Toast.makeText(AddAttendeesActivity.this, "Already marked", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            databaseReference.child("Users").child(userKey).child("Clubs").child(clubName).child("attendance").setValue("Present");
                            User user1 = new User(userKey, "Present");
                            databaseReference.child("Clubs").child(clubName).child("Attendees").push().setValue(user1);
                            Toast.makeText(AddAttendeesActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }
    }
}