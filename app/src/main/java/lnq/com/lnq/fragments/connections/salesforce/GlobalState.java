package lnq.com.lnq.fragments.connections.salesforce;

import android.app.Application;

public class GlobalState extends Application {
    private OAuthTokens accessTokens;

    /** Access Tokens**/
    public OAuthTokens getAccessTokens() { return accessTokens; }
    public void setAccessTokens(OAuthTokens accessTokens) { this.accessTokens = accessTokens; }

}
