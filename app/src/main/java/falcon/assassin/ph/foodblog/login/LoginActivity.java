package falcon.assassin.ph.foodblog.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import falcon.assassin.ph.foodblog.MainActivity;
import falcon.assassin.ph.foodblog.R;
import falcon.assassin.ph.foodblog.registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnRegister;
    EditText usernameField, passwordField;
    ProgressBar progressBarLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Button
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_reg);

        // Text Field
        usernameField = findViewById(R.id.loginUserNameField);
        passwordField = findViewById(R.id.loginPasswordField);

        progressBarLogin = findViewById(R.id.progressBarLogin);
        progressBarLogin.setVisibility(View.GONE);

        // Methods for onClickListener
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    /**
     * On click event listener
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_login:

                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    progressBarLogin.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        progressBarLogin.setVisibility(View.GONE);
                                        // if sign in is successful redirect to main
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        redirectToMain(user);
                                    } else {

                                        progressBarLogin.setVisibility(View.GONE);
                                        // if sign in is failed show error message
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {

                    Toast.makeText(LoginActivity.this, "Please provide username and password..",
                            Toast.LENGTH_SHORT).show();

                }
                break;

            case R.id.btn_reg:

                Intent regIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(regIntent);
                finish();
                break;

            default:

                break;
        }
    }

    /**
     * Redirect to main activity
     *
     * @param user
     */
    protected void redirectToMain(FirebaseUser user) {

        // Redirect to main activity
        Intent redirectIntentMain = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(redirectIntentMain);
        finish();
    }
}
