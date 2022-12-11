package com.example.biketheride;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biketheride.databinding.ActivityLoginBinding;
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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;

    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        session();
        binding.btSignInGoogle.setOnClickListener(this);
    }

    public void registrar(View v) {
        Intent i = new Intent(this, RegistrarUser.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void login(View v) {

        if (!binding.etEmail.getText().toString().isEmpty() && !binding.etPassword.getText().toString().isEmpty()) {
            mAuth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
// Inicio de sesion correcto
                        Log.d(TAG, "signInWithCredential:success");

                        irMain(task.getResult().getUser().getEmail());

                    } else {
// Fallo de inicio de sesión
                        mostrarAlert();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }

                }
            });

        } else {
            Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// Resultado devuelto al iniciar el Intent desde GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
// Google Sign In fue exitoso, autenticar con Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
// Falló el inicio de sesión de Google
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    //Autenticacion con firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Inicio de sesión correcto
                            Log.d(TAG, "signInWithCredential:success");
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

                            mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()){

                                        ModeloUser user=new ModeloUser(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(),mAuth.getCurrentUser().getUid(),"");
                                        user.addToDatabase(mAuth.getCurrentUser().getUid());

                                    }
                                    irMain(task.getResult().getUser().getEmail());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
// If sign in fails, display a message to the user.
                            mostrarAlert();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void session() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);
        if (email != null) {
            irMain(email);

        }
    }

    public void irMain(String email) {

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("email", email);
        startActivity(i);
        binding.etEmail.setText("");
        binding.etPassword.setText("");


    }

    private void mostrarAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error");
        builder.setMessage("Error al autenticar el usuario");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);// para hacerlo modal
        dialog.show();

    }
}