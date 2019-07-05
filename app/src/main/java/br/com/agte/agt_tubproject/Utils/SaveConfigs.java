package br.com.agte.agt_tubproject.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveConfigs {

    private static final String DEFAULT_COLOR = "0~0~0";

    private static SaveConfigs saveConfigs;
    private SharedPreferences sharedPrefs;
    private Context context;

    public static SaveConfigs getInstance(Context context){
        return (saveConfigs != null ? saveConfigs : new SaveConfigs(context));
    }

    public SaveConfigs(Context context) {
        this.context = context;
        initSharedPreferences();
    }

    private void initSharedPreferences(){
        sharedPrefs = context.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
    }

    public void saveEnginePrefs(int engine){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.ENGINE, engine);
        editor.apply();
    }

    public void saveColorPrefs(int r, int g, int b){
        String rgb = r + "~" + g + "~" + b; //Creating the RGB color string

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.COLOR, rgb);
        editor.apply();
    }

    public void saveTemperaturePrefs(int temperature){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.TEMPERATURE, temperature);
        editor.apply();
    }

    public int loadEnginePrefs(){
        return  sharedPrefs.getInt(Constants.ENGINE, 0);
    }

    public String loadColorPrefs(){
        return  sharedPrefs.getString(Constants.COLOR, DEFAULT_COLOR);
    }

    public int loadTemperaturePrefs(){
        return  sharedPrefs.getInt(Constants.TEMPERATURE, 0);
    }
}
