package com.flightish;

/**
 * Created by yogesh on 8/4/16.
 */
public class AppConstants {
    public static final String LOCALHOST = "http://192.168.2.4/";

    public static final String APP_NAME = "Flightish";
    public static final String SHARED = "spicejet_pref";
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String LOCATION_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    public static final String OUT_JSON = "/json";
    public static final String GOOGLE_API_KEY = "myGoogleKey";
    public static final String UBER_KEY = "myUberKey";
    public static final String CAB_AUTOCOMPLETE_LOG_TAG = "Cab Autocomplete";

    public static final String STATUS_CHECK = LOCALHOST + "spicejet/statusCheck.php?pnr=";
    public static final String DASHBOARD = LOCALHOST + "spicejet/getDataByPnr.php?pnr=";
    public static final String MENU = LOCALHOST + "spicejet/getMenu.php";
    public static final String Luggage = LOCALHOST + "spicejet/getStatus.php?pnr=";
}
