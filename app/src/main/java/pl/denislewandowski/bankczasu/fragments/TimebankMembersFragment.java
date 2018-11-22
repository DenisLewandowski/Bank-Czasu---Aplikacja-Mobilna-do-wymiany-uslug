package pl.denislewandowski.bankczasu.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.Service;
import pl.denislewandowski.bankczasu.TimebankData;
import pl.denislewandowski.bankczasu.TimebankViewModel;
import pl.denislewandowski.bankczasu.UserItem;
import pl.denislewandowski.bankczasu.adapters.UsersAdapter;

public class TimebankMembersFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<UserItem> userItems;
    private UsersAdapter adapter;
    private List<String> userIds;
    private String timebankId;
    private ContentLoadingProgressBar progressBar;

    private TimebankViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        timebankId = sharedPreferences.getString("CURRENT_TIMEBANK", "null");

        viewModel =  ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);

        return inflater.inflate(R.layout.fragment_timebank_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.timebank_members_recyclerview);
        progressBar = getView().findViewById(R.id.progress_bar);

        userIds = new ArrayList<>();
        userItems = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        getUsersInfo();
    }

    private void getUsersInfo() {
        FirebaseDatabase.getInstance().getReference("Timebanks")
                .child(timebankId).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    userIds.add(ds.getValue(String.class));
                }
                getUserItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserItems() {
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(userIds.contains(ds.getKey())) {
                        UserItem userItem = new UserItem(ds.getKey(), ds.child("Name").getValue(String.class),
                                ds.child("timeCurrency").getValue(Integer.class));
                       userItems.add(userItem);
                    }
                }
                adapter = new UsersAdapter(userItems, getContext());
                recyclerView.setAdapter(adapter);
                progressBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
