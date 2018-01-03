package wallet.mycoin;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Created by arundas on 25/10/17.
 */

public class PermissionUtilities {



    private PermissionCallback permissionCallback;
    /*
    *
    * <p>
    * Sample Constants
    * public int requestCode = 200;
    * public String permission = Manifest.permission.CAMERA;
    * public String[] permissionArray = new String[]{CAMERA_PERMISSION};
    * </p>
    *
    *
    * @param context Activity instance
    * @param rootView root container layout object
    * @param permissionArray string array that contains list of required permissions
    * @param permission string required permission
    * @param requestCode permission request code
    * @param permissionCallback callback object
    * @param onPermissionDeniedMessage message text on permission denial
    */
    public void checkPermission (Activity context,
                                       View rootView,
                                       String[] permissionArray,
                                       String permission,
                                       int requestCode,
                                       PermissionCallback permissionCallback,
                                       String onPermissionDeniedMessage){
        this.permissionCallback = permissionCallback;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1){
            permissionCallback.onSuccess();
        }else{
            if(ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED){
                permissionCallback.onSuccess();
            }else {
                if(isPermissionAskedOnce(context,permission)){
                    showMessage(context,rootView,onPermissionDeniedMessage);
                }else{
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context,permission)) {
                        setPermissionAskedOnce(context,true,permission);
                        ActivityCompat.requestPermissions(context,permissionArray, requestCode);
                    }else{
                        ActivityCompat.requestPermissions(context,permissionArray, requestCode);
                    }
                }
            }

        }
    }

    private void showMessage(final Activity context, View rootView, String onPermissionDeniedMessage) {
        if(rootView!=null){
            Snackbar snackbar = Snackbar
                    .make(rootView, onPermissionDeniedMessage, Snackbar.LENGTH_LONG)
                    .setAction("Open Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    });
            View snackbarView = snackbar.getView();
            TextView snackTextView = (TextView) snackbarView
                    .findViewById(android.support.design.R.id.snackbar_text);
            snackTextView.setMaxLines(5);
            snackbar.show();
        }

    }

    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        if(grantResults != null && grantResults.length>0){
            boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(cameraAccepted) {
                permissionCallback.onSuccess();
            }
            else{
                permissionCallback.onFailure();
            }
        }
    }

    public interface PermissionCallback {

        void onSuccess();
        void onFailure();
    }

    private void setPermissionAskedOnce(Activity context,boolean isAsked,String permissionkey){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Permission",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(permissionkey,isAsked);
        editor.apply();
    }

    private boolean isPermissionAskedOnce(Activity context,String permissionkey){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Permission",context.MODE_PRIVATE);
        boolean isAsked = sharedPreferences.getBoolean(permissionkey, false);
        return isAsked;
    }

}
