
package com.salvi.TaskManagER.ViewModels;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.salvi.TaskManagER.Entities.NonFunctionalRequirement;
import com.salvi.TaskManagER.Support.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NonFunctionalRequirementViewModel extends AndroidViewModel {

    private final DatabaseHelper databaseHelper;
    private final ExecutorService executor =
        Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<NonFunctionalRequirement>> requirements =
        new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Long> operationResult =
        new MutableLiveData<>();

    public NonFunctionalRequirementViewModel(
        @NonNull Application application
    ) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
    }

    public LiveData<List<NonFunctionalRequirement>> getRequirements() {
        return requirements;
    }

    public LiveData<Long> getOperationResult() {
        return operationResult;
    }

    public void loadRequirements(int reunionId) {
        executor.execute(() -> {
            List<NonFunctionalRequirement> list = new ArrayList<>();

            try (Cursor cursor =
                 databaseHelper.getNonFunctionalRequirements(reunionId)) {

                while (cursor.moveToNext()) {
                    list.add(new NonFunctionalRequirement(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("reunion_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("create_at")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("importance_level")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("difficulty")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("time_required"))
                    ));
                }
            }

            requirements.postValue(list);
        });
    }

    public void insertRequirement(
        int reunionId,
        String description,
        int importance,
        int difficulty,
        double timeRequired
    ) {
        executor.execute(() -> {
            long result = databaseHelper.insertNonFunctionalRequirement(
                reunionId, description, importance, difficulty, timeRequired
            );

            operationResult.postValue(result);

            if (result != -1) {
                loadRequirements(reunionId);
            }
        });
    }

    public void updateRequirement(
        int id,
        int reunionId,
        String description,
        int importance,
        int difficulty,
        double timeRequired
    ) {
        executor.execute(() -> {
            int result = databaseHelper.updateNonFunctionalRequirement(
                id, description, importance, difficulty, timeRequired
            );

            operationResult.postValue((long) result);

            if (result > 0) {
                loadRequirements(reunionId);
            }
        });
    }

    public void deleteRequirement(int id, int reunionId) {
        executor.execute(() -> {
            int result = databaseHelper.deleteNonFunctionalRequirementById(id);
            operationResult.postValue((long) result);

            if (result > 0) {
                loadRequirements(reunionId);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
        databaseHelper.close();
    }
}
