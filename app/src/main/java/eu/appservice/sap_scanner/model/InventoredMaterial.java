package eu.appservice.sap_scanner.model;

import java.util.Date;

import eu.appservice.sap_scanner.Material;

/**
 * Created by luke on 28.03.15.
 */
public class InventoredMaterial {
    private Material material;
    private String inventoredDate;


    public InventoredMaterial(Material material,String inventoredDate) {
        this.material = material;
        this.inventoredDate=inventoredDate;


    }

    public InventoredMaterial() {
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getInventoredDate() {
        return inventoredDate;
    }

    public void setInventoredDate(String inventoredDate) {
        this.inventoredDate = inventoredDate;
    }

    @Override
    public String toString() {
        return "InventoredMaterial{" +
                "material=" + material +
                ", inventoredDate=" + inventoredDate +
                '}';
    }
}
