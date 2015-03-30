package eu.appservice.sap_scanner;

import java.io.Serializable;

public class CollectedMaterial extends Material implements Serializable {

    /**
     * @author mochelek 01-03-2013
     */
    private static final long serialVersionUID = 5288781473728302727L;
    private Double collectedQuantity;
    private String mpk;
    private String budget;
    private String date;

    private boolean isToZero;
    private String signAddress;


    /**
     *@author mochelek 01-03-2013
     * empty constructor
     */
    public CollectedMaterial() {
        super();

    }

    /**
     * @author mochelek 01-03-2013
     * @param material material which is collected
     * @param budget budget from
     * @param date date of material collection
     * @param mpk place (machine or production line) which generate this costs
     * @param collectedQuantity collected quantity
     * @param signAddress address where will be save signature of collector
     *
     */
    public CollectedMaterial(Material material, Double collectedQuantity, String budget, String mpk, boolean isToZero, String date, String signAddress) {
        // this.material=material;
        super(material.getId(), material.getIndex(), material.getName(), material.getUnit(), material.getAmount(), material.getStore(), material.getDescription());

        this.collectedQuantity = collectedQuantity;
        this.budget = budget;
        this.mpk = mpk;
        this.date = date;
        this.isToZero = isToZero;
        this.signAddress = signAddress;

    }


    public String getSignAddress() {
        return signAddress;
    }

    public void setSignAddress(String signAddress) {
        this.signAddress = signAddress;
    }

    public boolean isToZero() {
        return isToZero;
    }

    public void setToZero(boolean toZero) {
        isToZero = toZero;
    }


    public Double getCollectedQuantity() {
        return collectedQuantity;
    }

    /**
     * @param collectedQuantity the collectedQuantity to set
     */
    public void setCollectedQuantity(Double collectedQuantity) {
        this.collectedQuantity = collectedQuantity;
    }

    /**
     * @return the mpk
     */
    public String getMpk() {
        return mpk;
    }

    /**
     * @param mpk the mpk to set
     */
    public void setMpk(String mpk) {
        this.mpk = mpk;
    }

    /**
     * @return the budget
     */
    public String getBudget() {
        return budget;
    }

    /**
     * @param budget the budget to set
     */
    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getDate() {
        return date;
    }






    @Override
    public String toString() {
        return new StringBuilder().append("id:").append(getId())
                                    .append(" ").append(getIndex())
                                    .append(" ").append(getName())
                                    .append(" ").append("ilość ").append(collectedQuantity)
                                    .append(" ").append(mpk).append(" ")
                                    .append(budget).append(" ")
                                    .append("czy na zero: ").append(isToZero())
                                    .append(" pobrane ").append(date)
                                    .append("\n").toString() ;
    }


/*    public boolean saveToDatabase() {

        return false;
    }*/
}
