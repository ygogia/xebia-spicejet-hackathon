package com.flightish.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flightish.AppConstants;
import com.flightish.R;
import com.flightish.adapter.OrderAdapter;
import com.flightish.model.DashboardDTO;
import com.flightish.model.MenuDTO;
import com.flightish.util.APIClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DashBoardActivity extends AppCompatActivity{
    private TextView timer, userName, baggage, flight_status;
    private ListView orderList;
    String pnr;
    protected static final String TAG = "MonitoringActivity";
    protected static final String TAG2 = "NEW";
    ArrayList uuidList = new ArrayList(150);
    ProgressDialog pDialog;
    Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        APIClient apiClient = new APIClient();

        timer = (TextView) findViewById(R.id.countdown);
        userName = (TextView) findViewById(R.id.userName);
        baggage = (TextView) findViewById(R.id.baggage);
        flight_status = (TextView)findViewById(R.id.flightStatus);
        orderList = (ListView) findViewById(R.id.orders);



        List<MenuDTO> menuDTOList = apiClient.getMenu();
        if(menuDTOList!=null){
            orderList.setAdapter(new OrderAdapter(this, R.layout.menu_list, menuDTOList));
        }

        SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.SHARED, Context.MODE_WORLD_WRITEABLE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        if(sharedpreferences.contains("pnr")) {
            pnr = sharedpreferences.getString("pnr", "");
            DashboardDTO dashboardDTO = apiClient.getDashboardDTO(sharedpreferences.getString("pnr", ""),sharedpreferences.getString("source", ""), sharedpreferences.getString("destination", ""));
            if(dashboardDTO!=null){
                userName.setText(dashboardDTO.getName().toUpperCase());
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, +2);
                Date date = calendar.getTime();
                long mills = dashboardDTO.getTime().getTime() - date.getTime();
                long boardingTime = dashboardDTO.getTime().getTime() - (new Date().getTime());
                final CountDownTimer countDownTimer = new CountDownTimer(boardingTime, 1000) {
                    public void onTick(long millis) {
                        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        timer.setText(hms);
                    }

                    public void onFinish() {
                        timer.setText("Flighting Leaving");
                        Intent i = new Intent(DashBoardActivity.this, EndActivity.class);
                        startActivity(i);
                        finish();
                    }

                };
                new CountDownTimer(mills, 1000) {
                    public void onTick(long millis) {
                        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        timer.setText(hms);
                    }

                    public void onFinish() {
                        flight_status.setText("...left To Board");
                        baggage.setText("Luggage Submitted");
                        countDownTimer.start();
                    }
                }.start();
                flight_status.setText(dashboardDTO.isBoard() ? "Time To Boarding @Gate#"+dashboardDTO.getLane_no() : "Time  to CheckIn@ Counter#" + dashboardDTO.getLane_no());
                baggage.setText(dashboardDTO.isBoard()?"Luggage @AIRPORT":"Luggage with you ");
            }else{
                Log.d("DASHBOARD_ACTIVITY","DTO NUll");
            }
        }else{
            Intent i = new Intent(DashBoardActivity.this, CabPageActivity.class);
            startActivity(i);
            finish();
        }
    }



}
