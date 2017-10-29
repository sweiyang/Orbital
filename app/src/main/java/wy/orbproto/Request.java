package wy.orbproto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Request extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<String> list_of_teams = new ArrayList<String>();
    private DatabaseReference use = FirebaseDatabase.getInstance().getReference("Users");
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference root;
    private static boolean isAdmin;
    private ArrayList<String> listofAdmin = new ArrayList<String>();
    private String useremail;
    private String user;
    private String currentTeam;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Request");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid();

        root = FirebaseDatabase.getInstance().getReference();

        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_teams);
        listView.setAdapter(arrayAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        root.child("Users/" + user + "/email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                useremail = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        root.child("Users/" + user + "/currentTeam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentTeam = dataSnapshot.getValue(String.class);
                root.child("Groups/" + currentTeam + "/admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            listofAdmin.add(d.getValue(String.class));
                        }
                        if(listofAdmin.contains(useremail)){
                            Request.isAdmin = true;
                        }
                        else {
                            Request.isAdmin = false;
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

    public void gotoInvitation(View view){
        if(Request.isAdmin) {
            Intent intent = new Intent(this, Invitation.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "You do not have the Administrator's rights to access this function", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoRequest(View view){
        Intent intent = new Intent(this,Accept.class);
        startActivity(intent);

    }
}
