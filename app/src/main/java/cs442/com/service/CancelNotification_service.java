package cs442.com.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CancelNotification_service extends BroadcastReceiver {
    public CancelNotification_service() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Intent service = new Intent();
        service.setComponent(new ComponentName(context,NotificationService.class));
        context.stopService(service);

    }
}
