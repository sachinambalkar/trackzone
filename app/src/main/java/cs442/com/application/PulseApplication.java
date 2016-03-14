package cs442.com.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

/**
 * Created by Sachin on 03-04-2015.
 */
public class PulseApplication extends Application
{

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
    }
}
