package wallet.mycoin.csv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import wallet.mycoin.memory.KoinexMemory;
import wallet.mycoin.model.CoinType;
import wallet.mycoin.model.Transaction;
import wallet.mycoin.model.TransactionType;

/**
 * Created by Arun.Das on 03-01-2018.
 */

public class ImportCSVHandler {

    Context mContext;
    public void ReadCSVFromFile(Context context,String filePath){
        this.mContext = context;

        new ReadCSVExportToFirebase(context,filePath).execute();


    }

    private List<String[]> readFromCSV(String csvPath) {
        String next[] = {};
        List<String[]> list = new ArrayList<String[]>();
        try {
            File file = new File(csvPath);
            //CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file)));
            CSVReader reader = new CSVReader(new InputStreamReader(mContext.getAssets().open("test.csv")));
            for(;;) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private class ReadCSVExportToFirebase extends AsyncTask<Object,Boolean,Boolean>{

        Context context;
        String filePath;
        ProgressDialog pd;
        public ReadCSVExportToFirebase(Context context,String filePath){
            this.context = context;
            this.filePath = filePath;
            pd = new ProgressDialog(context);
            pd.setMessage("Importing Data from CSV and Updating to database");
            pd.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... objects) {

            DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference(KoinexMemory.getUserData(context).getUserid());
            List<String[]> listFromCSV = readFromCSV(filePath);
            boolean isValidationSuccess = false;
            if(listFromCSV.size()>0){
                isValidationSuccess = validateFile(listFromCSV);
            }
            if(isValidationSuccess){
                for(int i=1;i<listFromCSV.size();i++){
                    String[] CSV_tran = listFromCSV.get(i);
                    String key = mFirebaseDatabase.push().getKey();

                    Transaction transaction = new Transaction();
                    transaction.setKey(key);
                    transaction.setDate(getDateFromCSV(CSV_tran[0]));
                    transaction.setCoinType(getCoinTypeFromCSV(CSV_tran[1]));
                    transaction.setTransactionType(getTransactionTypeFromCSV(CSV_tran[2]));
                    transaction.setVolume(CSV_tran[3]);
                    transaction.setPriceUnit(CSV_tran[4]);
                    transaction.setPrice(CSV_tran[5]);
                    transaction.setFees(CSV_tran[7]);
                    transaction.setTotal(CSV_tran[8]);

                    mFirebaseDatabase.child(key).setValue(transaction);
                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
            }
            if(result){
                showAlertDialog("Import successful");
            }else{
                showAlertDialog("Import failed. Corrupted file");
            }
        }
    }


    private TransactionType getTransactionTypeFromCSV(String value) {
        if(value.equalsIgnoreCase("BUY"))
            return TransactionType.BUY;
        else
            return TransactionType.SELL;
    }

    private CoinType getCoinTypeFromCSV(String value) {
        if(value.startsWith("XRP"))
            return CoinType.XRP;
        if(value.startsWith("BTC"))
            return CoinType.BTC;
        if(value.startsWith("BCH"))
            return CoinType.BCH;
        if(value.startsWith("ETH"))
            return CoinType.ETH;
        if(value.startsWith("LTC"))
            return CoinType.LTC;
        return null;

    }

    private String getDateFromCSV(String value) {
        String[] splited = value.split("\\s+");
        return splited[0];
    }

    private boolean validateFile(List<String[]> listFromCSV) {
        String[] headers = listFromCSV.get(0);
        if(headers[0].equals("Timestamp") &&
                headers[1].equals("Pair") &&
                headers[2].equals("Type") &&
                headers[3].equals("Quantity") &&
                headers[4].equals("Price per unit") &&
                headers[5].equals("Amount") &&
                headers[6].equals("Fees Percentage") &&
                headers[7].equals("Fees") &&
                headers[8].equals("Total Amount")
                ) {
            return true;
        }
        else{
            return false;
        }
    }

    private void showAlertDialog(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Import from CSV");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
