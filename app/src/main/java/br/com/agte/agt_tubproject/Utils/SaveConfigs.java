package br.com.agte.agt_tubproject.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveConfigs {

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
        String rgb = r + Constants.COLOR_SPLITER + g + Constants.COLOR_SPLITER + b; //Creating the RGB color string

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.COLOR, rgb);
        editor.apply();
    }

    public void saveSpecificColor(String colorCode, int value){
        String[] rgb = loadColorPrefs().split(Constants.COLOR_SPLITER);

        int r = Integer.parseInt(rgb[0]), g = Integer.parseInt(rgb[1]), b = Integer.parseInt(rgb[2]);
        switch (colorCode){
            case Constants.COLOR_R: r = value;
                break;
            case Constants.COLOR_G: g = value;
                break;
            case Constants.COLOR_B: b = value;
                break;
        }

        String rgb_string = r + Constants.COLOR_SPLITER + g + Constants.COLOR_SPLITER + b; //Creating the RGB color string

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.COLOR, rgb_string);
        editor.apply();
    }

    public void saveTemperaturePrefs(int temperature){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.TEMPERATURE, temperature);
        editor.apply();
    }

    public void saveNSpots(int number_leds){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.N_LED, number_leds);
        editor.apply();
    }

    public int loadEnginePrefs(){
        return  sharedPrefs.getInt(Constants.ENGINE, 0);
    }

    public String loadColorPrefs(){
        return  sharedPrefs.getString(Constants.COLOR, Constants.DEFAULT_COLOR);
    }

    public int loadTemperaturePrefs(){
        return  sharedPrefs.getInt(Constants.TEMPERATURE, 0);
    }

    public int loadNSpots(){
        return sharedPrefs.getInt(Constants.N_LED, 4);
    }
}
