package pl.denislewandowski.bankczasu.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.denislewandowski.bankczasu.ImageRotationUtil;
import pl.denislewandowski.bankczasu.dialogs.ChangeDataDialog;
import pl.denislewandowski.bankczasu.LoginValidator;
import pl.denislewandowski.bankczasu.R;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    private static final int PICK_FROM_FILE = 1;
    private static final int FROM_CAMERA = 2;
    private FirebaseUser user;
    private RelativeLayout changeLoginView, changeEmailView, changePasswordView, changePhotoView;
    private TextView userLogin, userEmail;
    private ImageView userImage;
    private TextView timeCurrencyValueTextView;

    private DatabaseReference db;
    private StorageReference storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference(user.getUid());

        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.my_profile);

        userLogin = getView().findViewById(R.id.userLoginTextView);
        userEmail = getView().findViewById(R.id.userEmailTextView);
        userImage = getView().findViewById(R.id.userImageView);
        timeCurrencyValueTextView = getView().findViewById(R.id.time_currency_value);

        setUpUserInformation();

        changeLoginView = getView().findViewById(R.id.change_login_view);
        changeEmailView = getView().findViewById(R.id.change_email_view);
        changePasswordView = getView().findViewById(R.id.change_password_view);
        changePhotoView = getView().findViewById(R.id.change_photo_view);

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

        db = FirebaseDatabase.getInstance().getReference("Users");
        db.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int timeCurrencyValue = dataSnapshot.child("timeCurrency").getValue(Integer.class);
                timeCurrencyValueTextView.setText(String.valueOf(timeCurrencyValue));
                userLogin.setText(dataSnapshot.child("Name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changePhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] items = {getString(R.string.take_photo), getString(R.string.choose_from_gallery)};
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.choose_photo));
                builder.setIcon(R.drawable.ic_photo_dialog);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            dispatchTakePictureIntent();
                        }
                        if (i == 1) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, PICK_FROM_FILE);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }


    private void setUpUserInformation() {
        if (user != null) {
            userLogin.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());

            Glide.with(this)
                    .load(R.drawable.ic_user)
                    .into(userImage);

            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext())
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(userImage);
                }
            });
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
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(user.getUid()).child("Name").setValue(editText.getText().toString());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;

        if (requestCode == FROM_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            addImageToFirebaseStorage(bitmap, 100);
        }

        if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK && data != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                bitmap = ImageRotationUtil.modifyOrientation(bitmap, inputStream);

                addImageToFirebaseStorage(bitmap, 7);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addImageToFirebaseStorage(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        Toast.makeText(getContext(), "Zapisywanie...", Toast.LENGTH_SHORT).show();

        UploadTask uploadTask = storage.putBytes(baos.toByteArray());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Nie udało się zmienić zdjęcia", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Zmieniono zdjęcie", Toast.LENGTH_SHORT).show();
                setUpUserInformation();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, FROM_CAMERA);
        }
    }
}

