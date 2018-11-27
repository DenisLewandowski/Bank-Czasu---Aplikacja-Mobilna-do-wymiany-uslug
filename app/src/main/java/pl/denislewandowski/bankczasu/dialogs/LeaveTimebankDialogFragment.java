package pl.denislewandowski.bankczasu.dialogs;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.R;

public class LeaveTimebankDialogFragment extends DialogFragment {

    private Button closeButton;
    private Button leaveTimebankButton;
    private String userId;
    private String timebankId;
    private List<String> servicesIdList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.dialog_exit_timebank, container, false);
        closeButton = mView.findViewById(R.id.close_button);
        leaveTimebankButton = mView.findViewById(R.id.leave_timebank_button);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        timebankId = sharedPreferences.getString("CURRENT_TIMEBANK", "timebankID");

        servicesIdList = new ArrayList<>();

        leaveTimebankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveTimebank();
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


    private void leaveTimebank() {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        final DatabaseReference timebankRef = FirebaseDatabase.getInstance().getReference("Timebanks").child(timebankId);

        userRef.child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    servicesIdList.add(ds.getValue(String.class));
                }
                deleteServicesFromTimebank();
                userRef.child("Services").removeValue();
                userRef.child("Timebank").setValue("");

                timebankRef.child("Members")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if(userId.equals(ds.getValue(String.class))) {
                                        timebankRef.child("Members").child(ds.getKey()).removeValue();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                getDialog().dismiss();
                Toast.makeText(getContext(), "Opuściłeś bank czasu", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteServicesFromTimebank() {
        FirebaseDatabase.getInstance().getReference("Timebanks")
                .child(timebankId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.child("Services").getChildren()) {
                        if (servicesIdList.contains(ds.getKey())) {
                            ds.getRef().removeValue();
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

