package com.team29.speakingpartners.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team29.speakingpartners.R;
import com.team29.speakingpartners.helper.CheckPermissionForApp;
import com.team29.speakingpartners.helper.ImagePickerHelper;
import com.team29.speakingpartners.helper.ImageProcessingHelper;
import com.team29.speakingpartners.helper.RoundedCornersTransformation;
import com.team29.speakingpartners.model.UserModel;
import com.team29.speakingpartners.net.ConnectionChecking;
import com.team29.speakingpartners.utils.GlideApp;
import com.team29.speakingpartners.utils.GlideOptions;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = EditProfileActivity.class.getSimpleName();

    public static final int PERMISSION_REQ_ID_CAMERA = 10;

    AppCompatEditText txtName, txtDateOfBirth;
    AppCompatSpinner spLevel, spCountry;
    RadioGroup radioGroup;
    AppCompatRadioButton radMale, radFemale;

    AppCompatImageView imgProfile;
    AppCompatButton btnUploadProfileImage;

    LinearLayout btnSave, btnCancel;

    ArrayList<String> levelLists, countryLists;
    String currentLevel = "", currentCountry = "";

    Date dateOfBirth;

    Calendar myCalendar;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    Uri uriProfileImage = null;
    String urlProfileImage = "";
    String user_id = "";

    Bitmap profileImageBitmap;

    ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

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
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "");
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (myCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                    dateOfBirth = myCalendar.getTime();
                    txtDateOfBirth.setText(new SimpleDateFormat("dd/MM/yyyy").format(myCalendar.getTime()));
                } else {
                    dateOfBirth = null;
                    txtDateOfBirth.setText("Invalid Date.");
                }
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

        imgProfile = findViewById(R.id.img_edit_profile);

        btnUploadProfileImage = findViewById(R.id.btn_edit_profile_img);
        btnUploadProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CheckPermissionForApp.checkSelfPermission(
                            EditProfileActivity.this, Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
                        showPhotoPicker();
                    }
                }
            }
        });

        btnSave = findViewById(R.id.layout_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().isEmpty()) {
                    txtName.setError("Name is required!");
                    txtName.requestFocus();
                }
                saveToFireStore();

                finish();
            }
        });

        btnCancel = findViewById(R.id.layout_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_CAMERA : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoPicker();
                } else {
                    Toast.makeText(getApplicationContext(), "No permission for " + Manifest.permission.RECORD_AUDIO, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            uriProfileImage = ImagePickerHelper.getPickImageResultUri(this, data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profileImageBitmap = ImageProcessingHelper.scaleDownBitmapImage(bitmap, 300, true);
                imgProfile.setImageBitmap(profileImageBitmap);

                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f);
                circularProgressDrawable.start();

                GlideApp.with(this)
                        .load(profileImageBitmap)
                        .placeholder(circularProgressDrawable)
                        .apply(GlideOptions.bitmapTransform(
                                new RoundedCornersTransformation(
                                        EditProfileActivity.this, 5, 2, "#BDBDBD", 10)))
                        .into(imgProfile);
                storeImageToFirebase();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void storeImageToFirebase() {
        progressDialog.show();
        final StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("user-profile-images/"
                        + mAuth.getCurrentUser().getEmail()
                        + "/" + mAuth.getCurrentUser().getEmail()
                        + "-" + System.currentTimeMillis());
        Log.d(TAG, "Path = " + profileImageRef.getDownloadUrl());

        if (profileImageBitmap != null) {
            // progressBar.setVisibility(View.VISIBLE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            profileImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = profileImageRef.putBytes(data);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        urlProfileImage = task.getResult().toString();
                        Log.d(TAG, "Image URL = " + urlProfileImage);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }

    }

    private void showPhotoPicker() {
        ImagePickerHelper.startSelectImageIntent(EditProfileActivity.this);
    }


    private void saveToFireStore() {
        progressDialog.show();

        String name = txtName.getText().toString();
        String gender = radMale.isChecked() ? getString(R.string.str_user_gender_male) : getString(R.string.str_user_gender_female);
        String level = spLevel.getSelectedItem().toString();
        String country = spCountry.getSelectedItem().toString();

        DocumentReference docRef = mFirestore.collection("users").document(user_id);
        updateInformation(docRef, "user_name", name);
        updateInformation(docRef, "gender", gender);
        updateInformation(docRef, "level", level);
        updateInformation(docRef, "country", country);
        if (dateOfBirth != null) {
            docRef.update("date_of_birth", dateOfBirth);
        }
        if (!urlProfileImage.equals("")) {
            updateInformation(docRef, "url_photo", urlProfileImage);
        }
        updateInformation(docRef, "modified_date", new Date());
    }

    private void updateInformation(DocumentReference docRef, final String field_name, final String value) {

        docRef.update(field_name, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Update Success " + field_name + " = " + value);
                    }
                });
    }

    private void updateInformation(DocumentReference docRef, final String field_name, final Date date) {
        docRef.update(field_name, date)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Update Success " + field_name + " = " + date);
                        // progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setUpCountryLists() {
        mFirestore.collection("countries")
                .orderBy("name", Query.Direction.ASCENDING)
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
            Query query = mFirestore.collection("users")
                    .whereEqualTo("email", email);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen error", e);
                        return;
                    }

                    for (QueryDocumentSnapshot snapshot : snapshots) {
                        UserModel model = snapshot.toObject(UserModel.class).withId(snapshot.getId());
                        user_id = model.id;

                        txtName.setText(model.getUser_name());
                        txtDateOfBirth.setText(model.getDateOfBirthString());

                        String gender = model.getGender();
                        if (gender.equals("Male")) {
                            radMale.setChecked(true);
                        } else if (gender.equals("Female")) {
                            radFemale.setChecked(true);
                        }

                        currentLevel = model.getLevel();
                        Log.d(TAG, "CurrentLevel => " + currentLevel);
                        currentCountry = model.getCountry();
                        Log.d(TAG, "CurrentCountry => " + currentCountry);

                        String url_img = model.getUrl_photo();
                        if (!url_img.equals("")) {
                            GlideApp.with(getApplicationContext())
                                    .load(url_img)
                                    .into(imgProfile);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
