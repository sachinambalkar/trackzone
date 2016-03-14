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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import cs442.com.ActionClass.HomeScreenButton;
import cs442.com.ActionClass.MapHelper;
import cs442.com.Activity.MainActivity;
import cs442.com.pulse.R;

/**
 * Created by Sachin on 04-08-2015.
 */
public class FblistAdapter extends BaseAdapter implements Filterable
{
    private MapHelper mhelper;
    private FriendFilter friendFilter;
    private Activity activity;
    private static LayoutInflater inflater = null;
    private Map<String, Object> newPost[];
    private Map<String, Object> filteredList[];

    double latA,longA;
    final MainActivity.ButtonActions buttonActions;
    GoogleMap gmap;

    public FblistAdapter(Activity activity, Map<String, Object> root,ArrayList<String> id)
    {
        mhelper=new MapHelper();
        this.activity=activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int idSize;
        if(id!=null)
            idSize=id.size();
        else
            idSize=0;



        newPost=new Map[idSize];
        if(root!=null)
        for (int p = 0; p < idSize; p++) {
            newPost[p] = (Map<String, Object>) root.get(id.get(p));
        }
        filteredList=newPost;
        latA=((MainActivity) activity).getLatitudeA();
        longA=((MainActivity) activity).getLongitudeA();
        buttonActions = new HomeScreenButton();
        gmap = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.mapFacebook)).getMap();
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.getUiSettings().setCompassEnabled(true);
        gmap.setOnInfoWindowClickListener((MainActivity) activity);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v;
        if(convertView == null)
            v = inflater.inflate(R.layout.friend_details, null);
        else
            v= convertView;
        if(newPost[position]!=null) {
            TextView tv_srno=(TextView)v.findViewById(R.id.friend_srno);
            TextView tv_name=(TextView)v.findViewById(R.id.friend_name);
            TextView tv_time=(TextView)v.findViewById(R.id.friend_timeDetail);
            TextView tv_distance=(TextView)v.findViewById(R.id.friend_distanceDetail);
            final String name=newPost[position].get("NAME").toString();
            tv_srno.setText( String.valueOf(position + 1));
            tv_name.setText(name);

            String timezone = TimeZone.getDefault().getID();
            String timeFormat=activity.getString(R.string.time_format);
            String dateVal= newPost[position].get("TIME").toString();
            SimpleDateFormat sdf= new SimpleDateFormat(timeFormat);
            if(!timezone.equals(activity.getString(R.string.db_timezone)))
            {
                TimeZone istTimeZone = TimeZone.getTimeZone(timezone);
                Date dateToConvert = null;
                try {
                    DateFormat format3 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                    dateToConvert = format3.parse(dateVal);
                } catch (ParseException e1) {
                        try{
                            DateFormat format3 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                            dateToConvert = format3.parse(dateVal);
                        }catch (ParseException e2){
                            try {
                                DateFormat format3 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                                dateToConvert = format3.parse(dateVal);
                            }catch (ParseException e3){
                                e3.printStackTrace();
                            }
                        }
                }
                if(dateToConvert!=null){
                    sdf.setTimeZone(istTimeZone);
                    dateVal = sdf.format(dateToConvert);
                }
                else
                    dateVal="Error";
            }

            tv_time.setText(dateVal);
            final Double latitudeVal = (Double) newPost[position].get("LAT");
            final Double longitudeVal = (Double) newPost[position].get("LON");
            double distance = MainActivity.calcDistance(latA, longA,latitudeVal, longitudeVal);
            tv_distance.setText(round(distance,2)  +" Miles");
            if(position%2==0) {
                v.setBackgroundColor(Color.rgb(204, 204, 204));
            }else {
                v.setBackgroundColor(activity.getResources().getColor(R.color.highlighted_text_material_light));
            }
            final LatLng currentPosition = new LatLng(latitudeVal,longitudeVal);
            final MarkerOptions mMarker=new MarkerOptions().position(currentPosition).snippet((newPost[position].get("TIME").toString())).title((newPost[position].get("NAME").toString()));
            final Marker marker=gmap.addMarker(mMarker);
            LinearLayout bv=(LinearLayout)v.findViewById(R.id.friend_linearlayout);
            bv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 10));
                    mhelper.setMarker(marker);
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
                Map tempList[]=new Map[newPost.length];
                int index=0;

                for(Map map:newPost){
                    if(map.get("NAME").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList[index]=map;
                        index++;
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
