
package com.salvi.TaskManagER;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.salvi.TaskManagER.ViewModels.FunctionalRequirementViewModel;
import com.salvi.TaskManagER.ViewModels.NonFunctionalRequirementViewModel;
import com.salvi.TaskManagER.ViewModels.ReunionViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etReunionName;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAdd;
    
    private ReunionViewModel reunionViewModel;
    private FunctionalRequirementViewModel functionalViewModel;
    private NonFunctionalRequirementViewModel nonFunctionalViewModel;

    private Integer currentReunionId = null;
    private boolean isSavingForRequirement = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);

        initializeComponents();
        setupViewPager();
        initializeViewModels();
        configureListeners();
    }

    private void initializeComponents() {
        etReunionName = findViewById(R.id.etReunionName);
        tabLayout = findViewById(R.id.tabRequirements);
        viewPager = findViewById(R.id.vpRequirements);
        fabAdd = findViewById(R.id.fabAddRequirement);
        
        findViewById(R.id.btnReturn).setOnClickListener(v -> finish());
    }

    private void setupViewPager() {
        // Configura o ViewPager para alternar entre as listas de requisitos
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Aqui você retornaria seus fragmentos de lista, se existirem.
                // Por enquanto, o ViewPager está configurado para a estrutura do layout.
                return new Fragment(); 
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Funcionais" : "Não funcionais");
        }).attach();
    }

    private void initializeViewModels() {
        reunionViewModel = new ViewModelProvider(this).get(ReunionViewModel.class);
        functionalViewModel = new ViewModelProvider(this).get(FunctionalRequirementViewModel.class);
        nonFunctionalViewModel = new ViewModelProvider(this).get(NonFunctionalRequirementViewModel.class);

        reunionViewModel.getOperationResult().observe(this, id -> {
            if (id != null && id > 0 && isSavingForRequirement) {
                isSavingForRequirement = false;
                currentReunionId = id.intValue();
                showRequirementDialog();
            }
        });
    }

    private void configureListeners() {
        fabAdd.setOnClickListener(v -> {
            String name = etReunionName.getText().toString().trim();
            if (name.isEmpty()) {
                etReunionName.setError("Dê um nome à reunião antes de prosseguir.");
                return;
            }

            if (currentReunionId == null) {
                isSavingForRequirement = true;
                reunionViewModel.insertReunion(name);
            } else {
                showRequirementDialog();
            }
        });
    }

    private void showRequirementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_requirement, null);
        builder.setView(dialogView);

        EditText etDesc = dialogView.findViewById(R.id.etDescription);
        Spinner spImportance = dialogView.findViewById(R.id.spImportance);
        Spinner spDifficulty = dialogView.findViewById(R.id.spDifficulty);
        EditText etTime = dialogView.findViewById(R.id.etTimeRequired);

        String[] levels = getResources().getStringArray(R.array.requirement_levels);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImportance.setAdapter(adapter);
        spDifficulty.setAdapter(adapter);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String desc = etDesc.getText().toString().trim();
            String timeStr = etTime.getText().toString().trim();

            if (desc.isEmpty() || timeStr.isEmpty()) return;

            double time = Double.parseDouble(timeStr.replace(",", "."));
            int importance = spImportance.getSelectedItemPosition() + 1;
            int difficulty = spDifficulty.getSelectedItemPosition() + 1;

            if (tabLayout.getSelectedTabPosition() == 0) {
                functionalViewModel.insertRequirement(currentReunionId, desc, importance, difficulty, time);
            } else {
                nonFunctionalViewModel.insertRequirement(currentReunionId, desc, importance, difficulty, time);
            }
            Toast.makeText(this, "Requisito salvo!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}
