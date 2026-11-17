package com.salvi.TaskManagER;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.salvi.TaskManagER.Support.Location;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnRegister;
    private MaterialButton btnListMeetings;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                if (!Location.hasPermission(this)) {
                    Toast.makeText(
                        this,
                        "A localização será necessária ao cadastrar requisitos.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        );

        setContentView(R.layout.activity_main);
        initializeComponents();
        configureListeners();

        if (!Location.hasPermission(this)) {
            locationPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void initializeComponents() {
        btnRegister = findViewById(R.id.btnRegister);
        btnListMeetings = findViewById(R.id.btnListProducts);
    }

    private void configureListeners() {
        // cadastro de reunião
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                MainActivity.this,
                RegisterActivity.class
            );
            startActivity(intent);
        });

        // lista de reuniões
        btnListMeetings.setOnClickListener(v -> {
            Intent intent = new Intent(
                MainActivity.this,
                ListActivity.class
            );
            startActivity(intent);
        });
    }
}
