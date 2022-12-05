package com.example.biketheride.reserva;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biketheride.R;
import com.example.biketheride.mybikes.MisBicisFragment;
import com.example.biketheride.mybikes.MyBikesRecyclerViewAdapter;
import com.example.biketheride.ui.bike.BicisDisponiblesFragment;
import com.example.biketheride.ui.bike.Bike;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservasFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference mDat;

    public static List<Reserva> misReservas = new ArrayList<Reserva>();
    public static List<Bike> bikesReserva = new ArrayList<Bike>();

    private StorageReference mStorageReference;

    private FirebaseAuth mauth;

    public ReservasFragment() {
        // Required empty public constructor
    }


    public static ReservasFragment newInstance(String param1, String param2) {
        ReservasFragment fragment = new ReservasFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_list_myreservas, container, false);

        recyclerView = view.findViewById(R.id.listMyReservas);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);


        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mauth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDat = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


        misreservasFirebase();

        mAdapter = new MyReservasRecyclerViewAdapter(misReservas);

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void misreservasFirebase() {

        System.out.println("Reservas: "+mDatabase.child("reservas").orderByChild("idUser").equalTo(mauth.getCurrentUser().getUid()));
        mDatabase.child("reservas").orderByChild("idUser").equalTo(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                misReservas.clear();
                bikesReserva.clear();
                TextView tvNoReservas = view.findViewById(R.id.tvNoReserva);


                if (snapshot.exists()) {
                    misReservas.clear();
                    bikesReserva.clear();


                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {

                        String fecha = productSnapshot.child("fecha").getValue().toString();
                        String id = productSnapshot.child("id").getValue().toString();
                        String idUser = productSnapshot.child("idUser").getValue().toString();
                        String idBike=productSnapshot.child("idBike").getValue().toString();
                        String city=productSnapshot.child("city").getValue().toString();
                        String location=productSnapshot.child("location").getValue().toString();

                        Reserva reserva = new Reserva(id,idUser,idBike,fecha,city,location);
                        misReservas.add(reserva);
                        //downloadPhoto(bike);
                    }
                }
                if (mAdapter.getItemCount() == 0) {
                    tvNoReservas.setVisibility(View.VISIBLE);

                }else{
                    tvNoReservas.setVisibility(View.INVISIBLE);
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
/*
    private void downloadPhoto(Bike c) {

        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(c.getImage());
        System.out.println("Imagen download: " + mStorageReference);

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final File localFile = File.createTempFile("PNG_" + timeStamp, ".png");
            mStorageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Insert the downloaded image in its right position at the ArrayList

                    String url = "gs://" + taskSnapshot.getStorage().getBucket() + "/" + taskSnapshot.getStorage().getName();
                    ;
                    Log.d(TAG, "Loaded " + url);
                    for (Bike c : misBicis) {
                        if (c.getImage().equals(url)) {
                            c.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                            mAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded pic " + c.getImage() + ";" + url + localFile.getAbsolutePath());
                        }
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/




    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Bike item);
    }
}