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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.Service;
import pl.denislewandowski.bankczasu.adapters.ServicesNeededAdapter;

public class ServicesOfferedFragment extends Fragment {

    List<Service> services;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private ServicesNeededAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services_offered, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recycler_view_offered);
        mAuth = FirebaseAuth.getInstance();

        // DODAWANIE NOWEJ USŁUGI:

//        Service service = new Service();
//        service.setName("Mycie samochodu");
//        service.setCategory(Category.HOME);
//        service.setDescription("Umyję samochód na wysoki połysk.");
//        service.setId(UUID.randomUUID().toString());
//        service.setCreationDate(new Date());
//        service.setTimeCurrencyValue(2);
//        service.setServiceOwnerId(mAuth.getCurrentUser().getUid());

//        FirebaseDatabase.getInstance()
//                .getReference("Services")
//                .child(service.getId())
//                .setValue(service);


        services = new ArrayList<>();
        adapter = new ServicesNeededAdapter(services, getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        getServicesFromDatabase();
    }


    private void getServicesFromDatabase() {
        FirebaseDatabase.getInstance().getReference("Services").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                services.add(dataSnapshot.getValue(Service.class));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
