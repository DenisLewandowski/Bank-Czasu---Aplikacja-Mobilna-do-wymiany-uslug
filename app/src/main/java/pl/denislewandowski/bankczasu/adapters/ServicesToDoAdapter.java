package pl.denislewandowski.bankczasu.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.model.Service;

public class ServicesToDoAdapter extends RecyclerView.Adapter<ServicesToDoAdapter.ViewHolder> {
    private List<Service> services;
    private Context context;
    private FirebaseUser currentUser;

    public ServicesToDoAdapter(List<Service> services, Context context) {
        this.services = services;
        this.context = context;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_to_do, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Service service = services.get(position);
        holder.bindView(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView currentUserServiceNameTextView, otherUserServiceNameTextView;
        TextView currentUserName, otherUserName;
        ImageView currentUserImageView, otherUserImageView, arrowImageView;
        TextView timeCurrencyTextView;
        TextView isDoneTextView;
        public RelativeLayout viewForeground, viewBackground;
        TextView leftBackgroundTextView, rightBackgroundTextView;
        ImageView leftBackgroundImageView, rightBackgroundImageView;
        public boolean isCurrentUserServiceOwner;

        ViewHolder(View itemView) {
            super(itemView);
            currentUserServiceNameTextView = itemView.findViewById(R.id.service_name_current);
            otherUserServiceNameTextView = itemView.findViewById(R.id.service_name_other);
            currentUserImageView = itemView.findViewById(R.id.current_user_image);
            currentUserName = itemView.findViewById(R.id.current_user_name);
            otherUserImageView = itemView.findViewById(R.id.other_user_image);
            otherUserName = itemView.findViewById(R.id.other_user_name);
            timeCurrencyTextView = itemView.findViewById(R.id.time_currency_value);
            arrowImageView = itemView.findViewById(R.id.arrow_image);
            isDoneTextView = itemView.findViewById(R.id.is_service_done);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            viewBackground = itemView.findViewById(R.id.view_background);
            leftBackgroundTextView = itemView.findViewById(R.id.confirm_text);
            leftBackgroundImageView = itemView.findViewById(R.id.confirm_icon);
            rightBackgroundTextView = itemView.findViewById(R.id.delete_text);
            rightBackgroundImageView = itemView.findViewById(R.id.delete_icon);
        }

        void bindView(Service service) {
            timeCurrencyTextView.setText(String.valueOf(service.getTimeCurrencyValue()));
            setUserImage(currentUser.getUid(), currentUserImageView);
            isCurrentUserServiceOwner = isCurrentUserService(service);
            if (isCurrentUserService(service)) {
                leftBackgroundImageView.setVisibility(View.INVISIBLE);
                leftBackgroundTextView.setVisibility(View.INVISIBLE);
                setUserImage(service.getClientId(), otherUserImageView);
                currentUserServiceNameTextView.setText(service.getName());
                setLeftArrowImage();
                isDoneTextView.setText(context.getString(R.string.swipe_to_cancel));
                setUserName(service.getServiceOwnerId(), currentUserName);
                setUserName(service.getClientId(), otherUserName);
            } else {
                setUserImage(service.getServiceOwnerId(), otherUserImageView);
                rightBackgroundImageView.setVisibility(View.INVISIBLE);
                rightBackgroundTextView.setVisibility(View.INVISIBLE);
                otherUserServiceNameTextView.setText(service.getName());
                setUserName(service.getClientId(), currentUserName);
                setUserName(service.getServiceOwnerId(), otherUserName);
            }
        }

        private void setUserName(String userId, final TextView userNameTextView) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(userId).child("Name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userNameTextView.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }

        private void setUserImage(String userId, final ImageView imageView) {
            StorageReference storage = FirebaseStorage.getInstance().getReference(userId + ".jpg");
            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context.getApplicationContext())
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView);
                }
            });
        }

        private void setLeftArrowImage() {
            arrowImageView.setImageResource(R.drawable.ic_arrow_left);
        }

        boolean isCurrentUserService(Service service) {
            return currentUser.getUid().equals(service.getServiceOwnerId());
        }
    }

    public void removeItem(int position) {
        services.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Service service, int position) {
        services.add(position, service);
        notifyItemInserted(position);
    }
}