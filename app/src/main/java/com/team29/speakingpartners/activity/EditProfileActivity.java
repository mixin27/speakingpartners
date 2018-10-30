package com.team29.speakingpartners.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.team29.speakingpartners.R;
import com.team29.speakingpartners.net.ConnectionChecking;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = EditProfileActivity.class.getSimpleName();

    AppCompatEditText txtName, txtDateOfBirth;
    AppCompatSpinner spLevel, spCountry;
    RadioGroup radioGroup;
    AppCompatRadioButton radMale, radFemale;

    LinearLayout btnSave;

    ArrayList<String> levelLists, countryLists;
    String currentLevel = "", currentCountry = "";

    Date dateOfBirth;

    Calendar myCalendar;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // ActionBar
        setUpActionBar();

        if (ConnectionChecking.checkConnection(this)) {
            fetchData();
        }

        txtName = findViewById(R.id.edit_profile_user_name);
        txtDateOfBirth = findViewById(R.id.edit_profile_dob);

        countryLists = new ArrayList<>();
        spCountry = findViewById(R.id.sp_edit_profile_country);
        setUpCountryLists();

        levelLists = new ArrayList<>();
        spLevel = findViewById(R.id.sp_edit_profile_level);
        setUpLevelLists();

        radioGroup = findViewById(R.id.edit_gender_gp);
        radMale = findViewById(R.id.edit_rad_male);
        radFemale = findViewById(R.id.edit_rad_female);

        // Date Of Birth
        myCalendar = Calendar.getInstance();
        // DatePicker
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "");
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                dateOfBirth = myCalendar.getTime();
                txtDateOfBirth.setText(DateFormat.getDateInstance(DateFormat.FULL).format(dateOfBirth));
            }
        };

        txtDateOfBirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new DatePickerDialog(EditProfileActivity.this, date,
                            myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
            }
        });

        btnSave = findViewById(R.id.layout_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInformation();
            }
        });

    }

    private void updateUserInformation() {
        if (txtName.getText().toString().isEmpty()) {
            txtName.setError("Name is required!");
            txtName.requestFocus();
        }

        saveToFireStore();

    }

    private void saveToFireStore() {
        String name = txtName.getText().toString();
        String gender = radMale.isChecked() ? getString(R.string.str_user_gender_male) : getString(R.string.str_user_gender_female);
        String level = spLevel.getSelectedItem().toString();
        String country = spCountry.getSelectedItem().toString();



    }

    private void setUpCountryLists() {
        mFirestore.collection("countries")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("name"));
                                updateCountrySpinner(document.getString("name"));
                            }
                        } else {
                            Log.e(TAG, "FirebaseFirestore -> " + task.getException());
                        }
                    }
                });
    }

    private void updateCountrySpinner(String name) {
        countryLists.add(name);
        spCountry.setAdapter(new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, countryLists));
        if (currentCountry != null) {
            int i = countryLists.indexOf(currentCountry);
            spCountry.setSelection(i);
        }
    }

    private void setUpLevelLists() {
        mFirestore.collection("skills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("level"));
                                updateLevelSpinner(document.getString("level"));
                            }
                        } else {
                            Log.e(TAG, "FirebaseFirestore -> " + task.getException());
                        }
                    }
                });
    }

    private void updateLevelSpinner(String level) {
        levelLists.add(level);
        spLevel.setAdapter(new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, levelLists));
        if (currentLevel != null) {
            int i = levelLists.indexOf(currentLevel);
            spLevel.setSelection(i);
        }
    }

    private void fetchData() {
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            mFirestore.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Fetch User information success!");
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    txtName.setText(doc.getString("user_name"));

                                    String dob = getDateOfBirth(doc.getDate("date_of_birth"));
                                    txtDateOfBirth.setText(dob);

                                    String gender = doc.getString("gender");
                                    if (gender.equals("Male")) {
                                        radMale.setChecked(true);
                                    } else if (gender.equals("Female")) {
                                        radFemale.setChecked(true);
                                    }

                                    currentLevel = doc.getString("level");
                                    Log.d(TAG, "CurrentLevel => " + currentLevel);
                                    currentCountry = doc.getString("country");
                                    Log.d(TAG, "CurrentCountry => " + currentCountry);
                                }
                            }
                        }
                    });

        }
    }

    private String getDateOfBirth(Date date_of_birth) {
        if (date_of_birth == null) {
            return "";
        }

        return DateFormat.getDateInstance(DateFormat.FULL).format(date_of_birth);
    }

    private void setUpActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
