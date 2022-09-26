package com.himanshu.aicte.common.user.authentication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.himanshu.aicte.common.R;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Button btResetPassword = findViewById(R.id.button_forgot_password_reset);

        btResetPassword.setOnClickListener(view -> resetPassword());

    }

    private void resetPassword() {

        EditText etEmail = findViewById(R.id.editText_forgot_password_email);
        ProgressBar progressBar = findViewById(R.id.progressBar_forgot_password);

        String email = etEmail.getText().toString();

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

        FirebaseAuth fbAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                builder.setTitle("Password reset email sent");
                builder.setMessage("check your email for password reset!")
                        .setCancelable(false)
                        .setNeutralButton(android.R.string.ok, (dialog, id) -> finish());
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
        });

    }

}