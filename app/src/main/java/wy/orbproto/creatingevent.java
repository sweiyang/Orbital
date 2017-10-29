package wy.orbproto;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Date;

public class creatingevent extends AppCompatActivity {
    private TextView time;
    private TimePicker simpleTimePicker;
    private TimePicker simpleTimePicker2;
    private EditText venue;
    private EditText trainingOverview;
    private DatabaseReference root;
    private FirebaseAuth auth;
    private ArrayList<Event> list_of_events = new ArrayList<Event>();
    private Button createEvent;
    private String user;
    private ArrayList<String> list_of_user = new ArrayList<String>();
    private String day;
    private String month;
    private String year;
    private int starth, startmin, endh,endmin;
    private String currentTeam;
    private String nameDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatingevent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create event");
        time = (TextView) findViewById(R.id.time);
        simpleTimePicker = (TimePicker) findViewById(R.id.simpleTimePicker);
        simpleTimePicker.setIs24HourView(false); // used to display AM/PM mode
        simpleTimePicker2 = (TimePicker) findViewById(R.id.timePicker);
        simpleTimePicker2.setIs24HourView(false); // used to display AM/PM mode
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        venue = (EditText) findViewById(R.id.venue);
        trainingOverview = (EditText) findViewById(R.id.trainingOver);
        root = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid();
        createEvent = (Button) findViewById(R.id.button2);
        Intent intent;
        intent = getIntent();
        day = intent.getStringExtra("Day");
        month = intent.getStringExtra("Month");
        year = intent.getStringExtra("Year");
        nameDay = intent.getStringExtra("NameDay");
        Toast.makeText(this, nameDay, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, year, Toast.LENGTH_SHORT).show();


        root.child("Users/" + user + "/currentTeam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentTeam = dataSnapshot.getValue(String.class);
                root.child("Groups/" + currentTeam + "/Event").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list_of_events.clear();
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            Event e;
                            e = data.getValue(Event.class);
                            list_of_events.add(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                root.child("Groups/" + currentTeam + "/user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            list_of_user.add(d.getValue(String.class));
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



//        root.child("Groups/" + currentTeam + "/User").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                list_of_user.clear();
//                for(DataSnapshot data : dataSnapshot.getChildren()){
//                    String u;
//                    u = data.getValue(String.class);
//                    list_of_user.add(u);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23) {
                    starth = simpleTimePicker.getHour();
                    startmin = simpleTimePicker.getMinute();
                    endh = simpleTimePicker2.getHour();
                    endmin = simpleTimePicker2.getMinute();
                }
                else{
                    starth = simpleTimePicker.getCurrentHour();
                    startmin = simpleTimePicker.getCurrentMinute();
                    endh = simpleTimePicker2.getCurrentHour();
                    endmin = simpleTimePicker2.getCurrentMinute();
                }
                Event event = new Event(starth,startmin,endh,endmin);
                event.setYear(year);
                event.setMonth(month);
                event.setDay(day);
                event.setLocation(venue.getText().toString());
                event.setTrainingOverview(trainingOverview.getText().toString());
                event.addNotAttending(list_of_user);
                event.setNameDay(nameDay);
                list_of_events.add(event);

                root.child("Groups/" + currentTeam + "/Event").setValue(list_of_events);

                Toast.makeText(creatingevent.this, "Creation of even successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(creatingevent.this, Calender.class);
                startActivity(intent);;
            }
        });
    }

    public void gobacktoCalender(View view){
        Intent intent = new Intent(this,Calender.class);
        startActivity(intent);
    }

}
