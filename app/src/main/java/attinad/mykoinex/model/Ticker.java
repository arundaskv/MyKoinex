
package attinad.mykoinex.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Ticker implements Serializable{

    @SerializedName("prices")
    @Expose
    private Prices prices;
    @SerializedName("stats")
    @Expose
    private Stats stats;

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

}
