package com.example.biketheride;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.biketheride.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);

        Bundle bundle=getIntent().getExtras();

        setToolbar();
        setDefaultFragment();

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
                   /* case R.id.mapa:
                        fragment = new ();
                        fragmentTransaction = true;
                        break;*/
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

                if (fragmentTransaction) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                    item.setChecked(true);
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
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new BicisDisponiblesFragment()).commit();

        MenuItem item = binding.navView.getMenu().getItem(0);
        item.setChecked(true);
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

}