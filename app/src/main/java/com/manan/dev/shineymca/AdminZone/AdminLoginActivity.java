package com.manan.dev.shineymca.AdminZone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.Utility.Methods;

public class AdminLoginActivity extends AppCompatActivity {

    EditText mClubEmail, mClubPassword;
    Button mLogin;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mContext = (Context) AdminLoginActivity.this;

        initializeVariables();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mClubEmail.getText().toString();
                String password = mClubPassword.getText().toString();
                if(checker(email, password)){
                    try {
                        pd.show();
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    setResult(101);
                                    Methods.callSharedPreference(getApplicationContext(), mAuth.getCurrentUser().getDisplayName());
                                    startActivity(new Intent(AdminLoginActivity.this, AdminHomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(AdminLoginActivity.this, "Error signing you in.", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }
                        });

                    } catch (Exception e) {
                        pd.hide();
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                }
            }
        });

    }

    private boolean checker(String email, String password) {
        if(email.equals("") || email.trim().equals("")){
            mClubEmail.setError("Email Address cannot be empty");
            return false;
        }
        if(password.equals("") || password.trim().equals("")){
            mClubPassword.setError("Password Cannot be empty");
            return false;
        }
        return true;
    }

    private void initializeVariables() {
        mClubEmail = (EditText) findViewById(R.id.et_club_email);
        mClubPassword = (EditText) findViewById(R.id.et_club_password);

        mClubEmail.setText("manantechnosurge@gmail.com");
        mClubPassword.setText("12345678");

        mLogin = (Button) findViewById(R.id.bt_login);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
    }


}
