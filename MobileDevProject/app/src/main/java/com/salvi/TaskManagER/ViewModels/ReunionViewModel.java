
package com.salvi.TaskManagER.ViewModels;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.salvi.TaskManagER.Entities.Reunion;
import com.salvi.TaskManagER.Support.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReunionViewModel extends AndroidViewModel {

    private final DatabaseHelper databaseHelper;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Reunion>> reunions =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Long> operationResult =
            new MutableLiveData<>();

    public ReunionViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
    }

    public LiveData<List<Reunion>> getReunions() {
        return reunions;
    }

    public LiveData<Long> getOperationResult() {
        return operationResult;
    }

    public void loadReunions() {
        executor.execute(() -> {
            List<Reunion> list = new ArrayList<>();

            try (Cursor cursor = databaseHelper.getAllReunions()) {
                while (cursor.moveToNext()) {
                    list.add(new Reunion(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("create_at"))
                    ));
                }
            }

            reunions.postValue(list);
        });
    }

    public void insertReunion(String name) {
        executor.execute(() -> {
            long result = databaseHelper.insertReunion(name);
            operationResult.postValue(result);

            if (result != -1) {
                loadReunions();
            }
        });
    }

    public void updateReunion(int id, String name) {
        executor.execute(() -> {
            int result = databaseHelper.updateReunion(id, name);
            operationResult.postValue((long) result);

            if (result > 0) {
                loadReunions();
            }
        });
    }

    public void deleteReunion(int id) {
        executor.execute(() -> {
            int result = databaseHelper.deleteReunionById(id);
            operationResult.postValue((long) result);

            if (result > 0) {
                loadReunions();
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
