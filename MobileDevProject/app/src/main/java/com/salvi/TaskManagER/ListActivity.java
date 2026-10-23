
package com.salvi.TaskManagER;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.salvi.TaskManagER.Support.DatabaseHelper;

import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private DatabaseHelper dbExecutor;
    private TableLayout tableRequirements;

    private int reunionId;
    private String requirementType;

    private static final String FUNCTIONAL = "functional";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbExecutor = new DatabaseHelper(this);
        tableRequirements = findViewById(R.id.tableProducts);

        reunionId = getIntent().getIntExtra("reunion_id", -1);
        requirementType = getIntent().getStringExtra("requirement_type");

        if (requirementType == null) {
            requirementType = FUNCTIONAL;
        }

        findViewById(R.id.btnReturn2).setOnClickListener(v -> finish());

        if (reunionId == -1) {
            Toast.makeText(
                    this,
                    "Reunião não informada.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        loadRequirements();
    }

    private void loadRequirements() {
        // Preserva o cabeçalho, se houver, e remove as linhas anteriores.
        if (tableRequirements.getChildCount() > 1) {
            tableRequirements.removeViews(
                    1,
                    tableRequirements.getChildCount() - 1
            );
        }

        Cursor cursor = FUNCTIONAL.equals(requirementType)
                ? dbExecutor.getFunctionalRequirements(reunionId)
                : dbExecutor.getNonFunctionalRequirements(reunionId);

        try (cursor) {
            if (cursor == null) {
                return;
            }

            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("description")
                );

                String createAt = cursor.getString(
                        cursor.getColumnIndexOrThrow("create_at")
                );

                int importance = cursor.getInt(
                        cursor.getColumnIndexOrThrow("importance_level")
                );

                int difficulty = cursor.getInt(
                        cursor.getColumnIndexOrThrow("difficulty")
                );

                double timeRequired = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("time_required")
                );

                addRequirementRow(
                        id,
                        description,
                        createAt,
                        importance,
                        difficulty,
                        timeRequired
                );
            }
        }
    }

    private void addRequirementRow(
            int id,
            String description,
            String createAt,
            int importance,
            int difficulty,
            double timeRequired
    ) {
        TableRow row = new TableRow(this);
        row.setPadding(4, 4, 4, 4);

        row.addView(createCell(description));
        row.addView(createCell(createAt));
        row.addView(createCell(String.valueOf(importance)));
        row.addView(createCell(String.valueOf(difficulty)));
        row.addView(createCell(
                String.format(Locale.getDefault(), "%.2f", timeRequired)
        ));

        Button btnView = createButton("Ver", "#1976D2");
        Button btnEdit = createButton("Editar", "#388E3C");
        Button btnDelete = createButton("Excluir", "#D32F2F");

        row.addView(btnView);
        row.addView(btnEdit);
        row.addView(btnDelete);

        tableRequirements.addView(row);

        btnView.setOnClickListener(v ->
                showRequirementDetails(
                        description,
                        createAt,
                        importance,
                        difficulty,
                        timeRequired
                )
        );

        btnEdit.setOnClickListener(v ->
                showEditDialog(
                        id,
                        description,
                        importance,
                        difficulty,
                        timeRequired
                )
        );

        btnDelete.setOnClickListener(v ->
                confirmDelete(id)
        );
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text == null ? "" : text);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(12, 12, 12, 12);
        tv.setBackgroundColor(Color.parseColor("#EEEEEE"));
        tv.setMaxWidth(280);
        return tv;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(12);
        button.setTextColor(Color.WHITE);
        button.setPadding(8, 4, 8, 4);
        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor(color)
                )
        );

        button.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        return button;
    }

    private void showRequirementDetails(
            String description,
            String createAt,
            int importance,
            int difficulty,
            double timeRequired
    ) {
        new AlertDialog.Builder(this)
                .setTitle("Detalhes do requisito")
                .setMessage(
                        "Descrição: " + description +
                                "\n\nData de criação: " + createAt +
                                "\n\nImportância: " + importance +
                                "\n\nDificuldade: " + difficulty +
                                "\n\nTempo necessário: " + timeRequired
                )
                .setPositiveButton("Fechar", null)
                .show();
    }

    private void showEditDialog(
            int id,
            String description,
            int importance,
            int difficulty,
            double timeRequired
    ) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(48, 16, 48, 8);

        EditText etDescription = createInput(
                "Descrição",
                description
        );

        EditText etImportance = createInput(
                "Importância (1 a 4)",
                String.valueOf(importance)
        );
        etImportance.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );

        EditText etDifficulty = createInput(
                "Dificuldade (1 a 4)",
                String.valueOf(difficulty)
        );
        etDifficulty.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );

        EditText etTime = createInput(
                "Tempo necessário",
                String.valueOf(timeRequired)
        );
        etTime.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        form.addView(etDescription);
        form.addView(etImportance);
        form.addView(etDifficulty);
        form.addView(etTime);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Editar requisito")
                .setView(form)
                .setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String newDescription =
                                    etDescription.getText().toString().trim();

                            String importanceText =
                                    etImportance.getText().toString().trim();

                            String difficultyText =
                                    etDifficulty.getText().toString().trim();

                            String timeText =
                                    etTime.getText().toString().trim()
                                            .replace(',', '.');

                            if (newDescription.isEmpty()
                                    || importanceText.isEmpty()
                                    || difficultyText.isEmpty()
                                    || timeText.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        "Preencha todos os campos.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            try {
                                int newImportance =
                                        Integer.parseInt(importanceText);

                                int newDifficulty =
                                        Integer.parseInt(difficultyText);

                                double newTime =
                                        Double.parseDouble(timeText);

                                if (newImportance < 1 || newImportance > 4
                                        || newDifficulty < 1
                                        || newDifficulty > 4
                                        || !Double.isFinite(newTime)
                                        || newTime < 0) {
                                    Toast.makeText(
                                            this,
                                            "Importância e dificuldade devem "
                                                    + "estar entre 1 e 4; "
                                                    + "o tempo não pode ser "
                                                    + "negativo.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                    return;
                                }

                                int updated;

                                if (FUNCTIONAL.equals(requirementType)) {
                                    updated = dbExecutor
                                            .updateFunctionalRequirement(
                                                    id,
                                                    newDescription,
                                                    newImportance,
                                                    newDifficulty,
                                                    newTime
                                            );
                                } else {
                                    updated = dbExecutor
                                            .updateNonFunctionalRequirement(
                                                    id,
                                                    newDescription,
                                                    newImportance,
                                                    newDifficulty,
                                                    newTime
                                            );
                                }

                                if (updated > 0) {
                                    Toast.makeText(
                                            this,
                                            "Requisito atualizado.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    dialog.dismiss();
                                    loadRequirements();
                                } else {
                                    Toast.makeText(
                                            this,
                                            "Não foi possível atualizar.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }

                            } catch (NumberFormatException e) {
                                Toast.makeText(
                                        this,
                                        "Informe valores numéricos válidos.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })
        );

        dialog.show();
    }

    private EditText createInput(String hint, String value) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setText(value);
        input.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return input;
    }

    private void confirmDelete(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir requisito")
                .setMessage("Deseja realmente excluir este requisito?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    int deleted;

                    if (FUNCTIONAL.equals(requirementType)) {
                        deleted = dbExecutor
                                .deleteFunctionalRequirementById(id);
                    } else {
                        deleted = dbExecutor
                                .deleteNonFunctionalRequirementById(id);
                    }

                    if (deleted > 0) {
                        Toast.makeText(
                                this,
                                "Requisito excluído.",
                                Toast.LENGTH_SHORT
                        ).show();
                        loadRequirements();
                    } else {
                        Toast.makeText(
                                this,
                                "Não foi possível excluir o requisito.",
                                Toast.LENGTH_SHORT
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
