package pl.denislewandowski.bankczasu.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.model.Service;
import pl.denislewandowski.bankczasu.model.TimebankData;
import pl.denislewandowski.bankczasu.TimebankViewModel;
import pl.denislewandowski.bankczasu.adapters.TimebankServicesAdapter;

public class MyServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Service> services;
    private TextView emptyListTextView;
    private TimebankServicesAdapter adapter;
    private String userId;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        services = new ArrayList<>();


        return inflater.inflate(R.layout.fragment_myservices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recycler_view_myservices);
        emptyListTextView = getView().findViewById(R.id.empty_view);

        getUserServices();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

    private void getUserServices() {
        final TimebankViewModel timebankViewModel = ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);
        timebankViewModel.timebankData.observe(getActivity(), new Observer<TimebankData>() {
            @Override
            public void onChanged(@Nullable TimebankData timebankData) {
                List<Service> allServices = timebankData.getServices();
                services.clear();
                for (Service s : allServices) {
                    if (s.getServiceOwnerId().equals(userId) && s.getClientId().isEmpty()) {
                        if (!services.contains(s))
                            services.add(s);
                    }
                }
                adapter = new TimebankServicesAdapter(services, getContext(), TimebankServicesAdapter.MY_SERVICES);
                recyclerView.setAdapter(adapter);
                if(services.isEmpty())
                    emptyListTextView.setVisibility(View.VISIBLE);
                else
                    emptyListTextView.setVisibility(View.INVISIBLE);
            }
        });
    }
}


