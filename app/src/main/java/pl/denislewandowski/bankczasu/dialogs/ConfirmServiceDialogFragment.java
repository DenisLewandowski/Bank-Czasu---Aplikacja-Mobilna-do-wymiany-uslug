package pl.denislewandowski.bankczasu.dialogs;

import android.arch.lifecycle.ViewModelProviders;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.denislewandowski.bankczasu.FirebaseRepository;
import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.model.Service;
import pl.denislewandowski.bankczasu.TimebankViewModel;

public class ConfirmServiceDialogFragment extends DialogFragment {

    private Button closeButton;
    private Button backCurrencyButton;
    private String userId;
    private String timebankId;
    private FirebaseRepository repository;
    private Service service;
    private TimebankViewModel viewModel;
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
        repository = new FirebaseRepository(getActivity());
        service = (Service) getArguments().getSerializable("SERVICE");
        viewModel = ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);


        backCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.timeCurrencyReturn(service);
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

