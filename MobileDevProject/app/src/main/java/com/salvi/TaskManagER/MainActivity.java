
package com.salvi.TaskManagER;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnRegister;
    private MaterialButton btnListMeetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        configureListeners();
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
