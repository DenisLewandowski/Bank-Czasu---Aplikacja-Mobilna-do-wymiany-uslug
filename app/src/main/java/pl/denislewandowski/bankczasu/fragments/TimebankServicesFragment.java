package pl.denislewandowski.bankczasu.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.model.Service;
import pl.denislewandowski.bankczasu.model.TimebankData;
import pl.denislewandowski.bankczasu.TimebankViewModel;
import pl.denislewandowski.bankczasu.adapters.TimebankServicesAdapter;

public class TimebankServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Service> services;
    private ContentLoadingProgressBar progressBar;
    private TimebankServicesAdapter adapter;
    private String userId;
    private String timebankId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return inflater.inflate(R.layout.fragment_timebank_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recycler_view_needed);
        progressBar = getView().findViewById(R.id.progress_bar);

        services = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        getTimebankId();
    }

    private void getTimebankId() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("Timebank").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timebankId = (String) dataSnapshot.getValue();
                subscribeServices();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void subscribeServices() {
        TimebankViewModel timebankViewModel = ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);
        timebankViewModel.timebankData.observe(getActivity(), new Observer<TimebankData>() {
            @Override
            public void onChanged(@Nullable TimebankData timebankData) {
                services = timebankData.getServices();
                adapter = new TimebankServicesAdapter(services, getContext());
                recyclerView.setAdapter(adapter);
                progressBar.hide();
            }
        });
    }
}
