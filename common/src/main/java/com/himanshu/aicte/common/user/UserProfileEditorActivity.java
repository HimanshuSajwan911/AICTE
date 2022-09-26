package com.himanshu.aicte.common.user;

import static com.himanshu.aicte.common.database.Constant.ADMIN_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.database.Constant.ADMIN_PROFILE_IMAGE_PATH;
import static com.himanshu.aicte.common.database.Constant.USER_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.database.Constant.USER_PROFILE_IMAGE_PATH;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.himanshu.aicte.common.R;
import com.himanshu.aicte.common.util.GlideApp;

import java.util.HashMap;

public class UserProfileEditorActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;

    private final int IMAGE_SELECTOR_REQUEST_CODE = 102;

    private ImageView ivProfilePicture;
    private Uri imageUri;

    private StorageReference mStorageReference;

    private EditText etFirstName, etLastName, etEmail, etPhone;
    private RadioGroup rgGender;

    private String mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_editor);

        Intent intent = getIntent();
        mUserType = intent.getStringExtra("userType");
        if (mUserType == null || mUserType.equals("")) {
            mUserType = User.TYPE_USER;
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        ivProfilePicture = findViewById(R.id.imageView_user_profile_user_profile_editor_picture);

        ivProfilePicture.setOnClickListener(v -> selectImage());

        etFirstName = findViewById(R.id.textInputEditText_user_profile_first_name);
        etLastName = findViewById(R.id.textInputEditText_user_profile_last_name);
        etEmail = findViewById(R.id.textInputEditText_user_profile_email);
        etPhone = findViewById(R.id.textInputEditText_user_profile_phone);

        etEmail.setKeyListener(null);

        rgGender = findViewById(R.id.radioGroup_user_profile_gender);

        Button btUpdate = findViewById(R.id.button_user_profile_update);
        btUpdate.setOnClickListener(v -> updateUserProfile());

        displayProfileImage();
        displayUserProfile();

    }

    private void selectImage() {
        Intent intentImageSelector = new Intent();
        intentImageSelector.setType("image/*");
        intentImageSelector.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentImageSelector, IMAGE_SELECTOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SELECTOR_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                ivProfilePicture.setImageURI(imageUri);
                uploadProfileImage();
            } else {
                Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void displayUserProfile() {

        ProgressDialog progressFetchingData = new ProgressDialog(this);
        progressFetchingData.setMessage("Fetching Data");
        progressFetchingData.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressFetchingData.setIndeterminate(true);
        progressFetchingData.setProgress(0);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userIdText = "User ID: " + userId;
        TextView tvUserId = findViewById(R.id.textView_user_profile_editor_user_id);
        tvUserId.setText(userIdText);
        tvUserId.setOnClickListener(viewUserId -> {
            FragmentActivity fragmentActivity = UserProfileEditorActivity.this;
            ClipboardManager clipboardManager = (ClipboardManager) fragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("AICTEUserId", userId);
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(UserProfileEditorActivity.this, "User ID copied.", Toast.LENGTH_SHORT).show();
        });

        DocumentReference documentUser;

        if (mUserType.equals(User.TYPE_ADMIN)) {
            documentUser = ADMIN_COLLECTION_REFERENCE.document(userId);
        } else {
            documentUser = USER_COLLECTION_REFERENCE.document(userId);
        }

        ProgressDialog progressDialog = new ProgressDialog(UserProfileEditorActivity.this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);

        progressDialog.show();
        documentUser.get().addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {

                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    User user = task.getResult().toObject(User.class);
                    etFirstName.setText(user.getFirstName());
                    etLastName.setText(user.getLastName());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone());
                    String gender = user.getGender();
                    if (gender.equalsIgnoreCase("male")) {
                        rgGender.check(R.id.radioButton_user_profile_gender_male);
                    } else if (gender.equalsIgnoreCase("female")) {
                        rgGender.check(R.id.radioButton_user_profile_gender_female);
                    } else {
                        rgGender.check(R.id.radioButton_user_profile_gender_other);
                    }
                } else {
                    Toast.makeText(UserProfileEditorActivity.this, "User data does not exists.", Toast.LENGTH_SHORT).show();
                }
            } else {
                String errMessage = task.getException().getMessage();
                if (errMessage == null) {
                    errMessage = "Could not retrieve user profile";
                }
                Toast.makeText(UserProfileEditorActivity.this, errMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUserProfile() {

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
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

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("First name required!");
            etPhone.requestFocus();
            return;
        }

        if (selectedGenderId == -1) {
            TextInputLayout tilGender = findViewById(R.id.textInputLayout_user_profile_gender);
            tilGender.setError("Gender is required!");
            return;
        }
        gender = rbGender.getText().toString();

        String userId = FirebaseAuth.getInstance().getUid();

        HashMap<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("firstName", firstName);
        userUpdate.put("lastName", lastName);
        userUpdate.put("gender", gender);
        userUpdate.put("phone", phone);

        DocumentReference documentUser;
        if (mUserType.equals(User.TYPE_ADMIN)) {
            documentUser = ADMIN_COLLECTION_REFERENCE.document(userId);
        } else {
            documentUser = USER_COLLECTION_REFERENCE.document(userId);
        }

        documentUser.update(userUpdate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserProfileEditorActivity.this, "User updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                String errMessage = task.getException().getMessage();
                if (errMessage == null) {
                    errMessage = "Could not update user profile";
                }
                Toast.makeText(UserProfileEditorActivity.this, errMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void displayProfileImage(){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference;

        if (mUserType.equals(User.TYPE_ADMIN)) {
            pathReference = storageReference.child(ADMIN_PROFILE_IMAGE_PATH);
        } else {
            pathReference = storageReference.child(USER_PROFILE_IMAGE_PATH);
        }
        pathReference = pathReference.child(firebaseUser.getUid());

        GlideApp.with(this)
                .load(pathReference)
                .error(R.drawable.image_user_profile_100x117)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivProfilePicture);

    }

    private void uploadProfileImage() {
        StorageReference profileImageRef;

        if (mUserType.equals(User.TYPE_ADMIN)) {
            profileImageRef = mStorageReference.child(ADMIN_PROFILE_IMAGE_PATH);
        } else {
            profileImageRef = mStorageReference.child(USER_PROFILE_IMAGE_PATH);
        }

        profileImageRef.child(firebaseUser.getUid())
                .putFile(imageUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfileEditorActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        String errMessage = task.getException().getMessage();
                        if (errMessage == null) {
                            errMessage = "Could not upload user profile picture";
                        }
                        Toast.makeText(UserProfileEditorActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
