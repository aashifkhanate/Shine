package com.manan.dev.shineymca.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manan.dev.shineymca.BottomNavigator;
import com.manan.dev.shineymca.Utility.Methods;
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.Utility.UserQRCodeGenerator;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.manan.dev.shineymca.Utility.Methods.getUserIDSharedPref;

public class ProfileFragment extends android.support.v4.app.Fragment{

    private Button mLogout;
    private View mView;
    private ImageView mProfilePic;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private Context mContext;
    private TextView mPhone;
    private TextView mName;
    private TextView mEmail;
    private TextView mBranch;
    private TextView mYear;
    private TextView mReg;
    private ProgressDialog mProgress;
    private List<String> itemlist;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        mLogout = (Button)mView.findViewById(R.id.profile_logout_btn);
        mProfilePic = (ImageView)mView.findViewById(R.id.profile_pic);
        mName = (TextView)mView.findViewById(R.id.tv_name);
        mPhone = (TextView)mView.findViewById(R.id.tv_phone);
        mEmail = (TextView)mView.findViewById(R.id.tv_email);
        mBranch = (TextView)mView.findViewById(R.id.tv_branch);
        mYear = (TextView)mView.findViewById(R.id.tv_year);
        mReg = (TextView)mView.findViewById(R.id.name_events);
        Toast.makeText(getApplicationContext(),getUserIDSharedPref(mContext),Toast.LENGTH_SHORT).show();

        String uid = mAuth.getCurrentUser().getUid().toString();
        Log.d("prerna",uid);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ValueEventListener valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("userImage")){
                String s = dataSnapshot.child("userImage").getValue().toString();
                Toast.makeText(getApplicationContext(), "image" + s, Toast.LENGTH_SHORT).show();
                Picasso.get().load(s).into(mProfilePic);}
                String name = dataSnapshot.child("userName").getValue().toString();
                String email = dataSnapshot.child("userEmail").getValue().toString();
                String phone = dataSnapshot.child("userPhone").getValue().toString();
                String branch  = dataSnapshot.child("userBranch").getValue().toString();
                String year = dataSnapshot.child("userYear").getValue().toString();
                            mName.setText(name);
                            mEmail.setText(email);
                            mPhone.setText(phone);
                            mBranch.setText(branch);
                            mYear.setText(year);
                            if(dataSnapshot.hasChild("registeredEvents")) {
                                String event = dataSnapshot.child("registeredEvents").getValue().toString();
                                mReg.setText(event);
                            }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "NETWORK ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                AlertDialog alertDialog = alertDialogBuilder.create();
                final LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);
                ImageView QRCode=new ImageView(mContext);
                UserQRCodeGenerator codeGenerator = new UserQRCodeGenerator();
                Bitmap qrCode = codeGenerator.GenerateClick(getUserIDSharedPref(mContext), mContext, 800, 940);
                QRCode.setImageBitmap(qrCode);
                layout.addView(QRCode);
                alertDialog.setView(layout);
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(true);

            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Methods.callSharedPreference(getApplicationContext(), "default");
                finishActivity activity = (finishActivity) mContext;
                activity.finisher(true);
                startActivity(new Intent(getContext(), BottomNavigator.class));
            }
        });

        return mView;
    }

    public interface finishActivity {
        void finisher(boolean value);
    }
}

