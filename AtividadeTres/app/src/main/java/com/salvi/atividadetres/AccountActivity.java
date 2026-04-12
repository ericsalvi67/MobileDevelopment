package com.salvi.atividadetres;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.MaterialToolbar;

public class AccountActivity extends AppCompatActivity {

    EditText etName, etEmail, etAccount, etOldPwd, etNewPwd;
    Button btnReturn, btnUpdateUser, btnChangePwdUser, btnDeleteUser;
    ConstraintLayout clPassword;
    DatabaseHelper dbExecutor;
    boolean changePwd, editUser;
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
        setContentView(R.layout.activity_account);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbExecutor = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAccount = findViewById(R.id.etAccount);
        etOldPwd = findViewById(R.id.etOldPwd);
        etNewPwd = findViewById(R.id.etNewPwd);
        btnReturn = findViewById(R.id.btnReturn2);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);
        btnChangePwdUser = findViewById(R.id.btnChangePwdUser);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        clPassword = findViewById(R.id.clPassword);

        changePwd = false;
        editUser = false;
        editUserInfo();
        changePassword();

        AccountDTO account = getIntent().getSerializableExtra("AccountDTO", AccountDTO.class);

        if (account != null) {
            etName.setText(account.name);
            etEmail.setText(account.email);
            etAccount.setText(account.account);
            isLogged = true;
        }

        findViewById(R.id.btnEditUser).setOnClickListener(v -> {
            editUser = !editUser;
            editUserInfo();
        });
        btnUpdateUser.setOnClickListener(v -> updateUserInfo(account.id));
        btnChangePwdUser.setOnClickListener(v -> {
            changePwd = !changePwd;
            changePassword();
        });
        btnDeleteUser.setOnClickListener(v -> deleteUser(account.id));
        btnReturn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("AccountDTO", account);
            startActivity(i);
            finish();
        });
    }

    private void editUserInfo() {

        clPassword.setVisibility(editUser && changePwd ? VISIBLE : GONE);
        etOldPwd.setVisibility(editUser && changePwd ? VISIBLE : GONE);
        etNewPwd.setVisibility(editUser && changePwd ? VISIBLE : GONE);
        btnChangePwdUser.setVisibility(editUser ? VISIBLE : GONE);
        btnUpdateUser.setVisibility(editUser ? VISIBLE : GONE);
        btnDeleteUser.setVisibility(editUser ? VISIBLE : GONE);

        etName.setEnabled(editUser);
        etEmail.setEnabled(editUser);
        etAccount.setEnabled(false);
        etOldPwd.setEnabled(editUser);
        etNewPwd.setEnabled(editUser);
    }

    private void changePassword() {

        clPassword.setVisibility(changePwd ? VISIBLE : GONE);
        etOldPwd.setVisibility(changePwd ? VISIBLE : GONE);
        etNewPwd.setVisibility(changePwd ? VISIBLE : GONE);

        etOldPwd.setEnabled(changePwd);
        etNewPwd.setEnabled(changePwd);
    }

    private void updateUserInfo(int Id) {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String oldPwd = etOldPwd.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();

        SQLiteDatabase db = dbExecutor.getWritableDatabase();
        ContentValues newData = new ContentValues();

        if (name.isEmpty()) {
            etName.setError("campo vazio");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("campo vazio");
            etEmail.requestFocus();
            return;
        }

        if (changePwd) {
            if (oldPwd.isEmpty()) {
                etOldPwd.setError("campo vazio");
                etOldPwd.requestFocus();
                return;
            }
            if (newPwd.isEmpty()) {
                etNewPwd.setError("campo vazio");
                etNewPwd.requestFocus();
                return;
            }
            if (oldPwd.equals(newPwd)) {
                etNewPwd.setError("A senha não pode ser igual a antiga");
                etNewPwd.requestFocus();
                return;
            }
            newData.put("password", newPwd);
        }

        newData.put("name", name);
        newData.put("email", email);

        int rows = db.update("item", newData, "id=?", new String[]{String.valueOf(Id)});
        if (rows > 0) {
            Toast.makeText(this, "Dados atualizados", Toast.LENGTH_SHORT).show();
            NotificationExecutor.sendNotification(
                    this,
                    "Cadastro",
                    "Usuário atualizado com sucesso"
            );

            if (changePwd) {
                changePwd = false;
                changePassword();
            }
            etOldPwd.setText("");
            etNewPwd.setText("");
        } else {
            Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUser(int Id) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Deletar Conta")
                .setMessage("Deseja mesmo deletar a conta?")
                .setCancelable(false)

                .setPositiveButton("Deletar", (dialog, which) -> {
                    SQLiteDatabase db = dbExecutor.getWritableDatabase();
                    db.delete("item", "id=?", new String[]{String.valueOf(Id)});

                    Toast.makeText(this, "Conta deletada", Toast.LENGTH_SHORT).show();
                    NotificationExecutor.sendNotification(
                            this,
                            "Conta removida",
                            "Conta foi excluída com sucesso"
                    );

                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    finish();
                })
                .setNegativeButton("Voltar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}