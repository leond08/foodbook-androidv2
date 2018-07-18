package falcon.assassin.ph.foodblog.account.setup;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import falcon.assassin.ph.foodblog.MainActivity;
import falcon.assassin.ph.foodblog.R;
import falcon.assassin.ph.foodblog.user.User;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolBar;
    private CircleImageView profileImage;
    private Button btnSetup;
    private EditText setupNameField;
    private Uri imageUri = null;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private ProgressBar setupProgressBar;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference userRef;
    private String name;
    private String userId;
    private boolean isChanged = false;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        init();
    }

    protected void init() {

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");
        userRef =  myRef.child(userId);

        mToolBar = findViewById(R.id.setupActionBar);
        setSupportActionBar(mToolBar);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        setupProgressBar = findViewById(R.id.setupProgressBar);
        setupProgressBar.setVisibility(View.GONE);

        profileImage = findViewById(R.id.profile_image);
        btnSetup = findViewById(R.id.btn_setup);
        setupNameField = findViewById(R.id.setUpNameField);

        getSupportActionBar().setTitle("Account Settings");

        profileImage.setOnClickListener(this);
        btnSetup.setOnClickListener(this);

        retriveUser();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {



        switch (view.getId()) {

            case R.id.profile_image:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        //Toast.makeText(SetupActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();

                        try {
                            ActivityCompat.requestPermissions(SetupActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                        catch (Exception e) {
                            Log.d("tag", e.getMessage().toString());
                        }

                    }
                    else {
                            cropImage();
                    }
                }
                else {

                    cropImage();
                }

                break;

            case R.id.btn_setup:

                name = setupNameField.getText().toString();

                StorageReference image_profile = mStorageRef.child("profile_images").child(userId + ".jpg");

                if (!TextUtils.isEmpty(name)) {

                    setupProgressBar.setVisibility(View.VISIBLE);

                    if (isChanged) {
                        image_profile.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content

                                    setupProgressBar.setVisibility(View.INVISIBLE);
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    User user = new User(name,downloadUrl.toString());

                                    // Insert data to database;
                                    addUser(user);
                                    Log.d("tag: Image Uri:", downloadUrl.toString());
                                    Toast.makeText(SetupActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...
                                    Toast.makeText(SetupActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                    else {

                        myRef.child(userId).child("fullname").setValue(name)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(SetupActivity.this, "Successfully save your settings", Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            });
                    }

                }
                else {
                    Toast.makeText(SetupActivity.this,"Please input your name...", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                profileImage.setImageURI(imageUri);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.d("tag", error.getMessage());
            }
        }
    }

    protected void addUser(User user) {

        myRef.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(SetupActivity.this, "Successfully save your settings", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
    }

    protected void cropImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);

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
                        setupNameField.setText(value.fullname);

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
