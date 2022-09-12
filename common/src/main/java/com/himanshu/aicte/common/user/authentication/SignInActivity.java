package com.himanshu.aicte.common.user.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.himanshu.aicte.common.R;
import com.himanshu.aicte.common.user.User;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private String mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        mUserType = intent.getStringExtra("userType");
        if (mUserType == null || mUserType.equals("")) {
            mUserType = User.TYPE_USER;
        }

        TextView tvForgotPassword = findViewById(R.id.textView_sign_in_forgot_password);
        TextView tvSignUp = findViewById(R.id.textView_sign_in_sign_up);

        Button btSignIn = findViewById(R.id.button_sign_in_sign_in);
        btSignIn.setOnClickListener(view -> signInUser());

        tvForgotPassword.setOnClickListener(view -> forgotPassword());
        tvSignUp.setOnClickListener(view -> signUp());

    }


    private void signInUser() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        EditText etEmail = findViewById(R.id.textInputEditText_sign_in_email);
        EditText etPassword = findViewById(R.id.textInputEditText_sign_in_password);

        ProgressBar progressBar = findViewById(R.id.progressBar_sign_in);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

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

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (task.isSuccessful()) {

                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser == null) {
                    Toast.makeText(this, "could not find user data!", Toast.LENGTH_LONG).show();
                } else if (fbUser.isEmailVerified()) {

                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                    builder.setTitle("Email not verified");
                    builder.setMessage("Your Email is not verified! \nVerify it before signing in.")
                            .setCancelable(false)
                            .setNeutralButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();

                    fbUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "An email verification is sent to you.", Toast.LENGTH_SHORT).show();
                            } else {
                                String errMessage = task.getException().getMessage();
                                if (errMessage == null) {
                                    errMessage = "could not send verification email.";
                                }
                                Toast.makeText(SignInActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthWeakPasswordException | FirebaseAuthUserCollisionException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    Toast.makeText(SignInActivity.this, "incorrect email or password!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void forgotPassword() {
        Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void signUp() {
        Intent intentSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
        intentSignUp.putExtra("userType", mUserType);
        startActivity(intentSignUp);
    }

}