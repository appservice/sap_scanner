package eu.appservice.sap_scanner;

import java.io.Serializable;

public class PlantStrucMpk implements Serializable{
    private int id;
    private int parent_id;
    private int view_id;
    private String name;
    private String value;
    private String budget;


    public PlantStrucMpk(int parent_id, int view_id, String name, String value) {
        //this.id = id;
        this.parent_id = parent_id;
        this.view_id = view_id;
        this.name = name;
        this.value = value;
    }

    public PlantStrucMpk() {

    }

    public PlantStrucMpk(int parentId, int view_id, String name, String value, String budget) {
        super();

        this.parent_id = parentId;
        this.view_id = view_id;
        this.name = name;
        this.value = value;
        this.budget = budget;
    }

    public PlantStrucMpk(int id, int parent_id, int view_id, String name, String value, String budget) {
        super();
        this.id = id;
        this.parent_id = parent_id;
        this.view_id = view_id;
        this.name = name;
        this.value = value;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public int getView_id() {
        return view_id;
    }

    /**
     * @return the budget
     */
    public String getBudget() {
        return budget;
    }

    @Override
    public String toString() {

        return id + " " + parent_id + " " + view_id + " " + name + " " + value + " " + budget;
    }


}
