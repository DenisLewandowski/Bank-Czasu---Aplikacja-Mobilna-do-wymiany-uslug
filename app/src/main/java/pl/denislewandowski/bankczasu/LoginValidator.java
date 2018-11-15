package pl.denislewandowski.bankczasu;

import android.util.Patterns;
import android.widget.EditText;

public class LoginValidator {

    public boolean validateEmail(EditText emailEditText) {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Uzupełnij email");
            emailEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Wpisz poprawny adres");
            emailEditText.requestFocus();
            return false;
        }

        return true;
    }

    public boolean validatePassword(EditText passwordEditText) {
        String password = passwordEditText.getText().toString().trim();

        if (password.isEmpty()) {
            passwordEditText.setError("Wpisz hasło");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Hasło powinno mieć przynajmniej 6 znaków");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    public boolean validatePasswords(EditText passwordEditText, EditText passwordEditText2) {
        String password = passwordEditText.getText().toString().trim();
        String password2 = passwordEditText2.getText().toString().trim();

        if (password.length() < 6) {
            passwordEditText.setError("Hasło powinno mieć przynajmniej 6 znaków");
            passwordEditText.requestFocus();
            return false;
        }

        if (!password.equals(password2)) {
            passwordEditText2.setError("Podane hasła są różne");
            passwordEditText2.requestFocus();
            return false;
        }
        return true;
    }
}
