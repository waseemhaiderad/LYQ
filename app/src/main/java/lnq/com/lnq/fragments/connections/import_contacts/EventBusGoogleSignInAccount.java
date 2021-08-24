package lnq.com.lnq.fragments.connections.import_contacts;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class EventBusGoogleSignInAccount {
    private GoogleSignInAccount signInAccount;

    public EventBusGoogleSignInAccount(GoogleSignInAccount signInAccount) {
        this.signInAccount = signInAccount;
    }

    public GoogleSignInAccount getSignInAccount() {
        return signInAccount;
    }

    public void setSignInAccount(GoogleSignInAccount signInAccount) {
        this.signInAccount = signInAccount;
    }
}
