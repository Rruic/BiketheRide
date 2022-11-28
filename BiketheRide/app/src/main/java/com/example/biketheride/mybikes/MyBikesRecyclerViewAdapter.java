package com.example.biketheride.mybikes;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.biketheride.R;
import com.example.biketheride.ui.bike.BicisDisponiblesFragment;
import com.example.biketheride.ui.bike.Bike;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyBikesRecyclerViewAdapter extends RecyclerView.Adapter<MyBikesRecyclerViewAdapter.ViewHolder> {

    private List<Bike> dataSet;


    private final BicisDisponiblesFragment.OnListFragmentInteractionListener mListener;

    public MyBikesRecyclerViewAdapter(List<Bike> items, BicisDisponiblesFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
        this.dataSet = items;
    }

    @Override
    public MyBikesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_mybikes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyBikesRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = dataSet.get(position);
        holder.setOnClickListeners();

        holder.textViewCity.setText(dataSet.get(position).getCity());
        holder.textViewLocation.setText(dataSet.get(position).getLocation());
        holder.imageViewIcon.setImageBitmap(dataSet.get(position).getImageBitmap());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    System.out.println("CardView "+holder.mItem.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View mView;
        private final TextView textViewCity;
        private final TextView textViewLocation;

        private final ImageView imageViewIcon;
        private final ImageButton imageButtonEliminar;
        private Bike mItem;
        private Context context;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewCity = view.findViewById(R.id.textViewCityMyBike);
            imageViewIcon = view.findViewById(R.id.imageViewIconMyBike);
            textViewLocation = view.findViewById(R.id.textViewLocationMyBike);
            imageButtonEliminar = view.findViewById(R.id.imageButtonEliminarBici);
            context = view.getContext();

        }

        void setOnClickListeners() {
            imageButtonEliminar.setOnClickListener(this);

        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }


        @Override
        public void onClick(View v) {
            System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid());

            System.out.println(mItem.getIdUser());
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


            AlertDialog.Builder aDial= new AlertDialog.Builder(v.getContext());
            aDial.setTitle("Eliminar");
            aDial.setMessage("¿Deseas eliminar la bicicleta?");
            aDial.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDatabase.child("bikes_list").child(mItem.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                             snapshot.getRef().removeValue();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });                }
            });
            aDial.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.out.println("No");
                }
            });
            aDial.create().show();


            Toast.makeText(v.getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
        }
    }
}


