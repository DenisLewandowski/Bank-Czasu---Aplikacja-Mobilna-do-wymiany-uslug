package pl.denislewandowski.bankczasu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bankczasu.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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

        Service service = new Service();
        service.setName("Oferta Pierwsza");
//        Service service2 = new Service();
//        service2.setName("Oferta Druga");
//        Service service3 = new Service();
//        service3.setName("Oferta Trzecia");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.setValue(service);

        List<Service> services = new ArrayList<>();
        services.add(service);
//        services.add(service2);
//        services.add(service3);

        recyclerView.setAdapter(new ServicesNeededAdapter(services,getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
