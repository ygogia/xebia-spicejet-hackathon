package com.flightish.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flightish.AppConstants;
import com.flightish.adapter.GooglePlacesAutocompleteAdapter;
import com.flightish.R;
import com.flightish.model.Location;
import com.flightish.util.APIClient;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.uber.sdk.android.rides.RequestButton;
import com.uber.sdk.android.rides.RideParameters;

public class CabPageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ShimmerTextView title;
    APIClient apiClient;
    AutoCompleteTextView source,destination;
    LinearLayout layout;
    EditText pnr;
    RequestButton requestButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_page);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final Button look_cab = (Button) findViewById(R.id.look_cab);
        final Button button = (Button) findViewById(R.id.submit);
        pnr = (EditText)findViewById(R.id.pnr);
        apiClient = new APIClient();
        SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.SHARED, Context.MODE_WORLD_WRITEABLE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        if(!sharedpreferences.contains("pnr")) {
            layout = (LinearLayout) findViewById(R.id.cabLayout);
            requestButton = (RequestButton) findViewById(R.id.uber_button);
            layout.removeView(requestButton);
            layout.removeView(button);

            source = (AutoCompleteTextView) findViewById(R.id.source);
            title = (ShimmerTextView) findViewById(R.id.shimmer_title);
            title.setText(AppConstants.APP_NAME);

            source.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.autocomplete_item));
            source.setOnItemClickListener(this);

            destination = (AutoCompleteTextView) findViewById(R.id.destination);
            destination.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.autocomplete_item));
            destination.setOnItemClickListener(this);

            Shimmer shimmer = new Shimmer();
            shimmer.start(title);

            look_cab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (source.getText().toString().trim().length() == 0 || destination.getText().toString().trim().length()== 0 || pnr.getText().toString().trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "All Fields Are MAndatory", Toast.LENGTH_LONG).show();
                        layout.removeView(look_cab);
                        return;
                    }
                    if (APIClient.isNetworkAvailable(getApplicationContext())) {

                        Location sourceLocation = apiClient.getLocation(source.getText().toString());
                        Location destinationLocation = apiClient.getLocation(apiClient.autocomplete("airport"));
                        editor.putString("pnr",pnr.getText().toString());
                        editor.putString("source",source.getText().toString());
                        editor.putString("destination",destination.getText().toString());
                        editor.commit();
                        if (sourceLocation != null && destinationLocation != null) {

                            requestButton.setClientId(AppConstants.UBER_KEY);
                            RideParameters rideParams = new RideParameters.Builder()
                                    .setPickupLocation(sourceLocation.getLat(), sourceLocation.getLon(), null, null)
                                    .setDropoffLocation(destinationLocation.getLat(), destinationLocation.getLon(), null, null)
                                    .build();
                            requestButton.setRideParameters(rideParams);
                            layout.removeView(look_cab);
                            layout.addView(requestButton);
                        }
                        layout.addView(button);
                    } else {
                        layout.removeView(look_cab);
                        Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                    }
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CabPageActivity.this, DashBoardActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }else{
            Intent i = new Intent(CabPageActivity.this, DashBoardActivity.class);
            startActivity(i);
            finish();
        }
    }
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
