package pl.denislewandowski.bankczasu.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import pl.denislewandowski.bankczasu.CategoryUtils;
import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.model.Service;
import pl.denislewandowski.bankczasu.fragments.EditServiceFragment;
import pl.denislewandowski.bankczasu.fragments.ServiceDescriptionFragment;

public class TimebankServicesAdapter extends RecyclerView.Adapter<TimebankServicesAdapter.ViewHolder> {
    private List<Service> servicesNeededList;
    private Context context;

    public TimebankServicesAdapter(List<Service> servicesNeededList, Context context) {
        this.servicesNeededList = servicesNeededList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Service service = servicesNeededList.get(position);
        holder.bindView(service);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                Bundle bundle = new Bundle();
                bundle.putSerializable("SERVICE", service);

                if(service.getServiceOwnerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    fragment = new EditServiceFragment();
                } else {
                    fragment = new ServiceDescriptionFragment();
                }
                fragment.setArguments(bundle);
                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return servicesNeededList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView categoryImageView;
        public TextView timeCurrencyTextView;
        public RelativeLayout parentLayout;
        public TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.offer_title);
            categoryImageView = itemView.findViewById(R.id.category_image);
            timeCurrencyTextView = itemView.findViewById(R.id.time_currency_value_list);
            parentLayout = itemView.findViewById(R.id.item_layout);
            userName = itemView.findViewById(R.id.service_owner_name);
        }

        void bindView(Service service) {
            titleTextView.setText(service.getName());
            timeCurrencyTextView.setText(String.valueOf(service.getTimeCurrencyValue()));
            if (categoryImageView != null) {
                Glide.with(context).load(CategoryUtils.imageResourceIds[service.getCategory()])
                        .into(categoryImageView);
            }
            getServiceOwnerName(service);
        }

        private void getServiceOwnerName(Service service) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(service.getServiceOwnerId()).child("Name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userName.setText(dataSnapshot.getValue(String.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }

    }
}
