package com.salvi.atividadetres;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AboutActivity extends AppCompatActivity {
    Button btnReturn;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_about);

        btnReturn = findViewById(R.id.btnReturn3);

        btnReturn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

}