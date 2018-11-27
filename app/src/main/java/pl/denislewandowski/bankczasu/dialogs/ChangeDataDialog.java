package pl.denislewandowski.bankczasu.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pl.denislewandowski.bankczasu.R;


public class ChangeDataDialog {
    public static final int CHANGE_LOGIN_DIALOG = 1;
    public static final int CHANGE_EMAIL_DIALOG = 2;
    public static final int CHANGE_PASSWORD_DIALOG = 3;
    private final int DIALOG_TYPE;

    private Context context;
    private LayoutInflater layoutInflater;

    private int dialogTitleId;
    private int dialogHintId;
    public AlertDialog dialog;

    public ChangeDataDialog(Context ctx, LayoutInflater layoutInflater, int DIALOG_TYPE) {
        this.context = ctx;
        this.layoutInflater = layoutInflater;
        this.DIALOG_TYPE = DIALOG_TYPE;
        switch (DIALOG_TYPE) {
            case CHANGE_LOGIN_DIALOG: {
                dialogTitleId = R.string.change_login;
                dialogHintId = R.string.user_name;
                break;
            }
            case CHANGE_EMAIL_DIALOG: {
                dialogTitleId = R.string.change_email;
                dialogHintId = R.string.email;
                break;
            }
            case CHANGE_PASSWORD_DIALOG: {
                dialogTitleId = R.string.change_password;
                dialogHintId = R.string.password;
                break;
            }
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View mView;
        if(DIALOG_TYPE != CHANGE_PASSWORD_DIALOG) {
            mView = layoutInflater.inflate(R.layout.dialog_change_data, null);
            EditText editText = mView.findViewById(R.id.change_option_edittext);
            editText.setHint(dialogHintId);
        } else {
            mView = layoutInflater.inflate(R.layout.dialog_change_password, null);
        }
        TextView title = mView.findViewById(R.id.dialog_title);
        Button cancelButton = mView.findViewById(R.id.cancel_button);
        title.setText(dialogTitleId);
        builder.setView(mView);
        dialog = builder.create();
        dialog.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
}
