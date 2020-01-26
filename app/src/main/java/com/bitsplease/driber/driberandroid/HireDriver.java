package com.bitsplease.driber.driberandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import java.util.Calendar;


public class HireDriver extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private static int GET_LOCATION = 1;
    CameraUpdate camUpdate = null;
    String uEmail;
    String uPass;
    Button getCurrentLocation;
    Button pickDate;
    Button pickTime;
    Button sendDriver;
    GoogleMap mMap;
    String latitude;
    String longitude;
    String hireDate;
    String hireTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Complicated stuff. Basically, it loads the map and starts the Location Service.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_driver);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment3);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GET_LOCATION);
        getCurrentLocation = (Button) findViewById(R.id.getCurrentLocation);
        getCurrentLocation.setOnClickListener(this);
        pickDate = (Button) findViewById(R.id.pickDate);
        pickDate.setOnClickListener(this);
        pickDate.setEnabled(false);
        pickTime = (Button) findViewById(R.id.pickTime);
        pickTime.setOnClickListener(this);
        pickTime.setEnabled(false);
        sendDriver = (Button) findViewById(R.id.sendDriver);
        sendDriver.setOnClickListener(this);
        sendDriver.setEnabled(false);
        uEmail = getIntent().getStringExtra("uEmail");
        uPass = getIntent().getStringExtra("uPass");
        Log.i("DriberServiceStart", "Successfully started HireDriver service!");
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        mMap = map;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true); //Start Location Service
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        Log.i("DriberLocationService", "Map service is online!");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        month = month + 1;
        Log.d("DriberCalendarEvent","User date set: " + month +"/" + day + "/" + year);
        hireDate = Integer.toString(month) + "/" + Integer.toString(day) + "/" + Integer.toString(year);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        hireTime = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
        Log.d("DriberCalendarEvent", "User set time: " + hireTime);
    }

    private Response.Listener<String> createMyReqSuccessListener(String userEmail, String userPassword)
    {
        //Send new hireDriver request
        final String uEmail = userEmail;
        final String uPass = userPassword;
        final String goodReply = "OK";

        return new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (response.trim().equals(goodReply))
                {
                    Intent executeHiredDriverStatusService = new Intent(HireDriver.this, HiredDriverStatus.class);
                    startActivity(executeHiredDriverStatusService);
                    Log.i("DriberBackendReply", response);
                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener()
    {
        return new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                String mafuyuServiceError = error.toString();
                Log.e("DriberBackendReply", mafuyuServiceError);
            }
        };
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.getCurrentLocation)
        {
            //Set map camera to current location
            Location userLocation = mMap.getMyLocation();

            if (userLocation != null)
            {
                try
                {
                    latitude = Double.toString(userLocation.getLatitude());
                    longitude = Double.toString(userLocation.getLongitude());
                    LatLng currLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    camUpdate = CameraUpdateFactory.newLatLngZoom(currLatLng, 15);
                    mMap.animateCamera(camUpdate);
                    pickDate.setEnabled(true);
                    Log.i("DriberLocationService", "Updated user position!");
                }

                catch (Exception e)
                {
                    Log.wtf("DriberUnexpectedCrashEvent", "Driber has crashed unexpectedly. Please see exception below");
                    Log.wtf("DriberUnexpectedCrashEvent", e.toString());
                }
            }

            else
            {
                Toast.makeText(this, "Please turn on your GPS. If your GPS is already turned on, please wait for the location service to find your current location", Toast.LENGTH_LONG).show();
                Log.i("DriberLocationService", "GPS is either disabled or the service is already searching for the current location.");
            }
        }

        else if (v.getId() == R.id.pickDate)
        {
            //Asks for date using popup dialog
            try
            {
                Log.i("DriberCalendarEvent", "Asking user for hire date...");
                Calendar dateCalendar = Calendar.getInstance();
                int year = dateCalendar.get(Calendar.YEAR);
                int month = dateCalendar.get(Calendar.MONTH);
                int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
                dpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpDialog.show();
                pickTime.setEnabled(true);


            }

            catch (Exception e)
            {
                Log.e("DriberCrashEvent", e.toString());
            }
        }

        else if (v.getId() == R.id.pickTime)
        {
            //Asks for time
            try
            {
                Log.i("DriberCalendarEvent", "Asking user for hire time...");
                Calendar timeCalendar = Calendar.getInstance();
                int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = timeCalendar.get(Calendar.MINUTE);
                TimePickerDialog tpDialog = new TimePickerDialog(this, this, hour, minute, DateFormat.is24HourFormat(getApplicationContext()));
                tpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpDialog.show();
                sendDriver.setEnabled(true);
            }

            catch (Exception e)
            {
                Log.e("DriberCrashEvent", e.toString());
            }
        }

        else if (v.getId() == R.id.sendDriver)
        {
            //Send current location coordinates
            //Put code here to push to database
            Log.i("DriberHireDriverEvent", "Sending coordinates to driver");
            Toast.makeText(this, "Driver hire date: " + hireDate, Toast.LENGTH_LONG).show();
            String mafuyuServiceUrl = "http://mafuyu.atwebpages.com/driber_api/database.php?action=newride" + "&email=" + uEmail + "&password=" + uPass + "&lat=" + Double.parseDouble(latitude) + "&long=" + Double.parseDouble(longitude) + "&date=" + hireDate + "&time=" + hireTime;
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest strRequest = new StringRequest(Request.Method.GET, mafuyuServiceUrl, createMyReqSuccessListener(uEmail, uPass), createMyReqErrorListener());
            queue.add(strRequest);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hire_driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
        {
            // Handle the camera action
        }

        else if (id == R.id.nav_gallery)
        {

        }

        else if (id == R.id.nav_slideshow)
        {

        }

        else if (id == R.id.nav_manage)
        {

        }

        else if (id == R.id.nav_share)
        {

        }

        else if (id == R.id.nav_send)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}