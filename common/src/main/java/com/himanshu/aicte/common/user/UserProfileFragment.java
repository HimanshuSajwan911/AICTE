package com.himanshu.aicte.common.user;

import static android.app.Activity.RESULT_OK;
import static com.himanshu.aicte.common.database.Constant.ADMIN_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.database.Constant.USER_COLLECTION_REFERENCE;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.himanshu.aicte.common.R;
import com.himanshu.aicte.common.user.authentication.SignInActivity;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";

    private static final int RESULT_CODE_SIGN_IN = 111;

    private EditText etFirstName, etLastName, etEmail, etPhone, etGender;

    private View mView;

    private String mUserType;

    public UserProfileFragment() {
        mUserType = User.TYPE_USER;
    }

    public UserProfileFragment(String userType) {
        mUserType = userType;
    }

    public void setUserType(String userType) {
        mUserType = userType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Profile");
        }

        etFirstName = mView.findViewById(R.id.textInputEditText_fragment_user_profile_first_name);
        etLastName = mView.findViewById(R.id.textInputEditText_fragment_user_profile_last_name);
        etEmail = mView.findViewById(R.id.textInputEditText_fragment_user_profile_email);
        etPhone = mView.findViewById(R.id.textInputEditText_fragment_user_profile_phone);
        etGender = mView.findViewById(R.id.textInputEditText_fragment_user_profile_gender);

        etFirstName.setKeyListener(null);
        etLastName.setKeyListener(null);
        etEmail.setKeyListener(null);
        etPhone.setKeyListener(null);
        etGender.setKeyListener(null);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            displaySignInButton();
            startSignInActivity();
        } else {
            displayUserProfile();
        }

        return mView;
    }

    public void displayUserProfile() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userIdText = "User ID: " + userId;
        TextView tvUserId = mView.findViewById(R.id.textView_fragment_user_profile_user_id);
        tvUserId.setText(userIdText);
        tvUserId.setOnClickListener(viewUserId -> {
            FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity != null) {
                ClipboardManager clipboardManager = (ClipboardManager) fragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("AICTEUserId", userId);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getActivity(), "User ID copied.", Toast.LENGTH_SHORT).show();
            }
        });

        DocumentReference documentUser;

        if (mUserType.equals(User.TYPE_ADMIN)) {
            documentUser = ADMIN_COLLECTION_REFERENCE.document(userId);
        } else {
            documentUser = USER_COLLECTION_REFERENCE.document(userId);
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);

        progressDialog.show();
        documentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        User user = task.getResult().toObject(User.class);
                        etFirstName.setText(user.getFirstName());
                        etLastName.setText(user.getLastName());
                        etEmail.setText(user.getEmail());
                        etPhone.setText(user.getPhone());
                        etGender.setText(user.getGender());
                    } else {
                        Toast.makeText(getContext(), "User data does not exists.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errMessage = task.getException().getMessage();
                    if (errMessage == null) {
                        errMessage = "Could not  retrieve user profile";
                    }
                    Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void refreshUserProfile() {
        displayUserProfile();
    }

    public void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();

        resetForm();
        startSignInActivity();

    }

    private void startSignInActivity() {
        Intent intentSignIn = new Intent(getContext(), SignInActivity.class);
        intentSignIn.putExtra("userType", mUserType);
        startActivityForResult(intentSignIn, RESULT_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                displayUserProfile();
                Button buttonSignIn = mView.findViewById(R.id.button_fragment_user_profile_sign_in);
                buttonSignIn.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void displaySignInButton(){
        Button buttonSignIn = mView.findViewById(R.id.button_fragment_user_profile_sign_in);
        buttonSignIn.setVisibility(View.VISIBLE);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity();
            }
        });
    }

    private void resetForm(){

        TextView tvUserId = mView.findViewById(R.id.textView_fragment_user_profile_user_id);
        tvUserId.setText(getString(R.string.user_id));

        etFirstName.setText(R.string.first_name);
        etLastName.setText(R.string.last_name);
        etEmail.setText(R.string.email);
        etPhone.setText(R.string.phone_number);
        etGender.setText(R.string.gender);

        displaySignInButton();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (id == R.id.menuItem_fragment_user_profile_refresh) {
                refreshUserProfile();
                return true;
            } else if (id == R.id.menuItem_fragment_user_profile_update_profile) {

            } else if (id == R.id.menuItem_fragment_user_profile_sign_out) {
                signOutUser();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}