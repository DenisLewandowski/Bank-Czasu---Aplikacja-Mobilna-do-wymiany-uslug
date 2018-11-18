package pl.denislewandowski.bankczasu.activities;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.dialogs.AboutApplicationDialogFragment;
import pl.denislewandowski.bankczasu.fragments.MessagesFragment;
import pl.denislewandowski.bankczasu.fragments.MyProfileFragment;
import pl.denislewandowski.bankczasu.fragments.TimebankFragment;
import pl.denislewandowski.bankczasu.fragments.UserWithoutTimebankFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private long backPressed;
    private DrawerLayout drawer;
    private TextView timeCurrencyValueTextView;
    private DatabaseReference databaseUser;
    private boolean hasUserAnyTimebank;

    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        timeCurrencyValueTextView = navigationView.getHeaderView(0).findViewById(R.id.time_currency_value_header);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };

        databaseUser.child(mAuth.getCurrentUser().getUid()).child("Timebank").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String timebankId = dataSnapshot.getValue(String.class);
                setTimebankFragment(timebankId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        TimebankFragment sf = (TimebankFragment) getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
//        if (sf == null) {
//            ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.content_main, new TimebankFragment(), "MAIN_FRAGMENT");
//            ft.commit();
//        }

        databaseUser.child(mAuth.getCurrentUser().getUid())
                .child("timeCurrency")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int userTimeCurrencyValue = dataSnapshot.getValue(Integer.class);
                        timeCurrencyValueTextView.setText(String.valueOf(userTimeCurrencyValue));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (findViewById(R.id.drawer_layout));
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().findFragmentByTag("TIMEBANK_FRAGMENT").isVisible()) {
            doubleClickExit();
        } else {
            super.onBackPressed();
        }
    }

    void doubleClickExit() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toast.makeText(this,
                    "Nacisnij ponownie WSTECZ, aby zamknąć!", Toast.LENGTH_SHORT)
                    .show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            mAuth.signOut();
            finish();
        }

        displaySelectedScreen(id);

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_services_offered: {
                if (hasUserAnyTimebank)
                    fragment = new TimebankFragment();
                else
                    fragment = new UserWithoutTimebankFragment();
                break;
            }
            case R.id.nav_messages: {
                fragment = new MessagesFragment();
                break;
            }
            case R.id.nav_myoffers: {

                break;
            }
            case R.id.nav_myprofile: {
                fragment = new MyProfileFragment();
                break;
            }
            case R.id.nav_tutorial: {

                break;
            }
            case R.id.nav_aboutapplication: {
                AboutApplicationDialogFragment dialog = new AboutApplicationDialogFragment();
                dialog.show(getFragmentManager(), "AboutAppDialog");
                break;
            }
        }

        if (fragment != null) {
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    private void setTimebankFragment(String timebankId) {
        if (timebankId == null) {
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, new UserWithoutTimebankFragment(), "TIMEBANK_FRAGMENT");
            ft.commit();
            hasUserAnyTimebank = false;
        } else {
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, new TimebankFragment(), "TIMEBANK_FRAGMENT");
            ft.commit();
            hasUserAnyTimebank = true;
        }
    }
}
