package com.salvi.atividadetres;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    EditText etLogin, etPassword;
    Button btnInfo;
    DatabaseHelper dbExecutor;

    AccountDTO loggedAccount;
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
        if (item.getItemId() == R.id.menu_logout) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbExecutor = new DatabaseHelper(this);
        etLogin = findViewById(R.id.etAccountLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnInfo = findViewById(R.id.btnInfo);


        loggedAccount = getIntent().getSerializableExtra("AccountDTO", AccountDTO.class);
        if (loggedAccount != null) {
            etLogin.setText(loggedAccount.account);
            isLogged = true;
        } else {
            btnInfo.setVisibility(GONE);
            etLogin.setText("");
            etPassword.setText("");
        }

        findViewById(R.id.btnInfo).setOnClickListener( v -> {
                Intent i = new Intent(this, AccountActivity.class);
                i.putExtra("AccountDTO", loggedAccount);
                startActivity(i);
            }
        );
        findViewById(R.id.btnLogin).setOnClickListener(v -> login());
        findViewById(R.id.btnRegister).setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    private void login() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (login.isEmpty()) {
            etLogin.setError("O campo vazio");
            etLogin.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("O campo vazio");
            etPassword.requestFocus();
            return;
        }

        SQLiteDatabase db = dbExecutor.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,name,email,account,password " +
                                          "FROM item " +
                                         "WHERE account=? " +
                                           "AND password=?",
                new String[]{login, password}
        );

        if (cursor.moveToFirst()) {
            loggedAccount = new AccountDTO(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("account")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
            btnInfo.setVisibility(VISIBLE);
            Toast.makeText(this, "Bem vindo " + loggedAccount.name + " ", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, AccountActivity.class);
            i.putExtra("AccountDTO", loggedAccount);
            startActivity(i);
        } else {
            etLogin.setError("Usuário ou senha incorretos");
            etLogin.requestFocus();
            loggedAccount = null;
            return;
        }
        cursor.close();
        db.close();
    }
}