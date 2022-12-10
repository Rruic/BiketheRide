package com.example.biketheride.mybikes;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biketheride.R;
import com.example.biketheride.bike.BicisDisponiblesFragment;
import com.example.biketheride.bike.Bike;
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
 * Use the {@link MisBicisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MisBicisFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BicisDisponiblesFragment.OnListFragmentInteractionListener mListener;
    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference mDatabase;
    public static List<Bike> misBicis = new ArrayList<Bike>();
    private StorageReference mStorageReference;

    private FirebaseAuth mauth;

    public MisBicisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MisBicisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MisBicisFragment newInstance(String param1, String param2) {
        MisBicisFragment fragment = new MisBicisFragment();
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
        view = inflater.inflate(R.layout.fragment_item_list_mybikes, container, false);

        recyclerView = view.findViewById(R.id.listMyBike);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);


        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mauth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


        misbicisFirebase();

        mAdapter = new MyBikesRecyclerViewAdapter(misBicis, mListener);

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void misbicisFirebase() {

        mDatabase.child("bikes_list").orderByChild("idUser").equalTo(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                misBicis.clear();
                TextView tvNoBici = view.findViewById(R.id.tvNoMyBici);


                if (snapshot.exists()) {
                    misBicis.clear();

                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        /*Bike bike = productSnapshot.getValue(Bike.class);
                        bicis.add(bike);
                        downloadPhoto(bike);*/
                        String city = productSnapshot.child("city").getValue().toString();
                        String location = productSnapshot.child("location").getValue().toString();
                        String image = productSnapshot.child("image").getValue().toString();

                        String idBike=productSnapshot.child("id").getValue().toString();

                        Bike bike = new Bike(city, location, image,idBike);
                        misBicis.add(bike);
                        downloadPhoto(bike);

                    }
                }
                if (mAdapter.getItemCount() == 0) {
                    tvNoBici.setVisibility(View.VISIBLE);

                }else{
                    tvNoBici.setVisibility(View.INVISIBLE);
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

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BicisDisponiblesFragment.OnListFragmentInteractionListener) {
            mListener = (BicisDisponiblesFragment.OnListFragmentInteractionListener) context;
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