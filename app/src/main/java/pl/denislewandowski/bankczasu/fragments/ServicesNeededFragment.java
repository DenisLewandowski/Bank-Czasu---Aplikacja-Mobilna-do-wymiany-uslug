package pl.denislewandowski.bankczasu.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.Service;
import pl.denislewandowski.bankczasu.adapters.ServicesNeededAdapter;

public class ServicesNeededFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services_needed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recycler_view_needed);

//        Service service = new Service();
//        service.setName("Oferta Pierwsza");
//        service.setCategory(Category.EDUCATION);
//        Service service2 = new Service();
//        service2.setName("Oferta Druga");
//        service2.setCategory(Category.HOME);

//        Service service3 = new Service();
//        service3.setName("Oferta Trzecia");



        List<Service> services = new ArrayList<>();
//        services.add(service);
//        services.add(service2);
//        services.add(service2);
//        services.add(service3);

        recyclerView.setAdapter(new ServicesNeededAdapter(services,getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
