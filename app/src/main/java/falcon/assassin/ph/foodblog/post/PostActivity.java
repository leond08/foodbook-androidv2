package falcon.assassin.ph.foodblog.post;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import falcon.assassin.ph.foodblog.MainActivity;
import falcon.assassin.ph.foodblog.R;
import falcon.assassin.ph.foodblog.account.setup.SetupActivity;
import falcon.assassin.ph.foodblog.model.Post;
import falcon.assassin.ph.foodblog.user.User;
import id.zelory.compressor.Compressor;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolBar;
    private EditText postDescField;
    private ImageView postImageView;
    private Uri imageUri = null;
    private TextView mTitleActionBar, mTitleActionBarLeft, mTitleActionBarRight;
    private ProgressBar postProgressBar;


    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;

    private String currentUser;
    private String postDesc;
    private File thumbnailImage;
    private String thumbnailUri = null;
    private TextView postFullName;
    private CircleImageView profileImage;
    private DatabaseReference userRef;
    private boolean isSuccessUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postProgressBar = findViewById(R.id.postProgressBar);
        postProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        postFullName = findViewById(R.id.postFullName);
        profileImage = findViewById(R.id.postImageThumb);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userRef = database.getReference("user").child(currentUser);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mToolBar = findViewById(R.id.mainActionBar);
        setSupportActionBar(mToolBar);

        postDescField = findViewById(R.id.postDescField);
        postImageView = findViewById(R.id.postImageView);

        postImageView.setOnClickListener(this);

        getSupportActionBar().setTitle("");
        mTitleActionBar = mToolBar.findViewById(R.id.actionCustomTitle);
        mTitleActionBar.setText("Create Food");
        mTitleActionBarLeft = mToolBar.findViewById(R.id.actionCustomTitleLeft);
        mTitleActionBarLeft.setText("Cancel");
        mTitleActionBarRight = mToolBar.findViewById(R.id.actionCustomTitleRight);
        mTitleActionBarRight.setText("Post");

        mTitleActionBarLeft.setOnClickListener(this);
        mTitleActionBarRight.setOnClickListener(this);

        // Retrieve User
        retriveUser();

    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                postImageView.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.d("tag", error.getMessage());
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.postImageView:

                cropImage();

                break;

            case R.id.actionCustomTitleLeft:

                // Cancel

                Intent cancelIntent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(cancelIntent);
                finish();

                break;

            case R.id.actionCustomTitleRight:

                postDesc = postDescField.getText().toString();
                String randomId = UUID.randomUUID().toString();

                StorageReference image_post = mStorageRef.child("post_file").child(currentUser).child(randomId + ".jpg");
                StorageReference thumbnailPost = mStorageRef.child("post_file/thumbnails").child(currentUser).child(randomId + ".jpg");

                if (!TextUtils.isEmpty(postDesc) && imageUri != null) {
                    postProgressBar.setVisibility(View.VISIBLE);
                        try {

                            // Create thumbnail......
                            File file = new File(imageUri.getPath());
                            // Compress file

                            thumbnailImage = new Compressor(PostActivity.this)
                                    .setMaxWidth(200)
                                    .setMaxHeight(200)
                                    .setQuality(10)
                                    .compressToFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {

                            if (thumbnailImage.isFile()) {

                                InputStream stream = new FileInputStream(thumbnailImage);

                                UploadTask uploadTask = thumbnailPost.putStream(stream);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Log.d("tag", exception.getMessage().toString());
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                        // ...
                                        thumbnailUri = taskSnapshot.getDownloadUrl().toString();
                                        Log.d("tag", "Thumbnail saved!");

                                    }
                                });
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    // Upload original File
                        image_post.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        postProgressBar.setVisibility(View.INVISIBLE);

                                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postObj = new HashMap<String, Object>();
                                        postObj.put("description",postDesc);
                                        postObj.put("originalImageUri", downloadUrl);
                                        postObj.put("thumbnailUri", thumbnailUri);
                                        postObj.put("timeStamp", ServerValue.TIMESTAMP);
                                        postObj.put("userId", currentUser);

                                        // Insert data to database;
                                        postFood(postObj);
                                        Log.d("tag: Image Uri:", downloadUrl.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                        Toast.makeText(PostActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {

                        Toast.makeText(PostActivity.this, "Please provide details..", Toast.LENGTH_SHORT).show();
                    }

                break;
        }
    }

    protected void cropImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(PostActivity.this);

    }

    protected void postFood(Map<String, Object> post){
        String random = UUID.randomUUID().toString();

        myRef.child("post").push().setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(PostActivity.this, "Posted Successfully", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
    }


    protected void retriveUser() {
        // Read from the database

        try {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated

                    if (dataSnapshot.exists()) {

                        User value = dataSnapshot.getValue(User.class);
                        postFullName.setText(value.fullname);

                        imageUri = Uri.parse(value.imageUri);

                        Glide
                                .with(getApplicationContext())
                                .load(value.imageUri)
                                .into(profileImage);

                    } else {

                        Log.d("tag", "No record to retrieve");
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("tag", error.toException().toString());
                }
            });
        }
        catch (Exception error) {
            Log.d("tag", error.getMessage());
        }
    }
}
