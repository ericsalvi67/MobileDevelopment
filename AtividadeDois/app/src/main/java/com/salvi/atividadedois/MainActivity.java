package com.salvi.atividadedois;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private EditText boxName = null;
    private EditText boxAddress = null;
    private EditText boxPhone = null;
    private RadioGroup radioGenre = null;
    private DatePicker pickerBirth = null;
    private String todayDate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boxName = findViewById(R.id.boxName);
        boxAddress = findViewById(R.id.boxAddress);
        boxPhone = findViewById(R.id.boxPhone);
        radioGenre = (RadioGroup) findViewById(R.id.Genero);
        pickerBirth = findViewById(R.id.datePicker);
        pickerBirth.init(1970,0, 1,null);
        pickerBirth.setMaxDate(System.currentTimeMillis());
        todayDate = String.format("%02d-%02d-%04d",
                pickerBirth.getDayOfMonth(),
                pickerBirth.getMonth() + 1,
                pickerBirth.getYear()
        );


        Button buttonAccept = findViewById(R.id.buttonAccept);

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = boxName.getText().toString().trim();
                String address = boxAddress.getText().toString().trim();
                String phone = boxPhone.getText().toString().trim();
                RadioButton genre = findViewById(radioGenre.getCheckedRadioButtonId()) ;
                String birthDate = String.format("%02d-%02d-%04d",
                                pickerBirth.getDayOfMonth(),
                                pickerBirth.getMonth() + 1,
                                pickerBirth.getYear());

                if (name.isEmpty()) {
                    boxName.setError("O nome é obrigatório");
                    boxName.requestFocus();
                    return;
                }

                if (address.isEmpty()) {
                    boxAddress.setError("Endereço obrigatório");
                    boxAddress.requestFocus();
                    return;
                }

                if (phone.isEmpty() || !phone.matches("\\d{9,11}")) {
                    boxPhone.setError("Telefone inválido");
                    boxPhone.requestFocus();
                    return;
                }

                if (birthDate.equals(todayDate)) {
                    Toast.makeText(MainActivity.this,"Data de nascimento inválida", Toast.LENGTH_SHORT).show();
                    pickerBirth.requestFocus();
                    return;
                }

                if (genre.getText().equals("Masculino"))
                    Toast.makeText(MainActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Usuária cadastrada com sucesso!", Toast.LENGTH_LONG).show();
            }
        });

    }
}