package pl.denislewandowski.bankczasu;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bankczasu.R;

public class AboutApplicationDialogFragment extends DialogFragment {
    private Button cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.dialog_about_application, container, false);
        cancelButton = mView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return mView;
    }

}
