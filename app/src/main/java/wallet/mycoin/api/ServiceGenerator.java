package wallet.mycoin.api;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by ANSHAD on 23/7/15.
 */
public class ServiceGenerator {
    private static final String BASE_URL = "https://koinex.in/api";

    // No need to instantiate this class.
    private ServiceGenerator() {
    }
    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        mOkHttpClient.setConnectTimeout(50, TimeUnit.SECONDS);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(mOkHttpClient));
        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }
}
