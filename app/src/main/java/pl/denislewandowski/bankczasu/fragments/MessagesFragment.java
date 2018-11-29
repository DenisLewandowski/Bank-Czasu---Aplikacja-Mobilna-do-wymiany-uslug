package pl.denislewandowski.bankczasu.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.TimebankViewModel;
import pl.denislewandowski.bankczasu.adapters.MessageAdapter;
import pl.denislewandowski.bankczasu.model.Chat;
import pl.denislewandowski.bankczasu.model.TimebankData;
import pl.denislewandowski.bankczasu.model.UserItem;


public class MessagesFragment extends Fragment {
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private List<UserItem> userItemList;
    private List<Chat> chatList;
    private ImageButton sendMessageButton;
    private TextView messageTextView;
    private DatabaseReference databaseReference;
    private TimebankData timebankData;
    private TimebankViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);
        userItemList = viewModel.usersData.getValue();
        timebankData = viewModel.timebankData.getValue();

        databaseReference = FirebaseDatabase.getInstance().getReference("Timebanks")
                .child(timebankData.getId()).child("Messages");

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.group_chat));

        recyclerView = view.findViewById(R.id.message_recycler_view);
        sendMessageButton = view.findViewById(R.id.send_message_button);
        messageTextView = view.findViewById(R.id.text_send_message);

        getMessages();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                messageTextView.setText("");
            }
        });
    }


    private void sendMessage() {
        Chat chat = new Chat();
        chat.setMessage(messageTextView.getText().toString());
        chat.setSender(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.push().setValue(chat);
    }

    private void getMessages() {
        viewModel.timebankData.observe(getActivity(), new Observer<TimebankData>() {
            @Override
            public void onChanged(@Nullable TimebankData timebankData) {
                chatList = timebankData.getMessages();
                if (chatList != null) {
                    adapter = new MessageAdapter(chatList, userItemList, getContext());
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}
