package com.example.biketheride.reserva;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


import com.example.biketheride.MainActivity;
import com.example.biketheride.R;
import com.example.biketheride.chat.ChatActivity;
import com.example.biketheride.ui.bike.Bike;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MyReservasRecyclerViewAdapter extends RecyclerView.Adapter<MyReservasRecyclerViewAdapter.ViewHolder> {

    private List<Reserva> dataSet;
    private DatabaseReference mDatabase;
    private String idUserBike;
    private String descriptionBike;


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

        mDatabase.child("bikes_list").child(dataSet.get(position).getIdBike()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               idUserBike=snapshot.child("idUser").getValue().toString();
               descriptionBike=snapshot.child("description").getValue().toString();

                mDatabase.child("user").child(idUserBike).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.textViewOwner.setText(snapshot.child("name").getValue().toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //holder.imageViewIcon.setImageBitmap(dataSet.get(position).getImageBitmap());

        holder.imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);

                //String uidUserB=dataSet.get(position).getIdUser();
                intent.putExtra("uid",idUserBike);
                view.getContext().startActivity(intent);
                System.out.println("Chat");
            }
        });
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
        private final TextView textViewOwner;

        private final ImageView imageViewChat;
        private final ImageButton imageButtonEliminar;
        private Reserva mItem;
        private Context context;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewFecha = view.findViewById(R.id.textViewFechaReserva);
            textViewCity = view.findViewById(R.id.textViewCityMyReserva);
            textViewLocation = view.findViewById(R.id.textViewLocationMyReserva);
            imageButtonEliminar = view.findViewById(R.id.imageButtonEliminarReserva);
            imageViewChat=view.findViewById(R.id.ivChatReserva);
            textViewOwner = view.findViewById(R.id.textViewOwnerReserva);

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
                            String msg="Disculpe, pero ya no deseo reservar su bicicleta con descripción "+descriptionBike+", en "+mItem.getCity()+", "+mItem.getLocation()+", para la fecha "+ MainActivity.getFecha();

                            sendMsg(msg);
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

        private void sendMsg(final String message) {

            String myId=FirebaseAuth.getInstance().getCurrentUser().getUid();

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


