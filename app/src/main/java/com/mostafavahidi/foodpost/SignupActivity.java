package com.mostafavahidi.foodpost;

/**
 * Created by Mostafa on 7/9/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mostafavahidi.foodpost.data.FoodPostUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @BindView(R.id.input_email)
    EditText emailSignupEditText;
    @BindView(R.id.input_password)
    EditText passwordSignupEditText;

    @BindView(R.id.input_name)
    EditText nameSignupEditText;
    @BindView(R.id.input_username)
    EditText usernameSignupEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_signup);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @OnClick(R.id.btn_signup)
    public void createClicked() {
        Log.i("TEST", "Create oncliked!");
        String email = emailSignupEditText.getText().toString();
        String password = passwordSignupEditText.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.i("TEST", "User: " + user.getUid());
                            FoodPostUser foodPostUser = new FoodPostUser();
                            foodPostUser.setName(nameSignupEditText.getText().toString());
                            foodPostUser.setUsername(usernameSignupEditText.getText().toString());
                            foodPostUser.setEmail(user.getEmail());
                            foodPostUser.setUid(user.getUid());

                            DatabaseReference reference = firebaseDatabase.getReference();
                            reference.child("users/" + user.getUid()).setValue(foodPostUser);

                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.e("TEST", "Failed to create the user.", task.getException());
                        }
                    }
                });
    }

    @OnClick(R.id.link_login)
    public void linkClicked() {
        Intent intentMain = new Intent(this, MainActivity.class);
        this.startActivity(intentMain);
    }

}
