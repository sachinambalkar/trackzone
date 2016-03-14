package cs442.com.pulse;
        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.preference.PreferenceManager;
        import android.widget.Toast;

        import com.firebase.client.Firebase;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.TimeZone;
        import java.util.concurrent.ExecutionException;

        import cs442.com.ActionClass.SharedPreferenceAction;

public class FacebookUpdateService extends Service implements LocationListener
{
    static  private Firebase myFirebaseRef=null;
    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    public FacebookUpdateService() {
        //notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            if (myFirebaseRef == null)
                myFirebaseRef = new Firebase("https://pulsealert.firebaseio.com/");
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener listener = new FacebookUpdateService();
            String provider;
            Location loc;

            provider = LocationManager.NETWORK_PROVIDER;
            locationManager.requestLocationUpdates(provider, 4000, 0, listener);
            loc = locationManager.getLastKnownLocation(provider);
            if (loc == null) {
                provider = LocationManager.GPS_PROVIDER;
                locationManager.requestLocationUpdates(provider, 4000, 0, listener);
                loc = locationManager.getLastKnownLocation(provider);
            }
            if (loc == null) {
                provider = LocationManager.PASSIVE_PROVIDER;
                locationManager.requestLocationUpdates(provider, 4000, 0, listener);
                loc = locationManager.getLastKnownLocation(provider);
            }

//        try {
//           // updateFacebookLocation(loc);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return super.onStartCommand(intent, flags, startId);
    }

/*
    public void updateFacebookLocation(Location updatedLocation) throws Exception
    {
        String DB_timeZone = "America/Chicago";
        String tempTitle = getString(R.string.facebook_Alert_TitleName);
        String fbid,fbname;
        try {
            fbid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.facebook_Alert_TitleName), null);
            SharedPreferenceAction  sharedPref = new SharedPreferenceAction();
            fbname = sharedPref.getValue(getApplicationContext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fbid=null;
            fbname=null;
        }
        if (fbid!=null)
        {
            String timezone = TimeZone.getDefault().getID();
            String TimeFormat=getString(R.string.time_format);
            //String mydate= java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat);
            Date now = new Date();
            String mydate = sdf.format(now);

            if(!timezone.equals(DB_timeZone))
            {
                TimeZone istTimeZone = TimeZone.getTimeZone(DB_timeZone);
                sdf.setTimeZone(istTimeZone);
                mydate = sdf.format(now);
            }
            Map<String, Object> idChild = new HashMap<>();
            idChild.put("LAT", updatedLocation.getLatitude());
            idChild.put("LON", updatedLocation.getLongitude());
            idChild.put("NAME", fbname);
            idChild.put("TIME", mydate);
            myFirebaseRef.child("/" + fbid).updateChildren(idChild);
        }
    }
*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
