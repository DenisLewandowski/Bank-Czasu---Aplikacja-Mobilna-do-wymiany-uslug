package pl.denislewandowski.bankczasu.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.FirebaseRepository;
import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.model.Chat;
import pl.denislewandowski.bankczasu.model.Service;
import pl.denislewandowski.bankczasu.model.TimebankData;
import pl.denislewandowski.bankczasu.TimebankViewModel;
import pl.denislewandowski.bankczasu.model.UserItem;
import pl.denislewandowski.bankczasu.dialogs.AboutApplicationDialogFragment;
import pl.denislewandowski.bankczasu.fragments.MessagesFragment;
import pl.denislewandowski.bankczasu.fragments.MyProfileFragment;
import pl.denislewandowski.bankczasu.fragments.ServicesToDoFragment;
import pl.denislewandowski.bankczasu.fragments.TimebankFragment;
import pl.denislewandowski.bankczasu.fragments.UserWithoutTimebankFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private long backPressed;
    private DrawerLayout drawer;
    private TextView timeCurrencyValueTextView, userNameTextView;
    private ImageView userImage;
    private DatabaseReference databaseUser;
    private boolean hasUserAnyTimebank;
    private ContentLoadingProgressBar progressBar;
    private TimebankViewModel timebankViewModel;
    private String timebankId;
    FirebaseRepository repository;

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
        progressBar = findViewById(R.id.progress_bar);
        userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_user_login);
        userImage = navigationView.getHeaderView(0).findViewById(R.id.nav_header_image);
        timebankViewModel = ViewModelProviders.of(this).get(TimebankViewModel.class);

        addCurrentTimebankIdToSharedPreferences();

        repository = new FirebaseRepository(this);

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

        repository.setCurrentUserImage(userImage);
        repository.setUserName(userNameTextView);
        FirebaseUser user = mAuth.getCurrentUser();
        userNameTextView.setText(user.getDisplayName());
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
                if (!timebankId.equals(""))
                    fragment = new MessagesFragment();
                else
                    fragment = new UserWithoutTimebankFragment();
                break;
            }
            case R.id.nav_todo: {
                if (!timebankId.equals(""))
                    fragment = new ServicesToDoFragment();
                else
                    fragment = new UserWithoutTimebankFragment();
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
        if (timebankId.equals("")) {
            ft = getSupportFragmentManager().beginTransaction();
            progressBar.hide();
            ft.replace(R.id.content_main, new UserWithoutTimebankFragment(), "TIMEBANK_FRAGMENT");
            ft.commit();
            hasUserAnyTimebank = false;
        } else {
            ft = getSupportFragmentManager().beginTransaction();
            progressBar.hide();
            ft.replace(R.id.content_main, new TimebankFragment(), "TIMEBANK_FRAGMENT");
            ft.commit();
            hasUserAnyTimebank = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    public void addCurrentTimebankIdToSharedPreferences() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Timebank")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        timebankId = dataSnapshot.getValue(String.class);
                        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CURRENT_TIMEBANK", timebankId);
                        editor.commit();
                        //TODO : edytować to żeby nie bylo w shared i nazwać lepiej
                        if (timebankId != null) {
                            setTimebankDataInViewModel();
                            setUsersInViewModel();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void setTimebankDataInViewModel() {
        FirebaseDatabase.getInstance().getReference("Timebanks").child(timebankId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Service> services = new ArrayList<>();
                        List<String> members = new ArrayList<>();
                        List<Chat> chatList = new ArrayList<>();

                        for (DataSnapshot ds : dataSnapshot.child("Services").getChildren()) {
                            services.add(ds.getValue(Service.class));
                        }
                        for (DataSnapshot ds : dataSnapshot.child("Members").getChildren()) {
                            members.add(ds.getValue(String.class));
                        }
                        for (DataSnapshot ds : dataSnapshot.child("Messages").getChildren()) {
                            chatList.add(ds.getValue(Chat.class));
                        }
                        timebankViewModel.timebankData.setValue(new TimebankData(timebankId, members, services, chatList));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void setUsersInViewModel() {
        final List<UserItem> userItems = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("Timebank").getValue(String.class).equals(timebankId)) {
                        List<String> userIds = new ArrayList<>();
                        for (DataSnapshot ds2 : ds.child("Services").getChildren()) {
                            userIds.add(ds2.getValue(String.class));
                        }

                        UserItem userItem = new UserItem(ds.getKey(), ds.child("Name").getValue(String.class),
                                ds.child("timeCurrency").getValue(Integer.class), userIds);
                        userItems.add(userItem);
                    }
                }
                timebankViewModel.usersData.setValue(userItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
