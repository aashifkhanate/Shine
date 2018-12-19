package com.manan.dev.shineymca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.manan.dev.shineymca.Utility.Methods;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterSecondActivity extends AppCompatActivity {

    private TextInputLayout mUsername, mPhone, mEmail;
    private Spinner mYear, mBranch;
    private Button mRegisterBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register_second);
        mEmail = (TextInputLayout)findViewById(R.id.register2_email);
        mUsername = (TextInputLayout) findViewById(R.id.register2_username);
        mPhone = (TextInputLayout)findViewById(R.id.register2_phone);
        mYear = (Spinner) findViewById(R.id.register2_year);
        mBranch = (Spinner)findViewById(R.id.register2_branch);
        mRegisterBtn = (Button) findViewById(R.id.register2_register_btn);
        mProgress = new ProgressDialog(this);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setTitle("Registering");
        mProgress.setMessage("Databasing your details");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = mAuth.getCurrentUser().getUid().toString();
        if(user.getEmail() != null)
            mEmail.getEditText().setText(user.getEmail());
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userUsername = mUsername.getEditText().getText().toString();
                String userYear = mYear.getSelectedItem().toString();
                String userBranch = mBranch.getSelectedItem().toString();
                String userPhone = mPhone.getEditText().getText().toString();
                final String userEmail = mEmail.getEditText().getText().toString();
               // String deviceID= user.getIdToken(true).toString();
//                M.put("userImage", "default");
                if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userUsername) && !TextUtils.isEmpty(userYear) && !TextUtils.isEmpty(userBranch)) {
                        if (isValidMobile(userPhone)){
                            if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                                HashMap<String, String> M = new HashMap<String, String>();
                                M.put("userName", user.getDisplayName());
                                M.put("userUsername", userUsername);
                                M.put("userEmail", userEmail);
                                M.put("userYear", userYear);
                                M.put("userBranch", userBranch);
                                M.put("userImage", user.getPhotoUrl().toString());
                                M.put("userPhone", userPhone);
                              //  M.put("deviceID",deviceID);
                               // M.put("userQRCode",userQRcode);
                                mProgress.show();
                                final DatabaseReference mRegRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                final DatabaseReference mUsernameRef = FirebaseDatabase.getInstance().getReference().child("Usernames");
                                mRegRef.setValue(M).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mUsernameRef.child(userUsername).setValue(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mRegRef.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                                                    mProgress.dismiss();
                                                    Toast.makeText(RegisterSecondActivity.this, "Details databased", Toast.LENGTH_SHORT).show();
                                                    Methods.callSharedPreference(getApplicationContext(), userEmail);
                                                    Methods.callUserIDSharedPreference(getApplicationContext(), uid);
                                                    startActivity(new Intent(RegisterSecondActivity.this, BottomNavigator.class));
                                                    finish();
                                                }
                                            });
                                        } else {
                                            mProgress.hide();
                                            Toast.makeText(RegisterSecondActivity.this, "Some error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(RegisterSecondActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(RegisterSecondActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                        }
                }else{
                    Toast.makeText(RegisterSecondActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }
}