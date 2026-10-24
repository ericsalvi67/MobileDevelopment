
package com.salvi.TaskManagER.Entities;

public class NonFunctionalRequirement {

    public int id;
    public int reunionId;
    public String description;
    public String createAt;
    public int importanceLevel;
    public int difficulty;
    public double timeRequired;

    public NonFunctionalRequirement(
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
}
