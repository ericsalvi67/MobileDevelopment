
package com.salvi.TaskManagER.Entities;

public class Reunion {

    public int id;
    public String name;
    public String startDate;
    public String endDate;
    public String createAt;

    public Reunion(int id, String name, String startDate, String endDate, String createAt) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createAt = createAt;
    }
}
