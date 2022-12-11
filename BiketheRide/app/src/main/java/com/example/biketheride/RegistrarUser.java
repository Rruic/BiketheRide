package com.example.biketheride;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biketheride.databinding.ActivityRegistrarBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrarUser extends AppCompatActivity {

    private ActivityRegistrarBinding binding;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        binding = ActivityRegistrarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailUser = binding.etEmail.getText().toString().trim();
                String passUser = binding.etPassword.getText().toString().trim();
                String nombreUser = binding.etNombre.getText().toString().trim();

                if (emailUser.isEmpty() || passUser.isEmpty() || nombreUser.isEmpty()) {
                    Toast.makeText(RegistrarUser.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                } else {
                    registrarUser(nombreUser, emailUser, passUser);
                }
            }
        });
    }

    private void registrarUser(String nombreUser, String emailUser, String passUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    ModeloUser user=new ModeloUser(nombreUser,mAuth.getCurrentUser().getEmail(),mAuth.getCurrentUser().getUid(),"");
                    user.addToDatabase(mAuth.getCurrentUser().getUid());
                    Toast.makeText(RegistrarUser.this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarUser.this);

                    builder.setTitle("Error de registro");
                    builder.setMessage("Comprueba que el correo electrónico sea válido y la contraseña tenga como mínimo 6 caracteres");
                    builder.setPositiveButton("Aceptar", null);

                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);// para hacerlo modal
                    dialog.show();
                }
            }
        });
    }


    public void volver(View v) {
        finish();
    }
}