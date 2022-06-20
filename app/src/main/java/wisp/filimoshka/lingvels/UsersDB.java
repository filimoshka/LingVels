package wisp.filimoshka.lingvels;

import java.util.ArrayList;

public class UsersDB {

    public String email, user_name;
    public ArrayList<Float> results = new ArrayList<Float>(); // intermediate


    public UsersDB() {
    }

    public UsersDB(String email, String user_name, ArrayList<Float> results) {
        this.email = email;
        this.user_name = user_name;
        this.results = results;
    }
}
