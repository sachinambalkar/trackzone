package cs442.com.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.animation.AnimationsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs442.com.ActionClass.SwipeDismissTouchListener;
import cs442.com.Activity.MainActivity;
import cs442.com.database.Alert;
import cs442.com.fragment.EditFragment;
import cs442.com.pulse.R;

/**
 * Created by Sachin on 02-02-2016.
 */
public class VivzAdapter extends RecyclerView.Adapter<VivzAdapter.MyviewHolder>  implements Filterable
{
    private AlertFilter alertFilter;
    public int valInterator=0;
    LayoutInflater layoutInflater;
    List<Alert> alerts= Collections.emptyList();
    private List<Alert> alertsFiltered= Collections.emptyList();

    Context context;
    static int colorid=0;
    private int previousPosition=0;
    final ViewGroup dismissableContainer;

    public VivzAdapter(Context context,List<Alert> alerts){
        this.alerts=alerts;
        this.alertsFiltered=alerts;
        layoutInflater = LayoutInflater.from(context);
        this.context=context;
        dismissableContainer= (ViewGroup)((MainActivity)context).findViewById(R.id.viewAllAlert_layout);
    }
    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       View view= layoutInflater.inflate(R.layout.alert,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {
 //       if(current!=null){
///        holder.textView.setText(current.title);
        //holder.imageView.setImageResource(current.iconId);
   //     }
        /////////////////////////////////////////////////
        Alert alert;
        alert = alerts.get(position);
        final Alert finalAlert = alert;
        if(alert.getAlertStatus()==1)
            holder.rb.setChecked(true);
        else {
            holder.rb.setChecked(false);
            holder.rb.setActivated(true);
        }
//        Alert finalAlert1 = alert;
        final Alert finalAlert1 = alert;
        final StringBuilder sb=new StringBuilder("Status: ");

        holder.rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((holder.rb).isActivated()){
//                    ValCheck[valInterator]=idval;
                    Toast.makeText(context, "Alert Activated", Toast.LENGTH_SHORT).show();
                    holder.rb.setChecked(true);
                    holder.rb.setActivated(false);
                    sb.append(1);
                    holder.status.setText(sb.toString());
                    updateAlertStatus(1, finalAlert);
                    finalAlert1.setAlertStatus(1);
                    valInterator++;
                }
                else {
                    Toast.makeText(context, "Alert Deactivated", Toast.LENGTH_SHORT).show();
                    holder.rb.setChecked(false);
                    holder.rb.setActivated(true);
                    sb.append(0);
                    holder.status.setText(sb.toString());
                    updateAlertStatus(0,finalAlert);
                    finalAlert1.setAlertStatus(0);
                }
            }
        });

        holder.title.setText(alert.getAlertTitle());
        holder.description.setText(alert.getAlertDescription());
        double distance;
        if(((MainActivity)context).getLatitudeA()==0) {
            holder.location.setText("Distance: error");
        }
        else {
            distance = MainActivity.calcDistance(
                    ((MainActivity) context).getLatitudeA(),
                    ((MainActivity) context).getLongitudeA(),
                    alert.getAlertAddress().getAddressLatitude(),
                    alert.getAlertAddress().getAddressLongitude());
            holder.location.setText("Distance: "+Math.round(distance*1000)/1000.f+" miles");
        }
        //location.setText("Lat: "+alert.getAlertAddress().getAddressLatitude()+" | Long: "+alert.getAlertAddress().getAddressLongitude()+"\n"+"Distance: "+dis+" miles");
        holder.status.setText("Status: "+alert.getAlertStatus());

        switch (colorid)
        {
            case 1:
                holder.lv.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                holder.lv.setBackgroundColor(Color.BLUE);
                break;
            case 3:
                holder.lv.setBackgroundColor(Color.RED);
                break;
            case 4:
                holder.lv.setBackgroundColor(Color.YELLOW);
                break;
            default:
                holder.lv.setBackgroundColor(Color.BLACK);
                colorid=0;
                break;
        }
        colorid++;

        holder.v.setOnTouchListener(new SwipeDismissTouchListener(
                holder.v,
                null,
                new SwipeDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }
                    @Override
                    public void onDismiss(View view, Object token) {
                        // Toast.makeText(activity, "t: "+finalAlert.getAlertID(),Toast.LENGTH_LONG).show();
                        dismissableContainer.removeView(holder.v);
                        holder.v.setVisibility(View.GONE);
                        ((ActionBarActivity)context).getSupportActionBar().setTitle("Edit Alert");
                        Fragment mfrag= null;
                        try {
                            mfrag = new EditFragment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Bundle data=new Bundle();
                        data.putLong(context.getResources().getString(R.string.alertId), finalAlert.getAlertID());
                        if (mfrag != null) {
                            mfrag.setArguments(data);
                        }
                        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container,mfrag)
                                .commit();
                    }
                }));

        // dismissableContainer.addView(v);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dismissableContainer.removeView(v);
//                v.setVisibility(View.INVISIBLE);
/*
                Fragment mfrag=new EditFragment();
                Bundle data=new Bundle();
                data.putLong("alertID", finalAlert.getAlertID());
                mfrag.setArguments(data);
                FragmentManager fragmentManager = ((MainActivity)activity).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container,mfrag)
                        .commit();
*/

            }
        });











        /////////////////////////////////////////////////

        if(position> previousPosition)
        {
            AnimationsUtils.animate(holder,true);
        }
        else
        {
            AnimationsUtils.animate(holder,false);
        }
        previousPosition=position;
    }


    @Override
    public int getItemCount() {
        return alerts.size();
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



    class MyviewHolder extends RecyclerView.ViewHolder{

        LinearLayout lv;
        TextView title,location,status,description;
        RadioButton rb;
        View v;
        public MyviewHolder(View itemView) {
            super(itemView);
            v=itemView;
            lv=(LinearLayout)itemView.findViewById(R.id.color_layout);
            title = (TextView)itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
            location = (TextView)itemView.findViewById(R.id.location);
            status = (TextView)itemView.findViewById(R.id.status);
            rb=(RadioButton)itemView.findViewById(R.id.radioButton_activate);
//            textView=(TextView)itemView.findViewById(R.id.liatTextView);
  //          imageView=(ImageView)itemView.findViewById(R.id.list_item);
        }
    }


    public void updateAlertStatus(int status, Alert alert)
    {
        MainActivity.datasource.updateAlert(alert.getAlertID(),alert.getAlertAddress().getAddressID(),alert.getAlertTitle(),alert.getAlertDescription(),alert.getAlertRadius(),status);
        ((MainActivity)context).changeAlertStatus(alert.getAlertID(),status);
//       Toast.makeText(activity,"Val: "+val,Toast.LENGTH_SHORT).show();
    }


}
