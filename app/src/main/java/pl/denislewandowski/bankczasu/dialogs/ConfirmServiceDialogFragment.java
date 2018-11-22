package pl.denislewandowski.bankczasu.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.Service;

public class ConfirmServiceDialogFragment extends DialogFragment {

    private Button closeButton;
    private Button backCurrencyButton;
    private String userId;
    private String timebankId;
    private Service service;
    public boolean serviceConfirmed = false;

    public static ConfirmServiceDialogFragment newInstance(Service service) {
        ConfirmServiceDialogFragment frag = new ConfirmServiceDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("SERVICE", service);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.dialog_service_to_do_bought, container, false);
        closeButton = mView.findViewById(R.id.close_button);
        backCurrencyButton = mView.findViewById(R.id.confirm_button);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        timebankId = sharedPreferences.getString("CURRENT_TIMEBANK", "timebankID");

        service = (Service) getArguments().getSerializable("SERVICE");


        backCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveBackCurrency(service);
                deleteServiceFromDatabase(service);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return mView;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private void giveBackCurrency(final Service service) {
        final DatabaseReference timeCurrencyRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(service.getServiceOwnerId()).child("timeCurrency");
        timeCurrencyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int timeCurrency = dataSnapshot.getValue(Integer.class);
                    timeCurrencyRef.setValue(timeCurrency + service.getTimeCurrencyValue());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void deleteServiceFromDatabase(final Service service) {
        FirebaseDatabase.getInstance().getReference("Timebanks")
                .child(timebankId).child("Services").child(service.getId()).removeValue();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(service.getServiceOwnerId())
                .child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.getValue(String.class).equals(service.getId())) {
                            ds.getRef().removeValue();
                            serviceConfirmed = true;
                            Toast.makeText(getContext(), "Potwierdzono wykonanie usługi", Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}

