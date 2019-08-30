package br.com.agte.agt_tubproject.Utils;

public class Constants {

    // SharedPreferences file
    public static final String PREFS_FILE = "tub_prefs";

    // Defines
    public static final int MIN_TEMP = 18;
    public static final int BROAD_ERR = -99;
    public static final String DEFAULT_COLOR = "0~0~0";
    public static final String COLOR_SPLITER = "~";

    // Permission request code
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int ENABLE_BLUETOOTH_REQUEST = 101;

    // Generated ESP WiFi information
    public static final String ESP_SSID_PREFIX = "AGTSensor_";
    //public static final String ESP_NETWORK_PSWD = "agtadmin"; //Network has no security

    // HTTP request information
    public static final String ESP_SERVER_POST_URL = "http://192.168.4.1/update";
    public static final String SSID_VALUE = "SSID";
    public static final String PSWD_VALUE = "PSWD";

    // Control TAGs
    public static final String N_LED = "Number Leds";
    public static final String LED = "Led";
    public static final String LED_ALL = "Led";
    public static final String STRIP = "Strip";
    public static final String ENGINE = "Engine";
    public static final String COLOR = "Color";
    public static final String COLOR_R = "Color_r";
    public static final String COLOR_G = "Color_g";
    public static final String COLOR_B = "Color_b";
    public static final String TEMPERATURE = "Temperature";

    public static final String INITIAL = "Initial";
    public static final String SETUP1 = "Setup1";
    public static final String SETUP2 = "Setup2";
    public static final String FINAL = "Final";

    // Broadcast Actions & TAGs
    public static final String RECEIVED_DATA = "receivedData";
    public static final String ADAPTER_STATUS = "adapterStatus";
    public static final String BT_STATUS = "btStatus";
    public static final String CONN_STATUS = "connStatus";
    public static final String CONN_MSG = "connMessage";

    // Colors String
    public static final String COLOR_MAX = "#FF0000";
    public static final String COLOR_MED = "#EECC00";
    public static final String COLOR_OFF = "#CCCCCC";
}
