package net.augustana.maegan.unabridgedscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {
    TextView authView;
    DatabaseReference ref;
    private FirebaseAuth auth;
    Button signoutButton;
    SignInButton signInButton;

    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("users");

        signInButton = findViewById(R.id.sign_in_button);
        authView = findViewById(R.id.authTextView);
        signoutButton = findViewById(R.id.signoutButton);

        //Google authentication
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        auth = FirebaseAuth.getInstance();
        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI changes for if the user is signed in or not
        FirebaseUser account = auth.getCurrentUser();
        if(account != null) {
            authView.setText("Signed in as " + account.getDisplayName());
            signInButton.setEnabled(false);
            signoutButton.setEnabled(true);
        } else {
            authView.setText("Not signed in");
            signInButton.setEnabled(true);
            signoutButton.setEnabled(false);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_CODE);
            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                googleSignInClient.signOut();
                authView.setText("Not signed in");
                signInButton.setEnabled(true);
                signoutButton.setEnabled(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * See if the sign-in attempt worked
     *
     * @param completedTask result of sign-in attempt
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w("Error: ", "signInResult:failed code:" + e.getStatusCode());
            authView.setText("Not signed in");
        }
    }

    /**
     * Attempt to connect to Firebase Authentication with a Google account
     * Based largely on code in this article:
     * https://medium.com/@ihimanshurawat/google-login-for-android-with-firebase-af9743c26d3e
     *
     * @param acct the account the user signed in with
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            authView.setText("Signed in as " + user.getDisplayName());
                            signInButton.setEnabled(false);
                            signoutButton.setEnabled(true);
                        } else {
                            Log.w("Auth: ", "signInWithCredential:failure", task.getException());
                            authView.setText("Not signed in");
                            signInButton.setEnabled(true);
                            signoutButton.setEnabled(false);
                        }
                    }
                });
    }
}
