package wallet.mycoin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wallet.mycoin.model.Transaction;

/**
 * Created by Arun.Das on 22-12-2017.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    public Context mContext;
    public List<Transaction> transactions;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText, coin_type, tr_type, volumeTxt, unitPriceTxt, priceTxt,feesTxt,totalTxt;


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
        Transaction transaction = transactions.get(position);
        holder.dateText.setText(transaction.getDate());
        holder.coin_type.setText(transaction.getCoinType().toString());
        holder.tr_type.setText(transaction.getTransactionType().toString());
        holder.volumeTxt.setText(transaction.getVolume());
        holder.unitPriceTxt.setText(transaction.getPriceUnit());
        holder.priceTxt.setText(transaction.getPrice());
        holder.feesTxt.setText(transaction.getFees());
        holder.totalTxt.setText(transaction.getTotal());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
