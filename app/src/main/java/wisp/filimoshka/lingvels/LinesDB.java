package wisp.filimoshka.lingvels;

public class LinesDB implements Comparable {

    public String dif_level;
    public String text_line;
    public int id_line, character_id, task_opt_id, lesson;
    public boolean speech, task_opt;

    public LinesDB() {
    }

    public LinesDB(int id_line, String text_line, boolean speech, boolean task_opt, String dif_level, int lesson, int character_id, int task_opt_id) {
        this.id_line = id_line;
        this.text_line = text_line;
        this.speech = speech;
        this.task_opt = task_opt;
        this.dif_level = dif_level;
        this.lesson = lesson;
        this.character_id = character_id;
        this.task_opt_id = task_opt_id;
    }

    public int getId() {
        return id_line;
    }


    @Override
    public int compareTo(Object o) {
        int compareId=((LinesDB)o).getId();
        return this.id_line-compareId;

    }
}
