package pl.denislewandowski.bankczasu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bankczasu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyProfileFragment extends Fragment {

    private FirebaseUser user;
    private RelativeLayout changeLoginView, changeEmailView, changePasswordView;
    private TextView userLogin, userEmail;
    private ImageView userImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.my_profile);

        userLogin = getView().findViewById(R.id.userLoginTextView);
        userEmail = getView().findViewById(R.id.userEmailTextView);
        userImage = getView().findViewById(R.id.userImageView);

        setUpUserInformation();

        changeLoginView = getView().findViewById(R.id.change_login_view);
        changeEmailView = getView().findViewById(R.id.change_email_view);
        changePasswordView = getView().findViewById(R.id.change_password_view);

        changeLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(ChangeDataDialog.CHANGE_LOGIN_DIALOG);
            }
        });

        changeEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(ChangeDataDialog.CHANGE_EMAIL_DIALOG);
            }
        });

        changePasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(ChangeDataDialog.CHANGE_PASSWORD_DIALOG);
            }
        });
    }


    private void setUpUserInformation() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userLogin.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(userImage);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_default_image)
                        .into(userImage);
            }
        }
    }

    private void createDialog(final int DIALOG_TYPE) {
        final ChangeDataDialog changeDataDialog =
                new ChangeDataDialog(getContext(), getLayoutInflater(), DIALOG_TYPE);
        changeDataDialog.showDialog();
        final LoginValidator lv = new LoginValidator();
        final EditText editText = changeDataDialog.dialog.findViewById(R.id.change_option_edittext);

        changeDataDialog.dialog.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (DIALOG_TYPE) {
                    case ChangeDataDialog.CHANGE_EMAIL_DIALOG: {
                        if (lv.validateEmail(editText)) {
                            user.updateEmail(editText.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                changeDataDialog.dialog.cancel();
                                                setUpUserInformation();
                                                Toast.makeText(getContext(), "Zmieniono email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        break;
                    }
                    case ChangeDataDialog.CHANGE_LOGIN_DIALOG: {
                        if (!editText.getText().toString().isEmpty()) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editText.getText().toString()).build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                changeDataDialog.dialog.cancel();
                                                setUpUserInformation();
                                                Toast.makeText(getContext(), "Zmieniono nazwę użytkownika", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            editText.setError("Wpisz nazwę użytkownika!");
                            editText.requestFocus();
                        }
                        break;
                    }
                    case ChangeDataDialog.CHANGE_PASSWORD_DIALOG: {
                        EditText newPasswordEditText = changeDataDialog.dialog.findViewById(R.id.new_password_edittext);
                        EditText newPasswordEditText2 = changeDataDialog.dialog.findViewById(R.id.new_password_edittext2);
                        if (lv.validatePasswords(newPasswordEditText, newPasswordEditText2)) {
                            user.updatePassword(newPasswordEditText.getText().toString());
                            changeDataDialog.dialog.cancel();
                            Toast.makeText(getContext(), "Zmieniono hasło", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }
        });
    }

}
