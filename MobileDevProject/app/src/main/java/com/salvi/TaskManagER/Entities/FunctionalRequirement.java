
package com.salvi.TaskManagER.Entities;

public class FunctionalRequirement {

    private int id;
    private int reunionId;
    private String description;
    private String createAt;
    private int importanceLevel;
    private int difficulty;
    private double timeRequired;

    public FunctionalRequirement(
            int id,
            int reunionId,
            String description,
            String createAt,
            int importanceLevel,
            int difficulty,
            double timeRequired
    ) {
        this.id = id;
        this.reunionId = reunionId;
        this.description = description;
        this.createAt = createAt;
        this.importanceLevel = importanceLevel;
        this.difficulty = difficulty;
        this.timeRequired = timeRequired;
    }

    public int getId() { return id; }
    public int getReunionId() { return reunionId; }
    public String getDescription() { return description; }
    public String getCreateAt() { return createAt; }
    public int getImportanceLevel() { return importanceLevel; }
    public int getDifficulty() { return difficulty; }
    public double getTimeRequired() { return timeRequired; }

    public void setId(int id) { this.id = id; }
    public void setReunionId(int reunionId) { this.reunionId = reunionId; }
    public void setDescription(String description) { this.description = description; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
    public void setImportanceLevel(int importanceLevel) {
        this.importanceLevel = importanceLevel;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    public void setTimeRequired(double timeRequired) {
        this.timeRequired = timeRequired;
    }
}
