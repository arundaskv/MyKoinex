package wallet.mycoin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wallet.mycoin.api.Connectivity;
import wallet.mycoin.api.DBServer;
import wallet.mycoin.api.ServiceGenerator;
import wallet.mycoin.api.TickerClient;
import wallet.mycoin.engin.CoinEngin;
import wallet.mycoin.memory.KoinexMemory;
import wallet.mycoin.model.CoinType;
import wallet.mycoin.model.MyTicker;
import wallet.mycoin.model.Ticker;
import wallet.mycoin.model.Transaction;
import wallet.mycoin.model.UnitDepo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import wallet.mycoin.model.UserData;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    TextView emailId, usernameTxt;
    CircleImageView profileImage;
    private ProgressBar progressBar;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<Transaction> transactionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        googleSignIn();
        initView();
        restore();
        setupToolbar();
        setupFab();
        initDrawerView();
        getTicker();
        fetchData();
    }

    private void fetchData() {
        if(Connectivity.isConnected(this)){
            transactionList = new ArrayList();
            if(mFirebaseDatabase!=null){
                mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        transactionList.clear();
                        for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                            String key = childSnapShot.getKey();
                            Transaction transaction = childSnapShot.getValue(Transaction.class);
                            transaction.setKey(key);
                            transactionList.add(transaction);
                        }

                        if(transactionList.size()>0){
                            CoinEngin.calculateUnitsBasedOnDBValues(HomeActivity.this,transactionList, new OnCalculation() {
                                @Override
                                public void onFiishedCalculation() {
                                    updateUnitDeposit();
                                    updateBalanceProfitInfo();
                                }
                            });
                        }else{
                            KoinexMemory.saveCoinUnitData(HomeActivity.this,null);
                            updateUnitDeposit();
                            updateBalanceProfitInfo();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }

        }

    }

    private void initDrawerView() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        usernameTxt = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        emailId = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.profile_image);


        if(KoinexMemory.getUserData(this)!=null){
            UserData userData = KoinexMemory.getUserData(this);
            usernameTxt.setText(userData.getUsername());
            emailId.setText(userData.getEmailId());
            profileImage.setImageResource(R.drawable.profile_placeholder_24dp);
        }
    }

    private void googleSignIn() {
        if(Connectivity.isConnected(this)){
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mAuth = FirebaseAuth.getInstance();
            if(mAuth==null || mAuth.getCurrentUser()==null || KoinexMemory.getUserData(this)==null){
                signIn();
            }
        }
    }

    private void signIn() {
        if(Connectivity.isConnected(this)){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        if(Connectivity.isConnected(this) && mAuth!=null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            debugToast("Google SignIn failed");
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String email = mAuth.getCurrentUser().getEmail();
            String username = mAuth.getCurrentUser().getDisplayName();
            String profileImageUrl = mAuth.getCurrentUser().getPhotoUrl().toString();
            String userId = mAuth.getCurrentUser().getEmail().split("@")[0];

            UserData userdata = new UserData();
            userdata.setUsername(username);
            userdata.setEmailId(email);
            userdata.setUserid(userId);
            userdata.setPhotoUrl(profileImageUrl);
            mFirebaseDatabase = mFirebaseInstance.getReference(userId);
            KoinexMemory.saveUserData(this,userdata);

            if(emailId!=null && usernameTxt!=null &&profileImage!=null){
                emailId.setText(email);
                usernameTxt.setText(username);
                Glide.with(this).load(profileImageUrl).placeholder(R.drawable.profile_placeholder_24dp).into(profileImage);
            }

            Menu menu = navigationView.getMenu();
            MenuItem nav_account = menu.findItem(R.id.nav_account);
            nav_account.setTitle("Sign Out");

            fetchData();

        } else {
            clearAllToDefaults();
        }
    }

    private void clearAllToDefaults() {
        KoinexMemory.saveUserData(HomeActivity.this,null);
        KoinexMemory.saveCoinUnitData(HomeActivity.this,null);

        bitcoinBalance.setText("0");
        bitcoinUnit.setText("0");
        bitcoinProfit.setText("0");
        bitcoinDeposit.setText("0");

        bitcoincashBalance.setText("0");
        bitcoincashUnit.setText("0");
        bitcoincashProfit.setText("0");
        bitcoincashDeposit.setText("0");

        rippleBalance.setText("0");
        rippleUnit.setText("0");
        rippleProfit.setText("0");
        rippleDeposit.setText("0");

        litecoinBalance.setText("0");
        litecoinUnit.setText("0");
        litecoinProfit.setText("0");
        litecoinDeposit.setText("0");

        ethereumBalance.setText("0");
        ethereumUnit.setText("0");
        ethereumProfit.setText("0");
        ethereumDeposit.setText("0");

        percentText.setText("0");

        totalBalance.setText("0");

        totalProfit.setText("0");

        totalDeposit.setText("0");

        adjustTextColorBasedOnProfit(bitcoinProfit,0);
        adjustTextColorBasedOnProfit(bitcoincashProfit,0);
        adjustTextColorBasedOnProfit(ethereumProfit,0);
        adjustTextColorBasedOnProfit(litecoinProfit,0);
        adjustTextColorBasedOnProfit(rippleProfit,0);
        adjustTextColorBasedOnProfit(percentText,0);

        usernameTxt.setText("Guest User");
        emailId.setText("");
        profileImage.setImageResource(R.drawable.profile_placeholder_24dp);
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
        fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAddTransactionPage();
            }
        });
    }

    private void setupToolbar() {
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        /*progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);*/

        mFirebaseInstance = FirebaseDatabase.getInstance();
        if(KoinexMemory.getUserData(this)!=null){
            mFirebaseDatabase = mFirebaseInstance.getReference(KoinexMemory.getUserData(this).getUserid());
        }

    }

    private void getTicker() {
        if(Connectivity.isConnected(this)){
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
                adjustTextColorBasedOnProfit(percentText,percent);
                adjustTextColorBasedOnProfit(totalProfit,profit);

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
                adjustTextColorBasedOnProfit(percentText,0);
                totalProfit.setTextColor(getResources().getColor(R.color.colorAccent));
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
            adjustTextColorBasedOnProfit(percentText,0);
            totalProfit.setTextColor(getResources().getColor(R.color.colorAccent));
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_account) {
            if(Connectivity.isConnected(HomeActivity.this) && mAuth!=null){
                if(mAuth.getCurrentUser()== null){
                    signIn();
                }else{
                    alertForAccountActivity(false,"Are you sure you want to Sign out from Google Account. All your saved data will be lost");
                }
            }
        } else if (id == R.id.nav_add_transaction) {
            openAddTransactionPage();
        } else if (id == R.id.nav_history_transaction) {
            if(Connectivity.isConnected(this)){
                if(KoinexMemory.getUserData(this)!=null){
                    Intent intent = new Intent(HomeActivity.this,TransactionHistoryActivity.class);
                    startActivity(intent);
                }else{
                    debugToast("Sign in with Google for Transactions");
                }
            }
        }
        if(drawer!=null){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void openAddTransactionPage() {
        if(Connectivity.isConnected(this)){
            if(KoinexMemory.getUserData(this)!=null){
                Intent intent = new Intent(HomeActivity.this,AddTransactionActivity.class);
                startActivity(intent);
            }else{
                debugToast("Sign in with Google for Transactions");
            }
        }
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Menu menu = navigationView.getMenu();
                        MenuItem nav_account = menu.findItem(R.id.nav_account);
                        nav_account.setTitle("Sign In");

                        updateUI(null);
                        debugToast("Logged out from Google Account");
                    }
                });
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        if(drawer!=null){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                finish();
                super.onBackPressed();
            }
        }

    }

    private void alertForAccountActivity(final boolean isSignIn,String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Action");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(isSignIn){
                    signIntoFirebase();
                }else{
                    signOutFromFirebase();
                }

            }
        })
         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)  {

                    }
                });
        alertDialogBuilder.create().show();

    }

    private void signOutFromFirebase() {
        if(Connectivity.isConnected(this)){
            signOut();
        }

    }

    private void signIntoFirebase() {
            signIn();
    }

}
