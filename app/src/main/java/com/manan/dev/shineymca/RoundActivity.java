package com.manan.dev.shineymca;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manan.dev.shineymca.AdminZone.AddRoundActivity;
import com.manan.dev.shineymca.Models.Coordinator;
import com.manan.dev.shineymca.Models.Round;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RoundActivity extends AppCompatActivity {

    private TextView mClubName,mRoundName,mRoundDescription,mSpecialNotes,mDate,mTime,mVenue;
    private String clubName, roundNo;
    ImageView mRoundPoster;
    LinearLayout coordsLinearLayout,faqsLinearLayout;
    Round round;
    private DatabaseReference mRoundReference;
    private ChildEventListener mRoundListener;
    private DatabaseReference mRef, mUserRef;
    private Button roundRegBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);
        initializeVariable();
    }
    private void initializeVariable() {
        mRoundDescription=(TextView)findViewById(R.id.tv_description) ;
        mSpecialNotes=(TextView)findViewById(R.id.tv_special_note);
        mDate=(TextView)findViewById(R.id.tv_round_date);
        mTime=(TextView)findViewById(R.id.tv_round_start_time);
        mVenue=(TextView)findViewById(R.id.tv_round_loc);
        coordsLinearLayout=(LinearLayout)findViewById(R.id.ll_coordinators);
        faqsLinearLayout=(LinearLayout)findViewById(R.id.ll_faqs);
        clubName = getIntent().getStringExtra("clubName");
        roundNo = getIntent().getStringExtra("round");
        roundRegBtn = (Button)findViewById(R.id.act_round_register_btn);
        round=new Round();
        mRoundPoster=(ImageView)findViewById(R.id.round_img);
        mRoundReference = FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("Rounds");

    }
    private void displayData() {
        if(round==null)
            return;
        Picasso.get().load(round.getPoster()).into(mRoundPoster);
        mRoundDescription.setText(round.getDescription());
        mVenue.setText(round.getVenue());
        mSpecialNotes.setText(round.getSpecialNotes());
        SpannableString s = new SpannableString(round.getName());
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, round.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(round.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(cal.getTime());
        mDate.setText(formattedDate);
        cal.setTimeInMillis(round.getTime());
        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm", Locale.US);
        String formattedTime = sdf1.format(cal.getTime());
        mTime.setText(formattedTime);
        for(int i = 0; i < round.getCoordinators().size(); i++){
            @SuppressLint("InflateParams") LinearLayout view = (LinearLayout) LayoutInflater.from(RoundActivity.this).inflate(R.layout.layout_single_round_coordinators, null,false);
            ((TextView) view.findViewById(R.id.tv_coords_name)).setText(round.getCoordinators().get(i).getCoordName());
            ((TextView) view.findViewById(R.id.tv_coords_phone_no)).setText(round.getCoordinators().get(i).getCoordPhone());
            coordsLinearLayout.addView(view);
        }

        for(int i = 0; i < round.getFaq().size(); i++){
            @SuppressLint("InflateParams") LinearLayout view = (LinearLayout) LayoutInflater.from(RoundActivity.this).inflate( R.layout.layout_single_round_faq, null,false);
            ((TextView) view.findViewById(R.id.tv_round_question)).setText(round.getFaq().get(i).getQuestion());
            ((TextView) view.findViewById(R.id.tv_round_answer)).setText(round.getFaq().get(i).getAnswer());
            faqsLinearLayout.addView(view);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseListener();
    }



    @Override
    protected void onPause() {
        super.onPause();
        detatchDatabaseListener();
        coordsLinearLayout.removeAllViews();
        faqsLinearLayout.removeAllViews();

    }
    //Database Listeners
    private void attachDatabaseListener() {

        final long totalRegistered = 0;

        mRef = FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("Rounds")
                .child(roundNo).child("totalRegistered");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                totalRegistered = dataSnapshot.getValue();
            }

        @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser)
                .child(clubName).child("Rounds").child(roundNo).child("status");

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(RoundActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                if(dataSnapshot.getValue() == null){
                    roundRegBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mUserRef.setValue("registered");
                            roundRegBtn.setText("Registered");
//                            mRef.
                        }
                    });
                }else{
                    roundRegBtn.setText("Registered");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (mRoundListener == null) {
            mRoundListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("OnAdded", dataSnapshot.toString());

                        if (dataSnapshot.getKey().equals(roundNo)) {
                            round = dataSnapshot.getValue(Round.class);
                            displayData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                int a=2;
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("OnChanged", dataSnapshot.toString());

                        if (dataSnapshot.getKey().equals(String.valueOf(roundNo))) {
                            round = dataSnapshot.getValue(Round.class);
                            displayData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getKey().equals(String.valueOf(roundNo))) {
                        round = null;
                    }
                    displayData();
                    Toast.makeText(RoundActivity.this, "Sorry! This event no longer exists!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mRoundReference.addChildEventListener(mRoundListener);
        }


    }
    private void detatchDatabaseListener() {
        if (mRoundListener != null) {
            mRoundReference.removeEventListener(mRoundListener);
            mRoundListener = null;
        }
    }
}
