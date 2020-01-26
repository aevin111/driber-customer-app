package com.bitsplease.driber.driberandroid;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    Button sendRegistration;
    EditText emailAddress;
    EditText password;
    EditText confirmPassword;
    EditText phoneNumber;

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.sendRegistration)
        {
            Log.i("DriberUserRegistration", "Sending registration...");

            if (password.getText().toString().equals(confirmPassword.getText().toString()))
            {
                registerUser();
            }

            else
            {
                Toast.makeText(MainActivity.this, "Your passwords do not match!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("DriberUserRegistration", "Loading registration form...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        sendRegistration = (Button) findViewById(R.id.sendRegistration);
        sendRegistration.setOnClickListener(this);
        Log.i("DriberUserRegistration", "Registration form loaded!");
    }

    private Response.Listener<String> createMyReqSuccessListener(String userEmail, String userPassword, String phoneNumber)
    {
        final String uEmail = userEmail;
        final String uPass = userPassword;
        final String uNum = phoneNumber;

        return new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                String goodResponse = "OK";
                String badResponse = "Invalid email/password!";

                if (response.trim().equals(goodResponse))
                {
                    Toast.makeText(MainActivity.this, "You are now registered. Go back to the main page and login using your credentials", Toast.LENGTH_LONG).show();
                    Log.i("DriberUserRegistration", "Done!");
                    onBackPressed();
                }

                else if (response.trim().equals(badResponse))
                {
                    Toast.makeText(MainActivity.this, "Please try again!", Toast.LENGTH_LONG).show();
                    Log.i("DriberUserRegistration", "Failed.");
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

    public void registerUser()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String mafuyuServiceUrl = "http://mafuyu.atwebpages.com/driber_api/database.php?action=register" + "&email=" + emailAddress.getText().toString() + "&password=" + password.getText().toString() + "&phone=" + phoneNumber.getText().toString();
        StringRequest strRequest = new StringRequest(Request.Method.GET, mafuyuServiceUrl, createMyReqSuccessListener(emailAddress.getText().toString(), password.getText().toString(), phoneNumber.getText().toString()), createMyReqErrorListener());
        queue.add(strRequest);
    }
}
