package wy.orbproto;

import java.util.ArrayList;

/**
 * Created by BoonKok on 6/10/2017.
 */

public class User {

    private String name;
    private ArrayList<String> listOfTeam = new ArrayList<String>();
    private String email;
    private String uid;
    private ArrayList<String> listofReq = new ArrayList<String>();




    public User(){

    }

    public User(String name, String email,String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public User(String name, ArrayList<String> team, String email, String uid){
        this.name = name;
        this.listOfTeam = team;
        this.email = email;
        this.uid = uid;


    }

    public String getUID() {
        return uid;
    }
    public void addTeam (String name){
        listOfTeam.add(name);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getListOfTeam() {
        return listOfTeam;
    }

    public String getEmail() {
        return email;
    }
}
