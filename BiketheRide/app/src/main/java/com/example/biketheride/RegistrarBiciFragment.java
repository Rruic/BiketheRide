package com.example.biketheride;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biketheride.bike.Bike;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class RegistrarBiciFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;

    private Double longitude;
    private Double latitude;
    private String city;
    private String country;
    private String description;
    private String street;
    private String id;
    private String image;

    private Uri selectedImage;

    private Button btRegistrar;
    private ImageButton ibtBici;
    private EditText etDescription;
    private ProgressBar progressBar;
    private TextView tvCargaFoto;

    private static final int SELECT_FILE = 1;

    private StorageReference storageRef;
    private FirebaseAuth mauth;
    private DatabaseReference mDatabase;

    public RegistrarBiciFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageRef= FirebaseStorage.getInstance().getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        //verifica permiso de ubicación y lo solicita al usuario cuando sea necesario
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //mostrar el diálogo de permisos del sistema, se llama al método launch() de la instancia de ActivityResultLauncher
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            getLocation();
        }
    }

    //respuesta de permisos
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        getLocation();
                        if (fineLocationGranted != null && fineLocationGranted && coarseLocationGranted != null && coarseLocationGranted) {
                            getLocation();
                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            getLocation();
                            // Only approximate location access granted.
                        } else {
                            Toast.makeText(getContext(), "Se necesita permiso de ubicación para conocer las coordenadas de la bicicleta", Toast.LENGTH_LONG).show();
                            // No location access granted.
                        }
                    }
            );


    //método para obtener latitud y longitud
    @SuppressLint("MissingPermission")
    private void getLocation() {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            //Geocodificación inversa: consiste en convertir una latitud y longitud de coordenadas geográficas en una dirección
                            Geocoder geocoder= new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addresses;

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                //String addresse = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                city = addresses.get(0).getLocality();
                                country = addresses.get(0).getCountryName();
                                street = addresses.get(0).getThoroughfare()+ " "+addresses.get(0).getSubThoroughfare();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_registrar_bici, container, false);

        mauth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        btRegistrar =v.findViewById(R.id.btRegistrarBici);
        ibtBici =v.findViewById(R.id.ibtCargarFoto);
        etDescription =v.findViewById(R.id.etDescripBici);

        progressBar = v.findViewById(R.id.progressBarCargaFoto);
        tvCargaFoto=v.findViewById(R.id.tvCargandoFotoBici);

        ibtBici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Seleccione una imagen"),
                        SELECT_FILE);
            }
        });

        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = String.valueOf(etDescription.getText());

                if (longitude == null || latitude == null) {
                    Toast.makeText(getContext(), "Revisa el estado de ubicación", Toast.LENGTH_LONG).show();
                } else if (description.equals("")) {
                    Toast.makeText(getContext(), "Añade una descripción", Toast.LENGTH_LONG).show();
                } else {
                    cargaImagen();
                    }
            }
        });
        return v;
    }


    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Uri selectedImageUri = null;

        //String filePath = null;
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = data.getData();
                    String selectedPath=selectedImage.getPath();
                    if (requestCode == SELECT_FILE) {

                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getContext().getContentResolver().openInputStream(
                                        selectedImage);

                                //Habilitar botón regitrar
                                btRegistrar.setEnabled(true);
                                Resources res = getResources();
                                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.rounded_corners_bt, null);
                                btRegistrar.setBackground(drawable);
                                // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                                Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                                ibtBici.setImageBitmap(bmp);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }
    }

    public void registrar(){
        String idUser=mauth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Bike bike = new Bike(image,description,city,longitude,latitude,street,country,id,idUser);

        bike.addToDatabase(id);

        Toast.makeText(getContext(), "Bicicleta registrada", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RegistrarBiciFragment()).commit();

    }

    public void cargaImagen(){
        id=mDatabase.child("bikes_list").push().getKey();
        StorageReference reference = storageRef.child(id);

        UploadTask uploadTask= reference.putFile(selectedImage);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                tvCargaFoto.setVisibility(View.VISIBLE);

                progressBar.setProgress((int) ((snapshot.getBytesTransferred()/snapshot.getTotalByteCount())*100));

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                tvCargaFoto.setText(R.string.tvCargaCompletaFotoBici);

                image=taskSnapshot.getMetadata().getReference().toString();//Ubicación en Storage

                registrar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Fallo al cargar la imagen",Toast.LENGTH_LONG).show();
            }
        });

    }
}