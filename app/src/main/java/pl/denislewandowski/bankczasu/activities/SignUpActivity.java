package pl.denislewandowski.bankczasu.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.denislewandowski.bankczasu.LoginValidator;
import pl.denislewandowski.bankczasu.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText userNameEditText, emailEditText, passwordEditText, passwordEditText2;
    private Button signUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText2 = findViewById(R.id.passwordEditText2);
        signUpButton = findViewById(R.id.signUpButton);
        userNameEditText = findViewById(R.id.userNameEditText);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {
                    LoginValidator lv = new LoginValidator();
                    if (lv.validateName(userNameEditText) && lv.validateEmail(emailEditText) && lv.validatePasswords(passwordEditText, passwordEditText2)) {
                        registerUser();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Sprawdź połączenie z internetem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        final String name = userNameEditText.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    ref.child("timeCurrency").setValue(4);
                    ref.child("Name").setValue(name);
                    Toast.makeText(SignUpActivity.this, "Rejestracja przebiegła pomyślnie!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignUpActivity.this, "Jesteś już zarejestrowany!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else
            return false;
    }
}
