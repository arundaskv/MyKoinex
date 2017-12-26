package wallet.mycoin;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import wallet.mycoin.api.DBServer;
import wallet.mycoin.memory.KoinexMemory;
import wallet.mycoin.model.CoinType;
import wallet.mycoin.model.Transaction;
import wallet.mycoin.model.TransactionType;

public class AddTransactionActivity extends AppCompatActivity {

    Switch tranasctionType;
    TextView dateTxt, totalTxt,priceTxt;
    EditText volumeEtx, unitPriceEtx, feesEtx;
    Button saveBtn;
    ImageButton dateImg;
    Spinner coinSpinner;
    boolean isUpdate=false;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ArrayList<Transaction> transactions = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        restoreIfAny();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(KoinexMemory.getUserData(this).getUserid());

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transactions.clear();
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    Transaction transaction = childSnapShot.getValue(Transaction.class);
                    transactions.add(transaction);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });





    }

    private void restoreIfAny() {
        if(getIntent().hasExtra("transaction")){
            isUpdate = true;
            Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");

            if(transaction.getTransactionType()==TransactionType.SELL){
                tranasctionType.setChecked(true);
            }else{
                tranasctionType.setChecked(false);
            }
            tranasctionType.setEnabled(false);

            dateTxt.setText(transaction.getDate());
            unitPriceEtx .setText(transaction.getPriceUnit());
            volumeEtx .setText(transaction.getVolume());
            priceTxt .setText(transaction.getPrice());
            feesEtx .setText(transaction.getFees());
            totalTxt .setText(transaction.getTotal());
            coinSpinner.setSelection(getSelection(transaction.getCoinType()));
        }
    }

    private int getSelection(CoinType coinType) {
        switch (coinType){
            case BTC:
                return 0;
            case LTC:
                return 1;
            case XRP:
                return 2;
            case ETH:
                return 3;
            case BCH:
                return 4;
                default:
                    return 0;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        coinSpinner = findViewById(R.id.coinSpinner);
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

        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        dateTxt.setText(""+Calendar.DAY_OF_MONTH+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR));

        dateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validationSuccess()){
                    alertForSave();
                }
            }


        });

    }
    private void alertForSave() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Transaction");
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


    private void saveAllData() {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(getTransactionType());
        transaction.setCoinType(getCoinType());
        transaction.setDate(dateTxt.getText().toString());
        transaction.setVolume(volumeEtx.getText().toString());
        transaction.setPriceUnit(unitPriceEtx.getText().toString());
        transaction.setPrice(priceTxt.getText().toString());
        transaction.setFees(feesEtx.getText().toString());
        transaction.setTotal(totalTxt.getText().toString());
        if(!isUpdate){
            String key = mFirebaseDatabase.push().getKey();
            mFirebaseDatabase.child(key).setValue(transaction);
        }else{
            Transaction trans = (Transaction) getIntent().getSerializableExtra("transaction");
            String key =trans.getKey() ;
            mFirebaseDatabase.child(key).setValue(transaction);
        }

        finish();

    }

    private TransactionType getTransactionType() {
        if(tranasctionType.getText().toString().equalsIgnoreCase("Buy")){
            return TransactionType.BUY;
        }
        return TransactionType.SELL;
    }

    private CoinType getCoinType() {
        if(coinSpinner.getSelectedItem().toString().toString().equalsIgnoreCase("BTC")){
            return CoinType.BTC;
        }else if(coinSpinner.getSelectedItem().toString().toString().equalsIgnoreCase("LTC")){
            return CoinType.LTC;
        }else if(coinSpinner.getSelectedItem().toString().toString().equalsIgnoreCase("XRP")){
            return CoinType.XRP;
        }else if(coinSpinner.getSelectedItem().toString().toString().equalsIgnoreCase("ETH")){
            return CoinType.ETH;
        }else if(coinSpinner.getSelectedItem().toString().toString().equalsIgnoreCase("BCH")){
            return CoinType.BCH;
        }
        return null;
    }


    private boolean validationSuccess() {
        return true;
    }

    private void debugToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    private double getDoubleFromString4Points(String text) {
        double value = 0;
        try{
            value = Double.parseDouble(text);
        }catch (Exception e){

        }
        return value;
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
