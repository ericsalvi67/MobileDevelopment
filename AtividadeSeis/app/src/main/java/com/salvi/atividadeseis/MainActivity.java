package com.salvi.atividadeseis;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btSend = null;
    private TextView tvResponse = null;
    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        tvResponse = (TextView) findViewById(R.id.tvResponse);
        btSend = (Button) findViewById(R.id.btSend);


        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResponse.setText("");
                execute();
            }
        });
    }
    public void execute() {

        AccountDTO account = getFields();
        if (account == null) {
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String urlConexao = "http://10.0.2.2:8080/login";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", account.email);
        params.put("password", account.password);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                urlConexao,
                new JSONObject(params),
                response -> {
                    toast = Toast.makeText(this, "Logado com sucesso", Toast.LENGTH_SHORT);
                    toast.show();
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);
                            String message = data.optString("error", "Erro desconhecido");
                            tvResponse.setText("Erro da API: " + message);
                        } catch (Exception e) {
                            tvResponse.setText("Erro ao processar resposta: " + e.getMessage());
                        }
                    } else {
                        tvResponse.setText("Erro: " + error.toString());
                    }
                }
        );
        queue.add(req);
    }

    private AccountDTO getFields() {
        AccountDTO account = new AccountDTO();
        account.email = etEmail.getText().toString().trim();
        account.password = etPassword.getText().toString().trim();

        if (account.email.isEmpty()) {
            etEmail.setError("Campo obrigatório");
            etEmail.requestFocus();
            return null;
        }
        if (account.password.isEmpty()) {
            etPassword.setError("Campo obrigatório");
            etPassword.requestFocus();
            return null;
        }

        return account;
    }
}