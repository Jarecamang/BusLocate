package com.jarecamang.buslocate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Alejandro on 22/02/2018.
 */

public class RouteActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_details);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getExtras().getString("bus_data");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.routeData);
        textView.setText(message + "123");
    }
}
