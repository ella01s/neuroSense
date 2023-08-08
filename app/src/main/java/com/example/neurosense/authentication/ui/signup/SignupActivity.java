package com.example.neurosense.authentication.ui.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.neurosense.R;
import com.example.neurosense.authentication.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextDOB;
    private EditText editTextMOB;
    private EditText editTextYOB;
    private Button buttonRegister;
    private Switch switchSignupToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.editText_name);
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editTextT_password);
        editTextDOB = findViewById(R.id.editText_DOB);
        editTextMOB = findViewById(R.id.editText_MOB);
        editTextYOB = findViewById(R.id.editText_YOB);
        buttonRegister = findViewById(R.id.button_register);
        switchSignupToLogin = findViewById(R.id.switch_signup_to_login);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        switchSignupToLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Switch to the login activity
                    // Replace LoginActivity.class with the actual class for your login activity
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void register() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String dob = editTextDOB.getText().toString().trim();
        String mob = editTextMOB.getText().toString().trim();
        String yob = editTextYOB.getText().toString().trim();

        // Perform your registration logic here
        // You can validate the input fields and save the user data to your database

        // Show a toast message indicating successful registration
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
    }
}
