package attinad.mykoinex.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Arun.Das on 15-12-2017.
 */
public class MyTicker implements Serializable {

    @SerializedName("bitcoin")
    @Expose
    private double bitcoin;
    @SerializedName("litecoin")
    @Expose
    private double litecoin;
    @SerializedName("ripple")
    @Expose
    private double ripple;
    @SerializedName("ethereum")
    @Expose
    private double ethereum;
    @SerializedName("bitcoincash")
    @Expose
    private double bitcoincash;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public double getBitcoin() {
        return bitcoin;
    }

    public void setBitcoin(double bitcoin) {
        this.bitcoin = bitcoin;
    }

    public double getLitecoin() {
        return litecoin;
    }

    public void setLitecoin(double litecoin) {
        this.litecoin = litecoin;
    }

    public double getRipple() {
        return ripple;
    }

    public void setRipple(double ripple) {
        this.ripple = ripple;
    }

    public double getEthereum() {
        return ethereum;
    }

    public void setEthereum(double ethereum) {
        this.ethereum = ethereum;
    }

    public double getBitcoincash() {
        return bitcoincash;
    }

    public void setBitcoincash(double bitcoincash) {
        this.bitcoincash = bitcoincash;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}