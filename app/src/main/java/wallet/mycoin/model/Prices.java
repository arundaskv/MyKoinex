
package wallet.mycoin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prices {

    @SerializedName("BTC")
    @Expose
    private String bTC;
    @SerializedName("ETH")
    @Expose
    private String eTH;
    @SerializedName("BCH")
    @Expose
    private String bCH;
    @SerializedName("XRP")
    @Expose
    private String xRP;
    @SerializedName("LTC")
    @Expose
    private String lTC;
    @SerializedName("MIOTA")
    @Expose
    private Double mIOTA;
    @SerializedName("OMG")
    @Expose
    private Double oMG;
    @SerializedName("GNT")
    @Expose
    private Double gNT;

    public String getBTC() {
        return bTC;
    }

    public void setBTC(String bTC) {
        this.bTC = bTC;
    }

    public String getETH() {
        return eTH;
    }

    public void setETH(String eTH) {
        this.eTH = eTH;
    }

    public String getBCH() {
        return bCH;
    }

    public void setBCH(String bCH) {
        this.bCH = bCH;
    }

    public String getXRP() {
        return xRP;
    }

    public void setXRP(String xRP) {
        this.xRP = xRP;
    }

    public String getLTC() {
        return lTC;
    }

    public void setLTC(String lTC) {
        this.lTC = lTC;
    }

    public Double getMIOTA() {
        return mIOTA;
    }

    public void setMIOTA(Double mIOTA) {
        this.mIOTA = mIOTA;
    }

    public Double getOMG() {
        return oMG;
    }

    public void setOMG(Double oMG) {
        this.oMG = oMG;
    }

    public Double getGNT() {
        return gNT;
    }

    public void setGNT(Double gNT) {
        this.gNT = gNT;
    }

}
