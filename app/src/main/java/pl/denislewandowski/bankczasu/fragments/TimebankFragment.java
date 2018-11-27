package pl.denislewandowski.bankczasu.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.adapters.ServicesFragmentPagerAdapter;
import pl.denislewandowski.bankczasu.dialogs.LeaveTimebankDialogFragment;
import pl.denislewandowski.bankczasu.dialogs.TimebankCodeDialogFragment;

public class TimebankFragment extends Fragment {

    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String timebankId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.app_name);
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ServicesFragmentPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        setHasOptionsMenu(true);
        addCurrencyValueToSharedPreferences();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, new AddServiceFragment())
                .addToBackStack(null)
                .commit();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 2) fab.setVisibility(View.INVISIBLE);
                if(tab.getPosition() != 2) fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timebank_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.new_person) {
            TimebankCodeDialogFragment dialog = new TimebankCodeDialogFragment();
            dialog.show(getFragmentManager(), "TIMEBANK_CODE_DIALOG" );
        }

        if(id == R.id.exit_timebank) {
            LeaveTimebankDialogFragment dialog = new LeaveTimebankDialogFragment();
            dialog.show(getChildFragmentManager(), "LEAVE_TIMEBANK_DIALOG");
        }

        return super.onOptionsItemSelected(item);
    }


    private void addCurrencyValueToSharedPreferences() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("timeCurrency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer timeCurrencyValue = dataSnapshot.getValue(Integer.class);
                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("TIME_CURRENCY", timeCurrencyValue);
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
