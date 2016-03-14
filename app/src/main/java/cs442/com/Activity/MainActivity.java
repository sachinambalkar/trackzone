
package cs442.com.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import java.util.ArrayList;
import java.util.List;
import cs442.com.ActionClass.HomeScreenButton;
import cs442.com.ActionClass.SharedPreferenceAction;
import cs442.com.database.Alert;
import cs442.com.fragment.AlertNotificationFragment;
import cs442.com.fragment.NavigationDrawerFragment;
import cs442.com.database.DatabaseHandler;
import cs442.com.fragment.NewAlertFragment;
import cs442.com.fragment.ViewAllAlertsFragment;
import cs442.com.pulse.R;
import cs442.com.service.NotificationService;

    public class MainActivity extends ActionBarActivity implements GoogleMap.OnInfoWindowClickListener
    {

        private NotificationService s;
        double latitudeA, longitudeA;
        double db_longitude[], db_latitude[], db_distance[],db_radius[];
        int db_status[];
        long db_alertId[];

        String username;
        String db_title[];

        NavigationDrawerFragment mNavigationDrawerFragment;
        CharSequence mTitle;
        SharedPreferenceAction sharedPref;
        public static DatabaseHandler datasource;
        ButtonActions buttonActions;
        UpdateNotification updateNotification;

    /*** OVERRIDDEN FUNCTIONS ***/
    @Override
    public View onCreateView(View parent, String name, Context context,AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = new SharedPreferenceAction();
        PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
        datasource = new DatabaseHandler(this);
        datasource.open();
        buttonActions = new HomeScreenButton();
        updateNotification=new AlertNotificationFragment();
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout),(LinearLayout)findViewById(R.id.HomeScreenMap_linearlayout));
        mTitle = getTitle();

       //updateWithNewLocation(sys_location);
        load_all_locations();
        checkLocationAndInternet();
    }

    public void checkLocationAndInternet()
    {
        LocationManager lm;
        boolean gps_enabled = false,network_enabled = false;
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){ex.printStackTrace();}

        final Context context=getApplication();
        if(!gps_enabled && !network_enabled)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage(context.getResources().getString(R.string.enableInternetGPSMessage));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    (MainActivity.this).startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        }
        else
        {
            ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(!(conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()))
            {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage(context.getResources().getString(R.string.enableInternetGPSMessage));
                dialog.setPositiveButton(context.getResources().getString(R.string.open_dataconnection_settings), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent( Settings.ACTION_WIFI_SETTINGS);
                        (MainActivity.this).startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                }
                });
                dialog.show();
            }
        }
    }


    @Override
    protected void onResume() {
        datasource.open();
        checkLocationAndInternet();
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.putExtra("title", "title");
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        super.onResume();
    }
    @Override
    protected void onPause() {
        datasource.close();
      //  unbindService(mConnection);
        super.onPause();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        buttonActions.showPopup(this, marker,getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
    }

/*** CUSTOM FUNCTIONS ***/
    public String getUsername() {
        if(username == null) {
            if (sharedPref == null) {
                sharedPref = new SharedPreferenceAction();
                username = sharedPref.getValue(getApplicationContext());
            }
        }
        return username;
    }

    public void setUsername(String name) {
        username = name;
        //if(sharedPref == null)
        {
            sharedPref = new SharedPreferenceAction();
            sharedPref.save(getApplicationContext(), username);
        }
    }



    public boolean load_all_locations(){
        List<Alert> alerts = datasource.readAllAlerts();
        db_latitude = new double[alerts.size()];
        db_longitude = new double[alerts.size()];
        db_title = new String[alerts.size()];
        db_distance=new double[alerts.size()];
        db_radius=new double[alerts.size()];
        db_status=new int[alerts.size()];
        db_alertId=new long[alerts.size()];
        for(int iter = 0; iter < alerts.size(); iter++){
            db_longitude[iter] = alerts.get(iter).getAlertAddress().getAddressLongitude();
            db_latitude[iter] = alerts.get(iter).getAlertAddress().getAddressLatitude();
            db_title[iter] = alerts.get(iter).getAlertTitle();
            db_radius[iter] = alerts.get(iter).getAlertRadius();
            db_status[iter] = alerts.get(iter).getAlertStatus();
            db_alertId[iter] = alerts.get(iter).getAlertID();
  //          drawMarker(db_latitude[iter], db_longitude[iter], db_title[iter]);
        }
        return true;
    }



    public void changeAlertStatus(long id,int status){
       int val;
        for(val=0;val<db_alertId.length;val++)
            if(db_alertId[val]==id)
                break;
        if(val<db_alertId.length) {
            if (status == 0 && db_status[val] == 1)
                stopService(new Intent(this, NotificationService.class));
            db_status[val] = status;
        }
    }


/*
    public void clearMap() {
        if(mMap!=null)
            mMap.clear();
    }
*/





    public double getLatitudeA()
    {
        return  latitudeA;
    }
    public double getLongitudeA()
    {
        return  longitudeA;
    }

    public void setLatitudeA(double latitudeA){ this.latitudeA=latitudeA; }
    public void setLongitudeA(double longitudeA) { this.longitudeA= longitudeA;}


    public static double calcDistance(double latA, double longA, double latB, double longB) {
        double theDistance = (Math.sin(Math.toRadians(latA)) *
        Math.sin(Math.toRadians(latB)) +
        Math.cos(Math.toRadians(latA)) *
        Math.cos(Math.toRadians(latB)) *
        Math.cos(Math.toRadians(longA - longB)));
        return Math.toDegrees(Math.acos(theDistance)) * 69.09;
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

        private ServiceConnection mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className,IBinder binder) {
                NotificationService.MyBinder b = (NotificationService.MyBinder) binder;
                s = b.getService();
                s.setUpdateUI(MainActivity.this);
            }
            public void onServiceDisconnected(ComponentName className) {
                s = null;
            }
        };


        public void callFromNotification(ArrayList<Object> data){
            updateNotification.callUpdate(data, MainActivity.this);
        }


        public void createSampleAlert(View v){
            SampleAlert sampleAlert=new NewAlertFragment();
            sampleAlert.createOneSampleAlert(this);
            Toast.makeText(this,"Alert created",Toast.LENGTH_SHORT).show();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container,new ViewAllAlertsFragment()).commit();
            try {
                s.getLocation();
            }catch(Exception e){
                e.printStackTrace();
            }
        }



        /*** CUSTOM INTERFACES ***/
        public interface ButtonActions {
            void showPopup(final Activity context, final Marker marker,final FragmentManager fm);
        }

        public  interface UpdateNotification{
            void callUpdate(ArrayList<Object> locationDetails,Activity activity);
        }
        public interface SampleAlert{
            void createOneSampleAlert(Activity activity);
        }



    }