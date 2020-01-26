package com.bitsplease.driber.driberandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainPage extends AppCompatActivity implements View.OnClickListener
{
    EditText userEmailAddress;
    EditText userPassword;
    Button loginButton;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("DriberServiceStart", "Starting Driber...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        userEmailAddress = (EditText) findViewById(R.id.userEmailAddress);
        userPassword = (EditText) findViewById(R.id.userPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerUser);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        Log.i("DriberServiceStart", "Successfully started Driber!");
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.loginButton)
        {
            //This will go to the LoginProccess Class

            if (userEmailAddress.getText().toString().equals("") && userPassword.getText().toString().equals("") || userEmailAddress.getText().toString().equals(null) && userPassword.getText().toString().equals(null))
            {
                userEmailAddress.setText("empty");
                userPassword.setText("empty");
            }

            Log.i("DriberLoginEvent", "Logging in to Driber...");
            verifyCredentials(userEmailAddress.getText().toString(), userPassword.getText().toString());
        }

        else if (v.getId() == R.id.registerUser)
        {
            //User registration
            Intent registration = new Intent(MainPage.this, MainActivity.class);
            startActivity(registration);
        }
    }

    private Response.Listener<String> createMyReqSuccessListener(String userEmail, String userPassword)
    {
        final String uEmail = userEmail;
        final String uPass = userPassword;

        return new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                String goodResponse = "OK";
                String badResponse = "Invalid email/password!";

                if (response.trim().equals(goodResponse))
                {
                    Log.i("DriberLoginEvent", "Successfully logged in!");
                    Intent driverStartService = new Intent(MainPage.this, PickServiceType.class);
                    driverStartService.putExtra("uEmail", uEmail);
                    driverStartService.putExtra("uPass", uPass);
                    startActivity(driverStartService);
                }

                else if (response.trim().equals(badResponse))
                {
                    Toast.makeText(MainPage.this, "Invalid Email Address/Password!", Toast.LENGTH_LONG).show();
                    Log.i("DriberLoginEvent", "Login failed!");
                    Log.i("DriberLoginEvent", "Email entered: " + uEmail);
                    Log.i("DriberLoginEvent", "Password entered: " + uPass);
                }

                else
                {
                    Log.w("DriberUnknownProblem", "Nasaan ka na paginoon? Bakit nangyari ito?. Reponse: " + response);
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


    public void verifyCredentials(String userEmail, String userPassword)
    {
        //Verifies email and password using MySQL.
        //Email and password are valid if the DB returns the same values.
        //As I do not have a DB ready, this will do for now.

        try
        {
            //Volley implementation
            //Mafuyu is my backend service. See more here: http://mafuyu.atwebpages.com

            if (!userEmail.equals("") && !userPassword.equals("") || !userEmail.equals(null) && !userPassword.equals(null))
            {
                RequestQueue queue = Volley.newRequestQueue(this);
                String mafuyuServiceUrl = "http://mafuyu.atwebpages.com/driber_api/database.php?action=login" + "&email=" + userEmail + "&password=" + userPassword;
                StringRequest strRequest = new StringRequest(Request.Method.GET, mafuyuServiceUrl, createMyReqSuccessListener(userEmail, userPassword), createMyReqErrorListener());
                queue.add(strRequest);
            }
        }

        catch (Exception e)
        {
            Log.i("DriberLoginEvent", "Login failed due to an error. See the error stated below.");
            Log.e("DriberDBError", "The login service cannot connect to the database. Reason will be shown below.");
            Log.e("DriberDBError", e.toString());
        }
    }

}