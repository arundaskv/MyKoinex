package wallet.mycoin;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import wallet.mycoin.memory.KoinexMemory;
import wallet.mycoin.model.UnitDepo;

public class AddUnitsActivity extends AppCompatActivity {

    EditText bitcoin;
    EditText litecoin;
    EditText ripple;
    EditText ethreum;
    EditText bitcoincash;

    EditText bitcoinDeposit;
    EditText litecoinDeposit;
    EditText rippleDeposit;
    EditText ethreumDeposit;
    EditText bitcoincashDeposit;

    EditText totalDeposit;

    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_units);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        displayValuesFromMemory();
    }

    private void displayValuesFromMemory() {
        UnitDepo unitDepo = KoinexMemory.getCoinUnitData(this);
        if (unitDepo != null) {
            bitcoin.setText(getStringFromDouble4Point(unitDepo.getBitcoinUnit()));
            litecoin.setText(getStringFromDouble4Point(unitDepo.getLitecoinUnit()));
            ripple.setText(getStringFromDouble4Point(unitDepo.getRippleUnit()));
            ethreum.setText(getStringFromDouble4Point(unitDepo.getEthereumUnit()));
            bitcoincash.setText(getStringFromDouble4Point(unitDepo.getBitcoincashUnit()));

            bitcoinDeposit.setText(getStringFromDouble(unitDepo.getBitcoinDeposit()));
            litecoinDeposit.setText(getStringFromDouble(unitDepo.getLitecoinDeposit()));
            rippleDeposit.setText(getStringFromDouble(unitDepo.getRippleDeposit()));
            ethreumDeposit.setText(getStringFromDouble(unitDepo.getEthereumDeposit()));
            bitcoincashDeposit.setText(getStringFromDouble(unitDepo.getBitcoincashDeposit()));

            totalDeposit.setText(getStringFromDouble(unitDepo.getTotalDeposit()));

        }else{
            bitcoin.setText("");
            litecoin.setText("");
            ripple.setText("");
            ethreum.setText("");
            bitcoincash.setText("");

            bitcoinDeposit.setText("");
            litecoinDeposit.setText("");
            rippleDeposit.setText("");
            ethreumDeposit.setText("");
            bitcoincashDeposit.setText("");

            totalDeposit.setText("");
        }
    }

    private String getStringFromDouble(double value) {
        String stringValue="0";
        try{
            stringValue = Double.toString(value);
        }
        catch (Exception e){

        }
        return stringValue;
    }
    private String getStringFromDouble4Point(double value) {
        String stringValue="0";
        try{
            stringValue = String.format("%.4f", value);
        }
        catch (Exception e){

        }
        return stringValue;
    }
    private void initView() {
        bitcoin = findViewById(R.id.bitcoinEtx);
        litecoin = findViewById(R.id.litecoinEtx);
        ripple = findViewById(R.id.rippleEtx);
        ethreum = findViewById(R.id.ethrEtx);
        bitcoincash = findViewById(R.id.bitcoincashEtx);

        bitcoinDeposit = findViewById(R.id.bitcoinDp);
        litecoinDeposit = findViewById(R.id.litecoinDp);
        rippleDeposit = findViewById(R.id.rippleDp);
        ethreumDeposit = findViewById(R.id.ethrDp);
        bitcoincashDeposit = findViewById(R.id.bitcoincashDp);

        totalDeposit = findViewById(R.id.totalDp);
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateAllFields()){
                    alertForSave();
                }else{
                    Toast.makeText(AddUnitsActivity.this, "Validation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bitcoin.clearFocus();
        litecoin.clearFocus();
        ripple.clearFocus();
        ethreum.clearFocus();
        bitcoincash.clearFocus();
        bitcoincashDeposit.clearFocus();
        ethreumDeposit.clearFocus();
        litecoinDeposit.clearFocus();
        rippleDeposit.clearFocus();
        bitcoinDeposit.clearFocus();
        totalDeposit.clearFocus();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void alertForSave() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Koinex Units");
        alertDialogBuilder.setMessage("Are you sure with the values ?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               saveAllData();
            }
        })
         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)  {

                    }
         });
          alertDialogBuilder.create().show();

    }

    private boolean validateAllFields(){

        if((bitcoin.getText().length()>0 && bitcoinDeposit.getText().length()==0)
                ||(bitcoin.getText().length()==0 && bitcoinDeposit.getText().length()>0))
            return false;

        if((litecoin.getText().length()>0 && litecoinDeposit.getText().length()==0)
                ||(litecoin.getText().length()==0 && litecoinDeposit.getText().length()>0))
            return false;

        if((ripple.getText().length()>0 && rippleDeposit.getText().length()==0)
                ||(ripple.getText().length()==0 && rippleDeposit.getText().length()>0))
            return false;

        if((ethreum.getText().length()>0 && ethreumDeposit.getText().length()==0)
                ||(ethreum.getText().length()==0 && ethreumDeposit.getText().length()>0))
            return false;

        if((bitcoincash.getText().length()>0 && bitcoincashDeposit.getText().length()==0)
                ||(bitcoincash.getText().length()==0 && bitcoincashDeposit.getText().length()>0))
            return false;

        if(totalDeposit.getText().length()==0)
            return false;

        if(getDoubleFromString(totalDeposit.getText().toString())==0){
            return false;
        }

        return true;
    }

    private void saveAllData(){

            UnitDepo unitDepo = new UnitDepo();

            double bitcoinUnit = getDoubleFromString(bitcoin.getText().toString());
            double bitcoinDp = getDoubleFromString(bitcoinDeposit.getText().toString());
            double litecoinUnit = getDoubleFromString(litecoin.getText().toString());
            double litecoinDp = getDoubleFromString(litecoinDeposit.getText().toString());
            double rippleUnit = getDoubleFromString(ripple.getText().toString());
            double rippleUnitDp = getDoubleFromString(rippleDeposit.getText().toString());
            double etherUnit = getDoubleFromString(ethreum.getText().toString());
            double etherDp = getDoubleFromString(ethreumDeposit.getText().toString());
            double bitcoinCash = getDoubleFromString(bitcoincash.getText().toString());
            double bitcoinCashDp = getDoubleFromString(bitcoincashDeposit.getText().toString());
            double totalDp = getDoubleFromString(totalDeposit.getText().toString());

            unitDepo.setBitcoinUnit(bitcoinUnit);
            unitDepo.setBitcoinDeposit(bitcoinDp);
            unitDepo.setLitecoinUnit(litecoinUnit);
            unitDepo.setLitecoinDeposit(litecoinDp);
            unitDepo.setRippleUnit(rippleUnit);
            unitDepo.setRippleDeposit(rippleUnitDp);
            unitDepo.setEthereumUnit(etherUnit);
            unitDepo.setEthereumDeposit(etherDp);
            unitDepo.setBitcoincashUnit(bitcoinCash);
            unitDepo.setBitcoincashDeposit(bitcoinCashDp);
            unitDepo.setTotalDeposit(totalDp);

            KoinexMemory.saveCoinUnitData(this,unitDepo);
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            finish();


    }

    private double getDoubleFromString(String text) {
        double value = 0;
        try{
            value = Double.parseDouble(text);
        }catch (Exception e){

        }
        return value;
    }
}
