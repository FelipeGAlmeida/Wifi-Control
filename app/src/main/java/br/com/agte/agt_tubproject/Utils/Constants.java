package br.com.agte.agt_tubproject.Utils;

public class Constants {

    //SharedPreferences file
    public static final String PREFS_FILE = "tub_prefs";

    //Min. Temperature
    public static final int MIN_TEMP = 25;

    // Permission request code
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    // Generated ESP WiFi information
    public static final String ESP_SSID_PREFIX = "AGTSensor_";
    //public static final String ESP_NETWORK_PSWD = "agtadmin";

    // HTTP request information
    public static final String ESP_SERVER_POST_URL = "http://192.168.4.1/update";
    public static final String SSID_VALUE = "SSID";
    public static final String PSWD_VALUE = "PSWD";

    // Fragment TAGs
    public static final String CONTROL = "Control";
    public static final String ENGINE = "Engine";
    public static final String COLOR = "Color";
    public static final String TEMPERATURE = "Temperature";

    public static final String INITIAL = "Initial";
    public static final String SETUP1 = "Setup1";
    public static final String SETUP2 = "Setup2";
    public static final String FINAL = "Final";

}
