package com.example.biketheride;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.biketheride.databinding.ActivityMainBinding;
import com.example.biketheride.mybikes.MisBicisFragment;
import com.example.biketheride.ui.bike.BicisDisponiblesFragment;
import com.example.biketheride.ui.bike.Bike;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements BicisDisponiblesFragment.OnListFragmentInteractionListener, View.OnClickListener {

    private FirebaseAuth mauth;
    private DatabaseReference mDatabase;
    String title;


    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);

        mauth=FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Bundle bundle=getIntent().getExtras();
        mDatabase.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Establece el nombre de usuario en la cabecera en tiempo real
                TextView tvPerfil= (TextView) binding.navView.getHeaderView(0).findViewById(R.id.tvPerfil);
                tvPerfil.setText(snapshot.child(mauth.getCurrentUser().getUid()).child("name").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setToolbar();
        setDefaultFragment();

        binding.navView.getHeaderView(0).findViewById(R.id.btEditPerfil).setOnClickListener(MainActivity.this);

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                boolean fragmentTransaction = false;

                switch (item.getItemId()) {
                    case R.id.bicisDisponibles:
                        fragment = new BicisDisponiblesFragment();
                        fragmentTransaction = true;
                        break;
                    case R.id.mapa:
                        fragment = new MapsFragment();
                        fragmentTransaction = true;
                        break;
                    case R.id.reservas:
                        fragment = new ReservasFragment();
                        fragmentTransaction = true;
                        break;
                    case R.id.mensajes:
                        fragment = new MensajesFragment();
                        fragmentTransaction = true;
                        break;
                    case R.id.misBicis:
                        fragment = new MisBicisFragment();
                        fragmentTransaction = true;
                        break;
                    case R.id.registrarBici:
                        fragment = new RegistrarBiciFragment();
                        fragmentTransaction = true;
                        break;
                    case R.id.logout:
                        SharedPreferences prefs=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=prefs.edit();
                        editor.clear();
                        editor.apply();
                        FirebaseAuth.getInstance().signOut();

                        finish();
                        break;
                }


               // System.out.println("Fragm "+(getSupportFragmentManager().findFragmentById(R.id.content_frame).getTag()==fragment.getTag()));
                if (fragmentTransaction) {

                    //if (fragment==getSupportFragmentManager().findFragmentById(R.id.content_frame)){}

                    String tag=item.getTitle().toString();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment,tag).addToBackStack(null).commit();
                    //item.setChecked(true);
                    getSupportActionBar().setTitle(item.getTitle());
                    binding.drawerLayout.closeDrawers();
                }

                return true;
            }
        });


        SharedPreferences prefs=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("email",bundle.getString("email"));
        editor.apply();


    }

    //Carga fragment por defecto
    private void setDefaultFragment() {
        MenuItem item = binding.navView.getMenu().getItem(0);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new BicisDisponiblesFragment(),item.getTitle().toString()).commit();

        //item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle());
    }

    private void setToolbar(){
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onListFragmentInteraction(Bike item) {

    }

    @Override
    public void onClick(View view) {

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        getSupportActionBar().hide();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EditPerfilFragment()).addToBackStack(null).commit();
        getSupportActionBar().setTitle("Editar perfil");
        binding.drawerLayout.closeDrawers();
    }
    public void showDrawer(){
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().show();

    }

    public FirebaseAuth getMauth() {
        return mauth;
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);


        getSupportActionBar().setTitle(f.getTag());


    }
}