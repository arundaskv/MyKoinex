package attinad.mykoinex.api;

import attinad.mykoinex.model.Ticker;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Arun.Das on 14-12-2017.
 */

public interface TickerClient {

    String TICKER_API_URL = "/ticker";
//https://koinex.in/api/ticker

    @GET(TICKER_API_URL)
    void getLatestTicker(Callback<Ticker> mCallback);

}
