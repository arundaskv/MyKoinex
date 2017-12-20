package wallet.mycoin.model;

/**
 * Created by Arun.Das on 15-12-2017.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnitDepo {

    @SerializedName("bitcoin_unit")
    @Expose
    private double bitcoinUnit;
    @SerializedName("bitcoin_deposit")
    @Expose
    private double bitcoinDeposit;
    @SerializedName("litecoin_unit")
    @Expose
    private double litecoinUnit;
    @SerializedName("litecoin_deposit")
    @Expose
    private double litecoinDeposit;
    @SerializedName("ripple_unit")
    @Expose
    private double rippleUnit;
    @SerializedName("ripple_deposit")
    @Expose
    private double rippleDeposit;
    @SerializedName("ethereum_unit")
    @Expose
    private double ethereumUnit;
    @SerializedName("ethereum_deposit")
    @Expose
    private double ethereumDeposit;
    @SerializedName("bitcoincash_unit")
    @Expose
    private double bitcoincashUnit;
    @SerializedName("bitcoincash_deposit")
    @Expose
    private double bitcoincashDeposit;
    @SerializedName("total_deposit")
    @Expose
    private double totalDeposit;

    public double getBitcoinUnit() {
        return bitcoinUnit;
    }

    public void setBitcoinUnit(double bitcoinUnit) {
        this.bitcoinUnit = bitcoinUnit;
    }

    public double getBitcoinDeposit() {
        return bitcoinDeposit;
    }

    public void setBitcoinDeposit(double bitcoinDeposit) {
        this.bitcoinDeposit = bitcoinDeposit;
    }

    public double getLitecoinUnit() {
        return litecoinUnit;
    }

    public void setLitecoinUnit(double litecoinUnit) {
        this.litecoinUnit = litecoinUnit;
    }

    public double getLitecoinDeposit() {
        return litecoinDeposit;
    }

    public void setLitecoinDeposit(double litecoinDeposit) {
        this.litecoinDeposit = litecoinDeposit;
    }

    public double getRippleUnit() {
        return rippleUnit;
    }

    public void setRippleUnit(double rippleUnit) {
        this.rippleUnit = rippleUnit;
    }

    public double getRippleDeposit() {
        return rippleDeposit;
    }

    public void setRippleDeposit(double rippleDeposit) {
        this.rippleDeposit = rippleDeposit;
    }

    public double getEthereumUnit() {
        return ethereumUnit;
    }

    public void setEthereumUnit(double ethereumUnit) {
        this.ethereumUnit = ethereumUnit;
    }

    public double getEthereumDeposit() {
        return ethereumDeposit;
    }

    public void setEthereumDeposit(double ethereumDeposit) {
        this.ethereumDeposit = ethereumDeposit;
    }

    public double getBitcoincashUnit() {
        return bitcoincashUnit;
    }

    public void setBitcoincashUnit(double bitcoincashUnit) {
        this.bitcoincashUnit = bitcoincashUnit;
    }

    public double getBitcoincashDeposit() {
        return bitcoincashDeposit;
    }

    public void setBitcoincashDeposit(double bitcoincashDeposit) {
        this.bitcoincashDeposit = bitcoincashDeposit;
    }

    public double getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(double totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

}