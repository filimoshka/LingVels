package wisp.filimoshka.lingvels;

public class CountsDB implements Comparable {

    public int id_count, bg_count, char_count, line_count, to_count;

    public CountsDB() {
    }

    public CountsDB(int id_count, int bg_count, int char_count, int line_count, int to_count) {
        this.id_count = id_count;
        this.bg_count = bg_count;
        this.char_count = char_count;
        this.line_count = line_count;
        this.to_count = to_count;
    }

    public int getId() {
        return id_count;
    }


    @Override
    public int compareTo(Object o) {
        int compareId=((CountsDB)o).getId();
        return this.id_count-compareId;

    }
}
