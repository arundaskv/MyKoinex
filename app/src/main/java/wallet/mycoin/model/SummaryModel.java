package wallet.mycoin.model;

/**
 * Created by Arun.Das on 10-01-2018.
 */

public class SummaryModel {
    double totalVolume = 0.0;
    double unitPrice = 0.0;
    double totalPrice = 0.0;

    public double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
