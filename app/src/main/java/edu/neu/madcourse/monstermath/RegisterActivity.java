package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText textInputUsername, textInputEmail, textInputPassword;
    Button btnRegister;

    ProgressBar progressBar;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        hideSystemUI();

        progressBar = findViewById(R.id.progressBar);

        textInputUsername = findViewById(R.id.username);
        textInputEmail = findViewById(R.id.email);
        textInputPassword = findViewById(R.id.password);

        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = textInputUsername.getText().toString().trim();
                String txtEmail = textInputEmail.getText().toString().trim();
                String txtPassword = textInputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                } else if (!validateUsername(txtUsername) || !validateEmailAddress(txtEmail) || !validatePassword(txtPassword)) {
                    Toast.makeText(RegisterActivity.this, "Invalid fields.", Toast.LENGTH_SHORT).show();
                } else {
                    register(txtUsername, txtEmail, txtPassword);
                }
            }
        });
    }

    private void register(String username, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("numOfGamesPlayed", 0);
                            hashMap.put("personalBestScoreEasy", 0);
                            hashMap.put("personalBestScoreMedium", 0);
                            hashMap.put("personalBestScoreHard", 0);

                            databaseReference.child("Users")
                                    .child(username)
                                    .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, GameSettingActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private boolean validateEmailAddress(String emailAddress) {
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress).find()){
            textInputEmail.setError("Please enter a valid email");
        } else {
            textInputEmail.setError(null);
            return true;
        }
        return false;
    }

    private boolean validatePassword(String password) {
        if (password.length() < 8){
            textInputPassword.setError("Password must have at least 8 characters");
        } else {
            return true;
        }
        return false;
    }

    private boolean validateUsername(String username) {
        final boolean[] result = {true};
        FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childData: snapshot.getChildren()) {
                            String thisUsername = childData.getKey();
                            if (thisUsername.equals(username)) {
                                result[0] = false;
                                textInputUsername.setError("Username exists.");
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return result[0];
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}