
package com.salvi.TaskManagER;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.salvi.TaskManagER.Support.DatabaseHelper;

public class ListActivity extends AppCompatActivity {

    private DatabaseHelper dbExecutor;
    private TableLayout tableReunions;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbExecutor = new DatabaseHelper(this);

        tableReunions = findViewById(R.id.tableReunions);
        tvEmpty = findViewById(R.id.tvEmptyReunions);

        findViewById(R.id.btnReturnReunions).setOnClickListener(v -> finish());

        loadReunions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbExecutor != null && tableReunions != null) {
            loadReunions();
        }
    }

    private void loadReunions() {
        // Mantém a primeira linha, que contém o cabeçalho.
        if (tableReunions.getChildCount() > 1) {
            tableReunions.removeViews(
                1,
                tableReunions.getChildCount() - 1
            );
        }

        Cursor cursor = dbExecutor.getAllReunions();
        int count = 0;

        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                    );

                    String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                    );

                    addReunionRow(id, name);
                    count++;
                }
            }
        }

        tvEmpty.setVisibility(
            count == 0 ? View.VISIBLE : View.GONE
        );

        tableReunions.setVisibility(
            count == 0 ? View.GONE : View.VISIBLE
        );
    }

    private void addReunionRow(int id, String name) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(4, 8, 4, 8);

        TextView tvName = createCell(name);
        tvName.setTextColor(Color.BLACK);
        tvName.setOnClickListener(v -> {
            Intent intent = new Intent(
                ListActivity.this,
                RegisterActivity.class
            );

            intent.putExtra("reunion_id", id);
            intent.putExtra("reunion_name", name);

            startActivity(intent);
        });

        row.addView(tvName);

        ImageButton btnDelete = createIconButton(
            android.R.drawable.ic_menu_delete,
            "Excluir reunião"
        );

        row.addView(btnDelete);

        tableReunions.addView(row);

        // Tocar no nome apenas abre a visualização dos requisitos.
        btnDelete.setOnClickListener(v -> confirmDelete(id, name));
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text == null ? "" : text);
        tv.setTextSize(16);
        tv.setPadding(12, 14, 12, 14);
        tv.setMaxWidth(500);
        tv.setBackgroundColor(Color.parseColor("#EEEEEE"));

        tv.setLayoutParams(new TableRow.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        ));

        return tv;
    }

    private ImageButton createIconButton(int iconRes, String description) {
        ImageButton button = new ImageButton(this);
        button.setImageResource(iconRes);
        button.setContentDescription(description);
        button.setBackgroundResource(android.R.drawable.btn_default);
        button.setPadding(12, 12, 12, 12);

        button.setLayoutParams(new TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        return button;
    }

    private void confirmDelete(int id, String name) {
        new AlertDialog.Builder(this)
            .setTitle("Excluir reunião")
            .setMessage(
                "Deseja realmente excluir a reunião \"" +
                    name + "\"?"
            )
            .setPositiveButton("Excluir", (dialog, which) -> {
                int deleted = dbExecutor.deleteReunionById(id);

                if (deleted > 0) {
                    Toast.makeText(
                        this,
                        "Reunião excluída.",
                        Toast.LENGTH_SHORT
                    ).show();

                    loadReunions();
                } else {
                    Toast.makeText(
                        this,
                        "Não foi possível excluir a reunião.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbExecutor != null) {
            dbExecutor.close();
        }
    }
}
