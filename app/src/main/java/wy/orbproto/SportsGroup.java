package wy.orbproto;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SportsGroup extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button  add_room;
    private EditText room_name;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<String>();
    private ArrayList<String> list_of_users = new ArrayList<String>();
    private ArrayList<String> list_of_teams = new ArrayList<String>();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Groups");
    private DatabaseReference use = FirebaseDatabase.getInstance().getReference("Users");
    private String email;
    private FirebaseUser temp2;
    private FirebaseAuth auth;
    private String user;
    private dataListAdapter listAdapter;
    private ListView listView2;
    private TextView showcurrentteam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_group); Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        add_room = (Button) findViewById(R.id.btn_add_room);
        room_name = (EditText) findViewById(R.id.room_name_edittext);
        listView = (ListView) findViewById(R.id.displaylistofteam);
        listView2 = (ListView) findViewById(R.id.listView);
        temp2 = FirebaseAuth.getInstance().getCurrentUser();
        email = temp2.getEmail();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid();
        showcurrentteam = (TextView) findViewById(R.id.showcurrentteam);


        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_teams);
        listView.setAdapter(arrayAdapter);
        listView2.setAdapter(arrayAdapter);

        use.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Toast.makeText(SportsGroup.this, data.getKey(), Toast.LENGTH_SHORT).show();
                    list_of_users.add(data.getKey());
                }
                if(!list_of_users.contains(user)) {
                    User user1 = new User(temp2.getDisplayName(), email, temp2.getUid());
                    use.child(temp2.getUid()).setValue(user1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addRoom();

            }
        });
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        use.child(temp2.getUid() + "/listOfTeam").addValueEventListener(new ValueEventListener() {
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

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nameofteam = (String) adapterView.getItemAtPosition(i);
                Map<String,Object> updateCurrentViewTeam = new HashMap<String, Object>();
                updateCurrentViewTeam.put(user + "/currentTeam" , nameofteam);
                use.updateChildren(updateCurrentViewTeam);

            }
        });


        use.child(user + "/currentTeam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showcurrentteam.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void addRoom() {
        String name = room_name.getText().toString().trim();

        Team team = new Team(name, temp2.getEmail());
        root.child(name).setValue(team);
        room_name.setText("");
        if(list_of_rooms.contains(name)){
            Toast.makeText(this, "Team already exist", Toast.LENGTH_LONG).show();
        }

        list_of_teams.add(name);
        use.child(temp2.getUid()).setValue(new User(temp2.getDisplayName(),list_of_teams,email,temp2.getUid()));
        String nameofteam = name;
        Map<String,Object> updateCurrentViewTeam = new HashMap<String, Object>();
        updateCurrentViewTeam.put(user + "/currentTeam" , nameofteam);
        use.updateChildren(updateCurrentViewTeam);

       /* String id = root.push().getKey();
        root.child(id).setValue(team);*/
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
            signOut();
            // Handle the camera action
        }
        else if( id == R.id.calender){
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

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
            row = inflater.inflate(R.layout.list, parent, false);
            TextView title;
            title = (TextView) row.findViewById(R.id.title);
            title.setText(Title.get(position));


            return (row);
        }
    }

}

