package com.manan.dev.shineymca;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manan.dev.shineymca.AdminZone.AdminLoginActivity;
import com.manan.dev.shineymca.Utility.Methods;

public class RegisterFirstActivity extends AppCompatActivity{

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private LoginButton loginButton;
    private ProgressDialog mProgress;
    private TextView mAdminZone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register_first);
        Context context = this;
        callSharedPreference(this);
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        loginButton = (LoginButton)findViewById(R.id.register1_facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading");
        mProgress.setMessage("Please wait to enter further details");
        mProgress.setCanceledOnTouchOutside(false);
        mAdminZone = (TextView) findViewById(R.id.tv_admin_zone);
        mAdminZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(RegisterFirstActivity.this, AdminLoginActivity.class), 100);
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("prerna",loginResult.toString());
                        loginButton.setVisibility(View.GONE);
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(),"Login failed! Please try again.",Toast.LENGTH_SHORT).show();
                        Log.e("jayati", "XXXXXXXX");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(),"Login failed! Please try again.",Toast.LENGTH_SHORT).show();
                        Log.e("jayati", "XXXXXXXX");
                    }

                });
    }

    private void callSharedPreference(Context context) {
        Methods.callSharedPreference(context, "default");
        Methods.callUserIDSharedPreference(context,"default");
    }

    private void handleFacebookAccessToken(AccessToken token) {
        mProgress.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegisterFirstActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();
                            if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                Log.d("prerna","a");
                                startActivity(new Intent(RegisterFirstActivity.this, RegisterSecondActivity.class));
                                Methods.callUserIDSharedPreference(getApplicationContext(), task.getResult().getUser().getUid());
                                finish();
                            } else {
                                Log.d("prerna","b");
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                Log.d("prerna",firebaseUser.getUid());
                                Log.d("prerna",task.getResult().getUser().getUid());
                                Log.d("prerna",firebaseUser.getEmail());
                                Methods.callSharedPreference(getApplicationContext(), task.getResult().getUser().getEmail());
                                Methods.callUserIDSharedPreference(getApplicationContext(), task.getResult().getUser().getUid());
                                startActivity(new Intent(RegisterFirstActivity.this, BottomNavigator.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterFirstActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == 101){
                finish();
            }
        }
    }

}