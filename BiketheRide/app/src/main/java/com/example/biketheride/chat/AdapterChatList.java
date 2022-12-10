package com.example.biketheride.chat;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biketheride.ModeloUser;
import com.example.biketheride.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.Myholder> {

    private Context context;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private List<ModeloUser> usersList;
    private HashMap<String, String> ultimoMsgMap;

    public AdapterChatList(Context context, List<ModeloUser> users) {
        this.context = context;
        this.usersList = users;
        ultimoMsgMap = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, final int position) {

        final String uidUserB = usersList.get(position).getUid();
        String username = usersList.get(position).getName();
        String ultMsg = ultimoMsgMap.get(uidUserB);
        holder.name.setText(username);
        holder.ultimoMsg.setText(ultMsg);

/*
        // si no hay ultimo mensaje ocultar
        if (ultMsg == null || ultMsg.equals("")) {
            holder.ultimoMsg.setVisibility(View.GONE);
        } else {
            holder.ultimoMsg.setVisibility(View.VISIBLE);
            holder.ultimoMsg.setText(ultMsg);
        }*/


        // Ir a ChatActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);

                // id de usuario de la bici
                intent.putExtra("uid", uidUserB);
                context.startActivity(intent);
            }
        });

    }

    // setting last message sent by users.
    public void setultimoMsgMap(String userId, String ultimoMensaje) {
        ultimoMsgMap.put(userId, ultimoMensaje);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class Myholder extends RecyclerView.ViewHolder {
        TextView name, ultimoMsg;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameonline);
            ultimoMsg = itemView.findViewById(R.id.ultimoMsg);

        }
    }
}
