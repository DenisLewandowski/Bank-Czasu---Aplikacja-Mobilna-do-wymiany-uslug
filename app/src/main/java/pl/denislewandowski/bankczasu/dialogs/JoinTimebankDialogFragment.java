package pl.denislewandowski.bankczasu.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.denislewandowski.bankczasu.R;

public class JoinTimebankDialogFragment extends DialogFragment {

    private Button cancelButton;
    private Button joinTimebank;
    private EditText timebankCodeEditText;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.dialog_join_to_timebank, container, false);
        cancelButton = mView.findViewById(R.id.cancel_button);
        joinTimebank = mView.findViewById(R.id.confirm_button);
        timebankCodeEditText = mView.findViewById(R.id.timebank_code);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        joinTimebank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timebankCode = timebankCodeEditText.getText().toString().trim();
                if (!timebankCode.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("Timebanks").child(timebankCode)
                            .addListenerForSingleValueEvent(valueEventListener);
                } else {
                    timebankCodeEditText.setError("Wpisz kod banku czasu!");
                    timebankCodeEditText.requestFocus();
                }
            }
        });

        return mView;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                addUserToTimebank(dataSnapshot.getKey());
                getDialog().dismiss();
                Toast.makeText(getActivity(), "Dołączyłeś do banku czasu!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Bank czasu o podanym kodzie nie istnieje", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void addUserToTimebank(String timebankCode) {
        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Timebank").setValue(timebankCode);
        FirebaseDatabase.getInstance().getReference("Timebanks").child(timebankCode)
                .child("Members").push().setValue(userId);
    }

}

