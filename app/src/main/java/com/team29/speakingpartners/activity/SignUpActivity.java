package com.team29.speakingpartners.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import com.team29.speakingpartners.MainActivity;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.net.ConnectionChecking;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = SignUpActivity.class.getSimpleName();

    AppCompatTextView tvAlreadyAccount;

    AppCompatRadioButton radMale, radFemale;
    RadioGroup radioGroup;

    AppCompatEditText txtUsername, txtEmail, txtPassword;
    AppCompatButton btnSignUp;

    AppCompatSpinner spCountries, spLevel;
    ArrayList<String> countryNameList, levelList;

    /*AppCompatCheckBox chkAgree;*/

    ProgressBar progressSignUp;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    private boolean isShowingPassword = false;
    AppCompatImageView btnShowPassword;

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

        setContentView(R.layout.activity_sign_up);

        // FirebaseFirestore
        mFirestore = FirebaseFirestore.getInstance();
        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // User information
        txtUsername = findViewById(R.id.txt_sign_up_user_name);
        txtEmail = findViewById(R.id.txt_sign_up_user_email);
        txtPassword = findViewById(R.id.txt_sign_up_user_password);
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

        btnShowPassword = findViewById(R.id.sign_up_show_password_text);
        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        levelList = new ArrayList<>();
        spLevel = findViewById(R.id.sp_speaking_level_list);
        setUpLevelList();

        countryNameList = new ArrayList<>();
        spCountries = findViewById(R.id.sp_country_list);
        setUpCountryList();

        // Gender (Radio Group)
        radioGroup = findViewById(R.id.gender_group);
        radMale = findViewById(R.id.rad_male);
        radFemale = findViewById(R.id.rad_female);

        // Have already account
        tvAlreadyAccount = findViewById(R.id.tv_already_have_account);
        tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        // Agreement CheckBox
        /*chkAgree = findViewById(R.id.chk_agree);
        chkAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "isChecked : " + isChecked);
                if (isChecked) {
                    btnSignUp.setEnabled(true);
                    btnSignUp.setTextColor(getResources().getColor(R.color.color_white));
                } else {
                    btnSignUp.setEnabled(false);
                    btnSignUp.setTextColor(getResources().getColor(R.color.color_grey));
                }
            }
        });*/

        // ProgressBar
        progressSignUp = findViewById(R.id.progress_sign_up);

        // Complete Sign Up
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionChecking.checkConnection(getApplicationContext())) {
                    checkUserDataCreatingAnAccount();
                } else {
                    Log.d(TAG, "No connection");
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void enableDisableField(boolean flag) {
        txtUsername.setEnabled(flag);
        txtPassword.setEnabled(flag);
        txtEmail.setEnabled(flag);
        spCountries.setEnabled(flag);
        spLevel.setEnabled(flag);
        radioGroup.setEnabled(flag);
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

    private void clearFieldFocus() {
        txtUsername.clearFocus();
        txtEmail.clearFocus();
        txtPassword.clearFocus();
    }

    private void checkUserDataCreatingAnAccount() {
        String user_name = txtUsername.getText().toString();
        String email = txtEmail.getText().toString().toLowerCase().trim();
        String password = txtPassword.getText().toString().trim();
        String gender = radMale.isChecked() ? getString(R.string.str_user_gender_male): getString(R.string.str_user_gender_female);

        if (user_name.isEmpty()) {
            txtUsername.setError("User name is required");
            txtUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Please enter a valid email!");
            txtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            txtPassword.setError("Password is required");
            txtPassword.requestFocus();
        }

        if (txtPassword.length() < 6) {
            txtUsername.setError("Minimum length of password should be 6!");
            txtUsername.requestFocus();
        }

        String level = spLevel.getSelectedItem().toString();
        String country = spCountries.getSelectedItem().toString();

        clearFieldFocus();
        enableDisableField(false);
        btnSignUp.setVisibility(View.GONE);
        progressSignUp.setVisibility(View.VISIBLE);
        doUserSignUp(email, password, user_name, gender, level, "");
    }

    private void doUserSignUp(final String email, String password, final String user_name, final String gender, final String level, final String country) {

        // FirebaseAuth registering
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sign Up Successful");

                            storeUserDataToFirestore(email, user_name, gender, level, country);

                            finish();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            Log.d(TAG, "Sign Up Failed");
                            enableDisableField(true);
                            btnSignUp.setVisibility(View.VISIBLE);
                            progressSignUp.setVisibility(View.GONE);

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Log.d(TAG, "Already registered");
                                Toast.makeText(getApplicationContext(), "You have already registered!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "FirebaseAuthError => " + task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void storeUserDataToFirestore(String email, String user_name, String gender, String level, String country) {
        // Store user data in FirebaseFirestore
        UserModel user = new UserModel(user_name, email, gender,
                level, country, "", new Date(), new Date(), null, 1);
        mFirestore.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Store user data successful!");
                        progressSignUp.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "StoreUserDataError => " + e.getMessage());
                        enableDisableField(true);
                    }
                });
    }

    private void setUpLevelList() {
        mFirestore.collection("skills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("level"));
                                updateLevelSpinner(document.get("level").toString());
                            }
                        } else {
                            Log.e(TAG, "FirebaseFirestore -> " + task.getException());
                        }
                    }
                });
    }

    private void updateLevelSpinner(String level) {
        levelList.add(level);
        spLevel.setAdapter(new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_dropdown_item, levelList));
    }

    // Country List
    private void setUpCountryList() {
        mFirestore.collection("countries")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("name"));
                                updateCountrySpinner(document.get("name").toString());
                            }
                        } else {
                            Log.e(TAG, "FirebaseFirestore -> " + task.getException());
                        }
                    }
                });
    }

    private void updateCountrySpinner(String name) {
        countryNameList.add(name);
        spCountries.setAdapter(
                new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_dropdown_item, countryNameList));
    }

//    private void updateDateOfBirthField() {
//        String format = getResources().getString(R.string.str_date_format);
//        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
//
//        txtBirthday.setText(sdf.format(myCalendar.getTime()));
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
