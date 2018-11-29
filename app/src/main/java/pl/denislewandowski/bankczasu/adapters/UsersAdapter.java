package pl.denislewandowski.bankczasu.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.fragments.PrivateMessageFragment;
import pl.denislewandowski.bankczasu.model.UserItem;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<UserItem> userList;
    private Context context;

    public UsersAdapter(List<UserItem> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserItem user = userList.get(position);
        holder.bindView(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, currencyValueTextView;
        ImageView userImageView, sendImage;

        ViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name);
            currencyValueTextView = itemView.findViewById(R.id.time_currency_value);
            userImageView = itemView.findViewById(R.id.user_image);
            sendImage = itemView.findViewById(R.id.send_message);
        }

        void bindView(final UserItem user) {
            userNameTextView.setText(user.getName());
            currencyValueTextView.setText(String.valueOf(user.getTimeCurrency()));

            sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                   PrivateMessageFragment fragment = new PrivateMessageFragment();
                   Bundle bundle = new Bundle();
                   bundle.putSerializable("USER_ITEM", user);
                   fragment.setArguments(bundle);
                   ft.replace(R.id.content_main, fragment);
                   ft.addToBackStack(null);
                   ft.commit();
                }
            });
            if(user.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                sendImage.setVisibility(View.GONE);
            }
            setUserImage(user.getId());
        }

        private void setUserImage(String userId) {
            StorageReference storage = FirebaseStorage.getInstance().getReference(userId + ".jpg");
            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context.getApplicationContext())
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(userImageView);
                }
            });
        }

    }

}
