package com.manan.dev.shineymca.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.shineymca.Models.Coordinator;
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.Utility.Methods;

import java.util.ArrayList;

public class CoordinatorAdapter extends RecyclerView.Adapter<CoordinatorAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Coordinator> mCoordinators;

    public CoordinatorAdapter(Context context, ArrayList<Coordinator> itemList) {
        this.mContext = context;
        this.mCoordinators = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_coordinators, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        final Coordinator coordinator = mCoordinators.get(position);
        final int pos = position;

        Log.d("yd", coordinator.getCoordName() + " " + coordinator.getCoordPhone() + " " + coordinator.getCoordId());

        holder.mName.setText(coordinator.getCoordName());
        holder.mPhone.setText(coordinator.getCoordPhone());
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, coordinator.getCoordName(), Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("Coordinators").child(Methods.getEmailSharedPref(mContext)).child(coordinator.getCoordId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mCoordinators.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mName, mPhone, mDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.tvUserName);
            mPhone = (TextView) itemView.findViewById(R.id.tvUserId);
            mDelete = (TextView) itemView.findViewById(R.id.removeCoordinator);
        }
    }
}
