package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import edu.neu.madcourse.monstermath.Model.Score;
import edu.neu.madcourse.monstermath.Model.ScoreAdapter;
import edu.neu.madcourse.monstermath.Model.User;

public class ScoreBoardActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();
    private String level;
    static String USERNAME;
    private ArrayList<Score> scoreList = new ArrayList<>();
    private RecyclerView rvScores;
    private ScoreAdapter adapter;

    // personal best score settings
    private TextView tvPersonalBestScore, tvNumOfGamesPlayed;
    private int numOfGamesPlayed;
    private Button btnScoreBoardHome;

    // Firebase settings
    DatabaseReference rootDatabaseRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        hideSystemUI();

        // get username
        getUsername();

        // default level is easy
        level = "Easy";

        // connect UI components
        tvNumOfGamesPlayed = findViewById(R.id.tvNumOfGamesPlayed);
        tvPersonalBestScore = findViewById(R.id.tvPersonalBestScore);
        btnScoreBoardHome = findViewById(R.id.btnScoreBoardHome);

        btnScoreBoardHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScoreBoardActivity.this, GameSettingActivity.class));
            }
        });

        // set up for RecyclerView
        rvScores = (RecyclerView) findViewById(R.id.rvScores);
        adapter = new ScoreAdapter(scoreList);
        rvScores.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvScores.setLayoutManager(layoutManager);

        setScoreLevel();

        // get real-time number of games played
        readNumOfGamesPlayed();

        // read personal best score
        readPersonalBestScore();

        // read score rankings
        readScoreRanking();

        // get current user's device token
        getCurrentToken();
    }

    private void getCurrentToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();

                        rootDatabaseRef
                                .child("Users")
                                .child(USERNAME)
                                .child("token")
                                .setValue(token);
                    }
                });
    }

    private void readNumOfGamesPlayed() {
        // get real-time number of games played
        rootDatabaseRef
                .child("Users")
                .child(USERNAME)
                .child("numOfGamesPlayed")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        numOfGamesPlayed = snapshot.getValue(Integer.class);
                        tvNumOfGamesPlayed.setText(USERNAME + ", you have played " + numOfGamesPlayed + " rounds");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setScoreLevel() {
        // set up for toggle button group
        MaterialButtonToggleGroup toggleButtonGroupScore = findViewById(R.id.toggleBtnGrpScore);
        // add on button checked listener
        toggleButtonGroupScore.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnScoreEasy:
                            level = "Easy";
                            readPersonalBestScore();
                            readScoreRanking();
                            break;
                        case R.id.btnScoreMedium:
                            level = "Medium";
                            readPersonalBestScore();
                            readScoreRanking();
                            break;
                        case R.id.btnScoreHard:
                            level = "Hard";
                            readPersonalBestScore();
                            readScoreRanking();
                            break;
                    }
                }
            }
        });
    }
    private void getUsername() {
        USERNAME = getIntent().getExtras().getString("USERNAME");
    }

    private void readPersonalBestScore() {
        // get personal best score
        rootDatabaseRef
                .child("Users")
                .child(USERNAME)
                .child("personalBestScore" + level)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(ScoreBoardActivity.this, "You have not played a game yet.", Toast.LENGTH_LONG).show();
                        } else {
                            Integer personalBest = snapshot.getValue(Integer.class);
                            tvPersonalBestScore.setText("Your personal best score: " + personalBest);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readScoreRanking() {
        rootDatabaseRef
                .child("Scores")
                .child(level.toLowerCase())
                .orderByChild("score")
                .limitToLast(50)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        scoreList.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Score score = dataSnapshot.getValue(Score.class);
                            scoreList.add(0, score);
                        }
                        // update adapter
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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