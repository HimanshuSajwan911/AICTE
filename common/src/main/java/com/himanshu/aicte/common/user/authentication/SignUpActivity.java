package com.himanshu.aicte.common.user.authentication;

import static com.himanshu.aicte.common.database.Constant.ADMIN_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.database.Constant.USER_COLLECTION_REFERENCE;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.himanshu.aicte.common.R;
import com.himanshu.aicte.common.user.User;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private String mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        mUserType = intent.getStringExtra("userType");
        if (mUserType == null || mUserType.equals("")) {
            mUserType = User.TYPE_USER;
        }

        Button btSignup = findViewById(R.id.button_signup_signup);

        btSignup.setOnClickListener(view -> signUpUserWithEmailAndPassword());

    }


    private void signUpUserWithEmailAndPassword() {

        EditText etFirstName = findViewById(R.id.editText_signup_first_name);
        EditText etLastName = findViewById(R.id.editText_signup_last_name);
        EditText etEmail = findViewById(R.id.textInputEditText_signup_email);
        EditText etPassword = findViewById(R.id.textInputEditText_signup_password);
        EditText etConfirmPassword = findViewById(R.id.textInputEditText_signup_confirm_password);
        EditText etPhone = findViewById(R.id.textInputEditText_signup_phone);

        RadioGroup rgGender = findViewById(R.id.radioGroup_signup_gender);
        ProgressBar progressBar = findViewById(R.id.progressBar_signup);

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String phone = etPhone.getText().toString();
        String gender;

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        RadioButton rbGender = findViewById(selectedGenderId);

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name required!");
            etFirstName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name required!");
            etLastName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required!");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid Email!");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required!");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password length must be at least 6!");
            etPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password required!");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!TextUtils.equals(password, confirmPassword)) {
            etConfirmPassword.setError("Confirm password not matching with password!");
            etConfirmPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number required!");
            etPhone.requestFocus();
            return;
        }
        if (selectedGenderId == -1) {
            TextInputLayout tilGender = findViewById(R.id.textInputLayout_signup_gender);
            tilGender.setError("Gender is required!");
            return;
        }
        gender = rbGender.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, taskCreateUser -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (taskCreateUser.isSuccessful()) {
                        FirebaseUser fbUser = auth.getCurrentUser();
                        User user = new User(firstName, lastName, email, gender, phone, password, mUserType);

                        String userId = fbUser.getUid();
                        DocumentReference documentUser;

                        if (mUserType.equals(User.TYPE_ADMIN)) {
                            documentUser = ADMIN_COLLECTION_REFERENCE.document(userId);
                        } else {
                            documentUser = USER_COLLECTION_REFERENCE.document(userId);
                        }

                        documentUser.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Objects.requireNonNull(fbUser).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "User created successfully" + "\nAn email verification is sent to you.", Toast.LENGTH_LONG).show();

                                                Intent intentSignIn = new Intent(SignUpActivity.this, SignInActivity.class);
                                                startActivity(intentSignIn);
                                                finish();
                                            } else {
                                                String errMessage = task.getException().getMessage();
                                                if (errMessage == null) {
                                                    errMessage = "could not send email verification.";
                                                }
                                                Toast.makeText(SignUpActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    } else {
                        try {
                            throw Objects.requireNonNull(taskCreateUser.getException());
                        } catch (FirebaseAuthUserCollisionException existEmail) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setTitle("User already exists");
                            builder.setMessage(existEmail.getMessage())
                                    .setCancelable(false)
                                    .setNeutralButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());
                            AlertDialog alert = builder.create();
                            alert.show();

                        } catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });


    }

}