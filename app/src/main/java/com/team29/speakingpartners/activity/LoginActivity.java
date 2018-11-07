package com.team29.speakingpartners.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.team29.speakingpartners.MainActivity;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.net.ConnectionChecking;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    AppCompatTextView btnForgotPassword;
    AppCompatButton btnCreateNewAccount, btnLogin;
    AppCompatEditText txtEmail, txtPassword;
    AppCompatImageView btnShowPassword;
    private boolean isShowingPassword;

    ProgressBar progressLogin;

    private FirebaseAuth mAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Theme
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Fields
        txtEmail = findViewById(R.id.txt_login_email);
        txtPassword = findViewById(R.id.txt_login_password);
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    visibleShowHideButton(true);
                } else {
                    visibleShowHideButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnForgotPassword = findViewById(R.id.tv_forgot_password);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Forgot password is not available now.", Toast.LENGTH_SHORT).show();
            }
        });

        btnShowPassword = findViewById(R.id.show_password_text);
        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        // Progress
        progressLogin = findViewById(R.id.progress_login);

        // Login Button
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        // New account
        btnCreateNewAccount = findViewById(R.id.btn_create_new_account);
        btnCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private void togglePasswordVisibility() {
        if (isShowingPassword) {
            maskPassword();
        } else {
            unmaskPassword();
        }
        isShowingPassword = !isShowingPassword;
    }

    private void unmaskPassword() {
        txtPassword.setTransformationMethod(null);
        btnShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_black_24dp));
    }

    private void maskPassword() {
        txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        btnShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off_black_24dp));
    }

    private void visibleShowHideButton(boolean isShow) {
        if (isShow) {
            btnShowPassword.setVisibility(View.VISIBLE);
        } else {
            btnShowPassword.setVisibility(View.GONE);
        }
    }

    private void userLogin() {

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().toLowerCase().trim();

        if (email.isEmpty()) {
            txtEmail.setError("Email is required!");
            txtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Please enter a valid email!");
            txtEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            txtPassword.setError("Password is required!");
            txtPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            txtPassword.setError("Minimum length of password should be 6!");
            txtPassword.requestFocus();
            return;
        }

        enableDisableField(false);

        if (ConnectionChecking.checkConnection(getApplicationContext())) {
            doSignIn(email, password);
        } else {
            progressLogin.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            enableDisableField(true);
            txtPassword.requestFocus();
            Toast.makeText(getApplicationContext(), "No internet access!", Toast.LENGTH_SHORT).show();
        }

    }

    private void enableDisableField(boolean flag) {
        txtEmail.setEnabled(flag);
        txtPassword.setEnabled(flag);
        btnLogin.setEnabled(flag);
    }

    private void doSignIn(String email, String password) {
        progressLogin.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Login Successful");
                    finish();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    btnLogin.setVisibility(View.VISIBLE);
                    progressLogin.setVisibility(View.GONE);
                    txtPassword.requestFocus();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Login Failed!");
                progressLogin.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Login Falied", Toast.LENGTH_SHORT).show();
                enableDisableField(true);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User : " + currentUser.getEmail());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

    }
}
