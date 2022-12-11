package com.example.biketheride.bike;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;

import com.example.biketheride.DatePickerFragment;
import com.example.biketheride.MainActivity;
import com.example.biketheride.R;
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
 * Use the {@link BicisDisponiblesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BicisDisponiblesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnListFragmentInteractionListener mListener;
    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mauth;
    List<String> resrvFecha=new ArrayList<String>();

    public static List<Bike> bicis =new ArrayList<Bike>();
    private StorageReference mStorageReference;
    Button btFecha;

    public BicisDisponiblesFragment() {
        // Required empty public constructor
    }

    public static BicisDisponiblesFragment newInstance(String param1, String param2) {
        BicisDisponiblesFragment fragment = new BicisDisponiblesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_list, container, false);

        recyclerView = view.findViewById(R.id.listBike);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mauth=FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        mAdapter = new MyItemRecyclerViewAdapter(bicis, mListener);

        recyclerView.setAdapter(mAdapter);

        btFecha=view.findViewById(R.id.btFecha);
        if (MainActivity.getFecha()!=null){

            btFecha.setText(MainActivity.getFecha());

            reservaFechaSelecc();
        }
        btFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });


        return view;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque enero es cero
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                btFecha.setText(selectedDate);
                MainActivity.setFecha(selectedDate);
                reservaFechaSelecc();

            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void reservaFechaSelecc(){
        mDatabase.child("reservas").orderByChild("fecha").equalTo(MainActivity.getFecha()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resrvFecha.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    resrvFecha.add(productSnapshot.child("idBike").getValue().toString());
                }
                bicisFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void bicisFirebase() {
        mDatabase.child("bikes_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bicis.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    if (!(productSnapshot.child("idUser").getValue().equals(mauth.getCurrentUser().getUid()))&&!(resrvFecha.contains(productSnapshot.child("id").getValue()))){
                        Bike bike = productSnapshot.getValue(Bike.class);
                        bicis.add(bike);

                        downloadPhoto(bike);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void downloadPhoto(Bike c) {

        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(c.getImage());

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
                    for (Bike c : bicis) {
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

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Bike item);
    }
}