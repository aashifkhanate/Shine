package com.manan.dev.shineymca.AdminZone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manan.dev.shineymca.Models.User;
import com.manan.dev.shineymca.R;

public class AddUserNameActivity extends AppCompatActivity {
    private EditText addUserName;
    private DatabaseReference databaseReference;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private String userName;
    private String clubName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_name);
        mAuth = FirebaseAuth.getInstance();
        clubName = mAuth.getCurrentUser().getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        addUserName = (EditText)findViewById(R.id.et_username);
        submitButton = (Button)findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = addUserName.getText().toString();
                databaseReference.child("Usernames").child(userName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userKey = (String) dataSnapshot.getValue();
                        Log.d("prerna",userKey);
                        databaseReference.child("Users").child(userKey).child("Clubs").child(clubName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.hasChild("attendance")) {
                                    Toast.makeText(AddUserNameActivity.this, "Already marked", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("Users").child(userKey).child("Clubs").child(clubName).child("attendance").setValue("Present");
                                    User user1 = new User(userKey, "Present");
                                    databaseReference.child("Clubs").child(clubName).child("Attendees").push().setValue(user1);
                                    Toast.makeText(AddUserNameActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }
}
