package wallet.mycoin;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import wallet.mycoin.csv.ImportCSVHandler;


public class SettingsActivitiy extends AppCompatActivity {



    private static final int PICKFILE_REQUEST_CODE = 100;
    LinearLayout linearLayout;
    RelativeLayout rootLayout;
    Toolbar toolbar;
     public int requestCode = 200;
     public String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
     public String[] permissionArray = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
    PermissionUtilities.PermissionCallback permissionCallback;
    PermissionUtilities permissionUtilities = new PermissionUtilities();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        setupToolbar();
        initView();
    }

    private void initView() {
        linearLayout = findViewById(R.id.importLayout);
        rootLayout = findViewById(R.id.rootLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionUtilities.checkPermission(SettingsActivitiy.this,rootLayout,permissionArray,permission,requestCode, permissionCallback,"Read External Storage permission is required to import data from storage");
            }
        });
        permissionCallback = new PermissionUtilities.PermissionCallback() {
            @Override
            public void onSuccess() {
                importCSVHere();
            }

            @Override
            public void onFailure() {

            }
        };
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void setupToolbar() {
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void importCSVHere(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/csv");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICKFILE_REQUEST_CODE && resultCode==RESULT_OK){
            String Fpath = data.getDataString();
            if(Fpath.split("\\.")[1].equalsIgnoreCase("csv")){
                new ImportCSVHandler().ReadCSVFromFile(SettingsActivitiy.this,Fpath);
            }else{
                Toast.makeText(this, "Format not supported", Toast.LENGTH_SHORT).show();
            }

        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
