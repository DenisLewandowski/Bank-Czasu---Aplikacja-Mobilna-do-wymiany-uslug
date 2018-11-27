package pl.denislewandowski.bankczasu.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.adapters.MessageAdapter;
import pl.denislewandowski.bankczasu.model.Chat;
import pl.denislewandowski.bankczasu.model.UserItem;


public class PrivateMessageFragment extends Fragment {
    private UserItem userItem;
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private List<Chat> chatList;
    private ImageButton sendMessageButton;
    private TextView messageTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userItem = (UserItem) getArguments().getSerializable("USER_ITEM");

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(userItem.getName());

        recyclerView = getView().findViewById(R.id.message_recycler_view);
        sendMessageButton = getView().findViewById(R.id.send_message_button);
        messageTextView = getView().findViewById(R.id.text_send_message);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        getMessages(FirebaseAuth.getInstance().getCurrentUser().getUid(), userItem.getId());

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageTextView.getText().toString();
                Chat chat = new Chat(FirebaseAuth.getInstance().getCurrentUser().getUid(), userItem.getId(), message);
                sendMessage(chat);
            }
        });
    }

    private void sendMessage(Chat chat) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Messages");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", chat.getSender());
        hashMap.put("reciver", chat.getReciver());
        hashMap.put("message", chat.getMessage());

        ref.push().setValue(hashMap);
    }

    private void getMessages(final String userId, final String chatPartnerId) {
        chatList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getReciver().equals(userId) && chat.getSender().equals(chatPartnerId) ||
                            chat.getSender().equals(userId) && chat.getReciver().equals(chatPartnerId)) {
                        chatList.add(chat);
                    }
                }

                adapter = new MessageAdapter(chatList, getContext(), userItem);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
