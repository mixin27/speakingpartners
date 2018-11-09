package com.team29.speakingpartners;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.activity.EditProfileActivity;
import com.team29.speakingpartners.activity.ProfileDetailActivity;
import com.team29.speakingpartners.adapter.MyViewPagerAdapter;
import com.team29.speakingpartners.background.CallingStateService;
import com.team29.speakingpartners.fragment.ActiveUserFragment;
import com.team29.speakingpartners.fragment.HomeFragment;
import com.team29.speakingpartners.fragment.PendingFragment;
import com.team29.speakingpartners.fragment.RecentFragment;
import com.team29.speakingpartners.helper.CheckPermissionForApp;
import com.team29.speakingpartners.net.ConnectionChecking;
import com.team29.speakingpartners.utils.GlideApp;
import com.team29.speakingpartners.utils.GlideOptions;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    public static final int PERMISSION_REQ_ID_CAMERA = 10;

    private BottomNavigationView mBottomNavView;
    private ViewPager mViewPager;

    AppCompatTextView toolBarTitle;
    AppCompatImageView toolBarProfileButton;

    HomeFragment mHomeFragment;
    ActiveUserFragment mActiveUserFragment;
    RecentFragment mRecentFragment;
    PendingFragment mPendingFragment;

    MenuItem mPrevMenuItem;

    FirebaseAuth mAuth;

    boolean hasRecordAudioPermission, hasCameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity created");
        setContentView(R.layout.activity_main);

        // Permission
        requestApplicationPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);

        // ActionBar
        setUpTooBar(toolbar);

        // BottomNavigationView
        setUpBottomNavView();

        // ViewPager
        setUpViewPager();

        mAuth = FirebaseAuth.getInstance();
    }

    private void requestApplicationPermission() {
        hasRecordAudioPermission = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
        if(!hasRecordAudioPermission){
            // ask the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQ_ID_RECORD_AUDIO);
        }

        hasCameraPermission = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if(!hasCameraPermission){
            // ask the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQ_ID_CAMERA);
        }
    }

    // ViewPager
    private void setUpViewPager() {
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mPrevMenuItem != null) {
                    mPrevMenuItem.setChecked(false);
                } else {
                    mBottomNavView.getMenu().getItem(0).setChecked(false);
                }

                Log.d(TAG, "Fragment " + i);
                mBottomNavView.getMenu().getItem(i).setChecked(true);
                mPrevMenuItem = mBottomNavView.getMenu().getItem(i);
                toolBarTitle.setText(mPrevMenuItem.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        loadFragment();
    }

    private void loadFragment() {

        mHomeFragment = new HomeFragment();
        mRecentFragment = new RecentFragment();
        mActiveUserFragment = new ActiveUserFragment();
        mPendingFragment = new PendingFragment();

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mHomeFragment);
        adapter.addFragment(mActiveUserFragment);
        adapter.addFragment(mPendingFragment);
        adapter.addFragment(mRecentFragment);
        mViewPager.setAdapter(adapter);
    }

    // BottomNavigationView
    private void setUpBottomNavView() {
        mBottomNavView = findViewById(R.id.navigation);
        mBottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_nav_home:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.bottom_nav_active_user:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.bottom_nav_pending:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.bottom_nav_recent:
                        mViewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });
    }

    // ActionBar
    private void setUpTooBar(Toolbar tooBar) {
        toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarProfileButton = findViewById(R.id.toolbar_btn_proile);

        if (tooBar != null) {
            toolBarTitle.setText(getResources().getString(R.string.bottom_nav_home));
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "Listen Error");
                                return;
                            }

                            if (snapshots != null) {
                                for (QueryDocumentSnapshot snapshot : snapshots) {
                                    if (!snapshot.getString("url_photo").equals("")) {
                                        toolBarProfileButton.setBackgroundDrawable(null);
                                        GlideApp.with(MainActivity.this)
                                                .load(snapshot.getString("url_photo"))
                                                .apply(GlideOptions.circleCropTransform())
                                                .into(toolBarProfileButton);
                                    }
                                }
                            }
                        }
                    });

            toolBarProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ProfileDetailActivity.class));
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID_RECORD_AUDIO && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasRecordAudioPermission = true;
        }else if (requestCode == PERMISSION_REQ_ID_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true;
        }

    }

    // Activity
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Activity paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Activity destroyed");
//        stopBackgroundService();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "Activity restarted");

        if (mAuth.getCurrentUser() == null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Activity resumed");
        if (mAuth.getCurrentUser() == null) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Activity stopped");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Activity started");

        if (ConnectionChecking.checkConnection(this)) {
            Log.d(TAG, "Online");
        } else {
            Log.d(TAG, "Offline");
        }

        if (ConnectionChecking.checkConnection(this)) {
            startBackgroundService();
        }
    }

    private void startBackgroundService() {
        Intent intent = new Intent(this, CallingStateService.class);
        startService(intent);
    }

    private void stopBackgroundService() {
        Intent intent = new Intent(this, CallingStateService.class);
        stopService(intent);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Back Pressed");

        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        }

        mViewPager.setCurrentItem(0);
    }
}
