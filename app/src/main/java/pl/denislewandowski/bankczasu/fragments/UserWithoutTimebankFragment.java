package pl.denislewandowski.bankczasu.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.dialogs.JoinTimebankDialogFragment;

public class UserWithoutTimebankFragment extends Fragment {

    private Button joinTimebankButton, createTimebankButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_without_timebank, container, false);

        joinTimebankButton = view.findViewById(R.id.join_timebank_button);
        createTimebankButton = view.findViewById(R.id.create_timebank_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        joinTimebankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinTimebankDialogFragment dialog = new JoinTimebankDialogFragment();
                dialog.show(getChildFragmentManager(), "JOIN_TIMEBANK_DIALOG" );
            }
        });


        createTimebankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTimebank();
            }
        });

    }

    private void createNewTimebank() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String newTimebankId = UUID.randomUUID().toString();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("Timebank").setValue(newTimebankId);
        FirebaseDatabase.getInstance().getReference("Timebanks")
                .child(newTimebankId).child("Members").push().setValue(userId);
    }
}
