package wy.orbproto;

import android.provider.ContactsContract;
import android.service.quicksettings.Tile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
import java.util.Map;

public class Accept extends AppCompatActivity {
    private dataListAdapter listAdapter;
    private ListView displayRequest;
    private ArrayList<String> list_of_groupuser = new ArrayList<String>();
    private ArrayList<String> list_of_ownrequest = new ArrayList<String>();
    private DatabaseReference root;
    private FirebaseAuth auth;
    private String user;
    private String useremail;
    private ArrayList<String> listofteam = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Accept");
        displayRequest = (ListView) findViewById(R.id.listofReq);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid();
        root = FirebaseDatabase.getInstance().getReference();

        listAdapter = new dataListAdapter(list_of_ownrequest);
        displayRequest.setAdapter(listAdapter);

        root.child("Users/" + user + "/listofReq/group_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null) {
                    list_of_ownrequest.add(dataSnapshot.getValue(String.class));
                }
                listAdapter.notifyDataSetChanged();

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
            row = inflater.inflate(R.layout.custom, parent, false);
            final TextView title;
            title = (TextView) row.findViewById(R.id.title);
            Button button = (Button) row.findViewById(R.id.accept);
            title.setText(Title.get(position));
            root = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser().getUid();
            Toast.makeText(Accept.this, useremail, Toast.LENGTH_SHORT).show();
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    root.child("Users/" + user + "/email").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            useremail = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    root.child("Users/" + user + "/listOTeam").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listofteam.clear();
                            for(DataSnapshot d: dataSnapshot.getChildren()){
                                listofteam.add(d.getValue(String.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(Accept.this, Title.get(position), Toast.LENGTH_SHORT).show();
                    root.child("Groups/"+Title.get(position)+"/user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            list_of_groupuser.add(data.getValue(String.class));
                            Toast.makeText(Accept.this, list_of_groupuser.get(0), Toast.LENGTH_SHORT).show();
                        }
                        try {
                            Map<String, Object> updateUser = new HashMap<String, Object>();
                            list_of_groupuser.add(useremail);

                            updateUser.put("/user", list_of_groupuser);
                            root.child("Groups/" + Title.get(position)).updateChildren(updateUser);
                            listofteam.add(Title.get(position));
                            list_of_ownrequest.remove(Title.get(position));
                            //Toast.makeText(Accept.this, "test", Toast.LENGTH_SHORT).show();
                            if (list_of_ownrequest.isEmpty())
                                list_of_ownrequest.clear();
                            Map<String, Object> updateListReq = new HashMap<String, Object>();
                            updateListReq.put("/listofReq", list_of_ownrequest);
                            root.child("Users/" + user).updateChildren(updateListReq);
                            Map<String,Object> updateTeamList = new HashMap<String,Object>();
                            updateTeamList.put("/listOfTeam",listofteam);
                            root.child("Users/" + user).updateChildren(updateTeamList);
                        }catch (IndexOutOfBoundsException e){

                        }



                        //Toast.makeText(Accept.this, list_of_groupuser.get(1), Toast.LENGTH_SHORT).show();

                        listAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                }
            });


            return (row);
        }
    }
}

