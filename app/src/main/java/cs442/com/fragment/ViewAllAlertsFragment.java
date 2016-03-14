package cs442.com.fragment;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import cs442.com.Activity.MainActivity;
import cs442.com.Adapter.Information;
import cs442.com.Adapter.VivzAdapter;
import cs442.com.database.Alert;
import cs442.com.database.AlertAdapter;
import cs442.com.pulse.R;

public class ViewAllAlertsFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener
{
    VivzAdapter vivzadapter;
    private RecyclerView recyclerView;
    List<Alert> globalAlerts;
    AlertAdapter adapter;
    List<Alert> alerts;
   // public View rootView;
    public ViewAllAlertsFragment(){}

    private static View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("View Alert");
    }catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_view_all_alerts, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }
           setHasOptionsMenu(true);

        recyclerView=(RecyclerView)view.findViewById(R.id.my_recycler_view);


        return view;
    }


    public static List<Information> getData(){
        List<Information> data=new ArrayList<>();
        int[] icons={R.drawable.pulselogo,R.drawable.delete_blue,R.drawable.ic_drawer};
        String[] titles={"sachin","pravin","wawge"};

        for(int i=0;(i<titles.length)&&(i<icons.length);i++){
            Information information=new Information();
            information.iconId=icons[i];
            information.title=titles[i];
            data.add(information);
        }
        return data;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        alerts= MainActivity.datasource.readAllAlerts();
        globalAlerts=alerts;
        adapter = new AlertAdapter(getActivity(), alerts);
        setListAdapter(adapter);

        vivzadapter=new VivzAdapter(getActivity(),alerts);
        recyclerView.setAdapter(vivzadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if(alerts.size()>0)
        {
            PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, true);
            SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (setNoti.getBoolean("v_alert", true)) {
                try {
                    showActivityOverlay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }



    private void showActivityOverlay() throws Exception
    {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, true);
        SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setNoti.edit().putBoolean("v_alert", false).apply(); ;

        LinearLayout layout;
        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_view_alert);
        layout = (LinearLayout) dialog.findViewById(R.id.overlay_view_alert_id);
        ImageView imgv=(ImageView)dialog.findViewById(R.id.ivOverlayActivity);
        imgv.setImageDrawable(getResources().getDrawable(R.drawable.allalertview));
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
    public void onResume() {
        super.onResume();
        alerts= MainActivity.datasource.readAllAlerts();
        globalAlerts=alerts;
        adapter = new AlertAdapter(getActivity(), alerts);
        setListAdapter(adapter);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        adapter = new AlertAdapter(getActivity(), globalAlerts);
       setListAdapter(adapter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
            if(adapter!=null)
                adapter.getFilter().filter(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(adapter!=null)
            adapter.getFilter().filter(s);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fb_menu,menu);
        SearchManager searchManager      = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView)searchMenuItem.getActionView();
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem,this);
    }
}



