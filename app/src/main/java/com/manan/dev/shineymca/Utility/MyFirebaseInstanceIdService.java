package com.manan.dev.shineymca.Utility;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Suneja's on 12-07-2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private FirebaseAuth mAuth;
    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            final String uid = mAuth.getCurrentUser().getUid();
            String refreshToken = FirebaseInstanceId.getInstance().getToken();
            Log.v(TAG, "Refreshed token " + refreshToken);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            reference.child("token").setValue(refreshToken);
        }
    }
}
