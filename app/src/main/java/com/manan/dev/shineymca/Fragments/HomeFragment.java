package com.manan.dev.shineymca.Fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.shineymca.Models.Club;
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.SingleClubActivity;
import com.squareup.picasso.Picasso;
import com.wajahatkarim3.easyflipview.EasyFlipView;

/**
 * Created by nisha on 6/15/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {

    private RecyclerView mClubList;
    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mClubList = (RecyclerView)mView.findViewById(R.id.home_club_list);
        mClubList.setHasFixedSize(true);
        mClubList.setItemViewCacheSize(20);
        mClubList.setDrawingCacheEnabled(true);
        mClubList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mClubList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Club, ClubViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Club, ClubViewHolder>(
                Club.class,
                R.layout.single_club_layout,
                ClubViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child("Clubs")
        ) {
            @Override
            protected void populateViewHolder(ClubViewHolder viewHolder, final Club model, int position) {
                viewHolder.setName(model.getClubName());
                viewHolder.setDescription(model.getClubDescription());
                viewHolder.setIcon(model.getClubIcon());
                final EasyFlipView mFlip;
                mFlip = (EasyFlipView)viewHolder.mView.findViewById(R.id.single_club_to_club_specific_intent);
                mFlip.setFlipEnabled(false);
                LinearLayout a = (LinearLayout)viewHolder.mView.findViewById(R.id.single_club_front);
                LinearLayout b = (LinearLayout)viewHolder.mView.findViewById(R.id.single_club_back);
                a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Ria bna ise", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getContext(),SingleClubActivity.class);
                        Bundle extras=new Bundle();
                        extras.putString("clubName",model.getClubName());
                        extras.putString("clubDescription",model.getClubDescription());
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                });
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Ria bna ise", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), SingleClubActivity.class).putExtra("clubName", model.getClubName()));
                    }
                });

                final ImageView mInfo;
                mInfo = (ImageView)viewHolder.mView.findViewById(R.id.single_club_info);
                mInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFlip.setFlipEnabled(true);
                        mFlip.flipTheView(true);
                        mFlip.setFlipEnabled(false);
                    }
                });

            }

        };
        mClubList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ClubViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView textView = mView.findViewById(R.id.single_club_club_name);
            textView.setText(name);
        }public void setDescription(String name){
            TextView textView = mView.findViewById(R.id.single_club_description);
            textView.setText(name);
        }public void setIcon(String name){
            ImageView img = mView.findViewById(R.id.single_club_image);
            Picasso.get().load(name).into(img);
        }
    }
}
