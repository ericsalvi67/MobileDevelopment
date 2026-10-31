package com.salvi.TaskManagER;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.salvi.TaskManagER.Support.DatabaseHelper;
import com.salvi.TaskManagER.ViewModels.FunctionalRequirementViewModel;
import com.salvi.TaskManagER.ViewModels.NonFunctionalRequirementViewModel;
import com.salvi.TaskManagER.ViewModels.ReunionViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText tvReunionName;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TableLayout tableRequirements;

    private Calendar startDate;
    private Calendar endDate;
    private FloatingActionButton fabAdd;
    private MaterialButton btnReturn;
    private MaterialButton btnSaveReunion;

    private ReunionViewModel reunionViewModel;
    private FunctionalRequirementViewModel functionalViewModel;
    private NonFunctionalRequirementViewModel nonFunctionalViewModel;

    private DatabaseHelper databaseHelper;

    private Integer currentReunionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);

        initializeComponents();

        databaseHelper = new DatabaseHelper(this);

        int reunionId = getIntent().getIntExtra("reunion_id", -1);

        if (reunionId != -1) {
            currentReunionId = reunionId;
            loadReunionName();
            loadRequirements();
        }

        initializeViewModels();
        configureListeners();
    }

    private void initializeComponents() {
        tvReunionName = findViewById(R.id.etReunionName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        tableRequirements = findViewById(R.id.tableRequirements);
        fabAdd = findViewById(R.id.fabAddRequirement);
        btnReturn = findViewById(R.id.btnReturn);
        btnSaveReunion = findViewById(R.id.btnSaveReunion);
    }

    private void initializeViewModels() {
        reunionViewModel =
            new ViewModelProvider(this).get(ReunionViewModel.class);

        functionalViewModel =
            new ViewModelProvider(this).get(FunctionalRequirementViewModel.class);

        nonFunctionalViewModel =
            new ViewModelProvider(this).get(NonFunctionalRequirementViewModel.class);

        reunionViewModel.getOperationResult().observe(this, id -> {
            if (id == null || id <= 0) {
                return;
            }

            if (currentReunionId == null) {
                currentReunionId = id.intValue();
            }

        });

        functionalViewModel.getRequirements().observe(this, requirements -> {
            if (currentReunionId != null) {
                loadRequirements();
            }
        });

        nonFunctionalViewModel.getRequirements().observe(this, requirements -> {
            if (currentReunionId != null) {
                loadRequirements();
            }
        });
    }

    private void configureListeners() {
        tvStartDate.setOnClickListener(v -> showStartDatePicker());
        tvEndDate.setOnClickListener(v -> showEndDatePicker());

        fabAdd.setOnClickListener(v -> {
            if (currentReunionId == null || currentReunionId <= 0) {
                Toast.makeText(
                    this,
                    "Primeiro, cadastre uma reunião clicando no nome.",
                    Toast.LENGTH_LONG
                ).show();
                return;
            }

            showRequirementDialog();
        });

        btnSaveReunion.setOnClickListener(v -> saveReunion());
        btnReturn.setOnClickListener(v -> finish());
    }

    private void saveReunion() {
        String name = tvReunionName.getText().toString().trim();
        String startDate = tvStartDate.getText().toString().trim();
        String endDate = tvStartDate.getText().toString().trim();

        if (name.isEmpty()) {
            tvReunionName.setError("Informe o nome da reunião.");
            return;
        }

        if (startDate.isEmpty()){
            tvStartDate.setError("Informe uma data inicial");
            return;
        }

        if (endDate.isEmpty()){
            tvEndDate.setError("Informe uma data final");
            return;
        }


        if (currentReunionId == null) {
            reunionViewModel.insertReunion(
                name,
                startDate.formatted("yyyy-MM-dd"),
                endDate.formatted("yyyy-MM-dd")
            );
            Toast.makeText(this, "Reunião cadastrada.", Toast.LENGTH_SHORT).show();
        } else {
            reunionViewModel.updateReunion(
                currentReunionId,
                name,
                startDate.formatted("yyyy-MM-dd"),
                endDate.formatted("yyyy-MM-dd")
            );
            Toast.makeText(this, "Reunião salva.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStartDatePicker() {

        Calendar initialDate = startDate != null
                ? (Calendar) startDate.clone()
                : Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    startDate = createDate(year, month, dayOfMonth);

                    tvStartDate.setText(formatDate(startDate));

                    // Verifica se a data final continua válida.
                    if (endDate != null && endDate.before(startDate)) {
                        endDate = null;
                        tvEndDate.setText("Data final");
                    }
                },
                initialDate.get(Calendar.YEAR),
                initialDate.get(Calendar.MONTH),
                initialDate.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void showEndDatePicker() {

        Calendar initialDate = endDate != null
                ? (Calendar) endDate.clone()
                : startDate != null
                ? (Calendar) startDate.clone()
                : Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    endDate = createDate(year, month, dayOfMonth);

                    tvEndDate.setText(formatDate(endDate));
                },
                initialDate.get(Calendar.YEAR),
                initialDate.get(Calendar.MONTH),
                initialDate.get(Calendar.DAY_OF_MONTH)
        );

        // Impede selecionar uma data final anterior à data inicial.
        if (startDate != null) {
            dialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
        }

        dialog.show();
    }

    private void showRequirementDialog() {
        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_requirement, null);

        EditText etDesc = dialogView.findViewById(R.id.etDescription);
        Spinner spImportance = dialogView.findViewById(R.id.spImportance);
        Spinner spDifficulty = dialogView.findViewById(R.id.spDifficulty);
        EditText etTime = dialogView.findViewById(R.id.etTimeRequired);
        RadioGroup rgType = dialogView.findViewById(R.id.rgRequirementType);

        String[] levels = getResources().getStringArray(R.array.requirement_levels);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            levels
        );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        spImportance.setAdapter(adapter);
        spDifficulty.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Adicionar requisito")
            .setView(dialogView)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .create();

        dialog.setOnShowListener(ignored ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String description =
                        etDesc.getText().toString().trim();

                    String timeText =
                        etTime.getText().toString().trim();

                    if (description.isEmpty()) {
                        etDesc.setError("Informe a descrição.");
                        return;
                    }

                    if (timeText.isEmpty()) {
                        etTime.setError("Informe o tempo estimado.");
                        return;
                    }

                    double time;

                    try {
                        time = Double.parseDouble(
                            timeText.replace(",", ".")
                        );

                        if (!Double.isFinite(time) || time < 0) {
                            etTime.setError(
                                "Informe um tempo válido, maior ou igual a zero."
                            );
                            return;
                        }
                    } catch (NumberFormatException e) {
                        etTime.setError("Informe um número válido.");
                        return;
                    }

                    if (rgType.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(
                            this,
                            "Selecione o tipo de requisito.",
                            Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    int importance =
                        spImportance.getSelectedItemPosition() + 1;

                    int difficulty =
                        spDifficulty.getSelectedItemPosition() + 1;

                    if (currentReunionId == null
                            || currentReunionId <= 0) {
                        Toast.makeText(
                            this,
                            "Nenhuma reunião válida foi selecionada.",
                            Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    if (rgType.getCheckedRadioButtonId() == R.id.rbFunctional) {
                        functionalViewModel.insertRequirement(
                            currentReunionId,
                            description,
                            importance,
                            difficulty,
                            time
                        );
                    } else {
                        nonFunctionalViewModel.insertRequirement(
                            currentReunionId,
                            description,
                            importance,
                            difficulty,
                            time
                        );
                    }

                    Toast.makeText(
                        this,
                        "Requisito enviado para cadastro.",
                        Toast.LENGTH_SHORT
                    ).show();

                    dialog.dismiss();
                })
        );

        dialog.show();
    }

    private void loadRequirements() {
        if (currentReunionId == null || databaseHelper == null) {
            return;
        }

        // Preserva a primeira linha, que contém o cabeçalho.
        if (tableRequirements.getChildCount() > 1) {
            tableRequirements.removeViews(
                1,
                tableRequirements.getChildCount() - 1
            );
        }

        loadFunctionalRequirements();
        loadNonFunctionalRequirements();
    }

    private void loadFunctionalRequirements() {
        Cursor cursor =
            databaseHelper.getFunctionalRequirements(currentReunionId);

        if (cursor == null) {
            return;
        }

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                );

                String description = cursor.getString(
                    cursor.getColumnIndexOrThrow("description")
                );

                int importance = cursor.getInt(
                    cursor.getColumnIndexOrThrow("importance_level")
                );

                int difficulty = cursor.getInt(
                    cursor.getColumnIndexOrThrow("difficulty")
                );

                double time = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("time_required")
                );

                addRequirementRow(
                    id,
                    description,
                    "Funcional",
                    importance,
                    difficulty,
                    time,
                    true
                );
            }
        } finally {
            cursor.close();
        }
    }

    private void loadNonFunctionalRequirements() {
        Cursor cursor =
            databaseHelper.getNonFunctionalRequirements(currentReunionId);

        if (cursor == null) {
            return;
        }

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                );

                String description = cursor.getString(
                    cursor.getColumnIndexOrThrow("description")
                );

                int importance = cursor.getInt(
                    cursor.getColumnIndexOrThrow("importance_level")
                );

                int difficulty = cursor.getInt(
                    cursor.getColumnIndexOrThrow("difficulty")
                );

                double time = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("time_required")
                );

                addRequirementRow(
                    id,
                    description,
                    "Não funcional",
                    importance,
                    difficulty,
                    time,
                    false
                );
            }
        } finally {
            cursor.close();
        }
    }

    private void addRequirementRow(
        int id,
        String description,
        String type,
        int importance,
        int difficulty,
        double time,
        boolean isFunctional
    ) {
        TableRow row = new TableRow(this);
        row.setPadding(4, 4, 4, 4);

        row.addView(createCell(description, 180));
        row.addView(createCell(type, 120));
        row.addView(createCell(String.valueOf(importance), 100));
        row.addView(createCell(String.valueOf(difficulty), 100));
        row.addView(createCell(
            String.format(Locale.getDefault(), "%.2f", time),
            100
        ));

        row.setOnClickListener(v ->
            showEditRequirementDialog(
                id,
                description,
                isFunctional,
                importance,
                difficulty,
                time
            )
        );

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        TextView editButton = new TextView(this);
        editButton.setText("✏️");
        editButton.setTextSize(20);
        editButton.setGravity(Gravity.CENTER);
        editButton.setPadding(8, 12, 8, 12);
        editButton.setContentDescription("Editar requisito");

        editButton.setOnClickListener(v ->
            showEditRequirementDialog(
                id,
                description,
                isFunctional,
                importance,
                difficulty,
                time
            )
        );

        TextView deleteButton = new TextView(this);
        deleteButton.setText("🗑️");
        deleteButton.setTextSize(20);
        deleteButton.setGravity(Gravity.CENTER);
        deleteButton.setPadding(8, 12, 8, 12);
        deleteButton.setContentDescription("Excluir requisito");

        deleteButton.setOnClickListener(v ->
            confirmDelete(id, description, isFunctional)
        );

        actions.addView(editButton);
        actions.addView(deleteButton);

        row.addView(actions);
        tableRequirements.addView(row);
    }

    private TextView createCell(String text, int widthDp) {
        TextView cell = new TextView(this);

        cell.setText(text);
        cell.setPadding(8, 12, 8, 12);
        cell.setTextColor(
            getColor(
                com.google.android.material.R.color
                    .material_on_surface_emphasis_medium
                )
        );

        cell.setLayoutParams(new TableRow.LayoutParams(
            dpToPx(widthDp),
            TableRow.LayoutParams.WRAP_CONTENT
        ));

        return cell;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
            .getDisplayMetrics().density + 0.5f);
    }

    private void confirmDelete(
            int id,
            String description,
            boolean isFunctional
    ) {
        new AlertDialog.Builder(this)
            .setTitle("Excluir requisito")
            .setMessage(
                "Deseja excluir o requisito \"" +
                    description + "\"?"
            )
            .setPositiveButton("Excluir", (dialog, which) -> {
                if (isFunctional) {
                    functionalViewModel.deleteRequirement(
                        id,
                        currentReunionId
                    );
                } else {
                    nonFunctionalViewModel.deleteRequirement(
                        id,
                        currentReunionId
                    );
                }

                Toast.makeText(
                    this,
                    "Solicitação de exclusão enviada.",
                    Toast.LENGTH_SHORT
                ).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void loadReunionName() {
        if (currentReunionId == null || databaseHelper == null) {
            return;
        }

        Cursor cursor = databaseHelper.getReunionById(currentReunionId);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    tvReunionName.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                    ));
                    startDate = parseDate(cursor.getString(
                        cursor.getColumnIndexOrThrow("start_date")
                    ));
                    endDate = parseDate(cursor.getString(
                        cursor.getColumnIndexOrThrow("end_date")
                    ));
                    tvStartDate.setText(startDate == null
                        ? "Data inicial" : formatDate(startDate));
                    tvEndDate.setText(endDate == null
                        ? "Data final" : formatDate(endDate));
                }
            } finally {
                cursor.close();
            }
        }
    }


    private void showEditRequirementDialog(
        int id,
        String description,
        boolean isFunctional,
        int importance,
        int difficulty,
        double time
    ) {
        if (currentReunionId == null || currentReunionId <= 0) {
            Toast.makeText(
                this,
                "Nenhuma reunião válida foi selecionada.",
                Toast.LENGTH_SHORT
            ).show();
            return;
        }

        View dialogView = getLayoutInflater()
            .inflate(R.layout.dialog_requirement, null);

        EditText etDesc = dialogView.findViewById(R.id.etDescription);
        Spinner spImportance = dialogView.findViewById(R.id.spImportance);
        Spinner spDifficulty = dialogView.findViewById(R.id.spDifficulty);
        EditText etTime = dialogView.findViewById(R.id.etTimeRequired);
        RadioGroup rgType = dialogView.findViewById(R.id.rgRequirementType);

        // Preenche os campos com os dados atuais.
        etDesc.setText(description);
        etTime.setText(String.valueOf(time));

        String[] levels = getResources()
            .getStringArray(R.array.requirement_levels);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            levels
        );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        spImportance.setAdapter(adapter);
        spDifficulty.setAdapter(adapter);

        // Os níveis do banco começam em 1; as posições começam em 0.
        spImportance.setSelection(
            Math.max(0, Math.min(importance - 1, levels.length - 1))
        );

        spDifficulty.setSelection(
            Math.max(0, Math.min(difficulty - 1, levels.length - 1))
        );

        // Mantém o tipo original do requisito.
        rgType.check(
            isFunctional
                ? R.id.rbFunctional
                : R.id.rbNonFunctional
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Editar requisito")
            .setView(dialogView)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .create();

        dialog.setOnShowListener(ignored ->
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {

                String newDescription =
                        etDesc.getText().toString().trim();

                String timeText =
                        etTime.getText().toString().trim();

                if (newDescription.isEmpty()) {
                    etDesc.setError("Informe a descrição.");
                    return;
                }

                if (timeText.isEmpty()) {
                    etTime.setError("Informe o tempo estimado.");
                    return;
                }

                double newTime;

                try {
                    newTime = Double.parseDouble(
                        timeText.replace(",", ".")
                    );

                    if (!Double.isFinite(newTime) || newTime < 0) {
                        etTime.setError(
                            "Informe um tempo válido, maior ou igual a zero."
                        );
                        return;
                    }

                } catch (NumberFormatException e) {
                    etTime.setError("Informe um número válido.");
                    return;
                }

                if (rgType.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(
                        this,
                        "Selecione o tipo de requisito.",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                int newImportance =
                    spImportance.getSelectedItemPosition() + 1;

                int newDifficulty =
                    spDifficulty.getSelectedItemPosition() + 1;

                boolean newIsFunctional =
                    rgType.getCheckedRadioButtonId()
                        == R.id.rbFunctional;

                // Se o tipo mudou, exclui da tabela antiga e
                // cadastra na tabela correspondente ao novo tipo.
                if (newIsFunctional != isFunctional) {
                    new AlertDialog.Builder(this)
                        .setTitle("Alterar tipo de requisito")
                        .setMessage(
                            "Ao alterar o tipo, o requisito será " +
                            "movido para a tabela do novo tipo. " +
                            "Deseja continuar?"
                        )
                        .setPositiveButton("Continuar",
                            (d, which) -> {

                                if (isFunctional) {
                                    functionalViewModel
                                        .deleteRequirement(
                                            id,
                                            currentReunionId
                                        );
                                } else {
                                    nonFunctionalViewModel
                                        .deleteRequirement(
                                            id,
                                            currentReunionId
                                        );
                                }

                                if (newIsFunctional) {
                                    functionalViewModel
                                        .insertRequirement(
                                            currentReunionId,
                                            newDescription,
                                            newImportance,
                                            newDifficulty,
                                            newTime
                                        );
                                } else {
                                    nonFunctionalViewModel
                                        .insertRequirement(
                                            currentReunionId,
                                            newDescription,
                                            newImportance,
                                            newDifficulty,
                                            newTime
                                        );
                                }

                                dialog.dismiss();

                                Toast.makeText(
                                    this,
                                    "Solicitação de alteração enviada.",
                                    Toast.LENGTH_SHORT
                                ).show();
                            })
                        .setNegativeButton("Cancelar", null)
                        .show();

                    return;
                }

                // Atualiza o registro existente, preservando seu ID.
                if (isFunctional) {
                    functionalViewModel.updateRequirement(
                            id,
                            currentReunionId,
                            newDescription,
                            newImportance,
                            newDifficulty,
                            newTime
                    );
                } else {
                    nonFunctionalViewModel.updateRequirement(
                            id,
                            currentReunionId,
                            newDescription,
                            newImportance,
                            newDifficulty,
                            newTime
                    );
                }

                dialog.dismiss();

                Toast.makeText(
                    this,
                    "Solicitação de atualização enviada.",
                    Toast.LENGTH_SHORT
                ).show();
            })
        );

        dialog.show();
    }

    private Calendar createDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, 0, 0, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    private String formatDate(Calendar date) {
        return date == null ? "" : new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).format(date.getTime());
    }

    private Calendar parseDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            Calendar date = Calendar.getInstance();
            date.setTime(new SimpleDateFormat(
                "yyyy-MM-dd", Locale.US
            ).parse(value));
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}