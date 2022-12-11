package com.example.biketheride.chat;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biketheride.ModeloUser;
import com.example.biketheride.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private List<ModeloChatList> chatListList;
    private List<ModeloUser> usersList;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private AdapterChatList adapterChatList;
    private List<ModeloChat> chatList;

    public ChatListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.chatlistrecycle);
        chatListList = new ArrayList<>();
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("chatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModeloChatList modeloChatList = ds.getValue(ModeloChatList.class);
                    if (!modeloChatList.getId().equals(firebaseUser.getUid())) {
                        chatListList.add(modeloChatList);
                    }

                }
                leerChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;

    }

    // cargando chat del usuario
    private void leerChats() {
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModeloUser user = dataSnapshot1.getValue(ModeloUser.class);
                    for (ModeloChatList chatList : chatListList) {
                        if (user.getUid() != null && user.getUid().equals(chatList.getId())) {
                            usersList.add(user);
                            break;
                        }
                    }
                    adapterChatList = new AdapterChatList(getActivity(), usersList);
                    recyclerView.setAdapter(adapterChatList);

                    // obtengo el ultimo mensaje del usuario
                    for (int i = 0; i < usersList.size(); i++) {
                        ultimoMsg(usersList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ultimoMsg(final String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ultMsg = "";
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModeloChat chat = dataSnapshot1.getValue(ModeloChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }

                    ultMsg = chat.getMessage();
                }
                adapterChatList.setultimoMsgMap(uid, ultMsg);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
