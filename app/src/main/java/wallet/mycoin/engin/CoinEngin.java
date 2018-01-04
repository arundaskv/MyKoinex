package wallet.mycoin.engin;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import wallet.mycoin.OnCalculation;
import wallet.mycoin.memory.KoinexMemory;
import wallet.mycoin.model.CoinType;
import wallet.mycoin.model.Transaction;
import wallet.mycoin.model.TransactionType;
import wallet.mycoin.model.UnitDepo;

/**
 * Created by Arun.Das on 22-12-2017.
 */

public class CoinEngin {

    public static void calculateUnitsBasedOnDBValues(Context context, List<Transaction> transactions, OnCalculation onCalculation){
        UnitDepo unitDepo = new UnitDepo();
        double bitcoinUnit = 0;
        double bitcoinDp = 0;
        double litecoinUnit = 0;
        double litecoinDp = 0;
        double rippleUnit = 0;
        double rippleUnitDp = 0;
        double etherUnit = 0;
        double etherDp = 0;
        double bitcoinCash = 0;
        double bitcoinCashDp = 0;
        double totalDp = 0;
        for (Transaction transaction : transactions) {

           if(transaction.getCoinType()== CoinType.BTC){
               if(transaction.getTransactionType()==TransactionType.BUY){
                   bitcoinUnit = bitcoinUnit+getDoubleFromString(transaction.getVolume());
                   bitcoinDp = bitcoinDp+getDoubleFromString(transaction.getTotal());
               }else{
                   bitcoinUnit = bitcoinUnit-getDoubleFromString(transaction.getVolume());
                   bitcoinDp = bitcoinDp-getDoubleFromString(transaction.getTotal());
               }
           }
            if(transaction.getCoinType()== CoinType.LTC){
                if(transaction.getTransactionType()==TransactionType.BUY){
                    litecoinUnit = litecoinUnit+getDoubleFromString(transaction.getVolume());
                    litecoinDp = litecoinDp+getDoubleFromString(transaction.getTotal());
                }else{
                    litecoinUnit = litecoinUnit-getDoubleFromString(transaction.getVolume());
                    litecoinDp = litecoinDp-getDoubleFromString(transaction.getTotal());
                }
            }

            if(transaction.getCoinType()== CoinType.XRP){
                if(transaction.getTransactionType()==TransactionType.BUY){
                    rippleUnit = rippleUnit+getDoubleFromString(transaction.getVolume());
                    rippleUnitDp = rippleUnitDp+getDoubleFromString(transaction.getTotal());
                }else{
                    rippleUnit = rippleUnit-getDoubleFromString(transaction.getVolume());
                    rippleUnitDp = rippleUnitDp-getDoubleFromString(transaction.getTotal());
                }
            }

            if(transaction.getCoinType()== CoinType.ETH){
                if(transaction.getTransactionType()==TransactionType.BUY){
                    etherUnit = etherUnit+getDoubleFromString(transaction.getVolume());
                    etherDp = etherDp+getDoubleFromString(transaction.getTotal());
                }else{
                    etherUnit = etherUnit-getDoubleFromString(transaction.getVolume());
                    etherDp = etherDp-getDoubleFromString(transaction.getTotal());
                }
            }

            if(transaction.getCoinType()== CoinType.BCH){
                if(transaction.getTransactionType()==TransactionType.BUY){
                    bitcoinCash = bitcoinCash+getDoubleFromString(transaction.getVolume());
                    bitcoinCashDp = bitcoinCashDp+getDoubleFromString(transaction.getTotal());
                }else{
                    bitcoinCash = bitcoinCash-getDoubleFromString(transaction.getVolume());
                    bitcoinCashDp = bitcoinCashDp-getDoubleFromString(transaction.getTotal());
                }
            }
        }

        if(bitcoinUnit<0.0001){
            bitcoinUnit = 0;
        }if(litecoinUnit<0.0001){
            litecoinUnit = 0;
        }if(bitcoinCash<0.0001){
            bitcoinCash = 0;
        }if(etherUnit<0.0001){
            etherUnit = 0;
        }if(rippleUnit<0.0001){
            rippleUnit = 0;
        }

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

        totalDp = bitcoinDp+litecoinDp+rippleUnitDp+etherDp+bitcoinCashDp;

        unitDepo.setTotalDeposit(totalDp);

        KoinexMemory.saveCoinUnitData(context,unitDepo);
        onCalculation.onFiishedCalculation();

    }



    private static double getDoubleFromString(String text) {
        double value = 0;
        try{
            value = Double.parseDouble(text);
        }catch (Exception e){

        }
        return value;
    }
}
