package com.example.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static String TAG = "LoginAcitivity";
    
    private FirebaseAuth mAuth;

    private AppCompatButton signInButton, createUserButton;

    private EditText userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        signInButton = (AppCompatButton) findViewById(R.id.signInButton);
        signInButton.setPaintFlags(signInButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        createUserButton = (AppCompatButton) findViewById(R.id.createUserButton);

        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);
        


        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();

                if (emptyCheck(email, password))
                    return;
                

                createUserEmail(email, password);

            }
        });

        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();

                if (emptyCheck(email, password))
                    return;


                emailSignIn(email, password);

            }
        });


    }
    
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "nothing users", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean emptyCheck(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Email or Password is Empty", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }


    private void emailSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("User Email", user.getEmail());
                            Log.d("User Uid", user.getUid());
                        } else {
                            Log.d(TAG, "Fail : " + task.getException());
                        }
                    }
                });
    }

    private void createUserEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 생성 후 로그인 활성화
                            // FierbaseUser user = mAuth.getCurrentuser();
                            Log.d(TAG, "Success");
                        } else {
                            Log.d(TAG, "Fail : " + task.getException());
                        }

                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
    }

}