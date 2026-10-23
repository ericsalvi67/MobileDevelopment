
package com.salvi.TaskManagER.Entities;

public class Reunion {

    private int id;
    private String name;
    private String createAt;

    public Reunion(int id, String name, String createAt) {
        this.id = id;
        this.name = name;
        this.createAt = createAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
