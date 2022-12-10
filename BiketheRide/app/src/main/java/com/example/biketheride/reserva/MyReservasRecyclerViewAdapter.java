package com.example.biketheride.reserva;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.biketheride.R;
import com.example.biketheride.ui.bike.Bike;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Comparator;
import java.util.List;

public class MyReservasRecyclerViewAdapter extends RecyclerView.Adapter<MyReservasRecyclerViewAdapter.ViewHolder> {

    private List<Reserva> dataSet;
    private DatabaseReference mDatabase;



    //private final BicisDisponiblesFragment.OnListFragmentInteractionListener mListener;

    public MyReservasRecyclerViewAdapter(List<Reserva> items) {
        //mListener = listener;
        this.dataSet = items;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_myreservas, parent, false);
        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
dataSet.sort(Comparator.comparing(Reserva::getFecha));

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = dataSet.get(position);
        holder.setOnClickListeners();

        holder.textViewFecha.setText(dataSet.get(position).getFecha());
        holder.textViewLocation.setText(dataSet.get(position).getLocation());
        holder.textViewCity.setText(dataSet.get(position).getCity());

        //holder.imageViewIcon.setImageBitmap(dataSet.get(position).getImageBitmap());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    System.out.println("CardView "+holder.mItem.getId());
                }*/
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
        private final TextView textViewFecha;


        private final ImageView imageViewIcon;
        private final ImageButton imageButtonEliminar;
        private Reserva mItem;
        private Context context;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewFecha = view.findViewById(R.id.textViewFechaReserva);
            textViewCity = view.findViewById(R.id.textViewCityMyReserva);
            imageViewIcon = view.findViewById(R.id.imageViewIconMyReserva);
            textViewLocation = view.findViewById(R.id.textViewLocationMyReserva);
            imageButtonEliminar = view.findViewById(R.id.imageButtonEliminarReserva);
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

            System.out.println(mItem.getId());
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


            AlertDialog.Builder aDial= new AlertDialog.Builder(v.getContext());
            aDial.setTitle("Eliminar");
            aDial.setMessage("¿Deseas eliminar la reserva?");
            aDial.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDatabase.child("reservas").child(mItem.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        }
    }
}


