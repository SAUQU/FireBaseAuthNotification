package com.example.segundoauqui.firebaseauthandnotification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends FragmentActivity {


    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "MainActivity";
    private EditText etEmail, etPassword;
    private String email, password;
//    private TextView mStatusTextView;
//    private TextView mDetailTextView;
//    private EditText mEmailField;
//    private EditText mPasswordField;

    private SignInButton mGooglebtn;
    private static final int RC_SIGN_IN = 1;
    private LoginButton login_button;
    private GoogleApiClient mGoogleApiClient;


    private FirebaseAuth mAuth;
// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);






//        if(getIntent() != null) {
//            Intent intent = getIntent();
//            String action = intent.getStringExtra("action1");
//            if (action != null) {
//                switch (action) {
//                    case "Toasting":
//                        Intent intent1 = new Intent(this, Main2Activity.class);
//                        startActivity(intent1);
//                        break;
//                }
//            }
//        }


//
//        mStatusTextView = (TextView) findViewById(R.id.mStatusTextView);
//        mDetailTextView = (TextView) findViewById(R.id.mDetailsTextView);
//        mEmailField = (EditText) findViewById(R.id.mEmailField);
//        mPasswordField = (EditText) findViewById(R.id.mPasswordField);
//
//        // Buttons
//        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
//        findViewById(R.id.email_create_account_button).setOnClickListener(this);
//        findViewById(R.id.sign_out_button).setOnClickListener(this);
//        findViewById(R.id.verify_email_button).setOnClickListener(this);

        mGooglebtn = (SignInButton) findViewById(R.id.mGooglebtn);
        login_button = (LoginButton) findViewById(R.id.login_button);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "You got an Error", Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGooglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        //mAuth.signOut();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(MainActivity.this, Main2Activity.class));

                    Log.d(TAG, "onAuthStateChanged: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");

                }
            }
        };
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

    }



    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:success");


                        // Sign in success, update UI with the signed-in user's information

                        if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



//    public void authentication(View view) {
//
//        email = etEmail.getText().toString();
//        password = etPassword.getText().toString();
//        switch (view.getId()) {
////            case R.id.btnSingUpGoogle:
////                if(email.isEmpty() || password.isEmpty()){
////                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
////                }
////                else {
////                    mAuth.createUserWithEmailAndPassword(email, password)
////                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
////                                @Override
////                                public void onComplete(@NonNull Task<AuthResult> task) {
////                                    if (task.isSuccessful()) {
////                                        // Sign in success, update UI with the signed-in user's information
////                                        Log.d(TAG, "createUserWithEmail:success");
////                                        FirebaseUser user = mAuth.getCurrentUser();
////                                        updateUI(user);
////                                    } else {
////                                        // If sign in fails, display a message to the user.
////                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
////                                        Toast.makeText(MainActivity.this, "Authentication failed.",
////                                                Toast.LENGTH_SHORT).show();
////                                        updateUI(null);
////                                    }
////
////                                    // ...
////                                }
////                            });
////                    break;
////                }
//
//            case R.id.btnSingIn:
//                if(email.isEmpty() || password.isEmpty()){
//                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    mAuth.signInWithEmailAndPassword(email, password)
//                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//
//                                    if (task.isSuccessful()) {
//
//                                        Intent intentSignUP = new Intent(MainActivity.this, Main2Activity.class);
//                                        startActivity(intentSignUP);
//
//                                        Toast.makeText(MainActivity.this, "You are Signed In!", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    // If sign in fails, display a message to the user. If sign in succeeds
//                                    // the auth state listener will be notified and logic to handle the
//                                    // signed in user can be handled in the listener.
//                                    if (!task.isSuccessful()) {
//                                        Log.w(TAG, "signInWithEmail:failed", task.getException());
//                                        Toast.makeText(MainActivity.this, "Not Singed In",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    // ...
//                                }
//                            });
//                    break;
//                }
//
//
//            case R.id.btnSingUp:
//
//                if(email.isEmpty() || password.isEmpty()){
//                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
//                }
//                else {
//
//                    mAuth.createUserWithEmailAndPassword(email, password)
//                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(MainActivity.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();
//                                    }
//                                    // If sign in fails, display a message to the user. If sign in succeeds
//                                    // the auth state listener will be notified and logic to handle the
//                                    // signed in user can be handled in the listener.
//                                    if (!task.isSuccessful()) {
//                                        Toast.makeText(MainActivity.this, "Account Not created",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    // ...
//                                }
//                            });
//                    break;
//                }
//        }
//    }



//    private void updateUI(FirebaseUser user) {
//
//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
//                    user.getEmail(), user.isEmailVerified()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
//            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
//            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
//
//            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
//        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
//            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
//            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
//        }
//
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
