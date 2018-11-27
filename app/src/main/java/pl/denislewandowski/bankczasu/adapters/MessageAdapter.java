package pl.denislewandowski.bankczasu.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.model.Chat;
import pl.denislewandowski.bankczasu.model.UserItem;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private List<Chat> chatList;
    private List<UserItem> userItemList;
    private Context context;
    private UserItem userItem;
    private FirebaseUser user;

    public MessageAdapter(List<Chat> chatList, Context context, UserItem userItem) {
        this.chatList = chatList;
        this.context = context;
        this.userItem = userItem;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public MessageAdapter(List<Chat> chatList, List<UserItem> userItemList, Context context) {
        this.chatList = chatList;
        this.context = context;
        this.userItemList = userItemList;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        boolean isContinuous = false;
        boolean isFirst;

        if (position < chatList.size() - 1)
            isContinuous = chatList.get(position).getSender().equals(chatList.get(position + 1).getSender());
        if (position == 0)
            isFirst = true;
        else
            isFirst = !chatList.get(position).getSender().equals(chatList.get(position - 1).getSender());

        holder.showMessage.setText(chat.getMessage());
        if (getItemViewType(position) == MSG_TYPE_LEFT) {
            if (isFirst) {
                if (userItem != null)
                    holder.messageAuthorName.setText(userItem.getName());
                else {
                    holder.messageAuthorName.setText(getSenderName(chat.getSender()));
                }
                holder.messageAuthorName.setVisibility(View.VISIBLE);
            }
            if (isContinuous) {
                holder.userImageView.setVisibility(View.INVISIBLE);
            } else {
                if (userItem != null)
                    holder.setUserImage(userItem.getId());
                else
                    holder.setUserImage(chat.getSender());
            }
        }
    }


    private String getSenderName(String senderId) {
        for (UserItem ui : userItemList) {
            if (ui.getId().equals(senderId))
                return ui.getName();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView showMessage;
        TextView messageAuthorName;
        ImageView userImageView;


        ViewHolder(View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            userImageView = itemView.findViewById(R.id.user_image_message);
            messageAuthorName = itemView.findViewById(R.id.message_author_name);
        }

        private void setUserImage(String userId) {
            StorageReference storage = FirebaseStorage.getInstance().getReference(userId);
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

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

}
