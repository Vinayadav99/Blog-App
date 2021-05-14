package com.example.blogapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private ImageButton profilePic;
    private Button createAccountBtn;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;

    private Uri imageUri = null;


    private ProgressDialog mProgressDialog;
    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("mUsers");
        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference().child("mBlog_Profiles");
        mProgressDialog = new ProgressDialog(this);

        firstName = findViewById(R.id.firstnameAct);
        lastName = findViewById(R.id.lastNameAct);
        email = findViewById(R.id.emailAct);
        password = findViewById(R.id.passwordAct);
        profilePic = findViewById(R.id.profilePic);
        createAccountBtn = findViewById(R.id.createAccountAct);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });
    }

    private void createNewAccount() {

        final String name = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        final String em = email.getText().toString().trim();
        String pw = password.getText().toString();
        Log.d("Debug","Inside create account method");
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pw)){
            mProgressDialog.setMessage("Creating Account");
            mProgressDialog.show();
            Log.d("Debug","inside create account method and the if condition");
            mAuth.createUserWithEmailAndPassword(em,pw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if(authResult!=null){
                        Log.d("Debug","inside success listener for mAuth create user");
                        StorageReference imagePath = mStorageReference.child("mBlog_Profiles").child(imageUri.getLastPathSegment());
                        imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("Debug","before while loop");
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while(!urlTask.isSuccessful());
                                Uri downloadUrl = urlTask.getResult();
                                Log.d("Debug","After while loop :::: "+downloadUrl.toString());

                                Log.d("Debug","inside success listener for image path dbreference");
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDB = mDatabaseReference.child(userId);
                                currentUserDB.child("firstName").setValue(name);
                                currentUserDB.child("lastName").setValue(lname);
                                currentUserDB.child("email").setValue(em);
                                currentUserDB.child("image").setValue(downloadUrl.toString());


                                mProgressDialog.dismiss();
                                startActivity(new Intent(CreateAccountActivity.this,PostListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        });


                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);


        }
    }
}