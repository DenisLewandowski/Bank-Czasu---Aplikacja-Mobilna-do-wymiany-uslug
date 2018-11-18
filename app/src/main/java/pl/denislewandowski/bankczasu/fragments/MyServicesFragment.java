package pl.denislewandowski.bankczasu.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.Service;
import pl.denislewandowski.bankczasu.adapters.TimebankServicesAdapter;

public class MyServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Service> services;
    private List<String> serviceNames;
    private TimebankServicesAdapter adapter;
    private String userId;
    String timebankId;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        getUserData();

        return inflater.inflate(R.layout.fragment_myservices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recycler_view_myservices);

        services = new ArrayList<>();
        serviceNames = new ArrayList<>();

        adapter = new TimebankServicesAdapter(services, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }


    private void getUserData() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timebankId = (String) dataSnapshot.child("Timebank").getValue();
                for (DataSnapshot ds : dataSnapshot.child("Services").getChildren()) {
                    serviceNames.add((String) ds.getValue());
                }
                getUserServices();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserServices() {
        FirebaseDatabase.getInstance().getReference("Timebanks")
                .child(timebankId).child("Services").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (serviceNames.contains(ds.getKey()))
                        services.add(ds.getValue(Service.class));
                }
                adapter = new TimebankServicesAdapter(services, getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}


