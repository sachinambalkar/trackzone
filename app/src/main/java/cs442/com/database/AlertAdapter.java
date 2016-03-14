package cs442.com.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cs442.com.ActionClass.SwipeDismissTouchListener;
import cs442.com.Activity.MainActivity;
import cs442.com.fragment.EditFragment;
import cs442.com.pulse.R;

public class AlertAdapter extends BaseAdapter implements Filterable
{
        private AlertFilter alertFilter;
        public int valInterator=0;
        private Activity activity;
        private List<Alert> alerts;
        private List<Alert> alertsFiltered;
        private static LayoutInflater inflater = null;
        final ViewGroup dismissableContainer;
        static int colorid=0;
        public AlertAdapter(Activity activity, List<Alert> alerts)
        {
                this.activity = activity;
                this.alerts = alerts;
                this.alertsFiltered=alerts;
                inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                dismissableContainer= (ViewGroup)activity.findViewById(R.id.viewAllAlert_layout);
        }

    public int getCount(){
        return alerts.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, final View convertView, ViewGroup parent){
      final View v;
        if(convertView == null)
            v = inflater.inflate(R.layout.alert, null);
        else
            v= convertView;
       LinearLayout lv=(LinearLayout)v.findViewById(R.id.color_layout);
        TextView title = (TextView)v.findViewById(R.id.title);
        TextView description = (TextView)v.findViewById(R.id.description);
        TextView location = (TextView)v.findViewById(R.id.location);
        final TextView status = (TextView)v.findViewById(R.id.status);
        final RadioButton rb=(RadioButton)v.findViewById(R.id.radioButton_activate);
        Alert alert;
        alert = alerts.get(position);
        final Alert finalAlert = alert;
        if(alert.getAlertStatus()==1)
            rb.setChecked(true);
        else {
            rb.setChecked(false);
            rb.setActivated(true);
        }
        final Alert finalAlert1 = alert;
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rb.isActivated()){
                    Toast.makeText(activity,"Alert Activated",Toast.LENGTH_SHORT).show();
                    rb.setChecked(true);
                    rb.setActivated(false);
                    status.setText("Status: " + 1);
                    updateAlertStatus(1, finalAlert);
                    finalAlert1.setAlertStatus(1);
                    valInterator++;
                }
                else {
                    Toast.makeText(activity, "Alert Deactivated", Toast.LENGTH_SHORT).show();
                    rb.setChecked(false);
                    rb.setActivated(true);
                    status.setText("Status: "+0);
                    updateAlertStatus(0,finalAlert);
                    finalAlert1.setAlertStatus(0);
                }
            }
        });
        title.setText(alert.getAlertTitle());
        description.setText(alert.getAlertDescription());
        double distance;
        if(((MainActivity) activity).getLatitudeA()==0) {
            distance = 0;
            location.setText("Distance: error");
        }
        else {
            distance = MainActivity.calcDistance(
                    ((MainActivity) activity).getLatitudeA(),
                    ((MainActivity) activity).getLongitudeA(),
                    alert.getAlertAddress().getAddressLatitude(),
                    alert.getAlertAddress().getAddressLongitude());
            location.setText("Distance: "+Math.round(distance*1000)/1000.f+" miles");
        }

        status.setText("Status: "+alert.getAlertStatus());

        switch (colorid)
        {
            case 1:
                lv.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                lv.setBackgroundColor(Color.BLUE);
                break;
            case 3:
                lv.setBackgroundColor(Color.RED);
                break;
            case 4:
                lv.setBackgroundColor(Color.YELLOW);
                break;
            default:
                lv.setBackgroundColor(Color.BLACK);
                colorid=0;
                break;
        }
        colorid++;

        v.setOnTouchListener(new SwipeDismissTouchListener(
                v,
                null,
                new SwipeDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }
                    @Override
                    public void onDismiss(View view, Object token) {

                        dismissableContainer.removeView(v);
                        v.setVisibility(View.GONE);
                        ((ActionBarActivity)activity).getSupportActionBar().setTitle("Edit Alert");
                        Fragment mfrag= null;
                        try {
                            mfrag = new EditFragment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Bundle data=new Bundle();
                        data.putLong(activity.getResources().getString(R.string.alertId), finalAlert.getAlertID());
                        if (mfrag != null) {
                            mfrag.setArguments(data);
                        }
                        FragmentManager fragmentManager = ((MainActivity)activity).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container,mfrag)
                                .commit();

                    }
                }));


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Animation an;
        an = AnimationUtils.loadAnimation(activity, R.anim.dp_animation);
        v.startAnimation(an);

        return v;
    }


   public void updateAlertStatus(int status, Alert alert)
   {
       MainActivity.datasource.updateAlert(alert.getAlertID(),alert.getAlertAddress().getAddressID(),alert.getAlertTitle(),alert.getAlertDescription(),alert.getAlertRadius(),status);
       ((MainActivity)activity).changeAlertStatus(alert.getAlertID(),status);
   }

    @Override
    public Filter getFilter() {
        if(alertFilter==null)
           alertFilter=new AlertFilter();
        return alertFilter;
    }


    private class AlertFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                alerts=alertsFiltered;

                ArrayList<Alert> tempList=new ArrayList<>();
                // search content in alert list
                for (int i=0;i<alerts.size();i++) {
                    Boolean condition1=(alerts.get(i)).getAlertAddress().toString().toLowerCase().contains(constraint.toString().toLowerCase());
                    Boolean condition2 =(alerts.get(i)).getAlertTitle().toString().toLowerCase().contains(constraint.toString().toLowerCase());
                    if (condition1||condition2) {
                        tempList.add(alerts.get(i));
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values =tempList;
            } else {
                filterResults.count = alerts.size();
                filterResults.values = alerts;
            }
            return filterResults;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            alerts = (ArrayList<Alert>)results.values;
            notifyDataSetChanged();
        }
    }


}
