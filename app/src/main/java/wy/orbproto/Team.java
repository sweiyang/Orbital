package wy.orbproto;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by BoonKok on 5/30/2017.
 */

public class Team {
    String nameTeam;
    ArrayList<String> admin = new ArrayList<String>();
    ArrayList<String> user = new ArrayList<String>();

    public Team(){

    }


    public Team(String nameTeam,String admin){
        this.nameTeam = nameTeam;
        this.admin.add(admin);
        this.user.add(admin);
    }

    public ArrayList<String> getAdmin() {
        return admin;
    }

    public void setAdmin(ArrayList<String> admin) {
        this.admin = admin;
    }

    public ArrayList<String> getUser() {
        return user;
    }

    public void setUser(ArrayList<String> user) {
        this.user = user;
    }

    public String getNameTeam() {
        return nameTeam;
    }



}

