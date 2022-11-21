package com.example.biketheride;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.biketheride.databinding.ActivityMainBinding;
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

        SharedPreferences prefs=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("email",bundle.getString("email"));
        editor.apply();
    }

    public void clic(View v){
        SharedPreferences prefs=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();

        finish();
    }
}