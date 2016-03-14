package cs442.com.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import cs442.com.ActionClass.SharedPreferenceAction;
import cs442.com.Activity.MainActivity;
import cs442.com.database.Address;
import cs442.com.database.Alert;
import cs442.com.database.DatabaseHandler;
import cs442.com.database.DatabaseHelper;
import cs442.com.pulse.R;

public class NotificationService extends Service
{
    static int val=0;
    static  private Firebase myFirebaseRef=null;
    static Activity activity=null;
    private final IBinder mBinder = new MyBinder();
    NotificationManager notificationManager;
  //  private Handler mPeriodicEventHandler;
   // private  int PERIODIC_EVENT_TIMEOUT;
   static double db_longitude[], db_latitude[], db_distance[], db_radius[];
    static int db_status[];
   static long db_alertId[];
   static String db_title[], db_description[];
    public   static ArrayList<Object> allDetails;
   static HashMap<String,Object> hmap;
    static boolean databaseLoaded=false;
    LocationManager locationManager;
    Location sys_location;
    String provider;
    Handler handler = new Handler();
    Runnable runnable;


    StringBuilder title;
    public static DatabaseHandler datasource;

    @Override
    public void onCreate()
    {
        super.onCreate();
        datasource = new DatabaseHandler(this);
        datasource.open();

//        mPeriodicEventHandler = new Handler();
        PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
        if (myFirebaseRef == null)
            myFirebaseRef = new Firebase("https://pulsealert.firebaseio.com/");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider=LocationManager.NETWORK_PROVIDER;
        sys_location = locationManager.getLastKnownLocation(provider);

        if(sys_location==null) {
            provider=LocationManager.GPS_PROVIDER;
            sys_location = locationManager.getLastKnownLocation(provider);
        }
        if(sys_location==null) {
            provider=LocationManager.PASSIVE_PROVIDER;
            sys_location = locationManager.getLastKnownLocation(provider);
        }


        runnable = new Runnable() {
            @Override
            public void run() {
                Firebase myFirebaseRef;
  //              Firebase.setAndroidContext(getApplicationContext());
                myFirebaseRef = new Firebase("https://heal-thy.firebaseio.com/");
                try {
                    Criteria criteria = new Criteria();
                    LocationManager sys_locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    String provider = sys_locationManager.getBestProvider(criteria, true);

                    Location location = sys_locationManager.getLastKnownLocation(provider);
                    callLocationChangedAction(location);
                    updateFacebookLocation(location);
                    handler.postDelayed(this, 5000);
                }catch (SecurityException e){
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(runnable,1000);
        return START_STICKY;
    }

        public void updateFacebookLocation(Location updatedLocation) throws Exception
    {
        String DB_timeZone = "America/Chicago";
        String fbid,fbname;
        try {
            fbid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.facebook_Alert_TitleName), null);
            SharedPreferenceAction sharedPref = new SharedPreferenceAction();
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


    public void setUpdateUI(Activity activityParent) {
       activity= activityParent;
        val++;
        Toast.makeText(activity,"Hello "+val,Toast.LENGTH_LONG).show();
    }

    public void load_db() {
        List<Alert> alerts = datasource.readAllAlerts();
        db_latitude = new double[alerts.size()];
        db_longitude = new double[alerts.size()];
        db_title = new String[alerts.size()];
        db_distance = new double[alerts.size()];
        db_radius = new double[alerts.size()];
        db_status = new int[alerts.size()];
        db_alertId = new long[alerts.size()];
        db_description= new String[alerts.size()];
        for (int iter = 0; iter < alerts.size(); iter++) {
            db_longitude[iter] = alerts.get(iter).getAlertAddress().getAddressLongitude();
            db_latitude[iter] = alerts.get(iter).getAlertAddress().getAddressLatitude();
            db_title[iter] = alerts.get(iter).getAlertTitle();
            db_description[iter] = alerts.get(iter).getAlertDescription();
            db_radius[iter] = alerts.get(iter).getAlertRadius();
            db_status[iter] = alerts.get(iter).getAlertStatus();
            db_alertId[iter] = alerts.get(iter).getAlertID();
        }
        databaseLoaded=true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void triggerNotification(Notification.Builder builder) {
        /**
         * Listing 10-43: Triggering a Notification
         */
        String svc = Context.NOTIFICATION_SERVICE;
        notificationManager
                = (NotificationManager)getSystemService(svc);
        int NOTIFICATION_REF = 1;
        Notification notification = builder.getNotification();
        notificationManager.notify(NOTIFICATION_REF, notification);
    }


    private Notification.Builder notificationBuilder() {
        /**
         * Listing 10-35: Setting Notification options using the Notification Builder
         */

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder =
                new Notification.Builder(getApplicationContext());


        builder.setSmallIcon(R.drawable.pulselogo)
                .setTicker("Notification")
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setContentTitle("Reached at Destination")
                .setContentText("" + title)
                .addAction(R.drawable.delete_red, "Deactivate", pIntent)
                .setLights(Color.RED, 0, 1);
        Uri sound;
try {
    sound = Uri.parse(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ringtone", null));
}catch (Exception e){
    e.printStackTrace();
    sound=null;
}
        if(sound==null)
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
         else
            builder.setSound(sound);
        return builder;
    }



    public void callLocationChangedAction(Location location){
        if(activity!=null)
        {
            ((MainActivity)activity).setLatitudeA(location.getLatitude());
            ((MainActivity)activity).setLongitudeA(location.getLongitude());
        }
        load_db();
        int countinterval=1;
        if (location != null) {
            double latitude_para = location.getLatitude();
            double longitude_para = location.getLongitude();
            if(allDetails!=null)
                allDetails.clear();
            else
                allDetails=new ArrayList<>();
            hmap=null;
            if (db_longitude != null)
            {
                hmap = new HashMap<>(db_latitude.length);
                title=new StringBuilder();
                for (int i = 0; i < db_longitude.length; i++)
                {
                    if (db_status[i] == 1)
                    {
                        double distance=distFrom(db_latitude[i], db_longitude[i], latitude_para, longitude_para);
                        if ( distance<= db_radius[i]) {
                            title = title.append(" ").append(countinterval).append(". ").append(db_title[i]);
                            hmap.put(db_title[i] + "", db_alertId[i]);
                            Map mp=new HashMap();
                            mp.put("ID",db_alertId[i]);
                            mp.put("TITLE",db_title[i]);
                            mp.put("DISTANCE",distance);
                            mp.put("TIME",java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                            allDetails.add(mp);
                            countinterval++;
                        }
                    }
                }
            }
        }

        if (hmap != null) {
            if (hmap.size() > 0) {
                triggerNotification(notificationBuilder());
                if(activity!=null)
                    ((MainActivity)activity).callFromNotification(allDetails);

                try {
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
                triggerNotification(notificationBuilder());
            }
        }

        try {
            updateFacebookLocation(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }


    public class MyBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    public void getLocation(){
        sys_location = locationManager.getLastKnownLocation(provider);
        callLocationChangedAction(sys_location);
    }
}
