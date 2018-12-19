package com.manan.dev.shineymca.AdminZone.AdminFragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.shineymca.R;

public class AddDescriptionFragment extends DialogFragment {

    private Context mContext;
    private EditText mClubDescription;
    private TextView mClubName;
    private ImageView mCrossButton;
    private Button mSubmit;
    private String clubName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_club_description, container, false);

        initializeVariables(view);

        clubName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String mDescription = getArguments().getString("description");

        mClubDescription.setText(mDescription);
        mClubDescription.setSelection(mDescription.length());

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = mClubDescription.getText().toString().trim();
                if(!description.equals("")){
                    FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("clubDescription").setValue(description)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        getDialog().dismiss();
                                        Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "Unable to update. Try Again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    mClubDescription.setError("This Cannot be empty");
                }
            }
        });

        mCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        mClubName.setText(clubName);

        return view;
    }

    private void initializeVariables(View view) {
        mClubDescription = (EditText) view.findViewById(R.id.et_club_description);
        mClubName = (TextView) view.findViewById(R.id.tv_club_name);
        mCrossButton = (ImageView) view.findViewById(R.id.iv_cancel);
        mSubmit = (Button) view.findViewById(R.id.bt_submit);
    }
}
