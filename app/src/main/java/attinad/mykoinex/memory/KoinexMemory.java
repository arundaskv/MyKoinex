package attinad.mykoinex.memory;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import attinad.mykoinex.model.MyTicker;
import attinad.mykoinex.model.Ticker;
import attinad.mykoinex.model.UnitDepo;

/**
 * Created by Arun.Das on 15-12-2017.
 */

public class KoinexMemory {

    public static void saveCoinUnitData(Context context, UnitDepo unitDepo){
        SharedPreferences sharedPreferences = context.getSharedPreferences("mykoinex_memory",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonInString = new Gson().toJson(unitDepo);
        editor.putString("unitdepo",jsonInString);
        editor.commit();
    }

    public static void saveTickertData(Context context, MyTicker ticker){
        SharedPreferences sharedPreferences = context.getSharedPreferences("mykoinex_memory",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonInString = new Gson().toJson(ticker);
        editor.putString("my_koinex_ticker",jsonInString);
        editor.commit();
    }
    public static MyTicker getMyTickerData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("mykoinex_memory",0);
        String jsonInString = sharedPreferences.getString("my_koinex_ticker",null);
        try{
            if(jsonInString!=null){
                MyTicker myTicker = new Gson().fromJson(jsonInString, MyTicker.class);
                return myTicker;
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }
    public static UnitDepo getCoinUnitData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("mykoinex_memory",0);
        String jsonInString = sharedPreferences.getString("unitdepo",null);
        try{
            if(jsonInString!=null){
                UnitDepo unitdepo = new Gson().fromJson(jsonInString, UnitDepo.class);
                return unitdepo;
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }
}
