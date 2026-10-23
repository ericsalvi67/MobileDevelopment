package com.salvi.prova1;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProductListActivity extends AppCompatActivity {

    DatabaseHelper dbExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getProducts();

        findViewById(R.id.btnReturn2).setOnClickListener(v -> finish());
    }

    private void getProducts() {

        TableLayout tableProducts = findViewById(R.id.tableProducts);
        dbExecutor = new DatabaseHelper(this);
        Cursor cursor = dbExecutor.getAllProducts();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

            TableRow row = new TableRow(this);
            row.setPadding(4, 4, 4, 4);

            TextView tvProduct = createCell(cursor.getString(cursor.getColumnIndexOrThrow("product")));
            TextView tvBrand = createCell(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
            TextView tvExpireAt = createCell(cursor.getString(cursor.getColumnIndexOrThrow("expire_at")));
            Button btnDelete = createDeleteButton();

            row.addView(tvProduct);
            row.addView(tvBrand);
            row.addView(tvExpireAt);
            row.addView(btnDelete);

            tableProducts.addView(row);

            btnDelete.setOnClickListener(v -> confirmDelete(id));
        }

        cursor.close();
    }

    private Button createDeleteButton() {
        Button btnDelete = new Button(this);
        btnDelete.setText("X");
        btnDelete.setTextSize(16);
        btnDelete.setTextColor(Color.WHITE);
        btnDelete.setPadding(20,10,20,10);
        btnDelete.setBackgroundColor(Color.parseColor("#D32F2F"));

        return btnDelete;
    }

    private TextView createCell(String text) {

        TextView tv = new TextView(this);

        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(12, 12, 12, 12);
        tv.setBackgroundColor(Color.parseColor("#EEEEEE"));

        return tv;
    }

    private void confirmDelete(int id) {

        new AlertDialog.Builder(this)
                .setTitle("Excluir produto")
                .setMessage("Deseja realmente excluir este produto?")
                .setPositiveButton("Sim", (dialog, which) -> {

                    DatabaseHelper dbExecutor = new DatabaseHelper(this);
                    dbExecutor.deleteProductById(id);

                    Toast.makeText(this, "Produto excluído", Toast.LENGTH_SHORT).show();

                    finish();
                    startActivity(getIntent());
                })
                .setNegativeButton("Não", null)
                .show();
    }
}