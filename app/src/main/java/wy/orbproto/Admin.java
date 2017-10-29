package wy.orbproto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Admin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listView;
    private ListView displayPref;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_teams = new ArrayList<String>();
    private FirebaseAuth auth;
    private String user;
    private DatabaseReference use = FirebaseDatabase.getInstance().getReference();
    private dataListAdapter listAdapter;
    private ArrayList<String> list_of_users = new ArrayList<String>();
    private String currentTeam;
    private DatabaseReference root;
    private ArrayList<String> listofadmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_teams);
        auth = FirebaseAuth.getInstance();
        listView.setAdapter(arrayAdapter);
        user = auth.getCurrentUser().getUid();
        displayPref = (ListView) findViewById(R.id.listofusers);
        listAdapter = new dataListAdapter(list_of_users);
        displayPref.setAdapter(listAdapter);
        listofadmin = new ArrayList<String>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        use.child("Users/" + user + "/listOfTeam").addValueEventListener(new ValueEventListener() {
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

        use.child("Users/" + user +  "/currentTeam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentTeam = dataSnapshot.getValue(String.class);
                use.child("Groups/" + currentTeam + "/User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list_of_users.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            list_of_users.add(d.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                use.child("Groups/" + currentTeam + "/Admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listofadmin.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            listofadmin.add(d.getValue(String.class));
                        }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nameofteam = (String) adapterView.getItemAtPosition(i);
                Map<String,Object> updateCurrentViewTeam = new HashMap<String, Object>();
                updateCurrentViewTeam.put("Users/" + user + "/currentTeam" , nameofteam);
                use.updateChildren(updateCurrentViewTeam);

            }
        });
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if( id == R.id.calender){
            Intent intent = new Intent(this, Calender.class);
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

        dataListAdapter() {
            Title = null;

        }

        public dataListAdapter(ArrayList<String> text) {
            Title = text;


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
            row = inflater.inflate(R.layout.admin, parent, false);
            final TextView title;
            title = (TextView) row.findViewById(R.id.title);
            Button button = (Button) row.findViewById(R.id.accept);
            title.setText(Title.get(position));
            root = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser().getUid();
            if(listofadmin.contains(Title.get(position))){
                button.setText("Remove");
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listofadmin.remove(Title.get(position));
                        //Toast.makeText(Accept.this, "test", Toast.LENGTH_SHORT).show();
                        if (listofadmin.isEmpty())
                            listofadmin.clear();
                        Map<String, Object> updateListReq = new HashMap<String, Object>();
                        updateListReq.put("/Admin", listofadmin);
                        root.child("Groups/" + currentTeam).updateChildren(updateListReq);
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
            else {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listofadmin.add(Title.get(position));
                        //Toast.makeText(Accept.this, "test", Toast.LENGTH_SHORT).show();
                        if (listofadmin.isEmpty())
                            listofadmin.clear();
                        Map<String, Object> updateListReq = new HashMap<String, Object>();
                        updateListReq.put("/Admin", listofadmin);
                        root.child("Groups/" + currentTeam).updateChildren(updateListReq);
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }

            return (row);
        }
    }
}
