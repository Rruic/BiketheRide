package com.example.biketheride;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.biketheride.databinding.ActivityLoginBinding;
import com.example.biketheride.databinding.ActivityRegistrarBinding;

public class Registrar extends AppCompatActivity {

    private ActivityRegistrarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        binding = ActivityRegistrarBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);
    }
}