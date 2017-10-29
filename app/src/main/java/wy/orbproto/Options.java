package wy.orbproto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Options extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView upcomingschedule;
    private FirebaseAuth auth;
    private DatabaseReference root;
    private String user;
    private String currentteam;
    private ArrayList<String> listofEvent = new ArrayList<String>();
    private ArrayList<String> listofTime = new ArrayList<String>();
    private dataListAdapter listAdapter;
    private ListView listView;
    private ArrayList<String> list_of_teams = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    private DatabaseReference use = FirebaseDatabase.getInstance().getReference("Users");
    private ArrayList<String> listofdaytodisplay = new ArrayList<String>();
    private String []nameDays = new String[7];
    ArrayList<String> listofadmin = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        upcomingschedule = (ListView) findViewById(R.id.upcomingschedule);
        auth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();
        user = auth.getCurrentUser().getUid();
        listView = (ListView) findViewById(R.id.listView);

        nameDays[0] = "Sun";
        nameDays[1] = "Mon";
        nameDays[2] = "Tues";
        nameDays[3] = "Wed";
        nameDays[4] = "Thurs";
        nameDays[5] = "Fri";
        nameDays[6] = "Sat";

        listAdapter = new dataListAdapter(listofEvent,listofTime,listofdaytodisplay);
        upcomingschedule.setAdapter(listAdapter);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_teams);
        listView.setAdapter(arrayAdapter);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        root.child("Users/" + user + "/currentTeam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentteam = dataSnapshot.getValue(String.class);
                root.child("Groups/" + currentteam + "/Event").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listofEvent.clear();
                        listofTime.clear();
                        listofdaytodisplay.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            Event e = d.getValue(Event.class);
                            listofEvent.add(e.getTrainingOverview());
                            listofdaytodisplay.add(nameDays[Integer.parseInt(e.getNameDay())]);
                            if(e.getStartTimeMinute() < 10) {
                                listofTime.add(e.getDay() + "/" + e.getMonth() + " " + "|" + " " + Integer.toString(e.getStartTimeHour()) + ":0" + Integer.toString(e.getStartTimeMinute()));
                            }
                            else{
                                listofTime.add(e.getDay() + "/" + e.getMonth() + " " + "|" + " " + Integer.toString(e.getStartTimeHour()) + ":" + Integer.toString(e.getStartTimeMinute()));
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        use.child(user + "/listOfTeam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                list_of_teams.clear();
                while (i.hasNext()){
                    list_of_teams.add(((DataSnapshot)i.next()).getValue(String.class));
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nameofteam = (String) adapterView.getItemAtPosition(i);
                Map<String,Object> updateCurrentViewTeam = new HashMap<String, Object>();
                updateCurrentViewTeam.put(user + "/currentTeam" , nameofteam);
                use.updateChildren(updateCurrentViewTeam);

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            auth.signOut();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            // Handle the camera action
        }
        else if( id == R.id.calender){
            Intent intent = new Intent(this, Calender.class);
            root.child("Groups/" + currentteam + "/admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        listofadmin.add(d.getValue(String.class));
                    }
                    if(listofadmin.contains(auth.getCurrentUser().getEmail())) {
                        Calender.isAdmin = true;
                    }
                   // Toast.makeText(Options.this, "IsAdmin = " + String.valueOf(Calender.isAdmin), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            startActivity(intent);
        }
        else if(id == R.id.requests){
            Intent intent = new Intent(this,Request.class);
            startActivity(intent);
        }
        else if(id == R.id.home){
            Intent intent = new Intent (this,Options.class);
            startActivity(intent);
        }

        else if(id == R.id.roster){
            Intent intent = new Intent(this,Rooster.class);
            startActivity(intent);
        }
        else if(id == R.id.sportsgroups){
            Intent intent = new Intent(this,SportsGroup.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class dataListAdapter extends BaseAdapter {
        ArrayList<String> Title = new ArrayList<String>();
        ArrayList<String> Timer = new ArrayList<String>();
        ArrayList<String> NameDay = new ArrayList<String>();


        dataListAdapter() {
            Title = null;
            Timer = null;
            NameDay = null;

        }

        public dataListAdapter(ArrayList<String> text,ArrayList<String> time,ArrayList<String> nd) {
            Title = text;
            Timer = time;
            NameDay = nd;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return Title.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.homeschedule, parent, false);
            TextView title;
            TextView Time;
            TextView ND;
            title = (TextView) row.findViewById(R.id.title);
            Time = (TextView) row.findViewById(R.id.time);
            ND = (TextView) row.findViewById(R.id.day);
            title.setText(Title.get(position));
            Time.setText(Timer.get(position));
            ND.setText(NameDay.get(position));


            return (row);
        }
    }

}
