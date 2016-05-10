package com.flightish.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.flightish.AppConstants;
import com.flightish.R;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.SHARED, Context.MODE_WORLD_WRITEABLE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear().commit();
    }
}
