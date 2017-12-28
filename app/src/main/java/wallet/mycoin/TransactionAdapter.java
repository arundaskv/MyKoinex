package wallet.mycoin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import wallet.mycoin.api.Connectivity;
import wallet.mycoin.memory.KoinexMemory;
import wallet.mycoin.model.Transaction;

/**
 * Created by Arun.Das on 22-12-2017.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    public Context mContext;
    public List<Transaction> transactions;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText, coin_type, tr_type, volumeTxt, unitPriceTxt, priceTxt,feesTxt,totalTxt;
        public Toolbar toolbar;


        public MyViewHolder(View view) {
            super(view);
            dateText = (TextView) view.findViewById(R.id.dateText);
            coin_type = (TextView) view.findViewById(R.id.coin_type);
            tr_type = (TextView) view.findViewById(R.id.tr_type);
            volumeTxt = (TextView) view.findViewById(R.id.volumeTxt);
            unitPriceTxt = (TextView) view.findViewById(R.id.unitPriceTxt);
            priceTxt = (TextView) view.findViewById(R.id.priceTxt);
            feesTxt = (TextView) view.findViewById(R.id.feesTxt);
            totalTxt = (TextView) view.findViewById(R.id.totalTxt);
            toolbar = view.findViewById(R.id.card_toolbar);
            toolbar.inflateMenu(R.menu.card_menu);
        }

    }

    public TransactionAdapter(Context mContext, List<Transaction> transactions) {
        this.mContext = mContext;
        this.transactions = transactions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_history_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Transaction transaction = transactions.get(position);
        holder.dateText.setText(transaction.getDate());
        holder.coin_type.setText(transaction.getCoinType().toString());
        holder.tr_type.setText(transaction.getTransactionType().toString());
        holder.volumeTxt.setText(getStringFromDouble4Point(Double.parseDouble(transaction.getVolume())));
        holder.unitPriceTxt.setText(getStringFromDouble2Point(Double.parseDouble(transaction.getPriceUnit())));
        holder.priceTxt.setText(getStringFromDouble2Point(Double.parseDouble(transaction.getPrice())));
        holder.feesTxt.setText(getStringFromDouble2Point(Double.parseDouble(transaction.getFees())));
        holder.totalTxt.setText(getStringFromDouble2Point(Double.parseDouble(transaction.getTotal())));


        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                        openAddTransactionPage(transaction);
                        break;

                    case R.id.delete:
                        alertForDelete(transaction);
                        break;

                        default:

                }
                return false;
            }});
    }

    private void alertForDelete(final Transaction transaction) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Confirm Delete");
        alertDialogBuilder.setMessage("Are you sure with deleting this entry ?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delete(transaction);
            }

        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)  {

                    }
                });
        alertDialogBuilder.create().show();
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    private void openAddTransactionPage(Transaction transaction) {
        if(KoinexMemory.getUserData(mContext)!=null){
            Intent intent = new Intent(mContext,AddTransactionActivity.class);
            intent.putExtra("transaction",transaction);
            intent.putExtra("isDelete",false);
            mContext.startActivity(intent);
        }
    }
    private void delete(Transaction transaction) {
        if(Connectivity.isConnected(mContext)){
            FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseInstance.setPersistenceEnabled(true);
            DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(KoinexMemory.getUserData(mContext).getUserid());
            mFirebaseDatabase.child(transaction.getKey()).removeValue();
        }
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

    private String getStringFromDouble2Point(double value) {
        String stringValue="0";
        try{
            stringValue = String.format("%.2f", value);
        }
        catch (Exception e){

        }
        return stringValue;
    }
}
