package com.salvi.primeiroapp;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Dictionary;
import java.util.Hashtable;


public class MainActivity extends AppCompatActivity {

    private EditText boxName = null;
    private EditText boxPWD = null;

    private Dictionary<String, String> users = new Hashtable<>();
    private Dictionary<String, String> tips = new Hashtable<>();

    private void  popupSwitch(String title, String message, String name) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String tipDB = tips.get(name);
                    if (tipDB != null) {
                        String mensagemErro = "Dica: " + tipDB;
                        Toast.makeText(MainActivity.this, mensagemErro, Toast.LENGTH_LONG).show();
                    }
                }
            })
            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            })
            .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        users.put("Eric Salvi", "Caminhao123");
        users.put("Jeferson Silva", "QueroQuero21.");
        users.put("Sandra Clea", "Sandrinha77");

        tips.put("Eric Salvi", "Veiculo de transporte");
        tips.put("Jeferson Silva", "Ave tipica do RS");
        tips.put("Sandra Clea", "Diminutivo");

        boxName = findViewById(R.id.it_nome);
        boxPWD = findViewById(R.id.it_senha);

        Button btEnviar = findViewById(R.id.bt_confirmacao);

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = boxName.getText().toString().trim();
                String senha = boxPWD.getText().toString().trim();

                if (nome.isEmpty()) {
                    boxName.setError("O nome é obrigatório");
                    boxName.requestFocus();
                    return;
                }

                if (senha.isEmpty()) {
                    boxPWD.setError("A senha é obrigatória");
                    boxPWD.requestFocus();
                    return;
                }

                String senhaDB = users.get(nome);

                if (senhaDB == null) {
                    boxName.setError("Usuário não cadastrado.");
                    boxName.requestFocus();
                    return;
                }

                if (senhaDB.equals(senha)) {
                    Toast.makeText(MainActivity.this, "Bem vindo, " + nome + "!", Toast.LENGTH_LONG).show();
                    boxName.setText("");
                    boxPWD.setText("");
                    boxName.requestFocus();
                }
                else {
                    String mensagemErro = "Senha incorreta";
                    popupSwitch("Dica", "Deseja dica de senha?", nome);
                    boxPWD.setError(mensagemErro);
                    boxPWD.requestFocus();
                }
            }
        });

    }
}