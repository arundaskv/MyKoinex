package wallet.mycoin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivitiy extends Activity {

    private static final int PICKFILE_REQUEST_CODE = 100;
    static Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = SettingsActivitiy.this;        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.import_csv);

            final Preference importPreference = getPreferenceManager().findPreference("import_csv");
            importPreference.setSummary("Feature Coming");
            importPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //importCSVHere();
                    return false;
                }
            });

        }
    }

    private static void importCSVHere(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/csv");
        context.startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICKFILE_REQUEST_CODE && resultCode==RESULT_OK){
            String Fpath = data.getDataString();
            if(Fpath.split("\\.")[1].equalsIgnoreCase("csv")){
                Toast.makeText(context, ""+Fpath, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Format not supported", Toast.LENGTH_SHORT).show();
            }

        }

    }


}
