package pl.denislewandowski.bankczasu.dialogs;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.TimebankViewModel;

public class TimebankCodeDialogFragment extends DialogFragment {

    private TextView timebankCodeTextView;
    private Button closeButton, copyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_timebank_code, container, false);

        timebankCodeTextView = view.findViewById(R.id.timebank_code);
        closeButton = view.findViewById(R.id.cancel_button);
        copyButton = view.findViewById(R.id.copy_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("TIMEBANK_CODE", timebankCodeTextView.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TimebankViewModel viewModel = ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);
        timebankCodeTextView.setText(viewModel.timebankData.getValue().getId());
    }
}