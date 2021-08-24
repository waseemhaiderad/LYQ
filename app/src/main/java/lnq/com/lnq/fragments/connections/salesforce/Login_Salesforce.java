package lnq.com.lnq.fragments.connections.salesforce;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.model.event_bus_models.EventBusExportUsers;
import lnq.com.lnq.model.event_bus_models.EventBusSalesforceLogin;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Login_Salesforce extends Fragment {

    WebView webview;
    String callbackUrl;

    public Login_Salesforce() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login__salesforce, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String consumerKey = this.getResources().getString(R.string.consumer_key).toString();
        String url = this.getResources().getString(R.string.oAuthUrl).toString();
        callbackUrl = this.getResources().getString(R.string.callbackUrl).toString();

        //the url to load into the web view -- this will bring up the SFDC login page, formatted for a mobile device
        String reqUrl = url + consumerKey + "&redirect_uri=" + callbackUrl;

        //find the web view
        webview = view.findViewById(R.id.webview);

        webview.setWebViewClient(new LoginWebViewClient(getActivity()));

        webview.getSettings().setJavaScriptEnabled(true);

        Log.d("Login URL", reqUrl);

        webview.loadUrl(reqUrl);
        webview.requestFocus(View.FOCUS_DOWN);
        webview.clearHistory();
        webview.clearCache(true);
        webview.clearFormData();
        webview.clearSslPreferences();
        WebStorage.getInstance().deleteAllData();
        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    private class LoginWebViewClient extends WebViewClient {

        Activity act;

        public LoginWebViewClient(Activity myAct) {
            act = myAct;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            Log.d("TG:", "Redirect URL: " + url);

            //check if the redirect URL starts with the callbackUrl
            //if it does, we're done with the web view, and need to parse the tokens we got back from SFDC
            if (url.startsWith(callbackUrl)) {
                if (!url.contains("error")) {

                    Log.d("TG", "Redirecting to Main View");

                    //parse the access tokens from the callbackUrl redirect
                    OAuthTokens accessTokens = OAuthUtil.parseToken(url);

                    //save the access tokens to disk for next time
                    OAuthUtil.Save(accessTokens, getApplicationContext());

                    //keep track of the access tokens in the model
                    GlobalState gs = new GlobalState();
                    gs.setAccessTokens(accessTokens);

                    //DEBUG LOG--------------------------------------------------------
                    /*Log.d("OAUTH", "ID: " + accessTokens.get_id());
                    Log.d("OAUTH", "ORG ID: " + accessTokens.get_org_id());
                    Log.d("OAUTH", "User ID: " + accessTokens.get_user_id());
                    Log.d("OAUTH", "Access  Token: " + accessTokens.get_access_token());
                    Log.d("OAUTH", "Refresh Token: " + accessTokens.get_refresh_token());
                    OAuthUtil.RefreshToken(getApplicationContext(), accessTokens.get_refresh_token());
                    Log.d("OAUTH", "Refresh Token: " + accessTokens.get_refresh_token());*/
                    //-----------------------------------------------------------------
                    EventBus.getDefault().post(new EventBusSalesforceLogin());
                    getActivity().onBackPressed();
                    //redirect to the main view controller
//	        		Intent i = new Intent(act, MainViewController.class);
//	        		startActivity(i);
                } else {
                    Toast.makeText(act, "Error, could not log in. Access denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}