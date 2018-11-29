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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private TextView emptyListTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timebank_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_needed);
        progressBar = getView().findViewById(R.id.progress_bar);
        emptyListTextView = getView().findViewById(R.id.empty_view);

        services = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        subscribeServices();
    }

    private void subscribeServices() {
        TimebankViewModel timebankViewModel = ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);
        timebankViewModel.timebankData.observe(getActivity(), new Observer<TimebankData>() {
            @Override
            public void onChanged(@Nullable TimebankData timebankData) {
                if (timebankData != null) {
                    services.clear();
                    for(Service s : timebankData.getServices()) {
                        if(s.getClientId().isEmpty() && !services.contains(s)) {
                            services.add(s);
                        }
                    }

                    Collections.sort(services, new Comparator<Service>(){
                        public int compare(Service obj1, Service obj2) {
                            return obj1.getName().compareToIgnoreCase(obj2.getName()); // To compare string values
                        }
                    });

                    adapter = new TimebankServicesAdapter(services, getContext(), TimebankServicesAdapter.SERVICES);
                    recyclerView.setAdapter(adapter);
                    progressBar.hide();
                }

                if (services.isEmpty())
                    emptyListTextView.setVisibility(View.VISIBLE);
                else
                    emptyListTextView.setVisibility(View.INVISIBLE);
            }
        });
    }
}
