package cs442.com.fragment;


import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import cs442.com.pulse.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {


    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_help, container, false);
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.list);
        elv.setAdapter(new ExpandableListAdapter(getActivity()));
        return v;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private String[] groups =  { "Home Screen", "Add Alert", "View Alert",
                "Locate Friends"};

        private String[][] children = {
                //Home Screen
                { "The home screen is divided into two tabs.The first tab displays the list of all the notifications.The second tab displays a list of all the alerts and the location on the map"},

                //Add Alert
                { "You can add new alerts in this screen.There are few mandatory fields that needs to be filled in for you to be able to save a new alert.Title (Mandatory) - This gives a name to the alert for easy reference and is compulsory to enter.Description(Optional) - This is an optional field for some additional information on the alert.Address(Mandatory) - This is mandatory and the user has the option to either fill in the address manually and Google places will prompt with auto- complete suggestions.Alternatively the user can select a new location from the Google maps.The user also has the option to select the current location for the alert"},

                //View Alerts
                { "The user can view the list of all the alerts in this screen.Swipe to the left/right or tap on the alert to manage the alert. This will take you to the Edit Alert screen where you have the choice to edit the selected alert and save it.Tap on the On/Off button to activate/deactivate the alert" },

                //Locate Friends
                { "This is a new feature to locate Facebook friends and use their location as an alert.All the users(your friends) who are on Facebook and who have logged into Pulse app can be found here.You can tap on the location of any of the user and create a new alert." },

        };

        private Context myContext;
        public ExpandableListAdapter(Context context) {
            myContext = context;
        }
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        R.layout.article_list_child_item_layout,parent);
            }
            TextView yourSelection = (TextView) convertView
                    .findViewById(R.id.articleContentTextView);
            yourSelection
                    .setText(children[groupPosition][childPosition]);
            return convertView;
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }
        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }
        @Override
        public int getGroupCount() {
            return groups.length;
        }
        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        R.layout.article_list_item_layout, parent);
            }
            TextView groupName = (TextView) convertView
                    .findViewById(R.id.articleHeaderTextView);
            groupName.setText(groups[groupPosition]);
            return convertView;
        }
        @Override
        public boolean hasStableIds() {
            return false;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    /*

    public class SavedTabsListAdapter extends BaseExpandableListAdapter {

        private String[] groups =  { "Home Screen", "Add Alert", "View Alert",
                "Locate Friends"};

        private String[][] children = {
                //Home Screen
                { "This is the main screen. The first half of the screen shows the list of all the active alerts, Scroll to the right to view more alerts. ",
                        "The second half of the screen shows the alerts on the map for easy reference. You can tap on the location to view further details on the alert" },

                //Add Alert
                { "You can add new alerts in this screen",
                        "There are few mandatory fields that needs to be filled in for you to be able to save a new alert",
                        "Title (Mandatory) - This gives a name to the alert for easy reference and is compulsory to enter",
                        "Description(Optional) - This is an optional field for some additional information on the alert",
                        " NEED TO ADD FURTHER DETAILS HERE!"},

                //View Alerts
                { "The user can view the list of all the alerts in this screen",
                        "Swipe to the left/right to manage the alert. This will take you to the Edit Alert screen where you have the choice to edit the selected alert and save it",
                        "Tap on the On/Off button to activate/deactivate the alert" },

                //Locate Friends
                { "This is a new feature to locate Facebook friends and use their location as an alert",
                        "All the users(your friends) who are on Facebook and who have logged into Pulse app can be found here",
                        "You can tap on the location of any of the user and create a new alert." },
        };
        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public int getChildrenCount(int i) {
            return children[i].length;
        }

        @Override
        public Object getGroup(int i) {
            return groups[i];
        }

        @Override
        public Object getChild(int i, int i1) {
            return children[i][i1];
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup)
        {
            TextView textView = new TextView(HelpFragment.this.getActivity());
            textView.setText(getGroup(i).toString());
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(HelpFragment.this.getActivity());
            textView.setText(getChild(i, i1).toString());
            textView.setTextSize(20);
            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

    }


    */

}
