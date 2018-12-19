package com.manan.dev.shineymca;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manan.dev.shineymca.AdminZone.AdminHomeActivity;
import com.manan.dev.shineymca.Fragments.AboutFragment;
import com.manan.dev.shineymca.Fragments.CalenderFragment;
import com.manan.dev.shineymca.Fragments.NotificationFragment;
import com.manan.dev.shineymca.Fragments.ProfileFragment;
import com.manan.dev.shineymca.Fragments.UserTabsPagerAdapter;
import com.manan.dev.shineymca.Utility.Methods;

public class BottomNavigator extends AppCompatActivity implements ProfileFragment.finishActivity {
    BottomNavigationView navBar;
    ViewPager UserviewPager;
    private UserTabsPagerAdapter adapter;
    MenuItem prevItem;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_bottom_navigator);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                LogInStub("AuthListener");
            }
        };

        String title = getResources().getString(R.string.app_name);
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        UserviewPager = (ViewPager) findViewById(R.id.user_view_pager);
        navBar = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        navBar.getMenu().getItem(0).setChecked(true);
        navBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                UserviewPager.setCurrentItem(0);
                                break;
                            case R.id.action_calender:
                                UserviewPager.setCurrentItem(1);
                                break;
                            case R.id.action_notifications:
                                UserviewPager.setCurrentItem(2);
                                break;
                            case R.id.action_profile:
                                UserviewPager.setCurrentItem(3);
                                break;
                            case R.id.action_about:
                                UserviewPager.setCurrentItem(4);
                                break;
                        }
                        return false;
                    }
                });

        UserviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevItem != null)
                    prevItem.setChecked(false);
                else
                    navBar.getMenu().getItem(0).setChecked(false);
                navBar.getMenu().getItem(position).setChecked(true);
                prevItem = navBar.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setUpViewPager(UserviewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        adapter = new UserTabsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AboutFragment(), "ABOUT");
        adapter.addFragment(new CalenderFragment(), "CALENDER");
        adapter.addFragment(new NotificationFragment(), "NOTIFICATION");
        adapter.addFragment(new ProfileFragment(), "PROFILE");
        adapter.addFragment(new AboutFragment(), "ABOUT");
        UserviewPager.setAdapter(adapter);
    }

    @Override
    public void finisher(boolean value) {
        if (value) {
            state = true;
            finish();
        }
    }

    void LogInStub(String s) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getProviders().get(0).equals("facebook.com")) {

            String email = Methods.getEmailSharedPref(getApplicationContext());
            if (email.equals("default")) {
                mAuth.getCurrentUser().delete();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(BottomNavigator.this, RegisterFirstActivity.class));
                finish();
            }



        } else if (currentUser != null) {
            Toast.makeText(this, currentUser.toString(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this, "Switching to Dashboard!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BottomNavigator.this, AdminHomeActivity.class));
            finish();
        } else {
            if(!state) {
                startActivity(new Intent(BottomNavigator.this, RegisterFirstActivity.class));
                finish();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /*
    String email = Methods.getEmailSharedPref(getApplicationContext());
        if(email.equals("default")){
            if(mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().delete();
                LoginManager.getInstance().logOut();
            }
            startActivity(new Intent(BottomNavigator.this, RegisterFirstActivity.class));
            finish();
        }
     */
}
