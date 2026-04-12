package com.salvi.atividadetres;

import static java.lang.Thread.sleep;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etAccount, etPwd;
    DatabaseHelper dbExecutor;
    private MenuItem menuLogout;
    private boolean isLogged = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menuLogout = menu.findItem(R.id.menu_logout);

        if (menuLogout != null) {
            menuLogout.setVisible(isLogged);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_signup);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbExecutor = new DatabaseHelper(this);

        etName = findViewById(R.id.etNameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etAccount = findViewById(R.id.etAccountRegister);
        etPwd = findViewById(R.id.etPwdRegister);

        etAccount.setEnabled(false);

        //Gambi pra aparecer o userID
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                String email = etEmail.getText().toString();
                int pos = email.indexOf("@");
                if(pos != -1){
                    etAccount.setText(email.substring(0, pos));
                }
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> register());
        findViewById(R.id.btnReturn3).setOnClickListener(v -> finish());
    }

    private void register() {

        AccountDTO account = new AccountDTO();
        account.name = etName.getText().toString().trim();
        account.email = etEmail.getText().toString().trim();
        account.password = etPwd.getText().toString().trim();

        if (!account.email.contains("@")) {
            etEmail.setError("email incorreto");
            etEmail.requestFocus();
            return;
        }
        account.account = account.email.substring(0, account.email.indexOf("@"));

        etAccount.post( () -> {
            etAccount.setText(account.account);
            }
        );

        SQLiteDatabase db = dbExecutor.getWritableDatabase();
        ContentValues newData = new ContentValues();

        if (account.name.isEmpty()) {
            etName.setError("campo vazio");
            etName.requestFocus();
            return;
        }

        if (account.email.isEmpty()) {
            etEmail.setError("campo vazio");
            etEmail.requestFocus();
            return;
        }

        if (account.password.isEmpty()) {
            etPwd.setError("campo vazio");
            etPwd.requestFocus();
            return;
        }

        newData.put("name", account.name);
        newData.put("email", account.email);
        newData.put("account", account.account);
        newData.put("password", account.password);

        long id = db.insert("item", null, newData);

        if (id != -1) {

            int count = dbExecutor.getTotalRows("item");
            if (count%10 == 0)
                NotificationExecutor.sendNotification(
                        this,
                        "Contagem",
                        "total de " + count + " usuários cadastrados."
                );

            Toast.makeText(this, "Usuário Cadastrado", Toast.LENGTH_SHORT).show();
            NotificationExecutor.sendNotification(
                    this,
                    "Cadastro",
                    "Usuário cadastrado com sucesso"
            );

            Intent i = new Intent(this, AccountActivity.class);
            i.putExtra("AccountDTO", account);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Erro: ID de conta já existente", Toast.LENGTH_SHORT).show();
        }
    }
}