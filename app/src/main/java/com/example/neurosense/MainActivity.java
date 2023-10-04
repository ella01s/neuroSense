package com.example.neurosense;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.neurosense.authentication.ui.login.LoginActivity;
import com.example.neurosense.authentication.ui.signup.SignupActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonSignup;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttonSignup = findViewById(R.id.button_signup);
        buttonLogin = findViewById(R.id.button_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    public void launchSignup(View v) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    public void launchGameHub(View v) {
        Intent i = new Intent(this, GameHubActivity.class);
        startActivity(i);
    }
}
