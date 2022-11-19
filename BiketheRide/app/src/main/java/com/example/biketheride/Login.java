package com.example.biketheride;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.biketheride.databinding.ActivityLoginBinding;
import com.example.biketheride.databinding.ActivityMainBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);
    }
}