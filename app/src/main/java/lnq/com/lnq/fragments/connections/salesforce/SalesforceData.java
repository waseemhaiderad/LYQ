package lnq.com.lnq.fragments.connections.salesforce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import lnq.com.lnq.R;

public class SalesforceData extends AppCompatActivity {
    private final Boolean FORCE_LOG_IN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesforce_data);
        //Handle instance where we already have a refresh token and don't need to present the login dialog again
        //save the access tokens to disk for next time
        OAuthTokens tokens = OAuthUtil.Load(getApplicationContext());
        //if we have access tokens saved, use those. Otherwise, log in.
        if (tokens != null && !FORCE_LOG_IN) {
            GlobalState globalState = (GlobalState) getApplication();
            globalState.setAccessTokens(tokens);
            Log.d("TG", "Launching Main View");
            launchMainView();
        } else {
            Log.d("TG", "Launching Login View");
            launchLoginView();
        }

    }

    protected void launchLoginView() {
        Intent i = new Intent(this, Login_Salesforce.class);
        startActivity(i);
    }

    protected void launchMainView() {
        Log.d("TG", "Launched Main View");
    }
}