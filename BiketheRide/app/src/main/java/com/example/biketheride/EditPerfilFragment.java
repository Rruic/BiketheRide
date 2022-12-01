package com.example.biketheride;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.biketheride.databinding.FragmentEditPerfilBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditPerfilFragment extends Fragment implements View.OnClickListener {

    private FragmentEditPerfilBinding binding;


    private FirebaseAuth mauth;
    private DatabaseReference mDatabase;

    public EditPerfilFragment() {
    }

    public static EditPerfilFragment newInstance(String param1, String param2) {
        EditPerfilFragment fragment = new EditPerfilFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mauth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        obtenerDatos();
        binding = FragmentEditPerfilBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        // mainActivity.getMauth();
        //   binding.etEditName.setHint(nombre);
        //    binding.etEditEmail.setHint(email);

        obtenerDatos();


        binding.btVolverPerfil.setOnClickListener(this);
        binding.ibtSaveName.setOnClickListener(this);
        binding.ibtSaveEmail.setOnClickListener(this);
        binding.ibtSavePass.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtSaveName:
                actualizarNombre();
                break;
            case R.id.ibtSaveEmail:
                actualizarEmail();
                break;
            case R.id.ibtSavePass:
                actualizarPass();
                break;
            case R.id.btVolverPerfil:
                getActivity().onBackPressed();

        }
    }

    public void obtenerDatos() {

        mDatabase.child("user").child(mauth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tvnombre = snapshot.child("name").getValue().toString();
                String tvemail = snapshot.child("email").getValue().toString();
                binding.etEditName.setHint(tvnombre);
                binding.etEditEmail.setHint(tvemail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void actualizarNombre() {
        String inputName = binding.etEditName.getText().toString();
        if (inputName.length() > 0) {

            AlertDialog.Builder aDial = new AlertDialog.Builder(getContext());
            aDial.setTitle("Actualizar");
            aDial.setMessage("¿Deseas actualizar el nombre?");
            aDial.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //String nombre = inputName;
                    mDatabase.child("user").child(mauth.getCurrentUser().getUid()).child("name").setValue(inputName).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
                            binding.etEditName.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String mensaje = "Error al actualizar el nombre.";
                            alertError(mensaje);
                        }
                    });
                }
            });
            aDial.setNegativeButton("Cancelar", null);
            aDial.create().show();

        }

    }

    private void actualizarEmail() {
        String inputEmail = binding.etEditEmail.getText().toString();
        String mensaje;

        if (inputEmail.length() > 0) {
            //Comprobar email valido
            Pattern pat = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher mat = pat.matcher(inputEmail);
            if (mat.find()) {
                AlertDialog.Builder aDial = new AlertDialog.Builder(getContext());
                aDial.setTitle("Actualizar");
                aDial.setMessage("¿Deseas actualizar el email?");
                aDial.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //String email = inputEmail;
                        firabaseEmail(inputEmail);
                    }
                });
                aDial.setNegativeButton("Cancelar", null);
                aDial.create().show();

            } else {
                mensaje = "El email no es válido.";
                alertError(mensaje);
            }
        }
    }

    private void firabaseEmail(String email) {
        mauth.getCurrentUser().updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mDatabase.child("user").child(mauth.getCurrentUser().getUid()).child("email").setValue(email);
                Toast.makeText(getContext(), "Email actualizado correctamente", Toast.LENGTH_SHORT).show();
                binding.etEditEmail.setText("");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String mensaje = "Error al actualizar el email. Reinicia la sesión o prueba con otra dirección de correo electrónico.";

                alertError(mensaje);
            }
        });
    }

    private void actualizarPass() {

        String inputPass = binding.etEditPass.getText().toString();
        String mensaje;

        if (inputPass.length() >= 6) {

            AlertDialog.Builder aDial = new AlertDialog.Builder(getContext());
            aDial.setTitle("Actualizar");
            aDial.setMessage("¿Deseas actualizar la contraseña?");
            aDial.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //String pass = inputPass;
                    firebasePass(inputPass);
                    binding.etEditPass.setText("");

                }
            });
            aDial.setNegativeButton("Cancelar", null);
            aDial.create().show();
        } else if (inputPass.length() > 0 && inputPass.length() < 6) {
            mensaje = "La contraseña debe tener mas de 6 caracteres.";
            alertError(mensaje);
        }
    }


    private void firebasePass(String pass) {

        mauth.getCurrentUser().updatePassword(pass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


                Toast.makeText(getContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String mensaje = "Error al actualizar la contraseña. Reinicia sesión.";
                alertError(mensaje);
            }
        });
    }


    private void alertError(String mensaje) {
        AlertDialog.Builder aDial = new AlertDialog.Builder(getContext());
        aDial.setTitle("Error");
        aDial.setMessage(mensaje);
        aDial.setPositiveButton("Aceptar", null);
        aDial.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showDrawer();
    }


}