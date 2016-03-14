package cs442.com.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;
import cs442.com.ActionClass.HomeScreenButton;
import cs442.com.ActionClass.SwipeDismissTouchListener;
import cs442.com.Activity.MainActivity;
import cs442.com.database.Alert;
import cs442.com.pulse.R;

public class AlertNotifyAdapter extends BaseAdapter implements Filterable
{
    static int colorid=0;
    private FriendFilter friendFilter;
    private Activity activity;
    private static LayoutInflater inflater = null;
    private Map<String, Object> newPost[];
    private Map<String, Object> filteredList[];

    double latA,longA;
    final MainActivity.ButtonActions buttonActions;
    final ViewGroup dismissableContainer;
    ArrayList<Object> arrayDetail;
    public AlertNotifyAdapter(Activity activity,ArrayList<Object> arrayDetail)
    {
        dismissableContainer= (ViewGroup)activity.findViewById(R.id.notification_listlayout);
        buttonActions = new HomeScreenButton();
        alertNotifyAdapter(activity,arrayDetail);
    }

    public void alertNotifyAdapter(Activity activity,ArrayList<Object> arrayDetail)
    {
        this.arrayDetail=arrayDetail;
        this.activity=activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int arraySize;
        if(arrayDetail!=null)
            arraySize=arrayDetail.size();
        else
            arraySize=0;
        newPost=new Map[arraySize];
        if(arrayDetail!=null)
            for (int p = 0; p < arraySize; p++) {
                newPost[p] = (Map)arrayDetail.get(p);
            }
        filteredList=newPost;
        latA=((MainActivity) activity).getLatitudeA();
        longA=((MainActivity) activity).getLongitudeA();

/*
        gmap = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.mapFacebook)).getMap();
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.getUiSettings().setCompassEnabled(true);
        gmap.setOnInfoWindowClickListener((MainActivity) activity);
*/
    }

    @Override
    public int getCount() {
        return newPost.length;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View v;
        if(convertView == null)
            v = inflater.inflate(R.layout.homescreen_alertnotify, null);
        else
            v= convertView;



        if(newPost[position]!=null) {
            LinearLayout layout_color=(LinearLayout)v.findViewById(R.id.color_layoutAN);
            TextView tv_name=(TextView)v.findViewById(R.id.hs_alert_title);
            TextView tv_time=(TextView)v.findViewById(R.id.hs_alert_time);
            TextView tv_distance=(TextView)v.findViewById(R.id.hs_alert_distance);
            final String name=newPost[position].get("TITLE").toString();

            switch (colorid)
            {
                case 1:
                    layout_color.setBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    layout_color.setBackgroundColor(Color.BLUE);
                    break;
                case 3:
                    layout_color.setBackgroundColor(Color.RED);
                    break;
                case 4:
                    layout_color.setBackgroundColor(Color.YELLOW);
                    break;
                default:
                    layout_color.setBackgroundColor(Color.BLACK);
                    colorid=0;
                    break;
            }
            colorid++;

            tv_name.setText(name);

            String dateVal= newPost[position].get("TIME").toString();
            tv_time.setText(dateVal);

            final Double distance = (Double) newPost[position].get("DISTANCE");
            tv_distance.setText(round(distance,2)+" Miles");
            final long alertId=(long)newPost[position].get("ID");
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
                            try{
                                Alert alert = MainActivity.datasource.readAlertByID(alertId);
                                MainActivity.datasource.updateAlert(alert.getAlertID(),alert.getAlertAddress().getAddressID(),alert.getAlertTitle(),alert.getAlertDescription(),alert.getAlertRadius(),0);
//                                view.setVisibility(View.GONE);
//                                ((ViewGroup) view.getParent()).removeView(view);
                                arrayDetail.remove(position);
                                alertNotifyAdapter(activity,arrayDetail);
                                notifyDataSetChanged();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }));
            v.setOnClickListener(new View.OnClickListener() {
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

        }




        return v;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilter();
        }
        return friendFilter;
    }


    private class FriendFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                newPost=filteredList;
//                ArrayList<User> tempList = new ArrayList<User>();
                Map tempList[]=new Map[newPost.length];
                // search content in friend list
                int index=0;
                for (int i=0;i<newPost.length;i++) {
                    if (newPost[i].get("TITLE").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList[index++]=newPost[i];
                    }
                }
                Map finalList[]=new Map[index];
                System.arraycopy(tempList,0,finalList,0,index);
                filterResults.count = index;
                filterResults.values =finalList;
            } else {
                filterResults.count = newPost.length;
                filterResults.values = newPost;
            }
            return filterResults;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            newPost = (Map<String, Object>[]) results.values;
            notifyDataSetChanged();
        }
    }
}
