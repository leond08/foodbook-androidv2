package falcon.assassin.ph.foodblog.registration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
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
import falcon.assassin.ph.foodblog.account.setup.SetupActivity;
import falcon.assassin.ph.foodblog.login.LoginActivity;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegister, btnRegLogin;
    EditText regUsernameField,
             regPasswordField,
             regConfirmPasswordField;
    ProgressBar regProgressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // initialize content
        init();
    }


    protected void init() {

        mAuth = FirebaseAuth.getInstance();
        regUsernameField = findViewById(R.id.reguserNameField);
        regPasswordField = findViewById(R.id.regpasswordField);
        regConfirmPasswordField = findViewById(R.id.regconfirmPasswordField);

        regProgressBar = findViewById(R.id.regProgressBar);
        regProgressBar.setVisibility(View.GONE);

        btnRegLogin = findViewById(R.id.btn_reg_login);
        btnRegister = findViewById(R.id.btn_register);

        btnRegLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_register:

                String email = regUsernameField.getText().toString();
                String password = regPasswordField.getText().toString();
                String confirmPassword = regConfirmPasswordField.getText().toString();

                // check if fields is not empty
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&
                        !TextUtils.isEmpty(confirmPassword)) {

                    if (password.equals(confirmPassword)) {
                        regProgressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        regProgressBar.setVisibility(View.GONE);
                                        Log.d("tag", "Registration Successful");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        redirect(user);
                                    } else {
                                        regProgressBar.setVisibility(View.GONE);
                                        // If sign in fails, display a message to the user.
                                        Log.w("tag", task.getException().getMessage());
                                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                         });
                    }
                    else {

                        Toast.makeText(RegistrationActivity.this, "Password didn't match..",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                else {

                    Toast.makeText(RegistrationActivity.this, "Please provide required fields",
                            Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_reg_login:

                Intent intentLogin = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                finish();

                break;
        }
    }

    protected void redirect(FirebaseUser user) {

        // Redirect to main activity
        Intent redirectIntent = new Intent(RegistrationActivity.this, SetupActivity.class);
        startActivity(redirectIntent);
        finish();
    }
}
