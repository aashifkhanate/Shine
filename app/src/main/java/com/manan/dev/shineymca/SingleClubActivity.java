package com.manan.dev.shineymca;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manan.dev.shineymca.Fragments.HomeFragment;
import com.manan.dev.shineymca.Models.Club;
import com.manan.dev.shineymca.Models.Round;
import com.manan.dev.shineymca.Models.RoundStatus;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SingleClubActivity extends AppCompatActivity {

    private RecyclerView mRoundCircles;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String clubName;
    private String clubDescription;
    private String mRound;
    private String mRname;
    private View mView;
    private TextView mClubName;
    private TextView mClubDescription;
    Round round;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_club);
        Intent intent=getIntent();
        clubName=intent.getStringExtra("clubName");
        clubDescription=intent.getStringExtra("clubDescription");
        mRoundCircles = (RecyclerView)findViewById(R.id.club_round_circles);
        mClubName=(TextView)findViewById(R.id.single_mclub_name);
        mClubDescription=(TextView)findViewById(R.id.single_mclub_description);
        mClubName.setText(clubName);
        mClubDescription.setText(clubDescription);
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(SingleClubActivity.this,clubName,Toast.LENGTH_SHORT).show();
        //mRoundCircles.setHasFixedSize(true);
        mRoundCircles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        String uid = mAuth.getCurrentUser().getUid().toString();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Round, RoundViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Round, RoundViewHolder>(
                Round.class,
                R.layout.layout_round,
                RoundViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("Rounds")
        ) {

            @Override
            protected void populateViewHolder(RoundViewHolder viewHolder, Round model, final int position) {
                viewHolder.setRoundNum(String.valueOf(model.getNumber()));
                viewHolder.setRoundName(model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(SingleClubActivity.this, RoundActivity.class).putExtra("clubName", clubName).putExtra("round", String.valueOf(position+1)));}
            });

            }

        };
        mRoundCircles.setAdapter(firebaseRecyclerAdapter);


    }

    public static class RoundViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView roundNum;
        TextView roundName;
        public RoundViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            roundNum =(TextView)mView.findViewById(R.id.lay_round_number);
            roundName=(TextView)mView.findViewById(R.id.lay_round_name);
        }

        public void setRoundNum(String roundNo) {

            roundNum.setText(roundNo);

        }

        public void setRoundName(String roundNam) {

            roundName.setText(roundNam);
        }
    }
}
