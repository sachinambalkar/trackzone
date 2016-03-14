package cs442.com.fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cs442.com.Activity.MainActivity;
import cs442.com.Adapter.AlertNotifyAdapter;
import cs442.com.Adapter.Information;
import cs442.com.Adapter.VivzAdapter;
import cs442.com.database.Alert;
import cs442.com.pulse.R;
import cs442.com.service.NotificationService;
/**
 * A simple {@link Fragment} subclass.
 */
public class AlertNotificationFragment extends Fragment implements MainActivity.UpdateNotification {

    static View userInfoView=null;
    static boolean showOverlayLayout=false;
    private static View view;
    static HashMap<String,Object> hm=new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_alert_notification, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        Button button_stopAlert=(Button)view.findViewById(R.id.button_stop_alerts);
        button_stopAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NotificationService.allDetails != null) {
                    for (int i = 0; i < NotificationService.allDetails.size(); i++) {
                        Alert alert = MainActivity.datasource.readAlertByID(Integer.parseInt(((Map<String, Object>) NotificationService.allDetails.get(i)).get("ID").toString()));
                        MainActivity.datasource.updateAlert(alert.getAlertID(), alert.getAlertAddress().getAddressID(), alert.getAlertTitle(), alert.getAlertDescription(), alert.getAlertRadius(), 0);
                    }
                    NotificationService.allDetails.clear();
                    Toast.makeText(getActivity(), "All alerts are disabled", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "No alert is active", Toast.LENGTH_SHORT).show();
            }
        });



        if(NotificationService.allDetails !=null)
            if(NotificationService.allDetails.size()!=0)
            {
                PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, true);
                SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(getActivity());
                try {
                    if (setNoti.getBoolean("notify_alert", false))
                        showActivityOverlay(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        hideListView();





         return view;
    }


    @Override
    public void onResume() {
        super.onResume();
//        NotificationService ns=new NotificationService();
        if(NotificationService.allDetails !=null)
            if(NotificationService.allDetails.size()!=0)
            {
                AlertNotifyAdapter notifyAdapter = new AlertNotifyAdapter(getActivity(), NotificationService.allDetails);
                ListView notificationListview = (ListView) view.findViewById(R.id.lv_alertnotifiction);
                notificationListview.setAdapter(notifyAdapter);
            }
        hideListView();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void showActivityOverlay(Context context) throws Exception
    {
        PreferenceManager.setDefaultValues(context, R.xml.prefs, true);
        SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(context);
        setNoti.edit().putBoolean("notify_alert", false).commit();

        LinearLayout layout;
        final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_view_alert);
        layout = (LinearLayout) dialog.findViewById(R.id.overlay_view_alert_id);
        ImageView imgv=(ImageView)dialog.findViewById(R.id.ivOverlayActivity);
        imgv.setImageDrawable(context.getResources().getDrawable(R.drawable.notify_overlay));
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Home");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void  callUpdate(ArrayList<Object> locationDetails,Activity activity) {
        try {
//            NotificationService ns = new NotificationService();
            hideListView();
            AlertNotifyAdapter notifyAdapter = new AlertNotifyAdapter(activity, NotificationService.allDetails);
            ListView notificationListview = (ListView) view.findViewById(R.id.lv_alertnotifiction);
            notificationListview.setAdapter(notifyAdapter);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    public void hideListView(){
        LinearLayout listview_linearlayout=(LinearLayout)view.findViewById(R.id.notification_listlayout);
        if(NotificationService.allDetails==null||NotificationService.allDetails.size()==0)
        {
            LinearLayout ll =(LinearLayout)view.findViewById(R.id.new_alert_notificationLL);
            listview_linearlayout.setVisibility(View.GONE);
            if(userInfoView==null)
            {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                userInfoView = inflater.inflate(R.layout.userinfo_notification, null, false);
                ll.addView(userInfoView);
            }
            else
            {
                ll.removeView(userInfoView);
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                userInfoView = inflater.inflate(R.layout.userinfo_notification, null, false);
                ll.addView(userInfoView);

            }
        }
        else
        {
            listview_linearlayout.setVisibility(View.VISIBLE);
            if(userInfoView!=null)
                userInfoView.setVisibility(View.GONE);


        }


    }
}
