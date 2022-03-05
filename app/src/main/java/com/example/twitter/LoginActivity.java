package com.example.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    public static String TAG = "LoginAcitivity";
    private static final int RC_SIGN_IN = 9001;
    
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private AppCompatButton signInButton, createUserButton, googleSignButton, githubSignButton;

    private EditText userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        signInButton = (AppCompatButton) findViewById(R.id.signInButton);
        signInButton.setPaintFlags(signInButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        createUserButton = (AppCompatButton) findViewById(R.id.createUserButton);

        googleSignButton = (AppCompatButton) findViewById(R.id.googleSignButton);
        githubSignButton = (AppCompatButton) findViewById(R.id.githubSignButton);

        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);

        userEmail.setText("tweet1234@gmail.com");
        userPassword.setText("123123123");
        
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

        googleSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                //GoogleSignInClient
               mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

                signIn();
            }
        });




    }
    
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            Log.d(TAG, "currentUser != null");


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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "Success");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "Fail : " + task.getException());
                        }

                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                //firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }


}