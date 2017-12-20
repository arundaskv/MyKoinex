package attinad.mykoinex;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import attinad.mykoinex.api.ServiceGenerator;
import attinad.mykoinex.api.TickerClient;
import attinad.mykoinex.memory.KoinexMemory;
import attinad.mykoinex.model.MyTicker;
import attinad.mykoinex.model.Ticker;
import attinad.mykoinex.model.UnitDepo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends AppCompatActivity {

    TextView bitcoin;
    TextView bitcoinCash;
    TextView litecoin;
    TextView ether;
    TextView ripple;
    TextView timestamp;
    Toolbar toolbar;
    FloatingActionButton fab;

    TextView bitcoinUnit;
    TextView litecoinUnit;
    TextView rippleUnit;
    TextView ethereumUnit;
    TextView bitcoincashUnit;

    TextView bitcoinBalance;
    TextView litecoinBalance;
    TextView rippleBalance;
    TextView ethereumBalance;
    TextView bitcoincashBalance;

    TextView bitcoinDeposit;
    TextView litecoinDeposit;
    TextView rippleDeposit;
    TextView ethereumDeposit;
    TextView bitcoincashDeposit;

    TextView bitcoinProfit;
    TextView litecoinProfit;
    TextView rippleProfit;
    TextView ethereumProfit;
    TextView bitcoincashProfit;

    TextView totalDeposit;
    TextView totalBalance;
    TextView totalProfit;
    TextView percentText;

    boolean isRefreshEnabled = true;
    int countdownSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        restore();
        setupToolbar();
        setupFab();
        getTicker();
    }

    private void restore() {
        restoreOldTickerValues();
        updateUnitDeposit();
        updateBalanceProfitInfo();
    }

    private void restoreOldTickerValues() {
        MyTicker ticker = KoinexMemory.getMyTickerData(this);
        if(ticker !=null) {
            bitcoin.setText(" ₹" + ticker.getBitcoin());
            bitcoinCash.setText(" ₹" + ticker.getBitcoincash());
            ether.setText(" ₹" + ticker.getEthereum());
            litecoin.setText(" ₹" + ticker.getLitecoin());
            ripple.setText(" ₹" + ticker.getRipple());
            timestamp.setText(ticker.getTimestamp());
        }
    }

    private void setupFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(HomeActivity.this,AddUnitsActivity.class);
               startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Koinex Wallet");
    }

    private void initView() {
        bitcoin = findViewById(R.id.bitcoinValue);
        bitcoinCash = findViewById(R.id.bitcoinCashValue);
        ether = findViewById(R.id.etherValue);
        ripple = findViewById(R.id.rippleValue);
        litecoin = findViewById(R.id.liteValue);
        timestamp = findViewById(R.id.timestamp);

        bitcoinUnit = findViewById(R.id.bitcoinUnit);
        bitcoincashUnit = findViewById(R.id.bitcoinCashUnit);
        ethereumUnit = findViewById(R.id.etherUnit);
        rippleUnit = findViewById(R.id.rippleUnit);
        litecoinUnit = findViewById(R.id.litcoinUnit);

        bitcoinBalance = findViewById(R.id.bitcoinBalance);
        bitcoincashBalance = findViewById(R.id.bitcoinCashBalance);
        ethereumBalance = findViewById(R.id.etherBalance);
        rippleBalance = findViewById(R.id.rippleBalance);
        litecoinBalance = findViewById(R.id.litcoinBalance);

        bitcoinDeposit = findViewById(R.id.bitcoinDeposit);
        bitcoincashDeposit = findViewById(R.id.bitcoinCashDeposit);
        ethereumDeposit = findViewById(R.id.etherDeposit);
        rippleDeposit = findViewById(R.id.rippleDeposit);
        litecoinDeposit = findViewById(R.id.litcoinDeposit);

        bitcoinProfit = findViewById(R.id.bitcoinProfit);
        bitcoincashProfit = findViewById(R.id.bitcoinCashProfit);
        ethereumProfit = findViewById(R.id.etherProfit);
        rippleProfit = findViewById(R.id.rippleProfit);
        litecoinProfit = findViewById(R.id.litecoinProfit);

        totalDeposit = findViewById(R.id.totalDeposit);
        totalBalance = findViewById(R.id.totalBalance);
        totalProfit = findViewById(R.id.totalProfit);
        percentText = findViewById(R.id.percentText);

    }

    private void getTicker() {
        TickerClient tickerClient = ServiceGenerator.createService(TickerClient.class, "https://koinex.in/api");
        tickerClient.getLatestTicker(new Callback<Ticker>() {
            @Override
            public void success(Ticker ticker, Response response) {
                if(ticker!=null){
                    displayValues(ticker);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                debugToast("Failed to get current values");
                restore();
            }
        });
    }

    private void updateTimeStap() {
        MyTicker myTicker = KoinexMemory.getMyTickerData(this);
        if(myTicker!=null){
            timestamp.setText(myTicker.getTimestamp());
        }
    }

    private void displayValues(Ticker ticker) {
        if(ticker !=null){
            bitcoin.setText(" ₹"+ticker.getPrices().getBTC());
            bitcoinCash.setText(" ₹"+ticker.getPrices().getBCH());
            ether.setText(" ₹"+ticker.getPrices().getETH());
            litecoin.setText(" ₹"+ticker.getPrices().getLTC());
            ripple.setText(" ₹"+ticker.getPrices().getXRP());
            String timestampValue =getCurrentDateTime();
            timestamp.setText(timestampValue);

            MyTicker myTicker = new MyTicker();
            myTicker.setBitcoin(getDoubleFromString(ticker.getPrices().getBTC()));
            myTicker.setLitecoin(getDoubleFromString(ticker.getPrices().getLTC()));
            myTicker.setRipple(getDoubleFromString(ticker.getPrices().getXRP()));
            myTicker.setEthereum(getDoubleFromString(ticker.getPrices().getETH()));
            myTicker.setBitcoincash(getDoubleFromString(ticker.getPrices().getBCH()));
            myTicker.setTimestamp(timestampValue);
            KoinexMemory.saveTickertData(this,myTicker);

            updateUnitDeposit();

            updateBalanceProfitInfo();
        }
    }

    private void updateBalanceProfitInfo() {
        UnitDepo unitDepo = KoinexMemory.getCoinUnitData(this);
        MyTicker myTicker = KoinexMemory.getMyTickerData(this);
        if(myTicker!=null){
            if(unitDepo!=null){
                double bitcoinBalanceValue = unitDepo.getBitcoinUnit()*myTicker.getBitcoin();
                double litecoinBalanceValue = unitDepo.getLitecoinUnit()*myTicker.getLitecoin();
                double ethereumBalanceValue = unitDepo.getEthereumUnit()*myTicker.getEthereum();
                double rippleBalanceValue = unitDepo.getRippleUnit()*myTicker.getRipple();
                double bitcoincashBalanaceValue = unitDepo.getBitcoincashUnit()*myTicker.getBitcoincash();

                bitcoinBalance.setText(getStringFromDouble(bitcoinBalanceValue));
                bitcoincashBalance.setText(getStringFromDouble(bitcoincashBalanaceValue));
                rippleBalance.setText(getStringFromDouble(rippleBalanceValue));
                litecoinBalance.setText(getStringFromDouble(litecoinBalanceValue));
                ethereumBalance.setText(getStringFromDouble(ethereumBalanceValue));

                double totalBalanceValue = bitcoinBalanceValue
                        +litecoinBalanceValue
                        +ethereumBalanceValue
                        +rippleBalanceValue
                        +bitcoincashBalanaceValue;

                totalBalance.setText(getStringFromDouble(totalBalanceValue));
                totalDeposit.setText(getStringFromDouble(unitDepo.getTotalDeposit()));
                double profit = totalBalanceValue - unitDepo.getTotalDeposit();

                totalProfit.setText(getStringFromDouble(profit));

                double percent = (profit/unitDepo.getTotalDeposit())*100;
                percentText.setText(getStringFromDouble(percent));

                double btcProfit = bitcoinBalanceValue-unitDepo.getBitcoinDeposit();
                double bchProfit = bitcoincashBalanaceValue - unitDepo.getBitcoincashDeposit();
                double ethProfit = ethereumBalanceValue - unitDepo.getEthereumDeposit();
                double liteProfit = litecoinBalanceValue-unitDepo.getLitecoinDeposit();
                double xrpProfit = rippleBalanceValue - unitDepo.getRippleDeposit();

                bitcoinProfit.setText(getStringFromDouble(btcProfit));
                bitcoincashProfit.setText(getStringFromDouble(bchProfit));
                ethereumProfit.setText(getStringFromDouble(ethProfit));
                litecoinProfit.setText(getStringFromDouble(liteProfit));
                rippleProfit.setText(getStringFromDouble(xrpProfit));

                adjustTextColorBasedOnProfit(bitcoinProfit,btcProfit);
                adjustTextColorBasedOnProfit(bitcoincashProfit,bchProfit);
                adjustTextColorBasedOnProfit(ethereumProfit,ethProfit);
                adjustTextColorBasedOnProfit(litecoinProfit,liteProfit);
                adjustTextColorBasedOnProfit(rippleProfit,xrpProfit);

            }else{
                bitcoinBalance.setText("0");
                bitcoincashBalance.setText("0");
                rippleBalance.setText("0");
                litecoinBalance.setText("0");
                ethereumBalance.setText("0");
                percentText.setText("0");
                totalBalance.setText("0");
                totalProfit.setText("0");
                totalDeposit.setText("0");
                bitcoinProfit.setText("0");
                bitcoincashProfit.setText("0");
                ethereumProfit.setText("0");
                litecoinProfit.setText("0");
                rippleProfit.setText("0");

                adjustTextColorBasedOnProfit(bitcoinProfit,0);
                adjustTextColorBasedOnProfit(bitcoincashProfit,0);
                adjustTextColorBasedOnProfit(ethereumProfit,0);
                adjustTextColorBasedOnProfit(litecoinProfit,0);
                adjustTextColorBasedOnProfit(rippleProfit,0);
            }
        }else{
            bitcoinBalance.setText("0");
            bitcoincashBalance.setText("0");
            rippleBalance.setText("0");
            litecoinBalance.setText("0");
            ethereumBalance.setText("0");
            percentText.setText("0");
            totalBalance.setText("0");
            totalProfit.setText("0");
            totalDeposit.setText("0");
            bitcoinProfit.setText("0");
            bitcoincashProfit.setText("0");
            ethereumProfit.setText("0");
            litecoinProfit.setText("0");
            rippleProfit.setText("0");

            adjustTextColorBasedOnProfit(bitcoinProfit,0);
            adjustTextColorBasedOnProfit(bitcoincashProfit,0);
            adjustTextColorBasedOnProfit(ethereumProfit,0);
            adjustTextColorBasedOnProfit(litecoinProfit,0);
            adjustTextColorBasedOnProfit(rippleProfit,0);
        }

    }

    private void adjustTextColorBasedOnProfit(TextView view, double value) {
        if(value>0){
            view.setTextColor(Color.parseColor("#2db300"));
            String text = view.getText().toString();
            view.setText("+"+text);
        }else if(value<0){
            view.setTextColor(Color.RED);
        }else{
            view.setTextColor(bitcoinUnit.getCurrentTextColor());
        }


    }

    private void updateUnitDeposit() {
        UnitDepo unitDepo = KoinexMemory.getCoinUnitData(this);
        updateUnitsLabel(unitDepo);
        updateDepositAmount(unitDepo);
    }

    private void updateDepositAmount(UnitDepo unitDepo) {
        if(unitDepo!=null){
            bitcoinDeposit.setText(getStringFromDouble(unitDepo.getBitcoinDeposit()));
            litecoinDeposit.setText(getStringFromDouble(unitDepo.getLitecoinDeposit()));
            ethereumDeposit.setText(getStringFromDouble(unitDepo.getEthereumDeposit()));
            rippleDeposit.setText(getStringFromDouble(unitDepo.getRippleDeposit()));
            bitcoincashDeposit.setText(getStringFromDouble(unitDepo.getBitcoincashDeposit()));
        }else{
            bitcoinDeposit.setText("0");
            litecoinDeposit.setText("0");
            ethereumDeposit.setText("0");
            rippleDeposit.setText("0");
            bitcoincashDeposit.setText("0");

        }

    }

    private void updateUnitsLabel(UnitDepo unitDepo) {
        if(unitDepo!=null){
            bitcoinUnit.setText(getStringFromDouble4Point(unitDepo.getBitcoinUnit()));
            litecoinUnit.setText(getStringFromDouble4Point(unitDepo.getLitecoinUnit()));
            ethereumUnit.setText(getStringFromDouble4Point(unitDepo.getEthereumUnit()));
            rippleUnit.setText(getStringFromDouble4Point(unitDepo.getRippleUnit()));
            bitcoincashUnit.setText(getStringFromDouble4Point(unitDepo.getBitcoincashUnit()));
        }else{
            bitcoinUnit.setText("0");
            litecoinUnit.setText("0");
            ethereumUnit.setText("0");
            rippleUnit.setText("0");
            bitcoincashUnit.setText("0");

        }

    }



    private void debugToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTicker();
    }

    private String getCurrentDateTime(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return "Last update on "+currentDateTimeString;
    }

    private double getDoubleFromString(String text) {
        double value = 0;
        try{
            value = Double.parseDouble(text);
        }catch (Exception e){

        }
        return value;
    }

    private String getStringFromDouble(double value) {
        String stringValue="0";
        try{
            stringValue = String.format("%.2f", value);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.refresh:
                if(isRefreshEnabled){
                    debugToast("Refreshing...");
                    getTicker();
                    if(isRefreshEnabled){
                        new CountDownTimer(30000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                countdownSeconds = (int) (millisUntilFinished/1000);
                            }

                            public void onFinish() {
                                isRefreshEnabled = true;
                            }
                        }.start();
                    }
                    isRefreshEnabled = false;
                }else{
                    debugToast("Refresh Request Disabled for "+countdownSeconds+" Seconds");
                }
                break;
            default:
                break;
        }
        return true;
    }
}
