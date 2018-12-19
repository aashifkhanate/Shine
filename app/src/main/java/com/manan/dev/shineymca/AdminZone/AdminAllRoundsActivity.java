package com.manan.dev.shineymca.AdminZone;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.SingleClubActivity;
import com.wajahatkarim3.easyflipview.EasyFlipView;

public class AdminAllRoundsActivity extends AppCompatActivity {

    private ImageView mAddRound;
    private RecyclerView mRoundList;
    private String clubName;
    private DatabaseReference mRoundCount;
    private DatabaseReference mClubName;
    private long totalRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rounds_all);
        Bundle extras=getIntent().getExtras();
        if(extras==null){

        }
        else{
            clubName = extras.getString("clubName");
        }
        clubName="Manan";
        mRoundList = findViewById(R.id.admin_rounds_all_rounds_list);
        mRoundList.setHasFixedSize(true);
        mRoundList.setLayoutManager(new LinearLayoutManager(this));
        mAddRound = findViewById(R.id.admin_rounds_all_add_round);
        mClubName =    FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName);
        mRoundCount = FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("roundCount");
        addDatabaseListener();
        mAddRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminAllRoundsActivity.this, AddRoundActivity.class)
                        .putExtra("roundNumber", totalRound+1));
            }
        });

        clubName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

    }
    private void addDatabaseListener() {
        mRoundCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalRound=Long.parseLong(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Round, RoundViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Round, RoundViewHolder>(
                Round.class,
                R.layout.admin_single_round_in_admin_rounds_all_card,
                RoundViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("Rounds")
        ) {

            @Override
            protected void populateViewHolder(RoundViewHolder viewHolder, final Round model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setNumber(model.getNumber());
                viewHolder.mView.findViewById(R.id.rounds_all_specific_round_intent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AdminAllRoundsActivity.this, AddRoundActivity.class)
                                .putExtra("roundNumber", model.getNumber()));
                    }
                });
//                on click delete round
                viewHolder.mView.findViewById(R.id.delete_specific_round).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Delete Round");
                        builder.setMessage("Are you sure you want to delete Round "+String.valueOf(model.getNumber()));
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteRoundDatabaseListener(model.getNumber());
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alert =builder.create();
                        alert.setCancelable(false);
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                    }
                });
            }
        };
        mRoundList.setAdapter(firebaseRecyclerAdapter);
    }
    //Deleting a round
    private void deleteRoundDatabaseListener(final long number) {
        mClubName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot mRounds = dataSnapshot.child("Rounds");
                long i=1;
                for (DataSnapshot round : mRounds.getChildren()) {
                    DataSnapshot roundNum=round.child("number");
                    if( Long.parseLong(String.valueOf(roundNum.getValue()))== number){
                        mClubName.child("Rounds").child(String.valueOf(i)).removeValue();
                        i=i-1;
                        Toast.makeText(AdminAllRoundsActivity.this,"Deleted",Toast.LENGTH_LONG).show();
                    }
                    else if(Long.parseLong(String.valueOf(roundNum.getValue()))> number){
                        mClubName.child("Rounds").child(String.valueOf(i)).setValue(round.getValue());
                        mClubName.child("Rounds").child(String.valueOf(i)).child("number").setValue(i);
                        mClubName.child("Rounds").child(String.valueOf(i+1)).removeValue();


                    }
                    i=i+1;
                }
                mRoundCount.setValue(i-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class RoundViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView roundName, roundNumber;
        public RoundViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            roundName = (TextView) mView.findViewById(R.id.rounds_all_round_name);
            roundNumber = (TextView) mView.findViewById(R.id.rounds_all_round_number);
        }

        public void setName(String name){
            roundName.setText(name);
        }

        public void setNumber(long number){
            String hellolo = "Round" + String.valueOf(number);
            roundNumber.setText(hellolo);
        }
    }
}
