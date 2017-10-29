package wy.orbproto;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CompoundButtonCompat;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.CalendarWeekDayFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Calender extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MaterialCalendarView calendarView;
    private TextView summaryofday;
    private DatabaseReference root;
    private FirebaseAuth auth;
    private ArrayList<Event> list_of_events = new ArrayList<Event>();
    private String user;
    private ToggleButton indicatingAttendance;
    private ArrayList<String> list_of_notattending = new ArrayList<String>();
    private ArrayList<String> list_of_attending = new ArrayList<String>();
    private ArrayList<String> listofeventtobedisplayed = new ArrayList<String>();
    private ArrayList<String> listoftimetobedisplayed = new ArrayList<String>();
    private String index;
    private String currentTeam;
    private dataListAdapter listAdapter;
    private ListView listView;
    private HashSet<CalendarDay> list;
    private Calendar calendar1;
    private ArrayList<String> listoflocationtobedisplayed = new ArrayList<String>();
    private ArrayList<String> list_of_teams = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    private ListView listView1;
    private ArrayList<String> listofAdmin = new ArrayList<String>();
    private String useremail;
    public static boolean isAdmin;
    private int count =0 ;
    private HashMap<String,String> emails = new HashMap<String,String>();
    private ArrayList<Integer> listofindex = new ArrayList<Integer>();
    private HashMap<String,ArrayList<Integer>> mappingofindex = new HashMap<>();
    private ArrayList<Event> listofEvents = new ArrayList<Event>();
    private HashMap<String, ArrayList<Boolean>> mappingofattending = new HashMap<>();
    private ArrayList<Boolean> listoftruth = new ArrayList<Boolean>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Toolbar toolbar = (Toolbar) findViewById(R.id.calendertoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Calendar");
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        root = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid();
        useremail = auth.getCurrentUser().getEmail();
        listView = (ListView) findViewById(R.id.showevents);
        listView1 = (ListView) findViewById(R.id.listView);
        listAdapter = new dataListAdapter(listofeventtobedisplayed,listoftimetobedisplayed,listoflocationtobedisplayed,listofEvents);
        listView.setAdapter(listAdapter);



        list = new HashSet<CalendarDay>();
        calendar1 = Calendar.getInstance();
        calendarView.setSelectedDate(new Date());

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_teams);
        listView1.setAdapter(arrayAdapter);


        root.child("Users/" + user + "/currentTeam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentTeam = dataSnapshot.getValue(String.class);
                root.child("Groups/" + currentTeam + "/Event").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Event e;
                            e = data.getValue(Event.class);
                            list_of_events.add(e);
                            calendar1.set(Integer.parseInt(e.getYear()), Integer.parseInt(e.getMonth()) - 1, Integer.parseInt(e.getDay()));
                            list.add(CalendarDay.from(calendar1));

                        }
                        calendarView.addDecorators(new EventDecorator(Color.BLACK, list));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                root.child("Groups/" + currentTeam + "/attending").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            list_of_attending.add(data.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                root.child("Groups/" + currentTeam + "/notattending").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            list_of_notattending.add(data.getValue(String.class));
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        root.child("Users/" + user + "/listOfTeam").addValueEventListener(new ValueEventListener() {
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



        root.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    User u = d.getValue(User.class);
                    emails.put(u.getUID(),u.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nameofteam = (String) adapterView.getItemAtPosition(i);
                Map<String,Object> updateCurrentViewTeam = new HashMap<String, Object>();
                updateCurrentViewTeam.put(user + "/currentTeam" , nameofteam);
                root.child("Users").updateChildren(updateCurrentViewTeam);

            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                listofeventtobedisplayed.clear();
                listoftimetobedisplayed.clear();
                listoflocationtobedisplayed.clear();
                listofEvents.clear();
                listofindex.clear();
                listoftruth.clear();


                for(int i=0;i<list_of_events.size();i++){
                    if(String.valueOf(calendarView.getSelectedDate().getYear()).equals(list_of_events.get(i).getYear()) && (String.valueOf(calendarView.getSelectedDate().getMonth() + 1).equals(list_of_events.get(i).getMonth()) && (String.valueOf(calendarView.getSelectedDate().getDay()).equals(list_of_events.get(i).getDay())))) {
                        listofeventtobedisplayed.add(list_of_events.get(i).getTrainingOverview());
                        listoftimetobedisplayed.add(list_of_events.get(i).getStartTimeHour()+ ":" + list_of_events.get(i).getStartTimeMinute());
                        listoflocationtobedisplayed.add("Location:" + list_of_events.get(i).getLocation());
                        listofEvents.add(list_of_events.get(i));
                        listofindex.add(i);
                        if(list_of_events.get(i).getAttending().contains(emails.get(user))) {
                            listoftruth.add(true);
                        }
                        else
                            listoftruth.add(false);
                        //Toast.makeText(Calender.this, "i = " + i, Toast.LENGTH_SHORT).show();
                    }
                }
               // Toast.makeText(Calender.this, String.valueOf(calendarView.getSelectedDate().getDay()), Toast.LENGTH_SHORT).show();
                mappingofindex.put(String.valueOf(calendarView.getSelectedDate().getDay()),listofindex);
                mappingofattending.put(String.valueOf(calendarView.getSelectedDate().getDay()),listoftruth);
                listAdapter.notifyDataSetChanged();
            }
        });



    }
    public void isAdmin(){
        root.child("Groups/" + currentTeam + "/admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    listofAdmin.add(d.getValue(String.class));
                }
                if(listofAdmin.contains(useremail)) {
                    Calender.isAdmin = true;

                }
               // Toast.makeText(Calender.this, "isadmin = " + String.valueOf(Calender.isAdmin), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Toast.makeText(this, "inflate = " + String.valueOf(Calender.isAdmin), Toast.LENGTH_SHORT).show();
        isAdmin();
        if(Calender.isAdmin) {
            getMenuInflater().inflate(R.menu.calender, menu);
            return true;
        }
        else
            return  false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.create_event: {

                Intent intent = new Intent(this,creatingevent.class);
                try {
                    intent.putExtra("Day", String.valueOf(calendarView.getSelectedDate().getDay()));
                    intent.putExtra("Month", String.valueOf(calendarView.getSelectedDate().getMonth() + 1));
                    intent.putExtra("Year", String.valueOf(calendarView.getSelectedDate().getYear()));
                    intent.putExtra("NameDay", Integer.toString(calendarView.getSelectedDate().getDate().getDay()));
                    startActivity(intent);
                    return true;
                }catch (NullPointerException e){
                    Toast.makeText(this, "Select a day", Toast.LENGTH_SHORT).show();
                }
            }
            default:
            return super.onOptionsItemSelected(item);
        }
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



    class dataListAdapter extends BaseAdapter {
        ArrayList<String> Title = new ArrayList<String>();
        ArrayList<String> Timer = new ArrayList<String>();
        ArrayList<String> Location = new ArrayList<String>();
        ArrayList<Event> Objects = new ArrayList<Event>();

        dataListAdapter() {
            Title = null;
            Timer = null;
            Location = null;
            Objects = null;

        }

        public dataListAdapter(ArrayList<String> text,ArrayList<String> time, ArrayList<String> Loc,ArrayList<Event> obj) {
            Title = text;
            Timer = time;
            Location = Loc;
            Objects = obj;

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
            row = inflater.inflate(R.layout.calender, parent, false);
            final TextView title;
            TextView Time;
            TextView location;
            ToggleButton indicatingAttendence;
            DatabaseReference use = FirebaseDatabase.getInstance().getReference();


            indicatingAttendence = (ToggleButton) row.findViewById(R.id.toggleButton);
            try {
               // Toast.makeText(Calender.this, String.valueOf(Objects.get(position).getDay()) , Toast.LENGTH_SHORT).show();
                //Toast.makeText(Calender.this, String.valueOf("i am here = " + mappingofindex.get(String.valueOf(Objects.get(position).getDay())).get(position)), Toast.LENGTH_SHORT).show();
                indicatingAttendence.setTag(mappingofindex.get(String.valueOf(Objects.get(position).getDay())).get(position));
            }catch (NullPointerException e) {

            }
            title = (TextView) row.findViewById(R.id.title);
            Time = (TextView) row.findViewById(R.id.time);
            location = (TextView) row.findViewById(R.id.Loc);
            location.setText(Location.get(position));
            title.setText(Title.get(position));
            Time.setText(Timer.get(position));

           if(mappingofattending.get(String.valueOf(Objects.get(position).getDay())).get(position)){
                indicatingAttendence.setChecked(false);
            }
            else{
               indicatingAttendence.setChecked(true);
           }


            indicatingAttendence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    index = buttonView.getTag().toString();
                    if (isChecked) { // not attending
                        updateAttendance(index,false);

                    } else {
                        updateAttendance(index,true);
                    }

                }

            });

            return (row);
        }
    }

    public void updateAttendance(String index ,boolean going){
        final String currentUser = emails.get(user);
        final boolean status = going;
        final String position = index;
        list_of_attending.clear();
        list_of_notattending.clear();

        Toast.makeText(Calender.this, currentTeam, Toast.LENGTH_SHORT).show();
        root.child("Groups/" + currentTeam + "/Event/" + index + "/attending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    list_of_attending.add(d.getValue(String.class));
                    Toast.makeText(Calender.this, "i am here", Toast.LENGTH_SHORT).show();
                }
                root.child("Groups/" + currentTeam + "/Event/" + position+ "/notattending").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot d: dataSnapshot.getChildren()) {
                            list_of_notattending.add(d.getValue(String.class));
                            Toast.makeText(Calender.this, "i am here 2", Toast.LENGTH_SHORT).show();
                        }
                        if(!status){
                            if(list_of_attending.contains(emails.get(user))){
                                list_of_attending.remove(emails.get(user));
                            }
                            if(!list_of_notattending.contains(emails.get(user))){
                                list_of_notattending.add(emails.get(user));
                            }
                        }
                        else{
                            if(!list_of_attending.contains(emails.get(user))){
                                list_of_attending.add(emails.get(user));
                            }
                            if(list_of_notattending.contains(emails.get(user))){
                                list_of_notattending.remove(emails.get(user));
                            }
                        }
                        Map<String, Object> updateNotAttending = new HashMap<String, Object>();
                        Map<String, Object> updateAttending = new HashMap<String, Object>();;
                        updateAttending.put("Groups/" + currentTeam + "/Event/" + position + "/attending", list_of_attending);
                        updateNotAttending.put("Groups/" + currentTeam + "/Event/" + position + "/notattending", list_of_notattending);
                        root.updateChildren(updateAttending);
                        root.updateChildren(updateNotAttending);
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


        Toast.makeText(this, String.valueOf(list_of_attending.size()), Toast.LENGTH_SHORT).show();



    }
}
