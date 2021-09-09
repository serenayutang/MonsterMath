package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import edu.neu.madcourse.monstermath.Model.User;

public class MainActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();
    // Previously set up for Firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    User user;
    String usernameStr;

    // Previously set up for two dialogs
//    Button btnSendDialog, btnHistory;
//    TextView username;
//    int numOfStickerSent;
//    TextView tvNumOfStickerSent;

    public static final String SERVER_KEY = "key=AAAAqIkUxg4:APA91bGznQvbuE1j_BynlqrHXXZwUYAOy7lFXOt6x_D7UExgRViC1cXAP4_DFdxmLAGmiD9mlQ0uAaVbPoYUTqMlGx7OYvzbI1_kHirMnSo0dWa4WJbcx5Ila75K9o7iyzy7zm27r6xg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }




}