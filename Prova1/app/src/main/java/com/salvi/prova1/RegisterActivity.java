package com.salvi.prova1;

import static android.view.ViewDebug.trace;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    EditText etErpId, etProductName, etBrand, etExpireAt;
    DatabaseHelper dbExecutor;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_signup);

        dbExecutor = new DatabaseHelper(this);

        etProductName = findViewById(R.id.etProductRegister);
        etBrand = findViewById(R.id.etBrandRegister);
        etErpId = findViewById(R.id.etErpIdRegister);
        etExpireAt = findViewById(R.id.etExpireRegister);

        etErpId.setEnabled(false);
        etProductName.requestFocus();

        //Gambi pra aparecer o ERP_ID
        etBrand.setOnFocusChangeListener((v, hasFocus) -> {
            String brand = etBrand.getText().toString().trim();
            String product = etProductName.getText().toString().trim();
            String expireAt = etExpireAt.getText().toString().trim();


            if (!hasFocus && !brand.isBlank() && !product.isBlank() && !expireAt.isBlank()) {
                String Id = etBrand.getText().toString().trim() + "|" + etProductName.getText().toString().trim() + "|" + etExpireAt.getText().toString().trim();
                etErpId.setText(Id);
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> register());
        findViewById(R.id.btnReturn1).setOnClickListener(v -> finish());
    }

    private void register() {

        ProductViewModel product = new ProductViewModel();
        product.product_name = etProductName.getText().toString().trim();
        product.brand = etBrand.getText().toString().trim();
        product.expire_at = etExpireAt.getText().toString().trim();
        product.erp_id = etErpId.getText().toString().trim();

        SQLiteDatabase db = dbExecutor.getWritableDatabase();
        ContentValues newData = new ContentValues();

        if (product.product_name.isEmpty()) {
            etProductName.setError("campo vazio");
            etProductName.requestFocus();
            return;
        }

        if (product.brand.isEmpty()) {
            etBrand.setError("campo vazio");
            etBrand.requestFocus();
            return;
        }

        if (product.expire_at.isEmpty()) {
            etExpireAt.setError("campo vazio");
            etExpireAt.requestFocus();
            return;
        }

        if (product.erp_id.isEmpty())
        {
            etErpId.setError("campo vazio");
            etExpireAt.requestFocus();
            return;
        }

        newData.put("brand", product.brand);
        newData.put("product", product.product_name);
        newData.put("erp_id", product.erp_id);
        newData.put("expire_at", product.expire_at);

        try{
            long id = db.insert("product_items", null, newData);

            if (id != -1) {
                Toast.makeText(this, "Produto Cadastrado", Toast.LENGTH_SHORT).show();
                clearNFocusRegisters();
                finish();
            } else {
                Toast.makeText(this, "Erro: Produto já está cadastrado", Toast.LENGTH_SHORT).show();
                finish();
            }
            db.close();
        }catch(Exception e){
            e.printStackTrace();
            Log.e("ERRO_APP", "Falha de Insert", e);
            db.close();
            finish();
        }
    }

    private void clearNFocusRegisters() {
        etProductName.setText("");
        etBrand.setText("");
        etErpId.setText("");
        etExpireAt.setText("");
    }
}