package pl.denislewandowski.bankczasu;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseRepository {
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference timebankReference = FirebaseDatabase.getInstance().getReference("Timebanks");
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Activity activity;

    public FirebaseRepository(Activity activity) {
        this.activity = activity;
    }

    public void setUserName(final TextView userNameTextView) {
        usersReference.child(userId).child("Name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            userNameTextView.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void setUserImage(final ImageView userImage) {
        StorageReference storage = FirebaseStorage.getInstance().getReference(userId);
        Glide.with(activity)
                .load(R.drawable.ic_user)
                .into(userImage);
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(activity)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }
        });
    }

    public void addService(final Service service) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Timebank").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference("Timebanks")
                        .child(dataSnapshot.getValue(String.class))
                        .child("Services")
                        .child(service.getId())
                        .setValue(service);

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Services")
                        .push()
                        .setValue(service.getId());
                Toast.makeText(activity, "Dodano usługę!", Toast.LENGTH_LONG).show();
                activity.onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteService(final Service service, String timebankId) {
        timebankReference.child(timebankId).child("Services").child(service.getId()).removeValue();

        usersReference.child(userId).child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (service.getId().equals(snapshot.getValue(String.class))) {
                            snapshot.getRef().removeValue();
                            Toast.makeText(activity, "Usunięto usługę", Toast.LENGTH_LONG).show();
                            activity.onBackPressed();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void updateService(Service service, String timebankId) {
        timebankReference.child(timebankId).child("Services").child(service.getId())
                .updateChildren(service.toMap());
        Toast.makeText(activity, "Edytowano usługę", Toast.LENGTH_LONG).show();
        activity.onBackPressed();
    }

}
