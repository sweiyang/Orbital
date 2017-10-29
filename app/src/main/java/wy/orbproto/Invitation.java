package wy.orbproto;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Invitation extends AppCompatActivity {

    private DatabaseReference root;
    private DatabaseReference use;
    private FirebaseAuth auth;
    private String user;
    private EditText getUser;
    private ArrayList<String> list_of_users = new ArrayList<String>();
    private ArrayList<request_noti> list_of_request = new ArrayList<request_noti>();
    private ArrayList<String> list_of_pending = new ArrayList<String>();
    private EditText getGroup;
    private String userUid;
    private Boolean userExists = false;
    private static boolean found = false;
    private Map<String,String> map = new HashMap<String,String>();
    private String currentTeam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        root = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invitation");
        getUser = (EditText) findViewById(R.id.getUser);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid(); // rmb to check whether is the user in the team.

        getGroup = (EditText) findViewById(R.id.getGroup);

        root.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    User u;
                    u = data.getValue(User.class);
                    map.put(u.getEmail(),u.getUID());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void invite(View view) {

        map.get(getUser.getText().toString());

        Map<String, Object> updateListReq = new HashMap<String, Object>();
        updateListReq.put("/listofReq", new request_noti(getGroup.getText().toString(), userUid, "false"));
        root.child("Users/" + map.get(getUser.getText().toString())).updateChildren(updateListReq);
        getUser.setText("");
        getGroup.setText("");
    }



}
