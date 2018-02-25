package com.jarecamang.buslocate;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.Frame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 22/02/2018.
 */

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = MainActivity.class.getCanonicalName();

    private List<LatLng> routeStops;
    private Bus bus;
    private Bitmap routeImage;
    private ImageView imageView;
    private FrameLayout progressBarHolder;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private Integer duration;
    private Integer retry;
    private TextView durationTextView;
    private boolean mapFinished;
    private boolean downloadFinished;
    private GoogleMap printedMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapFinished = false;
        downloadFinished = false;
        duration=0;

        routeStops = new ArrayList<>();

        imageView = findViewById(R.id.route_image);

        //progressBarHolder = findViewById(R.id.progress_bar_holder);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        bus = new Bus(extras.getInt("route_id"),
                extras.getString("route_name"),
                extras.getString("route_description"),
                extras.getString("route_stops"),
                extras.getString("route_img"));

        // Capture the layout's TextView and set the string as its text
        durationTextView = findViewById(R.id.duration);

        new GetRouteData().execute();

        TextView textView = findViewById(R.id.routeData);
        textView.setText(bus.getName());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        printedMap = googleMap;
        mapFinished = true;
        onTasksFinished();
    }

    public void onTasksFinished(){
        if(mapFinished && downloadFinished){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLng position;
            for(int i=0;i<routeStops.size();i++){
                position = routeStops.get(i);
                if(i==0)
                    printedMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_start))
                            .title("Start")
                            .position(position));
                else if(i==routeStops.size()-1)
                    printedMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_poing))
                            .title("Start")
                            .position(position));
                else
                    printedMap.addMarker(new MarkerOptions().position(position));
                builder.include(position);
            }
            LatLngBounds bounds = builder.build();
            int height = getResources().getDisplayMetrics().heightPixels;
            printedMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (height * 0.10)));

            // Adding all the points in the route to LineOptions
//            PolylineOptions lineOptions = new PolylineOptions();
//            lineOptions.addAll(routeStops);
//            lineOptions.width(2);
//            lineOptions.color(Color.RED);
//            printedMap.addPolyline(lineOptions);
        }
    }

    private class GetRouteData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //inAnimation = new AlphaAnimation(0f, 1f);
            //inAnimation.setDuration(200);
            //progressBarHolder.setAnimation(inAnimation);
            //progressBarHolder.setVisibility(View.VISIBLE);
            Log.e("RouteData", "Route Data is downloading");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //It was hard to choose if I should have created another async task,
            //but I can control better the app behavior if I do it sequentially.
            //Anyway, the services latency isn't bad at all.

            try{
                InputStream in = new java.net.URL(bus.getImg_url()).openStream();
                routeImage = BitmapFactory.decodeStream(in);
            }catch(Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = bus.getStops_url();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    duration = jsonObj.getInt("estimated_time_milliseconds");
                    retry = jsonObj.getInt("retry_time_milliseconds");

                    // Getting JSON Array node
                    JSONArray stops = jsonObj.getJSONArray("stops");
                    float [] result = {0};
                    LatLng stop;
                    // looping through All Stops
                    for (int i = 0; i < stops.length(); i++) {
                        JSONObject s = stops.getJSONObject(i);
                        stop = new LatLng(s.getDouble("lat"), s.getDouble("lng"));
                        if(i>0){
                            Location.distanceBetween(routeStops.get(routeStops.size()-1).latitude,routeStops.get(routeStops.size()-1).longitude,stop.latitude, stop.longitude,result);
                            if(result[0] < 50000)
                                routeStops.add(stop);
                        }else
                            routeStops.add(stop);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //show mBusList
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            Log.e("RouteData", "Route Data downloaded");
            imageView.setImageBitmap(routeImage);
            durationTextView.setText("Duración "+duration/1000/60 + " Min. Aprox.");
            downloadFinished = true;
            onTasksFinished();
            //outAnimation = new AlphaAnimation(1f, 0f);
            //outAnimation.setDuration(200);
            //progressBarHolder.setAnimation(outAnimation);
            //progressBarHolder.setVisibility(View.GONE);
        }
    }
}
