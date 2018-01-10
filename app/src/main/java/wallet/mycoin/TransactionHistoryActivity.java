package wallet.mycoin;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import wallet.mycoin.api.Connectivity;
import wallet.mycoin.api.DBServer;
import wallet.mycoin.memory.KoinexMemory;
import wallet.mycoin.model.CoinType;
import wallet.mycoin.model.Transaction;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Transaction> transactionList;
    private TransactionAdapter adapter;
    private DatabaseReference mFirebaseDatabase;

    private ProgressBar progressBar;
    private AdView mAdView;

    FloatingActionButton fab;
    int filterId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        if(getIntent().hasExtra("filter")){
            filterId = getIntent().getIntExtra("filter",0);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupFab();
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        initAndshowAdView();
        transactionList = new ArrayList();
        adapter = new TransactionAdapter(TransactionHistoryActivity.this, transactionList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration( new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(KoinexMemory.getUserData(this).getUserid());

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transactionList.clear();
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    if(filterId==0) {
                        String key = childSnapShot.getKey();
                        Transaction transaction = childSnapShot.getValue(Transaction.class);
                        transaction.setKey(key);
                        transactionList.add(transaction);
                    }else{
                        String key = childSnapShot.getKey();
                        CoinType cointype = getCoinType(filterId);
                        if(cointype!=null){
                            Transaction transaction = childSnapShot.getValue(Transaction.class);
                            if(transaction.getCoinType()== cointype){
                                transaction.setKey(key);
                                transactionList.add(transaction);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if(transactionList.size()==0){
                    debugToast("No Transaction available");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private CoinType getCoinType(int filterId) {
        switch (filterId){
            case 1:
                return CoinType.BTC;
            case 2:
                return CoinType.LTC;
            case 3:
                return CoinType.XRP;
            case 4:
                return CoinType.ETH;
            case 5:
                return CoinType.BCH;
                default:return null;
        }

    }

    private void setupFab() {
        fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddTransactionPage();
            }
        });
    }
    private void openAddTransactionPage() {
        if(Connectivity.isConnected(this)){
            if(KoinexMemory.getUserData(this)!=null){
                Intent intent = new Intent(this,AddTransactionActivity.class);
                startActivity(intent);
            }else{
                debugToast("Sign in with Google for Transactions");
            }
        }else{
            Toast.makeText(this, "No Network Connectivity", Toast.LENGTH_SHORT).show();
        }
    }
    private void initAndshowAdView() {
        try{
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void debugToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
