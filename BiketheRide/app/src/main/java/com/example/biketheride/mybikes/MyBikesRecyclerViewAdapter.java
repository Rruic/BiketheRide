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
import com.example.biketheride.bike.BicisDisponiblesFragment;
import com.example.biketheride.bike.Bike;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class MyBikesRecyclerViewAdapter extends RecyclerView.Adapter<MyBikesRecyclerViewAdapter.ViewHolder> {

    private List<Bike> dataSet;


    private final BicisDisponiblesFragment.OnListFragmentInteractionListener mListener;

    public MyBikesRecyclerViewAdapter(List<Bike> items, BicisDisponiblesFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
        this.dataSet = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_mybikes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = dataSet.get(position);
        holder.setOnClickListeners();

        holder.textViewCity.setText(dataSet.get(position).getCity());
        holder.textViewLocation.setText(dataSet.get(position).getLocation());
        holder.imageViewIcon.setImageBitmap(dataSet.get(position).getImageBitmap());

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


            AlertDialog.Builder aDial = new AlertDialog.Builder(v.getContext());
            aDial.setTitle("Eliminar");
            aDial.setMessage("¿Deseas eliminar la bicicleta? También se eliminaran las reservas asociadas");
            aDial.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //mDatabase.child("reservas")
                    mDatabase.child("reservas").orderByChild("idBike").equalTo(mItem.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            System.out.println(!snapshot.hasChildren());

                            for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                String idUserReserva = productSnapshot.child("idUser").getValue().toString();
                                System.out.println("Reserva " + idUserReserva);

                                productSnapshot.getRef().removeValue();

                                mDatabase.child("bikes_list").child(mItem.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String description = snapshot.child("description").getValue().toString();
                                        snapshot.getRef().removeValue();
                                        System.out.println("sdfsdfdf "+mItem.getImage());
                                        eliminarImagenStorage(mItem.getId());

                                        String msg = "Disculpe pero la bicicleta con descripción " + description + ", en " + mItem.getCity() + ", " + mItem.getLocation() + "ya no esta en alquiler.";
                                        sendMsg(msg, idUserReserva);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            }
                            if (!snapshot.hasChildren()){
                                mDatabase.child("bikes_list").child(mItem.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
                                        eliminarImagenStorage(mItem.getId());
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
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

        public void eliminarImagenStorage(String image) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            //StorageReference imagesRef = storageRef.child("images")

            // Create a reference to the file to delete
            StorageReference desertRef = storageRef.child(image);

            // Delete the file
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }

        private void sendMsg(final String message, String idUserBike) {

            String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //String adminId="KpAlrOkLa0Oa4wlJEKPClSzxk2u2";
            //String idUserBike=mItem.getIdUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
            String timestamp = String.valueOf(System.currentTimeMillis());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", myId);
            hashMap.put("receiver", idUserBike);
            hashMap.put("message", message);
            hashMap.put("timestamp", timestamp);
            databaseReference.child("chats").push().setValue(hashMap);
            final DatabaseReference ref1 = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("chatList").child(idUserBike).child(myId);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        ref1.child("id").setValue(myId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final DatabaseReference ref2 = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("chatList").child(myId).child(idUserBike);
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        ref2.child("id").setValue(idUserBike);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}


