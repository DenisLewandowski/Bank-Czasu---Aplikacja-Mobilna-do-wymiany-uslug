package pl.denislewandowski.bankczasu.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import pl.denislewandowski.bankczasu.CategoryUtils;
import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.Service;

public class ServiceDescriptionFragment extends Fragment {
    private Service service;
    private TextView serviceName, serviceDescription, serviceDate,
            timecurrencyValue, serviceUserName, serviceCategory;
    private ImageView categoryIcon;
    private Button closeButton, getServiceButton;
    private String timebankId;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        service = (Service) getArguments().getSerializable("SERVICE");
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        timebankId = sharedPreferences.getString("CURRENT_TIMEBANK", "null");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.service);

        serviceName = getView().findViewById(R.id.service_name);
        serviceDescription = getView().findViewById(R.id.service_description);
        serviceDate = getView().findViewById(R.id.creation_date);
        timecurrencyValue = getView().findViewById(R.id.time_currency_value);
        serviceUserName = getView().findViewById(R.id.service_owner_name);
        serviceCategory = getView().findViewById(R.id.category_name);
        categoryIcon = getView().findViewById(R.id.category_image);
        closeButton = getView().findViewById(R.id.close_button);
        getServiceButton = getView().findViewById(R.id.get_service_button);

        serviceName.setText(service.getName());
        serviceDescription.setText(service.getDescription());
        serviceDate.setText("");
        timecurrencyValue.setText(String.valueOf(service.getTimeCurrencyValue()));
        serviceCategory.setText(getResources().getStringArray(R.array.category_names)[service.getCategory()]);
        categoryIcon.setImageResource(CategoryUtils.imageResourceIds[service.getCategory()]);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(service.getCreationDate());
        serviceDate.setText(date);

        getServiceOwnerName();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        getServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setServiceClient();
            }
        });
    }

    private void setServiceClient() {
        int timeCurrency = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("TIME_CURRENCY", 0);
        if(timeCurrency >= service.getTimeCurrencyValue()) {
            service.setClientId(currentUserId);
            FirebaseDatabase.getInstance().getReference("Timebanks")
                    .child(timebankId).child("Services").child(service.getId())
                    .updateChildren(service.toMap());
            Map<String, Object> map = new HashMap<>();
            map.put("timeCurrency", (timeCurrency - service.getTimeCurrencyValue()));
            FirebaseDatabase.getInstance().getReference("Users").child(currentUserId).updateChildren(map);
            Toast.makeText(getContext(), "Kupiłeś usługę", Toast.LENGTH_LONG).show();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Nie masz wystarczającej ilości czasowaluty", Toast.LENGTH_LONG).show();
        }

    }

    private void getServiceOwnerName() {
        FirebaseDatabase.getInstance().getReference("Users").child(service.getServiceOwnerId())
                .child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName;
                if(dataSnapshot.exists()) {
                    userName = dataSnapshot.getValue(String.class);
                    serviceUserName.setText(userName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
