package com.salvi.prova1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        findViewById((R.id.btnRegister)).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        findViewById(R.id.btnListProducts).setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class))
        );
    }
}