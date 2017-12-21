package wallet.mycoin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class AddTransactionActivity extends AppCompatActivity {

    Switch tranasctionType;
    TextView dateTxt, totalTxt,priceTxt;
    EditText volumeEtx, unitPriceEtx, feesEtx;
    Button saveBtn;
    ImageButton dateImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        tranasctionType = findViewById(R.id.transaction_type);
        tranasctionType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    compoundButton.setText("Sell");
                }else{
                    compoundButton.setText("Buy");
                }
            }
        });
    }

    private void initView() {
        tranasctionType = findViewById(R.id.transaction_type);
        saveBtn = findViewById(R.id.transaction_save);
        dateTxt = findViewById(R.id.dateTxtView);
        unitPriceEtx = findViewById(R.id.priceUnitEtx);
        volumeEtx = findViewById(R.id.volumeEtx);
        priceTxt = findViewById(R.id.priceEtx);
        feesEtx = findViewById(R.id.feesEtx);
        totalTxt = findViewById(R.id.totalTxt);
        dateImg = findViewById(R.id.dateImg);

        dateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog dialog = new DatePickerDialog(AddTransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear+1;
                        if(  year <= calendar.get(Calendar.YEAR)
                                && monthOfYear <= calendar.get(Calendar.MONTH)
                                && dayOfMonth <= calendar.get(Calendar.DAY_OF_MONTH)){
                            dateTxt.setText(""+dayOfMonth+"-"+month+"-"+year);

                        }else{
                            debugToast("Invalid Date");
                        }
                    }
                },
               calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
               calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });



        unitPriceEtx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(isFocused){
                    if(getDoubleFromString(unitPriceEtx.getText().toString())!=0){
                        unitPriceEtx.setSelectAllOnFocus(false);
                    }else{
                        unitPriceEtx.setSelectAllOnFocus(true);
                    }
                }
                if (!isFocused) {
                    double volume = 0;
                    double unitPrice = 0;
                    double price =0;
                    double fees = 0;
                    if (volumeEtx != null
                            && !TextUtils.isEmpty(volumeEtx.getText().toString())
                            && getDoubleFromString(volumeEtx.getText().toString()) != 0) {

                        volume = getDoubleFromString(volumeEtx.getText().toString());

                        if (!TextUtils.isEmpty(unitPriceEtx.getText().toString())
                                && getDoubleFromString(unitPriceEtx.getText().toString()) != 0) {
                            unitPrice = getDoubleFromString(unitPriceEtx.getText().toString());

                            price = volume * unitPrice;

                        } else {
                            debugToast("Unit Price is mandatory");
                        }

                    } else {
                        debugToast("Volume is mandatory");
                    }
                    priceTxt.setText("" + price);

                    double totalPrice = 0;
                    if(feesEtx!= null
                            && !TextUtils.isEmpty(feesEtx.getText().toString())
                            && getDoubleFromString(feesEtx.getText().toString()) != 0){
                        fees = getDoubleFromString(feesEtx.getText().toString());
                        totalPrice = fees + price;
                        totalTxt.setText("" + totalPrice);
                    }
                }
            }
        });

        volumeEtx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(isFocused){
                    if(getDoubleFromString(volumeEtx.getText().toString())!=0){
                        volumeEtx.setSelectAllOnFocus(false);
                    }else{
                        volumeEtx.setSelectAllOnFocus(true);
                    }
                }
                if(!isFocused){
                    double volume = 0;
                    double unitPrice = 0;
                    double price =0;
                    double fees = 0;
                    if (unitPriceEtx != null
                            && !TextUtils.isEmpty(unitPriceEtx.getText().toString())
                            && getDoubleFromString(unitPriceEtx.getText().toString()) != 0) {

                        unitPrice = getDoubleFromString(unitPriceEtx.getText().toString());

                        if (!TextUtils.isEmpty(volumeEtx.getText().toString())
                                && getDoubleFromString(volumeEtx.getText().toString()) != 0) {

                            volume = getDoubleFromString(volumeEtx.getText().toString());
                            price = volume * unitPrice;

                        } else {
                            debugToast("Volume is mandatory");
                        }

                    }
                    priceTxt.setText("" + price);

                    double totalPrice = 0;
                    if(feesEtx!= null
                            && !TextUtils.isEmpty(feesEtx.getText().toString())
                            && getDoubleFromString(feesEtx.getText().toString()) != 0){
                        fees = getDoubleFromString(feesEtx.getText().toString());
                        totalPrice = fees + price;
                        totalTxt.setText("" + totalPrice);
                    }



                }
            }
        });
        feesEtx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(isFocused){
                    if(getDoubleFromString(feesEtx.getText().toString())!=0){
                        feesEtx.setSelectAllOnFocus(false);
                    }else{
                        feesEtx.setSelectAllOnFocus(true);
                    }
                }
                if(!isFocused){
                    double fees = 0;
                    double price =0;
                    double totalPrice = 0;
                    if (priceTxt != null
                            && !TextUtils.isEmpty(priceTxt.getText().toString())
                            && getDoubleFromString(priceTxt.getText().toString()) != 0) {

                        price = getDoubleFromString(priceTxt.getText().toString());

                        if (!TextUtils.isEmpty(feesEtx.getText().toString())
                                && getDoubleFromString(feesEtx.getText().toString()) != 0) {

                            fees = getDoubleFromString(feesEtx.getText().toString());
                            totalPrice = fees + price;

                        } else {
                            debugToast("Fees value is mandatory");
                        }

                    }else {
                        debugToast("Price value is mandatory");
                    }
                    totalTxt.setText("" + totalPrice);
                }
            }
        });



    }
    private void debugToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
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
