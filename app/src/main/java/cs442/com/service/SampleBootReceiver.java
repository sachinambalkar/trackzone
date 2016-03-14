package cs442.com.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import cs442.com.Activity.MainActivity;
import cs442.com.pulse.FacebookUpdateService;

public class SampleBootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentNotify = new Intent(context, NotificationService.class);
        intentNotify.putExtra("title", "title");
        ContentResolver contentResolver = context.getContentResolver();
        // Find out what the settings say about which providers are enabled
        int mode = Settings.Secure.getInt(
                contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()&&!(mode == Settings.Secure.LOCATION_MODE_OFF)) {
            Log.d("NetworkCheckReceiver", "connected");
            if(!isMyServiceRunning(NotificationService.class,context))
            {
                context.startService(intent);
                Toast.makeText(context, "Pulse service on", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("NetworkCheckReceiver", "disconnected");
            if(isMyServiceRunning(NotificationService.class,context))
            {
                context.stopService(intent);
                Toast.makeText(context, "Pulse service off", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}