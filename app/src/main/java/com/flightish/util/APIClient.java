package com.flightish.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.flightish.AppConstants;
import com.flightish.adapter.OrderAdapter;
import com.flightish.model.DashboardDTO;
import com.flightish.model.MenuDTO;
import com.google.api.client.util.DateTime;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class APIClient {


    OkHttpClient client = new OkHttpClient();
    public String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean value = false;

        ConnectivityManager connec = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            value = true;
        }

        Log.d("Internet Available", Boolean.toString(value));
        return value;
    }
    public static com.flightish.model.Location getLocation(String name){
        APIClient apiClient = new APIClient();
        com.flightish.model.Location location = new com.flightish.model.Location();
        try {
            String response = apiClient.doGetRequest(AppConstants.LOCATION_API_BASE + name);
            if(response!=null){
                JSONObject jsonObject = new JSONObject(response).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                location.setLat((float)jsonObject.getDouble("lat"));
                location.setLon((float)jsonObject.getDouble("lng"));
                return location;
            }else{
                return null;
            }
        }catch(IOException e){
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(AppConstants.PLACES_API_BASE + AppConstants.TYPE_AUTOCOMPLETE + AppConstants.OUT_JSON);

            sb.append("?key=" + AppConstants.GOOGLE_API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(AppConstants.CAB_AUTOCOMPLETE_LOG_TAG, "Error processing Places API URL", e);
            return resultList.get(0);
        } catch (IOException e) {
            Log.e(AppConstants.CAB_AUTOCOMPLETE_LOG_TAG, "Error connecting to Places API", e);
            return resultList.get(0);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(AppConstants.CAB_AUTOCOMPLETE_LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList.get(0);
    }
    public static boolean getStatus(String pnr){
        APIClient apiClient = new APIClient();
        try {
            String response = apiClient.doGetRequest(AppConstants.STATUS_CHECK + pnr);
            if(response!=null){
                boolean status = (new JSONObject(response).getInt("status"))==0?false:true;
                return false;
            }
        } catch (IOException e) {
            Log.d("STATUS_CHECK","No Data");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("STATUS_CHECK","Wrong JSON Field Parsing");
            e.printStackTrace();
        }
        return false;
    }

    public static DashboardDTO getDashboardDTO(String pnr, String sourceAddress, String destinationAddress){
        DashboardDTO dashboardDTO = new DashboardDTO();
        APIClient apiClient = new APIClient();
        final  String url = AppConstants.DASHBOARD +pnr + "&to=" + sourceAddress.replace(" ","%20") + "&from=" + destinationAddress.replace(" ","%20");
        Log.d("DASHBOARD DTO", url);
        try {
            String response = apiClient.doGetRequest(url);
            if(response!=null){
                JSONObject jsonObject = new JSONObject(response).getJSONArray("details").getJSONObject(0);
                dashboardDTO.setName(jsonObject.getString("name"));
                dashboardDTO.setBoard(Integer.parseInt(jsonObject.getString("board")) == 0 ? false : true);
                dashboardDTO.setPhone(jsonObject.getString("phone"));
                dashboardDTO.setLane_no(Integer.parseInt(jsonObject.getString("lane_no")));
                if(jsonObject.getString("order")!=null){
                    dashboardDTO.setOrders(Arrays.asList(jsonObject.getString("order").split("\\s*:\\s*")));
                }
                dashboardDTO.setDestination(jsonObject.getString("destination"));
                dashboardDTO.setSource(jsonObject.getString("source"));
                dashboardDTO.setFlight_name(jsonObject.getString("flight name"));

                String dateTimeStr = jsonObject.getString("Date") + jsonObject.getString("time");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = (Date) sdf.parse(dateTimeStr);
                if(date!=null){
                    dashboardDTO.setTime(date);
                }
                return dashboardDTO;
            }
        } catch (IOException e) {
            Log.d("STATUS_CHECK", "No Data");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.d("STATUS_CHECK", "Wrong JSON Field Parsing");
            e.printStackTrace();
            return null;

        } catch (ParseException e) {
            e.printStackTrace();
            return dashboardDTO;

        }
        return null;
    }
    public static List<MenuDTO> getMenu(){
        List<MenuDTO> orders = new ArrayList<MenuDTO>();
        APIClient apiClient = new APIClient();
        final  String url = AppConstants.MENU;
        Log.d("MENU DTO", url);
        try {
            String response = apiClient.doGetRequest(url);
            if(response!=null){
                JSONArray jsonArray = new JSONObject(response).getJSONArray("details");
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    MenuDTO menuDTO = new MenuDTO();
                    menuDTO.setCommpany_name(jsonObject.getString("commpany name"));
                    menuDTO.setItem_name(jsonObject.getString("item name"));
                    menuDTO.setCompany_mail(jsonObject.getString("company mail"));
                    menuDTO.setPrice(jsonObject.getString("price"));
                    orders.add(menuDTO);
                }
                if(orders.size()!=0) {
                    return orders;
                }else{
                    return null;
                }
            }
        } catch (IOException e) {
            Log.d("STATUS_CHECK", "No Data");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.d("STATUS_CHECK", "Wrong JSON Field Parsing");
            e.printStackTrace();
            return null;

        }
        return null;
    }
    public static String getLuggageStatus(String pnr){
        APIClient apiClient = new APIClient();
        final  String url = AppConstants.Luggage +pnr;
        Log.d("MENU DTO", url);
        try {
            String response = apiClient.doGetRequest(url);
            if (response != null) {
               String status =  new JSONObject(response).getJSONArray("details").getJSONObject(0).getString("status");
               return status;
            }
        }catch (IOException e) {
            Log.d("STATUS_CHECK", "No Data");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.d("STATUS_CHECK", "Wrong JSON Field Parsing");
            e.printStackTrace();
            return null;
        }
        return null;
    }
}