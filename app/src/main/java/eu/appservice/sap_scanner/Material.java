package eu.appservice.sap_scanner;

import java.io.Serializable;


public class Material implements Serializable,Cloneable {


    /**
     *
     */
    private static final long serialVersionUID = -2613905129574352363L;
    private int id;
    private String name;
    private String index;
    private String unit;
    private double amount;
    private String store;
    private String description;


    public Material() {
        super();

    }

    public Material(int id, String index, String name, String unit,
                    double amount, String store, String description) {
        super();
        this.id = id;
        this.index = index;
        this.name = name;

        this.unit = unit;
        this.amount = amount;
        this.store = store;
        this.description = description;
    }


    public Material(String index, String name, String unit, double amount,
                    String store, String description) {
        super();
        this.name = name;
        this.index = index;
        this.unit = unit;
        this.amount = amount;
        this.store = store;
        this.description = description;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return the store
     */
    public String getStore() {
        return store;
    }

    /**
     * @param store the store to set
     */
    public void setStore(String store) {
        this.store = store;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return index + " " + name + " skład: " + store + " " + unit + " " + amount;//+" "+description;
    }

    public String[] getPosition() {
        String[] tab = new String[3];
        if (index.length() == 16 && index.regionMatches(0, "079989", 0, 6)) {
            tab[0] = index.substring(6, 8);
            tab[1] = index.substring(8, 11).replaceAll("^(00)|^0", "");
            tab[2] = index.substring(11, 13);
            return tab;
        }
        return null;
    }

    public void removeValue(double removedValue) {
        this.amount = this.amount - removedValue;
    }

    /**
     * @return splitted by "-" (when is 16 digits index as is used in CPGL)</br>
     * or by " " index (wnen is used on CPGL when starts CZ-AI...-18 chars)
     */
    public String getSplittedIndexByCpglRegule() {
        String index_id = this.getIndex();
        switch (index_id.length()) {
            case 16:

                return index_id.substring(0, 4) + "-" + index_id.substring(4, 8) + "-" + index_id.substring(8, 12) + "-" + index_id.substring(12, 16);//indexIdTab[1];

            case 18:
                if (index_id.substring(0, 4).equals("CZ-A")) {
                    return index_id.substring(0, 5) + " " + index_id.substring(5, 10) + " " + index_id.substring(10, 14) + " " + index_id.substring(14, 18);

                } else {
                    break;
                }

        }

        return index_id;
    }

    @Override
    public Material clone() {
        try{
        return (Material)super.clone();
        }catch(CloneNotSupportedException ex){
        }
        return null;
    }

    public String getDataToEncodeQrCode(){
        return index+";"+name+";"+unit+";"+store;
    }
}