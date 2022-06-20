package wisp.filimoshka.lingvels;

public class OptionsDB implements Comparable {

    public String first_opt, second_opt, third_opt;
    public int id_task_opt;
    public int correct_num;

    public OptionsDB() {
    }


    public OptionsDB(String first_opt, String second_opt, String third_opt, int id_task_opt, int correct_num) {
        this.first_opt = first_opt;
        this.second_opt = second_opt;
        this.third_opt = third_opt;
        this.id_task_opt = id_task_opt;
        this.correct_num = correct_num;
    }

    public int getId() {
        return id_task_opt;
    }


    @Override
    public int compareTo(Object o) {
        int compareId=((OptionsDB)o).getId();
        return this.id_task_opt-compareId;

    }
}
