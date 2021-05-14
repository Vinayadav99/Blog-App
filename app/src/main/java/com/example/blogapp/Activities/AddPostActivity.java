package com.example.blogapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private EditText mPostTitle;
    private EditText mPostDesc;
    private ImageButton mPostImage;
    private Button mSubmitButton;

    private ProgressDialog mProgress;

    private Uri imageUri;
    private static final int GALLERY_CODE = 1;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mPostDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("mBlog");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);

        mPostImage = findViewById(R.id.imageButton);
        mPostTitle = findViewById(R.id.postTitleEt);
        mPostDesc = findViewById(R.id.descriptionEt);
        mSubmitButton = findViewById(R.id.submitPost);

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        mProgress.setMessage("Posting....");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if(!titleVal.isEmpty() && !descVal.isEmpty() && imageUri!=null){
            StorageReference filePath = mStorage.child("MBlog_images").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    DatabaseReference newPost = mPostDatabase.push();

                    Map<String,String> map = new HashMap<>();
                    map.put("title",titleVal);
                    map.put("desc",descVal);
                    map.put("timeStamp",String.valueOf(java.lang.System.currentTimeMillis()));
                    map.put("userId",mUser.getUid());
                    map.put("image",downloadUrl.toString());

                    newPost.setValue(map);

                    mProgress.dismiss();

                    startActivity(new Intent(AddPostActivity.this,PostListActivity.class));
                }
            });

        }else{
            Toast.makeText(AddPostActivity.this,"Please fill all fields",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK){
            imageUri = data.getData();
            mPostImage.setImageURI(imageUri);
        }
    }
}