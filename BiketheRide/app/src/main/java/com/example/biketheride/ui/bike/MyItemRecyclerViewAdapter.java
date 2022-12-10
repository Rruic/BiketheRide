package com.example.biketheride.ui.bike;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biketheride.MainActivity;
import com.example.biketheride.R;
import com.example.biketheride.chat.ChatActivity;
import com.example.biketheride.reserva.Reserva;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<Bike> dataSet;

    private DatabaseReference mDatabase;
    //Context context;



    private final BicisDisponiblesFragment.OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<Bike> items, BicisDisponiblesFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
        this.dataSet = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        //context= context.getApplicationContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = dataSet.get(position);
        holder.setOnClickListeners();
        System.out.println("User:"+dataSet.get(position).getIdUser());
        mDatabase.child("user").child(dataSet.get(position).getIdUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.textViewOwner.setText(snapshot.child("name").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //holder.textViewOwner.setText(dataSet.get(position).getOwner());

        holder.textViewCity.setText(dataSet.get(position).getCity());
        holder.textViewLocation.setText(dataSet.get(position).getLocation());
        holder.textViewDescription.setText(dataSet.get(position).getDescription());
        holder.imageViewIcon.setImageBitmap(dataSet.get(position).getImageBitmap());
        holder.imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);

                // putting uid of user in extras
                String uidUserB=dataSet.get(position).getIdUser();
                intent.putExtra("uid",uidUserB);
                view.getContext().startActivity(intent);
                System.out.println("Chat");
            }
        });

        /*holder.imageButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String idUser= FirebaseAuth.getInstance().getCurrentUser().getUid();

                String id=mDatabase.child("reserva").push().getKey();

                System.out.println(dataSet.get(position).getIdUser());
                String idBike=dataSet.get(position).getId();
                String city=dataSet.get(position).getCity();
                String location=dataSet.get(position).getLocation();


                Reserva reserva = new Reserva(id,idUser,idBike, MainActivity.getFecha(),city,location);

                //reserva.addToDatabase(id);

                addToDatabase(reserva,id,view);


            }

        });*/

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    System.out.println("Aquiiiiiiiiisdf-------");
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void addToDatabase(Reserva reserva, String id,Context context){
        DatabaseReference database= FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        String key= database.child("reservas").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        database.child("reservas/"+id).setValue(reserva).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("En reserva OK");
                Toast.makeText(context, "Reserva realizada", Toast.LENGTH_LONG).show();


            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {




            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View mView;
        private final TextView textViewOwner;
        private final TextView textViewCity;
        private final TextView textViewLocation;
        private final TextView textViewDescription;
        private final ImageView imageViewChat;

        private final ImageView imageViewIcon;
        private final ImageButton imageButtonEmail;
        private Bike mItem;
        private Context context;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewOwner = view.findViewById(R.id.textViewOwner);
            textViewCity = view.findViewById(R.id.textViewCity);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
            textViewLocation = view.findViewById(R.id.textViewLocation);
            textViewDescription = view.findViewById(R.id.textViewDescription);
            imageButtonEmail = view.findViewById(R.id.imageButtonEmailCard);
            imageViewChat=view.findViewById(R.id.ivChat);
            context = view.getContext();

        }

        void setOnClickListeners() {
            imageButtonEmail.setOnClickListener(this);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewOwner.getText() + "'";
        }


        @Override
        public void onClick(View v) {

            String idUser= FirebaseAuth.getInstance().getCurrentUser().getUid();

            String id=mDatabase.child("reserva").push().getKey();

            //System.out.println(dataSet.get(position).getIdUser());
            String idBike=mItem.getId();
            String city=mItem.getCity();
            String location=mItem.getLocation();


            Reserva reserva = new Reserva(id,idUser,idBike, MainActivity.getFecha(),city,location);

            //reserva.addToDatabase(id);

            addToDatabase(reserva,id,context);


            Toast.makeText(v.getContext(), "Reserva realizada", Toast.LENGTH_LONG).show();
        }
    }
}


