package wallet.mycoin;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Arun.Das on 03-01-2018.
 */

public class PocketApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
