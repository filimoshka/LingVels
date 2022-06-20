package wisp.filimoshka.lingvels;

import java.util.ArrayList;

public class BackgroundsDB implements Comparable {

    public String bg_img;
    public int id_bg;
    public ArrayList<Integer> line_numbers = new ArrayList<Integer>();

    public BackgroundsDB() {
    }

    public BackgroundsDB(String bg_img, int id_bg, ArrayList<Integer> line_numbers) {
        this.bg_img = bg_img;
        this.id_bg = id_bg;
        this.line_numbers = line_numbers;
    }


    public int getId() {
        return id_bg;
    }


    @Override
    public int compareTo(Object o) {
        int compareId=((BackgroundsDB)o).getId();
        return this.id_bg-compareId;

    }
}
